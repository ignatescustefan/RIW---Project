import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class MainMenu {
    private String reverseIndexCollection;
    private Master master;

    public MainMenu(String initialFolder, String reverseIndexCollection) {
        this.reverseIndexCollection = reverseIndexCollection;
        this.master = new Master(new File(initialFolder));
    }

    private void printMenu() {
        System.out.println("Meniu:");
        System.out.println("Optiuni:");
        System.out.println("\tCreare index direct -> 1");
        System.out.println("\tCreare index invers -> 2");
        System.out.println("\tCautare booleana -> 3");
        System.out.println("\tCautare vectoriala -> 4");
        System.out.println("\tIesire -> 0");
        System.out.println("Alegeti optiune: ");
    }

    private void loadDirectIndex() {
        master.startDirectIndex();
    }

    private void loadReverseIndex() {
        master.startFinalMergeReverseIndex();
    }

    private void booleanSearch() {
        Search search = new BooleanSearchMongo(reverseIndexCollection);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduceti interogare, pentru iesire utilizati \"EXIT\": ");
        String query = scanner.nextLine();
        while (!query.equals("EXIT")) {
            System.out.println("Rezultate cautare pentru: " + query);
            System.out.println("Cautare booleana : " + search.generalSearch(query));
            System.out.println("Introduceti interogare, pentru iesire utilizati \"EXIT\": ");
            query = scanner.nextLine();
        }
    }

    private void vectorSearch() {
        Search search = new VectorSearch(reverseIndexCollection);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduceti interogare, pentru iesire utilizati \"EXIT\": ");
        String query = scanner.nextLine();
        while (!query.equals("EXIT")) {
            System.out.println("Rezultate cautare pentru: " + query);
            System.out.println("Cautare vectoriala : " + search.generalSearch(query));
            System.out.println("Introduceti interogare, pentru iesire utilizati \"EXIT\": ");
            query = scanner.nextLine();
        }
    }

    public void execute() {
        //citesc optiune
        while (true) {
            printMenu();
            Scanner scanner = new Scanner(System.in);
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    //index direct
                    System.out.println("Se creaza indexul direct...");
                    loadDirectIndex();
                    System.out.println("Indexul direct a fost creat.");
                    break;
                case "2":
                    //index invers
                    System.out.println("Se creaza indexul invers...");
                    loadReverseIndex();
                    System.out.println("Indexul invers a fost creat.");
                    break;
                case "3":
                    //cautare booleana
                    System.out.println("Se efectueaza cautarea booleana ..");
                    booleanSearch();
                    System.out.println();
                    break;
                case "4":
                    //cautare vectoriala
                    System.out.println("Se efectueaza cautarea vectoriala ..");
                    vectorSearch();
                    System.out.println();
                    break;
                case "0":
                    System.out.println("Iesire program!");
                    return;
                default:
                    System.out.println("Optiune invalida");
                    break;
            }
        }
    }
}

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class MainRIW {

    public static void Lab01() throws IOException {
        PageInfo pageInfo = new PageInfo("https://ac.tuiasi.ro/");
        System.out.println(pageInfo.getTitle());
        System.out.println(pageInfo.getKeywords());
        System.out.println(pageInfo.getDescription());
        System.out.println(pageInfo.getRobots());
        System.out.println(pageInfo.getLinks());
        System.out.println(pageInfo.getText());
        File file = new File("files/index.html");
        PageInfo pageInfo1 = new PageInfo(file);
        System.out.println(pageInfo1.getRobots());
        System.out.println(pageInfo1.getLinks());

        System.out.println(DirectIndex.createDirectIndex("files/file.txt"));
    }

    public static void Lab02() throws IOException {
        DirectoryProcessing directoryProcessing = new DirectoryProcessing(new File("files/dateIntrare"));
        //directoryProcessing.processingFile();
        System.out.println("\nIndex direct:");
        System.out.println(DirectIndex.createDirectIndex("files/file.txt"));
    }

    public static void Lab03() throws IOException {
        DirectoryProcessing directIndexProcessing = new DirectoryProcessing(new File("files/dateIntrare"));
        directIndexProcessing.createDirectIndex();
        DirectoryProcessing directoryProcessing = new DirectoryProcessing(new File("output/files/dateIntrare"));
        directoryProcessing.createReverseIndex();
        TreeMap<String, HashMap<String, Integer>> treeMap = DirectoryProcessing.loadReverseIndex("output/reverseIndex/reverseIndex.txt");
        System.out.println(treeMap.size());
    }

    public static void Lab04() throws IOException {
        TreeMap<String, HashMap<String, Integer>> reverseIndex = DirectoryProcessing.loadReverseIndex("output/reverseIndex/reverseIndex.txt");
        Search booleanSearch = new BooleanSearch(reverseIndex);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduceti query: ");
        String query = scanner.nextLine();
        while (query != "EXIT") {
            System.out.println("Rezultate cautare pentru: " + query);
            System.out.println(booleanSearch.generalSearch(query));
            System.out.println("Introduceti query: ");
            query = scanner.nextLine();
        }
    }

    public static void testMongo() throws IOException {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("riw");
        MongoCollection<BasicDBObject> collection = database.getCollection("reverseIndex", BasicDBObject.class);

        TreeMap<String, HashMap<String, Integer>> reverseIndex = DirectoryProcessing.loadReverseIndex("output/reverseIndex/reverseIndex.txt");

        for (Map.Entry<String, HashMap<String, Integer>> entryIndex : reverseIndex.entrySet()) {
            BasicDBObject documentKey = new BasicDBObject();
            BasicDBObject documentValue = new BasicDBObject();
            for (Map.Entry<String, Integer> entryValue : entryIndex.getValue().entrySet()) {
                String val = entryValue.getKey().replace('.', '*');
                documentValue.append(val, entryValue.getValue());
            }
            documentKey.append(entryIndex.getKey(), documentValue);
            collection.insertOne(documentKey);

        }
//        Document document=new Document("name","Mongo")
//                .append("type","database")
//                .append("count",1)
//                .append("info",new Document("x",203).append("y",102));
//        collection.insertOne(document);
        // Document myDoc=collection.find().first();
    }

    private static void testMaster() {
        Master master = new Master(new File("files/dateTest"));
        master.startDirectIndex();
//        master.startReverseIndex();
    }

    private static void testBooleanSearchMongo() throws IOException {
        Search search = new BooleanSearchMongo("reverseIndex");
        TreeMap<String, HashMap<String, Integer>> reverseIndex = DirectoryProcessing.loadReverseIndex("output/reverseIndex/reverseIndex.txt");
        Search booleanSearch = new BooleanSearch(reverseIndex);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduceti query: ");
        String query = scanner.nextLine();
        while (!query.equals("EXIT")) {
            System.out.println("Rezultate cautare pentru: " + query);
            System.out.println("MongoSearch : " + search.generalSearch(query));
            System.out.println("FileSearch : " + booleanSearch.generalSearch(query));
            System.out.println("Introduceti query: ");
            query = scanner.nextLine();
        }
    }

    public static void testVectorSearch() {
        Search search = new VectorSearch("reverseIndex");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduceti query: ");
        String query = scanner.nextLine();
        while (!query.equals("EXIT")) {
            System.out.println("Rezultate cautare pentru: " + query);
            System.out.println("MongoSearch : " + search.generalSearch(query));
            System.out.println("Introduceti query: ");
            query = scanner.nextLine();
        }
    }

    public static void testSomeImpovement() {
//        DirectoryProcessing directoryProcessing = new DirectoryProcessing(new File("files/dateIntrare"));
//        System.out.println(directoryProcessing.exploreDirectoryForMoreParallelization());
        Master master = new Master(new File("files/dateTest"));
        master.startDirectIndex();
        master.startFinalMergeReverseIndex();
    }

    public static void mainMenuProject() {
        MainMenu mainMenu = new MainMenu("files/dateIntrare", "reverseIndex");
        mainMenu.execute();
    }

    public static void main(String[] args) throws IOException {
//        Lab01();
//        Lab02();
//        Lab03();
//        Lab04();
//        testMongo();
//        testMaster();
//        testBooleanSearchMongo();
//        testVectorSearch();
//        testSomeImpovement();
        mainMenuProject();
    }
}

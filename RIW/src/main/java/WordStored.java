import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class WordStored {
    private File stopwordsFile;
    private File exceptionFile;
    private List<String> exceptionList;
    private List<String> stopWordsList;

    public WordStored() throws FileNotFoundException {
        stopwordsFile = new File("files/stopwords.txt");
        stopWordsList = new LinkedList<String>();
        exceptionFile = new File("files/exception.txt");
        exceptionList = new LinkedList<String>();
        readInputs();
    }

    private void readInputs() throws FileNotFoundException {
        Scanner scanner = new Scanner(stopwordsFile);
        while (scanner.hasNext()) {
            stopWordsList.add(scanner.next());
        }
        scanner = new Scanner(exceptionFile);
        while (scanner.hasNext()) {
            exceptionList.add(scanner.next());
        }
        //System.out.println(stopWordsList);
        //System.out.println(exceptionList);
    }

    //return baseform or exception
    public String isStored(String word) {
        if (exceptionList.contains(word.toLowerCase())) {
            return word.toLowerCase();
        } else if (!stopWordsList.contains(word.toLowerCase())) {
            //string getBaseFrom()
            Stemmer s = new Stemmer();
            s.add(word.toLowerCase().toLowerCase().toCharArray(), word.length());
            s.stem();
            String baseForm = s.toString();
            return baseForm;
        }
        return null;
    }
}

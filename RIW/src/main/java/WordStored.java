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

    public WordStored(File stopFile,File exception) throws FileNotFoundException {
        stopwordsFile=stopFile;
        stopWordsList=new LinkedList<String>();
        exceptionFile=exception;
        exceptionList=new LinkedList<String>();
        readInputs();
    }
    private void readInputs() throws FileNotFoundException {
        Scanner scanner= new Scanner(stopwordsFile);
        while (scanner.hasNext()){
            stopWordsList.add(scanner.next());
        }
        scanner=new Scanner(exceptionFile);
        while (scanner.hasNext()){
            exceptionList.add(scanner.next());
        }
        //System.out.println(stopWordsList);
        //System.out.println(exceptionList);
    }

    public boolean isStored(String word){
        if(exceptionList.contains(word.toLowerCase())){
            return true;
        } else if (!stopWordsList.contains(word.toLowerCase())){
            //string getBaseFrom()
            return true;
        }
        return false;
    }
}

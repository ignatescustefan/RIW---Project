import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class MainRIW {

    public static void Lab01() throws IOException {
        PageInfo pageInfo = new PageInfo("https://codesjava.com/jsoup-get-images-from-html-example/");
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
        System.out.println(SplitText.splitText("files/file.txt"));

    }

    public static void Lab02() throws IOException {
        DirectoryProcessing directoryProcessing = new DirectoryProcessing(new File("files/dateIntrare01"));
        directoryProcessing.createDirectIndex();
    }

    public static void Lab03() throws IOException {
        DirectoryProcessing directoryProcessing = new DirectoryProcessing(new File("output/files/dateIntrare01"));
        directoryProcessing.createReverseIndex();
        TreeMap<String, HashMap<String, Integer>> treeMap= DirectoryProcessing.loadReverseIndex("output/reverseIndex/reverseIndex.txt");
        System.out.println(treeMap.size());
    }

    public static void Lab04() throws IOException{
        TreeMap<String, HashMap<String, Integer>> reverseIndex= DirectoryProcessing.loadReverseIndex("output/reverseIndex/reverseIndex.txt");

        BooleanSearch booleanSearch=new BooleanSearch(reverseIndex);
        booleanSearch.spitQuery("ana are mere");
        System.out.println(booleanSearch.generalOperation("ana+are+Maria"));
    }

    public static void main(String[] args) throws IOException {
        //Lab01();
        // System.out.println(map);
        //SplitText.writeIndexToFile("files/file.txt");
        //Lab02();
        //Lab03();
        Lab04();
    }
}

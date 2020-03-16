import java.io.File;
import java.io.IOException;

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
        System.out.println(TextSplitter.createDirectIndexFromFile("files/file.txt"));

    }

    public static void Lab02() throws IOException {
        DirectoryProcessor directoryProcessing = new DirectoryProcessor(new File("files/inputs"));
        directoryProcessing.createDirectIndex();
    }

    public static void Lab03() throws IOException {
        DirectoryProcessor directoryProcessing = new DirectoryProcessor(new File("output/files/inputs"));
        directoryProcessing.createReverseIndex();
    }


    public static void main(String[] args) throws IOException {
        Lab02();
        Lab03();
    }
}

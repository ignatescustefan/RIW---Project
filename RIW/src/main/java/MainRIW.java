import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class MainRIW {


    public static void main(String[] args) throws IOException {
        PageInfo pageInfo=new PageInfo("https://codesjava.com/jsoup-get-images-from-html-example/");
        System.out.println(pageInfo.getTitle());
        System.out.println(pageInfo.getKeywords());
        System.out.println(pageInfo.getDescription());
        //System.out.println(pageInfo.getRobots());
        System.out.println(pageInfo.getLinks());
        System.out.println(pageInfo.getText());
        System.out.println(SplitText.splitText("files/file.txt"));
        File file=new File("files/index.html");
        PageInfo pageInfo1=new PageInfo(file);
        //System.out.println(pageInfo1.getRobots());
        System.out.println(pageInfo1.getLinks());
    }
}

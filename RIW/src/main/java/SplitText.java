import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

public class SplitText {
    public static HashMap<String, Integer> splitText(String fileName) throws IOException {
        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

        WordStored wordStored = new WordStored(new File("files/stopwords.txt"), new File("files/exception.txt"));

        try {
            //deschid fisierul
            Charset encoding = Charset.defaultCharset();
            InputStream file = new FileInputStream(fileName);
            Reader reader = new InputStreamReader(file, encoding);
            int character;
            StringBuilder sb = new StringBuilder();
            while ((character = reader.read()) != -1) {
                char ch = (char) character;
                if (Character.isLetterOrDigit(ch)) {
                    sb.append(ch);
                } else {
                    //gata cuvantul
                    //verific dictionar
                    //daca trebuie stocat
                    if (wordStored.isStored(sb.toString())) {
                        if (wordCount.containsKey(sb.toString())) {
                            //incrementez valoarea
                            wordCount.put(sb.toString().toLowerCase(), wordCount.get(sb.toString()) + 1);
                        } else {
                            wordCount.put(sb.toString().toLowerCase(), 1);
                        }
                    }
                    //resetez cuvantul;
                    sb.setLength(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        wordCount.remove("");
        return wordCount;
    }
}

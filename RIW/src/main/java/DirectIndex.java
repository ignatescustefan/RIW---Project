import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

public class DirectIndex {
    public static HashMap<String, Integer> createDirectIndex(String fileName) throws IOException {
        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

        WordStored wordStored = new WordStored();

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
                    String baseForm = wordStored.isStored(sb.toString());
                    if (baseForm != null) {
                        if (wordCount.containsKey(baseForm)) {
                            //incrementez valoarea
                            wordCount.put(baseForm, wordCount.get(baseForm) + 1);
                        } else {
                            wordCount.put(baseForm, 1);
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

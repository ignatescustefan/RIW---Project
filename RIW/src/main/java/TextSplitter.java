import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

public class TextSplitter {
    public static HashMap<String, Integer> createDirectIndexFromFile(String fileName) throws IOException {
        HashMap<String, Integer> wordCount = new HashMap<>();

        WordFilter wordFilter = new WordFilter();

        try {
            InputStream file = new FileInputStream(fileName);
            Reader reader = new InputStreamReader(file, Charset.defaultCharset());

            int character;
            StringBuilder stringBuilder = new StringBuilder();
            while ((character = reader.read()) != -1) {
                char ch = (char) character;
                if (Character.isLetterOrDigit(ch)) {
                    stringBuilder.append(ch);
                } else {
                    // Just found a word.
                    if (wordFilter.shouldBeStored(stringBuilder.toString())) {
                        if (wordCount.containsKey(stringBuilder.toString())) {
                            wordCount.put(stringBuilder.toString(), wordCount.get(stringBuilder.toString()) + 1);
                        } else {
                            wordCount.put(stringBuilder.toString(), 1);
                        }
                    }
                    stringBuilder.setLength(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        wordCount.remove("");
        return wordCount;
    }
}

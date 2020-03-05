import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

public class SplitText {
    public static HashMap<String, Integer> splitText(String fileName) throws IOException {
        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

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
                    WordStored wordStored = new WordStored(new File("files/stopwords.txt"), new File("files/exception.txt"));
                    if (wordStored.isStored(sb.toString())) {
                        if (wordCount.containsKey(sb.toString())) {
                            //incrementez valoarea
                            wordCount.put(sb.toString(), wordCount.get(sb.toString()) + 1);
                        } else {
                            wordCount.put(sb.toString(), 1);
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

    public static JSONObject serializeHashMap(String fileName) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(fileName, splitText(fileName));
        return jsonObject;
    }

    public static File writeIndexToFile(String fileName) throws IOException {
        JSONObject jsonObject = new JSONObject(splitText(fileName));
        //File file=new File();
        //file.createNewFile();
        File myObj = new File("output/" + fileName);
        myObj.getParentFile().mkdirs();

        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
            FileWriter fileWriter = new FileWriter(myObj);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
        } else {
            System.out.println("File already exists.");
            FileWriter fileWriter = new FileWriter(myObj);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
            return myObj;
        }
        return null;
    }
}

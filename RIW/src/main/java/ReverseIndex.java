import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReverseIndex {


    public HashMap<String, Map<String, Integer>> parseFile(String fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        HashMap<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
        String key = line;
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        while (line != null) {
            //     System.out.println(line);
            int i;
            if (!line.contains(" ") && line.length() != 0) {
                System.out.println(line);
                HashMap<String, Integer> copy = new HashMap<String, Integer>(hashMap);
                map.put(key, copy);
                hashMap.clear();
                key = line;
            } else {
                i = line.indexOf(" ");
                String sb = line.substring(0, i);

                Integer count = Integer.parseInt(line.substring(i + 1, line.length()));

                hashMap.put(sb, count);
            }
            line = bufferedReader.readLine();
        }
        HashMap<String, Integer> copy = new HashMap<String, Integer>(hashMap);
        map.put(key, copy);
        return map;
    }

    private HashMap<String, HashMap<String, Integer>> reverseIndex = new HashMap<String, HashMap<String, Integer>>();
    //  public void reverseIntex(List<F>)


}

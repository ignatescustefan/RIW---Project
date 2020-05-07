import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;

public class DirectoryProcessing {
    private Queue<File> directoryQueue;
    private File initiaDirectory;

    public DirectoryProcessing(File dir) {
        directoryQueue = new LinkedList<File>();
        initiaDirectory = dir;
        directoryQueue.add(initiaDirectory);
    }

    public List<File> exploreDirectory() {
        List<File> fileList = new ArrayList<>();
        while (!directoryQueue.isEmpty()) {
            File directory = directoryQueue.remove();
            File[] content = directory.listFiles();
            for (File file : content) {
                if (file.isDirectory()) {
                    directoryQueue.add(file);
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    public HashMap<String, List<String>> exploreDirectoryForMoreParallelization() {
        HashMap<String, List<String>> listHashMap = new HashMap<>();
        directoryQueue.clear();
        directoryQueue.add(initiaDirectory);
        while (!directoryQueue.isEmpty()) {
            File directory = directoryQueue.remove();
            File[] content = directory.listFiles();
            for (File file : content) {
                if (file.isDirectory()) {
                    directoryQueue.add(file);
                } else {
                    //e fisier
                    if (listHashMap.containsKey(file.getParent())) {
                        listHashMap.get(file.getParent()).add(file.toString());
                    } else {
                        List<String> list = new LinkedList<>();
                        list.add(file.toString());
                        listHashMap.put(file.getParent(), list);
                    }
                }
            }
        }
        return listHashMap;
    }


    public static HashMap<String, HashMap<String, Integer>> parseFile(String fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
        String key = line;
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        while (line != null) {
            //     System.out.println(line);
            int i;
            if (!line.contains(" ") && line.length() != 0) {
                // System.out.println(line);
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

    public void createDirectIndex() throws IOException {
        int i = 0;
        //creez map file;
        File mapFile = new File("output/mapfile/mapDirect.txt");
        if (mapFile.exists()) {
            mapFile.delete();
        }
        mapFile.getParentFile().mkdirs();
        mapFile.createNewFile();
        PrintWriter printWriterMap = new PrintWriter(mapFile);

        while (!directoryQueue.isEmpty()) {
            File dir = directoryQueue.remove();
            File[] contents = dir.listFiles();
            String output = "output/" + dir + "/b" + i + ".idx";
            File myObj = new File(output);
            if (myObj.exists()) {
                myObj.delete();
            }

            myObj.getParentFile().mkdirs();
            myObj.createNewFile();
            PrintWriter printWriter = new PrintWriter(myObj);
            for (File d : contents) {
                if (d.isFile()) {
                    printWriter.println(d.toString());
                    System.out.println("Se proceseaza fisierul: " + d.toString());
                    HashMap<String, Integer> hashMap = DirectIndex.createDirectIndex(d.toString());

                    for (String key : hashMap.keySet()) {
                        printWriter.println(key + " " + hashMap.get(key));
                    }
                    System.out.println("Se mapeaza: ");
                    printWriterMap.println(d.toString() + " " + output);
                } else if (d.isDirectory()) {
                    directoryQueue.add(d);
                }
            }
            printWriter.close();
            i++;
        }
        printWriterMap.close();
    }

    public void createReverseIndex() throws IOException {

        TreeMap<String, HashMap<String, Integer>> reverseIndex = new TreeMap<>();

//      HashMap<String, HashMap<String, Integer>> reverseIndex = new HashMap<String, HashMap<String, Integer>>();

        File mapFile = new File("output/mapfile/mapIndirect.txt");
        if (mapFile.exists()) {
            mapFile.delete();
        }
        mapFile.getParentFile().mkdirs();
        mapFile.createNewFile();
        PrintWriter printWriterMap = new PrintWriter(mapFile);
        //pt mapare index indirect
        HashMap<String, List<String>> mapList = new HashMap<String, List<String>>();

        while (!directoryQueue.isEmpty()) {
            File dir = directoryQueue.remove();
            File[] files = dir.listFiles();
            for (File d : files) {
                if (d.isDirectory()) {
                    directoryQueue.add(d);
                } else {
                    //process file

                    HashMap<String, HashMap<String, Integer>> mapHashMap = parseFile(d.toString());

                    for (Map.Entry<String, HashMap<String, Integer>> mapElement : mapHashMap.entrySet()) {
                        HashMap<String, Integer> elements = mapElement.getValue();
                        String docName = mapElement.getKey();
                        for (HashMap.Entry<String, Integer> entry : elements.entrySet()) {
                            //System.out.println(entry.getKey() + " " + entry.getValue());
                            String term = entry.getKey();
                            int value = entry.getValue();
                            if (!reverseIndex.containsKey(term)) {
                                HashMap<String, Integer> docCounter = new HashMap<String, Integer>();
                                docCounter.put(docName, value);
                                reverseIndex.put(term, docCounter);
                            } else {
                                HashMap<String, Integer> docCounter = reverseIndex.get(term);
                                docCounter.put(docName, value);
                                reverseIndex.put(term, docCounter);
                            }
                            if (!mapList.containsKey(term)) {
                                mapList.put(term, new LinkedList<String>());
                                mapList.get(term).add(d.toString());
                            } else {
                                if (!mapList.get(term).contains(d.toString())) {
                                    mapList.get(term).add(d.toString());
                                }
                            }
                            //printWriterMap.println(term+" "+d.toString());
                        }
                        //System.out.println(mapElement.getKey() + " : " + elements);
                    }
                }
            }
        }
        for (HashMap.Entry<String, List<String>> element : mapList.entrySet()) {
            printWriterMap.println(element.getKey() + " " + element.getValue());
        }
        File reverseIndexFile = new File("output/reverseIndex/reverseIndex.txt");
        if (reverseIndexFile.exists()) {
            reverseIndexFile.delete();
        }
        reverseIndexFile.getParentFile().mkdirs();
        reverseIndexFile.createNewFile();

        PrintWriter printWriterReverseIndex = new PrintWriter(reverseIndexFile);
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        printWriterReverseIndex.println(gsonBuilder.toJson(reverseIndex));
        printWriterReverseIndex.close();
        printWriterMap.close();
    }

    public static TreeMap<String, HashMap<String, Integer>> loadReverseIndex(String fileName) throws IOException {
        TreeMap<String, HashMap<String, Integer>> reverseIndex = new TreeMap<>();
        //reader
        JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(fileName)));
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            //e cuvant
            String word = jsonReader.nextName();
            HashMap<String, Integer> hashMapCurrentWord = new HashMap<>();
            //citesc urmatorul obiect
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                hashMapCurrentWord.put(jsonReader.nextName(), jsonReader.nextInt());
            }
            jsonReader.endObject();
            reverseIndex.put(word, hashMapCurrentWord);
        }
        jsonReader.endObject();
        return reverseIndex;
    }
}


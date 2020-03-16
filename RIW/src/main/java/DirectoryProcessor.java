import java.io.*;
import java.util.*;

public class DirectoryProcessor {
    private Queue<File> directoryQueue;


    public DirectoryProcessor(File dir) {
        directoryQueue = new LinkedList<>();
        directoryQueue.add(dir);

    }

    public void createDirectIndex() throws IOException {
        int fileNumber = 0;

        File mapFile = new File("output/mapfiles/directIndexMap.txt");
        createNewFileWithLocation(mapFile);
        PrintWriter mapWriter = new PrintWriter(mapFile);

        while (!directoryQueue.isEmpty()) {
            File directory = directoryQueue.remove();
            File[] filesFromDirectory = directory.listFiles();
            String output = "output/" + directory + "/b" + fileNumber + ".idx";

            File outputFile = new File(output);
            createNewFileWithLocation(outputFile);

            PrintWriter printWriter = new PrintWriter(outputFile);
            if (null != filesFromDirectory) {
                for (File file : filesFromDirectory) {
                    if (file.isFile()) {
                        printWriter.println(file.toString());
                        System.out.println("Processing: " + file.toString());
                        HashMap<String, Integer> hashMap = TextSplitter.createDirectIndexFromFile(file.toString());

                        for (String key : hashMap.keySet()) {
                            printWriter.println(key + " " + hashMap.get(key));
                        }

                        // Write map file.
                        mapWriter.println(file.toString() + " " + output);
                    } else if (file.isDirectory()) {
                        directoryQueue.add(file);
                    }
                }
            }
            printWriter.close();
            ++fileNumber;
        }
        mapWriter.close();
    }

    public HashMap<String, HashMap<String, Integer>> parseIndexFile(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        HashMap<String, Integer> wordHashMap = null;
        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();

        String line = bufferedReader.readLine();
        while (line != null) {
            if (line.contains(" ")) {
                int spaceIndex = line.indexOf(" ");
                String word = line.substring(0, spaceIndex);

                Integer count = Integer.parseInt(line.substring(spaceIndex + 1));
                assert null != wordHashMap;
                wordHashMap.put(word, count);
            } else if (line.length() > 0) {
                wordHashMap = new HashMap<>();
                map.put(line, wordHashMap);
            }
            line = bufferedReader.readLine();
        }
        return map;
    }

    public void createReverseIndex() throws IOException {
        HashMap<String, HashMap<String, Integer>> reverseIndex = new HashMap<>();
        File mapFile = new File("output/mapfiles/reverseMap.txt");
        createNewFileWithLocation(mapFile);
        PrintWriter mapWriter = new PrintWriter(mapFile);

        HashMap<String, List<String>> mapList = new HashMap<>();

        while (!directoryQueue.isEmpty()) {
            File directory = directoryQueue.remove();
            File[] filesFromDirectory = directory.listFiles();
            if (null != filesFromDirectory) {
                for (File file : filesFromDirectory) {
                    if (file.isDirectory()) {
                        directoryQueue.add(file);
                    } else {
                        // Process a file.
                        HashMap<String, HashMap<String, Integer>> mapHashMap = parseIndexFile(file.toString());

                        for (Map.Entry<String, HashMap<String, Integer>> mapElement : mapHashMap.entrySet()) {
                            HashMap<String, Integer> elements = mapElement.getValue();
                            String docName = mapElement.getKey();
                            for (HashMap.Entry<String, Integer> entry : elements.entrySet()) {

                                String word = entry.getKey();
                                int noOccurrences = entry.getValue();

                                if (reverseIndex.containsKey(word)) {
                                    HashMap<String, Integer> docCounter = reverseIndex.get(word);
                                    docCounter.put(docName, noOccurrences);
                                    reverseIndex.put(word, docCounter);
                                } else {
                                    HashMap<String, Integer> docCounter = new HashMap<>();
                                    docCounter.put(docName, noOccurrences);
                                    reverseIndex.put(word, docCounter);
                                }
                                if (!mapList.containsKey(word)) {
                                    mapList.put(word, new LinkedList<>());
                                }
                                mapList.get(word).add(file.toString());
                            }
                        }
                    }
                }
            }
        }

        for (HashMap.Entry<String, List<String>> element : mapList.entrySet()) {
            System.out.println(element.getKey() + " " + element.getValue());
            mapWriter.println(element.getKey() + " " + element.getValue());
        }

        File reverseIndexFile = new File("output/reverseIndex/reverseIndex.txt");
        createNewFileWithLocation(reverseIndexFile);
        PrintWriter reverseIndexWriter = new PrintWriter(reverseIndexFile);

        for (HashMap.Entry<String, HashMap<String, Integer>> entry : reverseIndex.entrySet()) {
            String word = entry.getKey();
            reverseIndexWriter.print(word + " ");

            for (HashMap.Entry<String, Integer> doc : entry.getValue().entrySet()) {

                reverseIndexWriter.print("{" + doc.getKey() + " " + doc.getValue() + "} ");
            }

            reverseIndexWriter.println();
        }
        mapWriter.close();
        reverseIndexWriter.close();
    }

    private void createNewFileWithLocation(File outputFile) throws IOException {
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
    }
}

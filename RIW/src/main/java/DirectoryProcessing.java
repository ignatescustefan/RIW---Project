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

    public HashMap<String, HashMap<String, Integer>> parseFile(String fileName) throws IOException {
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
                    HashMap<String, Integer> hashMap = SplitText.splitText(d.toString());

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

        HashMap<String, HashMap<String, Integer>> reverseIndex = new HashMap<String, HashMap<String, Integer>>();

        File mapFile = new File("output/mapfile/mapIndirect.txt");
        if (mapFile.exists()) {
            mapFile.delete();
        }
        mapFile.getParentFile().mkdirs();
        mapFile.createNewFile();
        PrintWriter printWriterMap = new PrintWriter(mapFile);

        HashMap<String,List<String>> mapList=new HashMap<String, List<String>>();

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
                            if(mapList.containsKey(term)){
                                mapList.get(term).add(d.toString());
                            }
                            else{
                                mapList.put(term,new LinkedList<String>());
                                mapList.get(term).add(d.toString());
                            }
                            //printWriterMap.println(term+" "+d.toString());
                        }
                        //System.out.println(mapElement.getKey() + " : " + elements);
                    }
                }
            }
        }

        for(HashMap.Entry<String,List<String>> element : mapList.entrySet()){
            System.out.println(element.getKey()+" "+element.getValue());
            printWriterMap.println(element.getKey()+" "+element.getValue());
        }
        File reverseIndexFile = new File("output/reverseIndex/reverseIndex.txt");
        if (reverseIndexFile.exists()) {
            reverseIndexFile.delete();
        }
        reverseIndexFile.getParentFile().mkdirs();
        reverseIndexFile.createNewFile();
        PrintWriter printWriterReverseIndex = new PrintWriter(reverseIndexFile);
//        System.out.println(reverseIndex.size());
        for (HashMap.Entry<String, HashMap<String, Integer>> element : reverseIndex.entrySet()) {
            String term = element.getKey();
            //   System.out.print(term+": ");
            printWriterReverseIndex.print(term + " ");
            for (HashMap.Entry<String, Integer> doc : element.getValue().entrySet()) {
                //     System.out.print("{"+doc.getKey()+" "+doc.getValue()+"} " );
                printWriterReverseIndex.print("{" + doc.getKey() + " " + doc.getValue() + "} ");
            }
            // System.out.println();
            printWriterReverseIndex.println();
        }
        printWriterMap.close();
//        System.out.println("Nr word="+nrWord+", nr date:" +nr);
    }

}

import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class DirectoryProcessing {
    private Queue<File> directoryQueue;
    private Queue<File> filesQueue;
    private File initiaDirectory;


    public DirectoryProcessing(File dir) {
        directoryQueue = new LinkedList<File>();
        filesQueue = new LinkedList<File>();
        initiaDirectory = dir;
        directoryQueue.add(initiaDirectory);

    }

    public void createDirectIndex() throws IOException {
        int i=0;
        //creez map file;
        File mapFile=new File("output/map"+initiaDirectory.toString()+".txt");
        if(mapFile.exists()){
            mapFile.delete();
        }
        mapFile.getParentFile().mkdirs();
        mapFile.createNewFile();
        PrintWriter printWriterMap=new PrintWriter(mapFile);

        while (!directoryQueue.isEmpty()) {
            File dir = directoryQueue.remove();
            File[] contents = dir.listFiles();
            String output="output/"+dir+"/b"+i+".idx";
            File myObj = new File(output);
            if(myObj.exists()){
                myObj.delete();
            }

            myObj.getParentFile().mkdirs();
            myObj.createNewFile();
            PrintWriter printWriter=new PrintWriter(myObj);
            for (File d : contents) {
                if (d.isFile()) {
                    filesQueue.add(d);
                    printWriter.println(d.toString());
                    System.out.println("Se proceseaza fisierul: "+d.toString());
                    HashMap<String,Integer> hashMap=SplitText.splitText(d.toString());

                    for (String key : hashMap.keySet()) {
                        printWriter.println(key + " " + hashMap.get(key));
                    }
                    System.out.println("Se mapeaza: ");
                    printWriterMap.println(d.toString()+" "+ output);
                } else if (d.isDirectory()) {
                    directoryQueue.add(d);
                }
            }
            printWriter.close();
            i++;
        }
        printWriterMap.close();
    }

    public void processngFiles() throws IOException {
//        File mapFile=new File("output/map"+initiaDirectory.toString()+".txt");
//        if(mapFile.exists()){
//            mapFile.delete();
//        }
//        mapFile.getParentFile().mkdirs();
//        mapFile.createNewFile();
//        PrintWriter printWriterMap=new PrintWriter(mapFile);
//        int i=0;
//        while (!filesQueue.isEmpty()) {
//            File file = filesQueue.remove();
//
//
//            String output="output/"+file.getParent()+"/b.idx";
//            File myObj = new File(output);
//            if(!myObj.exists()){
//                myObj.getParentFile().mkdirs();
//                myObj.createNewFile();
//            }
//
//        }
//        printWriterMap.close();
    }

}

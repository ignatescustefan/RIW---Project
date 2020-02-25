import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Laborator2 {
    private Queue<File> directoryQueue;
    private Queue<File> filesQueue;
    private File initiaDirectory;


    public Laborator2(File dir){
        directoryQueue=new LinkedList<File>();
        filesQueue=new LinkedList<File>();
        initiaDirectory=dir;
        directoryQueue.add(initiaDirectory);

    }

    public void parseDirectory(){
        while (!directoryQueue.isEmpty())
        {
            File dir = directoryQueue.remove();
            File[] contents= dir.listFiles();
            for(File d:contents){
                if(d.isFile()){
                    filesQueue.add(d);
                }
                else if (d.isDirectory()){
                    directoryQueue.add(d);
                }
            }
        }
        System.out.println(filesQueue);
        System.out.println(filesQueue.size());
    }
    public void processingFiles(){
        while (!filesQueue.isEmpty()){
            String file=filesQueue.remove().toString();
            System.out.println(file);
        }
    }

}

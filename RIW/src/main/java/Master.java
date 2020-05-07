import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class Master {
    private DirectoryProcessing directoryProcessing;

    private MongoClient mongoClient;
    private MongoDatabase databaseDirectIndex;
    private MongoDatabase databaseReverseIndex;
    private MongoDatabase databasePartialReverse;

    public Master(File dir) {
        directoryProcessing = new DirectoryProcessing(dir);
        mongoClient = new MongoClient("localhost", 27017);
        databaseDirectIndex = mongoClient.getDatabase("directIndexRIW");
        databaseReverseIndex = mongoClient.getDatabase("reverseIndex");
        databasePartialReverse = mongoClient.getDatabase("partialReverse");
    }

    public void startDirectIndex() {
        //iau lista de fisiere din structura de directoare
        List<File> fileList = directoryProcessing.exploreDirectory();//creez index mapare?
        //fac drop la database

        databaseDirectIndex.drop();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            for (File file : fileList) {
                executorService.execute(new Worker(file.toString(), IndexType.DIRECT_INDEX, null));
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) ;

            System.out.println("Index direct terminat");
        } catch (Exception e) {
            e.printStackTrace();
            executorService.shutdown();
        }
    }

//    public void startReverseIndex() {
//        //iau toate colectiile din dbIndexdirect
//        List<String> collections = getCollections(databaseDirectIndex);
//        databaseReverseIndex.drop();
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
//        try {
//            for (String collection : collections) {
//                executorService.execute(new Worker(collection, IndexType.REVERSE_INDEX, null));
//            }
//            executorService.shutdown();
//            while (!executorService.isTerminated()) ;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            executorService.shutdown();
//        }
//    }


    public void startFinalMergeReverseIndex() {
        startReverseIndexForDirectory();
        databaseReverseIndex.drop();
        List<String> collections = getCollections(databasePartialReverse);
        // fac merge la index partial invers
        try {
            Thread t = new Thread(new Worker("", IndexType.REVERSE_INDEX, collections));
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startReverseIndexForDirectory() {
        //iau toate colectiile din dbIndexdirect
        //List<String> collections = getCollections();
        HashMap<String, List<String>> partialReverse = directoryProcessing.exploreDirectoryForMoreParallelization();
        // calculez partial pt fiecare folder
        // fac merge pt colectiile partiale
        databasePartialReverse.drop();

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            for (Map.Entry<String, List<String>> directory : partialReverse.entrySet()) {
                executorService.execute(new Worker(directory.getKey(), IndexType.PARTIAL_REVERSE, directory.getValue()));
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) ;

        } catch (Exception e) {
            e.printStackTrace();
            executorService.shutdown();
        }
    }

    private List<String> getCollections(MongoDatabase databaseDirectIndex) {
        List<String> collections = new ArrayList<>();
        for (String collectionName : databaseDirectIndex.listCollectionNames()) {
            collections.add(collectionName);
        }
        return collections;
    }
}

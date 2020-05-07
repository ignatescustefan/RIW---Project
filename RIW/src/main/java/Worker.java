import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;

public class Worker implements Runnable {
    private String filePath;
    private IndexType indexType;
    private MongoClient mongoClient;
    private MongoDatabase databaseDirectIndex;
    private MongoDatabase databaseReverseIndex;
    private MongoDatabase databasaPartialReverse;
    private List<String> collectionList;

    public Worker(String filePath, IndexType indexType, List<String> collectionList) {
        this.filePath = filePath;
        this.indexType = indexType;
        mongoClient = new MongoClient("localhost", 27017);
        databaseDirectIndex = mongoClient.getDatabase("directIndexRIW");

        this.collectionList = collectionList;
    }

    @Override
    public void run() {
        if (indexType == IndexType.DIRECT_INDEX) {
            //creez hashmap pt fisierul curent
            System.out.println("Se proceseaza indexul direct pe fisierul: " + filePath);
            try {
                HashMap<String, Integer> indexDirect = DirectIndex.createDirectIndex(filePath);
                //il pun in mongo
                saveDirectIndexToMongo(indexDirect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (indexType == IndexType.REVERSE_INDEX) {
            System.out.println("Se calculeaza indexul invers final");
            databaseReverseIndex = mongoClient.getDatabase("reverseIndex");
            databasaPartialReverse = mongoClient.getDatabase("partialReverse");

            HashMap<String, HashMap<String, Integer>> reverseIndex = mergeAllPartialReverseIndex();
            saveReverseIndexToMongo(reverseIndex, databaseReverseIndex.getCollection("reverseIndex", Document.class));
        } else if (indexType == IndexType.PARTIAL_REVERSE) {
            System.out.println("Se calculeaza indexul invers partial pentru: " + filePath);
            databasaPartialReverse = mongoClient.getDatabase("partialReverse");
            MongoCollection<Document> collection = databasaPartialReverse.getCollection(filePath, Document.class);

            //calculez index partial
            //iau lista de fisiere pe care trebuie sa parcurg
            //creez
            HashMap<String, HashMap<String, Integer>> partiaReverse = getPartialReverseIndex();
            saveReverseIndexToMongo(partiaReverse, collection);
        }
    }

    @NotNull
    private HashMap<String, HashMap<String, Integer>> getPartialReverseIndex() {
        HashMap<String, HashMap<String, Integer>> partiaReverse = new HashMap<>();
        for (String direct : collectionList) {
            //iau indexul direct
            HashMap<String, Integer> directIndex = getDirectIndex(direct);
            //
            for (Map.Entry<String, Integer> entry : directIndex.entrySet()) {
                if (partiaReverse.containsKey(entry.getKey())) {
                    partiaReverse.get(entry.getKey()).put(direct, entry.getValue());
                } else {
                    HashMap<String, Integer> value = new HashMap<>();
                    value.put(direct, entry.getValue());
                    partiaReverse.put(entry.getKey(), value);
                }
            }
            // computePartial(collection, directIndex,direct);
        }
        return partiaReverse;
    }

    private void computePartial(MongoCollection<Document> collection, HashMap<String, Integer> directIndex, String path) {
        for (Map.Entry<String, Integer> entry : directIndex.entrySet()) {
            Document newDoc = new Document();
            newDoc.append("file", path);
            newDoc.append("count", entry.getValue());

            //caut daca exista cheia in indexul indirect
            BasicDBObject inQuery = new BasicDBObject();
            inQuery.put("term", entry.getKey());

            FindIterable<Document> iterable = collection.find(inQuery);
            if (iterable.iterator().hasNext()) {
                //daca exista fac update
                Document updateQuery = new Document();
                updateQuery.put("$push", new Document().append("docs", newDoc));
                //fac update
                collection.updateOne(inQuery, updateQuery);
                // System.out.println("insert: " + newDoc);
            } else {
                //fac insert
                Document doc = new Document();
                List<Document> files = new LinkedList<>();
                files.add(newDoc);
                doc.append("term", entry.getKey()).
                        append("docs", files);
                collection.insertOne(doc);
            }
        }
    }

    private HashMap<String, Integer> getDirectIndex(String filePath) {
        MongoCollection<Document> indexDirect = databaseDirectIndex.getCollection(filePath, Document.class);
        FindIterable<Document> findIterable = indexDirect.find().projection(fields(excludeId()));
        MongoCursor<Document> cursor = findIterable.iterator();

        HashMap<String, Integer> mapDirect = new HashMap<>();


        while (cursor.hasNext()) {
            Document obj = cursor.next();
            mapDirect.put(obj.getString("term"), obj.getInteger("count"));
        }
        cursor.close();
//        System.out.println(mapDirect);
        return mapDirect;
    }

    private HashMap<String, HashMap<String, Integer>> getPartialReverse(MongoCollection<Document> collection) {
        HashMap<String, HashMap<String, Integer>> partialReverse = new HashMap<>();
        FindIterable<Document> findIterable = collection.find().projection(fields(excludeId()));
        MongoCursor<Document> cursor = findIterable.iterator();

        while (cursor.hasNext()) {
            Document obj = cursor.next();

            HashMap<String, Integer> docs = new HashMap<>();
            List<Document> documentList = (List<Document>) obj.get("docs");
            for (Document d : documentList) {

                docs.put(d.getString("file"), d.getInteger("count"));
            }
            partialReverse.put(obj.getString("term"), docs);
        }
        cursor.close();
        return partialReverse;
    }

    private void saveDirectIndexToMongo(HashMap<String, Integer> directIndex) {
        MongoCollection<BasicDBObject> collection = databaseDirectIndex.getCollection(filePath, BasicDBObject.class);
        List<BasicDBObject> documents = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : directIndex.entrySet()) {
            BasicDBObject document = new BasicDBObject();
            document.put("term", entry.getKey());
            document.put("count", entry.getValue());
            documents.add(document);
        }
        collection.insertMany(documents);
    }

    private void saveReverseIndexToMongo(HashMap<String, HashMap<String, Integer>> reverseIndex, MongoCollection<Document> collection) {
        List<Document> documents = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, Integer>> entry : reverseIndex.entrySet()) {
            Document document = new Document();
            document.put("term", entry.getKey());
            //
            List<Document> documentList = new LinkedList<>();
            for (Map.Entry<String, Integer> mapEntry : entry.getValue().entrySet()) {
                Document doc = new Document();
                doc.put("file", mapEntry.getKey());
                doc.put("count", mapEntry.getValue());
                documentList.add(doc);
            }
            document.put("docs", documentList);
            documents.add(document);
        }
        collection.insertMany(documents);
    }

    private HashMap<String, HashMap<String, Integer>> mergeAllPartialReverseIndex() {
        //
        // colectia finala e prima colectie->scapt de o iteratie;
        HashMap<String, HashMap<String, Integer>> finalReverseindex = getPartialReverse(databasaPartialReverse.getCollection(collectionList.get(0), Document.class));
        for (int i = 1; i < collectionList.size(); i++) {
            HashMap<String, HashMap<String, Integer>> partialReverse = getPartialReverse(databasaPartialReverse.getCollection(collectionList.get(i), Document.class));
            //parcurg partial reverse;
            for (Map.Entry<String, HashMap<String, Integer>> partial : partialReverse.entrySet()) {
                //verific daca termenul exista in reverse
                if (finalReverseindex.containsKey(partial.getKey())) {
                    //faca append
                    finalReverseindex.get(partial.getKey()).putAll(partial.getValue());
                } else {
                    finalReverseindex.put(partial.getKey(), partial.getValue());
                }
            }
        }
        System.out.println(finalReverseindex.size());
        return finalReverseindex;
    }
}

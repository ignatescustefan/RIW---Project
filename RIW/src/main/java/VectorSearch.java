import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;

public class VectorSearch implements Search {

    private MongoDatabase databaseDirectIndex;
    private MongoDatabase databaseReverseIndex;

    private String reverseIndex;

    public VectorSearch(String reverseIndex) {
        this.reverseIndex = reverseIndex;
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        databaseDirectIndex = mongoClient.getDatabase("directIndexRIW");
        databaseReverseIndex = mongoClient.getDatabase(reverseIndex);
    }

    //tf
    private double getTermFrequency(String word, String document) {
        MongoCollection<Document> collection = databaseDirectIndex.getCollection(document);
        //iau informatii despre terment din colectia curenta
        BasicDBObject inQuery = new BasicDBObject();
        inQuery.put("term", word);
        BasicDBObject projectionField = new BasicDBObject();

        FindIterable<Document> findIterable = collection.find(inQuery).projection(fields(excludeId()));
        if (findIterable.iterator().hasNext()) {
            Document doc = findIterable.iterator().next();
            return (double) doc.getInteger("count") / collection.count();
        }
        return 0;
    }

    //idf
    private double getInverseDocumentFrequency(String key) {
        MongoCollection<Document> collection = databaseReverseIndex.getCollection(reverseIndex);
        long count = 0;
        for (String s : databaseDirectIndex.listCollectionNames()) {
            count++;
        }
        // count=databaseReverseIndex.getCollection(reverseIndex).count();
        BasicDBObject inQuery = new BasicDBObject();
        inQuery.put("term", key);
        FindIterable<Document> findIterable = collection.find(inQuery).projection(fields(excludeId()));
        if (findIterable.iterator().hasNext()) {
            Document document = findIterable.iterator().next();
            //am documentul cu datele despre cheie
            //aflu dimensiunea array-ului
            List<Document> files = (List<Document>) document.get("docs");
            int size = files.size();
            //aplic formula
            return Math.log((double) count / (size));
        }
        return 0;
    }


    private double getTermFrequencyQuery(String key, Queue<String> documents) {
        int noApparations = 0;
        for (String value : documents) {
            if (value.equals(key)) {
                noApparations++;
            }
        }
        return (double) noApparations / documents.size();
    }

    private HashMap<String, Double> transformInterogation(Queue<String> terms) {
        HashMap<String, Double> result = new HashMap<>();
        for (String key : terms) {
            result.put(key, getTermFrequencyQuery(key, terms) * getInverseDocumentFrequency(key));
        }
        return result;
    }


    //calculez doar pt datele din setul rezultat in urma cautarii boolene si cu termenii din cautare
    private HashMap<String, HashMap<String, Double>> tranformDocumentSet(Queue<String> terms, Set<String> initialSet) {
        HashMap<String, HashMap<String, Double>> result = new HashMap<>();
        for (String coll : initialSet) {
            HashMap<String, Double> vector = new HashMap<>();
            //pt fiecare colectie calcule key:tf(key,d)*idf(key)
            //iau termenii
            for (String key : terms) {
                double value = getTermFrequency(key, coll) * getInverseDocumentFrequency(key);
                vector.put(key, value);
            }
            result.put(coll, vector);
        }
        return result;
    }

    private double similaritateCos(HashMap<String, Double> doc1, HashMap<String, Double> doc2) {//dintre 2 vectoril

        //parcug doc care are elemente mai putine
        //doc1<doc2
        //produs scalar : sum(abs(doc1(i)*doc2(i))
        //norma euclidiana: sqrt(sum(doc1(i)^2)
        //                  sqrt(sum(doc2(i)^2)

        //doar pt cuvintele comune

        double sumScalar = 0.0;
        double normDoc1 = 0.0;
        double normDoc2 = 0.0;

        boolean isOneCommon = false;
        for (String key : doc1.keySet()) {
            if (doc2.containsKey(key)) {
                isOneCommon = true;
                sumScalar += Math.abs(doc1.get(key) * doc2.get(key));
            }
            normDoc1 += doc1.get(key) * doc1.get(key);
        }
        for(String key:doc2.keySet()){
            normDoc2 += doc2.get(key) * doc2.get(key);
        }
        if (!isOneCommon || normDoc1 == 0 || normDoc2 == 0) {
            return 0;
        }
        return Math.abs(sumScalar) / (Math.sqrt(normDoc1) * Math.sqrt(normDoc2));
    }


    @Override
    public Set<String> generalSearch(String query) {
        QuerySpliter querySpliter = new QuerySpliter();
        querySpliter.spitQuery(query.replace('+',' '));
        Search search = new BooleanSearchMongo(reverseIndex);
        Set<String> documents = search.generalSearch(query);
        //am lista restransa de documente;
        //
        HashMap<String, Double> queryVector = transformInterogation(querySpliter.getTerms());
        HashMap<String, HashMap<String, Double>> documentVector = tranformDocumentSet(querySpliter.getTerms(), documents);

        HashMap<String, Double> resultSearch = new HashMap<>();

        for (String document : documentVector.keySet()) {
            //calculez similaritatea
            double sim = similaritateCos(documentVector.get(document), queryVector);
            if (sim != 0) {
                resultSearch.put(document, sim);
            }
        }

        //System.out.println("Initial search: " + resultSearch);


        //sortare result search dupa valoare
        resultSearch = sortHashMapByValue(resultSearch);
        System.out.println("Sorted search: " + resultSearch);
        return resultSearch.keySet();
    }

    private HashMap<String, Double> sortHashMapByValue(HashMap<String, Double> hashMap) {
        return hashMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (x, y) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));
    }
}

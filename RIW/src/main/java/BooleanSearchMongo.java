import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BooleanSearchMongo implements Search {

    private String reverseDatabase;
    private QuerySpliter querySpliter;
    private MongoClient client;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> collection;

    public BooleanSearchMongo(String reverseDatabase) {
        this.client = new MongoClient("localhost", 27017);
        this.reverseDatabase = reverseDatabase;
        this.mongoDatabase = client.getDatabase(reverseDatabase);
        this.collection = mongoDatabase.getCollection("reverseIndex");
        this.querySpliter = new QuerySpliter();
    }

    private Set<String> getEntryForKey(String key) {
        //search
        Set<String> entrySet = new HashSet<>();
        BasicDBObject inQuery = new BasicDBObject();
        BasicDBObject projectField = new BasicDBObject();//{_id:0,term:0,"docs.count":0})
        inQuery.put("term", key);
        projectField.put("_id", 0);
        projectField.put("term", 0);
        projectField.put("docs.count", 0);
        ArrayList<Document> iterable = collection.find(inQuery).projection(projectField).into(new ArrayList<>());

        if (iterable.iterator().hasNext()) {
            Document document = iterable.get(0);
            List<Document> list = (List<Document>) document.get("docs");

            for (Document file : list) {
                //System.out.println(file);
                entrySet.add(file.getString("file"));
            }
            //   System.out.println(document.get("docs"));
        }
        return entrySet;
    }

    public Set<String> orOperation(Set<String> firstKeySet, Set<String> secondKeySet) {
        int nrFirstKeySet = firstKeySet.size();
        int nrSecondKeySet = secondKeySet.size();

        if (nrFirstKeySet < nrSecondKeySet) {
            for (String entry : firstKeySet) {
                if (!secondKeySet.contains(entry)) {
                    secondKeySet.add(entry);
                }
            }
            return secondKeySet;
        } else {
            for (String entry : secondKeySet) {
                if (!firstKeySet.contains(entry)) {
                    firstKeySet.add(entry);
                }
            }
            return firstKeySet;
        }
    }

    public Set<String> andOperation(Set<String> firstKeySet, Set<String> secondKeySet) {
        int nrFirstKeySet = firstKeySet.size();
        int nrSecondKeySet = secondKeySet.size();
        if (nrFirstKeySet < nrSecondKeySet) {
            firstKeySet.removeIf(entry -> !secondKeySet.contains(entry));
            return firstKeySet;
        } else {
            secondKeySet.removeIf(entry -> !firstKeySet.contains(entry));
            return secondKeySet;
        }
    }

    public Set<String> notOperation(Set<String> firstKeySet, Set<String> secondKeySet) {
        Set<String> result = new HashSet<>();
        for (String doc : firstKeySet) {
            if (!secondKeySet.contains(doc)) {
                result.add(doc);
            }
        }
        return result;
    }


    @Override
    public Set<String> generalSearch(String query) {
        querySpliter.spitQuery(query);
        Set<String> result = null;
        if (querySpliter.getTerms().isEmpty()) return null;
        try {
            result = getEntryForKey(querySpliter.getTerms().remove().toLowerCase());
            while (!querySpliter.getTerms().isEmpty() && !querySpliter.getOperation().isEmpty()) {
                String term = querySpliter.getTerms().remove();
                Character operator = querySpliter.getOperation().remove();
                Set<String> currentSet = getEntryForKey(term.toLowerCase());
                result = switchOverOperation(result, currentSet, operator);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public Set<String> switchOverOperation(Set<String> firstSet, Set<String> secondSet, Character op) {

        switch (op) {
            case '+':
                return andOperation(firstSet, secondSet);
            case ' ':
                return orOperation(firstSet, secondSet);
            case '~':
                return notOperation(firstSet, secondSet);
            default:
                return null;
        }
    }
}
import java.util.*;

public class BooleanSearch {

    private TreeMap<String, HashMap<String, Integer>> reverseIndex;

    public BooleanSearch(TreeMap<String, HashMap<String, Integer>> reverseIndex) {
        this.reverseIndex = reverseIndex;
    }

    private Set<String> getEntryForKey(String key) {
        return reverseIndex.get(key).keySet();
    }

    public Set<String> orOperation(String firstKey, String secondKey) {
        Set<String> firstKeySet = getEntryForKey(firstKey);
        Set<String> secondKeySet = getEntryForKey(secondKey);
        int nrFirstKeySet = firstKeySet.size();
        int nrSecondKeySet = secondKeySet.size();

        if (nrFirstKeySet < nrSecondKeySet) {
            for (String doc : firstKeySet) {
                secondKeySet.add(doc);
            }
            return secondKeySet;
        } else {
            for (String doc : secondKeySet) {
                firstKeySet.add(doc);
            }
            return firstKeySet;
        }
    }

    public Set<String> andOperation(String firstKey, String secondKey) {
        Set<String> firstKeySet = getEntryForKey(firstKey);
        Set<String> secondKeySet = getEntryForKey(secondKey);
        int nrFirstKeySet = firstKeySet.size();
        int nrSecondKeySet = secondKeySet.size();
        if (nrFirstKeySet < nrSecondKeySet) {
            firstKeySet.removeIf(secondKeySet::contains);
            return firstKeySet;
        } else {
            secondKeySet.removeIf(firstKeySet::contains);
            return secondKeySet;
        }
    }

    public Set<String> notOperation(String firstKey, String secondKey) {
        Set<String> firstKeySet = getEntryForKey(firstKey);
        Set<String> secondKeySet = getEntryForKey(secondKey);
        Set<String> result = new HashSet<>();
        for (String doc : firstKeySet) {
            if (!secondKeySet.contains(doc)) {
                result.add(doc);
            }
        }
        return result;
    }

    public void spitQuery(String query){
        Queue<String> terms=new LinkedList<>();
        Queue<String> operation=new LinkedList<>();

        for(int i=0;i<query.length();i++){
            if (query.charAt(i)==' '){

            }
        }
    }

    public Set<String> generalOperation(Queue<String> terms, Queue<String> operation) {
        Set<String> result = null;
        String first;
        String second;
        String op;
        while (!terms.isEmpty()) {
            if (null == result) {
                first = terms.remove();
                second = terms.remove();
                op=operation.remove();
                result = switchOverOperation(result, first, second, op);
            } else {
                first = terms.remove();
            }

        }
        return result;
    }

    private Set<String> switchOverOperation(Set<String> result, String first, String second, String op) {
        switch (op) {
            case "and":
                result=andOperation(first,second);
                break;
            case "or":
                result=orOperation(first,second);
                break;
            case "not":
                result=notOperation(first,second);
                break;
            default:
        }
        return result;
    }
}

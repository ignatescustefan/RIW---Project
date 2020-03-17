import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BooleanSearch {

    private TreeMap<String, HashMap<String, Integer>> reverseIndex;
    private Queue<String> terms;
    private Queue<Character> operation;
    private WordStored wordStored = new WordStored(new File("files/stopwords.txt"), new File("files/exception.txt"));

    public BooleanSearch(TreeMap<String, HashMap<String, Integer>> reverseIndex) throws FileNotFoundException {
        this.reverseIndex = reverseIndex;
        terms = new LinkedList<>();
        operation = new LinkedList<>();
    }

    private Set<String> getEntryForKey(String key) {
        if (reverseIndex.containsKey(key))
            return new HashSet<>(reverseIndex.get(key).keySet()) ;
        return new HashSet<>();
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

    public void spitQuery(String query) {
        terms.clear();
        operation.clear();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < query.length(); i++) {
            if (Character.isLetterOrDigit(query.charAt(i))) {
                sb.append(query.charAt(i));
            } else {
                if (wordStored.isStored(sb.toString())) {
                    operation.add(query.charAt(i));
                    terms.add(sb.toString());
                }
                sb.setLength(0);
            }
        }
        if(wordStored.isStored(sb.toString())){
            terms.add(sb.toString());
        }
        System.out.println(terms + " " + operation);
    }


    public Set<String> generalOperation(String query) {
        spitQuery(query);
        Set<String> result = null;
        try {
            result = getEntryForKey(terms.remove().toLowerCase());
            while (!terms.isEmpty() && !operation.isEmpty()) {
                String term = terms.remove();
                Character operator = operation.remove();
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

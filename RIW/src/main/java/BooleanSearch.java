import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BooleanSearch implements Search {

    private TreeMap<String, HashMap<String, Integer>> reverseIndex;
    private QuerySpliter querySpliter;

    public BooleanSearch(TreeMap<String, HashMap<String, Integer>> reverseIndex) {
        this.reverseIndex = reverseIndex;
        this.querySpliter = new QuerySpliter();
    }

    private Set<String> getEntryForKey(String key) {
        if (reverseIndex.containsKey(key))
            return new HashSet<>(reverseIndex.get(key).keySet());
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

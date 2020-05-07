import java.util.LinkedList;
import java.util.Queue;

public class QuerySpliter {
    private Queue<String> terms;
    private Queue<Character> operation;
    private WordStored wordStored;

    public QuerySpliter() {
        operation = new LinkedList<>();
        terms = new LinkedList<>();
        try {
            wordStored = new WordStored();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void spitQuery(String query) {
        getTerms().clear();
        getOperation().clear();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < query.length(); i++) {
            if (Character.isLetterOrDigit(query.charAt(i))) {
                sb.append(query.charAt(i));
            } else {
                String baseForm = wordStored.isStored(sb.toString());
                if (baseForm != null) {

                    getOperation().add(query.charAt(i));
                    getTerms().add(baseForm);
                }
                sb.setLength(0);
            }
        }
        String baseForm = wordStored.isStored(sb.toString());
        if (baseForm != null) {
            getTerms().add(baseForm);
        }
        //  System.out.println(terms + " " + operation);
    }

    public Queue<String> getTerms() {
        return terms;
    }

    public Queue<Character> getOperation() {
        return operation;
    }
}

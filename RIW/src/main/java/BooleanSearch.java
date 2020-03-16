import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter

public class BooleanSearch implements Search {
    private HashMap<String, HashMap<String, Integer>> reverseIndex;

    /**
     * returns a list of documents
     */
    public List<String> andOperation(String key1, String key2) {
        
    }
}
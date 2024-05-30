import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class test {
    public static void test1() {
        new ArrayList<Integer>().toArray(new Integer[0]);
        new HashMap<Integer, List<Integer>>().computeIfAbsent(1, k -> new ArrayList<>()).add(1);
    }
}

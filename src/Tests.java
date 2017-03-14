import java.util.ArrayList;
import java.util.List;

public class Tests {
    public static void main(String[] args) {
        Functions f = new Functions();
        List<Integer> combo = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            combo = f.getNextCombination(combo, 4);
            System.out.println(combo);
        }
    }
}

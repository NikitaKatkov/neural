import java.util.ArrayList;
import java.util.List;

public class Functions {
    //методы данного класса потенциально могут использоваться во многих работах
    public List<List<Integer>> getCombinationSet(int lengthOfCombination, int maximumCombinationElement) {
        //получает следующее подмножество из предыдущего, length - количество членов, size - наибольшее значение члена комбинации
        List<List<Integer>> combinationSet = new ArrayList<>();
        List<Integer> combination = new ArrayList<>();
        for (int i = 0; i < lengthOfCombination; i++) {
            combination.add(i); //генерация первой последовательности
        }
        combinationSet.add(0, combination);

        recursiveIncrement(combinationSet, lengthOfCombination-1, maximumCombinationElement);
        return combinationSet;
    }

    private boolean recursiveIncrement (List<List<Integer>> combinationSet, int index, int maximumCombinationElement) {
        return false;
    }

    private int C(int n, int k) {
        return factorial(n)/factorial(k)/factorial(n-k);
    }

    private int factorial(int n) {
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result*=i;
        }
        return result;
    }
}

package main;

import java.util.ArrayList;
import java.util.List;

class Functions {
    //методы данного класса потенциально могут использоваться во многих работах

    static List<Integer> getNextCombination(List<Integer> combination, int maxCombinationSize) {
        int combinationSize = combination.size(); //длина предыдущей комбинации
        boolean lengthChange = true; //флаг необходимости увеличения длины комбинации

        // проверка на пустоту - для первого вызова метода
        if (combination.size() == 0) {
            combination.add(0,0);
            return combination;
        }
        //проверка на последнюю возможную комбинацию данной длины
        if (combination.get(combinationSize-1) < maxCombinationSize - 1) { //максимальное ли значение у последнего члена
            lengthChange = false;
        } else {//отличаются ли все соседние элементы на единицу или больше
            for (int i = combinationSize - 1; i > 0; i--) {
                if (combination.get(i) - combination.get(i - 1) > 1) {
                    lengthChange = false;
                    break;
                } else if (combination.size() == maxCombinationSize) return new ArrayList<Integer>(); //после возвращения самого множества вернем пустой список
            }
        }
        //если требуется создать комбинацию большей длины
        if (lengthChange) { //генерация комбинации из наименьших элементов длины combinationSize + 1
            combination.clear();
            for (int i = 0; i < combinationSize + 1; i++) {
                combination.add(i, i);
            }
        } else { //изменение членов комбинации в лексикографическом порядке при ее фиксированном размере
            int indexToIncrease = combinationSize - 1;
            while (combination.get(indexToIncrease) >= maxCombinationSize - combinationSize + indexToIncrease) indexToIncrease--;
            combination.set(indexToIncrease, combination.get(indexToIncrease) + 1);
            for (int i = indexToIncrease + 1; i < combinationSize; i++) {
                combination.set(i, combination.get(i - 1) + 1);
            }
        }
       return combination;
    }
}

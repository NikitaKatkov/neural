package main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    static List<Integer> parseIntegers(String layers) {
        List<String> items = Arrays.asList(layers.split("\\s*~\\s*"));
        List<Integer> integerList = new ArrayList<>();
        for (String item : items) {
            integerList.add(Integer.parseInt(item));
        }
        return integerList;
    }


    static List<Double> parseDoubles(String layers) {
        List<String> items = Arrays.asList(layers.split("\\s*~\\s*"));
        List<Double> doubleList = new ArrayList<>();
        for (String item : items) {
            doubleList.add(Double.parseDouble(item));
        }
        return doubleList;
    }

    // формирование пути к файлу с очередным файлом
    static String prepareFilePath(String folderPath, String fileName, int fileIndex) {
        return folderPath + String.format(fileName, fileIndex);
    }

    static void corruptPattern(int corruptionLevel, String fileName, String incorrectPatternsFolder,
                               char _specialSymbol, char _emptySymbol, int fileIndex, int patternHeight, int patternWidth, int[][] pattern) {
        // вычисление допустимого числа измененных битов
        int errorLimit =  (int)(patternHeight * patternWidth * corruptionLevel / 100);
        int errorCounter = 0;
        // генератор случайных позиций для внесения ошибок
        Random random = new Random(System.currentTimeMillis());
        char specialSymbol, emptySymbol;
        try (FileWriter writer = new FileWriter(prepareFilePath(incorrectPatternsFolder, fileName, fileIndex))) {
            for (int lineIndex = 0; lineIndex < patternHeight; lineIndex++) {
                for (int charIndex = 0; charIndex < patternWidth; charIndex++) {
                    if (errorCounter < errorLimit && random.nextInt(100) < corruptionLevel) {
                        /* эмпирически подобранное число, чтобы портилась не только верхняя часть паттерна, но и
                         добавлялось не меньше ошибок, чем нужно.
                         Если ошибку еще можно добавить, меняем символы */
                        specialSymbol = _emptySymbol;
                        emptySymbol = _specialSymbol;
                        errorCounter++;
                    } else {
                        specialSymbol = _specialSymbol;
                        emptySymbol = _emptySymbol;
                    }
                    writer.append(pattern[lineIndex][charIndex] == 1 ? specialSymbol : emptySymbol);
                }
                writer.append("\r\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при открытии файла: corruptPattern");
        }

    }

    static HashMap<Integer, Entry> getDataFromCSV (String filePath) {
        // признаки в csv файле между первым и последним разделителями !

        // разделитель csv
        String delimiter = ";";

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();
            throw new RuntimeException("Ошибка при открытии файла " +  filePath);
        }
        HashMap<Integer, Entry> dataSet = new HashMap<>();
        for (int index = 0; index < lines.size(); index++) {
            // в нулевой строке заголовок, он не нужен
            String line = lines.get(index);
            int[] borders = {0, line.indexOf(delimiter), line.length()};
            int id = Integer.parseInt(line.substring(borders[0], borders[1]));
            List<String> items = Arrays.asList(line.substring(borders[1] + 1, borders[2]).split(delimiter));
            List<Double> value = new ArrayList<>();
            for (String item: items) {
                value.add(Double.parseDouble(item));
            }

            dataSet.put(index, new Entry(id, value));
        }
        return dataSet;
    }
}

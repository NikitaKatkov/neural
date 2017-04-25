package main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Lab4 extends LabCommonClass {
    // значащий символ в паттернах
    private final char _specialSymbol = '1';
    // путь к паттернам - можно добавить ввод через консоль, но т.к. проект не выйдет за пределы моего гитхаба, делать не буду
    private final String _correctPattensFolderPath = "C:\\Programmes_Fastboot\\Java\\neural\\src\\resources\\lab4patterns\\";
    private final String _incorrectPatternsFolderPath = "resources\\lab4incorrectPatterns\\";
    private final String _fileName = "pattern%d.txt";

    //список паттернов
    private List<int[][]> _patterns;

    // конструктор
    Lab4(double norm, String activationFunction, int numberOfPatterns) {
        super(norm, activationFunction);
        if (numberOfPatterns < 1) {
            throw new RuntimeException("Число образцов должно быть не меньше одного!");
        }

        _patterns = new ArrayList<>();
        for (int fileIndex = 1; fileIndex <= numberOfPatterns; fileIndex++) {
            _patterns.add(getPattern(prepareFilePath(_correctPattensFolderPath, _fileName, fileIndex)));
        }
    }

    // формирование пути к файлу с очередным паттерном
    private String prepareFilePath(String folderPath, String fileName, int fileIndex) {
        return folderPath + String.format(fileName, fileIndex);
    }

    // инициализация сети
    private int[][] getPattern(String path) {
        List<String> lines;
        try {// чтение одного образца
            lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка при открытии файла с исходными данными: " + path); // обработка исключения "как Влад учил" :)
        }
        int patternHeight = lines.size();
        int patternWidth = lines.get(0).length();
        int[][] pattern = new int[patternHeight][patternWidth];

        // запись в массив
        int previousLength = patternWidth; // для проверки, является ли введенная матрица прямоугольной
        int lineIndex = 0, rowIndex = 0;
        for (String line : lines) {
            if (previousLength != line.length()) throw new RuntimeException("Введена непрямоугольная матрица! Файл: " + path);
            previousLength = line.length();

           for (char ch : line.toCharArray()) {
               pattern[lineIndex][rowIndex++] = ch == _specialSymbol ? 1 : -1;
           }
           rowIndex = 0;
           lineIndex++;
        }
        return pattern;
    }


    @Override
    boolean start() {
        return false;
    }

    @Override
    boolean trainNet() {
        return false;
    }
}

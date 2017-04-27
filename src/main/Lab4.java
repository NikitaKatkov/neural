package main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Lab4 extends LabCommonClass {
    // значащий символ в паттернах
    private final char _specialSymbol = '1', _emptySymbol = ' ';

    // путь к паттернам - можно добавить ввод через консоль, но т.к. проект не выйдет за пределы моего гитхаба, делать не буду
    private final String _correctPattensFolderPath = "C:\\Programmes_Fastboot\\Java\\neural\\src\\resources\\lab4patterns\\";
    private final String _incorrectPatternsFolderPath = "resources\\lab4incorrectPatterns\\";
    private final String _fileName = "pattern%d.txt";

    // параметры задачи
    private int _patternHeight, _patternWidth, _vectorLength;
    private Scanner _scanner;

    //список паттернов
    private List<int[][]> _originalPatterns;
    private List<int[]> _vectorizedPatterns;

    // контейнер нейронов
    private List<Neuron> _network;

    // контейнер весов
    private double[][] _weights;

    // конструктор
    Lab4(double norm, String activationFunction, int numberOfPatterns, Scanner scanner) {
        super(norm, activationFunction);

        if (numberOfPatterns < 1) {
            throw new RuntimeException("Число образцов должно быть не меньше одного!");
        }

        _scanner = scanner;
        _originalPatterns = new ArrayList<>();
        _vectorizedPatterns = new ArrayList<>();
        for (int fileIndex = 0; fileIndex < numberOfPatterns; fileIndex++) {
            _originalPatterns.add(getPattern(prepareFilePath(_correctPattensFolderPath, _fileName, fileIndex)));
            _vectorizedPatterns.add(vectorize(_originalPatterns.get(fileIndex)));
        }

        // параметры паттернов можно взять из любого примера, т.к. дальше проводится проверка соответствия
        // одинаковая длина строк гарантирована еще на этапе чтения из файла
        _patternHeight = _originalPatterns.get(0).length;
        _patternWidth = _originalPatterns.get(0)[0].length;
        _vectorLength = _patternHeight * _patternWidth;

        // проверка размеров паттернов
        for (int[][] pattern : _originalPatterns) {
            if (pattern.length != _patternHeight || pattern[0].length != _patternWidth) {
                printPattern(pattern, null);
                throw new RuntimeException("Размеры паттернов не совпадают!");
            }
        }

        // инициализация контейнера весов
        _weights = new double[_vectorLength][_vectorLength];
        for (int i = 0; i < _patternHeight; i++) {
            for (int j = 0; j < _patternWidth; j++) {
                if (i == j) continue; // нулевые веса на диагонали матрицы
                for (int patternIndex = 0; patternIndex < numberOfPatterns; patternIndex++) {
                    _weights[i][j] += _vectorizedPatterns.get(patternIndex)[i] * _vectorizedPatterns.get(patternIndex)[j];
                }
            }
        }

        // инициализация контейнера нейронов
        _network = new ArrayList<>();
        for (int index = 0; index < _vectorLength; index++) {
            _network.add(index, new Neuron(_activationFunction, 0, "simple"));
            //_network.get(index).setNet(_vectorizedPatterns.get(0)[index]); // какими значениями инициалиировать входы нейронов???
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

    // векторизация паттернов
    private int[] vectorize(int[][] pattern) { // в методе веторизация по столбцам!!
        int numberOfLines = pattern.length, numberOfRows = pattern[0].length, vectorIndex = 0;
        int[] vector = new int[numberOfLines * numberOfRows];
        for (int lineIndex = 0; lineIndex < numberOfLines; lineIndex++) {
            for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
                vector[vectorIndex] = pattern[lineIndex][rowIndex];
            }
        }
        return vector;
    }

    // девекторизация (есть такое слово вообще?) паттернов
    private int[][] devectorize(int[] vectorizedPattern) {
        int[][] pattern = new int[_patternHeight][_patternWidth];
        int vectorIndex = 0;
        for (int i = 0; i < _patternHeight; i++) {
            for (int j = 0; j < _patternWidth; j++) {
                pattern[i][j] = vectorizedPattern[vectorIndex++];
            }
        }
        return pattern;
    }

    // печать символа в консоль
    private void printPattern(int[][] patternInput, int[][] patternOutput) {
        char ch;
        for (int line = 0; line < _patternHeight; line++) {
            for (int pixel : patternInput[line]) {
                ch = pixel == 1 ? _specialSymbol : _emptySymbol;
                System.out.print(ch);
            }
            if (line == _patternHeight/2 && patternOutput != null) System.out.print("   ->   ");
            else System.out.print("        ");
            for (int pixel : patternOutput[line]) {
                ch = pixel == 1 ? _specialSymbol : _emptySymbol;
                System.out.print(ch);
            }
        }
    }

    @Override
    boolean start() {
        int input;
        do {
            System.out.println("Подать на вход правильные (1) или искаженные (2) паттерны? (0 для выхода): ");
            input = _scanner.nextInt();
            switch (input) {
                case 1:

                    break;
                case 2:

                    break;
                default:
                    System.out.println("Завершение работы");
                    return true;
            }
        } while (true);
    }

    // обработка одного паттерна
    private int[]  processPattern(int[] vectorizedPattern) {
        // инициализация входов сети
        int vectorIndex = 0;
        for (Neuron currentNeuron : _network) {
            currentNeuron.setNet(vectorizedPattern[vectorIndex]);
            currentNeuron.activationFunction(); // надо ли сначала вычислять выход??
        }
        boolean wasChanged;
        double previousOut;
        Neuron currentNeuron;
        int epochCounter = 0;
        do {
            wasChanged = false;
            for (int neuronIndex = 0; neuronIndex < _vectorLength; neuronIndex++) {
                currentNeuron = _network.get(neuronIndex);
                previousOut = currentNeuron.getOut();
                // вычисление нового сетевого входа
                netEvaluate(currentNeuron, neuronIndex);
                // вычисление нового выхода
                currentNeuron.activationFunction();
                if (previousOut != currentNeuron.getOut()) wasChanged = true;
                epochCounter++;
            }
        } while (wasChanged && epochCounter < _epochLimit);

        int[] vectorizedOutputPattern = new int[_vectorLength];
        // получение выходных значений, когда сеть боьше их не меняет
        for (int index = 0; index < _vectorLength; index++) {
            vectorizedOutputPattern[index] = (int)_network.get(index).getOut();
        }
        return vectorizedOutputPattern;
    }

    @Override
    boolean trainNet() {
        return false;
    }

    private void netEvaluate(Neuron currentNeuron, int neuronIndex) {
        double net = 0;
        for (int index = 0; index < _vectorLength; index++) {
            net += _weights[index][neuronIndex] * _network.get(index).getOut();
        }
        currentNeuron.setNet(net);
    }
}

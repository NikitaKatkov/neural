package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Lab1 implements LabCommonInterface {
    //ПАРАМЕТРЫ ЗАДАЧИ
    private int _numberOfVariables; //не включая фиктивную переменную x_0 = 1
    private int _numberOfSets;
    private double _norm; //норма обучения
    private String _activationFunction;
    private final int _epochLimit = 100;
    private final String _linearAF = "linear", _nonlinearAF = "nonlinear", _defaultFunction = "0101011101110111";


    //ПОЛЯ ДЛЯ ВЫЧИСЛЕНИЙ
    private int[][] _variables;
    private int[] _function, _y, _delta;
    private double[] _weight, _net, _out;
    private int _errorCounter;
    private List<Integer> _combinationSet;
    private boolean _enableSelection;

    //ВСПОМОГАТЕЛЬНЫЕ КОНСТРУКЦИИ
    private Scanner _consoleReader = new Scanner(System.in);

    //МЕТОДЫ
    //конструктор
    Lab1(int numberOfVariables, double norm, String activationFunction, boolean enableSelection) {
        numberOfVariables++;
        _numberOfVariables = numberOfVariables; //учиываем x_0
        this._enableSelection = enableSelection;
        _numberOfSets = (int) Math.pow(2, _numberOfVariables - 1);
        _norm = norm;
        if (!activationFunction.equals(_linearAF) && !activationFunction.equals(_nonlinearAF)) {
            System.out.println("Неверный параметр: функция активации (требуется linear или nonlinear)"); //вынести названия функций в константы
            throw new RuntimeException("Инициализация функции активации не выполнена");
        }
        _activationFunction = activationFunction;
        _variables = new int[_numberOfSets][_numberOfVariables];
        _function = new int[_numberOfSets];
        _weight = new double[_numberOfVariables];
        _net = new double[_numberOfSets];
        _y = new int[_numberOfSets];
        _out = new double[_numberOfSets];
        _delta = new int[_numberOfSets];
        _combinationSet = new ArrayList<>();
        if (!enableSelection) {
            for (int i = 0; i < _numberOfSets; i++) { //_combinationSet содержит полный список наборов, если не стоит флаг их перебора
                _combinationSet.add(i, i);
            }
        }
        initializeVariables();
        if (!initializeFunction()) throw new RuntimeException("Инициализация вектора значений функции не выполнена");
//        errorEvaluate();
    }

    //интерфейсный метод запуска работы
    public boolean start() { //возвращает булево значение в зависимости от корректности обучения
        if (_enableSelection) {
            return trainNetWithSelection();
        } else return trainNet();
    }

    //обучение без перебора наборов
    private boolean trainNet() {
        int epoch = 1;
        do {
             _errorCounter = 0;
            for (int setNumber : _combinationSet) {
                netEvaluate(setNumber);
                outEvaluate(setNumber);
                yEvaluate(setNumber);
                deltaEvaluate(setNumber);
                if (_delta[setNumber] != 0) {
                    weightCorrection(setNumber);
                    _errorCounter++;
                }
            }
            if (!_enableSelection) {
                System.out.println("    ЭПОХА: " + epoch);
                printData();
                System.out.println("Ошибки: " + _errorCounter + "\r\n ");
            } else {
                errorEvaluate();
//                System.out.println("    ЭПОХА: " + epoch);
//                printData();
//                System.out.println("Ошибки: " + _errorCounter + "\r\n
            }
            epoch++;
            if (epoch > _epochLimit) {
                if (!_enableSelection) System.out.println("Количество эпох превышает допустимый предел, остановка вычислений");
                return false;
            }
        } while (!(_errorCounter == 0 && checkNet()));
        return true;
    }

    //обучение с перебором наборов
    private boolean trainNetWithSelection() {
        boolean isTrainingComplete;
        int iteration = 0, iterationLimit = (int) Math.pow(2, _numberOfSets);
        do {
            _combinationSet = Functions.getNextCombination(_combinationSet, _numberOfSets);
            if (_combinationSet.size() == 0) { //пустой список означает конец перебора подмножеств
                System.out.println("Обучение не завершено ни на одном из подмножеств наборов переменных :(");
                return false;
            }
            clearFields();
//            System._out.println("\r\n        Комбинация наборов №" + (iteration+1));
            isTrainingComplete = trainNet();
            iteration++;
        } while (!isTrainingComplete && iteration < iterationLimit);
        System.out.println("Обучение завершено!\r\nКоличество наборов в обучающей выборке: " + _combinationSet.size() +
                "\r\nНомера наборов: " + _combinationSet);
        return isTrainingComplete;
    }

    //инициализация списка наборов переменных в порядке возрастания
    private void initializeVariables() {
        for (int i = 0; i < _numberOfSets; i++) {
            //установка x_0 = 1
            _variables[i][0] = 1;
            //установка остальных значений
            String binarySet = Integer.toBinaryString(i);
            int missingZerosCounter = _numberOfVariables - 1 - binarySet.length();
            StringBuilder zeros = new StringBuilder();
            for (int j = 0; j < missingZerosCounter; j++) {
                zeros.append("0");
            }
            binarySet = zeros.toString() + binarySet; //добавим в начало недостающие нули
            char temp;
            for (int j = 1; j < _numberOfVariables; j++) {
                temp = binarySet.charAt(j - 1);
                _variables[i][j] = Character.getNumericValue(temp);
            }
        }
    }

    //инициализация вектора значений булевой функции
    private boolean initializeFunction() {
        System.out.println("Введите вектор значений функции в одну строку без пробелов (\"default\" для собственного варианта задания): ");
        StringBuilder enteredFunction = new StringBuilder();
        enteredFunction.append(_consoleReader.next());
        if (enteredFunction.toString().equals("default")) {
            enteredFunction.replace(0, enteredFunction.length(), _defaultFunction); //мой вариант задания по умолчанию
        } else if (enteredFunction.length() != _numberOfSets) {
            System.out.println("Введен неверный вектор значений, возможно, присутствуют не числовые символы");
            return false;
        }
        for (int i = 0; i < _numberOfSets; i++) {
            _function[i] = Character.getNumericValue(enteredFunction.charAt(i));
        }
        return true;
    }


    //вычисление вектора ошибок
    private void deltaEvaluate(int setNumber) {
//        for (int i = 0; i < _numberOfSets; i++) {
            _delta[setNumber] = _function[setNumber] - _y[setNumber];
//        }
    }

    //подсчет количества ошибок
    private void errorEvaluate() {
        _errorCounter = 0;
        for (int i = 0; i < _numberOfSets; i++) {
            if (_function[i] != _y[i]) _errorCounter++;
        }
    }

    //первоначальный выход нейросети
    private void netEvaluate(int setNumber) {
        double temp;
//        for (int i = 0; i < _numberOfSets; i++) {
            temp = 0;
            for (int j = 0; j < _numberOfVariables; j++) {
                temp += _weight[j] * _variables[setNumber][j];
            }
            _net[setNumber] = temp;
//        }
    }

    //функция активации
    private void outEvaluate(int setNumber) {
        switch (_activationFunction) {
            case _linearAF:
//                System.arraycopy(_net, 0, _out, 0, _net.length);
                _out[setNumber] = _net[setNumber];
                break;
            case _nonlinearAF:
//                for (int i = 0; i < _numberOfSets; i++) { //здесь можно добавить любую ФА
                    _out[setNumber] = 0.5 * (Math.tanh(_net[setNumber]) + 1);
//                }
        }
    }

    //реальный выход нейросети (двоичный вектор)
    private void yEvaluate(int setNumber) {
        double border = 0;
        switch (_activationFunction) {
            case _linearAF:
                border = -0.00001; //для корректного сравнения с нулем чисел с плавающей точкой
                break;
            case _nonlinearAF:
                border = 0.5;
        }
//        for (int i = 0; i < _numberOfSets; i++) {
            _y[setNumber] = (_net[setNumber] >= border ? 1 : 0);
//        }
    }

    //производная сигмоидальной функции
    private double nonLinearDerivative(int index) {
        return -0.5 * Math.pow(Math.tanh(_net[index]), 2) + 0.5;
    }

    //коррекция весов
    private void weightCorrection(int setNumber) { // коррекция весов по вектору переменных с номером setNumber
        double derivative = 1;
        for (int i = 0; i < _numberOfVariables; i++) {
                if (_activationFunction.equals(_nonlinearAF))
                    derivative = nonLinearDerivative(i); //если производная не единица, домножим на нее
                _weight[i] += _norm * _delta[setNumber] * _variables[setNumber][i] * derivative;
        }
    }

    //проверка сети на работоспособность
    private boolean checkNet() {
        for (int i = 0; i < _numberOfSets; i++) {
            netEvaluate(i);
            outEvaluate(i);
            yEvaluate(i);
            deltaEvaluate(i);
            if (_delta[i] != 0) {
                return false;
            }
        }
        return true;
    }


    //геттеры, сеттеры, принтеры
    //печать лога в консоль
    private void printData() {
        System.out.print("Веса: ");
        for (int i = 0; i < _numberOfVariables; i++) System.out.format("%.2f ", _weight[i]);
        System.out.println();
//        System._out.println("NET: ");
//        for (int i = 0; i < _numberOfSets; i++) System._out.format("%.2f ", _net[i]);
//        System._out.println();
//        System._out.println("OUT: ");
//        for (int i = 0; i < _numberOfSets; i++) System._out.format("%.2f ", _out[i]);
//        System._out.println();
        System.out.println("Значения полученной функции: ");
        for (int i = 0; i < _numberOfSets; i++) System.out.format("%d ", _y[i]);
        System.out.println();
    }

    //очистка полей для повторного обучения
    private void clearFields() {
        for (int i = 0; i < _numberOfVariables; i++) {
            _weight[i] = 0;
        }
        for (int i = 0; i < _numberOfSets; i++) {
            _out[i] = 0;
            _net[i] = 0;
            _y[i] = 0;
        }
        _errorCounter = 0;
        System.arraycopy(_function, 0, _delta, 0, _function.length);
    }
}
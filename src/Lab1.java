import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Lab1 implements LabCommonInterface {
    //КОНСТАНТЫ
    private int NUMBER_OF_VARIABLES; //не включая фиктивную переменную x_0 = 1
    private int NUMBER_OF_SETS;
    private double N; //норма обучения
    private String ACTIVATION_FUNCTION;
    private int EPOCH_LIMIT = 100;
    private final String LINEAR_AF = "linear", NONLINEAR_AF = "nonlinear";


    //ПОЛЯ ДЛЯ ВЫЧИСЛЕНИЙ
    private int[][] variables;
    private int[] function, y, delta;
    private double[] weight, net, out;
    private int errorCounter;
    private List<Integer> combinationSet;
    private boolean enableSelection;

    //ВСПОМОГАТЕЛЬНЫЕ КОНСТРУКЦИИ
    private Scanner consoleReader = new Scanner(System.in);

    //МЕТОДЫ
    //конструктор
    Lab1 (int numberOfVariables, double norm, String activationFunc, boolean enableSelection) {
        numberOfVariables++;
        NUMBER_OF_VARIABLES = numberOfVariables; //учиываем x_0
        this.enableSelection = enableSelection;
        NUMBER_OF_SETS = (int)Math.pow(2, NUMBER_OF_VARIABLES-1);
        N = norm;
        if (!activationFunc.equals(LINEAR_AF) && !activationFunc.equals(NONLINEAR_AF)) {
            System.out.println("Неверный параметр: функция активации (требуется linear или nonlinear)"); //вынести названия функций в константы
            throw new RuntimeException("Инициализация функции активации не выполнена");        }
        ACTIVATION_FUNCTION = activationFunc;
        variables = new int[NUMBER_OF_SETS][NUMBER_OF_VARIABLES];
        function = new int[NUMBER_OF_SETS];
        weight = new double[NUMBER_OF_VARIABLES];
        net = new double[NUMBER_OF_SETS];
        y = new int[NUMBER_OF_SETS];
        out = new double[NUMBER_OF_SETS];
        delta = new int[NUMBER_OF_SETS];
        combinationSet = new ArrayList<>();
        if (!enableSelection) {
            for (int i = 0; i < NUMBER_OF_SETS; i++) { //combinationSet содержит полный список наборов, если не стоит флаг их перебора
                combinationSet.add(i, i);
            }
        }
        System.arraycopy(function, 0, delta, 0, function.length); //первоначальные значения ошибок
        initializeVariables();
        if (!initializeFunction()) throw new RuntimeException("Инициализация вектора значений функции не выполнена");
        errorEvaluate();
    }

    //интерфейсный метод запуска работы
    public boolean start() { //возвращает булево значение в зависимости от корректности обучения
        if (enableSelection) {
            return trainNetWithSelection();
        } else return trainNet();
    }

    //обучение без перебора наборов
    private boolean trainNet () {
        int epoch = 1;
        do {
            deltaEvaluate();
            weightCorrection();
            netEvaluate();
            outEvaluate();
            yEvaluate();
            errorEvaluate();
            if (!enableSelection) {
                System.out.println("    ЭПОХА: " + epoch);
                printData();
                System.out.println("Ошибки: " + errorCounter + "\r\n");
            }
            epoch++;
            if (epoch > EPOCH_LIMIT) {
                if (!enableSelection) System.out.println("Количество эпох превышает допустимый предел, остановка вычислений");
                return false;
            }
        } while (errorCounter > 0);
        return true;
    }

    //обучение с перебором наборов
    private boolean trainNetWithSelection () {
        boolean isTrainingComplete;
        int iteration = 0, iterationLimit = (int)Math.pow(2, NUMBER_OF_SETS);
        do {
            combinationSet = Functions.getNextCombination(combinationSet, NUMBER_OF_SETS);
            clearFields();
//            System.out.println("\r\n        Комбинация наборов №" + (iteration+1));
            isTrainingComplete = trainNet();
            iteration++;
        } while (!isTrainingComplete && iteration < iterationLimit);
        System.out.println("Обучение завершено: " + isTrainingComplete +
        "\r\nКоличество наборов в обучающей выборке: " + combinationSet.size() +
        "\r\nНомера наборов: " + combinationSet);
        return isTrainingComplete;
    }

    //инициализация списка наборов переменных в порядке возрастания
    private void initializeVariables () {
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            //установка x_0 = 1
            variables[i][0] = 1;
            //установка остальных значений
            String binarySet = Integer.toBinaryString(i);
            int missingZerosCounter = NUMBER_OF_VARIABLES - 1 - binarySet.length();
            StringBuilder zeros = new StringBuilder();
            for (int j = 0; j < missingZerosCounter; j++) {
                zeros.append("0");
            }
            binarySet = zeros.toString() + binarySet; //добавим в начало недостающие нули
            char temp;
            for (int j = 1; j < NUMBER_OF_VARIABLES; j++) {
                temp = binarySet.charAt(j-1);
                variables[i][j] = Character.getNumericValue(temp);
            }
        }
    }

    //инициализация вектора значений булевой функции
    private boolean initializeFunction () {
        System.out.println("Введите вектор значений функции в одну строку без пробелов (\"default\" для собственного варианта задания): ");
        StringBuilder enteredFunction = new StringBuilder();
        enteredFunction.append(consoleReader.next());
        if (enteredFunction.toString().equals("default")) {
            enteredFunction.replace(0, enteredFunction.length(), "0101011101110111"); //мой вариант задания по умолчанию
        } else if (enteredFunction.length() != NUMBER_OF_SETS) {
            System.out.println("Введен неверный вектор значений, возможно, присутствуют не числовые символы");
            return false;
        }
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            function[i] = Character.getNumericValue(enteredFunction.charAt(i));
        }
        return true;
    }


    //вычисление вектора ошибок
    private void deltaEvaluate () {
        for (int i =0; i < NUMBER_OF_SETS; i++) {
            delta[i] = function[i] - y[i];
        }
    }

    //подсчет количества ошибок
    private void errorEvaluate () {
        errorCounter = 0;
        for (int i =0; i < NUMBER_OF_SETS; i++) {
            if (function[i] != y[i]) errorCounter++;
        }
    }

    //первоначальный выход нейросети
    private void netEvaluate() {
        double temp;
        for (int i =0; i < NUMBER_OF_SETS; i++) {
            temp = 0;
            for (int j = 0; j < NUMBER_OF_VARIABLES; j++) {
                temp += weight[j]*variables[i][j];
            }
            net[i] = temp;
        }
    }

    //функция активации
    private void outEvaluate() {
        switch (ACTIVATION_FUNCTION) {
            case LINEAR_AF:
                System.arraycopy(net, 0, out,0, net.length);
                break;
            case NONLINEAR_AF:
                for (int i =0; i < NUMBER_OF_SETS; i++) { //здесь можно добавить любую ФА
                    out[i] = 0.5 * (Math.tanh(net[i]) + 1);
                }
        }
    }

    //реальный выход нейросети (двоичный вектор)
    private void yEvaluate() {
        double border = 0;
        switch (ACTIVATION_FUNCTION) {
            case LINEAR_AF:
                border = -1e-5; //для корректного сравнения с нулем чисел с плавающей точкой
                break;
            case NONLINEAR_AF:
                border = 0.5;
        }
        for (int i =0; i < NUMBER_OF_SETS; i++) {
            y[i] = (net[i] >= border ? 1 : 0);
        }
    }

    //производная сигмоидальной функции
    private double nonLinearDerivative(int index) {
        return -0.5*Math.pow(Math.tanh(net[index]), 2) + 0.5;
    }

    //коррекция весов
    private void weightCorrection () {
        double derivative = 1;
        for (int i = 0; i < NUMBER_OF_VARIABLES; i++) {
            for (int j : combinationSet) {
                if (ACTIVATION_FUNCTION.equals(NONLINEAR_AF)) derivative = nonLinearDerivative(i); //если производная не единица, домножим на нее
                weight[i] += N*delta[j]*variables[j][i]*derivative;
            }
        }
    }


    //геттеры, сеттеры, принтеры
    //печать лога в консоль
    private void printData() {
        System.out.print("Веса: ");
        for (int i = 0; i < NUMBER_OF_VARIABLES; i++) System.out.format("%.2f ", weight[i]);
        System.out.println();
//        System.out.println("NET: ");
//        for (int i = 0; i < NUMBER_OF_SETS; i++) System.out.format("%.2f ", net[i]);
//        System.out.println();
//        System.out.println("OUT: ");
//        for (int i = 0; i < NUMBER_OF_SETS; i++) System.out.format("%.2f ", out[i]);
//        System.out.println();
        System.out.println("Значения полученной функции: ");
        for (int i = 0; i < NUMBER_OF_SETS; i++) System.out.format("%d ", y[i]);
        System.out.println();
    }

    //очистка полей для повторного обучения
    private void clearFields() {
        for (int i = 0; i < NUMBER_OF_VARIABLES; i++) {
            weight[i] = 0;
        }
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            out[i] = 0;
            net[i] = 0;
            y[i] = 0;
        }
        errorCounter = 0;
        System.arraycopy(function, 0, delta, 0, function.length);
    }
}
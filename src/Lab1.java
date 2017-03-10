import java.util.Scanner;

class Lab1 {
    //КОНСТАНТЫ
    private int NUMBER_OF_VARIABLES; //включая фиктивную переменную x_0 = 1
    private int NUMBER_OF_SETS;
    private double N; //норма обучения
    private String ACTIVATION_FUNCTION;

    //ПОЛЯ ДЛЯ ВЫЧИСЛЕНИЙ
    private int[][] variables;
    private int[] function, y, out, delta;
    private double[] weight, net;
    private int errorCounter;

    //ВСПОМОГАТЕЛЬНЫЕ КОНСТРУКЦИИ
    private Scanner consoleReader = new Scanner(System.in);

    //МЕТОДЫ
    Lab1 (int numberOfVariables, double norm, String activationFunc) {
        NUMBER_OF_VARIABLES = numberOfVariables;
        NUMBER_OF_SETS = (int)Math.pow(2, NUMBER_OF_VARIABLES-1);
        N = norm;
        if (!activationFunc.equals("linear") && !activationFunc.equals("sigmoid")) {
            System.out.println("Неверный параметр: функция активации (требуется linear или sigmoid)");
            throw new RuntimeException("Инициализация функции активации не выполнена");        }
        ACTIVATION_FUNCTION = activationFunc;
        variables = new int[NUMBER_OF_SETS][NUMBER_OF_VARIABLES];
        function = new int[NUMBER_OF_SETS];
        weight = new double[NUMBER_OF_VARIABLES];
        net = new double[NUMBER_OF_SETS];
        y = new int[NUMBER_OF_SETS];
        out = new int[NUMBER_OF_SETS];
        delta = new int[NUMBER_OF_SETS];
        System.arraycopy(function, 0, delta, 0, function.length); //первоначальные значения ошибок
        initializeVariables();
        if (!initializeFunction()) throw new RuntimeException("Инициализация вектора значений функции не выполнена");
        errorEvaluate();
    }

    void start() {
        int epoch = 1;
        do {
            System.out.println("    ЭПОХА: " + epoch++);
            deltaEvaluate();
            weightCorrection();
            netEvaluate();
            switch (ACTIVATION_FUNCTION) {
                case "linear":
                    outEvaluateLinear();
                    yEvaluateLinear();
                    break;
                case "sigmoid":

            }
            printData();
            errorEvaluate();
            System.out.println("Ошибки: " + errorCounter + "\r\n");

            if (epoch > 100) {
                System.out.println("Количество эпох превышает допустимый предел, остановка вычислений");
                break;
            }
        } while (errorCounter > 0);
    }

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

    private boolean initializeFunction () {
        System.out.println("Введите вектор значений функции в одну строку без пробелов: ");
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


    private void deltaEvaluate () {
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            delta[i] = function[i] - y[i];
//            if (delta[i] != 0) errorCounter++;
        }
    }

    private void errorEvaluate () {
        errorCounter = 0;
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            if (function[i] != y[i]) errorCounter++;
        }
    }

    //линейная функция активации
    private void netEvaluate() {
        double temp;
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            temp = 0;
            for (int j = 0; j < NUMBER_OF_VARIABLES; j++) {
                temp += weight[j]*variables[i][j];
            }
            net[i] = temp;
        }
    }

    private void outEvaluateLinear () {
        double eps = -1e-5;
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            //работа линейной функции активации нейрона
//            out[i] = (net[i] > 0.0 ? 1 : 0);
            out[i] = net[i] > eps ? 1 : 0; //более корректное сравнение чисел с плавающей точкой
        }
    }

    private void yEvaluateLinear () {
        System.arraycopy(out, 0, y,0,out.length);
    }
    //

    //нелинейная функция активации

    //

    private void weightCorrection () {
        for (int i = 0; i < NUMBER_OF_VARIABLES; i++) {
            for (int j = 0; j < NUMBER_OF_SETS; j++) {
                weight[i] += N*delta[j]*variables[j][i]; //добавить умножение на лямбду-нелинейную ФА
            }
        }
    }

    //геттеры, сеттеры, принтеры
    private void printData() {
        System.out.print("Веса: ");
        for (int i = 0; i < NUMBER_OF_VARIABLES; i++) System.out.format("%.2f ", weight[i]);
        System.out.println();

        System.out.println("NET: ");
        for (int i = 0; i < NUMBER_OF_SETS; i++) System.out.format("%.2f ", net[i]);
        System.out.println();

        System.out.println("OUT: ");
        for (int i = 0; i < NUMBER_OF_SETS; i++) System.out.format("%d ", out[i]);
        System.out.println();

        System.out.println("Y: ");
        for (int i = 0; i < NUMBER_OF_SETS; i++) System.out.format("%d ", y[i]);
        System.out.println();
    }

}

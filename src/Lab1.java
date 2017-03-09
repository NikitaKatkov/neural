import java.util.Scanner;

class Lab1 {
    //КОНСТАНТЫ
    private int NUMBER_OF_VARIABLES; //включая фиктивную переменную x_0 = 1
    private int NUMBER_OF_SETS;
    private double N; //норма обучения
    private String ACTTIVATION_FUNCTION;

    //ПОЛЯ ДЛЯ ВЫЧИСЛЕНИЙ
    private int[][] variables;
    private int[] function, weight, net, y, out, delta;
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
        ACTTIVATION_FUNCTION = activationFunc;
        variables = new int[NUMBER_OF_SETS][NUMBER_OF_VARIABLES];
        function = new int[NUMBER_OF_SETS];
        weight = new int[NUMBER_OF_VARIABLES];
        net = new int[NUMBER_OF_SETS];
        y = new int[NUMBER_OF_SETS];
        out = new int[NUMBER_OF_SETS];
        delta = new int[NUMBER_OF_SETS];
        errorCounter = 0;
        initializeVariables();
        if (!initializeFunction()) throw new RuntimeException("Инициализация вектора значений функции не выполнена");
    }

    void start() {
        do {
            errorCounter = 0;
            netEvaluate();
            outEvaluateLinear();
            yEvaluateLinear();
            deltaEvaluate();
            System.out.println("Ошибки: " + errorCounter);
            if(errorCounter > 0) weightCorrection();
            printWeight();
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
        if (enteredFunction.length() != NUMBER_OF_SETS) {
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
            if (delta[i] != 0) errorCounter++;
        }
    }


    //линейная функция активации
    private void netEvaluate() {
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            for (int j = 0; j < NUMBER_OF_VARIABLES; j++) {
                net[i] = weight[j]*variables[i][j];
            }
        }
    }

    private void outEvaluateLinear () {
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            //работа линейной функции активации нейрона
            out[i] = net[i] >= 0 ? 1 : 0;
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
    private void printWeight() {
        System.out.print("Веса: ");
        for (int i = 0; i < NUMBER_OF_VARIABLES; i++) {
            System.out.print(weight[i] + " ");
        }
        System.out.println();
    }
}

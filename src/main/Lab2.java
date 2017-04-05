package main;

class Lab2 extends LabCommonClass {
    //ПАРАМЕТРЫ ЗАДАЧИ
    private double[] _function; //данные значения функции (основа обучения)
    private final int _numberOfPoints = 20; //число точек в интервале
    private int _intervalSize; //размер окна
    private double[] _predictedValues; //для записи вычислений обученной сети

    //конструктор
    Lab2(double norm, String activationFunction, int intervalSize, double beginOfInterval, double endOfInterval) {
        super(norm, activationFunction);
        _intervalSize = intervalSize;
        _weight = new double[_intervalSize];
        _function = new double[_numberOfPoints];
        _net = new double[_numberOfPoints];
        _delta = new double[_numberOfPoints];
        initializeFunction(beginOfInterval, endOfInterval); //вычисление первых 20 значений
        System.arraycopy(_function, 0, _net, 0, _intervalSize); //копирование первых значений функции для начала обучения
        _border = 0.05;
    }

    //инициализация значений функции
    private void initializeFunction(double beginOfInterval, double endOfInterval) {
        double step = (endOfInterval - beginOfInterval)/_numberOfPoints;
        for (double t = beginOfInterval, index = 0; index < _numberOfPoints; t += step, index++) {
            _function[(int)index] = Math.exp(-0.1*Math.pow(t,2)); //функция по варианту из методички
        }
    }

    @Override
    boolean start() {
        if (trainNet()) {
            _predictedValues = new double[_numberOfPoints + _intervalSize];
            System.arraycopy(_function, 0, _predictedValues, 0, _intervalSize);
            for (int index = _intervalSize; index < _numberOfPoints + _intervalSize; index++) {
                for (int weightIndex = 0, k = index; weightIndex < _intervalSize && k < index + _intervalSize;weightIndex++, k++) {
                    _predictedValues[index] += _weight[weightIndex] * _predictedValues[k - _intervalSize];
                }
            }
        }
        System.out.println("Подобранные веса: ");
        for (int i = 0; i < _intervalSize; i++) {
            System.out.format("%.3f ", _weight[i]);
        }
        System.out.println("\r\nВычисленные значения функции: ");
        for (int i = _intervalSize; i < _numberOfPoints + _intervalSize; i++) {
            System.out.format("%.2f ", _predictedValues[i]);
        }
        return true;
    }

    @Override
    void netEvaluate(int index) {
        double temp = 0;
        for (int weightIndex = 0, k = index; weightIndex < _intervalSize && k < index + _intervalSize;weightIndex++, k++) {
             temp += _weight[weightIndex] * _function[k - _intervalSize];
        }
        _net[index] = temp;
    }

    @Override
    void deltaEvaluate(int index) {
        _delta[index] = _function[index] - _net[index];
    }

    //out метод в родительском классе
    //y метод в родительском классе

    @Override
    void weightCorrection(int indexToCorrect) {
        for (int weightIndex = 0; weightIndex < _intervalSize; weightIndex++) {
            _weight[weightIndex] += _norm * _delta[indexToCorrect] * _function[indexToCorrect + weightIndex - _intervalSize];
        }
    }

    @Override
    protected boolean trainNet() {
        int epoch = 1;
        double epsilon = 0;
        do { //эпоха
            System.out.format("Эпоха: " + epoch + " -- Ошибка: %.3f\r\n", epsilon);
            for (int index = _intervalSize; index < _numberOfPoints; index++) {
                //шаг -- предоставление сети _intervalSize переменных из _function для вычисления одного значения _net
                netEvaluate(index); //вычисление одного значения вектора прогноза
                deltaEvaluate(index); //вычисление ошибки
                weightCorrection(index);
            }
            epsilon = 0;
            for (int i = _intervalSize; i < _numberOfPoints; i++) {
                epsilon += Math.pow(_delta[i], 2);
            }
            epsilon = Math.sqrt(epsilon);
            epoch++;
        } while (epsilon > _border && epoch < _epochLimit);
        return true;
    }
}

package main;

abstract class LabCommonClass { //для удобства запуска всех лабораторных

    //ОБЩИЕ ДЛЯ ВСЕХ ЗАДАЧ ПОЛЯ
    double _norm; //норма обучения
    String _activationFunction;
    final int _epochLimit = 100000;
    static final String _linearAF = "linear", _sigmoidAF = "sigmoid", _stepAF = "step", _bipolarAF = "bipolar";
    double[] _function;
    double[]  _net, _out, _y, _delta;
    int _errorCounter;
    double _border;


    //ОБЩИЕ МЕТОДЫ
    LabCommonClass(double norm, String activationFunction) {
        _norm = norm;
        if (!activationFunction.equals(_linearAF) && !activationFunction.equals(_sigmoidAF) && !activationFunction.equals(_stepAF)) {
            System.out.println("Неверный параметр: функция активации (требуется linear, step или sigmoid)"); //вынести названия функций в константы
            throw new RuntimeException("Инициализация функции активации не выполнена");
        }
        _activationFunction = activationFunction;
    }
    //запуск работы
    abstract boolean start();

    abstract boolean trainNet();

    //реальный выход нейросети (двоичный вектор)
    void yEvaluate(int firstIndex, int secondIndex) {
        _border = 0;
        switch (_activationFunction) {
            case _linearAF:
                _border = -0.00001; //для корректного сравнения с нулем чисел с плавающей точкой
                break;
            case _sigmoidAF:
                _border = 0.5;
        }
        _y[secondIndex] = (_net[secondIndex] >= _border ? 1 : 0);
    }
}

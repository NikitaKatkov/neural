package main;

abstract class LabCommonClass { //для удобства запуска всех лабораторных

    //ОБЩИЕ ДЛЯ ВСЕХ ЗАДАЧ ПОЛЯ
    double _norm; //норма обучения
    String _activationFunction;
    final int _epochLimit = 100000;
    final String _linearAF = "linear", _sigmoidAF = "sigmoid", _stepAF = "step";
    double[] _function, _y, _delta;
    double[] _weight, _net, _out;
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

    //коррекция весов
    abstract void weightCorrection(int index); // коррекция весов по обучающему набору с номером index

    abstract boolean trainNet();

    abstract void netEvaluate(int index);

    //вычисление вектора ошибок
    void deltaEvaluate(int setNumber) {
        _delta[setNumber] = _function[setNumber] - _y[setNumber];
    }

    //функция активации
    void outEvaluate(int setNumber) {
        switch (_activationFunction) {
            case _linearAF:
                _out[setNumber] = _net[setNumber];
                break;
            case _sigmoidAF:
                _out[setNumber] = 0.5 * (Math.tanh(_net[setNumber]) + 1);
        }
    }

    //реальный выход нейросети (двоичный вектор)
    void yEvaluate(int setNumber) {
        _border = 0;
        switch (_activationFunction) {
            case _linearAF:
                _border = -0.00001; //для корректного сравнения с нулем чисел с плавающей точкой
                break;
            case _sigmoidAF:
                _border = 0.5;
        }
        _y[setNumber] = (_net[setNumber] >= _border ? 1 : 0);
    }
}

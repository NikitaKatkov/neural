package main;

abstract class LabCommonClass { //для удобства запуска всех лабораторных

    //ОБЩИЕ ДЛЯ ВСЕХ ЗАДАЧ ПОЛЯ
    double _norm; //норма обучения
    String _activationFunction;
    final int _epochLimit = 100;
    final String _linearAF = "linear", _nonlinearAF = "nonlinear";
    int[] _function, _y, _delta;
    double[] _weight, _net, _out;
    int _errorCounter;


    //ОБЩИЕ МЕТОДЫ
    //запуск работы
    abstract boolean start();

    //коррекция весов
    abstract void weightCorrection(int index); // коррекция весов по обучающему набору с номером index

    abstract boolean trainNet();

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
            case _nonlinearAF:
                _out[setNumber] = 0.5 * (Math.tanh(_net[setNumber]) + 1);
        }
    }

    //реальный выход нейросети (двоичный вектор)
    void yEvaluate(int setNumber) {
        double border = 0;
        switch (_activationFunction) {
            case _linearAF:
                border = -0.00001; //для корректного сравнения с нулем чисел с плавающей точкой
                break;
            case _nonlinearAF:
                border = 0.5;
        }
        _y[setNumber] = (_net[setNumber] >= border ? 1 : 0);
    }
}

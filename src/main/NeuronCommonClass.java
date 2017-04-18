package main;

public class NeuronCommonClass {
    NeuronCommonClass(int nextLayer) {
        _nextLayer = nextLayer;
    }
    //номера следующего слоя для удобного поиска в массиве
    int _nextLayer;
    double _out;

    //индексы соседних слоев с собственным слоем нейрона
    public int getNextLayer() {
        return _nextLayer;
    }

    //сеттер и геттер
    public void setOut(double out) {
        _out = out;
    }

    public double getOut() {
        return _out;
    }
}

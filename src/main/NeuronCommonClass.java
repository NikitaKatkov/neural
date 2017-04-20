package main;

public class NeuronCommonClass {
    static final String BIAS_NEURON = "bias", SIMPLE_NEURON = "simple";
    NeuronCommonClass(int currentLayer, String neuronType) {
        _currentLayer = currentLayer;
        if (!neuronType.equals(BIAS_NEURON) & !neuronType.equals(SIMPLE_NEURON)) {
            throw new RuntimeException("попытка добавить нейрон неизвестного типа: " + neuronType);
        } else {
            _neuronType = neuronType;
        }
    }
    //номера следующего слоя для удобного поиска в массиве
    private int _currentLayer;
    double _out;
    private String _neuronType;

    //сеттер и геттер
    void setOut(double out) {
        _out = out;
    }
    double getOut() {
        return _out;
    }
    public int getCurrentLayer() {return _currentLayer;}
    public String getNeuronType() {
        return _neuronType;
    }
}

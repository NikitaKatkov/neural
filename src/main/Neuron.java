package main;

public class Neuron extends NeuronCommonClass{
    //конструктор
    Neuron(String activationFunction, int previousLayer, int nextLayer) {
        super(nextLayer);
        _previousLayer = previousLayer;
        _activationFunction = activationFunction;
    }

    // функция активации -- f(_net)
    private double border = 0.001;
    private String _activationFunction;
    private double activationFunction(int value) {
        switch (_activationFunction) {
            case LabCommonClass._linearAF:
                return value;
            case LabCommonClass._sigmoidAF:
                return (1 - Math.exp(-value))/(1 + Math.exp(-value));
            case LabCommonClass._stepAF:
                return Math.abs(value) > border ? 1 : 0;
            default:
                return value;
        }
    }



    private double _net;
    private int _previousLayer;

    // геттеры/сеттеры
    public double getNet() {
        return _net;
    }
    public void setNet(double _net) {
        this._net = _net;
    }
    public int getPreviousLayer() {
        return _previousLayer;
    }
}

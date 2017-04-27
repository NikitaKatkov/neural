package main;

public class Neuron extends NeuronCommonClass{
    //конструктор
    Neuron(String activationFunction, int currentLayer, String neuronType) {
        super(currentLayer, neuronType);
        _previousLayer = currentLayer - 1;
        _activationFunction = activationFunction;
    }

    // функция активации -- f(_net)
    private double border = 0.001;
    private String _activationFunction;

    void activationFunction() { // вычисляет out(net), записывает в свое поле _out
        switch (_activationFunction) {
            case LabCommonClass._linearAF:
                _out =  _net;
                break;
            case LabCommonClass._sigmoidAF:
                _out = (1 - Math.exp(-_net))/(1 + Math.exp(-_net));
                break;
            case LabCommonClass._stepAF:
                _out =  Math.abs(_net) > border ? 1 : 0;
                break;
            case LabCommonClass._bipolarAF:
                if (_net > 0) _out = 1;
                else if (_net < 0) _out = -1;
                break;
            default:
                _out = _net;
        }
    }

    double activationFunctionDerivative() {
        switch (_activationFunction) {
            case LabCommonClass._sigmoidAF:
                return 0.5*(1 - Math.pow(_out,2)); // т.к. в _out уже лежит вычисленное значние функции активации
            default:
                return 1;
        }
    }

    private double _net;
    private int _previousLayer;
    private double _delta;

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
    public double getDelta() {
        return _delta;
    }
    public void setDelta(double _delta) {
        this._delta = _delta;
    }
}

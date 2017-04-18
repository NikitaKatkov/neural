package main;

public class BiasNeuron extends NeuronCommonClass {
    BiasNeuron(double out, int nextLayer){
        super(nextLayer);
        _out = out;
    }
    BiasNeuron(int nextLayer) { //по умолчанию нейрон работает
        super(nextLayer);
        _out = 1;
    }
}

package main;

class BiasNeuron extends NeuronCommonClass {
    BiasNeuron(double out, int currentLayer, String neuronType){
        super(currentLayer, neuronType);
        _out = out;
    }
    BiasNeuron(int currentLayer, String neuronType) { //по умолчанию нейрон работает
        super(currentLayer, neuronType);
        _out = 1;
    }
}

package main;

public class Neuron {
    //конструктор
    Neuron(int previousLayer, int nextLayer) {
        this.previousLayer = previousLayer;
        this.nextLayer = nextLayer;
    }

    static double _activationFunction(int index) {
        return index; //записать функцию от индекса
    }

    private double net;
    private double y;

    public double getNet() {
        return net;
    }
    public void setNet(double net) {
        this.net = net;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }

    //номера следующего и предыдущего слоя для удобного поиска в массиве
    private int nextLayer;
    private int previousLayer;

    public int getNextLayer() {
        return nextLayer;
    }
    public int getPreviousLayer() {
        return previousLayer;
    }
}

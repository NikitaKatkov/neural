package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lab3 extends LabCommonClass{
    Lab3(double norm, String activationFunction, List<Integer> layerConfiguration, List<Integer> x, List<Integer> t) {
        super(norm, activationFunction);
        // инициализация структуры сети
        _network = new HashMap<>();
        int nextLayer;
        for (int layerIndex = 0; layerIndex < layerConfiguration.size(); layerIndex++) {
            //номер следующего слоя нейронов
            if (layerIndex + 1 == layerConfiguration.size()) nextLayer = layerIndex + 1;
            else nextLayer = -1;

            // нейрон по индексу 0 всегда нейрон смещения. Если он не нужен, его поле _out нужно изменить на 0
            List<NeuronCommonClass> layer = new ArrayList<>();
            layer.add(0, new BiasNeuron(1, nextLayer));
            for (int neuronIndex = 1; neuronIndex < layerConfiguration.get(layerIndex); neuronIndex++) {
                layer.add(new Neuron(activationFunction, layerIndex - 1, nextLayer));
            }
            _network.put(layerIndex, layer);
        }

        //инициализация входов и ожидаемого результата
        _layerConfiguration = layerConfiguration;
        _x = x;
        _t = t;
        //нейроны на нулевом уровне не будут содержать значений в поле net (сетевой вход), а будут содержать только значения out (выход)
        int xIndex = 0;
        for (NeuronCommonClass neuron: _network.get(0)) {
            neuron.setOut(_x.get(xIndex));
            xIndex++;
        }
    }

    // структура, хранящая слои нейронов - специально не совпадает со структурой слоя, чтобы разграничить обращение к этим сущностям
    private HashMap<Integer, List<NeuronCommonClass>> _network; // по сути обычный словарь, не пугайтесь
    private List<Integer> _x, _t, _layerConfiguration;


    @Override
    boolean start() {
        return false;
    }

    @Override
    void weightCorrection(int firstIndex, int secondIndex) {

    }

    @Override
    boolean trainNet() {
        //прямой ход - вычисление сетевых входов, выходов для каждого нейрона в каждом слое
        for (int layer : _layerConfiguration) {

        }
        return false;
    }

    @Override
    void netEvaluate(int firstIndex, int secondIndex) {

    }

    @Override
    void deltaEvaluate(int firstIndex, int secondIndex) {

    }

    @Override
    void outEvaluate(int firstIndex, int secondIndex) {

    }

    @Override
    void yEvaluate(int firstIndex, int secondIndex) {
        super.yEvaluate(firstIndex, secondIndex);
    }
}

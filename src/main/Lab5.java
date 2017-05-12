package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Lab5 extends LabCommonClass {
    private final String SIMPLE_NEURON = NeuronCommonClass.SIMPLE_NEURON;
    private final String _filePath = "C:\\Programmes_Fastboot\\Java\\neural\\src\\resources\\lab5data.txt";

    Lab5(double norm, String activationFunc, int numberOfAttributes ,int numberOfClusters) {
        super(norm, activationFunc);
        // количество входных нейронов -- число признаков, по которым производится кластеризация
        _numberOfAttributes = numberOfAttributes;
        // количество выходных нейронов -- количество кластеров
        _numberOfClusters = numberOfClusters;

        // инициализация контейнера нейронов - в нем всегда есть входной слой и слой Кохонена
        List<NeuronCommonClass> inputLayer = new ArrayList<>();

        // инициализация входов данными из файла
        _inputData = Functions.getDataFromCSV(_filePath);

        for (int i = 0; i < numberOfAttributes; i++) {
            // инициализация входных нейронов -- можно вынести из конструктора для изменения на лету, но нужно ли?
            inputLayer.add(i, new Neuron(_activationFunction, 0, SIMPLE_NEURON));
        }
        List<NeuronCommonClass> kohonenLayer = new ArrayList<>();
        for (int i = 0; i < numberOfClusters; i++) {
            kohonenLayer.add(new NeuronCommonClass(1, SIMPLE_NEURON));
        }
        _network = new HashMap<>();
        _network.put(0, inputLayer);
        _network.put(1, kohonenLayer);

        // инициализация контейнера весов
        _weights = new double[_numberOfAttributes][_numberOfClusters];
    }

    private HashMap<Integer, List<NeuronCommonClass>> _network; // словарь
    private HashMap<Integer, Entry> _inputData;
    private int _numberOfAttributes, _numberOfClusters;
    private double[][] _weights;



    @Override
    boolean start() {
        return trainNet();
    }

    @Override
    boolean trainNet() {
        boolean wasChanged;
        int epochCounter = 0;
        double epsilon = 0.0001;
        Neuron currentNeuron;
        do {
            wasChanged = false;
            // установка очередного вектора в качестве выходов нейронов 0 слоя
            for (int inputVectorIndex = 0; inputVectorIndex < _inputData.size(); inputVectorIndex++) {
                List<Double> out = _inputData.get(inputVectorIndex).get_value();
                for (int index = 0; index < _numberOfAttributes; index++) {
                    _network.get(0).get(index).setOut(out.get(index));
                }
            }

            epochCounter++;
        } while (wasChanged && epochCounter < _epochLimit);
        if (epochCounter >= _epochLimit) System.out.println("Лимит количества эпох превышен");

        return false;
    }

    private void netEvaluate(NeuronCommonClass currentNeuron, int neuronindex) {
        // вычисление входа только для слоя Кохонена
        double net = 0;
        int weightIndex = 0;
        for (NeuronCommonClass neuron : _network.get(0)) {
            net += neuron.getOut() * _weights[weightIndex++][neuronindex];
        }
        currentNeuron.setOut(net); // т.к. сетевой вход и выход совпадают, во вход даже писать ничего не будем
    }

    private int findMaxValue() {
        int maxIndex = 0;
        // просматриваем все выходы нейронов Кохонена и ищем максимальный
        for (int neuronIndex = 1; neuronIndex < _numberOfClusters; neuronIndex++) {
            if (_network.get(1).get(maxIndex).getOut()  < _network.get(1).get(neuronIndex).getOut())
                maxIndex = neuronIndex; // обновляем индекс максимального выхода
        }
        return maxIndex;
    }


    // изменение весов -- только для нейрона с наибольшим выходом ?
    /* если нет, то внести вовнутрьцикл по всем нейронам выходного слоя, параметр выпилить */
    private void weightCorrection(int neuronIndex) {
        for (int inputLayerNeuron = 0; inputLayerNeuron < _numberOfAttributes; inputLayerNeuron++) {
            _weights[inputLayerNeuron][neuronIndex] += _norm *
                    (_network.get(0).get(inputLayerNeuron).getOut() - _weights[inputLayerNeuron][neuronIndex]);
        }
    }
}

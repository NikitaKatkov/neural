package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class Lab5 extends LabCommonClass {
    private final String _simple_neuron = NeuronCommonClass.SIMPLE_NEURON;
    private final String _filePath = "C:\\Programmes_Fastboot\\Java\\neural\\src\\resources\\lab5data.txt";
    private final String _min = "min", _max = "max";

    Lab5(double norm, String activationFunc, int numberOfAttributes ,int numberOfClusters) {
        super(norm, activationFunc);
        // количество входных нейронов -- число признаков, по которым производится кластеризация
        _numberOfAttributes = numberOfAttributes;
        // количество выходных нейронов -- количество кластеров
        if (numberOfClusters < 1) throw new RuntimeException("Неверно задано число кластеров");
        _numberOfClusters = numberOfClusters;

        // инициализация контейнера нейронов - в нем всегда есть входной слой и слой Кохонена
        List<NeuronCommonClass> inputLayer = new ArrayList<>();

        // инициализация входов данными из файла
        _inputData = Functions.getDataFromFile(_filePath, _numberOfAttributes);
        _numberOfDataSets = Functions.getFileSize(_filePath);

        for (int i = 0; i < numberOfAttributes; i++) {
            // инициализация входных нейронов -- можно вынести из конструктора для изменения на лету, но нужно ли?
            inputLayer.add(i, new Neuron(_activationFunction, 0, _simple_neuron));
        }
        List<NeuronCommonClass> kohonenLayer = new ArrayList<>();
        for (int i = 0; i < numberOfClusters; i++) {
            kohonenLayer.add(new NeuronCommonClass(1, _simple_neuron));
        }
        _network = new HashMap<>();
        _network.put(0, inputLayer);
        _network.put(1, kohonenLayer);

        // инициализация контейнера весов
        Random random = new Random();
        _weights = new double[_numberOfAttributes][_numberOfClusters];
        /*for (int i = 0; i < _numberOfAttributes; i++) {
            for (int j = 0; j < _numberOfClusters; j++) {
                _weights[i][j] = 2000*random.nextGaussian();
            }
        }*/

        // контейнер с результатами работы
        _clusters = new HashMap<>();
        for (int i = 0; i < _numberOfClusters; i++) {
            _clusters.put(i, new ArrayList<Entry>());
        }
    }

    private HashMap<Integer, List<NeuronCommonClass>> _network; // словарь
    private HashMap<Integer, Entry> _inputData;
    private HashMap<Integer, List<Entry>> _clusters; // содержит результат работы
    private int _numberOfAttributes, _numberOfClusters;
    private double[][] _weights;
    private int _numberOfDataSets; // число входных векторов для кластеризации

    @Override
    boolean start() {
         if (trainNet()) {
             processData(false); // работа с уже обученной сетью
             printResult(true); // печать только ID - для печати данных передать true
             return true;
         } else return false;
    }

    @Override
    boolean trainNet() {
        return processData(true);
    }

    private boolean processData(boolean isTraining) {
        boolean wasChanged;
        int epochCounter = 0, maxOutIndex;
        double epsilon = 0.01, overallDelta;
        Entry currentEntry;
        List<Double> out;

        do {
            wasChanged = false;
            for (int dataSetIndex = 0; dataSetIndex < _numberOfDataSets; dataSetIndex++) {
                // установка очередного вектора в качестве выходов нейронов 0 слоя
                out = _inputData.get(dataSetIndex).get_value();
                for (int index = 0; index < _numberOfAttributes; index++) {
                    _network.get(0).get(index).setOut(out.get(index));
                }

                // предъявление очередного ветора значений, по которым производится кластреризация
                int neuronIndex = 0;
                for (NeuronCommonClass currentNeuron: _network.get(1)) {
                    netEvaluate(currentNeuron, neuronIndex);
                    neuronIndex++;
                }

                // нахождение нейрона с лучшим выходом
                maxOutIndex = findBestValue();

                if (isTraining) {
                    // коррекция весов
                    overallDelta = weightCorrection(maxOutIndex);

                    // проверка изменения весов
                    if (overallDelta > epsilon) wasChanged = true;
                } else {
                    // добавление в таблицу кластеров в режиме работы
                    _clusters.get(maxOutIndex).add(_inputData.get(dataSetIndex));
                }
            }
            epochCounter++;
        } while (wasChanged && epochCounter < _numberOfDataSets);
        return true;
    }

    // здесь сосредоточена логика сравнения данных -- метрика. Легко заменить на любую другую
    private void netEvaluate(NeuronCommonClass currentNeuron, int neuronIndex) {
        // вычисление входа только для слоя Кохонена
        double net = 0;
        int weightIndex = 0;
        /*
        for (NeuronCommonClass neuron : _network.get(0)) {
            net += neuron.getOut() * _weights[weightIndex++][neuronIndex]; // здесь было умножение
        } */

        // в out записаны расстояния от текущего обрабатываемого набора до вектора весов данного нейрона
        for (NeuronCommonClass neuron: _network.get(0)) {
            net += Math.pow(_weights[weightIndex][neuronIndex] - neuron.getOut(), 2);
            weightIndex++;
        }

        currentNeuron.setOut(Math.sqrt(net)); // т.к. сетевой вход и выход совпадают, во вход даже писать ничего не будем
    }

    private int findBestValue() { // ищет минимальный выход == минимальную разницу векторов
        int bestIndex = 0;
        // просматриваем все выходы нейронов Кохонена и ищем максимальный
        for (int neuronIndex = 1; neuronIndex < _numberOfClusters; neuronIndex++) {
            if (_network.get(1).get(bestIndex).getOut() > _network.get(1).get(neuronIndex).getOut())
                bestIndex = neuronIndex; // обновляем индекс максимального выхода
        }
        return bestIndex;
    }

    // изменение весов -- только для нейрона с наибольшим выходом ?
    /* если нет, то внести вовнутрь цикл по всем нейронам выходного слоя, параметр выпилить */
    private double weightCorrection(int neuronIndex) {
        // возвращает среднеквадратичное отклонение нового вектора весов от его предыдущего состояния
        double overallDelta = 0, delta;

        for (int inputLayerNeuron = 0; inputLayerNeuron < _numberOfAttributes; inputLayerNeuron++) {
            delta = _norm * (_network.get(0).get(inputLayerNeuron).getOut() - _weights[inputLayerNeuron][neuronIndex]);
            _weights[inputLayerNeuron][neuronIndex] += delta;
            overallDelta += Math.pow(delta, 2);
        }
        return Math.sqrt(overallDelta);
    }

    private void printResult(boolean withData) {
        String toPrint = withData ? "Значения: " : "IDs: ";
        String bracket;
        for (int clusterIndex = 0; clusterIndex < _numberOfClusters; clusterIndex++) {
            System.out.format("Кластер %d", clusterIndex);
            if (_numberOfAttributes == 1 && _clusters.get(clusterIndex).size() > 0) {
                double min = Functions.getSpecialValue(_clusters.get(clusterIndex), _min);
                double max = Functions.getSpecialValue(_clusters.get(clusterIndex), _max);
                System.out.format(" [от %.0f до %.0f]", min, max);
            }
            System.out.print(" : \r\n" + toPrint);
            for (Entry entry: _clusters.get(clusterIndex)) {
                if (withData) {
                    // печать самих признаков
                    if (entry.get_value().size() != 1) bracket = "|";
                    else bracket = "";

                    System.out.print(bracket);
                    for (double nextVal : entry.get_value()) {
                        System.out.format("%.0f; ", nextVal);
                    }
                    System.out.print(bracket + " ");
                } else {
                    // печать id
                    System.out.print(entry.get_ID() + "; ");
                }
            }
            System.out.println();
        }
    }

}
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Lab3 extends LabCommonClass {
    // константы
    static final String BIAS_NEURON = NeuronCommonClass.BIAS_NEURON;
    static final String SIMPLE_NEURON = NeuronCommonClass.SIMPLE_NEURON;

    // конструктор
    Lab3(double norm, String activationFunction, List<Integer> layerConfiguration, List<Double> x, List<Double> t) {
        super(norm, activationFunction);
        // инициализация входов и ожидаемого результата
        _layerConfiguration = layerConfiguration;
        _numberOfLayers = layerConfiguration.size();
        _inputX = x;
        _expectedOutput = t;

        // инициализация структуры сети
        _network = new HashMap<>();
        _weights = new HashMap<>();
        int nextLayer;
        for (int layerIndex = 0; layerIndex < layerConfiguration.size(); layerIndex++) {
            // номер следующего слоя нейронов
            nextLayer = layerIndex + 1;

            // нейрон по индексу 0 всегда нейрон смещения. Если он не нужен, его поле _out нужно изменить на 0
            List<NeuronCommonClass> layer = new ArrayList<>();
            layer.add(0, new BiasNeuron(layerIndex, BIAS_NEURON));
            for (int neuronIndex = 1; neuronIndex <= _layerConfiguration.get(layerIndex); neuronIndex++) {
                layer.add(new Neuron(activationFunction, layerIndex, SIMPLE_NEURON));
            }
            _network.put(layerIndex, layer);

            // инициализация контейнера весов, изначально все веса нулевые
            if (layerIndex < _numberOfLayers - 1) { // матриц весов на 1 меньше, чем слоев -- у последнего слоя ее нет
                // массивов весов на 1 меньше, чем слоев нейронов; размеры массивов -- размер текущего слоя*размер следующего
                // с учетом нейронов смещения
                _weights.put(layerIndex, new double[_layerConfiguration.get(layerIndex) + 1][_layerConfiguration.get(nextLayer) + 1]);
            }
        }

        // нейроны на нулевом уровне не будут содержать значений в поле net (сетевой вход), а будут содержать только значения out (выход)
        int xIndex = 0;
        for (NeuronCommonClass neuron : _network.get(0)) {
            neuron.setOut(_inputX.get(xIndex));
            xIndex++;
        }
    }

    // структура, хранящая слои нейронов - специально не совпадает со структурой слоя, чтобы разграничить обращение к этим сущностям
    private HashMap<Integer, List<NeuronCommonClass>> _network; // по сути обычный словарь, не пугайтесь
    private List<Integer> _layerConfiguration;
    private List<Double> _expectedOutput, _inputX;
    private int _numberOfLayers;
    // структура для хранения весов, совпадает по сути с контейнером нейронов, чтобы легче было искать соответствие между первыми и вторыми
    private HashMap<Integer, double[][]> _weights;


    @Override
    boolean start() {
        return trainNet();
    }


    @Override
    boolean trainNet() {
        double error;
        int epoch = 0;
        _border = 0.001;
        do {
            forwardEvaluation();
            backPropagation();
            weightCorrection();
            error = errorEvaluate();
            epoch++;
            System.out.format("Эпоха: " + epoch + "; Ошибка: %.5f\r\nВыходные значения: ", error);
            for (int i = 1; i <= _layerConfiguration.get(_numberOfLayers - 1); i++) {
                System.out.format("%.3f ", _network.get(_numberOfLayers - 1).get(i).getOut());
            }
            System.out.println();
        } while (error > _border && epoch < _epochLimit);
        return error <= _border;
    }

    private void forwardEvaluation() {
        List<NeuronCommonClass> currentLayer, previousLayer;
        double[][] currentWeights;
        //прямой ход - вычисление сетевых входов, выходов для каждого нейрона в каждом слое
        for (int layerIndex = 1; layerIndex < _numberOfLayers; layerIndex++) { // цикл идет по всем слоям нейросети, начиная с 1
            currentLayer = _network.get(layerIndex);
            previousLayer = _network.get(layerIndex - 1);
            currentWeights = _weights.get(layerIndex - 1);
            for (int neuronIndex = 1; neuronIndex <= _layerConfiguration.get(layerIndex); neuronIndex++) {
                // для каждого нейрона, кроме 0 -- это нейрон смещения, вычислим сетевой вход net и выход out
                Neuron currentNeuron = (Neuron) currentLayer.get(neuronIndex);
                netEvaluate(currentNeuron, neuronIndex, layerIndex, previousLayer, currentWeights);
                outEvaluate(currentNeuron);
            }
        }
    }

    private void backPropagation() {
        // сначала вычислим ошибку на последнем слое, формула для нее отличается от ошибок в скрытых слоях
        int expectedValueIndex = 0;
        double delta;
        for (NeuronCommonClass neuron : _network.get(_layerConfiguration.get(_numberOfLayers - 1))) {
            // цикл по всем нейронам последнего слоя
            if (neuron.getNeuronType().equals(BIAS_NEURON)) continue; // для нейрона смещения шибку не считаем
            if (neuron.getNeuronType().equals(SIMPLE_NEURON)) { // это условие на случай добавления других типов нейронов
                Neuron currentNeuron = (Neuron) neuron;
                delta = currentNeuron.activationFunctionDerivative() *( _expectedOutput.get(expectedValueIndex) - neuron.getOut());
                currentNeuron.setDelta(delta);
                expectedValueIndex++;
            }
        }

        // теперь считаем ошибки для всех внутренних слоев сети
        List<NeuronCommonClass> currentLayer, nextLayer;
        double[][] currentWeights;
        for (int layerIndex = _numberOfLayers - 2; layerIndex > 0; layerIndex--) {
            currentLayer = _network.get(layerIndex);
            nextLayer = _network.get(layerIndex - 1);
            currentWeights = _weights.get(layerIndex);
            for (int neuronIndex = 1; neuronIndex < _layerConfiguration.get(layerIndex); neuronIndex++) {
                // для каждого нейрона, кроме 0 -- это нейрон смещения, вычислим ошибку delta
                Neuron currentNeuron = (Neuron) currentLayer.get(neuronIndex);
                deltaEvaluate(currentNeuron, neuronIndex, layerIndex, nextLayer, currentWeights);
            }
        }
    }

    private void weightCorrection() {
        List<NeuronCommonClass> currentLayer, previousLayer;
        double[][] currentWeights;
        //прямой ход - вычисление сетевых входов, выходов для каждого нейрона в каждом слое
        for (int layerIndex = 1; layerIndex < _numberOfLayers; layerIndex++) { // цикл идет по всем слоям нейросети, начиная с 1
            currentLayer = _network.get(layerIndex);
            previousLayer = _network.get(layerIndex - 1);
            double currentWeight;
            currentWeights = _weights.get(layerIndex - 1);
            for (int weightFirstIndex = 0; weightFirstIndex < previousLayer.size(); weightFirstIndex++) {
                // цикл по всем весам, исходящим из одного нейрона предыдущего слоя
                for (int weightSecondIndex = 1; weightSecondIndex < currentLayer.size(); weightSecondIndex++) {
                    currentWeight = _weights.get(layerIndex - 1)[weightFirstIndex][weightSecondIndex]; // текущий изменяемый вес
                    currentWeight += _norm * previousLayer.get(weightFirstIndex).getOut() * ((Neuron)currentLayer.get(weightSecondIndex)).getDelta();
                    currentWeights[weightFirstIndex][weightSecondIndex] = currentWeight;

                }
            }
            _weights.put(layerIndex - 1, currentWeights);
        }
    }

    private void netEvaluate(Neuron currentNeuron, int neuronIndex, int layerIndex, List<NeuronCommonClass> previousLayer, double[][] currentWeights) {
        double net = 0;
        for (int previousNeuronIndex = 0; previousNeuronIndex <= _layerConfiguration.get(layerIndex - 1); previousNeuronIndex++) {
            // цикл по всем нейронам предыдущего слоя  (нейрон смещения тоже учитывается здесь)
            net += currentWeights[previousNeuronIndex][neuronIndex] * previousLayer.get(previousNeuronIndex).getOut();
        }
        currentNeuron.setNet(net);
    }

    private void deltaEvaluate(Neuron currentNeuron, int neuronIndex, int layerIndex, List<NeuronCommonClass> nextLayer, double[][] currentWeights) {
        double delta = 0;
        for (int nextNeuronIndex = 1; nextNeuronIndex < _layerConfiguration.get(layerIndex - 1); nextNeuronIndex++) {
            // цикл по всем нейронам следующего слоя без нейрона смещения
            delta += currentWeights[neuronIndex][nextNeuronIndex] * ((Neuron)nextLayer.get(nextNeuronIndex)).getDelta();
        }
        delta *= currentNeuron.activationFunctionDerivative();
        currentNeuron.setDelta(delta);
    }

    private void outEvaluate(Neuron currentNeuron) {
        currentNeuron.activationFunction(); // вычисляет out для одного нейрона
    }

    private double errorEvaluate() {
        double error = 0;
        for (int index = 0; index < _expectedOutput.size(); index++) {
            error += Math.pow(_expectedOutput.get(index) - _network.get(_numberOfLayers - 1).get(index + 1).getOut(), 2);
        }
        error = Math.sqrt(error);
        return error;
    }
}

package main;

import java.util.List;

public class Lab3 extends LabCommonClass{
    Lab3(double norm, String activationFunction, List<Integer> layerConfiguration) {
        super(norm, activationFunction);
    }

    @Override
    boolean start() {
        return false;
    }

    @Override
    void weightCorrection(int firstIndex, int secondIndex) {

    }

    @Override
    boolean trainNet() {
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

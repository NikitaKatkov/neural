public class Lab1Selection extends Lab1 implements LabCommonInterface {
    //первая лабораторная с реализацией перебора - вынесено в отдельный класс, потому что иначе Lab1 оказывается перегружен функционалом
    Lab1Selection (int numberOfVariables, double norm, String activationFunc) {
        super(numberOfVariables, norm, activationFunc);
    }

    public boolean start() {
        boolean trainingComplete = false;
        int iterationLimit = (int)Math.pow(2, NUMBER_OF_VARIABLES), iteration = 1;
        do {
            super.start();
//            trainingComplete =
            iteration++;
        } while (!trainingComplete || iteration < iterationLimit);
        return trainingComplete;
    }

}

package main;

import java.util.List;
import java.util.Scanner;

public class LabLauncher {
    public static void main(String[] args) {
        LabCommonClass lab;
        int labNumber;
        double norm;
        Scanner scanner = new Scanner(System.in);
        String activationFunc;
        if (args.length != 3) {
            System.out.println("Неверные аргументы командной строки (требуется: номер работы, норма обучения, функция активации)");
            return;
        } else {
            labNumber = Integer.parseInt(args[0]);
            norm = Double.parseDouble(args[1]);
            activationFunc = args[2];
        }
        switch (labNumber) {
            case 1:
                /*
                Здесь написан хороший код, архитектура которого позволяет настраивать работу нейросети при запуске программы.
                Это означает, что с разными параметрами программу придется запускать несколько раз. С точки зрения удобства чтения кода
                и адекватности стиля его написания это наилучший вариант. Но если потребуется запускать программу только один раз,
                нужно использовать нижеследующий закомментированный блок кода, который выполнит программу 4 раза.
                Но лучше запускать по-нормальному, мы ведь здоровые и адекватные люди, знающие, чо хардкод - это убого ;)
                */

                System.out.println("Количество переменных: ");
                int numberOfVariables = scanner.nextInt();
                System.out.println("Включить поиск наименьшего подмножества наборов для обучения? (y/n) ");
                String selection = scanner.next();
                boolean enableSelection;
                switch (selection) {
                    case "y":
                        enableSelection = true;
                        break;
                    case "n":
                        enableSelection = false;
                        break;
                    default:
                        System.out.println("Неверный формат введенных данных!");
                        return;
                }
                lab = new Lab1(numberOfVariables, norm, activationFunc, enableSelection);
                //конец адекватного запуска, начало глупого и некрасивого (и вообще на работе бы руки оторвали за такое)
/*
                System.out.format("%nЛинейная ФА без перебора%n");
                lab = new Lab1(4,0.15, "step", false);
                lab.start();
                System.out.format("%nЛинейная ФА с перебором%n");
                lab = new Lab1(4,0.15, "step", true);
                lab.start();
                System.out.format("%nНелинейная ФА без перебора%n");
                lab = new Lab1(4,0.15, "sigmoid", false);
                lab.start();
                System.out.format("%nНелинейная ФА с перебором%n");
                lab = new Lab1(4,0.15, "sigmoid", true);
                lab.start();
*/
                break;
            case 2:
                System.out.println("Число точек в интервале: ");
                int numberOfPoints = scanner.nextInt();
                System.out.println("Длина \"окна\": ");
                int intervalSize = scanner.nextInt();
                if (intervalSize <= 0 || intervalSize > numberOfPoints) {
                    System.out.println("Неверно задана длина \"окна\"!");
                    return;
                }
                System.out.println("Левая граница интервала: ");
                int beginOfInterval = scanner.nextInt();
                System.out.println("Правая граница интервала: ");
                int endOfInterval = scanner.nextInt();
//                System.out.println("Точность: ");
//                double border = scanner.nextDouble();
                lab = new Lab2(norm, activationFunc, intervalSize,beginOfInterval, endOfInterval, numberOfPoints);
                break;
            case 3:
                System.out.println("Конфигурация слоев в виде количества элементов в слоях: \"входы нейроны...нейроны выходы\" через пробел: ");
                String layersStr = scanner.next();
                List<Integer> layersConfiguration = Functions.parseIntegers(layersStr);
                System.out.println("Входные значения через пробел: ");
                String xStr = scanner.next();
                List<Double> x = Functions.parseDoubles(xStr);
                System.out.println("Требуемые выходные значения через пробел: ");
                String tStr = scanner.next();
                List<Double> t = Functions.parseDoubles(tStr);

                // проверка корректности ввода
                if (layersConfiguration.get(0) != x.size() - 1) {
                    System.out.println("Число входов не совпадает с числом входных нейронов!");
                    return;
                } else if (layersConfiguration.get(layersConfiguration.size() - 1) != t.size()) {
                    System.out.println("Число выходов не совпадает с числом выходных нейронов!");
                    return;
                }
                lab = new Lab3(norm, activationFunc, layersConfiguration, x, t);
                break;
            case 4:
                System.out.println("Количество паттернов: ");
                int numberOfPatterns = scanner.nextInt();

                lab = new Lab4(norm, activationFunc, numberOfPatterns, scanner);
                break;
            case 5:
                System.out.println("Количество признаков для кластеризации: ");
                int numberOfAttributes = scanner.nextInt();
                System.out.println("Количество кластеров: ");
                int numberOfClusters = scanner.nextInt();

                lab = new Lab5(norm, activationFunc, numberOfAttributes, numberOfClusters);
                break;
            default:
                System.out.println("Других лаб нет :)");
                return;
        }
        lab.start();
    }
}

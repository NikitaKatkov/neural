package main;

import java.util.Scanner;

public class LabLauncher {
    public static void main(String[] args) {
        LabCommonInterface lab;
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
                здесь написан хороший код, архитектура которого позволяет настраивать работу нейросети при запуске программы.
                Это означает, что с разными параметрами программу придется запускать несколько раз. С точки зрения удобства чтения кода
                и адекватности стиля его написания это наилучший вариант. Но если потребуется запускать программу только один раз,
                нужно использовать нижеследующий закомментированный блок кода, который выполнит программу 4 раза.
                Но лучше запускать по-нормальному, мы ведь здоровые и адекватные люди, знающие, чо хардкод - это убого ;)
                */
                System.out.println("Введите количество переменных: ");
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
                System.out.println("Линейная ФА без перебора");
                lab = new Lab1(4,0.11, "linear", false);
                lab.start();
                System.out.println("Линейная ФА с перебором");
                lab = new Lab1(4,0.11, "linear", true);
                lab.start();
                System.out.println("Нелинейная ФА без перебора");
                lab = new Lab1(4,0.11, "nonlinear", false);
                lab.start();
                System.out.println("Нелинейная ФА с перебором");
                lab = new Lab1(4,0.11, "nonlinear", true);
                lab.start();
                */
                break;
            default:
                System.out.println("Остальных лаб пока нет :)");
                return;
        }
        lab.start();
    }
}

import java.util.Scanner;

public class LabLauncher {
    public static void main(String[] args) {
        LabCommonClass lab = null;
        int labNumber = 0;
        double norm = 0.0;
        Scanner scanner = new Scanner(System.in);
        String activationFunc = "";
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
                System.out.println("Введите количество переменных: ");
                int numberOfVariables = scanner.nextInt();
                lab = new Lab1(numberOfVariables, norm, activationFunc);
                break;
            default:
                System.out.println("Остальных лаб пока нет :)");
                return;
        }
        lab.start();
    }
}

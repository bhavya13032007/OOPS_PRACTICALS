import java.util.Scanner;

enum Coin {
    ONE, TWO, FIVE, TEN
}

public class VendingMachine {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int price = 15;
        int total = 0;

        while (total < price) {
            System.out.print("Enter coin (ONE, TWO, FIVE, TEN): ");
            Coin coin = Coin.valueOf(sc.next().toUpperCase());

            int value = 0;

            switch (coin) {
                case ONE:
                    value = 1;
                    break;
                case TWO:
                    value = 2;
                    break;
                case FIVE:
                    value = 5;
                    break;
                case TEN:
                    value = 10;
                    break;
            }

            total += value;
            System.out.println("Total so far: " + total);
        }

        System.out.println("Paid. Change: " + (total - price));

        sc.close();
    }
}

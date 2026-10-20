abstract class Account {
    protected long accountNumber;
    protected String ownerName;
    protected long balance;

    Account(long accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    abstract double interestRate();

    abstract boolean canWithdraw(long amount);
}

class SavingsAccount extends Account {
    private long minBalance;

    SavingsAccount(long accountNumber, String ownerName, long balance, long minBalance) {
        super(accountNumber, ownerName, balance);
        this.minBalance = minBalance;
    }

    double interestRate() {
        return 4.0;
    }

    boolean canWithdraw(long amount) {
        return balance - amount >= minBalance;
    }
}

class CurrentAccount extends Account {
    private long overdraftLimit;

    CurrentAccount(long accountNumber, String ownerName, long balance, long overdraftLimit) {
        super(accountNumber, ownerName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    double interestRate() {
        return 0;
    }

    boolean canWithdraw(long amount) {
        return balance - amount >= -overdraftLimit;
    }
}

class FixedDepositAccount extends Account {

    FixedDepositAccount(long accountNumber, String ownerName, long balance) {
        super(accountNumber, ownerName, balance);
    }

    double interestRate() {
        return 7.0;
    }

    boolean canWithdraw(long amount) {
        return false;
    }
}

public class Driver {
    public static void main(String[] args) {

        Account[] accounts = {
            new SavingsAccount(101, "Rahul", 10000, 2000),
            new CurrentAccount(102, "Amit", 5000, 3000),
            new FixedDepositAccount(103, "Priya", 20000)
        };

        for (Account a : accounts) {
            System.out.println("Account: " + a.accountNumber);
            System.out.println("Interest Rate: " + a.interestRate() + "%");
            System.out.println("Can Withdraw 4000: " + a.canWithdraw(4000));

            if (a instanceof FixedDepositAccount) {
                System.out.println("This is a Fixed Deposit Account");
            }

            System.out.println();
        }
    }
}
package model;

public class SavingsAccount extends Account {

    private long minBalance;

    public SavingsAccount(long number, String name, long balance, long minBalance) {
        super(number, name, balance);
        this.minBalance = minBalance;
    }

    public double interestRate() {
        return 4.0;
    }

    public boolean canWithdraw(long amount) {
        return balance - amount >= minBalance;
    }
}
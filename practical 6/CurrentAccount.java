package model;

public class CurrentAccount extends Account implements Premium {

    private long overdraftLimit;

    public CurrentAccount(long number, String name, long balance, long limit) {
        super(number, name, balance);
        overdraftLimit = limit;
    }

    public double interestRate() {
        return 0;
    }

    public boolean canWithdraw(long amount) {
        return balance - amount >= -overdraftLimit;
    }
}
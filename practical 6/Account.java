package model;

import util.Transactable;
import util.InterestBearing;

public abstract class Account implements Transactable, InterestBearing {

    public static Account currentAccount;

    protected long accountNumber;
    protected String ownerName;
    protected long balance;

    public Account(long accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public long getBalance() {
        return balance;
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public void deposit(long amount) {
        balance = balance + amount;
    }

    public boolean withdraw(long amount) {
        if (canWithdraw(amount)) {
            balance = balance - amount;
            return true;
        }
        return false;
    }

    public String toString() {
        return accountNumber + " " + ownerName + " " + balance;
    }
}
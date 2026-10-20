package util;

import model.Account;

public interface InterestBearing {
    default double yearlyInterest() {
        return Account.currentAccount.interestRate() *
               Account.currentAccount.getBalance() / 100;
    }
}
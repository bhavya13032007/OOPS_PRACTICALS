import model.*;
import util.*;
import service.*;

import static java.lang.System.out;

public class MiniBank {

    public static void main(String[] args) {

        Account a1 = new SavingsAccount(101, "Rahul", 10000, 2000);
        Account a2 = new CurrentAccount(102, "Amit", 5000, 3000);

        BankService.printAccount(a1);
        BankService.printAccount(a2);

        Account.currentAccount = a1;

        System.out.println("Interest: " + a1.yearlyInterest());

        WithdrawRule rule1 = new WithdrawRule() {
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        WithdrawRule rule2 = (account, amount) ->
            account.canWithdraw(amount);

        out.println("Anonymous: " + rule1.allow(a1, 3000));
        out.println("Lambda: " + rule2.allow(a2, 7000));

        if (a2 instanceof Premium) {
            out.println("Premium account");
        }
    }
}
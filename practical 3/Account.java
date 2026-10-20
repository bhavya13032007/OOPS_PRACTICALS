class Account {
    private int accountNumber;
    private String ownerName;
    private double balance;

    Account(int accountNumber, String ownerName, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public String toString() {
        return "Account Number: " + accountNumber +
               ", Owner: " + ownerName +
               ", Balance: " + balance;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Account))
            return false;

        Account a = (Account) o;

        return accountNumber == a.accountNumber;
    }

    public int hashCode() {
        return Integer.hashCode(accountNumber);
    }
}
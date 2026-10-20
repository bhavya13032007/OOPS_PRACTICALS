
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.lang.annotation.*;
import java.lang.reflect.Field;

public class MiniBank {
    record BankInfo(String name, String branch) {}
    enum MenuOption { OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT }

    @FunctionalInterface
    interface WithdrawRule {
        boolean allow(Account account, long amount);
    }

    interface Transactable {
        void deposit(long amount) throws BankException;
        boolean withdraw(long amount) throws BankException;
    }

    interface InterestBearing {
        double interestRate();
        default double yearlyInterest(Account a) {
            return a.getBalance() * interestRate() / 100.0;
        }
    }

    interface Premium {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Id {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Positive {
        String message() default "must be > 0";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface MaxLength {
        int value();
    }

    static class Customer implements Cloneable, Serializable {
        private static final long serialVersionUID = 1L;
        private static long customerCounter = 100;
        private final String customerId;
        private String name;
        private String email;
        private String mobile;
        private Address address;

        private static String generateCustomerId() {
            return "CUST" + (++customerCounter);
        }

        Customer(String name, String email, String mobile) {
            this.customerId = generateCustomerId();
            this.name = name;
            this.email = email;
            this.mobile = mobile;
        }

        public String getCustomerId() { return customerId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getMobile() { return mobile; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        @Override
        public Customer clone() {
            try {
                Customer copy = (Customer) super.clone();
                if (address != null)
                    copy.address = new Address(address.line, address.city, address.pincode);
                return copy;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        public static class Address implements Serializable {
            private static final long serialVersionUID = 1L;
            private String line, city, pincode;

            Address(String line, String city, String pincode) {
                this.line = line;
                this.city = city;
                this.pincode = pincode;
            }

            public String getLine() { return line; }
            public String getCity() { return city; }
            public String getPincode() { return pincode; }
        }
    }

    static class BankException extends Exception {
        BankException(String message) { super(message); }
    }

    static class InsufficientFundsException extends BankException {
        private final long shortfall;
        InsufficientFundsException(long shortfall) {
            super("Insufficient funds. Shortfall: Rs. " + shortfall);
            this.shortfall = shortfall;
        }
        public long getShortfall() { return shortfall; }
    }

    static class AccountNotFoundException extends BankException {
        AccountNotFoundException(String id) {
            super("Account not found: " + id);
        }
    }

    static class InvalidAmountException extends BankException {
        InvalidAmountException() {
            super("Amount must be greater than zero.");
        }
    }

    static class Validator {
        private static final Pattern MOBILE = Pattern.compile("^[6-9]\\d{9}$");
        private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
        private static final Pattern PAN = Pattern.compile("^[A-Z]{5}\\d{4}[A-Z]$");
        private static final Pattern IFSC = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

        static boolean isValidMobile(String s) { return s != null && MOBILE.matcher(s).matches(); }
        static boolean isValidEmail(String s) { return s != null && EMAIL.matcher(s).matches(); }
        static boolean isValidPan(String s) { return s != null && PAN.matcher(s).matches(); }
        static boolean isValidIfsc(String s) { return s != null && IFSC.matcher(s).matches(); }
    }

    enum TransactionType { DEPOSIT, WITHDRAW, TRANSFER }
    record Command(TransactionType type, String accountNumber, long amount) {}

    static class CommandParser {
        static Command parse(String line) {
            String[] p = line.trim().split("\\s+");
            if (p.length != 3)
                throw new IllegalArgumentException("Use: TYPE ACCOUNT_NUMBER AMOUNT");
            return new Command(TransactionType.valueOf(p[0].toUpperCase()),
                    p[1], Long.parseLong(p[2]));
        }
    }

    static abstract class Account implements Transactable, InterestBearing,
            Comparable<Account>, Serializable {
        private static final long serialVersionUID = 1L;
        private static long counter = 0;

        @Id
        private final String accountNumber;
        private String ownerName;
        @Positive
        private long balance;
        private boolean active = true;

        Account(String ownerName, long openingBalance) throws InvalidAmountException {
            if (openingBalance < 0) throw new InvalidAmountException();
            this.accountNumber = String.format("AC%04d", ++counter);
            this.ownerName = ownerName;
            this.balance = openingBalance;
        }

        Account(String ownerName) throws InvalidAmountException {
            this(ownerName, 0);
        }

        public String getAccountNumber() { return accountNumber; }
        public String getOwnerName() { return ownerName; }
        public long getBalance() { return balance; }
        public boolean isActive() { return active; }
        public void close() { active = false; }

        protected void setBalance(long amount) { balance = amount; }

        @Override
        public synchronized void deposit(long amount) throws BankException {
            if (amount <= 0) throw new InvalidAmountException();
            if (!active) throw new BankException("Account is inactive.");
            balance = Math.addExact(balance, amount);
        }

        public abstract boolean canWithdraw(long amount);

        @Override
        public synchronized boolean withdraw(long amount) throws BankException {
            if (amount <= 0) throw new InvalidAmountException();
            if (!active) throw new BankException("Account is inactive.");
            if (!canWithdraw(amount)) {
                long shortfall = Math.max(0, amount - balance);
                throw new InsufficientFundsException(shortfall);
            }
            balance -= amount;
            return true;
        }

        public void transfer(Account to, long amount) throws BankException {
            if (to == null) throw new AccountNotFoundException("null");
            if (amount <= 0) throw new InvalidAmountException();
            Account first = accountNumber.compareTo(to.accountNumber) <= 0 ? this : to;
            Account second = first == this ? to : this;

            synchronized (first) {
                synchronized (second) {
                    withdraw(amount);
                    try {
                        to.deposit(amount);
                    } catch (BankException | RuntimeException e) {
                        balance += amount;
                        if (e instanceof BankException be) throw be;
                        throw e;
                    }
                }
            }
        }

        @Override
        public int compareTo(Account other) {
            return accountNumber.compareTo(other.accountNumber);
        }

        @Override
        public String toString() {
            return accountNumber + " | " + ownerName + " | Balance: Rs. " + balance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Account a)) return false;
            return accountNumber.equals(a.accountNumber);
        }

        @Override
        public int hashCode() { return accountNumber.hashCode(); }
    }

    static class SavingsAccount extends Account {
        private static final long serialVersionUID = 1L;
        private long minBalance;

        SavingsAccount(String name, long opening, long minBalance) throws InvalidAmountException {
            super(name, opening);
            this.minBalance = minBalance;
        }

        @Override public double interestRate() { return 4.0; }
        @Override public boolean canWithdraw(long amount) {
            return getBalance() - amount >= minBalance;
        }
    }

    static class CurrentAccount extends Account {
        private static final long serialVersionUID = 1L;
        private long overdraftLimit;

        CurrentAccount(String name, long opening, long overdraftLimit) throws InvalidAmountException {
            super(name, opening);
            this.overdraftLimit = overdraftLimit;
        }

        @Override public double interestRate() { return 0.0; }
        @Override public boolean canWithdraw(long amount) {
            return getBalance() - amount >= -overdraftLimit;
        }
    }

    static class FixedDepositAccount extends Account {
        private static final long serialVersionUID = 1L;

        FixedDepositAccount(String name, long opening) throws InvalidAmountException {
            super(name, opening);
        }

        @Override public double interestRate() { return 7.0; }
        @Override public boolean canWithdraw(long amount) { return false; }
    }

    static class PremiumSavingsAccount extends SavingsAccount implements Premium {
        private static final long serialVersionUID = 1L;
        PremiumSavingsAccount(String name, long opening, long minBalance) throws InvalidAmountException {
            super(name, opening, minBalance);
        }
    }

    static class StatementFormatter {
        static String buildStatement(Account a) {
            StringBuilder sb = new StringBuilder();
            sb.append("----- MINI BANK STATEMENT -----\n");
            sb.append("Account: ").append(a.getAccountNumber()).append('\n');
            sb.append("Owner: ").append(a.getOwnerName()).append('\n');
            sb.append("Balance: Rs. ").append(a.getBalance()).append('\n');
            sb.append("Interest rate: ").append(a.interestRate()).append("%\n");
            sb.append("Status: ").append(a.isActive() ? "Active" : "Inactive");
            return sb.toString();
        }
    }

    static class AnnotationValidator {
        static String[] validate(Object obj) {
            List<String> errors = new ArrayList<>();
            for (Field f : obj.getClass().getDeclaredFields()) {
                try {
                    f.setAccessible(true);
                    Object value = f.get(obj);
                    if (f.isAnnotationPresent(Positive.class) && value instanceof Number n
                            && n.longValue() <= 0) {
                        errors.add(f.getName() + ": " +
                                f.getAnnotation(Positive.class).message());
                    }
                    if (f.isAnnotationPresent(MaxLength.class) && value instanceof String s
                            && s.length() > f.getAnnotation(MaxLength.class).value()) {
                        errors.add(f.getName() + ": maximum length exceeded");
                    }
                } catch (ReflectiveOperationException | RuntimeException e) {
                    errors.add("Cannot validate " + f.getName());
                }
            }
            return errors.toArray(new String[0]);
        }
    }

    static class Repository<T> {
        private final HashMap<String, T> data = new HashMap<>();

        T save(T entity) {
            try {
                for (Field f : entity.getClass().getDeclaredFields()) {
                    if (f.isAnnotationPresent(Id.class)) {
                        f.setAccessible(true);
                        Object id = f.get(entity);
                        if (id == null) throw new IllegalArgumentException("Null id");
                        data.put(id.toString(), entity);
                        return entity;
                    }
                }
                throw new IllegalArgumentException("No @Id field found");
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @SafeVarargs
        final void saveAll(T... items) {
            for (T item : items) save(item);
        }

        T findById(String id) { return data.get(id); }
        ArrayList<T> findAll() { return new ArrayList<>(data.values()); }
        boolean delete(String id) { return data.remove(id) != null; }

        static <N extends Number> double average(N[] values) {
            if (values.length == 0) throw new IllegalArgumentException("Empty array");
            double sum = 0;
            for (N n : values) sum += n.doubleValue();
            return sum / values.length;
        }
    }

    static class AccountStore {
        private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
        private final ArrayList<String> history = new ArrayList<>();

        void add(Account a) { accounts.put(a.getAccountNumber(), a); }
        Account get(String id) { return accounts.get(id); }
        ArrayList<Account> getAll() { return new ArrayList<>(accounts.values()); }

        ArrayList<Account> sortedByBalance() {
            ArrayList<Account> result = getAll();
            result.sort(Comparator.comparingLong(Account::getBalance));
            return result;
        }

        synchronized void log(String line) { history.add(line); }
        synchronized ArrayList<String> getHistory() { return new ArrayList<>(history); }
    }

    static class StatePersister {
        static void save(Account[] accounts, Path file) throws IOException {
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(accounts);
            }
        }

        static Account[] load(Path file) throws IOException, ClassNotFoundException {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                return (Account[]) in.readObject();
            }
        }
    }

    static class TransactionLog {
        static void append(Path file, String line) throws IOException {
            Files.writeString(file, line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    static class ReportGenerator {
        static String generate(Path folder, Path output) throws IOException {
            long deposits = 0, withdrawals = 0;
            StringBuilder report = new StringBuilder("MINI BANK END-OF-DAY REPORT\n");
            if (Files.exists(folder)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
                    for (Path file : stream) {
                        if (!Files.isRegularFile(file)) continue;
                        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                        report.append(file.getFileName()).append(" | ")
                              .append(attr.size()).append(" bytes | ")
                              .append(attr.lastModifiedTime()).append('\n');

                        try (BufferedReader reader = Files.newBufferedReader(file)) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] p = line.split("\\s+");
                                if (p.length >= 3) {
                                    try {
                                        long amount = Long.parseLong(p[p.length - 1]);
                                        if (p[0].equals("DEPOSIT")) deposits += amount;
                                        if (p[0].equals("WITHDRAW")) withdrawals += amount;
                                    } catch (NumberFormatException ignored) {}
                                }
                            }
                        }
                    }
                }
            }
            report.append("Total deposits: Rs. ").append(deposits).append('\n');
            report.append("Total withdrawals: Rs. ").append(withdrawals).append('\n');
            Files.writeString(output, report.toString());
            return report.toString();
        }
    }

    static class TransactionProcessor {
        private final ExecutorService pool = Executors.newFixedThreadPool(4);

        void submit(Runnable task) { pool.execute(task); }

        void stop() throws InterruptedException {
            pool.shutdown();
            if (!pool.awaitTermination(1, TimeUnit.MINUTES)) pool.shutdownNow();
        }
    }

    static class AccountWorker implements Runnable {
        private final Account account;
        private final int times;
        private final long amount;

        AccountWorker(Account account, int times, long amount) {
            this.account = account;
            this.times = times;
            this.amount = amount;
        }

        @Override public void run() {
            for (int i = 0; i < times; i++) {
                try {
                    account.deposit(amount);
                } catch (BankException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    static class Buffer {
        private final Queue<Integer> queue = new LinkedList<>();
        private final int capacity = 3;

        synchronized void produce(int value) throws InterruptedException {
            while (queue.size() == capacity) wait();
            queue.add(value);
            notifyAll();
        }

        synchronized int consume() throws InterruptedException {
            while (queue.isEmpty()) wait();
            int value = queue.remove();
            notifyAll();
            return value;
        }
    }

    static class BankService {
        private final AccountStore store = new AccountStore();
        private final Path logFile = Paths.get("transactions.log");

        Account openAccount(String name, int type, long opening) throws BankException {
            Account a = switch (type) {
                case 1 -> new SavingsAccount(name, opening, 0);
                case 2 -> new CurrentAccount(name, opening, 1000);
                case 3 -> new FixedDepositAccount(name, opening);
                default -> throw new BankException("Invalid account type");
            };
            store.add(a);
            return a;
        }

        Account find(String id) throws AccountNotFoundException {
            Account a = store.get(id);
            if (a == null) throw new AccountNotFoundException(id);
            return a;
        }

        void deposit(String id, long amount) throws BankException, IOException {
            Account a = find(id);
            a.deposit(amount);
            store.log("DEPOSIT " + id + " " + amount);
            TransactionLog.append(logFile, "DEPOSIT " + id + " " + amount);
        }

        void withdraw(String id, long amount) throws BankException, IOException {
            Account a = find(id);
            a.withdraw(amount);
            store.log("WITHDRAW " + id + " " + amount);
            TransactionLog.append(logFile, "WITHDRAW " + id + " " + amount);
        }

        void transfer(String from, String to, long amount) throws BankException, IOException {
            Account a = find(from);
            Account b = find(to);
            a.transfer(b, amount);
            store.log("TRANSFER " + from + " " + amount + " TO " + to);
            TransactionLog.append(logFile, "WITHDRAW " + from + " " + amount);
            TransactionLog.append(logFile, "DEPOSIT " + to + " " + amount);
        }

        String statement(String id) throws BankException {
            return StatementFormatter.buildStatement(find(id));
        }

        ArrayList<Account> listAccounts() { return store.sortedByBalance(); }
        Account[] allAccounts() { return store.getAll().toArray(new Account[0]); }
        void add(Account a) { store.add(a); }
    }

    static class ResourceDemo implements AutoCloseable {
        @Override public void close() {
            System.out.println("Resource closed");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankService bank = new BankService();
        BankInfo info = new BankInfo("MiniBank", "Ahmedabad");
        System.out.println("===== " + info.name() + " | " + info.branch() + " =====");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Statement");
            System.out.println("6. List Accounts");
            System.out.println("7. Save Accounts");
            System.out.println("8. Load Accounts");
            System.out.println("9. Generate Report");
            System.out.println("10. Exit");
            System.out.print("Choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Enter a number.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            try {
                MenuOption option = switch (choice) {
                    case 1 -> MenuOption.OPEN_ACCOUNT;
                    case 2 -> MenuOption.DEPOSIT;
                    case 3 -> MenuOption.WITHDRAW;
                    case 4 -> MenuOption.TRANSFER;
                    case 10 -> MenuOption.EXIT;
                    default -> null;
                };

                if (option == MenuOption.EXIT) {
                    StatePersister.save(bank.allAccounts(), Paths.get("accounts.dat"));
                    System.out.println("Goodbye!");
                    running = false;
                    continue;
                }

                switch (choice) {
                    case 1 -> {
                        System.out.print("Owner name: ");
                        String name = sc.nextLine();
                        System.out.println("1 Savings  2 Current  3 Fixed Deposit");
                        int type = sc.nextInt();
                        System.out.print("Opening balance: ");
                        long amount = sc.nextLong();
                        sc.nextLine();
                        Account a = bank.openAccount(name, type, amount);
                        System.out.println("Account created: " + a);
                    }
                    case 2 -> {
                        System.out.print("Account number: ");
                        String id = sc.nextLine();
                        System.out.print("Amount: ");
                        long amount = sc.nextLong();
                        sc.nextLine();
                        bank.deposit(id, amount);
                        System.out.println("Deposit successful.");
                    }
                    case 3 -> {
                        System.out.print("Account number: ");
                        String id = sc.nextLine();
                        System.out.print("Amount: ");
                        long amount = sc.nextLong();
                        sc.nextLine();
                        bank.withdraw(id, amount);
                        System.out.println("Withdrawal successful.");
                    }
                    case 4 -> {
                        System.out.print("From account: ");
                        String from = sc.nextLine();
                        System.out.print("To account: ");
                        String to = sc.nextLine();
                        System.out.print("Amount: ");
                        long amount = sc.nextLong();
                        sc.nextLine();
                        bank.transfer(from, to, amount);
                        System.out.println("Transfer successful.");
                    }
                    case 5 -> {
                        System.out.print("Account number: ");
                        System.out.println(bank.statement(sc.nextLine()));
                    }
                    case 6 -> {
                        for (Account a : bank.listAccounts()) System.out.println(a);
                    }
                    case 7 -> {
                        StatePersister.save(bank.allAccounts(), Paths.get("accounts.dat"));
                        System.out.println("Accounts saved.");
                    }
                    case 8 -> {
                        Account[] loaded = StatePersister.load(Paths.get("accounts.dat"));
                        for (Account a : loaded) bank.add(a);
                        System.out.println("Accounts loaded.");
                    }
                    case 9 -> System.out.println(ReportGenerator.generate(
                            Paths.get("."), Paths.get("report.txt")));
                    default -> System.out.println("Invalid choice.");
                }
            } catch (BankException | IOException | ClassNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                sc.nextLine();
            } catch (ArithmeticException e) {
                System.out.println("Amount overflow.");
            }
        }
        sc.close();
    }
}

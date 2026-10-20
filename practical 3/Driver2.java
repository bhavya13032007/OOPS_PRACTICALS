class Driver2 {
    public static void main(String[] args) {

        Account a1 = new Account(101, "Rahul", 5000);
        Account a2 = new Account(101, "Amit", 7000);

        System.out.println(a1);
        System.out.println(a2);

        if (a1.equals(a2))
            System.out.println("Accounts are equal");
        else
            System.out.println("Accounts are not equal");

        AddressTest();
        
        Object obj = a1;

        if (obj instanceof Account)
            System.out.println("Object is an Account");
    }

    static void AddressTest() {
        Customer.Address address =
            new Customer.Address("12 MG Road", "Vadodara", "390001");

        Customer c1 = new Customer("Rahul", address);
        Customer c2 = c1.clone();

        System.out.println(c2.getAddress().getCity());
    }
}
class Driver1 {
    public static void main(String[] args) {

        Card[] cards = new Card[5];

        int count = 0;
        boolean duplicate = false;

        Card[] input = {
            new Card("Ace", "Spades"),
            new Card("King", "Hearts"),
            new Card("Queen", "Clubs"),
            new Card("Ace", "Spades"),
            new Card("Jack", "Diamonds")
        };

        for (int i = 0; i < input.length; i++) {

            for (int j = 0; j < count; j++) {
                if (input[i].equals(cards[j])) {
                    System.out.println("Duplicate found: " + input[i]);
                    duplicate = true;
                    break;
                }
            }

            if (duplicate)
                break;

            cards[count] = input[i];
            count++;
        }
    }
}
// Stores and manages all the accounts in an array
public class Bank {
    private BankAccount[] accounts;
    private int count;

    // default constructor
    public Bank() {
        accounts = new BankAccount[100];
        count = 0;
    }

    public int getCount() {
        return count;
    }

    // Returns the account at the given position (0 to count - 1)
    public BankAccount getAccountAt(int index) {
        return accounts[index];
    }

    // Adds a new account; returns false if the bank is full
    public boolean addAccount(BankAccount account) {
        if (count < accounts.length) {
            accounts[count] = account;
            count++;
            return true;
        }
        return false;
    }

    // Finds an account by account number; returns null if it does not exist
    public BankAccount findAccount(String accountNumber) {
        for (int i = 0; i < count; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                return accounts[i];
            }
        }
        return null;
    }

    // Removes an account and shifts the later accounts one place to the left
    public boolean removeAccount(String accountNumber) {
        for (int i = 0; i < count; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                for (int j = i; j < count - 1; j++) {
                    accounts[j] = accounts[j + 1];
                }
                accounts[count - 1] = null;
                count--;
                return true;
            }
        }
        return false;
    }

    // Adds up the balances of all accounts
    public double getTotalBalance() {
        double total = 0;
        for (int i = 0; i < count; i++) {
            total += accounts[i].getBalance();
        }
        return total;
    }

    // Returns the first account number from 1001 upwards that is not in use
    public String nextAccountNumber() {
        int candidate = 1001;
        while (findAccount(String.valueOf(candidate)) != null) {
            candidate++;
        }
        return String.valueOf(candidate);
    }
}

// Abstract base class: every account has a holder, a number and a balance,
// but each type of account (Savings, Current) describes itself differently.
public abstract class BankAccount {
    private String accountHolder;
    private final String accountNumber; // read-only: never changes once the account exists
    private double balance;             // private: only changed through deposit() and withdraw()

    // Default constructor assigns default values to the private variables
    public BankAccount() {
        this("Unknown", "0000", 0.0);
    }

    // Parameterised constructor creates an account with specific details
    public BankAccount(String accountHolder, String accountNumber, double openingBalance) {
        setAccountHolder(accountHolder); // use the setter so that validation is applied
        this.accountNumber = accountNumber;
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.balance = openingBalance;
    }

    // Each subclass says what kind of account it is
    public abstract String getAccountType();

    // Getters
    public String getAccountHolder() {
        return accountHolder;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    // Setter with validation (the account number and balance have no public setters)
    public void setAccountHolder(String accountHolder) {
        if (accountHolder == null || accountHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty.");
        }
        this.accountHolder = accountHolder.trim();
    }

    // Only subclasses may change the balance directly (needed to override withdraw)
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    // Rejects zero, negative and non-finite amounts
    protected void validateAmount(double amount) {
        if (!(amount > 0) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    public void deposit(double amount) {
        validateAmount(amount);
        balance += amount;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        validateAmount(amount);
        if (amount > balance) {
            throw new InsufficientFundsException(
                    String.format("Insufficient funds. Available balance: UGX %,.2f", balance));
        }
        balance -= amount;
    }
}

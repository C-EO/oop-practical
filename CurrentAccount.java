// A current account may go below zero, up to its overdraft limit
public class CurrentAccount extends BankAccount {
    private final double overdraftLimit = 50000;

    public CurrentAccount(String accountHolder, String accountNumber, double openingBalance) {
        super(accountHolder, accountNumber, openingBalance);
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    // Overrides withdraw(): the overdraft limit is added to what can be withdrawn
    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        validateAmount(amount);
        if (amount > getBalance() + overdraftLimit) {
            throw new InsufficientFundsException(String.format(
                    "Overdraft limit exceeded. Available: UGX %,.2f (balance plus UGX %,.2f overdraft)",
                    getBalance() + overdraftLimit, overdraftLimit));
        }
        setBalance(getBalance() - amount);
    }

    @Override
    public String getAccountType() {
        return "Current";
    }
}

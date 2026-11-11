// A savings account earns interest on its balance
public class SavingsAccount extends BankAccount {
    private final double interestRate = 0.05; // 5%

    public SavingsAccount(String accountHolder, String accountNumber, double openingBalance) {
        super(accountHolder, accountNumber, openingBalance);
    }

    public double getInterestRate() {
        return interestRate;
    }

    // Adds interest to the balance and returns the amount that was added
    public double addInterest() {
        double interest = getBalance() * interestRate;
        if (interest > 0) {
            deposit(interest);
        }
        return interest;
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }
}

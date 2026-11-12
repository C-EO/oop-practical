# Simple Banking System

A small Java Swing application for managing bank accounts. It was built for the CSC 1102 (Structured and Object-Oriented Programming) practical exam project.

**Group:** `Group 24`
**Members:**

| Name | Student Number | Registration No. |
|---|---|---|
| Sebalemba Dennis | 2600726046 | 26/U/26046/PS |
| Lubega Michael | 2600703548 | 26/U/03548/PS |
| Muyonjo Solomon | 2600703581 | 26/U/03581/PS |
| Rob Wilbert | 2600726537 | 26/X/26537/PS |
| Ssenfuma Emmanuel | 2600703644 | 26/U/03644/PS |

## Features

- Create **Savings** or **Current** accounts, with an optional opening deposit
- Account numbers are generated automatically (from 1001 upwards)
- **Deposit** and **withdraw** money from the selected account
- **Add interest** (5%) to savings accounts
- Current accounts can go into an **overdraft** of up to UGX 50,000
- **Delete** an account (after confirming)
- Live table of all accounts, with the total number of accounts and total balance in the header
- Activity log of every action, and clear error messages for invalid input (empty names, non-numeric or non-positive amounts, insufficient funds)

## How to run

You need a JDK (Java 8 or later).

```
javac *.java
java BankingGUI
```

All the files are in the same folder and use the default package.

## Classes

| Class | Purpose |
|---|---|
| `BankAccount` | Abstract base class. Holds the account holder, account number and balance (all `private`), with constructors, getters, a validated setter, `deposit()` and `withdraw()` |
| `SavingsAccount` | Extends `BankAccount`. Adds a 5% interest rate and `addInterest()` |
| `CurrentAccount` | Extends `BankAccount`. Overrides `withdraw()` to allow an overdraft |
| `InsufficientFundsException` | Custom checked exception thrown when a withdrawal is too large |
| `Bank` | Stores all accounts in a `BankAccount[]` array and provides add, find, remove, total balance and next account number |
| `BankingGUI` | The Swing window. Creates the `Bank`, builds the interface and handles button clicks. Contains `main()` |

## How the classes interact

1. `BankingGUI` creates one `Bank` when the program starts.
2. When the user creates an account, the GUI creates a `SavingsAccount` or `CurrentAccount` (chosen with a `switch`) and passes it to `Bank.addAccount()`.
3. The accounts table is rebuilt from the `Bank` array after every action, so the table and the data always match.
4. Deposit and withdraw call the methods on the selected `BankAccount`. Because the array holds both account types, the correct `withdraw()` runs for each (polymorphism).
5. Invalid input is reported by exceptions (`IllegalArgumentException`, `InsufficientFundsException`), which the GUI catches and shows in a dialog.

## OOP concepts demonstrated

- **Encapsulation:** private fields with getters and validated setters; the balance can only change through `deposit()` and `withdraw()`
- **Inheritance and abstraction:** `SavingsAccount` and `CurrentAccount` extend the abstract `BankAccount`
- **Polymorphism:** overriding `withdraw()` and `getAccountType()`, and an array that holds both account types
- **Constructors:** default and parameterised constructors, `super(...)` calls
- **Exception handling:** try/catch blocks with a custom exception
- **Arrays:** the `BankAccount[]` array inside `Bank`
- **Flow control:** `for`, `while`, `switch` and `if/else`

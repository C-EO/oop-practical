/*
 * CSC 1102: Structured and Object-Oriented Programming - Practical Exam Project
 * Simple Banking System
 *
 * Group name   : Group 24
 * Group leader : Lubega Michael
 * Members      :
 *   Name               Student No.   Registration No.
 *   Sebalemba Dennis   2600726046    26/U/26046/PS
 *   Lubega Michael     2600703548    26/U/03548/PS
 *   Muyonjo Solomon    2600703581    26/U/03581/PS
 *   Rob Wilbert        2600726537    26/X/26537/PS
 *   Ssenfuma Emmanuel  2600703644    26/U/03644/PS
 * GitHub link  : https://github.com/c-eo/oop-practical
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BankingGUI extends JFrame {

    // Colours and fonts used throughout the window
    private static final Color NAVY = new Color(0x1F2A44);
    private static final Color BACKGROUND = new Color(0xF2F5FA);
    private static final Color CARD_BORDER = new Color(0xDDE3EE);
    private static final Color TEXT = new Color(0x2B3445);
    private static final Color MUTED = new Color(0x6B7589);
    private static final Color BLUE = new Color(0x2F6FED);
    private static final Color GREEN = new Color(0x1FA971);
    private static final Color ORANGE = new Color(0xF08A24);
    private static final Color PURPLE = new Color(0x7B5CD6);
    private static final Color RED = new Color(0xD64545);
    private static final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_FONT = BASE_FONT.deriveFont(Font.BOLD);

    private static final String NUMBER_ERROR = "Enter a valid amount, e.g. 5000 (digits only, no commas).";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Bank bank;

    // New account form
    private JTextField holderField;
    private JTextField numberField;
    private JComboBox<String> typeBox;
    private JTextField openingField;

    // Accounts table
    private JTable table;
    private DefaultTableModel tableModel;

    // Transactions
    private JLabel selectedLabel;
    private JTextField amountField;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton interestButton;
    private JButton deleteButton;

    // Other display areas
    private JLabel summaryLabel;
    private JTextArea logArea;

    // Constructor
    public BankingGUI() {
        bank = new Bank();

        setTitle("Simple Banking System");
        setSize(1060, 760);
        setMinimumSize(new Dimension(940, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BACKGROUND);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Right-hand side: accounts table on top, transactions and log underneath
        JPanel bottom = new JPanel(new BorderLayout(0, 16));
        bottom.setOpaque(false);
        bottom.add(buildTransactionCard(), BorderLayout.NORTH);
        bottom.add(buildLogCard(), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(0, 16));
        right.setOpaque(false);
        right.add(buildAccountsCard(), BorderLayout.CENTER);
        right.add(bottom, BorderLayout.SOUTH);

        body.add(buildNewAccountCard(), BorderLayout.WEST);
        body.add(right, BorderLayout.CENTER);

        add(buildHeader(), BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        refreshTable();
    }

    // ---------------------------------------------------------------
    // Building the window
    // ---------------------------------------------------------------

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Simple Banking System");
        title.setFont(BASE_FONT.deriveFont(Font.BOLD, 26f));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage accounts, deposits and withdrawals");
        subtitle.setFont(BASE_FONT);
        subtitle.setForeground(new Color(0xAEB8D0));

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);

        summaryLabel = new JLabel("", SwingConstants.RIGHT);
        summaryLabel.setFont(BOLD_FONT);
        summaryLabel.setForeground(Color.WHITE);

        header.add(titles, BorderLayout.WEST);
        header.add(summaryLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildNewAccountCard() {
        holderField = createField();
        numberField = createField();
        numberField.setEditable(false);
        numberField.setBackground(new Color(0xF2F5FA));
        openingField = createField();

        typeBox = new JComboBox<>(new String[] { "Savings", "Current" });
        typeBox.setFont(BASE_FONT);
        typeBox.setBackground(Color.WHITE);

        JButton createButton = new RoundButton("Create Account", BLUE);
        createButton.addActionListener(e -> createAccount());

        typeBox.setPreferredSize(new Dimension(0, 38));
        typeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        createButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Components are stacked top to bottom, left-aligned and stretched to the card's width
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.add(createLabel("Account holder"));
        form.add(holderField);
        form.add(Box.createVerticalStrut(14));
        form.add(createLabel("Account number (automatic)"));
        form.add(numberField);
        form.add(Box.createVerticalStrut(14));
        form.add(createLabel("Account type"));
        form.add(typeBox);
        form.add(Box.createVerticalStrut(14));
        form.add(createLabel("Opening deposit (UGX, optional)"));
        form.add(openingField);
        form.add(Box.createVerticalStrut(22));
        form.add(createButton);

        for (Component component : form.getComponents()) {
            ((JComponent) component).setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(form, BorderLayout.NORTH);

        JPanel card = createCard("New account", wrapper);
        card.setPreferredSize(new Dimension(290, 0));
        return card;
    }

    private JPanel buildAccountsCard() {
        tableModel = new DefaultTableModel(
                new String[] { "Number", "Holder", "Type", "Balance (UGX)" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // the table is for viewing only
            }
        };

        table = new JTable(tableModel);
        table.setFont(BASE_FONT);
        table.setForeground(TEXT);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(0xDCE8FF));
        table.setSelectionForeground(TEXT);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(BOLD_FONT);
        tableHeader.setBackground(NAVY);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);
        tableHeader.setPreferredSize(new Dimension(0, 36));

        // Balances are easier to read when right-aligned
        DefaultTableCellRenderer rightAligned = new DefaultTableCellRenderer();
        rightAligned.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightAligned);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return createCard("Accounts (click a row to select it)", scrollPane);
    }

    private JPanel buildTransactionCard() {
        selectedLabel = new JLabel();
        selectedLabel.setFont(BASE_FONT.deriveFont(Font.BOLD, 16f));
        selectedLabel.setForeground(BLUE);

        amountField = createField();

        JPanel amountPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        amountPanel.setOpaque(false);
        amountPanel.setPreferredSize(new Dimension(220, 70));
        amountPanel.add(createLabel("Amount (UGX)"));
        amountPanel.add(amountField);

        depositButton = new RoundButton("Deposit", GREEN);
        withdrawButton = new RoundButton("Withdraw", ORANGE);
        interestButton = new RoundButton("Add Interest", PURPLE);
        deleteButton = new RoundButton("Delete", RED);

        depositButton.addActionListener(e -> deposit());
        withdrawButton.addActionListener(e -> withdraw());
        interestButton.addActionListener(e -> addInterest());
        deleteButton.addActionListener(e -> deleteAccount());

        JPanel buttons = new JPanel(new GridLayout(1, 4, 10, 0));
        buttons.setOpaque(false);
        buttons.add(depositButton);
        buttons.add(withdrawButton);
        buttons.add(interestButton);
        buttons.add(deleteButton);

        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(buttons, BorderLayout.SOUTH);

        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.add(amountPanel, BorderLayout.WEST);
        row.add(buttonWrapper, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.add(selectedLabel, BorderLayout.NORTH);
        content.add(row, BorderLayout.CENTER);

        return createCard("Transactions", content);
    }

    private JPanel buildLogCard() {
        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logArea.setForeground(TEXT);
        logArea.setBackground(new Color(0xF8FAFD));
        logArea.setMargin(new Insets(6, 8, 6, 8));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(new LineBorder(CARD_BORDER));
        scrollPane.setPreferredSize(new Dimension(0, 90));

        return createCard("Activity log", scrollPane);
    }

    // ---------------------------------------------------------------
    // Button actions
    // ---------------------------------------------------------------

    private void createAccount() {
        String holder = holderField.getText().trim();
        if (holder.isEmpty()) {
            showError("Enter the account holder's name.");
            return;
        }

        try {
            double opening = 0;
            String openingText = openingField.getText().trim();
            if (!openingText.isEmpty()) {
                opening = parseAmount(openingText);
            }

            String number = bank.nextAccountNumber();
            String type = (String) typeBox.getSelectedItem();
            BankAccount account;

            switch (type) {
                case "Savings":
                    account = new SavingsAccount(holder, number, opening);
                    break;
                default:
                    account = new CurrentAccount(holder, number, opening);
                    break;
            }

            if (!bank.addAccount(account)) {
                showError("The bank is full. No more accounts can be added.");
                return;
            }

            log("Created " + type + " account " + number + " for " + holder
                    + String.format(" with UGX %,.2f", opening));
            holderField.setText("");
            openingField.setText("");
            refreshTable();
            table.setRowSelectionInterval(bank.getCount() - 1, bank.getCount() - 1);
        } catch (NumberFormatException ex) {
            showError(NUMBER_ERROR);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void deposit() {
        BankAccount account = getSelectedAccount();
        if (account == null) {
            return;
        }

        try {
            double amount = parseAmount(amountField.getText());
            account.deposit(amount);
            log(String.format("Deposited UGX %,.2f into %s", amount, account.getAccountNumber()));
            amountField.setText("");
            refreshTable();
        } catch (NumberFormatException ex) {
            showError(NUMBER_ERROR);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void withdraw() {
        BankAccount account = getSelectedAccount();
        if (account == null) {
            return;
        }

        try {
            double amount = parseAmount(amountField.getText());
            account.withdraw(amount);
            log(String.format("Withdrew UGX %,.2f from %s", amount, account.getAccountNumber()));
            amountField.setText("");
            refreshTable();
        } catch (NumberFormatException ex) {
            showError(NUMBER_ERROR);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (InsufficientFundsException ex) {
            log("Withdrawal refused on " + account.getAccountNumber());
            showError(ex.getMessage());
        }
    }

    private void addInterest() {
        BankAccount account = getSelectedAccount();

        // Only savings accounts earn interest
        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            double interest = savings.addInterest();
            log(String.format("Added interest of UGX %,.2f (%.0f%%) to %s",
                    interest, savings.getInterestRate() * 100, savings.getAccountNumber()));
            refreshTable();
        }
    }

    private void deleteAccount() {
        BankAccount account = getSelectedAccount();
        if (account == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete account " + account.getAccountNumber() + " belonging to "
                        + account.getAccountHolder() + "?",
                "Delete account", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            bank.removeAccount(account.getAccountNumber());
            log("Deleted account " + account.getAccountNumber());
            refreshTable();
        }
    }

    // ---------------------------------------------------------------
    // Keeping the window up to date
    // ---------------------------------------------------------------

    // Rebuilds the table from the bank's array and keeps the selected row
    private void refreshTable() {
        int selectedRow = table.getSelectedRow();
        tableModel.setRowCount(0);

        for (int i = 0; i < bank.getCount(); i++) {
            BankAccount account = bank.getAccountAt(i);
            tableModel.addRow(new Object[] {
                    account.getAccountNumber(),
                    account.getAccountHolder(),
                    account.getAccountType(),
                    String.format("%,.2f", account.getBalance())
            });
        }

        if (selectedRow >= 0 && selectedRow < bank.getCount()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }

        numberField.setText(bank.nextAccountNumber());
        summaryLabel.setText(String.format("Accounts: %d     Total balance: UGX %,.2f",
                bank.getCount(), bank.getTotalBalance()));
        updateSelection();
    }

    // Shows the selected account and enables only the buttons that make sense
    private void updateSelection() {
        BankAccount account = getSelectedAccount();

        if (account == null) {
            selectedLabel.setText("Select an account from the table to make a transaction");
            selectedLabel.setForeground(MUTED);
        } else {
            selectedLabel.setText(String.format("%s  •  %s  •  %s  •  UGX %,.2f",
                    account.getAccountNumber(), account.getAccountHolder(),
                    account.getAccountType(), account.getBalance()));
            selectedLabel.setForeground(BLUE);
        }

        depositButton.setEnabled(account != null);
        withdrawButton.setEnabled(account != null);
        deleteButton.setEnabled(account != null);
        interestButton.setEnabled(account instanceof SavingsAccount);
    }

    // The table rows are in the same order as the bank's array
    private BankAccount getSelectedAccount() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= bank.getCount()) {
            return null;
        }
        return bank.getAccountAt(row);
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    // Converts text to a number; throws NumberFormatException if it is not a valid number
    private double parseAmount(String text) {
        double value = Double.parseDouble(text.trim());
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new NumberFormatException("Not a finite number");
        }
        return value;
    }

    private void log(String message) {
        logArea.append("[" + LocalTime.now().format(TIME_FORMAT) + "]  " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Simple Banking System", JOptionPane.ERROR_MESSAGE);
    }

    // White rounded panel with a heading, used for each section of the window
    private JPanel createCard(String title, JComponent content) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true), new EmptyBorder(14, 16, 16, 16)));

        JLabel heading = new JLabel(title);
        heading.setFont(BASE_FONT.deriveFont(Font.BOLD, 16f));
        heading.setForeground(NAVY);

        card.add(heading, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BASE_FONT.deriveFont(Font.BOLD, 12f));
        label.setForeground(MUTED);
        return label;
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setFont(BASE_FONT);
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true), new EmptyBorder(7, 10, 7, 10)));
        return field;
    }

    // A button drawn with rounded corners that changes colour on hover and press
    private static class RoundButton extends JButton {
        private final Color baseColor;

        RoundButton(String text, Color baseColor) {
            super(text);
            this.baseColor = baseColor;
            setFont(BOLD_FONT);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setRolloverEnabled(true);
            setMargin(new Insets(0, 6, 0, 6));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(130, 42));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = baseColor;
            if (!isEnabled()) {
                fill = new Color(0xE3E7EF);
            } else if (getModel().isPressed()) {
                fill = baseColor.darker();
            } else if (getModel().isRollover()) {
                fill = baseColor.brighter();
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();

            super.paintComponent(g); // draws the text
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankingGUI gui = new BankingGUI();
            gui.setVisible(true);
        });
    }
}

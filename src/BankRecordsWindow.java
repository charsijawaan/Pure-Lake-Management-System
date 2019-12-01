import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

class BankRecordsWindow extends Activity {

    private DBHelper dbHelper;

    BankRecordsWindow() {
        super();
        dbHelper = DBHelper.getDBHelper();
        initComponents();
    }

    private void initComponents() {
        setActivityName("Bank Records");
        setDefaultCloseOperation(Activity.DISPOSE_ON_CLOSE);

        RecordTransactionPanel recordPanel = new RecordTransactionPanel();
        ViewTransactionsPanel viewPanel = new ViewTransactionsPanel();
        BankBalancePanel bankBalancePanel = new BankBalancePanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tabbedPane.add(recordPanel, "Record Transaction");
        tabbedPane.add(viewPanel, "View Transactions");
        tabbedPane.add(bankBalancePanel, "Bank Balance");
        tabbedPane.addChangeListener((e) -> {
            viewPanel.fillTable();
            bankBalancePanel.fillTable();
        });

        addView(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private class RecordTransactionPanel extends JPanel {

        RecordTransactionPanel() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JRadioButton debitRadioButton = new JRadioButton("Debit");
            JRadioButton creditRadioButton = new JRadioButton("Credit");
            JTextField amountField = new JTextField(20);
            JButton doneButton = new JButton("Record Transaction");

            doneButton.addActionListener((e) -> {
                String amount = amountField.getText();
                if(amount.length() > 0) {
                    new DatePicker((day, month, year) -> {
                        try {
                            if(debitRadioButton.isSelected()) {
                                dbHelper.recordDebit(day, month, year, amount);
                            }
                            else {
                                dbHelper.recordCredit(day, month, year, amount);
                            }
                            AlertBox.show("Transaction recorded successfully.");
                        }
                        catch (SQLException ex) {
                            AlertBox.show("Some database error occurred!");
                            System.exit(0);
                        }
                    });
                }
                else {
                    AlertBox.show("All fields with * are required!");
                }
            });

            amountField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                    char keyChar = e.getKeyChar();

                    if(!Character.isDigit(keyChar))
                        e.consume();
                }
            });

            debitRadioButton.setSelected(true);
            debitRadioButton.addActionListener((e) -> {
                if(!debitRadioButton.isSelected())
                    debitRadioButton.setSelected(true);

                creditRadioButton.setSelected(false);
            });

            creditRadioButton.addActionListener((e) -> {
                if(!creditRadioButton.isSelected())
                    creditRadioButton.setSelected(true);

                debitRadioButton.setSelected(false);
            });

            panel1.add(debitRadioButton);
            panel1.add(creditRadioButton);

            panel2.add(new JLabel("*Amount"));
            panel2.add(amountField);

            panel3.add(doneButton);

            add(panel1);
            add(panel2);
            add(panel3);
        }
    }

    private class ViewTransactionsPanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable transactionsTable;
        private int lastSearchIndex;

        private int creditVal;
        private int debitVal;

        ViewTransactionsPanel() {
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col != 0;
                }
            };

            lastSearchIndex = -1;
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel fieldsPanel = new JPanel();
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            fieldsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            JTextField dateField = new JTextField(20);
            JButton searchByDateBtn = new JButton("Search");
            JButton updateBtn = new JButton("Update");

            searchByDateBtn.addActionListener((e) -> {
                String dateToSearch = dateField.getText().toLowerCase();

                if(dateToSearch.length() == 0)
                    return;

                boolean found = false;
                String date;
                for(int i = lastSearchIndex + 1; i < tableModel.getRowCount(); i++) {
                    date = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
                    if(date.equals(dateToSearch)) {
                        transactionsTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    transactionsTable.clearSelection();
                    lastSearchIndex = -1;
                }
            });

            updateBtn.addActionListener((e) -> {
                int i = transactionsTable.getSelectedRow();

                // If some row is selected
                if(i > -1) {
                    String id = (String) tableModel.getValueAt(i, 0);
                    String date = (String) tableModel.getValueAt(i, 1);
                    String credit = (String) tableModel.getValueAt(i, 2);
                    String debit = (String) tableModel.getValueAt(i, 3);

                    // Some validation
                    if(debit.length() > 0 && credit.length() > 0) {

                        if(!Utilities.isInteger(debit) || !Utilities.isInteger(credit)) {
                            AlertBox.show("Credit and Debit should be numbers!");
                            return;
                        }

                        if(!((!credit.equals("0") && debit.equals("0")) || (credit.equals("0") && !debit.equals("0")))) {
                            AlertBox.show("Debit should be 0 when credit > 0 and vise versa!");
                            return;
                        }

                        try {
                            dbHelper.updateTransaction(id, date, credit, debit);

                            int balanceAdjustment = 0;
                            int creditAdjustment = Integer.parseInt(credit) - creditVal;
                            int debitAdjustment = Integer.parseInt(debit) - debitVal;
                            balanceAdjustment -= creditAdjustment;
                            balanceAdjustment += debitAdjustment;

                            dbHelper.adjustBalance(balanceAdjustment);

                            AlertBox.show("Transaction Updated Successfully.");
                            transactionsTable.clearSelection();
                        }
                        catch (SQLException ex) {
                            AlertBox.show("Some database error occurred.");
                            ex.printStackTrace();
                            System.exit(0);
                        }
                    }
                    else {
                        AlertBox.show("All fields with * are required.");
                    }
                }
                else {
                    AlertBox.show("Must select a row first!");
                }
            });


            fieldsPanel.add(new JLabel("Date"));
            fieldsPanel.add(dateField);
            fieldsPanel.add(searchByDateBtn);
            fieldsPanel.add(updateBtn);

            transactionsTable = new JTable(tableModel);
            transactionsTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    int i = transactionsTable.getSelectedRow();

                    creditVal = Integer.parseInt((String) tableModel.getValueAt(i, 2));
                    debitVal = Integer.parseInt((String) tableModel.getValueAt(i, 3));
                }
            });

            tableModel.addColumn("Id");
            tableModel.addColumn("Date");
            tableModel.addColumn("Credit");
            tableModel.addColumn("Debit");

            JScrollPane scrollPane = new JScrollPane(transactionsTable);

            add(fieldsPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void fillTable() {
            transactionsTable.removeAll();
            tableModel.setRowCount(0);
            try {
                ResultSet resultSet = dbHelper.getAllTransactions();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String date = resultSet.getString("date");
                    String credit = resultSet.getString("credit");
                    String debit = resultSet.getString("debit");
                    tableModel.addRow(new Object[]{id, date, credit, debit});
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred.");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private class BankBalancePanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable bankBalanceTable;

        BankBalancePanel() {
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

            bankBalanceTable = new JTable(tableModel);
            tableModel.addColumn("Balance");

            JScrollPane scrollPane = new JScrollPane(bankBalanceTable);

            add(scrollPane);
        }

        private void fillTable() {
            bankBalanceTable.removeAll();
            tableModel.setRowCount(0);
            try {
                ResultSet resultSet = dbHelper.getBankBalance();
                while(resultSet.next()) {
                    String bankBalance = resultSet.getString("balance");
                    tableModel.addRow(new Object[]{bankBalance});
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred.");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

class ExpensesWindow extends Activity {

    private DBHelper dbHelper;

    ExpensesWindow() {
        super();
        dbHelper = DBHelper.getDBHelper();
        initComponents();
    }

    private void initComponents() {
        setActivityName("Expenses");
        setDefaultCloseOperation(Activity.DISPOSE_ON_CLOSE);

        RecordExpensesPanel recordExpensesPanel = new RecordExpensesPanel();
        ViewExpensesPanel viewExpensesPanel = new ViewExpensesPanel();
        AddExpenseCategory addExpenseCategory = new AddExpenseCategory();
        ViewSalariesPanel viewSalariesPanel = new ViewSalariesPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tabbedPane.add(recordExpensesPanel, "Record Expenses");
        tabbedPane.add(viewExpensesPanel, "View Expenses");
        tabbedPane.add(addExpenseCategory, "Add Expense Category");
        tabbedPane.add(viewSalariesPanel, "View Salaries");
        tabbedPane.addChangeListener((e) -> viewExpensesPanel.fillTable());

        addView(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private class AddExpenseCategory extends JPanel {

        AddExpenseCategory() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JTextField categoryNameField = new JTextField(20);
            panel1.add(new JLabel("*Category Name"));
            panel1.add(categoryNameField);

            JButton doneBtn = new JButton("Add Category");
            doneBtn.addActionListener((e) -> {
                String categoryName = categoryNameField.getText();

                if(categoryName.length() == 0) {
                    AlertBox.show("All fields with a * are required!");
                    return;
                }

                try {
                    dbHelper.insertExpenseCategory(categoryName);

                    AlertBox.show("Expense Category added successfully.");
                    categoryNameField.setText("");
                }
                catch (SQLException ex) {
                    AlertBox.show("Some database error occurres!");
                    System.exit(0);
                }
            });
            panel2.add(doneBtn);

            add(panel1);
            add(panel2);
        }
    }

    private class RecordExpensesPanel extends JPanel {

        RecordExpensesPanel() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JComboBox<Expense> expenseNameComboBox = new JComboBox<>();
            fillComboBox(expenseNameComboBox);
            panel1.add(new JLabel("*Expense Category"));
            panel1.add(expenseNameComboBox);

            JTextField amountField = new JTextField(20);
            amountField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                    char ch = e.getKeyChar();

                    if(!Character.isDigit(ch)) {
                        e.consume();
                    }
                }
            });
            panel2.add(new JLabel("*Amount"));
            panel2.add(amountField);

            JTextArea descriptionArea = new JTextArea(5, 20);
            panel3.add(new JLabel("Description"));
            panel3.add(descriptionArea);

            JButton doneBtn = new JButton("Record Expense");
            doneBtn.addActionListener((e) -> {
                String expenseId = ((Expense) expenseNameComboBox.getSelectedItem()).getId();
                String amount = amountField.getText();
                String desc = descriptionArea.getText();

                if(amount.length() > 0) {
                    new DatePicker((day, month, year) -> {
                        String date = year + "-" + month + "-" + day;
                        try {
                            dbHelper.insertExpense(expenseId, date, amount, desc);

                            AlertBox.show("Expense added successfully.");
                            expenseNameComboBox.setSelectedIndex(0);
                            amountField.setText("");
                            descriptionArea.setText("");
                        }
                        catch (SQLException ex) {
                            AlertBox.show("Some database error occurres!");
                            ex.printStackTrace();
                            System.exit(0);
                        }
                    });
                }
                else {
                    AlertBox.show("All fields with a * are required!");
                }
            });
            panel4.add(doneBtn);

            add(panel1);
            add(panel2);
            add(panel3);
            add(panel4);
        }

        void fillComboBox(JComboBox<Expense> comboBox) {
            try {
                ResultSet resultSet = dbHelper.getExpensesCategory();
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");

                    comboBox.addItem(new Expense(id, name));
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred!");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private class ViewExpensesPanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable expenseTable;
        private int lastSearchIndex;

        ViewExpensesPanel() {
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            lastSearchIndex = -1;
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel fieldsPanel = new JPanel();
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.X_AXIS));

            JTextField nameField = new JTextField(20);
            JTextField dateField = new JTextField(20);
            JButton searchByNameBtn = new JButton("Search By Name");
            JButton searchByDateBtn = new JButton("Search By Date");
            JButton deleteBtn = new JButton("Delete");

            searchByNameBtn.addActionListener((e) -> {
                String nameToSearch = nameField.getText().toLowerCase();

                if(nameToSearch.length() == 0)
                    return;

                boolean found = false;
                String name;
                for(int i = lastSearchIndex + 1; i < tableModel.getRowCount(); i++) {
                    name = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
                    if(name.contains(nameToSearch) || nameToSearch.contains(name)) {
                        expenseTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    expenseTable.clearSelection();
                    lastSearchIndex = -1;
                }
            });

            searchByDateBtn.addActionListener((e) -> {
                String dateToSearch = dateField.getText().toLowerCase();

                if(dateToSearch.length() == 0)
                    return;

                boolean found = false;
                String date;
                for(int i = lastSearchIndex + 1; i < tableModel.getRowCount(); i++) {
                    date = ((String) tableModel.getValueAt(i, 2)).toLowerCase();

                    if(date.length() == 0)
                        continue;

                    if(date.equals(dateToSearch)) {
                        expenseTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    expenseTable.clearSelection();
                    lastSearchIndex = -1;
                }
            });

            deleteBtn.addActionListener((e) -> {
               int index = expenseTable.getSelectedRow();
               if(index > -1) {
                   String id = (String) tableModel.getValueAt(index, 0);
                   try {
                       dbHelper.deleteExpense(id);
                       tableModel.removeRow(index);
                   }
                   catch (SQLException ex) {
                       AlertBox.show("Some database error occurred!");
                       ex.printStackTrace();
                   }
               }
               else {
                   AlertBox.show("Select a row to delete record!");
               }
            });

            fieldsPanel.add(new JLabel("Name"));
            fieldsPanel.add(nameField);
            fieldsPanel.add(new JLabel("Date"));
            fieldsPanel.add(dateField);
            fieldsPanel.add(searchByNameBtn);
            fieldsPanel.add(searchByDateBtn);
            fieldsPanel.add(deleteBtn);

            expenseTable = new JTable(tableModel);

            tableModel.addColumn("Id");
            tableModel.addColumn("Name");
            tableModel.addColumn("Date");
            tableModel.addColumn("Amount");
            tableModel.addColumn("Description");

            JScrollPane scrollPane = new JScrollPane(expenseTable);

            add(fieldsPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void fillTable() {
            expenseTable.removeAll();
            tableModel.setRowCount(0);
            try {
                ResultSet resultSet = dbHelper.getAllExpenses();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    String cnic = resultSet.getString("date");
                    String phone = resultSet.getString("amount");
                    String address = resultSet.getString("description");
                    tableModel.addRow(new Object[]{id, name, cnic, phone, address});
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred.");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private class ViewSalariesPanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable salariesTable;

        ViewSalariesPanel() {
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

            JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField monthField = new JTextField(20);
            JTextField yearField = new JTextField(20);
            JButton searchBtn = new JButton("Search");
            searchBtn.addActionListener((e) -> {
                String month = monthField.getText().toLowerCase();
                String year = yearField.getText().toLowerCase();

                if(month.length() > 0 && year.length() > 0) {
                    salariesTable.removeAll();
                    tableModel.setRowCount(0);

                    try {
                        ResultSet resultSet = dbHelper.getSalaries(month, year);
                        while (resultSet.next()) {
                            String date = resultSet.getString("date");
                            String name = resultSet.getString("name");
                            String salary = resultSet.getString("salary");
                            String advanceReturn = resultSet.getString("advanceReturn");

                            tableModel.addRow(new Object[]{date, name, salary, advanceReturn});
                        }
                    }
                    catch (SQLException ex) {
                        AlertBox.show("Some database error occurred!");
                        ex.printStackTrace();
                        System.exit(0);
                    }
                }
                else {
                    AlertBox.show("All fields with a * are required!");
                }
            });

            fieldsPanel.add(new JLabel("*Month"));
            fieldsPanel.add(monthField);
            fieldsPanel.add(new JLabel("*Year"));
            fieldsPanel.add(yearField);
            fieldsPanel.add(searchBtn);

            salariesTable = new JTable(tableModel);
            tableModel.addColumn("Date");
            tableModel.addColumn("Employee Name");
            tableModel.addColumn("Salary");
            tableModel.addColumn("Advance Return");

            add(fieldsPanel, BorderLayout.NORTH);
            add(new JScrollPane(salariesTable), BorderLayout.CENTER);
        }
    }
}

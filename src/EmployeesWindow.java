import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

class EmployeesWindow extends Activity {

    private DBHelper dbHelper;

    EmployeesWindow() {
        super();
        dbHelper = DBHelper.getDBHelper();
        initComponents();
    }

    private void initComponents() {
        setActivityName("Employees");
        setDefaultCloseOperation(Activity.DISPOSE_ON_CLOSE);

        AddEmpPanel addEmpPanel = new AddEmpPanel();
        EmpSalaryPanel empSalaryPanel = new EmpSalaryPanel();
        ViewEmpPanel viewEmpPanel = new ViewEmpPanel();
        EmpAdvancesPanel empAdvancesPanel = new EmpAdvancesPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tabbedPane.addChangeListener((e) -> {
            empSalaryPanel.fillTable();
            viewEmpPanel.fillTable();
        });

        tabbedPane.add(addEmpPanel, "Add Employee");
        tabbedPane.add(empSalaryPanel, "Employee Salary");
        tabbedPane.add(viewEmpPanel, "View Employees");
        tabbedPane.add(empAdvancesPanel, "Employees Advances");

        addView(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private class AddEmpPanel extends JPanel {

        AddEmpPanel() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel7 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JTextField nameField = new JTextField(20);
            panel1.add(new JLabel("*Name"));
            panel1.add(nameField);

            JTextField cnicField = new JTextField(20);
            panel2.add(new JLabel("CNIC"));
            panel2.add(cnicField);

            JTextField phoneField = new JTextField(20);
            panel3.add(new JLabel("Phone"));
            panel3.add(phoneField);

            JTextField addressField = new JTextField(20);
            panel4.add(new JLabel("Address"));
            panel4.add(addressField);

            JTextField dateField = new JTextField(20);
            panel5.add(new JLabel("Date of joining"));
            panel5.add(dateField);

            JTextField salaryField = new JTextField(20);
            panel6.add(new JLabel("Salary (RS)"));
            panel6.add(salaryField);

            JButton addEmpBtn = new JButton("Add Employee");
            panel7.add(addEmpBtn);
            addEmpBtn.addActionListener((e) -> {
                String name = nameField.getText();
                String cnic = cnicField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();
                String date = dateField.getText();
                String salary = salaryField.getText();

                // Some validation
                if(name.length() > 0) {
                    if(salary.trim().length() == 0) {
                        salary = "0";
                    }
                    else if(!Utilities.isInteger(salary)) {
                        AlertBox.show("Salary must be a number.");
                        return;
                    }

                    try {
                        dbHelper.insertEmployee(name, cnic, phone, address, date, salary);
                        AlertBox.show("Employee Added Successfully.");
                        nameField.setText("");
                        cnicField.setText("");
                        phoneField.setText("");
                        addressField.setText("");
                        dateField.setText("");
                        salaryField.setText("");
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
            });

            add(panel1);
            add(panel2);
            add(panel3);
            add(panel4);
            add(panel5);
            add(panel6);
            add(panel7);
        }
    }

    private class EmpSalaryPanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable empTable;
        private int lastSearchIndex;

        EmpSalaryPanel() {
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
            JTextField cnicField = new JTextField(20);
            JTextField salaryField = new JTextField(20);
            JTextField advanceReturnField = new JTextField(20);
            JButton searchByNameBtn = new JButton("Search By Name");
            JButton searchByCnicBtn = new JButton("Search By CNIC");
            JButton doneBtn = new JButton("Done");
            salaryField.setEnabled(false);

            searchByNameBtn.addActionListener((e) -> {
                String nameToSearch = nameField.getText().toLowerCase();

                if(nameToSearch.length() == 0)
                    return;

                boolean found = false;
                String name;
                for(int i = lastSearchIndex + 1; i < tableModel.getRowCount(); i++) {
                    name = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
                    if(name.contains(nameToSearch) || nameToSearch.contains(name)) {
                        empTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    empTable.clearSelection();
                    lastSearchIndex = -1;
                }
            });

            searchByCnicBtn.addActionListener((e) -> {
                String cniToSearch = cnicField.getText().toLowerCase();

                if(cniToSearch.length() == 0)
                    return;

                boolean found = false;
                String cnic;
                for(int i = lastSearchIndex + 1; i < tableModel.getRowCount(); i++) {
                    cnic = ((String) tableModel.getValueAt(i, 2)).toLowerCase();

                    if(cnic.length() == 0)
                        continue;

                    if(cnic.contains(cniToSearch) || cniToSearch.contains(cnic)) {
                        empTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    empTable.clearSelection();
                    lastSearchIndex = -1;
                }
            });

            doneBtn.addActionListener((e) -> {
                String salary = salaryField.getText();
                String advanceReturn = advanceReturnField.getText();

                int i = empTable.getSelectedRow();

                if(i == -1) {
                    AlertBox.show("Must select an employee first!");
                    return;
                }

                if(Utilities.isInteger(salary) && Utilities.isInteger(advanceReturn)) {
                    String id = (String) tableModel.getValueAt(i, 0);
                    new DatePicker((day, month, year) -> {
                        String date = year + "-" + month + "-" + day;
                        try {
                            dbHelper.recordSalary(id, date, salary, advanceReturn);
                            dbHelper.decrementAdvance(id, advanceReturn);
                            if(Integer.parseInt(advanceReturn) > 0) {
                                dbHelper.insertAdvanceRecord(id, date, "0", advanceReturn);
                            }

                            int currentAdvance = Integer.parseInt((String) tableModel.getValueAt(i, 7));
                            int remainingAdvance = currentAdvance - Integer.parseInt(advanceReturn);

                            tableModel.setValueAt(Integer.toString(remainingAdvance), i, 7);

                            AlertBox.show("Salary recorded successfully.");
                            salaryField.setText("");
                            advanceReturnField.setText("");
                        }
                        catch (Exception ex) {
                            AlertBox.show("Some database error occured!");
                            ex.printStackTrace();
                            System.exit(0);
                        }
                    });
                }
                else {
                    AlertBox.show("Salary and Advance returns must be numbers!");
                }
            });

            fieldsPanel.add(new JLabel("Name"));
            fieldsPanel.add(nameField);
            fieldsPanel.add(new JLabel("CNIC"));
            fieldsPanel.add(cnicField);
            fieldsPanel.add(searchByNameBtn);
            fieldsPanel.add(searchByCnicBtn);
            fieldsPanel.add(new JLabel("     Salary"));
            fieldsPanel.add(salaryField);
            fieldsPanel.add(new JLabel("Advance Return"));
            fieldsPanel.add(advanceReturnField);
            fieldsPanel.add(doneBtn);

            empTable = new JTable(tableModel);
            empTable.getSelectionModel().addListSelectionListener((event) -> {
                int row = empTable.getSelectedRow();
                if(row > -1) {
                    String salary = empTable.getValueAt(row, 6).toString();
                    salaryField.setText(salary);
                }
            });

            tableModel.addColumn("Id");
            tableModel.addColumn("Name");
            tableModel.addColumn("CNIC");
            tableModel.addColumn("Phone");
            tableModel.addColumn("Address");
            tableModel.addColumn("Date Of Joining");
            tableModel.addColumn("Salary");
            tableModel.addColumn("Advance Taken");

            JScrollPane scrollPane = new JScrollPane(empTable);

            add(fieldsPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void fillTable() {
            empTable.removeAll();
            tableModel.setRowCount(0);
            try {
                ResultSet resultSet = dbHelper.getAllEmployees();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    String cnic = resultSet.getString("cnic");
                    String phone = resultSet.getString("phone");
                    String address = resultSet.getString("address");
                    String date = resultSet.getString("dateOfJoining");
                    String salary = resultSet.getString("salary");
                    String advance = resultSet.getString("advanceTaken");
                    tableModel.addRow(new Object[]{id, name, cnic, phone, address, date, salary, advance});
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred.");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private class ViewEmpPanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable empTable;
        private int lastSearchIndex;

        ViewEmpPanel() {
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col != 0 && col != 7;
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
            JTextField cnicField = new JTextField(20);
            JButton searchByNameBtn = new JButton("Search By Name");
            JButton searchByCnicBtn = new JButton("Search By CNIC");
            JButton updateBtn = new JButton("Update");
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
                        empTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    empTable.clearSelection();
                    lastSearchIndex = -1;
                }
            });

            searchByCnicBtn.addActionListener((e) -> {
                String cniToSearch = cnicField.getText().toLowerCase();

                if(cniToSearch.length() == 0)
                    return;

                boolean found = false;
                String cnic;
                for(int i = lastSearchIndex + 1; i < tableModel.getRowCount(); i++) {
                    cnic = ((String) tableModel.getValueAt(i, 2)).toLowerCase();

                    if(cnic.length() == 0)
                        continue;

                    if(cnic.contains(cniToSearch) || cniToSearch.contains(cnic)) {
                        empTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    empTable.clearSelection();
                    lastSearchIndex = -1;
                }
            });

            updateBtn.addActionListener((e) -> {
                int i = empTable.getSelectedRow();

                // If some row is selected
                if(i > -1) {
                    String id = (String) tableModel.getValueAt(i, 0);
                    String name = (String) tableModel.getValueAt(i, 1);
                    String cnic = (String) tableModel.getValueAt(i, 2);
                    String phone = (String) tableModel.getValueAt(i, 3);
                    String address = (String) tableModel.getValueAt(i, 4);
                    String date = (String) tableModel.getValueAt(i, 5);
                    String salary = (String) tableModel.getValueAt(i, 6);
                    String advance = (String) tableModel.getValueAt(i, 7);

                    // Some validation
                    if(name.length() > 0) {
                        if(advance.trim().length() == 0) {
                            advance = "0";
                        }
                        else if(!Utilities.isInteger(advance)) {
                            AlertBox.show("Advance must be a number.");
                            return;
                        }

                        try {
                            dbHelper.updateEmployee(id, name, cnic, phone, address, date, salary, advance);
                            AlertBox.show("Employee Updated Successfully.");
                            empTable.clearSelection();
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

            deleteBtn.addActionListener((e) -> {
                int index = empTable.getSelectedRow();
                if(index > -1) {
                    String id = (String) tableModel.getValueAt(index, 0);
                    try {
                        dbHelper.deleteEmployee(id);
                        tableModel.removeRow(index);
                        AlertBox.show("Employee record deleted successfully!");
                    }
                    catch (SQLException ex) {
                        AlertBox.show("Some database error occurred!");
                        ex.printStackTrace();
                    }
                }
                else {
                    AlertBox.show("Select a record to delete!");
                }
            });

            fieldsPanel.add(new JLabel("Name"));
            fieldsPanel.add(nameField);
            fieldsPanel.add(new JLabel("CNIC"));
            fieldsPanel.add(cnicField);
            fieldsPanel.add(searchByNameBtn);
            fieldsPanel.add(searchByCnicBtn);
            fieldsPanel.add(updateBtn);
            fieldsPanel.add(deleteBtn);

            empTable = new JTable(tableModel);

            tableModel.addColumn("Id");
            tableModel.addColumn("Name");
            tableModel.addColumn("CNIC");
            tableModel.addColumn("Phone");
            tableModel.addColumn("Address");
            tableModel.addColumn("Date Of Joining");
            tableModel.addColumn("Salary");
            tableModel.addColumn("Advance Taken");

            JScrollPane scrollPane = new JScrollPane(empTable);

            add(fieldsPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void fillTable() {
            empTable.removeAll();
            tableModel.setRowCount(0);
            try {
                ResultSet resultSet = dbHelper.getAllEmployees();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    String cnic = resultSet.getString("cnic");
                    String phone = resultSet.getString("phone");
                    String address = resultSet.getString("address");
                    String date = resultSet.getString("dateOfJoining");
                    String salary = resultSet.getString("salary");
                    String advance = resultSet.getString("advanceTaken");
                    tableModel.addRow(new Object[]{id, name, cnic, phone, address, date, salary, advance});
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred.");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private class EmpAdvancesPanel extends JPanel {

        private JComboBox<Employee> employeeComboBox;
        private DefaultTableModel tableModel;
        private JTable advancesTable;
        private JLabel totalAdvanceLabel;

        EmpAdvancesPanel() {
            employeeComboBox = new JComboBox<>();
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            getAllEmployees();
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel topCtrlsPanel = new JPanel();
            topCtrlsPanel.setLayout(new BoxLayout(topCtrlsPanel, BoxLayout.X_AXIS));

            JTextField advanceField = new JTextField(20);
            advanceField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                    if(!Character.isDigit(e.getKeyChar()))
                        e.consume();
                }
            });

            JButton recordAdvanceBtn = new JButton("Record");
            recordAdvanceBtn.addActionListener((e) -> {
                String amount = advanceField.getText();
                if(amount.length() == 0) {
                    AlertBox.show("All fields with * are required");
                }
                else {
                    new DatePicker((day, month, year) -> {
                        String date = year + "-" + month + "-" + day;
                        String empId = ((Employee) employeeComboBox.getSelectedItem()).getId();
                        try {
                            dbHelper.insertAdvanceRecord(empId, date, amount, "0");
                            dbHelper.incrementEmpAdvance(empId, amount);

                            AlertBox.show("Advance recorded successfully.");
                            advanceField.setText("");
                            fillTable(empId);
                        }
                        catch (SQLException ex) {
                            AlertBox.show("Some database error occurred!");
                            ex.printStackTrace();
                        }
                    });
                }
            });

            JPanel wrapperPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            wrapperPanel1.add(employeeComboBox);
            wrapperPanel1.add(new JLabel(" *Advance Amount"));
            wrapperPanel1.add(advanceField);
            wrapperPanel1.add(recordAdvanceBtn);

            JButton fetchReportBtn = new JButton("Fetch Report");
            fetchReportBtn.addActionListener((e) -> {
                String empId = ((Employee) employeeComboBox.getSelectedItem()).getId();
                fillTable(empId);
            });

            JPanel wrapperPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            wrapperPanel2.add(fetchReportBtn);

            topCtrlsPanel.add(wrapperPanel1);
            topCtrlsPanel.add(wrapperPanel2);

            advancesTable = new JTable(tableModel);
            tableModel.addColumn("Employee Name");
            tableModel.addColumn("Date");
            tableModel.addColumn("Advance Taken");
            tableModel.addColumn("Advance Returned");

            JPanel footerPanel = new JPanel();
            footerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            totalAdvanceLabel = new JLabel();
            totalAdvanceLabel.setFont(new Font(totalAdvanceLabel.getFont().getName(), Font.BOLD, 16));
            totalAdvanceLabel.setForeground(Color.decode("#57bf13"));
            footerPanel.add(totalAdvanceLabel);

            add(topCtrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(advancesTable), BorderLayout.CENTER);
            add(footerPanel, BorderLayout.SOUTH);
        }

        void fillTable(String empId) {
            try {
                advancesTable.removeAll();
                tableModel.setRowCount(0);

                ResultSet resultSet = dbHelper.getAdvancesRecord(empId);
                int totalAdvance = 0;
                while (resultSet.next()) {
                    String empName = resultSet.getString("name");
                    String date = resultSet.getString("date");
                    int advanceTaken = resultSet.getInt("advanceTaken");
                    int advanceReturned = resultSet.getInt("advanceReturned");
                    tableModel.addRow(new Object[]{empName, date, advanceTaken, advanceReturned});

                    totalAdvance += advanceTaken;
                    totalAdvance -= advanceReturned;
                }
                totalAdvanceLabel.setText("Total Advance : " + totalAdvance);
            }
            catch (SQLException ex) {
                AlertBox.show("Some database error occurred!");
                ex.printStackTrace();
            }
        }

        void getAllEmployees() {
            try {
                ResultSet resultSet = dbHelper.getAllEmployees();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    employeeComboBox.addItem(new Employee(id, name));
                }
            }
            catch (SQLException ex) {
                AlertBox.show("Some database error occurred!");
                ex.printStackTrace();
            }
        }
    }
}

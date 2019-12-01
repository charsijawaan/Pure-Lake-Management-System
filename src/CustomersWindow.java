import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

class CustomersWindow extends Activity {

    private DBHelper dbHelper;

    CustomersWindow() {
        super();
        dbHelper = DBHelper.getDBHelper();
        initComponents();
    }

    private void initComponents() {
        setActivityName("Customers");
        setDefaultCloseOperation(Activity.DISPOSE_ON_CLOSE);

        AddCustomerPanel addCustomerPanel = new AddCustomerPanel();
        ViewCustomerPanel viewCustomerPanel = new ViewCustomerPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tabbedPane.add(addCustomerPanel, "Add Customer");
        tabbedPane.add(viewCustomerPanel, "View Customers");
        tabbedPane.addChangeListener((e) -> viewCustomerPanel.fillTable());

        addView(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private class AddCustomerPanel extends JPanel {

        AddCustomerPanel() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));

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
            panel4.add(new JLabel("*Address"));
            panel4.add(addressField);

            JButton addCustomerBtn = new JButton("Add Customer");
            panel5.add(addCustomerBtn);
            addCustomerBtn.addActionListener((e) -> {
                String name = nameField.getText();
                String cnic = cnicField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();

                // Some validation
                if(name.length() > 0 && address.length() > 0) {
                    try {
                        dbHelper.insertCustomer(name, cnic, phone, address);
                        AlertBox.show("Customer Added Successfully.");
                        nameField.setText("");
                        cnicField.setText("");
                        phoneField.setText("");
                        addressField.setText("");
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
        }
    }

    private class ViewCustomerPanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable customerTable;
        private int lastSearchIndex;

        ViewCustomerPanel() {
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
            fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.X_AXIS));

            JTextField nameField = new JTextField(20);
            JTextField cnicField = new JTextField(20);
            JButton searchByNameBtn = new JButton("Search By Name");
            JButton searchByCnicBtn = new JButton("Search By CNIC");
            JButton updateBtn = new JButton("Update");

            searchByNameBtn.addActionListener((e) -> {
                String nameToSearch = nameField.getText().toLowerCase();

                if(nameToSearch.length() == 0)
                    return;

                boolean found = false;
                String name;
                for(int i = lastSearchIndex + 1; i < tableModel.getRowCount(); i++) {
                    name = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
                    if(name.contains(nameToSearch) || nameToSearch.contains(name)) {
                        customerTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    customerTable.clearSelection();
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
                        customerTable.setRowSelectionInterval(i, i);
                        lastSearchIndex = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    customerTable.clearSelection();
                    lastSearchIndex = -1;
                }
            });

            updateBtn.addActionListener((e) -> {
                int i = customerTable.getSelectedRow();

                // If some row is selected
                if(i > -1) {
                    String id = (String) tableModel.getValueAt(i, 0);
                    String name = (String) tableModel.getValueAt(i, 1);
                    String cnic = (String) tableModel.getValueAt(i, 2);
                    String phone = (String) tableModel.getValueAt(i, 3);
                    String address = (String) tableModel.getValueAt(i, 4);

                    // Some validation
                    if(name.length() > 0 && address.length() > 0) {
                        try {
                            dbHelper.updateCustomer(id, name, cnic, phone, address);
                            AlertBox.show("Customer Updated Successfully.");
                            customerTable.clearSelection();
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

            fieldsPanel.add(new JLabel("Name"));
            fieldsPanel.add(nameField);
            fieldsPanel.add(new JLabel("CNIC"));
            fieldsPanel.add(cnicField);
            fieldsPanel.add(searchByNameBtn);
            fieldsPanel.add(searchByCnicBtn);
            fieldsPanel.add(updateBtn);

            customerTable = new JTable(tableModel);

            tableModel.addColumn("Id");
            tableModel.addColumn("Name");
            tableModel.addColumn("CNIC");
            tableModel.addColumn("Phone");
            tableModel.addColumn("Address");

            JScrollPane scrollPane = new JScrollPane(customerTable);

            add(fieldsPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void fillTable() {
            customerTable.removeAll();
            tableModel.setRowCount(0);
            try {
                ResultSet resultSet = dbHelper.getAllCustomers();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    String cnic = resultSet.getString("cnic");
                    String phone = resultSet.getString("phone");
                    String address = resultSet.getString("address");
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
}

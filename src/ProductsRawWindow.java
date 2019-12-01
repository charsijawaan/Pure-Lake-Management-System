import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

class ProductsRawWindow extends Activity {

    private DBHelper dbHelper;

    ProductsRawWindow() {
        super();
        dbHelper = DBHelper.getDBHelper();
        initComponents();
    }

    private void initComponents() {
        setActivityName("Products/Raw Materials");
        setDefaultCloseOperation(Activity.DISPOSE_ON_CLOSE);

        AddProductPanel addProductPanel = new AddProductPanel();
        ViewProductsPanel viewProductsPanel = new ViewProductsPanel();
        AddRawMaterialPanel addRawMaterialPanel = new AddRawMaterialPanel();
        ViewRawMaterialPanel viewRawMaterialPanel = new ViewRawMaterialPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tabbedPane.add(addProductPanel, "Add Product");
        tabbedPane.add(viewProductsPanel, "View Products");
        tabbedPane.add(addRawMaterialPanel, "Add Raw Material");
        tabbedPane.add(viewRawMaterialPanel, "View Raw Materials");
        tabbedPane.addChangeListener((e) -> {
            viewProductsPanel.fillTable();
            viewRawMaterialPanel.fillTable();
        });

        addView(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private class AddProductPanel extends JPanel {

        AddProductPanel() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JTextField volumeField = new JTextField(20);
            panel1.add(new JLabel("*Volume"));
            panel1.add(volumeField);

            JTextArea descriptionField = new JTextArea(6, 30);
            panel2.add(new JLabel("Description"));
            panel2.add(descriptionField);

            JComboBox<String> categoryList = new JComboBox<>(Constants.QUALITIES);
            panel3.add(new JLabel("Category"));
            panel3.add(categoryList);

            JTextField quantityField = new JTextField(20);
            panel4.add(new JLabel("*Quantity in stock"));
            panel4.add(quantityField);

            JButton addCustomerBtn = new JButton("Add Product");
            panel5.add(addCustomerBtn);
            addCustomerBtn.addActionListener((e) -> {
                String volume = volumeField.getText();
                String description = descriptionField.getText();
                String category = (String) categoryList.getSelectedItem();
                String stock = quantityField.getText();

                // Some validation
                if(volume.length() > 0 && stock.length() > 0) {
                    try {
                        dbHelper.insertProduct(volume, description, category, stock);
                        AlertBox.show("Product Added Successfully.");
                        volumeField.setText("");
                        descriptionField.setText("");
                        categoryList.setSelectedIndex(0);
                        quantityField.setText("");
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

    private class ViewProductsPanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable productTable;

        ViewProductsPanel() {
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col != 0;
                }
            };

            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel fieldsPanel = new JPanel();
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.X_AXIS));

            JButton updateBtn = new JButton("Update");

            updateBtn.addActionListener((e) -> {
                int i = productTable.getSelectedRow();

                // If some row is selected
                if(i > -1) {
                    String id = (String) tableModel.getValueAt(i, 0);
                    String volume = (String) tableModel.getValueAt(i, 1);
                    String description = (String) tableModel.getValueAt(i, 2);
                    String category = (String) tableModel.getValueAt(i, 3);
                    String stock = (String) tableModel.getValueAt(i, 4);

                    // Some validation
                    if(volume.length() > 0 && stock.length() > 0) {

                        if(!Utilities.isInteger(stock)) {
                            AlertBox.show("Stock must be a number.");
                            return;
                        }

                        if(!Utilities.isValidCategory(category)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(String quality : Constants.QUALITIES) {
                                quality = quality + ", ";
                                stringBuilder.append(quality);
                            }
                            AlertBox.show("Not a valid category. Valid categories are " + stringBuilder.toString());
                            return;
                        }

                        try {
                            dbHelper.updateProduct(id, volume, description, category, stock);
                            AlertBox.show("Product Updated Successfully.");
                            productTable.clearSelection();
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

            fieldsPanel.add(Box.createHorizontalGlue());
            fieldsPanel.add(updateBtn);

            productTable = new JTable(tableModel);

            tableModel.addColumn("Id");
            tableModel.addColumn("Volume");
            tableModel.addColumn("Description");
            tableModel.addColumn("Category");
            tableModel.addColumn("Stock");

            JScrollPane scrollPane = new JScrollPane(productTable);
            add(fieldsPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void fillTable() {
            productTable.removeAll();
            tableModel.setRowCount(0);
            try {
                ResultSet resultSet = dbHelper.getAllProducts();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String volume = resultSet.getString("volume");
                    String description = resultSet.getString("description");
                    String category = resultSet.getString("category");
                    String stock = resultSet.getString("stock");
                    tableModel.addRow(new Object[]{id, volume, description, category, stock});
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred.");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private class AddRawMaterialPanel extends JPanel {

        AddRawMaterialPanel() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel panel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JTextField volumeField = new JTextField(20);
            panel1.add(new JLabel("*Volume"));
            panel1.add(volumeField);

            JTextArea descriptionField = new JTextArea(6, 30);
            panel2.add(new JLabel("Description"));
            panel2.add(descriptionField);

            JComboBox<String> categoryList = new JComboBox<>(Constants.QUALITIES);
            panel3.add(new JLabel("Category"));
            panel3.add(categoryList);

            JTextField quantityField = new JTextField(20);
            panel4.add(new JLabel("*Quantity in stock"));
            panel4.add(quantityField);

            JButton addCustomerBtn = new JButton("Add Raw Material");
            panel5.add(addCustomerBtn);
            addCustomerBtn.addActionListener((e) -> {
                String volume = volumeField.getText();
                String description = descriptionField.getText();
                String category = (String) categoryList.getSelectedItem();
                String stock = quantityField.getText();

                // Some validation
                if(volume.length() > 0 && stock.length() > 0) {
                    try {
                        dbHelper.insertRawMaterial(volume, description, category, stock);
                        AlertBox.show("Raw Material Added Successfully.");
                        volumeField.setText("");
                        descriptionField.setText("");
                        categoryList.setSelectedIndex(0);
                        quantityField.setText("");
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

    private class ViewRawMaterialPanel extends JPanel {

        private DefaultTableModel tableModel;
        private JTable rawMaterialTable;

        ViewRawMaterialPanel() {
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col != 0;
                }
            };

            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel fieldsPanel = new JPanel();
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.X_AXIS));

            JButton updateBtn = new JButton("Update");

            updateBtn.addActionListener((e) -> {
                int i = rawMaterialTable.getSelectedRow();

                // If some row is selected
                if(i > -1) {
                    String id = (String) tableModel.getValueAt(i, 0);
                    String volume = (String) tableModel.getValueAt(i, 1);
                    String description = (String) tableModel.getValueAt(i, 2);
                    String category = (String) tableModel.getValueAt(i, 3);
                    String stock = (String) tableModel.getValueAt(i, 4);

                    // Some validation
                    if(volume.length() > 0 && stock.length() > 0) {

                        if(!Utilities.isInteger(stock)) {
                            AlertBox.show("Stock must be a number.");
                            return;
                        }

                        if(!Utilities.isValidCategory(category)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(String quality : Constants.QUALITIES) {
                                quality = quality + ", ";
                                stringBuilder.append(quality);
                            }
                            AlertBox.show("Not a valid category. Valid categories are " + stringBuilder.toString());
                            return;
                        }

                        try {
                            dbHelper.updateRawMaterial(id, volume, description, category, stock);
                            AlertBox.show("Raw Material Updated Successfully.");
                            rawMaterialTable.clearSelection();
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

            fieldsPanel.add(Box.createHorizontalGlue());
            fieldsPanel.add(updateBtn);

            rawMaterialTable = new JTable(tableModel);

            tableModel.addColumn("Id");
            tableModel.addColumn("Volume");
            tableModel.addColumn("Description");
            tableModel.addColumn("Category");
            tableModel.addColumn("Stock");

            JScrollPane scrollPane = new JScrollPane(rawMaterialTable);
            add(fieldsPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void fillTable() {
            rawMaterialTable.removeAll();
            tableModel.setRowCount(0);
            try {
                ResultSet resultSet = dbHelper.getAllRawMaterials();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String volume = resultSet.getString("volume");
                    String description = resultSet.getString("description");
                    String category = resultSet.getString("category");
                    String stock = resultSet.getString("stock");
                    tableModel.addRow(new Object[]{id, volume, description, category, stock});
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

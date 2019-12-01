import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class SalesWindow extends Activity {

    private ViewSalesPanel viewSalesPanel;
    private ArrayList<Customer> customersList;
    private DBHelper dbHelper;

    SalesWindow() {
        super();
        dbHelper = DBHelper.getDBHelper();
        customersList = new ArrayList<>();
        getAllCustomers();
        initComponents();
    }

    private void initComponents() {
        setActivityName("Sales");
        setDefaultCloseOperation(Activity.DISPOSE_ON_CLOSE);

        RecordSalePanel recordSalePanel = new RecordSalePanel();
        viewSalesPanel = new ViewSalesPanel();
        RecievablesPanel recievablesPanel = new RecievablesPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tabbedPane.add(recordSalePanel, "Record Sale");
        tabbedPane.add(viewSalesPanel, "View Sales");
        tabbedPane.add(recievablesPanel, "Recievables");
        tabbedPane.addChangeListener((e) -> viewSalesPanel.fillTable());

        addView(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void getAllCustomers() {
        try {
            ResultSet resultSet = dbHelper.getAllCustomers();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");

                Customer customer = new Customer(id, name);
                customersList.add(customer);
            }
        }
        catch (SQLException e) {
            AlertBox.show("Some database error occurred!");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private class RecordSalePanel extends JPanel {

        private ArrayList<Product> productList;
        private ArrayList<FormFieldObject> fieldObjects;
        private JLabel totalLabel;
        private JTextField amountRecievedField;
        private String billNumber;
        private int totalBill;

        RecordSalePanel() {
            productList = new ArrayList<>();
            fieldObjects = new ArrayList<>();
            totalLabel = new JLabel("Total bill : 0");
            totalLabel.setFont(new Font(totalLabel.getFont().getName(), Font.BOLD, 16));
            totalLabel.setForeground(Color.decode("#57bf13"));
            totalBill = 0;
            billNumber = "";
            getAllProducts();
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

            JTextField billNumberField = new JTextField(20);
            billNumberField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                    char ch = e.getKeyChar();
                    if(!Character.isDigit(ch))
                        e.consume();
                }
            });

            JComboBox<Customer> customerComboBox = new JComboBox<>();
            for(Customer customer : customersList) {
                customerComboBox.addItem(customer);
            }

            JButton addSubSaleBtn = new JButton("Add Product");
            addSubSaleBtn.addActionListener((e) -> {
                JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                JComboBox<Product> productComboBox = new JComboBox<>();
                for(Product product : productList) {
                    productComboBox.addItem(product);
                }

                JTextField priceField = new JTextField(20);
                priceField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        super.keyTyped(e);
                        char keyChar = e.getKeyChar();

                        if(!Character.isDigit(keyChar))
                            e.consume();
                    }
                });
                JTextField quantityField = new JTextField(20);
                quantityField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        super.keyTyped(e);
                        char keyChar = e.getKeyChar();

                        if(!Character.isDigit(keyChar))
                            e.consume();
                    }
                });

                JButton addItemBtn = new JButton("Done");
                addItemBtn.addActionListener(new ActionListener() {

                    private JTextField btnPriceField = priceField,
                            btnQuantityField = quantityField;
                    private JButton btn = addItemBtn;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(btnPriceField.getText().length() ==0 || btnQuantityField.getText().length() == 0) {
                            AlertBox.show("All fields with a * are required!");
                            return;
                        }

                        int price = Integer.parseInt(btnPriceField.getText());
                        int quantity = Integer.parseInt(btnQuantityField.getText());

                        totalBill += price * quantity;
                        totalLabel.setText("Total bill : " + totalBill);

                        btn.setEnabled(false);
                    }
                });

                fieldsPanel.add(new JLabel("Product"));
                fieldsPanel.add(productComboBox);
                fieldsPanel.add(new JLabel("*Price"));
                fieldsPanel.add(priceField);
                fieldsPanel.add(new JLabel("*Quantity"));
                fieldsPanel.add(quantityField);
                fieldsPanel.add(addItemBtn);

                FormFieldObject fieldObject = new FormFieldObject();
                fieldObject.productJComboBox = productComboBox;
                fieldObject.priceField = priceField;
                fieldObject.quantityField = quantityField;
                fieldObject.addItemBtn = addItemBtn;
                fieldObjects.add(fieldObject);

                formPanel.add(fieldsPanel);
                formPanel.repaint();
                formPanel.revalidate();
            });

            JButton doneBtn = new JButton("Make Bill");
            doneBtn.addActionListener((e) -> {

                if(fieldObjects.isEmpty()) {
                    return;
                }

                billNumber = billNumberField.getText();
                String amountRecieved = amountRecievedField.getText();
                if(amountRecieved.length() == 0) {
                    AlertBox.show("All fields with a * are required!");
                    return;
                }
                else {
                    if(Integer.parseInt(amountRecieved) > totalBill) {
                        AlertBox.show("Can not be greater than total bill!");
                        return;
                    }
                }

                if(billNumber.length() == 0) {
                    billNumber = "0";
                }

                Customer customer = (Customer) customerComboBox.getSelectedItem();
                String customerId;

                if(customer != null) {
                    customerId = customer.getId();
                }
                else {
                    AlertBox.show("Some unknown problem occurred!");
                    return;
                }

                new DatePicker((day, month, year) -> {
                    String date = year + "-" + month + "-" + day;
                    try {
                        String saleId = dbHelper.insertSale(date, billNumber, customerId, totalBill, amountRecieved);

                        for(FormFieldObject fieldObject : fieldObjects) {
                            JComboBox<Product> productJComboBox = fieldObject.productJComboBox;
                            JTextField priceField = fieldObject.priceField;
                            JTextField quantityField = fieldObject.quantityField;

                            String productId = ((Product) productJComboBox.getSelectedItem()).getId();
                            String price = priceField.getText();
                            String quantitySold = quantityField.getText();

                            if(price.length() == 0 || quantitySold.length() == 0) {
                                continue;
                            }

                            dbHelper.insertSubSale(saleId, productId, price, quantitySold);
                        }

                        AlertBox.show("Sale Recorded Successfully.");
                        fieldObjects.clear();
                        formPanel.removeAll();
                        formPanel.repaint();
                        formPanel.revalidate();

                        totalBill = 0;
                        totalLabel.setText("Total bill : 0");
                        amountRecievedField.setText("");
                        billNumberField.setText("");
                        billNumber = "";
                    }
                    catch (SQLException ex) {
                        AlertBox.show("Some database error occurred!");
                        ex.printStackTrace();
                        System.exit(0);
                    }
                });
            });

            JButton clearBillBtn = new JButton("Reset Bill");
            clearBillBtn.addActionListener((e) -> {
                for(FormFieldObject fieldObject : fieldObjects) {
                    fieldObject.addItemBtn.setEnabled(true);
                }
                totalBill = 0;
                totalLabel.setText("Total bill : 0");
            });

            JPanel ctrlsWrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            ctrlsWrapperPanel.add(new JLabel("Bill Number"));
            ctrlsWrapperPanel.add(billNumberField);
            ctrlsWrapperPanel.add(customerComboBox);
            ctrlsWrapperPanel.add(addSubSaleBtn);

            JPanel topCtrlsPanel =  new JPanel();
            topCtrlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            topCtrlsPanel.setLayout(new BoxLayout(topCtrlsPanel, BoxLayout.X_AXIS));
            topCtrlsPanel.add(ctrlsWrapperPanel);
            topCtrlsPanel.add(Box.createHorizontalGlue());
            topCtrlsPanel.add(clearBillBtn);

            JPanel footerPanel = new JPanel();
            footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.X_AXIS));

            amountRecievedField = new JTextField(20);
            amountRecievedField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);

                    char ch = e.getKeyChar();
                    if(!Character.isDigit(ch)) {
                        e.consume();
                    }
                }
            });

            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            wrapper.add(new JLabel("*Amount Recieved : "));
            wrapper.add(amountRecievedField);
            wrapper.add(doneBtn);

            footerPanel.add(totalLabel);
            footerPanel.add(Box.createHorizontalGlue());
            footerPanel.add(wrapper);

            add(topCtrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(formPanel), BorderLayout.CENTER);
            add(footerPanel, BorderLayout.SOUTH);
        }

        private void getAllProducts() {
            try {
                ResultSet resultSet = dbHelper.getAllProducts();
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String volume = resultSet.getString("volume");
                    String category = resultSet.getString("category");
                    int quantityInStock = resultSet.getInt("stock");

                    Product product = new Product(id, volume, category, quantityInStock);
                    productList.add(product);
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred!");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private class ViewSalesPanel extends JPanel {

        GridBagConstraints gbc;
        private JPanel tablesPanel;
        private ArrayList<JLabel> labels;
        private JScrollPane scrollPane;

        ViewSalesPanel() {
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            tablesPanel = new JPanel();
            labels = new ArrayList<>();
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            tablesPanel.setLayout(new GridBagLayout());
            scrollPane = new JScrollPane(tablesPanel);

            JPanel topCtrlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton searchByDateBtn = new JButton("Search By Date");
            searchByDateBtn.addActionListener((e) -> new DatePicker(((day, month, year) -> {
                String date = year + "-" + month + "-" + day;
                System.out.println(date);
                for(JLabel label : labels) {
                    if(label.getText().contains(date)) {
                        tablesPanel.scrollRectToVisible(new Rectangle(0,
                                (int) label.getParent().getParent().getLocation().getY() + 400, 1, 1));
                        System.out.println(label.getParent().getParent().getLocation().getY());
                        break;
                    }
                }
            })));

            JTextField billNumberField = new JTextField(20);
            billNumberField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                    if(!Character.isDigit(e.getKeyChar()))
                        e.consume();
                }
            });
            JButton searchByBillBtn = new JButton("Search By Bill");
            searchByBillBtn.addActionListener((e) ->  {
                String billNumber = billNumberField.getText();
                if(billNumber.length() == 0) {
                    AlertBox.show("Bill number is required!");
                    return;
                }

                for(JLabel label : labels) {
                    if (label.getText().contains("Bill No. = " + billNumber)) {
                        tablesPanel.scrollRectToVisible(new Rectangle(0,
                                (int) label.getParent().getParent().getLocation().getY() + 400, 1, 1));
                        break;
                    }
                }
            });
            topCtrlsPanel.add(new JLabel("Bill Number : "));
            topCtrlsPanel.add(billNumberField);
            topCtrlsPanel.add(searchByBillBtn);
            topCtrlsPanel.add(searchByDateBtn);

            add(scrollPane, BorderLayout.CENTER);
            add(topCtrlsPanel, BorderLayout.NORTH);
        }

        private void fillTable() {
            tablesPanel.removeAll();
            tablesPanel.repaint();
            tablesPanel.revalidate();

            try {
                ResultSet resultSet = dbHelper.getAllSales();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String billNumber = resultSet.getString("billNumber");
                    String dateTime = resultSet.getString("dateTime");
                    String customerName = resultSet.getString("customerName");
                    int totalBill = resultSet.getInt("totalBill");
                    int amountRecieved = resultSet.getInt("amountRecieved");

                    JPanel subTablePanel = new JPanel();
                    subTablePanel.setLayout(new BorderLayout());
                    subTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

                    JLabel infoLabel = new JLabel("Bill No. = " + billNumber + ". Sold to " + customerName + " on " + dateTime);
                    infoLabel.setFont(new Font(infoLabel.getFont().getName(), Font.BOLD, 14));
                    labels.add(infoLabel);

                    JButton printInvoiceBtn = new JButton("Print");
                    JButton deleteSaleBtn = new JButton("Delete");
                    JButton updateBtn = new JButton("Update");
                    deleteSaleBtn.setActionCommand(id);
                    updateBtn.setActionCommand(id);

                    JPanel saleInfoPanel = new JPanel();
                    saleInfoPanel.setLayout(new BoxLayout(saleInfoPanel, BoxLayout.X_AXIS));
                    saleInfoPanel.add(infoLabel);
                    saleInfoPanel.add(Box.createHorizontalGlue());
                    saleInfoPanel.add(printInvoiceBtn);
                    saleInfoPanel.add(deleteSaleBtn);
                    saleInfoPanel.add(updateBtn);

                    DefaultTableModel tableModel = new DefaultTableModel() {
                        @Override
                        public boolean isCellEditable(int row, int col) {
                            return (row != this.getRowCount() - 1 && col != 3);
                        }
                    };
                    JTable subTable = new JTable(tableModel);
                    updateBtn.addActionListener(new UpdateSaleListener(subTable));

                    tableModel.addColumn("Product");
                    tableModel.addColumn("Price");
                    tableModel.addColumn("Quantity");
                    tableModel.addColumn("Sub Total");
                    tableModel.addColumn("Row Id");

                    JScrollPane scrollPane = new JScrollPane(subTable);
                    scrollPane.setPreferredSize(new Dimension(500, 180));

                    int total = 0;
                    ResultSet resultSet2 = dbHelper.getSubSale(id);
                    while (resultSet2.next()) {
                        String volume = resultSet2.getString("volume");
                        String price = resultSet2.getString("price");
                        String quantity = resultSet2.getString("quantitySold");
                        String rowId = resultSet2.getString("rowid");
                        int subTotal = Integer.valueOf(price) * Integer.valueOf(quantity);
                        total += subTotal;

                        tableModel.addRow(new Object[]{volume, price, quantity, subTotal, rowId});
                    }
                    tableModel.addRow(new Object[]{"", "", "", total});

                    JPanel billingInfoPanel = new JPanel();
                    billingInfoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

                    JLabel billingLabel = new JLabel();
                    billingLabel.setText("Amount Recieved = " + amountRecieved + ", Remaining Amount = "
                            + (totalBill - amountRecieved));

                    billingInfoPanel.add(billingLabel);

                    subTablePanel.add(saleInfoPanel, BorderLayout.NORTH);
                    subTablePanel.add(scrollPane, BorderLayout.CENTER);
                    subTablePanel.add(billingInfoPanel, BorderLayout.SOUTH);
                    subTable.revalidate();

                    printInvoiceBtn.addActionListener((e) -> printInvoice(dateTime,
                            billNumber, totalBill, amountRecieved, customerName, tableModel));
                    deleteSaleBtn.addActionListener((e) -> {
                        String saleId = e.getActionCommand();
                        try {
                            dbHelper.deleteSale(saleId);
                            // Remove the sale from the view sales panel
                            tablesPanel.removeAll();
                            fillTable();
                        }
                        catch (SQLException ex) {
                            AlertBox.show("Some database error occurred!");
                            ex.printStackTrace();
                        }
                    });

                    tablesPanel.add(subTablePanel, gbc);
                    gbc.gridy++;
                }
                tablesPanel.revalidate();
                scrollPane.revalidate();
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred.");
                e.printStackTrace();
                System.exit(0);
            }
        }

        private void printInvoice(String dateTime, String billNumber, int totalBill,
                                  int amountRecieved, String customerName, DefaultTableModel tableModel) {
            InvoicePrinter.printInvoice(InvoicePrinter.SALE_INVOICE, billNumber, totalBill,
                    amountRecieved, dateTime, customerName, tableModel);
        }
    }

    private class UpdateSaleListener implements ActionListener {

        private JTable bindTable;

        UpdateSaleListener(JTable bindTable) {
            this.bindTable = bindTable;
        }

        public void actionPerformed(ActionEvent e) {
            String updateSaleId = e.getActionCommand();
            int row = bindTable.getSelectedRow();
            if(row > -1) {
                String rowId = (String) bindTable.getValueAt(row, 4);
                String product = (String) bindTable.getValueAt(row, 0);
                String volume = product.split("~")[0];
                String category = product.split("~")[1];
                int oldSubtotal = (Integer) bindTable.getValueAt(row, 3);
                int price, quantity, subTotal;
                try {
                    price = Integer.valueOf((String) bindTable.getValueAt(row, 1));
                    quantity = Integer.valueOf((String) bindTable.getValueAt(row, 2));
                    subTotal = price * quantity;
                    subTotal -= oldSubtotal;
                }
                catch (Exception ex) {
                    AlertBox.show("Price and Quantity must be numbers!");
                    ex.printStackTrace();
                    return;
                }

                try {
                    int productId = dbHelper.productExists(volume, category);
                    if(productId != -1) {
                        dbHelper.updateSale(rowId, productId, price, quantity);
                        dbHelper.updateSaleTotalBill(updateSaleId, subTotal);
                        AlertBox.show("Sale record updated successfully.");
                        viewSalesPanel.fillTable();
                    }
                    else {
                        AlertBox.show("This product doesn't exist in database!");
                    }
                }
                catch (SQLException ex) {
                    AlertBox.show("Some database error occurred!");
                    ex.printStackTrace();
                }
            }
            else {
                AlertBox.show("Select a record to update!");
            }
        }
    }

    private class FormFieldObject {
        private JComboBox<Product> productJComboBox;
        private JTextField priceField;
        private JTextField quantityField;
        private JButton addItemBtn;
    }

    private class RecievablesPanel extends JPanel {

        private String fromDate, toDate;
        private DefaultTableModel tableModel;
        private JTable recievablesTable;
        private JLabel totalRecievableLabel;
        private JComboBox<Customer> customerComboBox;
        private JRadioButton fetchAllRadio;
        private int totalRecievable;

        RecievablesPanel() {
            fromDate = "";
            toDate = "";
            totalRecievable = 0;
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

            JPanel topCtrlsPanel = new JPanel();
            topCtrlsPanel.setLayout(new BoxLayout(topCtrlsPanel, BoxLayout.X_AXIS));
            JPanel ctrlsWrapperPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel ctrlsWrapperPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            customerComboBox = new JComboBox<>();
            for(Customer customer : customersList) {
                customerComboBox.addItem(customer);
            }

            JButton fromDateBtn = new JButton("From Date");
            JTextField fromDateField = new JTextField(20);
            fromDateField.setEditable(false);
            fromDateField.setEnabled(false);
            fromDateBtn.addActionListener((e) -> new DatePicker((day, month, year) -> {
               fromDate = year + "-" + month + "-" + day;
               fromDateField.setText(fromDate);
            }));

            JButton toDateBtn = new JButton("To Date");
            JTextField toDateField = new JTextField(20);
            toDateField.setEditable(false);
            toDateField.setEnabled(false);
            toDateBtn.addActionListener((e) -> new DatePicker((day, month, year) -> {
                toDate = year + "-" + month + "-" + day;
                toDateField.setText(toDate);
            }));

            JButton recieveAmountBtn = new JButton("Recieve Amount");
            recieveAmountBtn.setEnabled(false);
            recieveAmountBtn.addActionListener((e) -> new InputFrame());

            JButton printReportBtn = new JButton("Print Report");
            printReportBtn.setEnabled(false);
            printReportBtn.addActionListener((e) -> printReport(((Customer) customerComboBox.getSelectedItem())));

            fetchAllRadio = new JRadioButton("Fetch All");
            fetchAllRadio.addActionListener((e) -> {
                if(fetchAllRadio.isSelected()) {
                    toDateBtn.setEnabled(false);
                    fromDateBtn.setEnabled(false);
                    toDateField.setText("");
                    fromDateField.setText("");
                }
                else {
                    toDateBtn.setEnabled(true);
                    fromDateBtn.setEnabled(true);
                }
            });

            JButton doneBtn = new JButton("Fetch Report");
            doneBtn.addActionListener((e) -> {
                if((!fromDate.isEmpty() && !toDate.isEmpty()) || fetchAllRadio.isSelected()) {
                    String customerId = ((Customer) customerComboBox.getSelectedItem()).getId();
                    int records = fillTable(customerId, fetchAllRadio.isSelected());
                    if(records > 0) {
                        recieveAmountBtn.setEnabled(true);
                        printReportBtn.setEnabled(true);
                    }
                }
                else {
                    AlertBox.show("Please enter \"From Date\" and \"To Date\" to proceed!");
                }
            });

            recievablesTable = new JTable(tableModel);
            tableModel.addColumn("id");
            tableModel.addColumn("Bill Number");
            tableModel.addColumn("Date");
            tableModel.addColumn("Total Bill");
            tableModel.addColumn("Amount Recieved");
            tableModel.addColumn("Remaining Recievable");
            tableModel.addColumn("Cheque Numbers");

            ctrlsWrapperPanel1.add(customerComboBox);
            ctrlsWrapperPanel1.add(fromDateBtn);
            ctrlsWrapperPanel1.add(fromDateField);
            ctrlsWrapperPanel1.add(toDateBtn);
            ctrlsWrapperPanel1.add(toDateField);
            ctrlsWrapperPanel1.add(doneBtn);
            ctrlsWrapperPanel1.add(fetchAllRadio);
            ctrlsWrapperPanel2.add(recieveAmountBtn);
            ctrlsWrapperPanel2.add(printReportBtn);

            topCtrlsPanel.add(ctrlsWrapperPanel1);
            topCtrlsPanel.add(Box.createHorizontalGlue());
            topCtrlsPanel.add(ctrlsWrapperPanel2);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            totalRecievableLabel = new JLabel("Total Bill : 0");
            totalRecievableLabel.setFont(new Font(totalRecievableLabel.getFont().getName(), Font.BOLD, 16));
            totalRecievableLabel.setForeground(Color.decode("#57bf13"));
            bottomPanel.add(totalRecievableLabel);

            add(topCtrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(recievablesTable), BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        void printReport(Customer customer) {
            InvoicePrinter.printReport(customer.getName(), tableModel, totalRecievable);
        }

        private int fillTable(String customerId, boolean fetchAll) {
            recievablesTable.removeAll();
            tableModel.setRowCount(0);
            try {
                int count = 0;
                totalRecievable = 0;
                ResultSet resultSet;
                if(fetchAll)
                    resultSet = dbHelper.fetchRecievablesReport(customerId);
                else
                    resultSet = dbHelper.fetchRecievablesReport(customerId, fromDate, toDate);

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String billNumber = resultSet.getString("billNumber");
                    String date = resultSet.getString("dateTime");
                    int totalBill = resultSet.getInt("totalBill");
                    int amountRecieved = resultSet.getInt("amountRecieved");
                    int remainingBill = resultSet.getInt("remainingBill");
                    String chequeNumbers = resultSet.getString("chequeNumber");
                    totalRecievable += remainingBill;
                    tableModel.addRow(new Object[]{id, billNumber, date, totalBill, amountRecieved, remainingBill, chequeNumbers});
                    count++;
                }
                totalRecievableLabel.setText("Total Bill : " + totalRecievable);
                return count;
            }
            catch (SQLException ex) {
                AlertBox.show("Some database error occurred!");
                ex.printStackTrace();
                return -1;
            }
        }

        private class InputFrame extends JFrame {

            InputFrame() {
                super("Recieve Amount");
                initComponents();
            }

            private void initComponents() {
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setResizable(false);
                setSize(400, 150);
                setLocationRelativeTo(null);
                setLayout(new BorderLayout());

                JTextField chequeNumberField = new JTextField(20);
                chequeNumberField.setEnabled(false);

                JRadioButton r1 = new JRadioButton("Cash");
                JRadioButton r2 = new JRadioButton("Cheque");
                r1.setSelected(true);
                r1.addActionListener((e) -> {
                    if(r1.isSelected()) {
                        chequeNumberField.setEnabled(false);
                    }
                });
                r2.addActionListener((e) -> {
                    if(r2.isSelected()) {
                        chequeNumberField.setEnabled(true);
                    }
                });

                ButtonGroup bg = new ButtonGroup();
                bg.add(r1);
                bg.add(r2);

                JPanel panel1 = new JPanel();
                panel1.add(r1);
                panel1.add(r2);

                JTextField amountField = new JTextField(20);
                amountField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        super.keyTyped(e);
                        if(!Character.isDigit(e.getKeyChar()))
                            e.consume();
                    }
                });

                JButton recievedBtn = new JButton("Recieved");
                recievedBtn.addActionListener((e) -> {
                    String amount, chequeNumber;
                    if(r1.isSelected()) {
                        amount = amountField.getText();
                        chequeNumber = null;
                    }
                    else {
                        amount = amountField.getText();
                        chequeNumber = chequeNumberField.getText();
                        if(chequeNumber.length() == 0)
                            chequeNumber = null;
                    }

                    if(amount.length() == 0) {
                        AlertBox.show("All fields with * are required!");
                        return;
                    }

                    int amountInt = Integer.parseInt(amount);
                    String[] salesIds = new String[recievablesTable.getRowCount()];
                    int[] amountsToAdd = new int[recievablesTable.getRowCount()];
                    int count = 0;
                    for(int i = 0; i < salesIds.length; i++) {
                        if(((Integer) tableModel.getValueAt(i, 5)) <= 0) {
                            continue;
                        }
                        count++;
                        salesIds[count - 1] = (String) tableModel.getValueAt(i, 0);
                        if(((Integer) tableModel.getValueAt(i, 5)) >= amountInt) {
                            amountsToAdd[count - 1] = amountInt;
                            break;
                        }
                        else {
                            amountsToAdd[count - 1] = (Integer) tableModel.getValueAt(i, 5);
                            amountInt -= (Integer) tableModel.getValueAt(i, 5);
                        }
                    }
                    adjustAmounts(salesIds, amountsToAdd, chequeNumber, count);
                    fillTable(((Customer) customerComboBox.getSelectedItem()).getId(), fetchAllRadio.isSelected());
                    this.dispose();
                });

                JPanel panel2 = new JPanel();
                panel2.add(new JLabel("*Amount"));
                panel2.add(amountField);
                panel2.add(recievedBtn);

                JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
                panel3.add(new JLabel("Cheque Number"));
                panel3.add(chequeNumberField);

                add(panel1, BorderLayout.NORTH);
                add(panel2, BorderLayout.CENTER);
                add(panel3, BorderLayout.SOUTH);

                setVisible(true);
                amountField.requestFocus();
            }

            private void adjustAmounts(String[] salesIds, int[] amountToAdd, String chequeNumber, int count) {
                try {
                    for(int i = 0; i < count; i++) {
                        dbHelper.recieveAmount(salesIds[i], amountToAdd[i], chequeNumber);
                    }
                }
                catch (SQLException e) {
                    AlertBox.show("Some database error occurred!");
                    e.printStackTrace();
                }
            }
        }
    }
}

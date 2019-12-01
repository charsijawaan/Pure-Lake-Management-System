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

class PurchasesWindow extends Activity {

    private ArrayList<Supplier> suppliersList;
    private DBHelper dbHelper;

    PurchasesWindow() {
        super();
        dbHelper = DBHelper.getDBHelper();
        suppliersList = new ArrayList<>();
        getAllSuppliers();
        initComponents();
    }

    private void initComponents() {
        setActivityName("Purchases");
        setDefaultCloseOperation(Activity.DISPOSE_ON_CLOSE);

        RecordPurchasesPanel recordPurchasesPanel = new RecordPurchasesPanel();
        ViewPurchasesPanel viewPurchasesPanel = new ViewPurchasesPanel();
        LiabilitiesPanel liabilitiesPanel = new LiabilitiesPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tabbedPane.add(recordPurchasesPanel, "Record Purchase");
        tabbedPane.add(viewPurchasesPanel, "View Purchases");
        tabbedPane.add(liabilitiesPanel, "Liabilities");
        tabbedPane.addChangeListener((e) -> viewPurchasesPanel.fillTable());

        addView(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void getAllSuppliers() {
        try {
            ResultSet resultSet = dbHelper.getAllSuppliers();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");

                Supplier supplier = new Supplier(id, name);
                suppliersList.add(supplier);
            }
        }
        catch (SQLException e) {
            AlertBox.show("Some database error occurred!");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private class RecordPurchasesPanel extends JPanel {

        private ArrayList<RawMaterial> materialsList;
        private ArrayList<Supplier> suppliersList;
        private ArrayList<FormFieldObject> fieldObjects;
        private JLabel totalLabel;
        private JTextField amountPayedField;
        private int totalBill;

        RecordPurchasesPanel() {
            materialsList = new ArrayList<>();
            suppliersList = new ArrayList<>();
            fieldObjects = new ArrayList<>();
            totalLabel = new JLabel("Total bill : 0");
            totalLabel.setFont(new Font(totalLabel.getFont().getName(), Font.BOLD, 15));
            totalLabel.setForeground(Color.decode("#57bf13"));
            totalBill = 0;
            getAllRawMaterials();
            getAllSuppliers();
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

            JComboBox<Supplier> suppliersComboBox = new JComboBox<>();
            for(Supplier supplier : suppliersList) {
                suppliersComboBox.addItem(supplier);
            }

            JButton addSubPurchaseBtn = new JButton("Add Raw Material");
            addSubPurchaseBtn.addActionListener((e) -> {
                JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                JComboBox<RawMaterial> rawMaterialsComboBox = new JComboBox<>();
                for(RawMaterial rawMaterial : materialsList) {
                    rawMaterialsComboBox.addItem(rawMaterial);
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

                fieldsPanel.add(new JLabel("Raw Material"));
                fieldsPanel.add(rawMaterialsComboBox);
                fieldsPanel.add(new JLabel("*Price"));
                fieldsPanel.add(priceField);
                fieldsPanel.add(new JLabel("*Quantity"));
                fieldsPanel.add(quantityField);
                fieldsPanel.add(addItemBtn);

                FormFieldObject fieldObject = new FormFieldObject();
                fieldObject.rawMaterialJComboBox = rawMaterialsComboBox;
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

                String amountPayed = amountPayedField.getText();
                if(amountPayed.length() == 0) {
                    AlertBox.show("All fields with a * are required!");
                    return;
                }

                Supplier supplier = (Supplier) suppliersComboBox.getSelectedItem();
                String supplierId;

                if(supplier != null) {
                    supplierId = supplier.getId();
                }
                else {
                    AlertBox.show("Some unknown problem occurred!");
                    return;
                }

                new DatePicker((day, month, year) -> {
                    String date = year + "-" + month + "-" + day;
                    try {
                        String purchaseId = dbHelper.insertPurchase(date, supplierId, totalBill, amountPayed);

                        for(FormFieldObject fieldObject : fieldObjects) {
                            JComboBox<RawMaterial> productJComboBox = fieldObject.rawMaterialJComboBox;
                            JTextField priceField = fieldObject.priceField;
                            JTextField quantityField = fieldObject.quantityField;

                            String materialId = ((RawMaterial) productJComboBox.getSelectedItem()).getId();
                            String price = priceField.getText();
                            String quantityPurchased = quantityField.getText();

                            if(price.length() == 0 || quantityPurchased.length() == 0) {
                                continue;
                            }

                            dbHelper.insertSubPurchase(purchaseId, materialId, price, quantityPurchased);
                        }

                        AlertBox.show("Purchase Recorded Successfully.");
                        fieldObjects.clear();
                        formPanel.removeAll();
                        formPanel.repaint();
                        formPanel.revalidate();

                        totalBill = 0;
                        totalLabel.setText("Total bill : 0");
                        amountPayedField.setText("");
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

            JPanel topCtrlsPanel =  new JPanel();
            topCtrlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            topCtrlsPanel.setLayout(new BoxLayout(topCtrlsPanel, BoxLayout.X_AXIS));
            topCtrlsPanel.add(suppliersComboBox);
            topCtrlsPanel.add(addSubPurchaseBtn);
            topCtrlsPanel.add(Box.createHorizontalGlue());
            topCtrlsPanel.add(clearBillBtn);

            JPanel footerPanel = new JPanel();
            footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.X_AXIS));

            amountPayedField = new JTextField(20);
            amountPayedField.addKeyListener(new KeyAdapter() {
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
            wrapper.add(new JLabel("*Amount Payed : "));
            wrapper.add(amountPayedField);
            wrapper.add(doneBtn);

            footerPanel.add(totalLabel);
            footerPanel.add(Box.createHorizontalGlue());
            footerPanel.add(wrapper);

            add(topCtrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(formPanel), BorderLayout.CENTER);
            add(footerPanel, BorderLayout.SOUTH);
        }

        private void getAllRawMaterials() {
            try {
                ResultSet resultSet = dbHelper.getAllRawMaterials();
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String volume = resultSet.getString("volume");
                    String category = resultSet.getString("category");
                    int quantityInStock = resultSet.getInt("stock");

                    RawMaterial rawMaterial = new RawMaterial(id, volume, category, quantityInStock);
                    materialsList.add(rawMaterial);
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred!");
                e.printStackTrace();
                System.exit(0);
            }
        }

        private void getAllSuppliers() {
            try {
                ResultSet resultSet = dbHelper.getAllSuppliers();
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");

                    Supplier supplier = new Supplier(id, name);
                    suppliersList.add(supplier);
                }
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred!");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private class ViewPurchasesPanel extends JPanel {

        GridBagConstraints gbc;
        private JPanel tablesPanel;

        ViewPurchasesPanel() {
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            tablesPanel = new JPanel();
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            tablesPanel.setLayout(new GridBagLayout());
            JScrollPane scrollPane = new JScrollPane(tablesPanel);

            add(scrollPane);
        }

        private void fillTable() {
            tablesPanel.removeAll();
            tablesPanel.repaint();
            tablesPanel.revalidate();

            try {
                ResultSet resultSet = dbHelper.getAllPurchases();
                while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    String dateTime = resultSet.getString("dateTime");
                    String supplierName = resultSet.getString("supplierName");
                    int totalBill = resultSet.getInt("totalBill");
                    int amountPayed = resultSet.getInt("amountPayed");

                    JPanel subTablePanel = new JPanel();
                    subTablePanel.setLayout(new BorderLayout());
                    subTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

                    JLabel infoLabel = new JLabel("Purchased from " + supplierName + " on " + dateTime);
                    infoLabel.setFont(new Font(infoLabel.getFont().getName(), Font.BOLD, 14));

                    JButton printInvoiceBtn = new JButton("Print");
                    JButton deleteBtn = new JButton("Delete");
                    deleteBtn.setActionCommand(id);

                    JPanel purchaseInfoPanel = new JPanel();
                    purchaseInfoPanel.setLayout(new BoxLayout(purchaseInfoPanel, BoxLayout.X_AXIS));
                    purchaseInfoPanel.add(infoLabel);
                    purchaseInfoPanel.add(Box.createHorizontalGlue());
                    purchaseInfoPanel.add(printInvoiceBtn);
                    purchaseInfoPanel.add(deleteBtn);

                    DefaultTableModel tableModel = new DefaultTableModel() {
                        @Override
                        public boolean isCellEditable(int row, int col) { return false;
                        }
                    };
                    JTable subTable = new JTable(tableModel);
                    tableModel.addColumn("Raw Material");
                    tableModel.addColumn("Price");
                    tableModel.addColumn("Quantity");
                    tableModel.addColumn("Sub Total");

                    JScrollPane scrollPane = new JScrollPane(subTable);
                    scrollPane.setPreferredSize(new Dimension(500, 180));

                    int total = 0;
                    ResultSet resultSet2 = dbHelper.getSubPurchase(id);
                    while (resultSet2.next()) {
                        String volume = resultSet2.getString("volume");
                        int price = resultSet2.getInt("price");
                        int quantity = resultSet2.getInt("quantityPurchased");
                        int subTotal = price * quantity;
                        total += subTotal;

                        tableModel.addRow(new Object[]{volume, price, quantity, subTotal});
                    }
                    tableModel.addRow(new Object[]{"", "", "", total});

                    JPanel billingInfoPanel = new JPanel();
                    billingInfoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

                    JLabel billingLabel = new JLabel();
                    billingLabel.setText("Amount Payed = " + amountPayed + ", Remaining Amount = "
                            + (totalBill - amountPayed));

                    billingInfoPanel.add(billingLabel);

                    subTablePanel.add(purchaseInfoPanel, BorderLayout.NORTH);
                    subTablePanel.add(scrollPane, BorderLayout.CENTER);
                    subTablePanel.add(billingInfoPanel, BorderLayout.SOUTH);

                    printInvoiceBtn.addActionListener((e) -> printInvoice(dateTime, totalBill, amountPayed, supplierName, tableModel));
                    deleteBtn.addActionListener((e) -> {
                        String purchaseId = ((JButton) e.getSource()).getActionCommand();
                        try {
                            dbHelper.deletePurchase(purchaseId);
                            // Remove the purchase from the view purchases panel
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
            }
            catch (SQLException e) {
                AlertBox.show("Some database error occurred.");
                e.printStackTrace();
                System.exit(0);
            }
        }

        private void printInvoice(String dateTime, int totalBill, int amountPayed, String supplierName,
                                  DefaultTableModel tableModel) {
            InvoicePrinter.printInvoice(InvoicePrinter.PURCHASE_INVOICE, "0", totalBill,
                    amountPayed, dateTime, supplierName, tableModel);
        }
    }

    private class FormFieldObject {
        private JComboBox<RawMaterial> rawMaterialJComboBox;
        private JTextField priceField;
        private JTextField quantityField;
        private JButton addItemBtn;
    }

    private class LiabilitiesPanel extends JPanel {

        private String fromDate, toDate;
        private DefaultTableModel tableModel;
        private JTable liabilitiesTable;
        private JLabel totalLiabilitiesLabel;
        private JComboBox<Supplier> supplierComboBox;
        private JRadioButton fetchAllRadio;
        private int totalLiability;

        LiabilitiesPanel() {
            fromDate = "";
            toDate = "";
            totalLiability = 0;
            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            initComponents();
        }

        void initComponents() {
            setLayout(new BorderLayout());

            JPanel topCtrlsPanel = new JPanel();
            topCtrlsPanel.setLayout(new BoxLayout(topCtrlsPanel, BoxLayout.X_AXIS));
            JPanel ctrlsWrapperPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel ctrlsWrapperPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            supplierComboBox = new JComboBox<>();
            for(Supplier supplier : suppliersList) {
                supplierComboBox.addItem(supplier);
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

            JButton payAmountBtn = new JButton("Pay Amount");
            payAmountBtn.setEnabled(false);
            payAmountBtn.addActionListener((e) -> new PurchasesWindow.LiabilitiesPanel.InputFrame());

            JButton printReportBtn = new JButton("Print Report");
            printReportBtn.setEnabled(false);
            printReportBtn.addActionListener((e) -> printReport(((Supplier) supplierComboBox.getSelectedItem())));

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
                    String supplierId = ((Supplier) supplierComboBox.getSelectedItem()).getId();
                    int records = fillTable(supplierId, fetchAllRadio.isSelected());
                    if(records > 0) {
                        payAmountBtn.setEnabled(true);
                        printReportBtn.setEnabled(true);
                    }
                }
                else {
                    AlertBox.show("Please enter \"From Date\" and \"To Date\" to proceed!");
                }
            });

            liabilitiesTable = new JTable(tableModel);
            tableModel.addColumn("id");
            tableModel.addColumn("Bill Number");
            tableModel.addColumn("Date");
            tableModel.addColumn("Total Bill");
            tableModel.addColumn("Amount Payed");
            tableModel.addColumn("Remaining Liability");
            tableModel.addColumn("Cheque Numbers");

            ctrlsWrapperPanel1.add(supplierComboBox);
            ctrlsWrapperPanel1.add(fromDateBtn);
            ctrlsWrapperPanel1.add(fromDateField);
            ctrlsWrapperPanel1.add(toDateBtn);
            ctrlsWrapperPanel1.add(toDateField);
            ctrlsWrapperPanel1.add(doneBtn);
            ctrlsWrapperPanel1.add(fetchAllRadio);
            ctrlsWrapperPanel2.add(payAmountBtn);
            ctrlsWrapperPanel2.add(printReportBtn);

            topCtrlsPanel.add(ctrlsWrapperPanel1);
            topCtrlsPanel.add(Box.createHorizontalGlue());
            topCtrlsPanel.add(ctrlsWrapperPanel2);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            totalLiabilitiesLabel = new JLabel("Total Liabilities : 0");
            totalLiabilitiesLabel.setFont(new Font(totalLiabilitiesLabel.getFont().getName(), Font.BOLD, 16));
            totalLiabilitiesLabel.setForeground(Color.decode("#57bf13"));
            bottomPanel.add(totalLiabilitiesLabel);

            add(topCtrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(liabilitiesTable), BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        void printReport(Supplier supplier) {
            InvoicePrinter.printLiabilityReport(supplier.getName(), tableModel, totalLiability);
        }

        private int fillTable(String supplierId, boolean fetchAll) {
            liabilitiesTable.removeAll();
            tableModel.setRowCount(0);
            try {
                int count = 0;
                totalLiability = 0;
                ResultSet resultSet;
                if(fetchAll)
                    resultSet = dbHelper.fetchLiabilitiesReport(supplierId);
                else
                    resultSet = dbHelper.fetchLiabilitiesReport(supplierId, fromDate, toDate);

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String billNumber = resultSet.getString("billNumber");
                    String date = resultSet.getString("dateTime");
                    int totalBill = resultSet.getInt("totalBill");
                    int amountRecieved = resultSet.getInt("amountPayed");
                    int remainingBill = resultSet.getInt("remainingBill");
                    String chequeNumbers = resultSet.getString("chequeNumber");
                    totalLiability += remainingBill;
                    tableModel.addRow(new Object[]{id, billNumber, date, totalBill, amountRecieved, remainingBill, chequeNumbers});
                    count++;
                }
                totalLiabilitiesLabel.setText("Total Bill : " + totalLiability);
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
                super("Pay Amount");
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

                JButton payBtn = new JButton("Pay");
                payBtn.addActionListener((e) -> {
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
                    String[] salesIds = new String[liabilitiesTable.getRowCount()];
                    int[] amountsToAdd = new int[liabilitiesTable.getRowCount()];
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
                    fillTable(((Supplier) supplierComboBox.getSelectedItem()).getId(), fetchAllRadio.isSelected());
                    this.dispose();
                });

                JPanel panel2 = new JPanel();
                panel2.add(new JLabel("*Amount"));
                panel2.add(amountField);
                panel2.add(payBtn);

                JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
                panel3.add(new JLabel("Cheque Number"));
                panel3.add(chequeNumberField);

                add(panel1, BorderLayout.NORTH);
                add(panel2, BorderLayout.CENTER);
                add(panel3, BorderLayout.SOUTH);

                setVisible(true);
                amountField.requestFocus();
            }

            private void adjustAmounts(String[] purchasesIds, int[] amountToAdd, String chequeNumber, int count) {
                try {
                    for(int i = 0; i < count; i++) {
                        dbHelper.payAmount(purchasesIds[i], amountToAdd[i], chequeNumber);
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

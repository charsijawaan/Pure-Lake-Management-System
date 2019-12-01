import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

class ReportsWindow extends Activity {

    private DBHelper dbHelper;

    ReportsWindow() {
        super();
        dbHelper = DBHelper.getDBHelper();
        initComponents();
    }

    private void initComponents() {
        setActivityName("Reports");
        setDefaultCloseOperation(Activity.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tabbedPane.add("Liabilities Report", new LiabilitiesPanel());
        tabbedPane.add("Recievables Report", new RecievablesPanel());
        tabbedPane.add("Sales Report", new SalesReportPanel());
        tabbedPane.add("Purchases Report", new PurchasesReportPanel());
        tabbedPane.add("Profit & Loss Report", new ProfitLossReportPanel());
        tabbedPane.add("Opening/Closing Balance", new BalancePanel());

        addView(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private class LiabilitiesPanel extends JPanel {

        private String fromDate, toDate;
        private JLabel totalAmountLabel;
        private JTable reportTable;
        private DefaultTableModel tableModel;
        private JRadioButton fetchAllRadio;

        LiabilitiesPanel() {
            tableModel = new DefaultTableModel() {

                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            fromDate = "";
            toDate = "";
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel ctrlsPanel = new JPanel();
            ctrlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

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

            JButton generateReportBtn = new JButton("Generate Report");
            generateReportBtn.addActionListener((e) -> {
                if((!fromDate.isEmpty() && !toDate.isEmpty()) || fetchAllRadio.isSelected()) {
                    fillTable(fetchAllRadio.isSelected());
                }
                else {
                    AlertBox.show("Please enter \"From Date\" and \"To Date\" to proceed!");
                }
            });

            JButton printBtn = new JButton("Print Report");
            printBtn.addActionListener((e) -> {

            });

            ctrlsPanel.add(fromDateBtn);
            ctrlsPanel.add(fromDateField);
            ctrlsPanel.add(toDateBtn);
            ctrlsPanel.add(toDateField);
            ctrlsPanel.add(fetchAllRadio);
            ctrlsPanel.add(generateReportBtn);
            ctrlsPanel.add(printBtn);

            JPanel totalAmountsPanel = new JPanel();
            totalAmountsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            totalAmountLabel = new JLabel();
            totalAmountLabel.setText("Total Payable Amount : 0");
            totalAmountLabel.setFont(new Font(totalAmountLabel.getFont().getName(), Font.BOLD, 16));
            totalAmountLabel.setForeground(Color.decode("#57bf13"));

            totalAmountsPanel.add(totalAmountLabel);

            reportTable = new JTable(tableModel);
            tableModel.addColumn("Supplier Name");
            tableModel.addColumn("Payable Amount");

            add(ctrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(reportTable), BorderLayout.CENTER);
            add(totalAmountsPanel, BorderLayout.SOUTH);
        }

        private void fillTable(boolean fetchAll) {
            reportTable.removeAll();
            tableModel.setRowCount(0);
            try {
                int totalLiabilities = 0;
                ResultSet resultSet;
                if(fetchAll)
                    resultSet = dbHelper.fetchTotalLiabilities();
                else
                    resultSet = dbHelper.fetchTotalLiabilities(fromDate, toDate);

                while (resultSet.next()) {
                    String supplierName = resultSet.getString("supplierName");
                    int payable = resultSet.getInt("payable");
                    totalLiabilities += payable;
                    tableModel.addRow(new Object[] {supplierName, payable});
                }
                totalAmountLabel.setText("Total Liabilities : " + totalLiabilities);
            }
            catch (SQLException ex) {
                AlertBox.show("Some database error occurred!");
                ex.printStackTrace();
            }
        }
    }

    private class RecievablesPanel extends JPanel {

        private String fromDate, toDate;
        private JLabel totalAmountLabel;
        private JTable reportTable;
        private DefaultTableModel tableModel;
        private JRadioButton fetchAllRadio;

        RecievablesPanel() {
            tableModel = new DefaultTableModel() {

                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            fromDate = "";
            toDate = "";
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel ctrlsPanel = new JPanel();
            ctrlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

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

            JButton generateReportBtn = new JButton("Generate Report");
            generateReportBtn.addActionListener((e) -> {
                if((!fromDate.isEmpty() && !toDate.isEmpty()) || fetchAllRadio.isSelected()) {
                    fillTable(fetchAllRadio.isSelected());
                }
                else {
                    AlertBox.show("Please enter \"From Date\" and \"To Date\" to proceed!");
                }
            });

            JButton printBtn = new JButton("Print Report");
            printBtn.addActionListener((e) -> {

            });

            ctrlsPanel.add(fromDateBtn);
            ctrlsPanel.add(fromDateField);
            ctrlsPanel.add(toDateBtn);
            ctrlsPanel.add(toDateField);
            ctrlsPanel.add(fetchAllRadio);
            ctrlsPanel.add(generateReportBtn);
            ctrlsPanel.add(printBtn);

            JPanel totalAmountsPanel = new JPanel();
            totalAmountsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            totalAmountLabel = new JLabel();
            totalAmountLabel.setText("Total Recievable Amount : 0");
            totalAmountLabel.setFont(new Font(totalAmountLabel.getFont().getName(), Font.BOLD, 16));
            totalAmountLabel.setForeground(Color.decode("#57bf13"));

            totalAmountsPanel.add(totalAmountLabel);

            reportTable = new JTable(tableModel);
            tableModel.addColumn("Customer Name");
            tableModel.addColumn("Recievable Amount");

            add(ctrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(reportTable), BorderLayout.CENTER);
            add(totalAmountsPanel, BorderLayout.SOUTH);
        }

        private void fillTable(boolean fetchAll) {
            reportTable.removeAll();
            tableModel.setRowCount(0);
            try {
                int totalRecievables = 0;
                ResultSet resultSet;
                if(fetchAll)
                    resultSet = dbHelper.fetchTotalRecievables();
                else
                    resultSet = dbHelper.fetchTotalRecievables(fromDate, toDate);

                while (resultSet.next()) {
                    String supplierName = resultSet.getString("customerName");
                    int payable = resultSet.getInt("recievable");
                    totalRecievables += payable;
                    tableModel.addRow(new Object[] {supplierName, payable});
                }
                totalAmountLabel.setText("Total Recievable : " + totalRecievables);
            }
            catch (SQLException ex) {
                AlertBox.show("Some database error occurred!");
                ex.printStackTrace();
            }
        }
    }

    private class SalesReportPanel extends JPanel {

        private String fromDate, toDate;
        private JLabel amountRecievedLabel, amountToRecieveLabel;
        private JTable reportTable;
        private DefaultTableModel tableModel;
        private JRadioButton fetchAllRadio;

        SalesReportPanel() {
            tableModel = new DefaultTableModel() {

                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            fromDate = "";
            toDate = "";
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel ctrlsPanel = new JPanel();
            ctrlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

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

            JButton generateReportBtn = new JButton("Generate Report");
            generateReportBtn.addActionListener((e) -> {
                if((!fromDate.isEmpty() && !toDate.isEmpty()) || fetchAllRadio.isSelected()) {
                    fillTable(fetchAllRadio.isSelected());
                }
                else {
                    AlertBox.show("Please enter \"From Date\" and \"To Date\" to proceed!");
                }
            });

            JButton printBtn = new JButton("Print Report");
            printBtn.addActionListener((e) -> {

            });

            ctrlsPanel.add(fromDateBtn);
            ctrlsPanel.add(fromDateField);
            ctrlsPanel.add(toDateBtn);
            ctrlsPanel.add(toDateField);
            ctrlsPanel.add(fetchAllRadio);
            ctrlsPanel.add(generateReportBtn);
            ctrlsPanel.add(printBtn);

            JPanel totalAmountsPanel = new JPanel();
            totalAmountsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            amountRecievedLabel = new JLabel();
            amountRecievedLabel.setText("Total Amount Recieved : 0");
            amountRecievedLabel.setFont(new Font(amountRecievedLabel.getFont().getName(), Font.BOLD, 16));
            amountRecievedLabel.setForeground(Color.decode("#57bf13"));

            amountToRecieveLabel = new JLabel();
            amountToRecieveLabel.setText("Total Amount To Recieve : 0");
            amountToRecieveLabel.setFont(new Font(amountToRecieveLabel.getFont().getName(), Font.BOLD, 16));
            amountToRecieveLabel.setForeground(Color.decode("#ec992e"));

            totalAmountsPanel.add(amountRecievedLabel);
            totalAmountsPanel.add(new JLabel("  "));
            totalAmountsPanel.add(amountToRecieveLabel);

            reportTable = new JTable(tableModel);
            tableModel.addColumn("Date");
            tableModel.addColumn("Sold To");
            tableModel.addColumn("Amount");

            add(ctrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(reportTable), BorderLayout.CENTER);
            add(totalAmountsPanel, BorderLayout.SOUTH);
        }

        private void fillTable(boolean fetchAll) {
            reportTable.removeAll();
            tableModel.setRowCount(0);
            try {
                int totalBill = 0;
                int totalRemainingAmount = 0;
                ResultSet resultSet;
                if(fetchAll)
                    resultSet = dbHelper.fetchTotalSales();
                else
                    resultSet = dbHelper.fetchTotalSales(fromDate, toDate);

                while (resultSet.next()) {
                    String Date = resultSet.getString("dateTime");
                    String customerName = resultSet.getString("customerName");
                    int amount = resultSet.getInt("totalBill");
                    int remaining = resultSet.getInt("remainingAmount");
                    totalBill += amount;
                    totalRemainingAmount += remaining;
                    tableModel.addRow(new Object[] {Date, customerName, amount});
                }
                int totalRecieved = totalBill - totalRemainingAmount;
                amountRecievedLabel.setText("Total Amount Recieved : " + totalRecieved);
                amountToRecieveLabel.setText("Total Amount To Recieve : " + totalRemainingAmount);
            }
            catch (SQLException ex) {
                AlertBox.show("Some database error occurred!");
                ex.printStackTrace();
            }
        }
    }

    private class PurchasesReportPanel extends JPanel {

        private String fromDate, toDate;
        private JLabel amountPayedLabel, amountToPayLabel;
        private JTable reportTable;
        private DefaultTableModel tableModel;
        private JRadioButton fetchAllRadio;

        PurchasesReportPanel() {
            tableModel = new DefaultTableModel() {

                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            fromDate = "";
            toDate = "";
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel ctrlsPanel = new JPanel();
            ctrlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

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

            JButton generateReportBtn = new JButton("Generate Report");
            generateReportBtn.addActionListener((e) -> {
                if((!fromDate.isEmpty() && !toDate.isEmpty()) || fetchAllRadio.isSelected()) {
                    fillTable(fetchAllRadio.isSelected());
                }
                else {
                    AlertBox.show("Please enter \"From Date\" and \"To Date\" to proceed!");
                }
            });

            JButton printBtn = new JButton("Print Report");
            printBtn.addActionListener((e) -> {

            });

            ctrlsPanel.add(fromDateBtn);
            ctrlsPanel.add(fromDateField);
            ctrlsPanel.add(toDateBtn);
            ctrlsPanel.add(toDateField);
            ctrlsPanel.add(fetchAllRadio);
            ctrlsPanel.add(generateReportBtn);
            ctrlsPanel.add(printBtn);

            JPanel totalAmountsPanel = new JPanel();
            totalAmountsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

            amountPayedLabel = new JLabel();
            amountPayedLabel.setText("Total Amount Payed : 0");
            amountPayedLabel.setFont(new Font(amountPayedLabel.getFont().getName(), Font.BOLD, 16));
            amountPayedLabel.setForeground(Color.decode("#57bf13"));

            amountToPayLabel = new JLabel();
            amountToPayLabel.setText("Total Amount To Pay : 0");
            amountToPayLabel.setFont(new Font(amountToPayLabel.getFont().getName(), Font.BOLD, 16));
            amountToPayLabel.setForeground(Color.decode("#ec992e"));

            totalAmountsPanel.add(amountPayedLabel);
            totalAmountsPanel.add(new JLabel("  "));
            totalAmountsPanel.add(amountToPayLabel);

            reportTable = new JTable(tableModel);
            tableModel.addColumn("Date");
            tableModel.addColumn("Purchased From");
            tableModel.addColumn("Amount");

            add(ctrlsPanel, BorderLayout.NORTH);
            add(new JScrollPane(reportTable), BorderLayout.CENTER);
            add(totalAmountsPanel, BorderLayout.SOUTH);
        }

        private void fillTable(boolean fetchAll) {
            reportTable.removeAll();
            tableModel.setRowCount(0);
            try {
                int totalBill = 0;
                int totalRemainingAmount = 0;
                ResultSet resultSet;
                if(fetchAll)
                    resultSet = dbHelper.fetchTotalPurchases();
                else
                    resultSet = dbHelper.fetchTotalPurchases(fromDate, toDate);

                while (resultSet.next()) {
                    String Date = resultSet.getString("dateTime");
                    String customerName = resultSet.getString("supplierName");
                    int amount = resultSet.getInt("totalBill");
                    int remaining = resultSet.getInt("remainingAmount");
                    totalBill += amount;
                    totalRemainingAmount += remaining;
                    tableModel.addRow(new Object[] {Date, customerName, amount});
                }
                int totalRecieved = totalBill - totalRemainingAmount;
                amountPayedLabel.setText("Total Amount Payed : " + totalRecieved);
                amountToPayLabel.setText("Total Amount To Pay : " + totalRemainingAmount);
            }
            catch (SQLException ex) {
                AlertBox.show("Some database error occurred!");
                ex.printStackTrace();
            }
        }
    }

    private class ProfitLossReportPanel extends JPanel {

        private JCheckBox todayCheckBox;
        private String fromDate, toDate;
        private JLabel salesLabel, expensesLabel, purchasesLabel, salariesLabel, profitLabel;

        ProfitLossReportPanel() {
            fromDate = "";
            toDate = "";
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            JPanel ctrlsPanel = new JPanel();
            ctrlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

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

            todayCheckBox = new JCheckBox("Today");
            todayCheckBox.addActionListener((e) -> {
                if(todayCheckBox.isSelected()) {
                    String date = Utilities.getTodaysDate();
                    toDateBtn.setEnabled(false);
                    fromDateBtn.setEnabled(false);
                    toDateField.setText(date);
                    fromDateField.setText(date);
                    fromDate = date;
                    toDate = date;
                }
                else {
                    toDateBtn.setEnabled(true);
                    fromDateBtn.setEnabled(true);
                }
            });

            JButton generateReportBtn = new JButton("Calculate");
            generateReportBtn.addActionListener((e) -> generateProfitLossReport(fromDate, toDate));

            ctrlsPanel.add(fromDateBtn);
            ctrlsPanel.add(fromDateField);
            ctrlsPanel.add(toDateBtn);
            ctrlsPanel.add(toDateField);
            ctrlsPanel.add(todayCheckBox);
            ctrlsPanel.add(generateReportBtn);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

            salesLabel = new JLabel("Sales :         0");
            expensesLabel = new JLabel("Expenses :  0");
            purchasesLabel = new JLabel("Purchases : 0");
            salariesLabel = new JLabel("Salaries :     0");
            profitLabel = new JLabel("Profit :          0");

            salesLabel.setFont(new Font(salesLabel.getFont().getName(), Font.PLAIN, 16));
            expensesLabel.setFont(new Font(expensesLabel.getFont().getName(), Font.PLAIN, 16));
            purchasesLabel.setFont(new Font(purchasesLabel.getFont().getName(), Font.PLAIN, 16));
            salariesLabel.setFont(new Font(salariesLabel.getFont().getName(), Font.PLAIN, 16));
            profitLabel.setFont(new Font(profitLabel.getFont().getName(), Font.PLAIN, 16));

            centerPanel.add(salesLabel);
            centerPanel.add(expensesLabel);
            centerPanel.add(purchasesLabel);
            centerPanel.add(salariesLabel);
            centerPanel.add(new JLabel("-------------------------------" +
                    "----------------------------"));
            centerPanel.add(profitLabel);

            add(ctrlsPanel, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
        }

        private void generateProfitLossReport(String fromDate, String toDate) {
            try {
                ResultSet salesResultSet = dbHelper.getSalesSum(fromDate, toDate);
                ResultSet expensesResultSet = dbHelper.getExpensesSum(fromDate, toDate);
                ResultSet purchasesResultSet = dbHelper.getPurchasesSum(fromDate, toDate);
                ResultSet salariesResultSet = dbHelper.getSalariesSum(fromDate, toDate);

                int sales = salesResultSet.getInt("salesSum");
                int expenses = expensesResultSet.getInt("expensesSum");
                int purchases = purchasesResultSet.getInt("purchasesSum");
                int salaries = salariesResultSet.getInt("salariesSum");

                int profit = sales - (expenses + purchases + salaries);
                profitLabel.setText("Profit :          " + profit);
                salesLabel.setText("Sales :         " + sales);
                expensesLabel.setText("Expenses :  " + expenses);
                purchasesLabel.setText("Purchases : " + purchases);
                salariesLabel.setText("Salaries :     " + salaries);
            }
            catch (SQLException ex) {
                AlertBox.show("Some database error occurred!");
                ex.printStackTrace();
            }
        }
    }

    private class BalancePanel extends JPanel {

        BalancePanel() {
            initComponents();
        }

        private void initComponents() {

        }
    }
}

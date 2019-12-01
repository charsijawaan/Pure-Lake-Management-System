import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Dashboard extends Activity {

    private DBHelper dbHelper;

    Dashboard() {
        super();
        this.dbHelper = DBHelper.getDBHelper();
        initComponents();
//        updateOpeningBalance();
    }

    private void initComponents() {
        setActivityName("Dashboard");
        setDefaultCloseOperation(Activity.EXIT_ON_CLOSE);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(3, 3));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        OptionButton employeesBtn = new OptionButton("Employees");
        OptionButton customersBtn = new OptionButton("Customers");
        OptionButton suppliersBtn = new OptionButton("Suppliers");
        OptionButton productsBtn = new OptionButton("Products/Materials");
        OptionButton bankBtn = new OptionButton("Bank Records");
        OptionButton salesBtn = new OptionButton("Sales");
        OptionButton purchasesBtn = new OptionButton("Purchases");
        OptionButton expensesBtn = new OptionButton("Expenses");
        OptionButton reportsBtn = new OptionButton("Reports");

        employeesBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            Dashboard.this.setVisible(false);
            EmployeesWindow window = new EmployeesWindow();
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Dashboard.this.setVisible(true);
                }
            });
            }
        });

        customersBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            Dashboard.this.setVisible(false);
            CustomersWindow window = new CustomersWindow();
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Dashboard.this.setVisible(true);
                }
            });
            }
        });

        suppliersBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            Dashboard.this.setVisible(false);
            SuppliersWindow window = new SuppliersWindow();
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Dashboard.this.setVisible(true);
                }
            });
            }
        });

        productsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            Dashboard.this.setVisible(false);
            ProductsRawWindow window = new ProductsRawWindow();
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Dashboard.this.setVisible(true);
                }
            });
            }
        });

        bankBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            Dashboard.this.setVisible(false);
            BankRecordsWindow window = new BankRecordsWindow();
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Dashboard.this.setVisible(true);
                }
            });
            }
        });

        salesBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            Dashboard.this.setVisible(false);
            SalesWindow window = new SalesWindow();
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Dashboard.this.setVisible(true);
                }
            });
            }
        });

        purchasesBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Dashboard.this.setVisible(false);
                PurchasesWindow window = new PurchasesWindow();
                window.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        Dashboard.this.setVisible(true);
                    }
                });
            }
        });

        expensesBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Dashboard.this.setVisible(false);
                ExpensesWindow window = new ExpensesWindow();
                window.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        Dashboard.this.setVisible(true);
                    }
                });
            }
        });

        reportsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Dashboard.this.setVisible(false);
                ReportsWindow window = new ReportsWindow();
                window.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        Dashboard.this.setVisible(true);
                    }
                });
            }
        });

        JPanel panel1 = new JPanel();
        panel1.add(employeesBtn);

        JPanel panel2 = new JPanel();
        panel2.add(customersBtn);

        JPanel panel3 = new JPanel();
        panel3.add(suppliersBtn);

        JPanel panel4 = new JPanel();
        panel4.add(productsBtn);

        JPanel panel5 = new JPanel();
        panel5.add(bankBtn);

        JPanel panel6 = new JPanel();
        panel6.add(salesBtn);

        JPanel panel7 = new JPanel();
        panel7.add(purchasesBtn);

        JPanel panel8 = new JPanel();
        panel8.add(expensesBtn);

        JPanel panel9 = new JPanel();
        panel9.add(reportsBtn);

        optionsPanel.add(panel1);
        optionsPanel.add(panel2);
        optionsPanel.add(panel3);
        optionsPanel.add(panel4);
        optionsPanel.add(panel5);
        optionsPanel.add(panel6);
        optionsPanel.add(panel7);
        optionsPanel.add(panel8);
        optionsPanel.add(panel9);

        addView(optionsPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void updateOpeningBalance() {
        if(dbHelper.balanceRecordEmpty()) {
            Dashboard.this.setVisible(false);
            OpeningBalanceWindow window = new OpeningBalanceWindow();
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Dashboard.this.setVisible(true);
                }
            });
        }
        else {

        }
    }

    private class OptionButton extends JPanel {

        private JLabel label;

        OptionButton(String text) {
            label = new JLabel(text, JLabel.CENTER);
            label.setForeground(Color.white);
            label.setFont(new Font(this.getFont().getName(), Font.BOLD, 17));

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(250,125));
            setBackground(Color.decode("#50abe8"));
            setOpaque(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    setBackground(Color.decode("#50abe8"));
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    setBackground(Color.decode("#4c93c3"));
                }
            });

            add(label, BorderLayout.CENTER);
        }
    }
}

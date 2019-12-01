import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

class OpeningBalanceWindow extends JFrame {

    private DBHelper dbHelper;

    OpeningBalanceWindow() {
        this.dbHelper = DBHelper.getDBHelper();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JLabel headerLabel = new JLabel();
        headerLabel.setText("Please enter an opening balance of this month to proceed");
        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.PLAIN, 17));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout());
        headerPanel.add(headerLabel);

        JTextField balanceField = new JTextField(20);
        balanceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if(!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        centerPanel.add(balanceField);

        JButton doneBtn = new JButton("Done");
        doneBtn.addActionListener((e) -> {
            String balance = balanceField.getText();
            if(balance.length() > 0) {
                String date = Utilities.getTodaysDate();
                String[] dateParts = date.split("-");
                dateParts[2] = "01";
                date = dateParts[0] + "-" + dateParts[1] + "-" + dateParts[2];

                try {
                    dbHelper.insertOpeningBalance(date, balance);
                    OpeningBalanceWindow.this.dispatchEvent(new WindowEvent(
                            OpeningBalanceWindow.this, WindowEvent.WINDOW_CLOSING));
                }
                catch (SQLException ex) {
                    AlertBox.show("Some database error occurred!");
                    ex.printStackTrace();
                }
            }
            else {
                AlertBox.show("Enter an opening balance to proceed!");
                balanceField.requestFocus();
            }
        });

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout());
        footerPanel.add(doneBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

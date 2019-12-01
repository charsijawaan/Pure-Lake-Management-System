import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

class LoginWindow extends JFrame {

    private DBHelper dbHelper;

    LoginWindow() {
        super(Constants.APP_NAME);
        dbHelper = DBHelper.getDBHelper();
        initComponents();
    }

    private void initComponents() {
        setSize(400, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(90, 30, 100, 30));

        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        usernameLabel.setFont(new Font(usernameLabel.getFont().getName(), Font.BOLD, 16));
        passwordLabel.setFont(new Font(usernameLabel.getFont().getName(), Font.BOLD, 16));

        JTextField usernameField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener((e) -> {
            String username = usernameField.getText();
            String pass = new String(passField.getPassword());

            if(username.length() > 0 && pass.length() > 0) {
                try {
                    if(dbHelper.isValidUser(username, pass)) {
                        new Dashboard();
                        dispose();
                    }
                    else {
                        AlertBox.show("Invalid Username or Password. Try again!");
                    }
                } catch (SQLException e1) {
                    AlertBox.show("Some database error occurred");
                    System.exit(0);
                }
            }
        });

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passField);
        formPanel.add(loginBtn);

        add(formPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}

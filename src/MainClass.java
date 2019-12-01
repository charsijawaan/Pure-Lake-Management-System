import javax.swing.*;
import java.io.*;

public class MainClass {

    static {
        setUiTHeme();
        createBackup();
    }

    public static void main(String[] args) {
        boolean TESTING_MODE = true;

        // Create connection with database
        DBHelper dbHelper = DBHelper.getDBHelper();
        boolean connected = dbHelper.openConnection();

        // Launch the application
        if(connected) {
            if(TESTING_MODE) {
                new Dashboard();
            }
            else {
                new LoginWindow();
            }
        }
        else {
            AlertBox.show("Unable to connect to pure lake database.");
        }
    }

    private static void createBackup() {
        String userHomeDir = System.getProperty("user.home");
        File source = new File(Constants.DB_NAME);
        File dest = new File(userHomeDir + "\\" + Constants.BACKUP_DB_NAME);

        InputStream is;
        OutputStream os;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            is.close();
            os.close();
        }
        catch (Exception ex) {
            AlertBox.show("Failed to create database backup!");
            ex.printStackTrace();
        }
    }

    private static void setUiTHeme() {
        boolean nimbusFound = false;
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    nimbusFound = true;
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

            if(!nimbusFound) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

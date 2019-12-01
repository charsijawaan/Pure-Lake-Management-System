import javax.swing.*;
import java.awt.*;

class Activity extends JFrame {

    private JLabel activityNameLabel;
    private JPanel contentViewPanel;

    Activity() {
        super(Constants.APP_NAME);
        initComponents();
    }

    private void initComponents() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel brandNamePanel = new JPanel();
        brandNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        brandNamePanel.setBackground(Color.decode("#3498db"));

        JLabel brandNameLabel = new JLabel(Constants.APP_NAME, JLabel.CENTER);
        brandNameLabel.setForeground(Color.white);
        brandNameLabel.setFont(new Font(brandNameLabel.getFont().getName(),
                Font.BOLD, 26));
        brandNamePanel.add(brandNameLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setLayout(new BorderLayout());

        JPanel activityNamePanel = new JPanel();
        activityNamePanel.setLayout(new BoxLayout(activityNamePanel, BoxLayout.X_AXIS));
        activityNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        activityNamePanel.setBackground(Color.decode("#f5f5f5"));

        activityNameLabel = new JLabel("Dashboard");
        activityNameLabel.setFont(new Font(brandNameLabel.getFont().getName(),
                Font.BOLD, 16));
        activityNameLabel.setForeground(Color.decode("#717070"));

        activityNamePanel.add(activityNameLabel);
        contentPanel.add(activityNamePanel, BorderLayout.NORTH);

        contentViewPanel = new JPanel();
        contentViewPanel.setLayout(new BorderLayout());

        contentPanel.add(contentViewPanel, BorderLayout.CENTER);

        add(brandNamePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    void setActivityName(String name) {
        activityNameLabel.setText(name);
    }

    void addView(Component component, String constraints) {
        contentViewPanel.add(component, constraints);
    }
}

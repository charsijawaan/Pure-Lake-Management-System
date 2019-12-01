import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class DatePicker extends JFrame {

    private Invokable invokable;
    private JComboBox<String> daysList;
    private JComboBox<String> yearsList;
    private JComboBox<String> monthsList;

    DatePicker(Invokable invokable) {
        this.invokable = invokable;
        initComponents();
    }

    private void initComponents() {
        setSize(350, 150);
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        daysList = new JComboBox<>();
        yearsList = new JComboBox<>();
        monthsList = new JComboBox<>(new String[]{"01", "02",
                "03", "04", "05", "06", "07", "08", "09", "10",
                "11", "12"});

        JButton doneBtn = new JButton("Done");
        doneBtn.addActionListener((e) -> {
            String day = (String) daysList.getSelectedItem();
            String month = (String) monthsList.getSelectedItem();
            String year = (String) yearsList.getSelectedItem();
            dispose();
            invokable.datePicked(day, month, year);
        });

        for(int i = 1; i <= 31; i++) {
            String extraZero = "";
            if(i < 10) {
                extraZero = "0";
            }
            daysList.addItem(extraZero + i);
        }

        for(int i = 2000; i <= 2050; i++) {
            yearsList.addItem(Integer.toString(i));
        }

        JPanel listsPanel = new JPanel();
        listsPanel.add(new JLabel("Day"));
        listsPanel.add(daysList);
        listsPanel.add(new JLabel("Month"));
        listsPanel.add(monthsList);
        listsPanel.add(new JLabel("Year"));
        listsPanel.add(yearsList);
        listsPanel.add(doneBtn);

        JLabel pickDateLabel = new JLabel("Pick Date", JLabel.CENTER);
        pickDateLabel.setFont(new Font(pickDateLabel.getFont().getName(), Font.PLAIN, 16));
        pickDateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(pickDateLabel, BorderLayout.NORTH);
        add(listsPanel, BorderLayout.CENTER);
        setVisible(true);
        setCurrentDate();
    }

    private void setCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String[] dateParts = dtf.format(now).split("/");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);

        monthsList.setSelectedIndex(month - 1);
        daysList.setSelectedIndex(day - 1);
        yearsList.setSelectedIndex(year - 2000);
    }
}

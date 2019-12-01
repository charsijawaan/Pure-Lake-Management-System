import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Utilities {

    static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        }
        catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    static boolean isValidCategory(String category) {
        category = category.toLowerCase();
        for(String quality : Constants.QUALITIES) {
            if(quality.toLowerCase().equals(category))
                return true;
        }
        return false;
    }

    static String getTodaysDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String[] dateParts = dtf.format(now).split("/");

        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];

        return year + "-" + month + "-" + day;
    }
}

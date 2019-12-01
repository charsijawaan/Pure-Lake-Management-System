import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;

class InvoicePrinter {

     static final int SALE_INVOICE = 1;
     static final int PURCHASE_INVOICE = 2;

     static void printInvoice(int type, String billNumber, int totalBill, int payed_recieved, String dateTime, String personName, DefaultTableModel tableModel) {
        String invoiceHeading;
        String invoicePara;
        StringBuilder stringBuilder = new StringBuilder();

        if(type == SALE_INVOICE) {
            invoiceHeading = "SALE INVOICE";
            invoicePara = "Sold to <b>" + personName + "</b> on <b>" + dateTime + "</b><br/>Bill Number : <b>"
                    + billNumber + "</b>";
        }
        else if(type == PURCHASE_INVOICE) {
            invoiceHeading = "PURCHASE INVOICE";
            invoicePara = "Purchased from <b>" + personName + "</b> on <b>" + dateTime + "</b>";
        }
        else {
            return;
        }

        stringBuilder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<title>Invoice</title>\n" +
                "\t<style type=\"text/css\">\n" +
                "\t\tbody {\n" +
                "\t\t\tfont-family: 'open.Sans',sans-serif;\n" +
                "\t\t\tmargin: 0;\n" +
                "\t\t}\n" +
                "\t\t.header {\n" +
                "\t\t\ttext-align: center;\n" +
                "\t\t    color: #ffffff;\n" +
                "\t\t    font-size: 15px;\n" +
                "\t\t    background-color: #5876dc;\n" +
                "\t\t    padding: 16px;\n" +
                "\t\t    font-weight: bold;\n" +
                "\t\t}\n" +
                "\t\t.brand-logo-area {\n" +
                "\t\t\tdisplay: flex;\n" +
                "\t\t\tjustify-content: center;\n" +
                "\t\t}\n" +
                "\t\t.brand-logo-area img {\n" +
                "\t\t\twidth: 148px;\n" +
                "\t\t}\n" +
                "\t\t.main-content {\n" +
                "\t\t\tpadding: 10px;\n" +
                "\t\t\tposition: relative;\n" +
                "\t\t}\n" +
                "\t\t.background {\n" +
                "\t\t\tposition: absolute;\n" +
                "\t\t    width: 100%;\n" +
                "\t\t    height: 92vh;\n" +
                "\t\t    top: 0px;\n" +
                "\t\t    left: 0px;\n" +
                "\t\t    display: flex;\n" +
                "\t\t    align-items: center;\n" +
                "\t\t    opacity: 0.2;\n" +
                "\t\t    justify-content: center;\n" +
                "\t\t    z-index: -1;\n" +
                "\t\t}\n" +
                "\t\t.company-name {\n" +
                "\t\t\ttext-align: center;\n" +
                "\t\t\tmargin-top: -14px;\n" +
                "    \t\tmargin-bottom: 40px;\n" +
                "\t\t}\n" +
                "\t\ttable {\n" +
                "\t\t\twidth: 100%;\n" +
                "\t\t}\n" +
                "\t\tth {\n" +
                "\t\t\ttext-align: left;\n" +
                "\t\t}\n" +
                "\t\tth, td {\n" +
                "\t\t\tpadding: 10px;\n" +
                "\t\t}\n" +
                "\t\ttable, th, td {\n" +
                "\t\t\tborder: 1px solid #222222;\n" +
                "\t\t\tborder-collapse: collapse;\n" +
                "\t\t}\n" +
                "\t\t.total-bill {\n" +
                "\t\t\ttext-align: right;\n" +
                "\t\t\tpadding-right: 20px;\n" +
                "\t\t}\n" +
                "\t\t@media print {\n" +
                "            body {\n" +
                "                -webkit-print-color-adjust: exact;\n" +
                "            }\n@page { margin: 0; }" +
                "        }\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<div class=\"header\"><span>");
        stringBuilder.append(invoiceHeading);
        stringBuilder.append("</span>");
        stringBuilder.append("</div>\n" +
                "\n" +
                "\t<div class=\"main-content\">\n" +
                "<div class=\"brand-logo-area\">\n" +
                "\t\t\t<img src=\"images/logo.jpg\">\n" +
                "\t\t</div>" +
                "\t\t<h4 class=\"company-name\">Sarwar & Son's Pure Lake Water</h4>\n" +
                "\t\t<p>");
        stringBuilder.append(invoicePara);
        stringBuilder.append("</p>\n" +
                "\n" +
                "\t\t<table>\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<th>Volume</th>\n" +
                "\t\t\t\t<th>Price</th>\n" +
                "\t\t\t\t<th>Quantity</th>\n" +
                "\t\t\t\t<th>Sub Total</th>\n" +
                "\t\t\t</tr>");

        String rows = "";
        for(int i = 0; i < tableModel.getRowCount(); i++) {
            rows += "<tr>";
            rows += "<td>" + tableModel.getValueAt(i, 0) + "</td>";
            rows += "<td>" + tableModel.getValueAt(i, 1) + "</td>";
            rows += "<td>" + tableModel.getValueAt(i, 2) + "</td>";

            if(i == tableModel.getRowCount() - 1) {
                rows += "<td><b>" + tableModel.getValueAt(i, 3) + "</b></td>";
            }
            else {
                rows += "<td>" + tableModel.getValueAt(i, 3) + "</td>";
            }
            rows += "</tr>";
        }

        stringBuilder.append(rows);
        stringBuilder.append("</table>");

        if(type == SALE_INVOICE) {
            stringBuilder.append("<label style='text-align: right; margin-top: 10px; display: block;'><b>Amount Recieved = " + payed_recieved +
                    ", Remaining Amount = " + (totalBill - payed_recieved) + "</b></label>");
        }
        else {
            stringBuilder.append("<label style='text-align: right; margin-top: 10px; display: block;'><b>Amount Payed = " + payed_recieved +
                    ", Remaining Liability = " + (totalBill - payed_recieved) + "</b></label>");
        }

        stringBuilder.append("<div class=\"background\">\n\" +\n" +
                "\"\t\t\t<img src=\"images/background.jpg\">\n\" +\n" +
                "\"\t\t</div>");
        stringBuilder.append("</div>\n" +
                "\n" +
                "\t<script type=\"text/javascript\">\n" +
                "\t\tprint();\n" +
                "\t</script>\n" +
                "</body>\n" +
                "</html>");

        String html = stringBuilder.toString();
        openHTMLFile(html);
     }

     static void printReport(String personName, DefaultTableModel tableModel, int total) {
         StringBuilder stringBuilder = new StringBuilder();

         stringBuilder.append("<!DOCTYPE html>\n" +
                 "<html>\n" +
                 "<head>\n" +
                 "\t<title>Invoice</title>\n" +
                 "\t<style type=\"text/css\">\n" +
                 "\t\tbody {\n" +
                 "\t\t\tfont-family: 'open.Sans',sans-serif;\n" +
                 "\t\t\tmargin: 0;\n" +
                 "\t\t}\n" +
                 "\t\t.header {\n" +
                 "\t\t\ttext-align: center;\n" +
                 "\t\t    color: #ffffff;\n" +
                 "\t\t    font-size: 15px;\n" +
                 "\t\t    background-color: #5876dc;\n" +
                 "\t\t    padding: 16px;\n" +
                 "\t\t    font-weight: bold;\n" +
                 "\t\t}\n" +
                 "\t\t.brand-logo-area {\n" +
                 "\t\t\tdisplay: flex;\n" +
                 "\t\t\tjustify-content: center;\n" +
                 "\t\t}\n" +
                 "\t\t.brand-logo-area img {\n" +
                 "\t\t\twidth: 148px;\n" +
                 "\t\t}\n" +
                 "\t\t.main-content {\n" +
                 "\t\t\tpadding: 10px;\n" +
                 "\t\t\tposition: relative;\n" +
                 "\t\t}\n" +
                 "\t\t.background {\n" +
                 "\t\t\tposition: absolute;\n" +
                 "\t\t    width: 100%;\n" +
                 "\t\t    height: 92vh;\n" +
                 "\t\t    top: 0px;\n" +
                 "\t\t    left: 0px;\n" +
                 "\t\t    display: flex;\n" +
                 "\t\t    align-items: center;\n" +
                 "\t\t    opacity: 0.2;\n" +
                 "\t\t    justify-content: center;\n" +
                 "\t\t    z-index: -1;\n" +
                 "\t\t}\n" +
                 "\t\t.company-name {\n" +
                 "\t\t\ttext-align: center;\n" +
                 "\t\t\tmargin-top: -14px;\n" +
                 "    \t\tmargin-bottom: 40px;\n" +
                 "\t\t}\n" +
                 "\t\ttable {\n" +
                 "\t\t\twidth: 100%;\n" +
                 "\t\t}\n" +
                 "\t\tth {\n" +
                 "\t\t\ttext-align: left;\n" +
                 "\t\t}\n" +
                 "\t\tth, td {\n" +
                 "\t\t\tpadding: 10px;\n" +
                 "\t\t}\n" +
                 "\t\ttable, th, td {\n" +
                 "\t\t\tborder: 1px solid #222222;\n" +
                 "\t\t\tborder-collapse: collapse;\n" +
                 "\t\t}\n" +
                 "\t\t.total-bill {\n" +
                 "\t\t\ttext-align: right;\n" +
                 "\t\t\tpadding-right: 20px;\n" +
                 "\t\t}\n" +
                 "\t\t@media print {\n" +
                 "            body {\n" +
                 "                -webkit-print-color-adjust: exact;\n" +
                 "            }\n@page { margin: 0; }" +
                 "        }\n" +
                 "\t</style>\n" +
                 "</head>\n" +
                 "<body>\n" +
                 "\t<div class=\"header\"><span>");
         stringBuilder.append("Bill Report");
         stringBuilder.append("</span>");
         stringBuilder.append("</div>\n" +
                 "\n" +
                 "\t<div class=\"main-content\">\n" +
                 "<div class=\"brand-logo-area\">\n" +
                 "\t\t\t<img src=\"images/logo.jpg\">\n" +
                 "\t\t</div>" +
                 "\t\t<h4 class=\"company-name\">Sarwar & Son's Pure Lake Water</h4>\n" +
                 "\t\t<p>");
         stringBuilder.append("Bill Report of <b>" + personName + "</b> from <b>" +
                 tableModel.getValueAt(0, 2) + "</b> to <b>" +
                 tableModel.getValueAt(tableModel.getRowCount() - 1, 2) + "</b>");
         stringBuilder.append("</p>\n" +
                 "\n" +
                 "\t\t<table>\n" +
                 "\t\t\t<tr>\n" +
                 "\t\t\t\t<th>Bill NUmber</th>\n" +
                 "\t\t\t\t<th>Date</th>\n" +
                 "\t\t\t\t<th>Total Bill</th>\n" +
                 "\t\t\t\t<th>Amount Recieved</th>\n" +
                 "\t\t\t\t<th>Remaining Bill</th>\n" +
                 "\t\t\t</tr>");

         String rows = "";
         for(int i = 0; i < tableModel.getRowCount(); i++) {
             rows += "<tr>";
             rows += "<td>" + tableModel.getValueAt(i, 1) + "</td>";
             rows += "<td>" + tableModel.getValueAt(i, 2) + "</td>";
             rows += "<td>" + tableModel.getValueAt(i, 3) + "</td>";
             rows += "<td>" + tableModel.getValueAt(i, 4) + "</td>";
             rows += "<td>" + tableModel.getValueAt(i, 5) + "</td>";
             rows += "</tr>";
         }

         stringBuilder.append(rows);
         stringBuilder.append("</table><div class=\"background\">\n" +
                 "\t\t\t<img src=\"images/background.jpg\">\n" +
                 "\t\t</div>");
         stringBuilder.append("<p style='text-align: right;'><b>Total Recievable = " + total + "</b></p>");
         stringBuilder.append("</div>\n" +
                 "\n" +
                 "\t<script type=\"text/javascript\">\n" +
                 "\t\tprint();\n" +
                 "\t</script>\n" +
                 "</body>\n" +
                 "</html>");

         String html = stringBuilder.toString();
         openHTMLFile(html);
     }

    static void printLiabilityReport(String personName, DefaultTableModel tableModel, int total) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<title>Liability Report</title>\n" +
                "\t<style type=\"text/css\">\n" +
                "\t\tbody {\n" +
                "\t\t\tfont-family: 'open.Sans',sans-serif;\n" +
                "\t\t\tmargin: 0;\n" +
                "\t\t}\n" +
                "\t\t.header {\n" +
                "\t\t\ttext-align: center;\n" +
                "\t\t    color: #ffffff;\n" +
                "\t\t    font-size: 15px;\n" +
                "\t\t    background-color: #5876dc;\n" +
                "\t\t    padding: 16px;\n" +
                "\t\t    font-weight: bold;\n" +
                "\t\t}\n" +
                "\t\t.brand-logo-area {\n" +
                "\t\t\tdisplay: flex;\n" +
                "\t\t\tjustify-content: center;\n" +
                "\t\t}\n" +
                "\t\t.brand-logo-area img {\n" +
                "\t\t\twidth: 148px;\n" +
                "\t\t}\n" +
                "\t\t.main-content {\n" +
                "\t\t\tpadding: 10px;\n" +
                "\t\t\tposition: relative;\n" +
                "\t\t}\n" +
                "\t\t.background {\n" +
                "\t\t\tposition: absolute;\n" +
                "\t\t    width: 100%;\n" +
                "\t\t    height: 92vh;\n" +
                "\t\t    top: 0px;\n" +
                "\t\t    left: 0px;\n" +
                "\t\t    display: flex;\n" +
                "\t\t    align-items: center;\n" +
                "\t\t    opacity: 0.2;\n" +
                "\t\t    justify-content: center;\n" +
                "\t\t    z-index: -1;\n" +
                "\t\t}\n" +
                "\t\t.company-name {\n" +
                "\t\t\ttext-align: center;\n" +
                "\t\t\tmargin-top: -14px;\n" +
                "    \t\tmargin-bottom: 40px;\n" +
                "\t\t}\n" +
                "\t\ttable {\n" +
                "\t\t\twidth: 100%;\n" +
                "\t\t}\n" +
                "\t\tth {\n" +
                "\t\t\ttext-align: left;\n" +
                "\t\t}\n" +
                "\t\tth, td {\n" +
                "\t\t\tpadding: 10px;\n" +
                "\t\t}\n" +
                "\t\ttable, th, td {\n" +
                "\t\t\tborder: 1px solid #222222;\n" +
                "\t\t\tborder-collapse: collapse;\n" +
                "\t\t}\n" +
                "\t\t.total-bill {\n" +
                "\t\t\ttext-align: right;\n" +
                "\t\t\tpadding-right: 20px;\n" +
                "\t\t}\n" +
                "\t\t@media print {\n" +
                "            body {\n" +
                "                -webkit-print-color-adjust: exact;\n" +
                "            }\n@page { margin: 0; }" +
                "        }\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<div class=\"header\"><span>");
        stringBuilder.append("Liability Report");
        stringBuilder.append("</span>");
        stringBuilder.append("</div>\n" +
                "\n" +
                "\t<div class=\"main-content\">\n" +
                "<div class=\"brand-logo-area\">\n" +
                "\t\t\t<img src=\"images/logo.jpg\">\n" +
                "\t\t</div>" +
                "\t\t<h4 class=\"company-name\">Sarwar & Son's Pure Lake Water</h4>\n" +
                "\t\t<p>");
        stringBuilder.append("Liability Report of <b>" + personName + "</b> from <b>" +
                tableModel.getValueAt(0, 2) + "</b> to <b>" +
                tableModel.getValueAt(tableModel.getRowCount() - 1, 2) + "</b>");
        stringBuilder.append("</p>\n" +
                "\n" +
                "\t\t<table>\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<th>Bill NUmber</th>\n" +
                "\t\t\t\t<th>Date</th>\n" +
                "\t\t\t\t<th>Total Bill</th>\n" +
                "\t\t\t\t<th>Amount Payed</th>\n" +
                "\t\t\t\t<th>Remaining Liability</th>\n" +
                "\t\t\t</tr>");

        String rows = "";
        for(int i = 0; i < tableModel.getRowCount(); i++) {
            rows += "<tr>";
            rows += "<td>" + tableModel.getValueAt(i, 1) + "</td>";
            rows += "<td>" + tableModel.getValueAt(i, 2) + "</td>";
            rows += "<td>" + tableModel.getValueAt(i, 3) + "</td>";
            rows += "<td>" + tableModel.getValueAt(i, 4) + "</td>";
            rows += "<td>" + tableModel.getValueAt(i, 5) + "</td>";
            rows += "</tr>";
        }

        stringBuilder.append(rows);
        stringBuilder.append("</table><div class=\"background\">\n" +
                "\t\t\t<img src=\"images/background.jpg\">\n" +
                "\t\t</div>");
        stringBuilder.append("<p style='text-align: right;'><b>Total Liability = " + total + "</b></p>");
        stringBuilder.append("</div>\n" +
                "\n" +
                "\t<script type=\"text/javascript\">\n" +
                "\t\tprint();\n" +
                "\t</script>\n" +
                "</body>\n" +
                "</html>");

        String html = stringBuilder.toString();
        openHTMLFile(html);
    }

    static void printTotalLiabilitiesReport() {

    }

    static void printTotalRecievablesReport() {

    }

    static void printTotalSalesReport() {

    }

    static void printTotalPurchasesReport() {

    }

    private static void openHTMLFile(String html) {
        try {
            File file = new File("invoice.html");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(html);
            writer.close();

            String path = file.getAbsolutePath();
            path = path.replace("\\", "/");
            path = path.replace(" ", "%20");
            Desktop.getDesktop().browse(new URI("file:///" + path));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import java.sql.*;

class DBHelper {

    private static final DBHelper dbHelper = new DBHelper();
    private Connection c;

    private DBHelper() { }

    static DBHelper getDBHelper() {
        return dbHelper;
    }

    boolean openConnection() {
        try {
            c = DriverManager.getConnection("jdbc:sqlite:" + Constants.DB_NAME);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    boolean isValidUser(String username, String pass) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM login WHERE username = '" + username + "' AND password = '" + pass + "'";
        ResultSet resultSet = stmt.executeQuery(sql);
        return resultSet.next();
    }

    void insertEmployee(String name, String cnic, String phone,
                        String address, String date, String salary) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO employees ('name', 'cnic', 'phone', 'address', 'dateOfJoining', 'salary')" +
                "VALUES('" + name +"', '" + cnic + "', '" + phone + "', '" + address + "', '" + date + "', "
                + salary + ")";
        stmt.execute(sql);
    }

    ResultSet getAllEmployees() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM employees";
        return stmt.executeQuery(sql);
    }

    void updateEmployee(String id, String name, String cnic, String phone,
                        String address, String date, String salary, String advance) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE employees SET name='" + name + "', cnic='" + cnic + "', phone='"
                + phone + "', address='" + address + "', dateOfJoining='" + date + "', salary="
                + salary + ", advanceTaken=" + advance + " WHERE id=" + id;
        stmt.execute(sql);
    }

    void recordSalary(String id, String date, String salary,
                      String advanceReturn) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO empSalaryRecord ('empId', 'date', 'salary', 'advanceReturn') VALUES (" + id + ", '" + date + "', " + salary + ", " + advanceReturn + ")";
        stmt.execute(sql);
    }

    void decrementAdvance(String empId, String advanceReturn) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE employees SET advanceTaken = advanceTaken - " + advanceReturn + " WHERE id = " + empId;
        stmt.execute(sql);
    }

    void insertCustomer(String name, String cnic, String phone, String address) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO customer ('name', 'cnic', 'phone', 'address')" +
                "VALUES('" + name +"', '" + cnic + "', '" + phone + "', '" + address + "')";
        stmt.execute(sql);
    }

    void updateCustomer(String id, String name, String cnic, String phone,
                        String address) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE customer SET name='" + name + "', cnic='" + cnic + "', phone='"
                + phone + "', address='" + address + "' WHERE id=" + id;
        stmt.execute(sql);
    }

    ResultSet getAllCustomers() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM customer ORDER BY id DESC";
        return stmt.executeQuery(sql);
    }

    void insertSupplier(String name, String cnic, String phone, String address) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO supplier ('name', 'cnic', 'phone', 'address')" +
                "VALUES('" + name +"', '" + cnic + "', '" + phone + "', '" + address + "')";
        stmt.execute(sql);
    }

    void updateSupplier(String id, String name, String cnic, String phone,
                        String address) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE supplier SET name='" + name + "', cnic='" + cnic + "', phone='"
                + phone + "', address='" + address + "' WHERE id=" + id;
        stmt.execute(sql);
    }

    ResultSet getAllSuppliers() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM supplier";
        return stmt.executeQuery(sql);
    }

    void insertProduct(String volume, String description, String category,
                       String stock) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO products ('volume', 'description', 'category', 'stock')VALUES('"
                + volume + "', '" + description + "', '" + category + "', " + stock + ")";
        stmt.execute(sql);
    }

    void updateProduct(String id, String volume, String description,
                       String category, String stock) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE products SET volume='" + volume + "', description='" + description + "', category='"
                + category + "', stock=" + stock + " WHERE id=" + id;
        stmt.execute(sql);
    }

    ResultSet getAllProducts() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM products";
        return stmt.executeQuery(sql);
    }

    void recordDebit(String day, String month, String year, String amount) throws SQLException {
        String date = day + "/" + month + "/" + year;
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO bankRecords ('date', 'debit')VALUES('" + date + "', " + amount + ")";
        stmt.execute(sql);

        increaseBalance(amount);
    }

    void recordCredit(String day, String month, String year, String amount) throws SQLException {
        String date = day + "/" + month + "/" + year;
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO bankRecords ('date', 'credit')VALUES('" + date + "', " + amount + ")";
        stmt.execute(sql);

        deductBalance(amount);
    }

    private void increaseBalance(String amount) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE bankBalance SET balance=balance + " + amount;
        stmt.execute(sql);
    }

    private void deductBalance(String amount) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE bankBalance SET balance=balance - " + amount;
        stmt.execute(sql);
    }

    ResultSet getAllTransactions() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM bankRecords";
        return stmt.executeQuery(sql);
    }

    void updateTransaction(String id, String date, String credit,
                           String debit) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE bankRecords SET date='" + date + "', credit=" +
                credit + ", debit=" + debit + " WHERE id=" + id;
        stmt.execute(sql);
    }

    void adjustBalance(int amount) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE bankBalance SET balance=balance + " + amount;
        stmt.execute(sql);
    }

    ResultSet getBankBalance() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM bankBalance";
        return stmt.executeQuery(sql);
    }

    void insertRawMaterial(String volume, String description, String category,
                       String stock) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO rawMaterial ('volume', 'description', 'category', 'stock')VALUES('"
                + volume + "', '" + description + "', '" + category + "', " + stock + ")";
        stmt.execute(sql);
    }

    ResultSet getAllRawMaterials() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM rawMaterial";
        return stmt.executeQuery(sql);
    }

    void updateRawMaterial(String id, String volume, String description,
                       String category, String stock) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE rawMaterial SET volume='" + volume + "', description='" + description + "', category='"
                + category + "', stock=" + stock + " WHERE id=" + id;
        stmt.execute(sql);
    }

    String insertSale(String date, String billNumber, String customerId, int totalBill, String amountRecieved)
            throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO sales ('dateTime', 'billNumber', 'customerId', 'totalBill', 'amountRecieved')" +
                "VALUES('" + date + "', " + billNumber + ", " + customerId + ", " + totalBill + ", " + amountRecieved + ")";
        stmt.execute(sql);

        sql = "SELECT id FROM sales ORDER BY id DESC LIMIT 1";
        ResultSet resultSet = stmt.executeQuery(sql);
        resultSet.next();
        return resultSet.getString("id");
    }

    void insertSubSale(String saleId, String productId, String price,
                         String quantitySold) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO subSales ('saleId', 'productId','price', 'quantitySold')VALUES("
                + saleId + ", " + productId + ", " + price + ", " + quantitySold + ")";
        stmt.execute(sql);

        decreaseProductQuantity(productId, quantitySold);
    }

    private void decreaseProductQuantity(String prosuctId,
                                         String quantitySold) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE products SET stock = stock - " + quantitySold + " WHERE id = " + prosuctId;
        stmt.execute(sql);
    }

    ResultSet getAllSales() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT sales.id, sales.billNumber, sales.dateTime, sales.totalBill, " +
                "sales.amountRecieved, customer.name AS customerName FROM sales" +
                " INNER JOIN customer ON sales.customerId = customer.id ORDER BY sales.id DESC";
        return stmt.executeQuery(sql);
    }

    ResultSet getSubSale(String saleId) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT subSales.rowid, products.volume || \"~\" || products.category AS volume, subSales.price, subSales.quantitySold FROM" +
                " subSales INNER JOIN products ON products.id = subSales.productId WHERE subSales.saleId = " + saleId;
        return stmt.executeQuery(sql);
    }

    ResultSet getAllPurchases() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT purchases.id, purchases.dateTime, purchases.totalBill," +
                " purchases.amountPayed, supplier.name AS supplierName FROM purchases" +
                " INNER JOIN supplier ON purchases.supplierId = supplier.id ORDER BY purchases.id DESC";
        return stmt.executeQuery(sql);
    }

    ResultSet getSubPurchase(String purchaseId) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT rawMaterial.volume AS volume, subPurchases.price, subPurchases.quantityPurchased FROM" +
                " subPurchases INNER JOIN rawMaterial ON rawMaterial.id = subPurchases.rawMaterialId WHERE subPurchases.purchaseId = " + purchaseId;
        return stmt.executeQuery(sql);
    }

    String insertPurchase(String date, String supplierId, int totalBill, String amountPayed)
            throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO purchases ('dateTime', 'supplierId', 'totalBill', 'amountPayed')" +
                "VALUES('" + date + "', " + supplierId + ", " + totalBill + ", " + amountPayed + ")";
        stmt.execute(sql);

        sql = "SELECT id FROM purchases ORDER BY id DESC LIMIT 1";
        ResultSet resultSet = stmt.executeQuery(sql);
        resultSet.next();
        return resultSet.getString("id");
    }

    void insertSubPurchase(String purchaseId, String materialId, String price,
                       String quantityPurchased) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO subPurchases ('purchaseId', 'rawMaterialId','price', 'quantityPurchased')VALUES("
                + purchaseId + ", " + materialId + ", " + price + ", " + quantityPurchased + ")";
        stmt.execute(sql);

        increaseMaterialQuantity(materialId, quantityPurchased);
    }

    private void increaseMaterialQuantity(String materialId,
                                  String quantityPurchased) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE rawMaterial SET stock = stock + " + quantityPurchased + " WHERE id = " + materialId;
        stmt.execute(sql);
    }

    void insertExpenseCategory(String name) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO expenseCategory ('name') VALUES ('" + name + "')";
        stmt.execute(sql);
    }

    ResultSet getExpensesCategory() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM expenseCategory";
        return stmt.executeQuery(sql);
    }

    void insertExpense(String expenseId, String date, String amount, String desc) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO expenses ('expenseCategory', 'date', 'amount', 'description')VALUES("
                + expenseId + ", '" + date + "', " + amount + ", '" + desc + "')";
        stmt.execute(sql);
    }

    ResultSet getAllExpenses() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT expenses.id, expenseCategory.name AS name, expenses.date," +
                " expenses.amount, expenses.description FROM expenses INNER" +
                " JOIN expenseCategory ON expenses.expenseCategory = expenseCategory.id" +
                " ORDER BY expenses.id DESC";
        return stmt.executeQuery(sql);
    }

    ResultSet getSalaries(String month, String year) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT employees.name, empSalaryRecord.date, empSalaryRecord.salary, " +
                "empSalaryRecord.advanceReturn FROM empSalaryRecord INNER JOIN employees " +
                "ON empSalaryRecord.empId = employees.id WHERE strftime('%m', empSalaryRecord.date)='" + month + "' AND strftime('%Y', empSalaryRecord.date)='" + year + "'";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchRecievablesReport(String customerId, String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT id, billNumber, dateTime, totalBill, amountRecieved," +
                " totalBill - amountRecieved AS remainingBill, chequeNumber FROM sales WHERE customerId" +
                " = " + customerId + " AND dateTime >= Date('" + fromDate + "') AND dateTime <= Date('" + toDate + "')";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchRecievablesReport(String customerId) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT id, billNumber, dateTime, totalBill, amountRecieved," +
                " totalBill - amountRecieved AS remainingBill, chequeNumber FROM sales WHERE customerId" +
                " = " + customerId;
        return stmt.executeQuery(sql);
    }

    ResultSet fetchLiabilitiesReport(String supplierId, String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT id, billNumber, dateTime, totalBill, amountPayed," +
                " totalBill - amountPayed AS remainingBill, chequeNumber FROM purchases WHERE supplierId" +
                " = " + supplierId + " AND dateTime >= Date('" + fromDate + "') AND dateTime <= Date('" + toDate + "')";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchLiabilitiesReport(String supplierId) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT id, billNumber, dateTime, totalBill, amountPayed," +
                " totalBill - amountPayed AS remainingBill, chequeNumber FROM purchases WHERE supplierId" +
                " = " + supplierId;
        return stmt.executeQuery(sql);
    }

    void recieveAmount(String saleId, int amountRecieved, String chequeNumber) throws SQLException {
        if(chequeNumber == null)
            chequeNumber = "";
        Statement stmt = c.createStatement();
        String sql;
        if(chequeNumber.length() > 0)
            sql = "UPDATE sales SET amountRecieved = amountRecieved + " + amountRecieved +
                     ", chequeNumber = chequeNumber || '" + chequeNumber + ",' WHERE id = " + saleId;
        else
            sql = "UPDATE sales SET amountRecieved = amountRecieved + " + amountRecieved +
                    " WHERE id = " + saleId;
        stmt.execute(sql);
    }

    void payAmount(String purchaseId, int amountPayed, String chequeNumber) throws SQLException {
        if(chequeNumber == null)
            chequeNumber = "";
        Statement stmt = c.createStatement();
        String sql;
        if(chequeNumber.length() > 0)
            sql = "UPDATE purchases SET amountPayed = amountPayed + " + amountPayed +
                    ", chequeNumber = chequeNumber || '" + chequeNumber + ",' WHERE id = " + purchaseId;
        else
            sql = "UPDATE purchases SET amountPayed = amountPayed + " + amountPayed +
                    " WHERE id = " + purchaseId;
        stmt.execute(sql);
    }

    void insertAdvanceRecord(String empId, String date, String advanceTaken,
                             String advanceReturned) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO empAdvancesRecord VALUES('" + empId + "', '" + date + "', '"
                + advanceTaken + "', '" + advanceReturned + "')";
        stmt.execute(sql);
    }

    void incrementEmpAdvance(String empId, String amount) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE employees SET advanceTaken = advanceTaken + " + amount + " WHERE id = " + empId;
        stmt.execute(sql);
    }

    ResultSet getAdvancesRecord(String empId) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT employees.name, empAdvancesRecord.date, empAdvancesRecord.advanceTaken," +
                " empAdvancesRecord.advanceReturned FROM empAdvancesRecord" +
                " INNER JOIN employees ON employees.id=empAdvancesRecord.empId WHERE empId=" + empId;
        return stmt.executeQuery(sql);
    }

    void deleteSale(String saleId) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "DELETE FROM subSales WHERE saleId=" + saleId;
        stmt.execute(sql);

        sql = "DELETE FROM sales WHERE id=" + saleId;
        stmt.execute(sql);
    }

    void deletePurchase(String purchaseId) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "DELETE FROM subPurchases WHERE purchaseId=" + purchaseId;
        stmt.execute(sql);

        sql = "DELETE FROM purchases WHERE id=" + purchaseId;
        stmt.execute(sql);
    }

    void deleteExpense(String id) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "DELETE FROM expenses WHERE id=" + id;
        stmt.execute(sql);
    }

    void deleteEmployee(String id) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "DELETE FROM empAdvancesRecord WHERE empId=" + id;
        stmt.execute(sql);

        sql = "DELETE FROM empSalaryRecord WHERE empId=" + id;
        stmt.execute(sql);

        sql = "DELETE FROM employees WHERE id=" + id;
        stmt.execute(sql);
    }

    int productExists(String volume, String category) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM products WHERE volume='" + volume + "' AND category='" + category + "' LIMIT 1";
        ResultSet resultSet = stmt.executeQuery(sql);
        if(resultSet.next()) {
            return resultSet.getInt("id");
        }
        else {
            return -1;
        }
    }

    void updateSale(String rowId, int productId, int price,
                       int quantitySold) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE subSales SET productId=" + productId + ", price=" + price +
                     ", quantitySold=" + quantitySold + " WHERE rowid=" + rowId;
        stmt.execute(sql);
    }

    void updateSaleTotalBill(String saleId, int totalBillAddition) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "UPDATE sales SET totalBill=totalBill + " + totalBillAddition + " WHERE id=" + saleId;
        stmt.execute(sql);
    }

    ResultSet fetchTotalLiabilities() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT supplier.name AS supplierName, supplier.id AS supplierId, " +
                     "SUM(purchases.totalBill - purchases.amountPayed) AS payable FROM purchases" +
                     " INNER JOIN supplier ON supplier.id=purchases.supplierId WHERE " +
                     "(purchases.totalBill - purchases.amountPayed) > 0 GROUP BY supplierId";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchTotalLiabilities(String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT supplier.name AS supplierName, supplier.id AS supplierId, " +
                "SUM(purchases.totalBill - purchases.amountPayed) AS payable FROM purchases" +
                " INNER JOIN supplier ON supplier.id=purchases.supplierId WHERE dateTime >= " +
                "Date('" + fromDate + "') AND dateTime <= Date('" + toDate + "') AND " +
                "(purchases.totalBill - purchases.amountPayed) > 0 GROUP BY supplierId ";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchTotalRecievables() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT customer.name AS customerName, customer.id AS customerId, " +
                "SUM(sales.totalBill - sales.amountRecieved) AS recievable FROM sales" +
                " INNER JOIN customer ON customer.id=sales.customerId WHERE " +
                "(sales.totalBill - sales.amountRecieved) > 0 GROUP BY customerId";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchTotalRecievables(String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT customer.name AS customerName, customer.id AS customerId, " +
                "SUM(sales.totalBill - sales.amountRecieved) AS recievable FROM sales" +
                " INNER JOIN customer ON customer.id=sales.customerId WHERE dateTime >= " +
                "Date('" + fromDate + "') AND dateTime <= Date('" + toDate + "') AND " +
                "(sales.totalBill - sales.amountRecieved) > 0 GROUP BY customerId ";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchTotalSales() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT customer.name AS customerName, sales.dateTime, sales.totalBill, (sales.totalBill" +
                " - sales.amountRecieved) AS remainingAmount FROM sales INNER JOIN customer ON" +
                " customer.id = sales.customerId";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchTotalSales(String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT customer.name AS customerName, sales.dateTime, sales.totalBill, (sales.totalBill" +
                " - sales.amountRecieved) AS remainingAmount FROM sales INNER JOIN customer ON" +
                " customer.id = sales.customerId WHERE sales.dateTime >= Date('" + fromDate + "') AND " +
                "sales.dateTime <= Date('" + toDate + "')";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchTotalPurchases() throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT supplier.name AS supplierName, purchases.dateTime, purchases.totalBill, (purchases.totalBill" +
                " - purchases.amountPayed) AS remainingAmount FROM purchases INNER JOIN supplier ON" +
                " supplier.id = purchases.supplierId";
        return stmt.executeQuery(sql);
    }

    ResultSet fetchTotalPurchases(String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT supplier.name AS supplierName, purchases.dateTime, purchases.totalBill, (purchases.totalBill" +
                " - purchases.amountPayed) AS remainingAmount FROM purchases INNER JOIN supplier ON" +
                " supplier.id = purchases.supplierId WHERE purchases.dateTime >= Date('" + fromDate + "') AND " +
                "purchases.dateTime <= Date('" + toDate + "')";
        return stmt.executeQuery(sql);
    }

    ResultSet getSalesSum(String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT SUM(totalBill) AS salesSum FROM sales WHERE dateTime >= Date('" + fromDate + "') AND dateTime <= Date('" + toDate + "')";
        return stmt.executeQuery(sql);
    }

    ResultSet getExpensesSum(String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT SUM(amount) AS expensesSum FROM expenses WHERE date >= Date('" + fromDate + "') AND date <= Date('" + toDate + "')";
        return stmt.executeQuery(sql);
    }

    ResultSet getPurchasesSum(String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT SUM(totalBill) AS purchasesSum FROM purchases WHERE dateTime >= Date('" + fromDate + "') AND dateTime <= Date('" + toDate + "')";
        return stmt.executeQuery(sql);
    }

    ResultSet getSalariesSum(String fromDate, String toDate) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "SELECT SUM(salary) AS salariesSum FROM empSalaryRecord WHERE date >= Date('" + fromDate + "') AND date <= Date('" + toDate + "')";
        return stmt.executeQuery(sql);
    }

    void insertOpeningBalance(String date, String balance) throws SQLException {
        Statement stmt = c.createStatement();
        String sql = "INSERT INTO balances ('yearMonth', 'openingBalance') VALUES ('" + date + "', '" + balance + "')";
        stmt.execute(sql);
    }

    boolean balanceRecordEmpty() {
        return true;
    }
}

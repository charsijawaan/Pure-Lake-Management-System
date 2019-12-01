public class Expense {

    private String id;
    private String name;

    Expense(String id, String name) {
        this.id = id;
        this.name = name;
    }

    String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

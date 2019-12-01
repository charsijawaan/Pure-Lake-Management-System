class Customer {

    private String id;
    private String name;

    Customer(String id, String name) {
       this.id = id;
       this.name = name;
    }

    String getId() {
        return this.id;
    }

    String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

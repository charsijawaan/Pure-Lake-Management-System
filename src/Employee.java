class Employee {

    String id;
    String name;

    Employee(String id, String name) {
        this.id = id;
        this.name = name;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + " : " + name;
    }
}

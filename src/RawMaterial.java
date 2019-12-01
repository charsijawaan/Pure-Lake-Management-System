public class RawMaterial {

    private String id;
    private String volume;
    private String category;
    private int quantityInStock;

    RawMaterial(String id, String volume, String category, int stock) {
        this.id = id;
        this.volume = volume;
        this.category = category;
        this.quantityInStock = stock;
    }

    String getId() {
        return this.id;
    }

    String getVolume() {
        return this.volume;
    }

    int getQuantityInStock() {
        return this.quantityInStock;
    }

    @Override
    public String toString() {
        return this.volume + " " + this.category;
    }
}

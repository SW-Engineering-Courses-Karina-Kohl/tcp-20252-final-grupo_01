package model;

public class Place {
    private static int nextId = 1;
    private final int id = nextId++;
    private String name;
    private String adress;
    private int capacity;

    public Place(String name, String adress, int max) {
        this.name = name;
        this.adress = adress;
        this.capacity = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String newAdress) {
        this.adress = newAdress;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int newCapacity) {
        // if (newCapacity < 0) throw new IllegalArgumentException("capacity cannot be negative");
        this.capacity = newCapacity;
    }

    public int getId() {
        return id;
    }
}

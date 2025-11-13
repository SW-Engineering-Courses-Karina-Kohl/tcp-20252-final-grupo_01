package models;

public class Place {
    private static int nextId = 1;
    private final int id = nextId++;
    private String name;
    private String address;
    private int capacity;

    public Place(String name, String address, int max) {
        this.name = name;
        this.address = address;
        this.capacity = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String newAdress) {
        this.address = newAdress;
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

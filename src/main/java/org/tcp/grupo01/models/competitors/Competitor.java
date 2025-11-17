package org.tcp.grupo01.models.competitors;

public abstract class Competitor {
    private static int nextId = 1;
    private final int id = nextId++;
    private String name;

    protected Competitor(String name) { this.name = name; }

    public final int getId() { return id; }
    public final String getName() { return name; }
    public final void setName(String name) { this.name = name; }
}

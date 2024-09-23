package org.vaadin.addons.componentfactory;

/**
 * Simple test data.<br>
 * <br>
 * Has NO equals/hash impl. Please use {@link Entity} in that case.
 */
public class Data {
    private final int id;

    public Data(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
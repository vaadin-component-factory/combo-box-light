package org.vaadin.addons.componentfactory;

import java.util.Objects;

/**
 * Extends Data to provide equals/hashcode impl.
 */
public class Entity extends Data {

    private String name;

    public Entity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Data data = (Data) o;
        return Objects.equals(getId(), data.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
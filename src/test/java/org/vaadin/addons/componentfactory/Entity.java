package org.vaadin.addons.componentfactory;

import java.util.Objects;

/**
 * Extends Data to provide equals/hashcode impl.
 */
public class Entity extends Data {
    public Entity(int id) {
        super(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return getId() == data.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
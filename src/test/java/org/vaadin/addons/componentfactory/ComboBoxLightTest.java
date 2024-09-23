package org.vaadin.addons.componentfactory;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

public class ComboBoxLightTest {

    private static final List<Data> TEST_DATA_ITEMS = IntStream.range(0, 3)
            .mapToObj(Data::new)
            .toList();

    private static final List<Entity> TEST_ENTITY_ITEMS = IntStream.range(0, 3)
            .mapToObj(Entity::new)
            .toList();

    public static final Data TEST_DATA = new Data(1);
    public static final Entity TEST_ENTITY = new Entity(1);

    @Test
    public void test_assureDpInitOnConstruction() {
        ComboBoxLight<Entity> light = new ComboBoxLight<>();

        Assert.assertNotNull(light.getDataProvider());

        try {
            light.setItemLabelGenerator(item -> "unused");
        } catch (NullPointerException e) {
            Assert.fail("NullPointerException was thrown using setItemLabelGenerator");
        }
    }

}
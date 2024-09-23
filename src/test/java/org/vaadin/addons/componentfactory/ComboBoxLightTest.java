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
    public void test_updateClientSideValueOnReset() {
        ComboBoxLight<Entity> light = new ComboBoxLight<>();
        light.setItems(TEST_ENTITY_ITEMS);
        light.setValue(TEST_ENTITY);

        // calls a reset and regenerates internal keys based on the key mapper
        light.setItemLabelGenerator(item -> "Item " + item.getId());

        String clientSideValue = light.getElement().getProperty("value");
        Assert.assertTrue(light.getKeyMapper().containsKey(clientSideValue));
    }


}
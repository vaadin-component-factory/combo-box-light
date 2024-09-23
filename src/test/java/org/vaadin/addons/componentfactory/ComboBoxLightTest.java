package org.vaadin.addons.componentfactory;

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
    public static final Data TEST_ENTITY = new Entity(1);


}
package org.vaadin.addons.componentfactory;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import com.vaadin.flow.data.provider.ListDataProvider;
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
    public void test_allowValueWhenNotInItems() throws Throwable {
        ComboBoxLight<Entity> light = new ComboBoxLight<>();

        // we use this workaround instead of setValue since setValue will trigger an automatic key generation
        // (at this moment in time) due to the client side key update. That will mess up our test case.
        Object fs = FieldUtils.readField(light, "fieldSupport", true);
        MethodUtils.invokeMethod(fs, true, "applyValue", TEST_ENTITY);

        // check, if the key mapper contains the first generated key.
        // Should be the case, if setting a value auto registers the key
        Assert.assertTrue(light.getKeyMapper().containsKey("1"));

    }

    public void test_keyMapperUsesEqualsHashCode() {
        ComboBoxLight<Entity> light = new ComboBoxLight<>();
        light.setItems(TEST_ENTITY_ITEMS);

        Assert.assertTrue(light.getKeyMapper().has(TEST_ENTITY));
    }

    @Test
    public void test_keyMapperUsesDataProviderGetId() {
        ComboBoxLight<Data> light = new ComboBoxLight<>();
        ListDataProvider<Data> dataProvider = new ListDataProvider<>(TEST_DATA_ITEMS) {
            @Override
            public Object getId(Data item) {
                return item != null ? item.getId() : null;
            }
        };

        light.setDataProvider(dataProvider);

        Assert.assertTrue(light.getKeyMapper().has(TEST_DATA));
    }
}
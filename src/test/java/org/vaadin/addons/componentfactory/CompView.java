package org.vaadin.addons.componentfactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;

@Route("light")
public class CompView extends VerticalLayout {

    public CompView() {
        inMemory();
    }

    private void inMemory() {
        ComboBoxLight<Entity> comboBox = new ComboBoxLight<>();
        comboBox.setLabel("Values");

        List<Entity> values = IntStream.range(0, 250)
                .mapToObj(Entity::new)
                .collect(Collectors.toList());

        comboBox.setItems(values);

        initItemLabelGenerator(comboBox);

        comboBox.setValue(new Entity(123));
        comboBox.addValueChangeListener(e -> Notification.show(asUserReadable(e.getValue())));

        add(comboBox);
    }

    private static void initItemLabelGenerator(ComboBoxLight<Entity> comboBox) {
        comboBox.setItemLabelGenerator(item -> asUserReadable(item));
    }

    private static void initLitRenderer(ComboBoxLight<Entity> comboBox) {
        comboBox.setRenderer(
                LitRenderer.<Entity> of("<span style='color: var(--lumo-secondary-text-color);'>" +
                                      "<b>${item.value}</b></span>")
                        .withProperty("value", item -> asUserReadable(item)));
    }

    private static String asUserReadable(Entity item) {
        return item == null ? "" : "Item " + item.getId();
    }


}

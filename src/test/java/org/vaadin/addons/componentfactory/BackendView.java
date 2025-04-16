package org.vaadin.addons.componentfactory;

import java.util.List;
import java.util.stream.Stream;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;

@Route("backend")
public class BackendView extends VerticalLayout {

    List<Entity> values = Stream.iterate(0, i -> i + 1)
            .limit(250)
            .map(i -> new Entity(i, "Name " + i))
            .toList();
    DataProvider<Entity, String> dataProvider = DataProvider.fromFilteringCallbacks(
            query -> values.stream()
                    .filter(entity -> entity.getName().contains(query.getFilter().orElse("")))
                    .limit(20),
            query -> (int) values.stream()
                    .filter(entity -> entity.getName().contains(query.getFilter().orElse(""))).limit(20).count());

    public BackendView() {
        backend();
    }

    private void backend() {
        ComboBoxLight<Entity> comboBox = new ComboBoxLight<>();
        comboBox.setLabel("Values");

        comboBox.setDataProvider(dataProvider);

        initItemLabelGenerator(comboBox);

        comboBox.setValue(new Entity(123, "Name 123"));
        comboBox.addValueChangeListener(e -> {
            if (e.isFromClient())
                Notification.show(asUserReadable(e.getValue()));
        });
        comboBox.setClearButtonVisible(true);
        
        Checkbox checkbox1 = new Checkbox("Autoselect", true);
        checkbox1.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                comboBox.setDisableBackendAutoselect(!e.getValue());
            }
        });

        Checkbox checkbox2 = new Checkbox("Reset", true);
        checkbox2.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                comboBox.setDisableResetAfterChange(!e.getValue());
            }
        });

        Button button = new Button("Value", event -> {
            if (comboBox.getValue() != null) {
                Notification.show("Value: " + comboBox.getValue().getName());
            } else {
                Notification.show("No value selected");
            }
        });
 
        add(comboBox, checkbox1, checkbox2, button);
    }

    private static void initItemLabelGenerator(ComboBoxLight<Entity> comboBox) {
        comboBox.setItemLabelGenerator(BackendView::asUserReadable);
    }

    private static void initLitRenderer(ComboBoxLight<Entity> comboBox) {
        comboBox.setRenderer(
                LitRenderer.<Entity>of("<span style='color: var(--lumo-secondary-text-color);'>" +
                        "<b>${item.value}</b></span>")
                        .withProperty("value", item -> asUserReadable(item)));
    }

    private static String asUserReadable(Entity item) {
        return item == null ? "" : item.getName();
    }

}

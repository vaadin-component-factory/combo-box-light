package org.vaadin.addons.componentfactory;

import java.util.List;
import java.util.Objects;
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
        ComboBoxLight<Data> comboBox = new ComboBoxLight<>();
        comboBox.setLabel("Values");

        List<Data> values = IntStream.range(0, 250)
                .mapToObj(Data::new)
                .collect(Collectors.toList());

        comboBox.setItems(values);

        initItemLabelGenerator(comboBox);

        comboBox.setValue(new Data(123));
        comboBox.addValueChangeListener(e -> Notification.show(asUserReadable(e.getValue())));


        add(comboBox);
    }

    private static void initItemLabelGenerator(ComboBoxLight<Data> comboBox) {
        comboBox.setItemLabelGenerator(item -> asUserReadable(item));
    }

    private static void initLitRenderer(ComboBoxLight<Data> comboBox) {
        comboBox.setRenderer(
                LitRenderer.<Data> of("<span style='color: var(--lumo-secondary-text-color);'>" +
                                      "<b>${item.value}</b></span>")
                        .withProperty("value", item -> asUserReadable(item)));
    }

    private static String asUserReadable(Data item) {
        return "Item " + item.getId();
    }


    public static class Data {
        private final int id;

        public Data(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return id == data.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
}

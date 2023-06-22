package org.vaadin.addons.componentfactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;

@Route("light")
public class CompView extends VerticalLayout {

    public CompView() {
        ComboBoxLight<Data> comboBox = new ComboBoxLight<Data>();
        comboBox.setLabel("Values");
        List<Data> values = IntStream.range(0, 250)
                .mapToObj(i -> new Data(i + " value"))
                .collect(Collectors.toList());
        comboBox.setRenderer(
                LitRenderer.<Data> of("<span style='color: var(--lumo-secondary-text-color);'><b>${item.value}</b></span>")
                        .withProperty("value", d -> d.getValue()));
        comboBox.setItems(values);
        comboBox.setItemLabelGenerator(Data::getValue);
        comboBox.addValueChangeListener(e -> {
            Notification.show(e.getValue().getValue());
        });
        comboBox.setValue(values.get(123));

        add(comboBox);
    }

    public static class Data {
        private String value;

        public Data(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

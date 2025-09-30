package org.vaadin.addons.componentfactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.data.provider.DataCommunicator.EmptyDataProvider;
import com.vaadin.flow.component.*;

import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

public class ComboBoxLight<T> extends AbstractComboBox<ComboBoxLight<T>, T>
        implements HasSize, HasValidation, HasDataProvider<T>, HasHelper {

    private DataProvider<T, ?> dataProvider = new EmptyDataProvider<>();
    private Registration dataProviderListenerRegistration;
    private ItemLabelGenerator<T> itemLabelGenerator = String::valueOf;
    private final KeyMapper<T> keyMapper = new KeyMapper<>();
    private static final String PROP_AUTO_OPEN_DISABLED = "autoOpenDisabled";
    private static final String PROP_INPUT_ELEMENT_VALUE = "_inputElementValue";
    private int customValueListenersCount;
    private ComboBoxLightRenderManager<T> renderManager;
    private final CompositeDataGenerator<T> dataGenerator = new CompositeDataGenerator<>();
    private boolean autoselect = true;
    private boolean resetAfterChange = true;

    private class CustomValueRegistration implements Registration {

        private Registration delegate;

        private CustomValueRegistration(Registration delegate) {
            this.delegate = delegate;
        }

        @Override
        public void remove() {
            if (delegate != null) {
                delegate.remove();
                customValueListenersCount--;

                if (customValueListenersCount == 0) {
                    setAllowCustomValue(false);
                }
                delegate = null;
            }
        }
    }

    private static <T> T presentationToModel(ComboBoxLight<T> comboBox,
            String presentation) {
        DataKeyMapper<T> keyMapper = comboBox.getKeyMapper();

        if (presentation == null || keyMapper == null) {
            return comboBox.getEmptyValue();
        }
        return keyMapper.get(presentation);
    }

    private static <T> String modelToPresentation(ComboBoxLight<T> comboBox,
            T model) {
        DataKeyMapper<T> keyMapper = comboBox.getKeyMapper();

        if (model == null || keyMapper == null) {
            return null;
        }
        return keyMapper.key(model);
    }

    public ComboBoxLight() {
        super(null, null, String.class, ComboBoxLight::presentationToModel,
                ComboBoxLight::modelToPresentation, true);
        setItemValuePath("key");
        setItemIdPath("key");

        renderManager = new ComboBoxLightRenderManager<>(this);

        super.addCustomValueSetListener(e -> this.getElement()
                .setProperty(PROP_INPUT_ELEMENT_VALUE, e.getDetail()));
        super.addValueChangeListener(e -> updateSelectedKey());
        addFilterChangeListener(e -> {
            if (e.isFromClient() && e.getFilter() != null
                    && !e.getFilter().isEmpty() 
                    && getDataProvider() instanceof BackEndDataProvider) {
                reset();
            }
        });
        addValueChangeListener(e -> {
            if (!resetAfterChange || !e.isFromClient()) {
                return;
            }
            if (dataProvider instanceof BackEndDataProvider) {
                setFilter("");
                reset();
                if (e.getValue() == null) {
                    return;
                }
                if (keyMapper.has(e.getValue())) {
                    getElement().executeJs("return 0;")
                            .then(res -> updateSelectedKey());
                }
            }
        });
    }

    private void updateSelectedKey() {
        // Send (possibly updated) key for the selected value
        T value = getValue();

        // when there is a value and the key mapper knows it, update the client,
        // otherwise reset
        getElement().setProperty("value",
                value != null && keyMapper.has(value) ? keyMapper.key(value)
                        : "");
    }

    public void setItemLabelGenerator(
            ItemLabelGenerator<T> itemLabelGenerator) {
        Objects.requireNonNull(itemLabelGenerator,
                "The item label generator can not be null");
        this.itemLabelGenerator = itemLabelGenerator;
        reset();
    }

    public ItemLabelGenerator<T> getItemLabelGenerator() {
        return itemLabelGenerator;
    }

    @SuppressWarnings("unchecked")
    private void reset() {
        keyMapper.removeAll();
        dataGenerator.destroyAllData();
        String filter = getFilterString();
        Query<T, String> query;
        DataProvider<T, String> dataProvider = (DataProvider<T, String>) getDataProvider();
        if (filter == null || filter.trim().isEmpty()) {
            query = new Query<>();
        } else {
            query = new Query<>(filter.trim());
        }
        List<String> items = dataProvider.fetch(query)
                .map(keyMapper::key).toList();

        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode jsonItems = factory.arrayNode();
        for (String item : items) {
            ObjectNode object = factory.objectNode();
            object.put("key", item);
            object.put("label",
                    getItemLabelGenerator().apply(keyMapper.get(item)));
            dataGenerator.generateData(keyMapper.get(item), object);
            jsonItems.add(object);
        }
        getElement().setPropertyJson("items", jsonItems);

        if (autoselect && dataProvider instanceof BackEndDataProvider && items.size() == 1) {
            // if there is only one item, we can set the value directly
            String key = items.getFirst();
            T item = keyMapper.get(key);
            if (item != null) {
                setModelValue(item, true);
            }
        }

        if (dataProvider instanceof BackEndDataProvider) {
            setFilter(filter);
        }

        if (!(dataProvider instanceof BackEndDataProvider)) {
            updateSelectedKey();
        }
  
    }

    /**
     * Enables or disables the dropdown opening automatically. If {@code false}
     * the dropdown is only opened when clicking the toggle button or pressing
     * Up or Down arrow keys.
     *
     * @param autoOpen
     *                 {@code false} to prevent the dropdown from opening
     *                 automatically
     */
    public void setAutoOpen(boolean autoOpen) {
        getElement().setProperty(PROP_AUTO_OPEN_DISABLED, !autoOpen);
    }

    /**
     * Gets whether dropdown will open automatically or not.
     *
     * @return @{code true} if enabled, {@code false} otherwise
     */
    public boolean isAutoOpen() {
        return !getElement().getProperty(PROP_AUTO_OPEN_DISABLED, false);
    }

    @Override
    public void setAutofocus(boolean autofocus) {
        super.setAutofocus(autofocus);
    }

    public boolean isAutofocus() {
        return isAutofocusBoolean();
    }

    @Override
    public void setPreventInvalidInput(boolean preventInvalidInput) {
        super.setPreventInvalidInput(preventInvalidInput);
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
    }

    public String getLabel() {
        return getLabelString();
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        super.setErrorMessage(errorMessage);
    }

    @Override
    public String getErrorMessage() {
        return super.getErrorMessageString();
    }

    @Override
    public void setInvalid(boolean invalid) {
        super.setInvalid(invalid);
    }

    @Override
    public boolean isInvalid() {
        return super.isInvalidBoolean();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        super.setRequiredIndicatorVisible(requiredIndicatorVisible);
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }

    public void setClearButtonVisible(boolean clearButtonVisible) {
        getElement().setProperty("clearButtonVisible", clearButtonVisible);
    }

    public boolean isClearButtonVisible() {
        return getElement().getProperty("clearButtonVisible", false);
    }

    @Override
    public void setOpened(boolean opened) {
        super.setOpened(opened);
    }

    public boolean isOpened() {
        return isOpenedBoolean();
    }

    @Override
    public void setPlaceholder(String placeholder) {
        super.setPlaceholder(placeholder);
    }

    public String getPlaceholder() {
        return getPlaceholderString();
    }

    @Override
    public void setPattern(String pattern) {
        super.setPattern(pattern);
    }

    public String getPattern() {
        return getPatternString();
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        this.dataProvider = Objects.requireNonNull(dataProvider,
                "Data provider must not be null");
        keyMapper.setIdentifierGetter(dataProvider::getId); // same as
                                                            // DataCommunicator

        reset();

        renderManager.scheduleRender();
        setupDataProviderListener(dataProvider);
    }

    private void setupDataProviderListener(DataProvider<T, ?> dataProvider) {
        if (dataProviderListenerRegistration != null) {
            dataProviderListenerRegistration.remove();
        }
        dataProviderListenerRegistration = dataProvider
                .addDataProviderListener(event -> reset());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (getDataProvider() != null
                && dataProviderListenerRegistration == null) {
            setupDataProviderListener(getDataProvider());
        }
    }

    public DataProvider<T, ?> getDataProvider() {
        return dataProvider;
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (dataProviderListenerRegistration != null) {
            dataProviderListenerRegistration.remove();
            dataProviderListenerRegistration = null;
        }
        super.onDetach(detachEvent);
    }

    @Override
    public void setAllowCustomValue(boolean allowCustomValue) {
        super.setAllowCustomValue(allowCustomValue);
    }

    public boolean isAllowCustomValue() {
        return isAllowCustomValueBoolean();
    }

    @Override
    public Registration addCustomValueSetListener(
            ComponentEventListener<CustomValueSetEvent<ComboBoxLight<T>>> listener) {
        setAllowCustomValue(true);
        customValueListenersCount++;
        Registration registration = super.addCustomValueSetListener(listener);
        return new CustomValueRegistration(registration);
    }

    /**
     * Sets the Renderer responsible to render the individual items in the list
     * of possible choices of the ComboBox. It doesn't affect how the selected
     * item is rendered - that can be configured by using
     * {@link #setItemLabelGenerator(ItemLabelGenerator)}.
     *
     * @param renderer
     *                 a renderer for the items in the selection list of the
     *                 ComboBox, not <code>null</code>
     *                 <p>
     *                 Note that filtering of the ComboBox is not affected by the
     *                 renderer that is set here. Filtering is done on the original
     *                 values and can be affected by
     *                 {@link #setItemLabelGenerator(ItemLabelGenerator)}.
     */
    public void setRenderer(Renderer<T> renderer) {
        Objects.requireNonNull(renderer, "The renderer must not be null");

        renderManager.setRenderer(renderer);
    }

    /**
     * Sets whether to disable the backend dataprovider autoselect feature.
     * When disabled, the autoselect functionality will not be triggered.
     *
     * @param disableBackendAutoslect {@code true} to disable backend autoselect,
     *                                {@code false} to enable it.
     */
    public void setDisableBackendAutoselect(boolean disableBackendAutoslect) {
        autoselect = !disableBackendAutoslect;
    }

    /**
     * Sets whether the reset behavior after a change should be disabled when using backend dataprovider.
     * When set to {@code true}, the component will not reset its state after a value change.
     * When set to {@code false}, the component will reset its state after a value change.
     *
     * @param disableResetAfterChange {@code true} to disable reset after change, {@code false} to enable it.
     */
    public void setDisableResetAfterChange(boolean disableResetAfterChange) {
        resetAfterChange = !disableResetAfterChange;
    }

    /**
     * Accesses the data generator managed by this controller
     */
    protected CompositeDataGenerator<T> getDataGenerator() {
        return dataGenerator;
    }

    /**
     * Returns the used key mapper. Please note, that any changes to this
     * instance will affect the combo box.
     * 
     * @return key mapper
     */
    protected KeyMapper<T> getKeyMapper() {
        return keyMapper;
    }

    class ComboBoxLightRenderManager<T> implements Serializable {

        private final ComboBoxLight<T> comboBox;
        private Renderer<T> renderer;

        private boolean renderScheduled;
        private final List<Registration> renderingRegistrations = new ArrayList<>();

        ComboBoxLightRenderManager(ComboBoxLight<T> comboBox) {
            this.comboBox = comboBox;
        }

        void setRenderer(Renderer<T> renderer) {
            Objects.requireNonNull(renderer, "The renderer must not be null");
            this.renderer = renderer;

            scheduleRender();
        }

        void scheduleRender() {
            if (renderScheduled || renderer == null) {
                return;
            }
            renderScheduled = true;
            runBeforeClientResponse(ui -> {
                render();
                renderScheduled = false;
            });
        }

        @SuppressWarnings("unchecked")
        private void render() {
            renderingRegistrations.forEach(Registration::remove);
            renderingRegistrations.clear();

            var rendering = renderer.render(comboBox.getElement(),
                    (DataKeyMapper<T>) keyMapper);

            rendering.getDataGenerator().ifPresent(renderingDataGenerator -> {
                Registration renderingDataGeneratorRegistration = comboBox
                        .getDataGenerator()
                        .addDataGenerator(renderingDataGenerator);
                renderingRegistrations.add(renderingDataGeneratorRegistration);
            });

            renderingRegistrations.add(rendering.getRegistration());

            reset();
        }
    }

}
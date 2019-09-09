package com.dlsc.unitfx;

import com.dlsc.unitfx.skins.QuantityInputFieldSkin;
import com.dlsc.unitfx.util.ControlsUtil;
import com.dlsc.unitfx.util.QuantitiesUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.Skin;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.util.function.Predicate;

/**
 * Concrete implementation of {@link QuantityInputControl} that uses a {@link NumberInputField< Double >} to let users enter
 * values.
 */
public class QuantityInputField<Q extends Quantity<Q>> extends QuantityInputControl<Q> {

    /**
     * Instances a new input field with no available units, null value and no precision.
     */
    public QuantityInputField() {
        bindQuantityValueProperty();
        bindPrecisionQuantityProperty();
        bindValueDirtyProperty();
        listenForInvalidChanges();
        listenForAutoFixProperty();

        ControlsUtil.bindBooleanToPseudoclass(this, valueDirtyProperty(), PseudoClass.getPseudoClass("dirty"));
        getStyleClass().add("quantity-input-field");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new QuantityInputFieldSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return QuantityInputField.class.getResource("quantity-input-field.css").toExternalForm();
    }

    /**
     * Applies the {@link #valueQuantityProperty()} to the {@link #valueProperty()} simulating a restore operation.  This
     * method not only cleans the value but also cleans the {@link #valueDirtyProperty()}.
     */
    public final void restoreValueProperty() {
        Quantity<Q> quantity = getValueQuantity();
        if (quantity != null) {
            setValue(quantity.getValue().doubleValue());
            setValueDirty(false);
        }
    }


    /**
     * Integer used to truncate the {@link #valueProperty() value} entered by the user and calculate the
     * {@link #valueQuantityProperty() quantity}. Default precision is '1' which means no truncation.
     * Bellow some examples:
     *
     * <ul>
     *     <li>Value=340</li>
     *     <li>Unit=Metre</li>
     *     <li>Precision=100</li>
     *     <li><b>ValueQuantity=300 Metre</b></li>
     * </ul>
     * @return
     */
    public final ObjectProperty<Double> precisionProperty() {
        return precision;
    }
    private final ObjectProperty<Double> precision = new SimpleObjectProperty<Double>(this, "precision") {
        @Override
        public void set(Double newValue) {
            if (newValue != null && newValue <= 0) {
                throw new IllegalArgumentException("Precision must be greater than 0.");
            }
            super.set(newValue);
        }
    };
    public final Double getPrecision() { return precisionProperty().get(); }
    public final void setPrecision(Double precision) { precisionProperty().set(precision); }


    /**
     * Represents the system base unit, which is the default one for the control.  If the {@link #unitProperty() unit}
     * selected is different to the base unit, the control will indicate visually (colored) that ambiguity. If no
     * base unit is set, not color effect will be applied in the skin.
     *
     * @return The base unit.
     */
    public final ObjectProperty<Unit<Q>> precisionUnitProperty() {
        return precisionUnit;
    }
    private final ObjectProperty<Unit<Q>> precisionUnit = new SimpleObjectProperty<>(this, "precisionUnit");
    public final Unit<Q> getPrecisionUnit() { return precisionUnitProperty().get(); }
    public final void setPrecisionUnit(Unit<Q> precisionUnit) { precisionUnitProperty().set(precisionUnit); }


    /**
     * Represents the precision as combination of {@link #precisionProperty()} ()} and {@link #precisionUnitProperty()}.
     * This value is calculated automatically by the control.  It is refreshed every time the precision or the precision
     * unit change.
     * @return The read only property storing the precision quantity.
     */
    public final ReadOnlyObjectProperty<Quantity<Q>> precisionQuantityProperty() { return precisionQuantity.getReadOnlyProperty(); }
    private final ReadOnlyObjectWrapper<Quantity<Q>> precisionQuantity = new ReadOnlyObjectWrapper<>(this, "precisionQuantity");
    public final Quantity<Q> getPrecisionQuantity() { return precisionQuantityProperty().get(); }
    private void setPrecisionQuantity(Quantity<Q> precisionQuantity) { this.precisionQuantity.set(precisionQuantity); }


    /**
     * Boolean property that indicates when the {@link #valueProperty() value} and the
     * {@link #valueQuantityProperty() valueQuantity} are different because after a precision was applied.
     *
     * @return The boolean property.
     */
    public final ReadOnlyBooleanProperty valueDirtyProperty() {
        return valueDirty.getReadOnlyProperty();
    }
    private final ReadOnlyBooleanWrapper valueDirty = new ReadOnlyBooleanWrapper(this, "valueDirty");
    public final boolean isValueDirty() { return valueDirtyProperty().get(); }
    private void setValueDirty(boolean valueDirty) { this.valueDirty.set(valueDirty); }


    /**
     * Boolean property that tells the control to allow introducing a negative quantity.
     * @return The boolean property.
     */
    public final BooleanProperty allowNegativesProperty() { return allowNegatives; }
    private final BooleanProperty allowNegatives = new SimpleBooleanProperty(this, "allowNegatives");
    public final boolean isAllowNegatives() { return allowNegativesProperty().get(); }
    public final void setAllowNegatives(boolean allowNegatives) { allowNegativesProperty().set(allowNegatives); }


    /**
     * The minimum value that can be entered in this field.  If this property is set, once the user enters a number
     * lower than this minimum the field becomes invalid and the {@link #valueProperty()} is set to {@code null}.
     * @return The minimum value.
     */
    public final ObjectProperty<Double> minimumValueProperty() { return minimumValue; }
    private final ObjectProperty<Double> minimumValue = new SimpleObjectProperty<>(this, "minimumValue");
    public final Double getMinimumValue() { return minimumValueProperty().get(); }
    public final void setMinimumValue(Double minimumValue) { minimumValueProperty().set(minimumValue); }


    /**
     * The maximum value that can be entered in this field.  If this property is set, once the user enters a number
     * greater than this minimum the field becomes invalid and the {@link #valueProperty()} is set to {@code null}.
     * @return The maximum value.
     */
    public final ObjectProperty<Double> maximumValueProperty() { return maximumValue; }
    private final ObjectProperty<Double> maximumValue = new SimpleObjectProperty<>(this, "maximumValue");
    public final Double getMaximumValue() { return maximumValueProperty().get(); }
    public final void setMaximumValue(Double maximumValue) { maximumValueProperty().set(maximumValue); }


    /**
     * Property that indicates when the value entered violates restrictions of minimum and maximum values.
     * @return The property that holds the flag.
     */
    public final ReadOnlyBooleanProperty invalidProperty() { return invalid.getReadOnlyProperty(); }
    private final ReadOnlyBooleanWrapper invalid = new ReadOnlyBooleanWrapper(this, "invalid");
    public final boolean isInvalid() { return invalid.get(); }
    private void setInvalid(boolean invalid) { this.invalid.set(invalid); }



    /**
     * Stores a validator object for the value converted from text entered.  If the value entered is not valid, a pseudo
     * class is added to the field, called ":invalid".  This can be used for styling the field to indicate invalid inputs.
     * @return The property storing the validator predicate.
     */
    public final ObjectProperty<Predicate<Double>> valueValidatorProperty() { return valueValidator; }
    private final ObjectProperty<Predicate<Double>> valueValidator = new SimpleObjectProperty<>(this, "valueValidator");
    public final Predicate<Double> getValueValidator() { return valueValidatorProperty().get(); }
    public final void setValueValidator(Predicate<Double> valueValidator) { valueValidatorProperty().set(valueValidator); }


    /**
     * Boolean property used to automatically convert the {@link #valueProperty()} when the {@link #unitProperty()} is changed.
     * @return The boolean property.
     */
    public final BooleanProperty autoFixValueProperty() {
        return autoFixValue;
    }
    private final BooleanProperty autoFixValue = new SimpleBooleanProperty(this, "autoFixValue");
    public final boolean isAutoFixValue() { return autoFixValueProperty().get(); }
    public final void setAutoFixValue(boolean autoFixValue) { autoFixValueProperty().set(autoFixValue); }


    // listeners

    private void bindQuantityValueProperty() {
        precisionQuantityProperty().addListener(obs -> updateValueQuantity());
    }

    @Override
    void updateValueQuantity() {
        Quantity<Q> quantity = QuantitiesUtil.createQuantity(getValue(), getUnit());
        Quantity<Q> precision = getPrecisionQuantity();
        if (quantity !=  null && precision != null) {
            quantity = QuantitiesUtil.roundQuantity(quantity, precision);
        }
        setValueQuantity(quantity);
    }

    @Override
    void updateDefaultUnit(Unit<Q> baseUnit) {
        super.updateDefaultUnit(baseUnit);
        if (getPrecisionUnit() == null) {
            setPrecisionUnit(baseUnit);
        }
    }

    private void bindPrecisionQuantityProperty() {
        InvalidationListener listener = o -> {
            Quantity<Q> precisionQ = QuantitiesUtil.createQuantity(getPrecision(), getPrecisionUnit());
            setPrecisionQuantity(precisionQ);
        };
        precisionProperty().addListener(listener);
        precisionUnitProperty().addListener(listener);
    }

    private void bindValueDirtyProperty() {
        InvalidationListener listener = obs -> {
            Quantity<Q> valueQuantity = getValueQuantity();
            Double value = getValue();
            Unit<Q> unit = getUnit();

            if (unit != null) {
                setValueDirty(
                        (valueQuantity != null && value == null) ||
                                (valueQuantity == null && value != null) ||
                                (value != null && valueQuantity != null && value.compareTo(valueQuantity.getValue().doubleValue()) != 0)
                );
            }
        };

        valueProperty().addListener(listener);
        valueQuantityProperty().addListener(listener);
    }

    private void listenForInvalidChanges() {
        getProperties().addListener((MapChangeListener<Object, Object>) change -> {
            if (change.getKey().equals("invalid")) {
                Boolean value = (Boolean) change.getValueAdded();
                setInvalid(Boolean.TRUE.equals(value));
            }
        });
    }

    private void listenForAutoFixProperty() {
        unitProperty().addListener((obs, oldV, newV) -> {
            if (isAutoFixValue()) {
                Double value = getValue();
                if (value != null && oldV != null && newV != null) {
                    Quantity<Q> quantity = QuantitiesUtil.createQuantity(value, oldV);
                    Quantity<Q> fixedQuantity = quantity.to(newV);
                    value = fixedQuantity.getValue().doubleValue();
                    setValue(value);
                }
            }
        });
    }

}

package com.dlsc.unitfx;

import com.dlsc.unitfx.util.QuantitiesUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.util.StringConverter;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * Base class for any quantity input control that allows to enter Unit of Measurement quantity based on a JSR-363
 * specification.  The input field is Double type based, so the value is a double property.
 *
 * @param <Q> In the JSR specification represents a quantity type in conjunction with a {@link javax.measure.Dimension}.
 * @see Quantity
 * @see Unit
 * @see javax.measure.Dimension
 * @see QuantityInputField
 */
public abstract class QuantityInputControl<Q extends Quantity<Q>> extends Control {

    /**
     * Instances a new input field with no available units, null value and no precision.
     */
    public QuantityInputControl() {
        bindQuantityValueProperty();
        listenForDefaultUnit();
    }

    /**
     * Represents the quantitative amount in the specific unit.  Basically represents the number introduced by the
     * user.
     * @return A double object being the value.
     */
    public final ObjectProperty<Double> valueProperty() {
        return value;
    }
    private final ObjectProperty<Double> value = new SimpleObjectProperty<>(this, "value");
    public final Double getValue() { return valueProperty().get(); }
    public final void setValue(Double value) { valueProperty().set(value); }


    /**
     * The selected type for the quantity value.
     *
     * @return A unit object which represent the selected format.
     */
    public final ObjectProperty<Unit<Q>> unitProperty() {
        return unit;
    }
    private final ObjectProperty<Unit<Q>> unit = new SimpleObjectProperty<>(this, "unit");
    public final Unit<Q> getUnit() { return unitProperty().get(); }
    public final void setUnit(Unit<Q> unit) { unitProperty().set(unit); }


    /**
     * Represents the quantity which is the combination of {@link #valueProperty()} and {@link #unitProperty()}. This
     * value is calculated automatically by the control.  It is refreshed every time the value or the unit change.
     * @return The read only property storing the quantity.  {@code null} if no value or unit has been set.
     */
    public final ReadOnlyObjectProperty<Quantity<Q>> valueQuantityProperty() { return valueQuantity.getReadOnlyProperty(); }
    private final ReadOnlyObjectWrapper<Quantity<Q>> valueQuantity = new ReadOnlyObjectWrapper<>(this, "quantityValue");
    public final Quantity<Q> getValueQuantity() { return valueQuantityProperty().get(); }
    void setValueQuantity(Quantity<Q> valueQuantity) { this.valueQuantity.set(valueQuantity); }


    /**
     * The list of available units supported within the control, this is the list of units the user has available
     * to select from.
     * @return The list of units available in the control.
     */
    public final ObservableList<Unit<Q>> getAvailableUnits() {
        return availableUnits;
    }
    private final ObservableList<Unit<Q>> availableUnits = FXCollections.observableArrayList();


    /**
     * Represents the system base unit, which is the default one for the control.  If the {@link #unitProperty() unit}
     * selected is different to the base unit, the control will indicate visually (colored) that ambiguity. If no
     * base unit is set, not color effect will be applied in the skin.
     *
     * @return The base unit.
     */
    public final ObjectProperty<Unit<Q>> baseUnitProperty() {
        return baseUnit;
    }
    private final ObjectProperty<Unit<Q>> baseUnit = new SimpleObjectProperty<>(this, "baseUnit");
    public final Unit<Q> getBaseUnit() { return baseUnitProperty().get(); }
    public final void setBaseUnit(Unit<Q> baseUnit) { baseUnitProperty().set(baseUnit); }


    /**
     * Boolean property used to restrict the edition in the control. If this is set to {@code true} the control will be
     * disabled.
     * @return The boolean property.
     */
    public final BooleanProperty readOnlyProperty() {
        return readOnly;
    }
    private final BooleanProperty readOnly = new SimpleBooleanProperty(this, "readOnly");
    public final boolean isReadOnly() { return readOnlyProperty().get(); }
    public final void setReadOnly(boolean readOnly) { readOnlyProperty().set(readOnly); }


    /**
     * String converter that allows customization of unit label on the skin.
     * @return The property storing string converter.
     */
    public final ObjectProperty<StringConverter<Unit<Q>>> unitStringConverterProperty() { return unitStringConverter; }
    private final ObjectProperty<StringConverter<Unit<Q>>> unitStringConverter = new SimpleObjectProperty<>(this, "unitStringConverter");
    public final StringConverter<Unit<Q>> getUnitStringConverter() { return unitStringConverterProperty().get(); }
    public final void setUnitStringConverter(StringConverter<Unit<Q>> unitStringConverter) { unitStringConverterProperty().set(unitStringConverter); }



    /**
     * The maximum digits in the integer part of the number.
     * @return The maximum value.
     */
    public final IntegerProperty numberOfIntegersProperty() { return numberOfIntegers; }
    private final IntegerProperty numberOfIntegers = new SimpleIntegerProperty(this, "numberOfIntegers", 40);
    public final int getNumberOfIntegers() { return numberOfIntegersProperty().get(); }
    public final void setNumberOfIntegers(int numberOfIntegers) { numberOfIntegersProperty().set(numberOfIntegers); }


    /**
     * The maximum digits in the decimal part of the number.
     * @return The maximum value.
     */
    public final IntegerProperty numberOfDecimalsProperty() { return numberOfDecimals; }
    private final IntegerProperty numberOfDecimals = new SimpleIntegerProperty(this, "numberOfDecimals", 3);
    public final int getNumberOfDecimals() { return numberOfDecimalsProperty().get(); }
    public final void setNumberOfDecimals(int numberOfDecimals) { numberOfDecimalsProperty().set(numberOfDecimals); }


    // listeners

    private void bindQuantityValueProperty() {
        InvalidationListener listener = obs -> updateValueQuantity();
        valueProperty().addListener(listener);
        unitProperty().addListener(listener);
    }

    private void listenForDefaultUnit() {
        baseUnitProperty().addListener((obs, oldV, newV) -> updateDefaultUnit(newV));
    }

    void updateValueQuantity() {
        Quantity<Q> quantity = QuantitiesUtil.createQuantity(getValue(), getUnit());
        setValueQuantity(quantity);
    }

    void updateDefaultUnit(Unit<Q> baseUnit) {
        if (getUnit() == null) {
            setUnit(baseUnit);
        }
    }

}

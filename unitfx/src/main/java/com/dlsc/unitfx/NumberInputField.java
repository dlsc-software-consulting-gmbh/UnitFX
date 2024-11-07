package com.dlsc.unitfx;

import com.dlsc.unitfx.util.ControlsUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

import java.lang.reflect.ParameterizedType;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Base control to number inputs, defines all base functionality to collect any object whose class is
 * child of {@link Number}.
 *
 * @param <T> The type of number.
 */
public abstract class NumberInputField<T extends Number> extends CustomTextField {

    /**
     * Instances a new number input with with {@code null} {@link #valueProperty() value}, no
     * {@link #validatorProperty() validator} and allowing negatives.
     */
    public NumberInputField() {
        NumberStringFilteredConverter converter = new NumberStringFilteredConverter();
        setTextFormatter(new TextFormatter<>(converter, null, converter.getFilter()));

        listenForValueChanges();
        listenForTextOrValidationChanges();
        listenForAllowNegativeChanges();

        getStyleClass().add("number-input-field");
        ControlsUtil.bindBooleanToPseudoclass(this, invalidProperty(), PseudoClass.getPseudoClass("invalid"));
    }

    /**
     * Converts the text to the input field number type object.
     * @param text The text to be converted.
     * @return In case of impossible conversion, return {@code null} instead of throwing exception.
     */
    protected abstract T convertTextToNumber(String text);

    /**
     * Converts the input number type object to string.
     *
     * @param number The number to be converted.
     * @return If the number is {@code null}, then return empty string.
     */
    protected abstract String convertNumberToText(T number);

    /**
     * The number value representation of the text written.
     * @return The number value.
     */
    public final ObjectProperty<T> valueProperty() { return value; }
    private final ObjectProperty<T> value = new SimpleObjectProperty<>(this, "value");
    public final T getValue() { return valueProperty().get(); }
    public final void setValue(T value) { valueProperty().set(value); }


    /**
     * Boolean property that tells the control to allow introducing a negative number.
     * @return The boolean property.
     */
    public final BooleanProperty allowNegativesProperty() { return allowNegatives; }
    private final BooleanProperty allowNegatives = new SimpleBooleanProperty(this, "allowNegatives", true);
    public final boolean isAllowNegatives() { return allowNegativesProperty().get(); }
    public final void setAllowNegatives(boolean allowNegatives) { allowNegativesProperty().set(allowNegatives); }


    /**
     * Stores a validator object for the value converted from text entered.  If the value entered is not valid, a pseudo
     * class is added to the field, called ":invalid".  This can be used for styling the field to indicate invalid inputs.
     * @return The property storing the validator predicate.
     */
    public final ObjectProperty<Predicate<T>> validatorProperty() { return validator; }
    private final ObjectProperty<Predicate<T>> validator = new SimpleObjectProperty<>(this, "validator");
    public final Predicate<T> getValidator() { return validatorProperty().get(); }
    public final void setValidator(Predicate<T> validator) { validatorProperty().set(validator); }


    /**
     * Property that indicates whether the {@link #valueProperty() value} is valid after being validated by
     * {@link #validatorProperty() validator}.
     * @return The read only boolean property.
     */
    public final ReadOnlyBooleanProperty invalidProperty() { return invalid.getReadOnlyProperty(); }
    private final ReadOnlyBooleanWrapper invalid = new ReadOnlyBooleanWrapper(this, "invalid");
    public final boolean isInvalid() { return invalid.get(); }
    private final void setInvalid(boolean invalid) { this.invalid.set(invalid); }


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


    /**
     * The minimum value that can be entered in this field.  If this property is set, once the user enters a number
     * lower than this minimum, the field becomes invalid and the {@link #valueProperty()} is set to {@code null}.
     * @return The minimum value.
     */
    public final ObjectProperty<T> minimumValueProperty() { return minimumValue; }
    private final ObjectProperty<T> minimumValue = new SimpleObjectProperty<>(this, "minimumValue");
    public final T getMinimumValue() { return minimumValueProperty().get(); }
    public final void setMinimumValue(T minimumValue) { minimumValueProperty().set(minimumValue); }


    /**
     * The maximum value that can be entered in this field.  If this property is set, once the user enters a number
     * greater than this minimum, the field becomes invalid and the {@link #valueProperty()} is set to {@code null}.
     * @return The maximum value.
     */
    public final ObjectProperty<T> maximumValueProperty() { return maximumValue; }
    private final ObjectProperty<T> maximumValue = new SimpleObjectProperty<>(this, "maximumValue");
    public final T getMaximumValue() { return maximumValueProperty().get(); }
    public final void setMaximumValue(T maximumValue) { maximumValueProperty().set(maximumValue); }


    /**
     * Number string converter that provides a filter for text changes.
     */
    class NumberStringFilteredConverter extends NumberStringConverter {

        NumberStringFilteredConverter() {
            super(isIntegerTypedField() ? NumberFormat.getIntegerInstance() : NumberFormat.getNumberInstance());
            NumberFormat nFormat = getNumberFormat();
            nFormat.setGroupingUsed(false);
            numberOfIntegersProperty().addListener(obs -> nFormat.setMaximumIntegerDigits(getNumberOfIntegers()));
            numberOfDecimalsProperty().addListener(obs -> nFormat.setMaximumFractionDigits(getNumberOfDecimals()));
        }

        UnaryOperator<TextFormatter.Change> getFilter() {
            return change -> {
                String newText = change.getControlNewText();
                if (newText.isEmpty()) {
                    return change;
                }

                if (isAllowNegatives()) {
                    if (newText.equals("-")) {
                        return change;
                    }

                    if (newText.startsWith("-")) {
                        newText = newText.substring(1);
                        if (newText.startsWith("-")) {
                            return null;
                        }
                    }
                }
                else if (newText.startsWith("-")) {
                    return null;
                }

                ParsePosition parsePosition = new ParsePosition( 0);
                Number number = getNumberFormat().parse(newText, parsePosition);
                if (number == null || parsePosition.getIndex() < newText.length()) {
                    return null;
                }

                return change;
            };
        }
    }


    // listeners

    private boolean updatingValue;

    private void listenForValueChanges() {
        valueProperty().addListener((obs, oldV, newV) -> {
            if (!updatingValue) {
                setText(convertNumberToText(newV));
            }
        });
    }

    private void listenForTextOrValidationChanges() {
        InvalidationListener textListener = obs -> {
            try {
                updatingValue = true;
                T number = convertTextToNumber(getText());
                setInvalid(isInvalidNumber(number));
                setValue(!isInvalid() ? number : null);
            }
            finally {
                updatingValue = false;
            }
        };

        textProperty().addListener(textListener);
        minimumValueProperty().addListener(textListener);
        maximumValueProperty().addListener(textListener);
        validatorProperty().addListener(textListener);
    }

    private void listenForAllowNegativeChanges() {
        allowNegativesProperty().addListener((obs, oldV, newV) -> {
            if (!newV && getValue() != null && getValue().doubleValue() < 0) {
                setValue(null);
            }
        });
    }

    private boolean isInvalidNumber(T number) {
        boolean invalid = false;

        if (getValidator() != null) {
            invalid = !getValidator().test(number);
        }

        if (getMinimumValue() != null && number != null) {
            invalid |= getMinimumValue().doubleValue() > number.doubleValue();
        }

        if (getMaximumValue() != null && number != null) {
            invalid |= getMaximumValue().doubleValue() < number.doubleValue();
        }

        return invalid;
    }

    @SuppressWarnings("unchecked")
    private boolean isIntegerTypedField() {
        Class<T> typeClass = (Class<T>) ((ParameterizedType) NumberInputField.this.getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];

        return Integer.class.equals(typeClass) ||
                Long.class.equals(typeClass) ||
                Short.class.equals(typeClass) ||
                Byte.class.equals(typeClass);
    }

}

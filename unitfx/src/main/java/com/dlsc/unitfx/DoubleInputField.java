package com.dlsc.unitfx;

import javafx.util.converter.DoubleStringConverter;

/**
 * Concrete implementation of {@link NumberInputField} for collecting {@link Double} type numbers.
 */
public class DoubleInputField extends NumberInputField<Double> {

    private final DoubleStringConverter converter = new DoubleStringConverter();

    @Override
    protected Double convertTextToNumber(String text) {
        try {
            return converter.fromString(text);
        }
        catch(Exception e) {
            return null;
        }
    }

    @Override
    protected String convertNumberToText(Double number) {
        return converter.toString(number);
    }

}

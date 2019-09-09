package com.dlsc.unitfx;

import javafx.util.converter.IntegerStringConverter;

/**
 * Concrete implementation of {@link NumberInputField} for collecting {@link Integer} type numbers.
 */
public class IntegerInputField extends NumberInputField<Integer> {

    private final IntegerStringConverter converter = new IntegerStringConverter();

    @Override
    protected Integer convertTextToNumber(String text) {
        try {
            return converter.fromString(text);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String convertNumberToText(Integer number) {
        return converter.toString(number);
    }
}

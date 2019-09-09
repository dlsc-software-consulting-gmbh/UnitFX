package com.dlsc.unitfx.util;

import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * Utility class that allows to manipulate {@link Quantity quantity} objects.
 */
public final class QuantitiesUtil {

    private QuantitiesUtil() {
        super();
    }

    /**
     * Creates a new quantity instance using the given value and the given unit.
     *
     * @param value The amount to be placed in the quantity instance.
     * @param unit The unit to represent the quantity.
     * @param <Q> The type of Quantity to be created.
     * @return The quantity instance, {@code null} if either the value param or unit param is null.
     */
    public static <Q extends Quantity<Q>> Quantity<Q> createQuantity(Number value, Unit<Q> unit) {
        Quantity<Q> valueQuantity = null;
        if (value != null && unit != null) {
            valueQuantity = Quantities.getQuantity(value, unit);
        }
        return valueQuantity;
    }

    /**
     * Truncates the given quantity value to the given precision, meaning that the value is floor rounded always.
     *
     * <p>
     *     Examples:
     *     <ul>
     *         <li>Quantity(115 Metre), Precision(10 Metre) = Truncated(110 Metre)</li>
     *         <li>Quantity(88 Metre), Precision(5 Metre) = Truncated(85 Metre)</li>
     *     </ul>
     * </p>
     *
     * @param value The quantity to be truncated.
     * @param precision The precision which the value is truncated to.
     * @param <Q> The quantity type.
     * @return A new quantity instance having the value truncated.
     */
    public static <Q extends Quantity<Q>> Quantity<Q> truncateQuantity(Quantity<Q> value, Quantity<Q> precision) {
        Quantity<Q> valueInSystemUnit = value.toSystemUnit();
        Quantity<Q> precisionInSystemUnit = precision.toSystemUnit();

        Quantity<?> divideResult = valueInSystemUnit.divide(precisionInSystemUnit);

        Quantity<Q> truncatedValueInPrecisionUnits = precision.multiply(divideResult.getValue().intValue());
        Quantity<Q> truncatedValueInValueUnits = truncatedValueInPrecisionUnits.to(value.getUnit());

        return truncatedValueInValueUnits;
    }

    /**
     * Applies a rounding mechanism that returns the nearest value according to the precision.
     *
     * <p>
     *     Examples:
     *     <ul>
     *         <li>Quantity(115 Metre), Precision(10 Metre) = Truncated(120 Metre)</li>
     *         <li>Quantity(88 Metre), Precision(5 Metre) = Truncated(90 Metre)</li>
     *         <li>Quantity(52 Metre), Precision(5 Metre) = Truncated(50 Metre)</li>
     *     </ul>
     * </p>
     *
     * @param value
     * @param precision
     * @param <Q>
     * @return
     */
    public static <Q extends Quantity<Q>> Quantity<Q> roundQuantity(Quantity<Q> value, Quantity<Q> precision) {
        Quantity<Q> valueInSystemUnit = value.toSystemUnit();
        Quantity<Q> precisionInSystemUnit = precision.toSystemUnit();

        Quantity<?> divideResult = valueInSystemUnit.divide(precisionInSystemUnit);

        long multiplier = Math.round(divideResult.getValue().doubleValue());

        Quantity<Q> truncatedValueInPrecisionUnits = precision.multiply(multiplier);
        Quantity<Q> truncatedValueInValueUnits = truncatedValueInPrecisionUnits.to(value.getUnit());

        return truncatedValueInValueUnits;
    }

}

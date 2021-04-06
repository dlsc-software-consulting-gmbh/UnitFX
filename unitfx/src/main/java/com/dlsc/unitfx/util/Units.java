package com.dlsc.unitfx.util;

import com.dlsc.unitfx.QuantityInputField;
import tech.units.indriya.AbstractSystemOfUnits;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.format.UnitStyle;
import tech.units.indriya.function.MultiplyConverter;
import tech.units.indriya.unit.AlternateUnit;
import tech.units.indriya.unit.TransformedUnit;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Customized system of units that incorporates all available units in all dimensions supported by
 *
 * the {@link QuantityInputField}.
 */
public final class Units extends AbstractSystemOfUnits {

    private static final Units INSTANCE = new Units();

    public static Units getInstance() {
        return INSTANCE;
    }

    // Mass units
    public static final Unit<Mass> GRAM = addUnit(Mass.class, tech.units.indriya.unit.Units.GRAM, null, true);
    public static final Unit<Mass> MILLIGRAM = addUnit(Mass.class, MetricPrefix.MILLI(GRAM), null, false);
    public static final Unit<Mass> KILOGRAM = addUnit(Mass.class, MetricPrefix.KILO(GRAM), null, false);


    // Length Units
    public static final Unit<Length> METRE = addUnit(Length.class, tech.units.indriya.unit.Units.METRE, null, true);
    public static final Unit<Length> CENTIMETRE = addUnit(Length.class, MetricPrefix.CENTI(METRE), null, false);
    public static final Unit<Length> MILLIMETRE = addUnit(Length.class, MetricPrefix.MILLI(METRE), null, false);
    public static final Unit<Length> KILOMETRE = addUnit(Length.class, MetricPrefix.KILO(METRE), null, false);
    public static final Unit<Length> FOOT = addUnit(Length.class, new TransformedUnit<>("ft", METRE, MultiplyConverter.ofRational(3048, 10000)), "ft", false);
    public static final Unit<Length> INCH = addUnit(Length.class, new TransformedUnit<>("in", FOOT, MultiplyConverter.ofRational(1, 12)), "in", false);
    public static final Unit<Length> NAUTICAL_MILE = addUnit(Length.class, new TransformedUnit<>("nm", METRE, MultiplyConverter.of(1852)), "nm", false);


    // Temperature
    public static final Unit<Temperature> CELSIUS = addUnit(Temperature.class, tech.units.indriya.unit.Units.CELSIUS, null, true);
    public static final Unit<Temperature> KELVIN = addUnit(Temperature.class, tech.units.indriya.unit.Units.KELVIN, null, false);


    // Angle
    public static final Unit<Angle> DEGREE = addUnit(Angle.class, new AlternateUnit<>(AbstractUnit.ONE, "d"), null, true);


    // Speed
    public static final Unit<Speed> METRE_PER_SECOND = addUnit(Speed.class, tech.units.indriya.unit.Units.METRE_PER_SECOND, null, false);
    public static final Unit<Speed> KILOMETRE_PER_HOUR = addUnit(Speed.class, tech.units.indriya.unit.Units.KILOMETRE_PER_HOUR, null, true);
    public static final Unit<Speed> KNOT = addUnit(Speed.class, NAUTICAL_MILE.divide(tech.units.indriya.unit.Units.HOUR).asType(Speed.class), "kt", false);


    private static <Q extends Quantity<Q>> Unit<Q> addUnit(Class<Q> type, Unit<Q> unit, String symbol, boolean baseUnit) {
        List<Unit<?>> units = INSTANCE.quantityToUnits.computeIfAbsent(type, t -> new ArrayList<>());
        units.add(unit);
        if (baseUnit) {
            INSTANCE.quantityToUnit.put(type, unit);
        }

        if (symbol == null) {
            return Helper.addUnit(INSTANCE.units, unit, unit.toString());
        }

        return Helper.addUnit(INSTANCE.units, unit, symbol, symbol, UnitStyle.SYMBOL_AND_LABEL);
    }

    private final Map<Class<? extends Quantity<?>>, List<Unit<?>>> quantityToUnits = new HashMap<>();

    private Units() {
        super();
    }

    @Override
    public String getName() {
        return Units.class.getSimpleName();
    }

    /**
     * Allows to get the list of units registered for the given quantity type.
     *
     * @param type The quantity type class.
     * @param <Q> The quantity type.
     * @return The list of registered units in this system of units.
     */
    @SuppressWarnings("unchecked")
    public <Q extends Quantity<Q>> List<Unit<Q>> getUnits(Class<Q> type) {
        @SuppressWarnings("rawtypes")
        List units = Collections.unmodifiableList(Optional
                .ofNullable(quantityToUnits.get(type))
                .orElse(Collections.emptyList())
        );
        return units;
    }

}

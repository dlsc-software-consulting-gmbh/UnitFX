package com.dlsc.unitfx.util;

import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.Node;


/**
 * Utility methods for JavaFX controls.
 */
public final class ControlsUtil {

    private ControlsUtil() {
    }

    /**
     * Binds a boolean property to a style class in the given node.  Doing this the style class is switched on/off
     * depending on the boolean property.
     *
     * @param node The node which the style class will be applied to.
     * @param booleanProperty The flag to switch on/off.
     * @param styleClass The style class to be applied.
     */
    public static void bindBooleanToStyleClass(Node node, ObservableValue<Boolean> booleanProperty, String styleClass) {
        booleanProperty.addListener((obs, oldV, newV) -> {
            if (Boolean.TRUE.equals(booleanProperty.getValue())) {
                if (!node.getStyleClass().contains(styleClass)) {
                    node.getStyleClass().add(styleClass);
                }
            } else {
                node.getStyleClass().remove(styleClass);
            }
        });
    }

    /**
     * Binds a boolean property to a pseudo class.  Doing this the pseudo class is switched on/off in the given node.
     *
     * @param node The node which the pseudo class will be applied to.
     * @param booleanProperty The flag to switch on/off.
     * @param pseudoClass The style class to be applied.
     */
    public static void bindBooleanToPseudoclass(Node node, ObservableValue<Boolean> booleanProperty, PseudoClass pseudoClass) {
        booleanProperty.addListener((obs, oldV, newV) -> node.pseudoClassStateChanged(pseudoClass, Boolean.TRUE.equals(booleanProperty.getValue())));
    }

    /**
     * Calculates the distance between two angles in degrees, the result is a number between 0 - 180.
     * @param alpha The initial angle.
     * @param beta The final angle.
     * @return The distance between the angles.
     */
    public static double distance(double alpha, double beta) {
        double phi = Math.abs(beta - alpha) % 360;
        return phi > 180 ? 360 - phi : phi;
    }

    /**
     * Calculates the rotation, which means the signed distance between two angles.
     * @param alpha The initial angle.
     * @param beta The final angle.
     * @return The rotation between the angles.
     */
    public static double rotation(double alpha, double beta) {
        double distance = distance(alpha, beta);
        int sign = (alpha - beta >= 0 && alpha - beta <= 180) || (alpha - beta <=-180 && alpha - beta >= -360) ? 1 : -1;
        return distance * sign;
    }

    /**
     * Calculates the x,y position of the given angle in relation with the circle represented by the middle point and radius.
     *
     * @param middle The middle of the circle in pixel representation.
     * @param radius The radius of the circle.
     * @param angle The angle for which the point is going to be calculated.
     * @return The point calculated.
     */
    public static Point2D calculatePointOnCircle(Point2D middle, double radius, double angle) {
        double newAngle = Math.abs(angle) % Constants.MAX_DEGREE;
        if (angle < 0) {
            newAngle = Constants.MAX_DEGREE - newAngle;
        }

        final double radians = Math.toRadians(newAngle);

        double tempX = radius * Math.cos(radians);
        double tempY = radius * Math.sin(radians);

        double x = middle.getX();
        double y = middle.getY();

        x += tempX;

        y = switchPixelAndCartesian(y); // to cartesian y
        y += tempY;
        y = switchPixelAndCartesian(y); // to pixel y

        return new Point2D(x, y);
    }

    private static double switchPixelAndCartesian(double value) {
        return value == 0 ? value : -value;
    }
}

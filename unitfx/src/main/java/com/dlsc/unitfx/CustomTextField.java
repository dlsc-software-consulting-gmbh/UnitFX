package com.dlsc.unitfx;


import com.dlsc.unitfx.skins.CustomTextFieldSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

/**
 * A custom text field that allows the application to set nodes on the left-hand and
 * right-hand side of the field (e.g. magnifying glass icon for search on the left-hand
 * side and an X icon for cancelling a search on the right-hand side).
 */
public class CustomTextField extends TextField {

    /**
     * Stores the node shown on the left-hand side of the text field.
     */
    private ObjectProperty<Node> left = new SimpleObjectProperty<>(this, "left");

    public final ObjectProperty<Node> leftProperty() {
        return left;
    }

    public final Node getLeft() {
        return left.get();
    }

    public final void setLeft(Node value) {
        left.set(value);
    }


    /**
     * Stores the node shown on the right-hand side of the text field.
     */
    private ObjectProperty<Node> right = new SimpleObjectProperty<>(this, "right");

    public final ObjectProperty<Node> rightProperty() {
        return right;
    }

    public final Node getRight() {
        return right.get();
    }

    public final void setRight(Node right) {
        this.right.set(right);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomTextFieldSkin(this) {
            @Override
            public ObjectProperty<Node> leftProperty() {
                return CustomTextField.this.leftProperty();
            }

            @Override
            public ObjectProperty<Node> rightProperty() {
                return CustomTextField.this.rightProperty();
            }
        };
    }

}

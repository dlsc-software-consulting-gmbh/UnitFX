package com.dlsc.unitfx.skins;

import javafx.beans.property.ObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.StackPane;
import javafx.scene.text.HitInfo;

public abstract class CustomTextFieldSkin extends TextFieldSkin {

    private static final PseudoClass HAS_NO_SIDE_NODE = PseudoClass.getPseudoClass("no-side-nodes"); //$NON-NLS-1$
    private static final PseudoClass HAS_LEFT_NODE = PseudoClass.getPseudoClass("left-node-visible"); //$NON-NLS-1$
    private static final PseudoClass HAS_RIGHT_NODE = PseudoClass.getPseudoClass("right-node-visible"); //$NON-NLS-1$

    private Node left;
    private StackPane leftPane;
    private Node right;
    private StackPane rightPane;

    private final TextField control;

    public CustomTextFieldSkin(final TextField control) {
        super(control);
        this.control = control;
        updateChildren();

        leftProperty().addListener(it -> updateChildren());
        rightProperty().addListener(it -> updateChildren());

    }

    public abstract ObjectProperty<Node> leftProperty();
    public abstract ObjectProperty<Node> rightProperty();

    private void updateChildren() {
        Node newLeft = leftProperty().get();
        if (newLeft != null) {
            getChildren().remove(leftPane);
            leftPane = new StackPane(newLeft);
            leftPane.setManaged(false);
            leftPane.setAlignment(Pos.CENTER_LEFT);
            leftPane.getStyleClass().add("left-pane"); //$NON-NLS-1$
            getChildren().add(leftPane);
            left = newLeft;
        }
        Node newRight = rightProperty().get();
        if (newRight != null) {
            getChildren().remove(rightPane);
            rightPane = new StackPane(newRight);
            rightPane.setManaged(false);
            rightPane.setAlignment(Pos.CENTER_RIGHT);
            rightPane.getStyleClass().add("right-pane"); //$NON-NLS-1$
            getChildren().add(rightPane);
            right = newRight;
        }

        control.pseudoClassStateChanged(HAS_LEFT_NODE, left != null);
        control.pseudoClassStateChanged(HAS_RIGHT_NODE, right != null);
        control.pseudoClassStateChanged(HAS_NO_SIDE_NODE, left == null && right == null);
    }

    @Override protected void layoutChildren(double x, double y, double w, double h) {
        final double fullHeight = h + snappedTopInset() + snappedBottomInset();

        final double leftWidth = leftPane == null ? 0.0 : snapSize(leftPane.prefWidth(fullHeight));
        final double rightWidth = rightPane == null ? 0.0 : snapSize(rightPane.prefWidth(fullHeight));

        final double textFieldStartX = snapPosition(x) + snapSize(leftWidth);
        final double textFieldWidth = w - snapSize(leftWidth) - snapSize(rightWidth);

        super.layoutChildren(textFieldStartX, 0, textFieldWidth, fullHeight);

        if (leftPane != null) {
            final double leftStartX = 0;
            leftPane.resizeRelocate(leftStartX, 0, leftWidth, fullHeight);
        }

        if (rightPane != null) {
            final double rightStartX = rightPane == null ? 0.0 : w - rightWidth + snappedLeftInset();
            rightPane.resizeRelocate(rightStartX, 0, rightWidth, fullHeight);
        }
    }

    @Override
    public HitInfo getIndex(double x, double y) {
        /**
         * This resolves https://bitbucket.org/controlsfx/controlsfx/issue/476
         * when we have a left Node and the click point is badly returned
         * because we weren't considering the shift induced by the leftPane.
         */
        final double leftWidth = leftPane == null ? 0.0 : snapSize(leftPane.prefWidth(getSkinnable().getHeight()));
        return super.getIndex(x - leftWidth, y);
    }

    @Override
    protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double pw = super.computePrefWidth(h, topInset, rightInset, bottomInset, leftInset);
        final double leftWidth = leftPane == null ? 0.0 : snapSize(leftPane.prefWidth(h));
        final double rightWidth = rightPane == null ? 0.0 : snapSize(rightPane.prefWidth(h));

        return pw + leftWidth + rightWidth;
    }

    @Override
    protected double computePrefHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double ph = super.computePrefHeight(w, topInset, rightInset, bottomInset, leftInset);
        final double leftHeight = leftPane == null ? 0.0 : snapSize(leftPane.prefHeight(-1));
        final double rightHeight = rightPane == null ? 0.0 : snapSize(rightPane.prefHeight(-1));

        return Math.max(ph, Math.max(leftHeight, rightHeight));
    }

    @Override
    protected double computeMinWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double mw = super.computeMinWidth(h, topInset, rightInset, bottomInset, leftInset);
        final double leftWidth = leftPane == null ? 0.0 : snapSize(leftPane.minWidth(h));
        final double rightWidth = rightPane == null ? 0.0 : snapSize(rightPane.minWidth(h));

        return mw + leftWidth + rightWidth;
    }

    @Override
    protected double computeMinHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double mh = super.computeMinHeight(w, topInset, rightInset, bottomInset, leftInset);
        final double leftHeight = leftPane == null ? 0.0 : snapSize(leftPane.minHeight(-1));
        final double rightHeight = rightPane == null ? 0.0 : snapSize(rightPane.minHeight(-1));

        return Math.max(mh, Math.max(leftHeight, rightHeight));
    }

}

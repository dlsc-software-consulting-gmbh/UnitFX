package com.dlsc.unitfx.skins;

import com.dlsc.unitfx.DoubleInputField;
import com.dlsc.unitfx.QuantityInputField;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

import javax.measure.Quantity;
import javax.measure.Unit;

public class QuantityInputFieldSkin<Q extends Quantity<Q>> extends SkinBase<QuantityInputField<Q>> {

    private final DoubleInputField editor;
    private final ComboBox<Unit<Q>> switcher;
    private final Label editorDisabled;
    private final Label switcherDisabled;

    public QuantityInputFieldSkin(QuantityInputField<Q> control) {
        super(control);

        Region dirtyIcon = new Region();
        dirtyIcon.getStyleClass().add("dirty-icon");
        dirtyIcon.visibleProperty().bind(control.valueDirtyProperty());
        dirtyIcon.managedProperty().bind(dirtyIcon.visibleProperty());
        dirtyIcon.setOnMouseClicked(evt -> control.restoreValueProperty());

        editor = new DoubleInputField();
        editor.getStyleClass().add("editor");
        editor.setLeft(dirtyIcon);
        editor.valueProperty().bindBidirectional(control.valueProperty());
        editor.numberOfIntegersProperty().bind(control.numberOfIntegersProperty());
        editor.numberOfDecimalsProperty().bind(control.numberOfDecimalsProperty());
        editor.allowNegativesProperty().bind(control.allowNegativesProperty());
        editor.minimumValueProperty().bind(control.minimumValueProperty());
        editor.maximumValueProperty().bind(control.maximumValueProperty());
        editor.addEventHandler(KeyEvent.KEY_PRESSED, evt -> {
            if (evt.getCode() == KeyCode.ENTER) {
                control.restoreValueProperty();
            }
        });
        editor.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                control.restoreValueProperty();
            }
        });
        editor.validatorProperty().bind(control.valueValidatorProperty());
        editor.invalidProperty().addListener(obs -> control.getProperties().put("invalid", editor.isInvalid()));

        switcher = new ComboBox<>();
        switcher.getStyleClass().add("unit-switcher");
        switcher.setItems(control.getAvailableUnits());
        switcher.valueProperty().bindBidirectional(control.unitProperty());
        switcher.converterProperty().bind(control.unitStringConverterProperty());
        switcher.setButtonCell(new UnitListCell());
        switcher.setCellFactory(lv -> new UnitListCell());

        editorDisabled = new Label();
        editorDisabled.getStyleClass().add("editor");
        editorDisabled.getStyleClass().add("editor-disabled");
        editorDisabled.textProperty().bind(editor.textProperty());

        BooleanProperty unitNotBaseUnit = new SimpleBooleanProperty();
        InvalidationListener unitsListener = obs -> {
            Unit<Q> baseUnit = control.getBaseUnit();
            Unit<Q> unit = control.getUnit();
            unitNotBaseUnit.set(unit != null && baseUnit != null && !unit.equals(baseUnit));
        };
        control.baseUnitProperty().addListener(unitsListener);
        control.unitProperty().addListener(unitsListener);
        unitsListener.invalidated(null);
        Label unitLbl = new Label();
        unitLbl.textProperty().bind(Bindings.createStringBinding(() -> convertUnitToString(control.getUnit()), control.unitProperty()));
        decorateUnitLabel(unitLbl, unitNotBaseUnit);
        switcherDisabled = new Label();
        switcherDisabled.setGraphic(unitLbl);
        switcherDisabled.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        switcherDisabled.getStyleClass().add("unit-switcher");
        switcherDisabled.getStyleClass().add("unit-switcher-disabled");

        updateChildren();
        control.readOnlyProperty().addListener(obs -> updateChildren());
        control.autoFixValueProperty().addListener(obs -> updateChildren());
    }

    private void updateChildren() {
        if (getSkinnable().isReadOnly()) {
            if (getSkinnable().isAutoFixValue()) {
                getChildren().setAll(editorDisabled, switcher);
            }
            else {
                getChildren().setAll(editorDisabled, switcherDisabled);
            }
        }
        else {
            getChildren().setAll(editor, switcher);
        }
    }

    private String convertUnitToString(Unit<Q> unit) {
        String text = "";
        if (getSkinnable().getUnitStringConverter() != null) {
            text = getSkinnable().getUnitStringConverter().toString(unit);
        }
        else if (unit != null) {
            text = unit.toString();
        }
        return text;
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        final double switcherWidth = snapSize(switcher.prefWidth(-1));

        final double fieldX = snapPosition(contentX);
        final double fieldY = snapPosition(contentY);
        final double fieldWidth = snapSize(contentWidth);
        final double fieldHeight = snapSize(contentHeight);

        final double textBoxWidth = fieldWidth - switcherWidth;
        final double unitBoxX = fieldWidth - switcherWidth;

        editorDisabled.resizeRelocate(fieldX, fieldY, textBoxWidth, fieldHeight);
        switcherDisabled.resizeRelocate(unitBoxX, fieldY, switcherWidth, fieldHeight);

        editor.resizeRelocate(fieldX, fieldY, textBoxWidth, fieldHeight);
        switcher.resizeRelocate(unitBoxX, fieldY, switcherWidth, fieldHeight);
    }


    private class UnitListCell extends ListCell<Unit<Q>> {

        private final Label icon;
        private final BooleanProperty itemNoBaseUnit = new SimpleBooleanProperty();

        UnitListCell() {
            icon = decorateUnitLabel(this, itemNoBaseUnit);
            InvalidationListener listener = obs -> {
                Unit<Q> baseUnit = getSkinnable().getBaseUnit();
                Unit<Q> unit = getItem();
                itemNoBaseUnit.set(unit != null && baseUnit != null && !unit.equals(baseUnit));
            };
            getSkinnable().baseUnitProperty().addListener(listener);
            itemProperty().addListener(listener);
            getStyleClass().add("unit-cell");
        }

        @Override
        protected void updateItem(Unit<Q> item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(convertUnitToString(item));
                setGraphic(icon);
            }
            else {
                setText(null);
                setGraphic(null);
            }
        }
    }


    private static Label decorateUnitLabel(Labeled unitLabel, BooleanProperty iconVisibleProperty) {
        Label icon = new Label();
        icon.getStyleClass().add("indicator");
        icon.setPrefWidth(5);
        icon.visibleProperty().bind(iconVisibleProperty);
        unitLabel.setGraphic(icon);
        unitLabel.setGraphicTextGap(5);
        unitLabel.getStyleClass().add("unit-label");
        return icon;
    }

}
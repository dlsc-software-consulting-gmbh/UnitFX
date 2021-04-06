module com.dlsc.unitfx {
    requires transitive javafx.controls;
    requires tech.units.indriya;
    requires tech.uom.lib.common;

    exports com.dlsc.unitfx;
    exports com.dlsc.unitfx.skins;
    exports com.dlsc.unitfx.util;

    opens com.dlsc.unitfx.skins;

}
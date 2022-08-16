module athensclub.yugiohutil {
    opens athensclub.yugiohutil.ui.controller to javafx.fxml;
    exports athensclub.yugiohutil;
    exports athensclub.yugiohutil.data;
    exports athensclub.yugiohutil.ui.controller;

    requires java.desktop;

    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    requires pdfbox;
    requires com.google.gson;
}
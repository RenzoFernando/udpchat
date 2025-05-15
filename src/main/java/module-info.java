module udpchat {
    requires javafx.controls;
    requires javafx.fxml;

    exports udpchat.peer;
    exports udpchat.network;
    exports udpchat.ui;
    opens   udpchat.ui to javafx.fxml;
}

module com.mcgreedy.optionothello {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.mcgreedy.optionothello to javafx.fxml;
    exports com.mcgreedy.optionothello;
    exports com.mcgreedy.optionothello.ui;
}
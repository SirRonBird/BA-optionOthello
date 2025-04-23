module com.mcgreedy.optionothello {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.logging.log4j;

    opens com.mcgreedy.optionothello to javafx.fxml;
    exports com.mcgreedy.optionothello;
    exports com.mcgreedy.optionothello.ui;
    exports com.mcgreedy.optionothello.utils;
    opens com.mcgreedy.optionothello.utils to javafx.fxml;
}
module com.mcgreedy.optionothello {
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires org.apache.logging.log4j;
  requires com.fasterxml.jackson.databind;
  requires org.fxmisc.flowless;
  requires org.fxmisc.richtext;
  requires reactfx;
  requires javafx.swing;
  requires org.graalvm.polyglot;
  requires java.scripting;
  requires java.prefs;
  requires eu.hansolo.tilesfx;

  // Für Jackson: Erlaube Zugriff auf Felder/Klassen via Reflection
  opens com.mcgreedy.optionothello.engine to com.fasterxml.jackson.databind;
  opens com.mcgreedy.optionothello.utils to com.fasterxml.jackson.databind;
  opens com.mcgreedy.optionothello.dtos to com.fasterxml.jackson.databind;

  // Für JavaFX FXML
  opens com.mcgreedy.optionothello to javafx.fxml;

  // Öffentliche API-Exports
  exports com.mcgreedy.optionothello;
  exports com.mcgreedy.optionothello.gamemanagement;
  exports com.mcgreedy.optionothello.engine;
  exports com.mcgreedy.optionothello.ui;
  exports com.mcgreedy.optionothello.utils;
  exports com.mcgreedy.optionothello.ai;
  exports com.mcgreedy.optionothello.dtos;
}

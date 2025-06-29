package com.mcgreedy.optionothello.ui;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.layout.VBox;
import org.fxmisc.richtext.LineNumberFactory;

public class JavaScriptEditor extends VBox {

  private final CodeArea codeArea;

  public JavaScriptEditor() {
    this.codeArea = new CodeArea();
    codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    codeArea.setWrapText(true);
    codeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14pt;");

    codeArea.setPrefHeight(200);

    final Pattern whiteSpace = Pattern.compile( "^\\s+" );
    codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
    {
      if ( KE.getCode() == KeyCode.ENTER ) {
        int caretPosition = codeArea.getCaretPosition();
        int currentParagraph = codeArea.getCurrentParagraph();
        Matcher m0 = whiteSpace.matcher( codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
        if ( m0.find() ) Platform.runLater( () -> codeArea.insertText( caretPosition, m0.group() ) );
      }
    });

    this.getChildren().addAll(codeArea);
    this.setPrefHeight(300);
  }

  public String getCode() {
    return codeArea.getText();
  }

  public void setCode(String code) {
    codeArea.replaceText(code);
  }

}

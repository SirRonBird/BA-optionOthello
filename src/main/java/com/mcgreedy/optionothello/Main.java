package com.mcgreedy.optionothello;

import com.mcgreedy.optionothello.ui.MainGUI;
import javafx.application.Application;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Main{

    public static void main(String[] args) {
        MainGUI.setGameManagerName("Sam");
       Application.launch(MainGUI.class, args);
    }
}

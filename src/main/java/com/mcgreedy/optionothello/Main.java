package com.mcgreedy.optionothello;

import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.ui.MainGUI;
import javafx.application.Application;

public class Main{

    public static void main(String[] args) {
        Gamemanager gamemanager = new Gamemanager();
        MainGUI.setGameManagerName(gamemanager);
        Application.launch(MainGUI.class, args);
    }
}

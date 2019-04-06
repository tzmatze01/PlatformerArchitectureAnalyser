package main.enums;

import javafx.scene.paint.Color;

public enum GameElemMenuEntries {

    BACKGROUND("Background", 'B', Color.BLACK),
    PLATFORM("Platform", 'P', Color.GREEN),
    INTERACTION("Interaktion", 'I', Color.BLUE),
    TRAP("Trap", 'T', Color.YELLOW),
    MOVING_PLATFORM("Mov. Pltf.", 'M', Color.GREY),
    DISAPPEARING_PLATFORM("Disap. Pltf.", 'D', Color.AZURE),
    ENEMY("Enemy", 'E', Color.RED);

    private String name;
    private char semChar;
    private Color color;

    GameElemMenuEntries(String name, char semChar, Color color)
    {
        this.name = name;
        this.semChar = semChar;
        this.color = color;
    }

    public String toString()
    {
        return this.name;
    }

    public char getChar() { return this.semChar; }

    private Color getColor()
    {
        return this.color;
    }
}

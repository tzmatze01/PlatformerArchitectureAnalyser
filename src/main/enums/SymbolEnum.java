package main.enums;

import javafx.scene.paint.Color;

public enum SymbolEnum {

    BACKGROUND("Background", 'X', Color.BLACK),
    BLOCK("Block", 'B', Color.RED),
    PLATFORM("Platform", 'P', Color.GREEN),
    INTERACTION("Interaktion", 'I', Color.BLUE),
    COLLECTABLE("Collectable", 'C', Color.BROWN),
    // TRAP("Trap", 'T', Color.YELLOW),
    //MOVING_PLATFORM("Mov. Pltf.", 'M', Color.DARKORANGE),
    DISAPPEARING_PLATFORM("Disap. Pltf.", 'D', Color.DARKGREY);

    private String name;
    private char semChar;
    private Color color;

    SymbolEnum(String name, char semChar, Color color)
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

    public Color getColor()
    {
        return this.color;
    }
}

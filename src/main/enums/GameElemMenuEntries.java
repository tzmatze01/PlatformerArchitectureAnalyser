package main.enums;

public enum GameElemMenuEntries {

    FLOOR("Floor", 'F'),
    BACKGORUND("Background", '-'),
    PLATFORM("Platform", 'P'),
    ENEMY("Enemy", 'E'),
    COLLECTABLE("Collectable", 'C');

    private String name;
    private char semChar;

    GameElemMenuEntries(String name, char semChar)
    {
        this.name = name;
        this.semChar = semChar;
    }

    public String toString()
    {
        return this.name;
    }

    public char getChar() { return this.semChar; }
}

package main.computation;

public enum GameElemMenuEntries {

    Floor("Floor"),
    Platform("Platform"),
    Enemy("Enemy"),
    Hole("Hole");

    private String name;
    // TODO private char displayChar;

    GameElemMenuEntries(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }

}

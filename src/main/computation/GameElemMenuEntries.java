package main.computation;

public enum GameElemMenuEntries {

    FLoor("Floor"),
    PLatform("Platform"),
    Enemy("Enemy"),
    Hole("Hole");

    private String name;

    GameElemMenuEntries(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }

}

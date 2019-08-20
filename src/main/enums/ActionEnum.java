package main.enums;

public enum ActionEnum {

    ADD("Add"),
    DELETE("Delete");

    private String description;

    ActionEnum(String description)
    {
        this.description = description;
    }

}

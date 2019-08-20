package main.enums;


public enum RasterEnum {

    R8x8("8x8", 8),
    R14x14("14x14", 14),
    R16x16("16x16", 16),
    R32x32("32x32", 32),
    R64x64("64x64", 64);

    private final String name;
    private final int raster;

    RasterEnum(String s, int r) {

        this.name = s;
        this.raster = r;
    }

    public String toString() {
        return this.name;
    }
}
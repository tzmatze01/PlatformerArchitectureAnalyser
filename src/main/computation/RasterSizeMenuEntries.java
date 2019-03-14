package main.computation;


public enum RasterSizeMenuEntries {

    R8x8("8x8", 8),
    R16x16("16x16", 16),
    R32x32("32x32", 32);

    private final String name;
    private final int raster;

    RasterSizeMenuEntries(String s, int r) {

        name = s;
        raster = r;
    }

    public String toString() {
        return this.name;
    }
}
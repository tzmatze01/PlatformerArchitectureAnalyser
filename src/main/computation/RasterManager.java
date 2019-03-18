package main.computation;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RasterManager {

    private Image tile;
    private PixelReader pixelReader;

    private Map<Integer, Character>  rasterboxCharMap; // key : rasterbox hash, value : char representation
    private char[][] charRepresentation;

    private int[][] charHashMap;

    private int rasterSize;
    private int tileSideLength;
    private int rasterBoxSideLength;

    public RasterManager(int rasterSize)
    {
        this.rasterSize = rasterSize;
        this.rasterboxCharMap = new HashMap<>();
        this.charRepresentation = new char[rasterSize][rasterSize];

        charHashMap = new int[rasterSize][rasterSize];
    }

    // TODO set rasterSize() -> calls setTile()

    public Image getTile() {
        return tile;
    }

    public void setTile(Image tile) {

        this.tile = tile;
        this.pixelReader =  tile.getPixelReader();
        this.tileSideLength = (int) tile.getWidth();
        this.rasterBoxSideLength = tileSideLength / rasterSize;

        this.charRepresentation = new char[rasterSize][rasterSize];


        charHashMap = new int[rasterSize][rasterSize];

        System.out.println("tilesidelength:" + tileSideLength);
        System.out.println("rbSideLength:" + rasterBoxSideLength);

        analyseTile();
    }

    // TODO build table char[][] - hash
    private void analyseTile()
    {
        // check if any previously analysed raster match with any raster of current tile
        for(int i = 0; i < rasterSize; ++i) {
            for(int j = 0; j < rasterSize; ++j) {

                int hash = analyseRasterBox(i, j);

                if(rasterboxCharMap.keySet().contains(hash)) {

                    // System.out.println(""+ i + " "+ j +" hash: "+hash);
                    charRepresentation[j][i] = rasterboxCharMap.get(hash);
                }
            }
        }
    }

    private int analyseRasterBox(int x, int y)
    {
        int xStart = rasterBoxSideLength * x;
        int xEnd = xStart + rasterBoxSideLength;

        int yStart = rasterBoxSideLength * y;
        int yEnd = yStart + rasterBoxSideLength;

        List<Color> pixels = new ArrayList<>();

        for (int readX = xStart; readX < xEnd; ++readX) {
            for (int readY = yStart; readY < yEnd; ++readY) {
                pixels.add(pixelReader.getColor(readX, readY));
            }
        }

        return new RasterBox(pixels).hashCode();
    }

    public char[][] getCharRepresentation()
    {
        printSemMap();
        return this.charRepresentation;
    }

    public void addCharForRasterBox(int x, int y, char c)
    {
        int hash = analyseRasterBox(x, y);

        System.out.println("x: "+ x + " y: "+ y +" hash: "+hash);

        rasterboxCharMap.put(hash, c);
        charRepresentation[y][x] = c;

        // TODO myb with flag param, to  disable 'autocompletion' of chars
         analyseTile();
    }

    public void deleteRasterBox(int x, int y)
    {
        int hash = analyseRasterBox(x, y);

        if(rasterboxCharMap.keySet().contains(hash))
            rasterboxCharMap.remove(hash);
        else
            System.out.println("Cannot delete, because this rasterbox was not mapped before!");
    }

    private void printSemMap()
    {
        for(int i = 0; i < rasterSize; ++i)
        {
            for(int j = 0; j < rasterSize; ++j)
            {
                System.out.print("\t"+charRepresentation[i][j]);
            }
            System.out.println();
        }
    }
}

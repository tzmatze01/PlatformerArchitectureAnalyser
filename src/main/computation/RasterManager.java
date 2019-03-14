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

    private Map<Integer, Character>  rasterboxCharMap; // key : rasterbox hash, value : char reoresentation
    private char[][] charRepresentation;

    private int rasterSize;
    private int tileSideLength;
    private int rasterBoxSideLength;

    public RasterManager(int rasterSize) //, Image tile)
    {
        this.rasterSize = rasterSize;
        //this.tile = tile;
        this.rasterboxCharMap = new HashMap<>();
        this.charRepresentation = new char[rasterSize][rasterSize];

        //this.pixelReader = tile.getPixelReader();

        //this.tileSideLength = (int) tile.getWidth();
        //this.rasterBoxSideLength = tileSideLength / rasterSize;
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

        analyseTile();
    }

    private void analyseTile()
    {
        // check if any previously analysed raster match with any raster of current tile
        for(int i = 0; i < rasterSize; ++i) {
            for(int j = 0; j < rasterSize; ++j) {
                int hash = analyseRasterBox(i, j);

                if(rasterboxCharMap.keySet().contains(hash))
                    charRepresentation[i][j] = rasterboxCharMap.get(hash);
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

        for (int readY = yStart; readY < yEnd; ++readY) {
            for (int readX = xStart; readX < xEnd; ++readX) {
                pixels.add(pixelReader.getColor(readX, readY));
            }
        }

        return new RasterBox(pixels).hashCode();
    }

    public char[][] getCharRepresentation()
    {
        return this.charRepresentation;
    }

    public void addCharForRasterBox(int x, int y, char c)
    {
        int hash = analyseRasterBox(x, y);

        System.out.println("x: "+ x + " y: "+ y +" hash: "+hash);
        
        rasterboxCharMap.put(hash, c);
        charRepresentation[x][y] = c;

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

}

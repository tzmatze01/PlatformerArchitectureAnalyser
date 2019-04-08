package main.computation;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import main.enums.GameElemMenuEntries;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RasterManager {

    private Image tile;
    private PixelReader pixelReader;

    private Map<Integer, Character>  rasterboxCharMap; // key : rasterbox hash, value : char representation
    private Map<Integer, GameElemMenuEntries>  hashGameElemMap; // key : rasterbox hash, value : char representation
    private GameElemMenuEntries[][] charRepresentation;

    private int rasterSize;
    private int tileSideLength;
    private int rasterBoxSideLength;

    public RasterManager(int rasterSize)
    {
        this.rasterSize = rasterSize;
        //this.rasterboxCharMap = new HashMap<>();
        this.hashGameElemMap = new HashMap<>();
        this.charRepresentation = new GameElemMenuEntries[rasterSize][rasterSize];
    }

    // TODO set rasterSize() -> calls setTile()

    public void setRasterSize(int rasterSize)
    {
        this.rasterSize = rasterSize;
        this.rasterBoxSideLength = tileSideLength / rasterSize;

        this.charRepresentation = new GameElemMenuEntries[rasterSize][rasterSize];

        analyseTile();
    }

    public Image getTile() {
        return tile;
    }

    public void setTile(Image tile) {

        this.tile = tile;
        this.pixelReader =  tile.getPixelReader();
        this.tileSideLength = (int) tile.getWidth();
        this.rasterBoxSideLength = tileSideLength / rasterSize;

        this.charRepresentation = new GameElemMenuEntries[rasterSize][rasterSize];

        analyseTile();
    }

    // TODO build table char[][] - hash for better performance
    private void analyseTile()
    {
        // check if any previously analysed raster match with any raster of current tile
        for(int i = 0; i < rasterSize; ++i) {
            for(int j = 0; j < rasterSize; ++j) {

                int hash = analyseRasterBox(i, j);

                //if(rasterboxCharMap.keySet().contains(hash)) {
                if(hashGameElemMap.keySet().contains(hash)) {
                    //charRepresentation[j][i] = rasterboxCharMap.get(hash);
                    charRepresentation[j][i] = hashGameElemMap.get(hash);
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

    public GameElemMenuEntries[][] getCharRepresentation()
    {
        //printSemMap();
        return this.charRepresentation;
    }

    public Map<Integer, GameElemMenuEntries> getGameElemMapping()
    {
        return this.hashGameElemMap;
    }

    public void addCharForRasterBox(int x, int y, GameElemMenuEntries geme)//char c)
    {
        int hash = analyseRasterBox(x, y);

        System.out.println("x: "+ x + " y: "+ y +" hash: "+hash);

        //rasterboxCharMap.put(hash, c);
        //charRepresentation[y][x] = c;

        hashGameElemMap.put(hash, geme);
        charRepresentation[y][x] = geme;

        // TODO myb with flag param, to  disable 'autocompletion' of chars
         analyseTile();
    }

    public void deleteRasterBox(int x, int y)
    {
        int hash = analyseRasterBox(x, y);

        //if(rasterboxCharMap.keySet().contains(hash))
        if(hashGameElemMap.keySet().contains(hash))
            hashGameElemMap.remove(hash);
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

    public void saveToFile(String filename, String tileName, String rbOffset) {
        try (PrintWriter out = new PrintWriter("src/main/semanticMaps/"+filename+"_rs"+rasterSize+"tn"+tileName+"ro"+rbOffset+".txt")) {

            for (int i = 0; i < rasterSize; ++i) {

                for (int j = 0; j < rasterSize; ++j) {
                    out.print(charRepresentation[i][j]);

                    if (j < rasterSize - 1)
                        out.print("; ");
                }
                out.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

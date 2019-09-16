package main.computation;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import main.enums.SymbolEnum;
import main.model.RasterBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RBManager {

    // offset for map
    private int xOffset = 0;
    private int yOffset = 0;

    private Image map;

    private PixelReader pixelReader;

    private int rbLength;

    private int[][] rbMatrix = new int[0][0];
    private int mHeight;
    private int mWidth;


    private HashMap<Integer, SymbolEnum> rbStorage = new HashMap<>(); // saves Chars-Hashes over all maps during runtime


    public void setMap(Image map, int numCellsHeight, int xOff, int yOff)
    {
        this.map = map;
        this.pixelReader = map.getPixelReader();

        this.mHeight = numCellsHeight;
        this.rbLength = (int)(map.getHeight() / numCellsHeight);
        this.mWidth = (int) (map.getWidth() / rbLength);

        this.xOffset = xOff;
        this.yOffset = yOff;
    }


    /*
    The creation of hashes for all RBs should start manually via a button, because the createn of hashes is
    time consuming
     */
    public String startAnalyzation()
    {
        if(mHeight != 0 && map != null) {

            this.mWidth = (int) (map.getWidth() / rbLength);
            this.rbMatrix = new int[mWidth][mHeight];

            for (int wBox = 0; wBox < mWidth; wBox++) {
                for (int hBox = 0; hBox < mHeight; hBox++) {
                    rbMatrix[wBox][hBox] = analyseRasterBox(wBox, hBox);
                }
            }

            return "Initialised RasterBlock Matrix.";
        }
        else
            return "Error. Raster size not set.";
    }

    public void addMapping(int widthRB, int heightRB, SymbolEnum symbol)
    {
        rbStorage.put(rbMatrix[widthRB][heightRB], symbol);
    }



    private int analyseRasterBox(int width, int height)
    {
        // this is only a warning
        if(Math.abs(xOffset) > rbLength)
            System.out.println("x offset is greater than the pixels in a rasterbox!");

        if(Math.abs(yOffset) > rbLength)
            System.out.println("y offset is greater than the pixels in a rasterbox!");

        int wStart = (this.rbLength * width) - this.xOffset;
        int wEnd = wStart + this.rbLength;

        int hStart = (this.rbLength * height) - this.yOffset;
        int hEnd = hStart + this.rbLength;


        wStart = (wStart < 0) ? 0 : wStart;
        wEnd = (wEnd > map.getWidth()) ? (int) map.getWidth() : wEnd;

        hStart = (wStart < 0) ? 0 : hStart;
        hEnd = (hEnd > map.getHeight()) ? (int) map.getHeight() : hEnd;



        List<Color> pixels = new ArrayList<>();

        for (int readW = wStart; readW < wEnd; ++readW) {
            for (int readH = hStart; readH < hEnd; ++readH) {
                pixels.add(pixelReader.getColor(readW, readH));
            }
        }

        return new RasterBox(pixels).hashCode();
    }

    public int getMatrixWidth()
    {
        return mWidth;
    }

    public int getmHeight()
    {
        return mHeight;
    }

    public int getNumRBs()
    {
        return mWidth * mHeight;
    }

    public int getNumMarkedRBs()
    {
        int marked = 0;

        for(int height = 0; height < mHeight; height++)
        {
            for(int width = 0; width < mWidth; width++)
            {
                if(rbStorage.containsKey(rbMatrix[width][height]))
                    marked++;
            }
        }

        return marked;
    }

    // returns geme with length 0 if requested tile-window is out of bounds from map
    public SymbolEnum[][] getCharMatrix(int tileNumber, int rbOffset)
    {


        // check if tile raster is out of range
        if((tileNumber * mHeight) + rbOffset + mHeight <= mWidth)
        {
            SymbolEnum[][] symbolTile = new SymbolEnum[mHeight][mHeight];

            int startWidth = (tileNumber * mHeight) + rbOffset;
            int endWidth = startWidth + mHeight;

            for(int heigth = 0; heigth < mHeight; heigth++) {
                for (int width = startWidth; width < endWidth; width++)
                {
                    int normWidth = width - startWidth;
                    symbolTile[normWidth][heigth] = rbStorage.get(rbMatrix[width][heigth]);
                }
            }


            return symbolTile;
        }

        System.out.println("Requested Raster window is out of bounds!");
        System.out.println("Returns null for tile: "+tileNumber+" rbo: "+rbOffset);
        return new SymbolEnum[0][0];
    }
 }

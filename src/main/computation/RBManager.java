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

    private int rbLength = 0;

    private int[][] rbMatrix = new int[0][0];
    private int mHeight = 0;
    private int mWidth = 0;


    private HashMap<Integer, SymbolEnum> rbStorage = new HashMap<>(); // saves Chars-Hashes over all maps during runtime


    public void setMap(Image map)
    {
        this.map = map;
        this.pixelReader = map.getPixelReader();
    }

    public void setNumRBsHeight(int numRBsHeight)
    {
        this.mHeight = numRBsHeight;

        if(this.map.getHeight() % numRBsHeight != 0)
        {
            System.out.println("Rastersize does not match Imageheight evenly!");
        }

        this.rbLength = (int)(map.getHeight() / numRBsHeight);
        //this.tileSideLength = (int)map.getHeight();
    }

    public void setMapOffset(int xOff, int yOff)
    {
        this.xOffset = xOff;
        this.yOffset = yOff;
    }

    /*
    The creation of hashes for all RBs should start manually via a button, because the createn of hashes is
    time consuming
     */
    public String startAnalyzation()
    {
        if(mHeight != 0 && rbLength != 0) {

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

    public void setCharForRB(int widthRB, int heightRB, SymbolEnum geme)
    {

        System.out.println("yRB: "+heightRB+ " xRB: "+widthRB);

        // TODO geändert

        /* check if char for this hash already stored, or put if not
        if(rbStorage.putIfAbsent(rbMatrix[heightRB][widthRB], geme) != null)
        {
            // check if the given char is not the same as the stored one
            if(rbStorage.get(rbMatrix[heightRB][widthRB]).getChar() != geme.getChar()) {
                //rbStorage.remove(rbMatrix[widthRB][heightRB]);
                rbStorage.put(rbMatrix[heightRB][widthRB], geme);
            }
        }
        */

        // check if char for this hash already stored
        if(rbStorage.containsKey(rbMatrix[widthRB][heightRB]))
        {
            // check if the given char is not the same as the stored one
            if(rbStorage.get(rbMatrix[widthRB][heightRB]).getChar() != geme.getChar()) {
                rbStorage.remove(rbMatrix[widthRB][heightRB]);
                rbStorage.put(rbMatrix[widthRB][heightRB], geme);
            }
        }
        else
            rbStorage.put(rbMatrix[widthRB][heightRB], geme);
    }

    /*
    private void initRasterBoxes()
    {
        this.mWidth = (int)(map.getWidth() / rbLength);

        // for the rest of the division above should also be a RB
        //if(map.getWidth() % rbLength != 0)
        //    mWidth += 1;

        this.rbMatrix = new int[mWidth][numRBsHeight];

        for(int wBox = 0; wBox < mWidth; wBox++)
        {
            for(int hBox = 0; hBox < numRBsHeight; hBox++)
            {
                rbMatrix[wBox][hBox] = analyseRasterBox(wBox, hBox);
            }
        }
    }
    */

    private int analyseRasterBox(int width, int height)
    {
        // this is only a warning
        if(Math.abs(xOffset) > rbLength)
            System.out.println("x offset is greater than the pixels in a rasterbox!");

        if(Math.abs(yOffset) > rbLength)
            System.out.println("y offset is greater than the pixels in a rasterbox!");

        int wStart = (rbLength * width) - xOffset;
        int wEnd = wStart + rbLength;

        int hStart = (rbLength * height) - yOffset;
        int hEnd = hStart + rbLength;

        //System.out.println("yOffset: "+yOffset+ " xOffset: "+xOffset+" rbslength: "+rbLength);
        //System.out.println("yStart: "+hStart+ " yEnd: "+hEnd);
        //System.out.println("xStart: "+wStart+ " xEnd: "+wEnd+"\n");

        // TODO also wStart > map.getWidth  && < 0
        if(wStart < 0)
            wStart = 0;
        else if(wEnd > map.getWidth())
            wEnd = (int)map.getWidth();

        if(hStart < 0)
            hStart = 0;
        else if(hEnd > map.getHeight())
            hEnd = (int)map.getHeight();


        //System.out.println("y: "+hStart+ " - "+hEnd+" x: "+wStart+" - "+wEnd);

        List<Color> pixels = new ArrayList<>();

        for (int readW = wStart; readW < wEnd; ++readW) {
            for (int readH = hStart; readH < hEnd; ++readH) {
                pixels.add(pixelReader.getColor(readW, readH));
            }
        }
        //System.out.println("RB x:"+width+" y:"+height+" has "+pixels.size()+" pixels!");

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
        // TODO faulty and dangerous if raasterBoxhashes not set
       // return rbMatrix.length * rbMatrix[0].length;
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
        SymbolEnum[][] gemes = new SymbolEnum[mHeight][mHeight];

        // check if tile raster is out of range
        if((tileNumber * mHeight) + rbOffset + mHeight > mWidth)
            System.out.println("Requested Raster window is out of bounds!");
        else
        {
            int startWidth = (tileNumber * mHeight) + rbOffset;
            int endWidth = startWidth + mHeight;

            for(int heigth = 0; heigth < mHeight; heigth++)
            {
                int tmpWidth = 0;
                for (int width = startWidth; width < endWidth; width++)
                {
                    gemes[tmpWidth][heigth] = rbStorage.get(rbMatrix[width][heigth]);
                    tmpWidth++;
                }
            }


            //System.out.println("Returns geme for tile: "+tileNumber+" rbo: "+rbOffset);
            return gemes;
        }

        System.out.println("Returns null for tile: "+tileNumber+" rbo: "+rbOffset);
        return new SymbolEnum[0][0];
    }
 }

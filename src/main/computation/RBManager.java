package main.computation;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import main.enums.GameElemMenuEntries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RBManager {

    private Image map;
    private PixelReader pixelReader;

    private int numRBsHeight;
    private int numRBsWidth;
    private int tileSideLength;
    private int rasterBoxSideLength;

    private int xMapOffset;
    private int yMapOffset;

    private int[][] rasterBoxHashes;
    private HashMap<Integer, GameElemMenuEntries> rbHashesForChars; // saves Chars-Hashes over all maps during runtime


    public RBManager() {
        this.rbHashesForChars = new HashMap<>();
        this.rasterBoxHashes = new int[0][0]; // init with 0 if getNumRBs is called early
    }

    public void setMap(Image map)
    {
        this.map = map;
        this.pixelReader = map.getPixelReader();
    }

    public void setNumRBsHeight(int numRBsHeight)
    {
        this.numRBsHeight = numRBsHeight;

        if(this.map.getHeight() % numRBsHeight != 0)
        {
            System.out.println("Rastersize does not match Imageheight evenly!");
        }

        this.rasterBoxSideLength = (int)(map.getHeight() / numRBsHeight);
        this.tileSideLength = (int)map.getHeight();
    }

    public void setMapOffset(int xOff, int yOff)
    {
        this.xMapOffset = xOff;
        this.yMapOffset = yOff;
    }

    /*
    The creation of hashes for all RBs should start manually via a button, because the createn of hashes is
    time consuming
     */
    public String startAnalyzation()
    {
        initRasterBoxes();

        return "Initialised Raster-Map.";
    }

    public void setCharForRB(int widthRB, int heightRB, GameElemMenuEntries geme)
    {

        System.out.println("yRB: "+heightRB+ " xRB: "+widthRB);

        // check if char for this hash already stored
        if(rbHashesForChars.containsKey(rasterBoxHashes[widthRB][heightRB]))
        {
            // check if the given char is not the same as the stored one
            if(rbHashesForChars.get(rasterBoxHashes[widthRB][heightRB]).getChar() != geme.getChar()) {
                rbHashesForChars.remove(rasterBoxHashes[widthRB][heightRB]);
                rbHashesForChars.put(rasterBoxHashes[widthRB][heightRB], geme);
            }
        }
        else
            rbHashesForChars.put(rasterBoxHashes[widthRB][heightRB], geme);
    }

    private void initRasterBoxes()
    {
        this.numRBsWidth = (int)(map.getWidth() / rasterBoxSideLength);

        // for the rest of the division above should also be a RB
        if(map.getWidth() % rasterBoxSideLength != 0)
            numRBsWidth += 1;

        this.rasterBoxHashes = new int[numRBsWidth][numRBsHeight];

        for(int wBox = 0; wBox < numRBsWidth; wBox++)
        {
            for(int hBox = 0; hBox < numRBsHeight; hBox++)
            {
                rasterBoxHashes[wBox][hBox] = analyseRasterBox(wBox, hBox);
            }
        }
    }

    private int analyseRasterBox(int width, int height)
    {

        if(Math.abs(xMapOffset) > rasterBoxSideLength)
            System.out.println("x offset is greater than the pixels in a rasterbox!");

        if(Math.abs(yMapOffset) > rasterBoxSideLength)
            System.out.println("y offset is greater than the pixels in a rasterbox!");

        int wStart = (rasterBoxSideLength * width) - xMapOffset;
        int wEnd = wStart + rasterBoxSideLength;

        int hStart = (rasterBoxSideLength * height) - yMapOffset;
        int hEnd = hStart + rasterBoxSideLength;

        //System.out.println("yOffset: "+yMapOffset+ " xOffset: "+xMapOffset+" rbslength: "+rasterBoxSideLength);
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

    public int getNumRBs()
    {
        // TODO faulty and dangerous if raasterBoxhashes not set
        return rasterBoxHashes.length * rasterBoxHashes[0].length;
    }

    public int getNumMarkedRBs()
    {
        int marked = 0;

        for(int width = 0; width < rasterBoxHashes.length; width++)
        {
            for(int height = 0; height < rasterBoxHashes[0].length; height++)
            {
                if(rbHashesForChars.containsKey(rasterBoxHashes[width][height]))
                    marked++;
            }
        }

        return marked;
    }

    public GameElemMenuEntries[][] getCharMap(int tileNumber, int rbOffset)
    {
        GameElemMenuEntries[][] gemes = new GameElemMenuEntries[numRBsHeight][numRBsHeight];

        // check if tile raster is out of range
        if((tileNumber * numRBsHeight) + rbOffset + numRBsHeight > numRBsWidth)
            System.out.println("Requested Raster window is out of bounds!");
        else
        {
            int startWidth = (tileNumber * numRBsHeight) + rbOffset;
            int endWidth = startWidth + numRBsHeight;

            for(int heigth = 0; heigth < numRBsHeight; heigth++) {

                int tmpWidth = 0;
                for (int width = startWidth; width < endWidth; width++)
                {
                    gemes[tmpWidth][heigth] = rbHashesForChars.get(rasterBoxHashes[width][heigth]);
                    tmpWidth++;
                }
            }
        }

        return gemes;
    }
 }

package main.controller;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.computation.RBManager;
import main.enums.CharActionsMenuEntries;
import main.enums.GameElemMenuEntries;
import main.computation.RasterManager;
import main.enums.RasterSizeMenuEntries;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/*
TODO register mouse pressed - felder schneller ausmalen
TODO canvas mit map auch anmalen
TODO autovervollständigung an und ausschaltbar

TODO buttons um bild zu verschieben


 */
public class CustomController implements Initializable {

    private int horizontalImageOffset = 0;
    private int verticalImageOffset = 0;

    private final int RESIZE_FACTOR = 3; // resizes image ! is also used in calculation for offset et al.

    //private RasterManager rasterManager;
    private RBManager rbManager;
    private double rbSideLength;
    private int rbOffset;

    private List<File> listOfFiles;
    private int currFileIndex;

    private Image currImage;
    private Image currDispImage;
    private String imageName;
    private int currImageSideLength;
    private int imageHeight;

    private int tileNumber;

    private Canvas cMap;
    private Canvas cSemMap;

    /*
            STACKPANE
     */
    @FXML
    private StackPane spMap;

    @FXML
    private StackPane spSemMap;

    @FXML
    private StackPane spLog;

    /*
            IMAGEVIEW
     */
    @FXML
    private ImageView ivMap;

    /*
            BUTTONS
     */
    @FXML
    private Button bStartAnalyzing;
    @FXML
    private Button bNextMap;

    @FXML
    private Button bNextTile;

    @FXML
    private Button bPreviousTile;

    @FXML
    private Button bFirstTile;

    @FXML
    private Button bSaveTile;

    @FXML
    private Button bNextRasterBox;

    @FXML
    private Button bPreviousRasterBox;

    @FXML
    private Button bPixelUp;

    @FXML
    private Button bPixelLeft;

    @FXML
    private Button bPixelRight;

    @FXML
    private Button bPixelDown;

    /*
            COMBOBOX
     */
    @FXML
    private ComboBox cbRaster;

    @FXML
    private ComboBox cbGameElement;

    @FXML
    private ComboBox cbAction;

    /*
            LABEL
     */

    private Text tLog;

    private Text tRBsFound;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        listOfFiles = new ArrayList<>();

        // load Folder with images
        File folder = new File("/Users/matthiasdaiber/Documents/Universitaet/SS19/code/maps/SMB/valid");
        File[] tmpFiles = folder.listFiles();

        for(File f : tmpFiles)
        {
            if(f.getName().contains(".png") || f.getName().contains(".jpg") || f.getName().contains(".jpeg"))
                listOfFiles.add(f);
        }
        currFileIndex = 0;

        tileNumber = 0;
        rbOffset = 0;

        bStartAnalyzing.setText("Start Analyzing");
        bNextMap.setText("Next Map");
        bNextTile.setText("Load Next Tile");
        bPreviousTile.setText("Load Previous Tile");
        bFirstTile.setText("Load First Tile");
        bSaveTile.setText("Save Tile");
        bNextRasterBox.setText("Load next RB");
        bPreviousRasterBox.setText("Load previous RB");
        bPixelUp.setText("Up");
        bPixelLeft.setText("Left");
        bPixelRight.setText("Right");
        bPixelDown.setText("Down");

        tLog = new Text(300, 100, "");
        tLog.setFont(Font.font ("Arial", 20));
        tLog.setFill(Color.BLACK);

        tRBsFound = new Text(300, 40, "0 RBs marked.");
        tRBsFound.setFont(Font.font ("Arial", 20));
        tRBsFound.setFill(Color.BLACK);

        VBox vbLog = new VBox();
        vbLog.getChildren().add(tRBsFound);
        vbLog.getChildren().add(tLog);

        spLog.getChildren().add(vbLog);

        cbRaster.setItems(FXCollections.observableArrayList(RasterSizeMenuEntries.values()));
        cbRaster.getSelectionModel().selectFirst();

        cbGameElement.setItems(FXCollections.observableArrayList(GameElemMenuEntries.values()));
        cbGameElement.getSelectionModel().selectFirst();

        cbAction.setItems(FXCollections.observableArrayList(CharActionsMenuEntries.values()));
        cbAction.getSelectionModel().selectFirst();

        //tfRaster.setText("10");
        //drawRaster();

        //rasterManager = new RasterManager(getRasterSize());
        rbManager = new RBManager();
    }

    @FXML
    private void moveImageUp()
    {
        verticalImageOffset -= RESIZE_FACTOR;
        loadTile();
        rbManager.setMapOffset(horizontalImageOffset / RESIZE_FACTOR, verticalImageOffset / RESIZE_FACTOR);
    }

    @FXML
    private void moveImageDown()
    {
        verticalImageOffset += RESIZE_FACTOR;
        loadTile();
        rbManager.setMapOffset(horizontalImageOffset / RESIZE_FACTOR, verticalImageOffset / RESIZE_FACTOR);
    }

    @FXML
    private void moveImageLeft()
    {
        horizontalImageOffset += RESIZE_FACTOR;
        loadTile();
        rbManager.setMapOffset(horizontalImageOffset / RESIZE_FACTOR, verticalImageOffset / RESIZE_FACTOR);
    }

    @FXML
    private void moveImageRight()
    {
        horizontalImageOffset -= RESIZE_FACTOR;
        loadTile();
        rbManager.setMapOffset(horizontalImageOffset / RESIZE_FACTOR, verticalImageOffset / RESIZE_FACTOR);
    }


    @FXML
    private void loadNextImage()
    {
        // TODO check if file or dir
        //currImage = new Image(getClass().getResource("../../maps/test.png").toExternalForm());
        System.out.println("path: "+listOfFiles.get(currFileIndex).getAbsolutePath());
        currImage = new Image(listOfFiles.get(currFileIndex).toURI().toString());

        imageHeight = (int) currImage.getHeight();

        // imageName = "test";
        imageName = listOfFiles.get(currFileIndex).getName();

        rbManager.setMap(currImage);

        ++currFileIndex;
        if(currFileIndex >= listOfFiles.size()) {
            System.out.println("seen all images");
            currFileIndex = 0;
        }

        // canvases need to be initiaised for every new iage, to fit proportions of img
        initCanvases();

        //if(ivMap.getImage() == null)
        loadFirstTile();
    }


    private void initCanvases()
    {
        currImageSideLength = (int) currImage.getHeight() * RESIZE_FACTOR;

        ivMap.setFitHeight(currImageSideLength);
        ivMap.setFitWidth(currImageSideLength);

        // init canvasas and add to stackpanes for overlay grid
        cMap = new Canvas(currImageSideLength, currImageSideLength);
        cSemMap = new Canvas(currImageSideLength, currImageSideLength);


        rbSideLength = currImageSideLength / getRasterSize();

        spMap.getChildren().add(cMap);
        spSemMap.getChildren().add(cSemMap);

        cMap.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                identifyRasterBox(event.getX(), event.getY());
                updateLabels();
            }
        });

        cSemMap.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                identifyRasterBox(event.getX(), event.getY());
                updateLabels();
            }
        });
    }

    private void updateLabels()
    {
        tRBsFound.setText("Marked "+rbManager.getNumMarkedRBs()+" / "+rbManager.getNumRBs()+" Rasterblocks");

        if(rbManager.getNumMarkedRBs() == rbManager.getNumRBs())
            tRBsFound.setFill(Color.GREEN);
        else
            tRBsFound.setFill(Color.BLACK);
    }

    private void identifyRasterBox(double x, double y)
    {

        // TODO calc in tilenumber for RBManager
        // TODO + RB offset

        int tmpTileNumber = tileNumber;

        double rasterBoxSide = cMap.getHeight() / getRasterSize();
        int rbX = (int ) (x / rasterBoxSide);
        int rbY = (int ) (y / rasterBoxSide);

        /*
        Not needed, because next calculation does the same if rbOffset > getRasterSize()
        this might cause problems with loadPreviousTile ?

        if(rbX + rbOffset > getRasterSize())
            tmpTileNumber++;
        else
            rbX += rbOffset;
        */

        rbX += rbOffset;
        rbX += tmpTileNumber * getRasterSize();

        /*
        if(cbAction.getValue().toString().matches(CharActionsMenuEntries.DELETE.toString()))
        {
            rasterManager.deleteRasterBox(rbX, rbY);
        }
        else if(cbAction.getValue().toString().matches(CharActionsMenuEntries.ADD.toString()))
        {
            rasterManager.addCharForRasterBox(rbX, rbY, getSemChar());
        }
        */

        rbManager.setCharForRB(rbX, rbY, getSemChar());

        drawRaster();
        drawSemanticMap();
    }

    private GameElemMenuEntries getSemChar()
    {
        GameElemMenuEntries geme = GameElemMenuEntries.BACKGROUND;

        if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.INTERACTION.toString()))
            geme = GameElemMenuEntries.INTERACTION;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.MOVING_PLATFORM.toString()))
            geme = GameElemMenuEntries.MOVING_PLATFORM;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.COLLECTABLE.toString()))
            geme = GameElemMenuEntries.COLLECTABLE;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.DISAPPEARING_PLATFORM.toString()))
            geme = GameElemMenuEntries.DISAPPEARING_PLATFORM;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.BLOCK.toString()))
            geme = GameElemMenuEntries.BLOCK;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.PLATFORM.toString()))
            geme = GameElemMenuEntries.PLATFORM;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.TRAP.toString()))
            geme = GameElemMenuEntries.TRAP;

        return geme;
    }

    private void cropImage()
    {
        // TODO check if image width and height are % raster == 0 -> crop on sides that do not eval 0
    }

    @FXML
    private void startAnalyzing()
    {
        String mssg = rbManager.startAnalyzation();

        tLog.setText(mssg);
    }

    @FXML
    private void loadFirstTile()
    {
        tileNumber = 0;
        loadTile();
    }

    @FXML
    private void loadPreviousTile()
    {
        if((tileNumber - 1) >= 0) {
            tileNumber -= 1;
            loadTile();
        }
    }

    @FXML
    private void loadNextTile()
    {
        if((tileNumber + 2) * imageHeight < currImage.getWidth()) {
            tileNumber += 1;
            loadTile();
        }
    }

    private void loadTile()
    {
        PixelReader pixelReader = currImage.getPixelReader();

        int offset = (int) (rbOffset * (rbSideLength / RESIZE_FACTOR)); // rbSideLength / RESIZE_FACTOR, because image is stretched * RESIZE_FACTOR
        int xStart = tileNumber * imageHeight + offset;
        int xEnd = xStart + imageHeight;

        if(xEnd <= currImage.getWidth()) {

            // Create WritableImage
            WritableImage croppedImg = new WritableImage(imageHeight, imageHeight);
            PixelWriter pwCrop = croppedImg.getPixelWriter();

            // Determine the color of each pixel in a specified row
            for (int readY = 0; readY < imageHeight; readY++) {
                for (int readX = xStart; readX < xEnd; readX++) {

                    Color color = pixelReader.getColor(readX, readY);
                    int pwX = readX - xStart; // normalise x pos

                    // Now write a brighter color to the PixelWriter.
                    color = color.brighter();
                    pwCrop.setColor(pwX, readY, color);
                }
            }

            GraphicsContext gMap = cMap.getGraphicsContext2D();
            gMap.clearRect(0,0, currImageSideLength, currImageSideLength);
            gMap.drawImage(croppedImg, 0,0, imageHeight, imageHeight, horizontalImageOffset, verticalImageOffset, currImageSideLength, currImageSideLength);


            drawRaster();
            drawSemanticMap();
        }
        else
            System.out.println("Out of bounds!");
    }

    private void drawSemanticMap()
    {
        // TODO char size relativ to rastersize

        GraphicsContext gcSemMap = cSemMap.getGraphicsContext2D();

        //gcSemMap.setStroke(Color.RED);
        gcSemMap.beginPath();

        //GameElemMenuEntries[][] semanticMap = rasterManager.getCharRepresentation();
        GameElemMenuEntries[][] semanticMap = rbManager.getCharMap(tileNumber, rbOffset);
        //Map<Integer, GameElemMenuEntries> mapping = rasterManager.getGameElemMapping();

        for(int width = 0; width < semanticMap.length; width++)
        {
            for(int height = 0; height < semanticMap.length; height++)
            {
                int x = (int) ((height * rbSideLength) + (rbSideLength / 2));
                int y = (int) ((width * rbSideLength) + (rbSideLength / 2));

                if(semanticMap[height][width] != null) {

                    gcSemMap.setStroke(semanticMap[height][width].getColor());
                    gcSemMap.strokeText("" + semanticMap[height][width].getChar(), x, y);
                }
            }
        }

        gcSemMap.closePath();
    }

    @FXML
    private void saveTile()
    {
        //rasterManager.saveToFile(imageName, ""+tileNumber, ""+rbOffset);
    }

    @FXML
    private void changeRaster()
    {
        rbSideLength = cMap.getHeight() / getRasterSize();
        //rasterManager.setRasterSize(getRasterSize());
        rbManager.setNumRBsHeight(getRasterSize());
        drawRaster();

        // TODO call nextrasterbox?
    }

    @FXML
    private void nextRasterBox()
    {
        /*
        TODO

        rbOffset++;
        if(rbOffset >= getRasterSize())
            rbOffset = 0;

        loadTile()


         */
        //if((rbOffset + 1) * rbSideLength > imageHeight)
        if(rbOffset >= getRasterSize()) {
            rbOffset = 0;
        }
        else {
            rbOffset += 1;
            loadTile();
        }
    }

    @FXML
    private void previousRasterBox()
    {
        //if((rbOffset - 1) * rbSideLength > imageHeight)
        if((rbOffset - 1) < 0)
            rbOffset = 0;
        else {
            rbOffset -= 1;
            loadTile();
        }
    }

    private void drawRaster()
    {
        //if(ivMap.getImage() != null) {

            GraphicsContext gcMap = cMap.getGraphicsContext2D();
            GraphicsContext gcSemMap = cSemMap.getGraphicsContext2D();


            // gcMap.clearRect(0,0, cMap.getWidth(), cMap.getHeight());
            // gcSemMap.clearRect(0,0, cSemMap.getWidth(), cSemMap.getWidth());
            //gcMap.clearRect(0,0, currImageSideLength, currImageSideLength);
            gcSemMap.clearRect(0,0, currImageSideLength, currImageSideLength);

            //gcMap.drawImage(currDispImage, 0,0, imageHeight, imageHeight, horizontalImageOffset, verticalImageOffset, currImageSideLength, currImageSideLength);
            //rasterManager.setTile(cMap.snapshot(null, null));

            gcMap.setStroke(Color.RED);
            gcSemMap.setStroke(Color.RED);

            gcMap.beginPath();
            gcSemMap.beginPath();

            gcMap.setLineWidth(1.0);
            gcSemMap.setLineWidth(1.0);

            gcMap.moveTo(0,0);
            gcSemMap.moveTo(0,0);

            // horizontal lines
            for(int i = 0; i <= currImageSideLength; i += rbSideLength)
            {
                gcMap.moveTo(0, i);
                gcSemMap.moveTo(0, i);

                gcMap.lineTo(currImageSideLength, i);
                gcSemMap.lineTo(currImageSideLength, i);

                gcMap.stroke();
                gcSemMap.stroke();
            }

            // vertical lines
            for(int i = 0; i <= currImageSideLength; i += rbSideLength)
            {
                gcMap.moveTo(i, 0);
                gcSemMap.moveTo(i, 0);

                gcMap.lineTo(i, currImageSideLength);
                gcSemMap.lineTo(i, currImageSideLength);

                gcMap.stroke();
                gcSemMap.stroke();
            }

            gcMap.closePath();
            gcSemMap.closePath();
        //}
    }

    public int getRasterSize()
    {
        int rasterSize = 0;


        if(cbRaster.getValue().toString().matches(RasterSizeMenuEntries.R8x8.toString())) {
            rasterSize = 8;
        }
        else if(cbRaster.getValue().toString().matches(RasterSizeMenuEntries.R14x14.toString())) {
            rasterSize = 14;
        }
        else if(cbRaster.getValue().toString().matches(RasterSizeMenuEntries.R16x16.toString())) {
            rasterSize = 16;
        }
        else if(cbRaster.getValue().toString().matches(RasterSizeMenuEntries.R32x32.toString())) {
            rasterSize = 32;
        }
        else if(cbRaster.getValue().toString().matches(RasterSizeMenuEntries.R64x64.toString())) {
            rasterSize = 64;
        }


        return rasterSize;
    }
}

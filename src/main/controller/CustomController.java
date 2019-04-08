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
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.enums.CharActionsMenuEntries;
import main.enums.GameElemMenuEntries;
import main.computation.RasterManager;
import main.enums.RasterSizeMenuEntries;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/*
TODO register mouse pressed - felder schneller ausmalen
TODO canvas mit map auch anmalen
TODO autovervollständigung an und ausschaltbar

TODO buttons um bild zu verschieben


 */
public class CustomController implements Initializable {

    private final int RESIZE_FACTOR = 3;

    private RasterManager rasterManager;
    private double rbSideLength;
    private int rbOffset;

    private File[] listOfFiles;
    private int currFileIndex;

    private Image currImage;
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

    /*
            IMAGEVIEW
     */
    @FXML
    private ImageView ivMap;

    /*
            BUTTONS
     */
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
            TEXTFIELD
     */
    @FXML
    private TextField tfLog;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // load Folder with images
        File folder = new File("/Users/matthiasdaiber/Documents/Universitaet/SS19/code/maps/SMB/valid");
        listOfFiles = folder.listFiles();
        currFileIndex = 0;

        // System.out.println("path: "+folder.getAbsolutePath());
        // System.out.println("path: "+System.getProperty("user.dir"));

        tileNumber = 0;
        rbOffset = 0;

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

        cbRaster.setItems(FXCollections.observableArrayList(RasterSizeMenuEntries.values()));
        cbRaster.getSelectionModel().selectFirst();

        cbGameElement.setItems(FXCollections.observableArrayList(GameElemMenuEntries.values()));
        cbGameElement.getSelectionModel().selectFirst();

        cbAction.setItems(FXCollections.observableArrayList(CharActionsMenuEntries.values()));
        cbAction.getSelectionModel().selectFirst();

        drawRaster();

        rasterManager = new RasterManager(getRasterSize());
    }

    @FXML
    private void moveImageUp()
    {

    }

    @FXML
    private void moveImageLeft()
    {

    }

    @FXML
    private void moveImageRight()
    {

    }

    @FXML
    private void moveImageDown()
    {

    }

    @FXML
    private void loadNextImage()
    {
        // TODO check if file or dir
        //currImage = new Image(getClass().getResource("../../maps/test.png").toExternalForm());
        System.out.println("path: "+listOfFiles[currFileIndex].getAbsolutePath());
        currImage = new Image(listOfFiles[currFileIndex].toURI().toString());



        imageHeight = (int) currImage.getHeight();

        // imageName = "test";
        imageName = listOfFiles[currFileIndex].getName();

        ++currFileIndex;
        if(currFileIndex >= listOfFiles.length) {
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
            }
        });

        cSemMap.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                identifyRasterBox(event.getX(), event.getY());
            }
        });
    }

    private void identifyRasterBox(double x, double y)
    {
        double rasterBoxSide = cMap.getHeight() / getRasterSize();
        int rbX = (int ) (x / rasterBoxSide);
        int rbY = (int ) (y / rasterBoxSide);

        if(cbAction.getValue().toString().matches(CharActionsMenuEntries.DELETE.toString()))
        {
            rasterManager.deleteRasterBox(rbX, rbY);
        }
        else if(cbAction.getValue().toString().matches(CharActionsMenuEntries.ADD.toString()))
        {
            rasterManager.addCharForRasterBox(rbX, rbY, getSemChar());
        }

        drawRaster();
        drawSemanticMap();
    }

    private GameElemMenuEntries getSemChar()
    {
        //char semChar = GameElemMenuEntries.BACKGROUND.getChar();
        GameElemMenuEntries geme = GameElemMenuEntries.BACKGROUND;

        if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.INTERACTION.toString()))
            geme = GameElemMenuEntries.INTERACTION;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.MOVING_PLATFORM.toString()))
            geme = GameElemMenuEntries.MOVING_PLATFORM;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.DISAPPEARING_PLATFORM.toString()))
            geme = GameElemMenuEntries.DISAPPEARING_PLATFORM;
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.ENEMY.toString()))
            geme = GameElemMenuEntries.ENEMY;
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
            PixelWriter pixelWriter = croppedImg.getPixelWriter();

            // Determine the color of each pixel in a specified row
            for (int readY = 0; readY < imageHeight; readY++) {
                for (int readX = xStart; readX < xEnd; readX++) {

                    Color color = pixelReader.getColor(readX, readY);
                    int pwX = readX - xStart;

                    // Now write a brighter color to the PixelWriter.
                    color = color.brighter();
                    pixelWriter.setColor(pwX, readY, color);
                }
            }

            ivMap.setImage(croppedImg);
            rasterManager.setTile(croppedImg);

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

        GameElemMenuEntries[][] semanticMap = rasterManager.getCharRepresentation();
        //Map<Integer, GameElemMenuEntries> mapping = rasterManager.getGameElemMapping();

        for(int i = 0; i < semanticMap.length; i++)
        {
            for(int j = 0; j < semanticMap.length; j++)
            {
                int x = (int) ((j * rbSideLength) + (rbSideLength / 2));
                int y = (int) ((i * rbSideLength) + (rbSideLength / 2));

                if(semanticMap[j][i] != null) {
                    gcSemMap.setStroke(semanticMap[j][i].getColor());
                    gcSemMap.strokeText("" + semanticMap[j][i].getChar(), y, x);
                }
            }
        }

        gcSemMap.closePath();
    }

    @FXML
    private void saveTile()
    {
        rasterManager.saveToFile(imageName, ""+tileNumber, ""+rbOffset);
    }

    @FXML
    private void changeRaster()
    {
        rbSideLength = cMap.getHeight() / getRasterSize();
        rasterManager.setRasterSize(getRasterSize());
        drawRaster();

        // TODO call nextrasterbox?
    }

    @FXML
    private void nextRasterBox()
    {
        //if((rbOffset + 1) * rbSideLength > imageHeight)
        if(rbOffset >= getRasterSize())
            rbOffset = 0;
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
        if(ivMap.getImage() != null) {

            GraphicsContext gcMap = cMap.getGraphicsContext2D();
            GraphicsContext gcSemMap = cSemMap.getGraphicsContext2D();

            gcMap.clearRect(0,0, cMap.getWidth(), cMap.getHeight());
            gcSemMap.clearRect(0,0, cSemMap.getWidth(), cSemMap.getWidth());

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
        }
    }

    public int getRasterSize()
    {
        int rasterSize = 0;

        if(cbRaster.getValue().toString().matches(RasterSizeMenuEntries.R8x8.toString())) {
            rasterSize = 8;
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

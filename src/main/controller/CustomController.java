package main.controller;

import javafx.collections.FXCollections;
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

import java.net.URL;
import java.util.ResourceBundle;


public class CustomController implements Initializable {


    //private final int canvasHeight = 300;
    //private final int canvasWidth = 400;

    private RasterManager rasterManager;

    private Image currDispImg;
    private int currWStepPos;
    private double rasterBoxSide;

    private GraphicsContext gcMap;
    private GraphicsContext gcSemMap;

    @FXML
    private StackPane spMap;

    @FXML
    private StackPane spSemMap;

    private Canvas cMap;
    private Canvas cSemMap;

    @FXML
    private ImageView ivMap;

    @FXML
    private ImageView ivSemMap;

    @FXML
    private Button bNextMap;

    @FXML
    private Button bNextTile;

    @FXML
    private Button bPreviousTile;

    @FXML
    private Button bFirstTile;

    @FXML
    private ComboBox cbRaster;

    @FXML
    private ComboBox cbGameElement;

    @FXML
    private ComboBox cbAction;

    @FXML
    private TextField tfLog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        currWStepPos = 0;

        bNextMap.setText("Next Map");
        bNextTile.setText("Load Next Tile");
        bPreviousTile.setText("Load Previous Tile");
        bFirstTile.setText("Load First Tile");

        cbRaster.setItems(FXCollections.observableArrayList(RasterSizeMenuEntries.values()));
        cbRaster.getSelectionModel().selectFirst();


        cbGameElement.setItems(FXCollections.observableArrayList(GameElemMenuEntries.values()));
        cbGameElement.getSelectionModel().selectFirst();

        cbAction.setItems(FXCollections.observableArrayList(CharActionsMenuEntries.values()));
        cbAction.getSelectionModel().selectFirst();

        drawRaster();

        rasterManager = new RasterManager(getRasterSize());
    }



    /*
        TODO: versetztes Raster
        TODO: merken von tiles und auf näcshtes Rasterbild anwenden -> store num RGB pixels in raster and check every other

        TODO: generate FV for sides of image ( platform start)
     */

    @FXML
    private void loadNextImage()
    {
        // TODO load next image from folder
        currDispImg = new Image(getClass().getResource("../images/test.png").toExternalForm());

        // canvases need to be initiaised for every new iage, to fit proportions of img
        initCanvases();

        if(ivMap.getImage() == null)
            loadNextTile();
    }


    private void initCanvases()
    {
        // init canvasas and add to stackpanes for overlay grid
        cMap = new Canvas(currDispImg.getHeight(), currDispImg.getHeight());
        cSemMap = new Canvas(currDispImg.getHeight(), currDispImg.getHeight());

        rasterBoxSide = cMap.getHeight() / getRasterSize();

        spMap.getChildren().add(cMap);
        spSemMap.getChildren().add(cSemMap);

        cMap.setOnMousePressed(new EventHandler<MouseEvent>() {

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

    private char getSemChar()
    {
        char semChar = GameElemMenuEntries.BACKGORUND.getChar();

        if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.COLLECTABLE.toString()))
            semChar = GameElemMenuEntries.COLLECTABLE.getChar();
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.FLOOR.toString()))
            semChar = GameElemMenuEntries.FLOOR.getChar();
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.ENEMY.toString()))
            semChar = GameElemMenuEntries.ENEMY.getChar();
        else if(cbGameElement.getValue().toString().matches(GameElemMenuEntries.PLATFORM.toString()))
            semChar = GameElemMenuEntries.PLATFORM.getChar();

        return semChar;
    }

    private void cropImage()
    {
        // TODO check if image width and height are % raster == 0 -> crop on sides that do not eval 0
    }

    @FXML
    private void loadFirstTile()
    {
        int height = (int) currDispImg.getHeight();
        currWStepPos = 0;

        loadTile(height, currWStepPos, (currWStepPos + height));

        currWStepPos += height;
    }

    @FXML
    private void loadPreviousTile()
    {
        int height = (int) currDispImg.getHeight();

        if((currWStepPos - height) >= 0) {

            //int start = currWStepPos - height;

            // TODO problem is marker after loading tile -> new marker in the middle of the picture ?
            //currWStepPos -= height;

            loadTile(height, (currWStepPos - height), currWStepPos);
            currWStepPos -= height;
        }
    }

    @FXML
    private void loadNextTile()
    {
        //System.out.println();
        //System.out.println("currWStepPos: "+currWStepPos);

        int height = (int) currDispImg.getHeight();

        if((currWStepPos + height) <= currDispImg.getWidth()) {

            loadTile(height, currWStepPos, (currWStepPos + height));
            currWStepPos += height;
        }
    }
    private void loadTile(int height, int xStart, int xEnd)
    {
        PixelReader pixelReader = currDispImg.getPixelReader();

        // Create WritableImage
        WritableImage croppedImg = new WritableImage(height, height);
        PixelWriter pixelWriter = croppedImg.getPixelWriter();

        // Determine the color of each pixel in a specified row
        for (int readY = 0; readY < height; readY++) {
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

    private void drawSemanticMap()
    {
        GraphicsContext gcSemMap = cSemMap.getGraphicsContext2D();

        gcSemMap.setStroke(Color.RED);
        gcSemMap.beginPath();

        char[][] semanticMap = rasterManager.getCharRepresentation();

        for(int i = 0; i < semanticMap.length; i++)
        {
            for(int j = 0; j < semanticMap.length; j++)
            {
                int x = (int) ((j * rasterBoxSide) + (rasterBoxSide / 2));
                int y = (int) ((i * rasterBoxSide) + (rasterBoxSide / 2));

                gcSemMap.strokeText(""+semanticMap[j][i], y, x);
            }
        }

        gcSemMap.closePath();
    }

    @FXML
    private void changeRaster()
    {

        rasterBoxSide = cMap.getHeight() / getRasterSize();
        rasterManager.setRasterSize(getRasterSize());
        drawRaster();

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

            int imageHeight = (int) ivMap.getImage().getHeight();
            int imageWidth = (int) ivMap.getImage().getWidth();

            gcMap.beginPath();
            gcSemMap.beginPath();

            gcMap.setLineWidth(1.0);
            gcSemMap.setLineWidth(1.0);

            gcMap.moveTo(0,0);
            gcSemMap.moveTo(0,0);

            // horizontal lines
            for(int i = 0; i <= imageHeight; i += rasterBoxSide)
            {
                gcMap.moveTo(0, i);
                gcSemMap.moveTo(0, i);

                gcMap.lineTo(imageWidth, i);
                gcSemMap.lineTo(imageWidth, i);

                gcMap.stroke();
                gcSemMap.stroke();
            }

            // vertical lines
            for(int i = 0; i <= imageWidth; i += rasterBoxSide)
            {
                gcMap.moveTo(i, 0);
                gcSemMap.moveTo(i, 0);

                gcMap.lineTo(i, imageHeight);
                gcSemMap.lineTo(i, imageHeight);

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

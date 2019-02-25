package main.controller;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.computation.GameElemMenuEntries;
import main.computation.RasterMenuEntries;

import java.net.URL;
import java.util.ResourceBundle;


public class CustomController implements Initializable {


    private final int canvasHeight = 300;
    private final int canvasWidth = 400;

    private Image currDispImg;
    private int currWStepPos;

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
    private ComboBox cbRaster;

    @FXML
    private ComboBox cbGameElement;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        currWStepPos = 0;

        // init canvasas and add to stackpanes for overlay grid
        cMap = new Canvas(canvasWidth, canvasHeight);
        cSemMap = new Canvas(canvasWidth, canvasHeight);

        spMap.getChildren().add(cMap);
        spSemMap.getChildren().add(cSemMap);

        bNextMap.setText("Next Map");
        bNextTile.setText("Next Tile");

        cbRaster.setItems(FXCollections.observableArrayList(RasterMenuEntries.values()));
        cbRaster.getSelectionModel().selectFirst();

        cbGameElement.setItems(FXCollections.observableArrayList(GameElemMenuEntries.values()));
        cbGameElement.getSelectionModel().selectFirst();

        ivMap.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                System.out.println("x: "+event.getSceneX()+ " y: "+event.getSceneY());
                System.out.println("x 2: "+event.getX()+ " y 2: "+event.getY());

            }
        });

        drawRaster();
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

        //ivMap.setImage(img);


        //System.out.println("wImg H "+wImage.getHeight());
        //System.out.println("wImg W "+wImage.getWidth());

        //ivSemMap.setImage(wImage);

        loadNextRasterTile();
        //drawRaster();
    }

    @FXML
    private void loadNextRasterTile()
    {

    }

    private void cropImage()
    {
        // TODO check if image width and height are % raster == 0 -> crop on sides that do not eval 0
    }

    @FXML
    private void changeGameElem()
    {

    }

    @FXML
    private void loadNextTile()
    {
        int height = (int) currDispImg.getHeight();

        // new subimage is not out of width of orig image
        if((currWStepPos + height) <= currDispImg.getWidth()) {

            PixelReader pixelReader = currDispImg.getPixelReader();

            // Create WritableImage
            WritableImage croppedImg = new WritableImage(height, height);
            PixelWriter pixelWriter = croppedImg.getPixelWriter();

            // Determine the color of each pixel in a specified row
            for (int readY = 0; readY < height; readY++) {
                for (int readX = currWStepPos; readX < (currWStepPos + height); readX++) {

                    Color color = pixelReader.getColor(readX, readY);
                    int pwX = readX - currWStepPos;

                    // Now write a brighter color to the PixelWriter.
                    color = color.brighter();
                    pixelWriter.setColor(pwX, readY, color);
                }
            }


            currWStepPos += height;

            ivMap.setImage(croppedImg);
            ivSemMap.setImage(croppedImg);
        }
        else
            System.out.println("Loaded image is put of bounds!");
    }

    @FXML
    private void drawRaster()
    {
        GraphicsContext gcMap = cMap.getGraphicsContext2D();
        GraphicsContext gcSemMap = cSemMap.getGraphicsContext2D();

        gcMap.setStroke(Color.RED);
        gcSemMap.setStroke(Color.RED);

        int rasterSize = 0;


        if(cbRaster.getValue().toString().matches(RasterMenuEntries.R8x8.toString())) {
            rasterSize = 8;
        }
        else if(cbRaster.getValue().toString().matches(RasterMenuEntries.R16x16.toString())) {
            rasterSize = 16;
        }
        else if(cbRaster.getValue().toString().matches(RasterMenuEntries.R32x32.toString())) {
            rasterSize = 32;
        }


        if(ivMap.getImage() != null) {

            // TODO canvas at wrong position?

            int imageHeight = (int) ivMap.getImage().getHeight();
            int imageWidth = (int) ivMap.getImage().getWidth();

            int step = imageHeight / rasterSize;

            System.out.println("img height: " + imageHeight);

            gcMap.beginPath();
            gcSemMap.beginPath();

            gcMap.setLineWidth(1.0);
            gcSemMap.setLineWidth(1.0);

            gcMap.moveTo(0,0);
            gcSemMap.moveTo(0,0);

            // horizontal lines
            for(int i = 0; i < imageHeight; i += step)
            {
                gcMap.moveTo(0, i);
                gcSemMap.moveTo(0, i);

                gcMap.lineTo(imageWidth, i);
                gcSemMap.lineTo(imageWidth, i);

                gcMap.stroke();
                gcSemMap.stroke();
            }


            // vertical lines
            for(int i = 0; i < imageWidth; i += step)
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
}

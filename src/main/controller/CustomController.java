package main.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;


public class CustomController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bConfirm.setText("Click me!");

        cbRaster.setItems(FXCollections.observableArrayList(MenuEntries.values()));

        cMap = new Canvas(canvasWidth, canvasHeight);
        cSemMap = new Canvas(canvasWidth, canvasHeight);

        //cMap.setHeight(canvasHeight);
        //cMap.setWidth(canvasWidth);
        //cSemMap.setHeight(canvasHeight);
        //cSemMap.setWidth(canvasWidth);


        gcMap = cMap.getGraphicsContext2D();
        gcSemMap = cSemMap.getGraphicsContext2D();

        gcMap.setStroke(Color.RED);

        gcMap.moveTo(100, 0);
        gcMap.lineTo(100, 50);
        gcMap.stroke();

        gcMap.setFill(Color.BLACK);
        gcMap.fillRect(50, 50, 100, 100);
    }

    private enum MenuEntries {
        R8x8("8x8"),
        R16x16("16x16"),
        R32x32("32x32");

        private final String name;

        MenuEntries(String s) {
            name = s;
        }

        public String toString() {
            return this.name;
        }
    }

    private final int canvasHeight = 300;
    private final int canvasWidth = 400;

    private GraphicsContext gcMap;
    private GraphicsContext gcSemMap;

    @FXML
    private StackPane spMap;

    @FXML
    private StackPane spSemMap;

    @FXML
    private Canvas cMap;

    @FXML
    private Canvas cSemMap;

    @FXML
    private ImageView ivMap;

    @FXML
    private Button bConfirm;

    @FXML
    private ComboBox cbRaster;

    /*
    @FXML
    public void initialize()
    {
        bConfirm.setText("Click me!");

        cbRaster.setItems(FXCollections.observableArrayList(MenuEntries.values()));

        cMap = new Canvas(canvasWidth, canvasHeight);
        cSemMap = new Canvas(canvasWidth, canvasHeight);

        //cMap.setHeight(canvasHeight);
        //cMap.setWidth(canvasWidth);
        //cSemMap.setHeight(canvasHeight);
        //cSemMap.setWidth(canvasWidth);

        gcMap = cMap.getGraphicsContext2D();
        gcSemMap = cSemMap.getGraphicsContext2D();

        gcMap.setStroke(Color.RED);

        gcMap.moveTo(100, 0);
        gcMap.lineTo(100, 50);
        gcMap.stroke();

        gcMap.setFill(Color.BLACK);
        gcMap.fillRect(50, 50, 100, 100);


    }
    */
    @FXML
    private void chooseRasterSize()
    {
        // TODO called when raster size changed

        System.out.println("new val: "+cbRaster.getValue());
    }

    /*
        TODO: versetztes Raster
        TODO: merken von tiles und auf näcshtes Rasterbild anwenden -> store num RGB pixels in raster and check every other
     */

    @FXML
    private void loadNextImage()
    {
        System.out.println("button clicked!");

        Image img = new Image(getClass().getResource("../images/test.png").toExternalForm());
        ivMap.setImage(img);
    }

    @FXML
    private void loadNextRasterTile()
    {

    }

    private void cropImage()
    {
        // TODO check if image width and height are % raster == 0 -> crop on sides that do not eval 0
    }

    private void drawRaster()
    {

    }
}

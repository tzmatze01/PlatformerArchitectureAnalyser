package main.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;


public class CustomController {

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

    private int canvasHeight = 300;
    private int canvasWidth = 400;

    private GraphicsContext gc;

    @FXML
    private Canvas canvas;

    @FXML
    private Button bConfirm;

    @FXML
    private ComboBox cbRaster;

    @FXML
    public void initialize()
    {
        bConfirm.setText("Click me!");

        cbRaster.setItems(FXCollections.observableArrayList(MenuEntries.values()));

        canvas.setHeight(canvasHeight);
        canvas.setWidth(canvasWidth);

        gc = canvas.getGraphicsContext2D();
    }

    @FXML
    private void chooseRasterSize()
    {
        // TODO called when raster size changed

        System.out.println("new val: "+cbRaster.getValue());
    }

    @FXML
    private void buttton_clicked()
    {
        // TODO button clicked
        System.out.println("button clicked!");
    }

    private void cropImage()
    {
        // TODO check if image width and height are % raster == 0 -> crop on sides that do not eval 0
    }
}

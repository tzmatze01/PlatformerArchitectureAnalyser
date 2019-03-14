package main.computation;

import java.util.Objects;

public class Pixel {

    private double red;
    private double green;
    private double blue;

    public Pixel(double red, double green, double blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public double getRed() {
        return red;
    }

    public void setRed(double red) {
        this.red = red;
    }

    public double getGreen() {
        return green;
    }

    public void setGreen(double green) {
        this.green = green;
    }

    public double getBlue() {
        return blue;
    }

    public void setBlue(double blue) {
        this.blue = blue;
    }

    public int hashCode()
    {
        return Objects.hash(this.red, this.green, this.blue);
    }
}

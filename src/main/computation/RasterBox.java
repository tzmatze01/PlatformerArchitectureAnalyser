package main.computation;

import javafx.scene.paint.Color;

import java.util.*;
import java.util.List;

public class RasterBox {

    // declares howmuch of which pixel is in this rasterbox
    // apixel is represented by a hash of r, g & b
    private HashMap<Integer, Integer> numPixels;

    public RasterBox(List<Color> pixels)
    {
        this.numPixels = new HashMap<>();

        fillRasterBox(pixels);
    }

    private void fillRasterBox(List<Color> pixles)
    {
        for(Color color : pixles)
        {
            Pixel p = new Pixel(color.getRed(), color.getGreen(), color.getBlue());

            if(numPixels.keySet().contains(p.hashCode())) {
                int value = numPixels.get(p.hashCode());
                numPixels.put(p.hashCode(), value + 1);
            }
            else
                numPixels.put(p.hashCode(), 1);
        }
    }

    public int hashCode()
    {
        // TODO possibly faulty because of hashmap ??? - fixed with sort

        List<Integer> values = new ArrayList<Integer>(numPixels.values());
        Collections.sort(values);

        List<Integer> keys = new ArrayList<Integer>(numPixels.keySet());
        Collections.sort(keys);

        return Objects.hash(keys.hashCode(), values.hashCode());
    }
}

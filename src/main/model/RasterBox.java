package main.model;

import javafx.scene.paint.Color;

import java.util.*;
import java.util.List;

public class RasterBox {

    // declares howmuch of which pixel is in this rasterbox
    // apixel is represented by a hash of r, g & b
    private HashMap<Integer, Integer> pixels;

    public RasterBox(List<Color> pixels)
    {
        this.pixels = new HashMap<>();

        fillRasterBox(pixels);
    }

    private void fillRasterBox(List<Color> colors)
    {
        for(Color color : colors)
        {
            Pixel p = new Pixel(color.getRed(), color.getGreen(), color.getBlue());

            if(pixels.putIfAbsent(p.hashCode(), 1) != null)
                pixels.put(p.hashCode(), pixels.get(p.hashCode()) + 1);

        }
    }

    @Override
    public int hashCode()
    {
        // sort pixels by keys, because the are unique

        List<Integer> keys = new ArrayList<Integer>(pixels.keySet());
        Collections.sort(keys);

        List<Integer> values = new ArrayList<Integer>();
        for(Integer key : keys)
            values.add(pixels.get(key));



        return Objects.hash(keys.hashCode(), values.hashCode());
    }
}

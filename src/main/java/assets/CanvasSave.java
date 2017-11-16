package assets;

import java.util.ArrayList;

/**
 * Created by Bastian Jarzombek on 16/11/2017.
 * Project Pixelart Tool
 */
public class CanvasSave {

    public String filename;
    public String path;
    public int units;
    public int pixel;
    public ArrayList<String> canvas;
    public double winWidth;
    public double winHeight;
    public double scale;

    public CanvasSave() {
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }


    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }


    public ArrayList<String> getCanvas()
    {
        return canvas;
    }

    public void setCanvas(ArrayList<String> canvas)
    {
        this.canvas = canvas;
    }


    public int getUnits()
    {
        return units;
    }

    public void setUnits(int units)
    {
        this.units = units;
    }


    public int getPixel()
    {
        return pixel;
    }

    public void setPixel(int pixel)
    {
        this.pixel = pixel;
    }


    public double getWinWidth()
    {
        return winWidth;
    }

    public void setWinWidth(double winWidth)
    {
        this.winWidth = winWidth;
    }


    public double getWinHeight()
    {
        return winHeight;
    }

    public void setWinHeight(double winHeight)
    {
        this.winHeight = winHeight;
    }


    public double getScale()
    {
        return scale;
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }

}

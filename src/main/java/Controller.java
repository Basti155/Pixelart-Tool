import assets.CanvasSave;
import com.google.gson.Gson;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import sun.plugin2.gluegen.runtime.CPU;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by Bastian Jarzombek on 14/11/2017.
 * Project Pixelart Tool
 */
public class Controller implements Initializable
{
    //TODO Make Subclasses!

    private GraphicsContext layer,layerGrid, layerHover, layerBackground;

    private WritableImage wim;
    private Color color;

    private List<Canvas> canvasDict = new ArrayList<>();

    private int brushSize, brushScale, units, imagesSize, canvasSize;
    private double scale, x, y;

    private boolean erase, saved;


    @FXML
    private Canvas canvas, canvasGrid ,canvasHover, canvasBackground;

    @FXML
    private GridPane gp;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Label lblScale, lblInfo, lblMode;

    @FXML
    private ChoiceBox<String> brushBox;



    public void initialize(URL location, ResourceBundle resources)
    {
        scale = 1.0;
        brushScale = 1;
        units = 20;
        canvasSize = 500;
        saved = false;

        canvasDict.add(canvasBackground);
        canvasDict.add(canvas);
        canvasDict.add(canvasGrid);
        canvasDict.add(canvasHover);

        brushBox.getSelectionModel().select(0);

        newCanvas(units, canvasSize, scale);
        setEvents();
    }

    private void setEvents()
    {
        brushBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> brushScale = (int)newValue  + 1);

                canvasHover.addEventHandler(MouseEvent.MOUSE_MOVED, e -> drawBrush(e.getX(), e.getY()));

        canvasHover.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if(!erase)
                draw();
            else
                clear(e.getX(), e.getY());
        });

        canvasHover.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            drawBrush(e.getX(), e.getY());

            if(!erase)
                drawMove(e.getX(), e.getY());
            else
                clear(e.getX(), e.getY());
        });

        canvasHover.addEventHandler(ScrollEvent.SCROLL, e -> {
            if(scale >= 0.2 && scale <= 1.5)
            {
                if(e.getDeltaY() > 3)
                    scale += 0.05;
                else
                    scale -= 0.05;

                setScale(scale);
            }
            else
            {
                if(scale <= 0.2)
                    scale += 0.05;
                else
                    scale -= 0.05;
            }
        });
    }

    private void newCanvas(int units, int pixel, double scale)
    {
        this.units = units;
        this.scale = scale;
        this.canvasSize = pixel;

        for (Canvas c: canvasDict)
        {
            c.setHeight(canvasSize);
            c.setWidth(canvasSize);
        }

        imagesSize = (int) canvas.getHeight();
        brushSize = imagesSize / this.units;

        color = Color.BLACK;
        colorPicker.setValue(color);

        layerBackground = canvasBackground.getGraphicsContext2D();
        layer = canvas.getGraphicsContext2D();
        layerGrid = canvasGrid.getGraphicsContext2D();
        layerHover = canvasHover.getGraphicsContext2D();

        layer.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        wim = new WritableImage(imagesSize, imagesSize);

        canvasHover.toFront();

        setScale(scale);

        drawGrid();
        drawBackground();
    }

    private void drawBackground()
    {
        for(int x = 0; x <= units; x++)
        {
            for (int y = 0; y <= units; y++)
            {
                if ((y % 2) == 0)
                    layerBackground.setFill(Color.GREY);
                else
                    layerBackground.setFill(Color.LIGHTGREY);

                x = Math.round(x * 100) / 100;
                y = Math.round(y * 100) / 100;

                layerBackground.fillRect(x * brushSize, y * brushSize, brushSize, brushSize);
            }
        }

    }

    private void drawGrid()
    {
        double w, h;
        w = canvasGrid.getWidth() / units;
        h = canvasGrid.getHeight() / units;

        layerGrid.setFill(Color.LIGHTGRAY);

        double x1, y1;

        for(int i = 0; i <= units; i++)
        {
            x1 = w * i;
            x1 = Math.round(x1 * 100) / 100;

            layerGrid.strokeLine(x1, 0, x1, canvasGrid.getHeight());
        }

        for(int i = 0; i <= units; i++)
        {
            y1 = h * i;
            y1 = Math.round(y1 * 100) / 100;

            layerGrid.strokeLine(0, y1, canvasGrid.getWidth(), y1);
        }
    }

    private void setScale(double scale)
    {
        for (Canvas c : canvasDict )
        {
            //TODO change pivot

            c.setScaleX(scale);
            c.setScaleY(scale);
        }

        scale = (double)Math.round(scale * 100) / 100;

        lblScale.setText("Scale: " + scale);
    }

    private void drawBrush(double x, double y)
    {
        layerHover.clearRect(0, 0, canvasHover.getWidth(), canvasHover.getHeight());

        this.x = ((x - (brushSize / 2) * brushScale) / brushSize);
        this.x = ((double) Math.round(this.x * 1) / 1) * brushSize;

        this.y = ((y - (brushSize / 2) * brushScale) / brushSize);
        this.y = ((double) Math.round(this.y * 1) / 1) * brushSize;

        if(!erase)
            layerHover.setFill(color);
        else
            layerHover.setFill(Color.WHITE);

        layerHover.fillRect(this.x, this.y, brushSize * brushScale, brushSize * brushScale);


        if(!color.equals(Color.BLACK))
            layerHover.setStroke(Color.BLACK);
        else
            layerHover.setStroke(Color.WHITE);

        layerHover.strokeRect(this.x, this.y, brushSize * brushScale, brushSize * brushScale);
    }

    private void draw()
    {
        layer.setFill(color);
        layer.fillRect(this.x, this.y, brushSize * brushScale, brushSize * brushScale);
    }

    private void drawMove(double x, double y)
    {
        this.x = ((x -(brushSize/2)*brushScale) / brushSize);
        this.x = ((double)Math.round(this.x * 1) / 1) * brushSize;

        this.y = ((y -(brushSize/2)*brushScale) / brushSize);
        this.y = ((double)Math.round(this.y * 1) / 1) * brushSize;

        draw();
    }

    private void clear(double x, double y)
    {
        this.x = ((x -(brushSize/2)*brushScale) / brushSize);
        this.x = ((double)Math.round(this.x * 1) / 1) * brushSize;

        this.y = ((y -(brushSize/2)*brushScale) / brushSize);
        this.y = ((double)Math.round(this.y * 1) / 1) * brushSize;

        layer.clearRect(this.x, this.y, brushSize * brushScale, brushSize * brushScale);
    }

    @FXML
    private void saveCanvas()
    {
        Gson gson = new Gson();
        CanvasSave canvasSave = new CanvasSave();

        FileChooser.ExtensionFilter filterPNG = new FileChooser.ExtensionFilter("Pixelart Tool files (*.pxt)", "*.PXT");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.setInitialFileName("PixelArt.pxt");
        fileChooser.getExtensionFilters().add(filterPNG);
        fileChooser.setSelectedExtensionFilter(filterPNG);
        File file = fileChooser.showSaveDialog(gp.getScene().getWindow());


        canvasSave.setFilename(file.getName());
        canvasSave.setPath(file.getPath());
        canvasSave.setPixel(canvasSize);
        canvasSave.setUnits(units);
        canvasSave.setScale(scale);
        canvasSave.setWinWidth(gp.getWidth());
        canvasSave.setWinHeight(gp.getHeight());

        System.out.println(gp.getWidth() + " : " + gp.getHeight());

        ArrayList<String> dict = new ArrayList<>();


        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        canvas.snapshot(sp, wim);

        PixelReader pr = wim.getPixelReader();

        for(int h = 0; h < units; h ++)
        {
            for (int w = 0; w < units; w++)
            {
                Color color = pr.getColor(w*brushSize+5, h*brushSize+5);

                dict.add(color.toString());
            }
        }
        canvasSave.setCanvas(dict);

        try {
            String out = gson.toJson(canvasSave);

            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.print(out);
            writer.close();

            lblInfo.setText("Saved " + file.getName());
        } catch (IOException e) {
            System.err.println("Failed to save as JSON!");
        }
    }

    @FXML
    private void getSavedCanvas()
    {
        Gson gson = new Gson();

        FileChooser.ExtensionFilter filterPNG = new FileChooser.ExtensionFilter("Pixelart Tool files (*.pxt)", "*.PXT");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Pixelart");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.getExtensionFilters().add(filterPNG);
        fileChooser.setSelectedExtensionFilter(filterPNG);
        File file = fileChooser.showOpenDialog(gp.getScene().getWindow());

        try {
            CanvasSave canvasSave = gson.fromJson(new FileReader(file), CanvasSave.class);
            lblInfo.setText("Opened " + canvasSave.getFilename());

            ArrayList<String> colors = canvasSave.getCanvas();
            int i = 0;

            // Clear Canvas
            newCanvas(canvasSave.getUnits(), canvasSave.getPixel(), canvasSave.getScale());
            for(int h = 0; h < units; h ++)
            {
                for (int w = 0; w < units; w++)
                {
                    layer.setFill(Color.valueOf(colors.get(i)));
                    layer.fillRect(w*brushSize, h*brushSize, brushSize, brushSize);
                    i++;
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Failed Read!");
        }
    }

    @FXML
    private void saveImage()
    {
        double sc = scale;
        setScale(1.0);

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        canvas.snapshot(sp, wim);

        FileChooser.ExtensionFilter filterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.setInitialFileName("PixelArt.png");
        fileChooser.getExtensionFilters().add(filterPNG);
        fileChooser.setSelectedExtensionFilter(filterPNG);
        File file = fileChooser.showSaveDialog(gp.getScene().getWindow());

        try
        {
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
            lblInfo.setTextFill(Color.GREEN);
            lblInfo.setText(file.getName() + " saved!");
        }
        catch (Exception e)
        {
            lblInfo.setTextFill(Color.RED);
            lblInfo.setText("Saving Error!");
        }

        setScale(sc);
    }

    @FXML
    private void changeColor()
    {
       color = colorPicker.getValue();
    }

    @FXML
    private void clearBool()
    {
        erase = !erase;

        if(erase)
            lblMode.setText("Mode: Erase");
        else
            lblMode.setText("Mode: Draw");
    }

    @FXML
    public void newFile()
    {
        newCanvas(units, canvasSize, scale);
    }



    @FXML
    private void exit()
    {
        // TODO Check if saved
        System.exit(0);
    }
}
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Bastian Jarzombek on 14/11/2017.
 */
public class Controller implements Initializable
{
    private GraphicsContext layer,layerGrid, layerHover;
    private WritableImage wim;
    private Color color;

    private List<Canvas> canvasDict = new ArrayList<>();

    private int brushSize, imagesSize, brushScale;
    private double scale, x, y;



    @FXML
    private Canvas canvas, canvasGrid ,canvasHover;

    @FXML
    private GridPane gp;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Label lblScale, lblInfo;

    @FXML
    private ChoiceBox<String> brush;



    public void initialize(URL location, ResourceBundle resources)
    {
        scale = 1.0;
        brushScale = 1;

        canvasDict.add(canvas);
        canvasDict.add(canvasGrid);
        canvasDict.add(canvasHover);

        brush.getSelectionModel().select(0);

        newCanvas();
        setEvents();
    }

    private void newCanvas()
    {
        imagesSize = (int)canvas.getHeight();
        brushSize = imagesSize / 20;

        color = Color.BLACK;
        colorPicker.setValue(color);

        layer = canvas.getGraphicsContext2D();
        layerGrid = canvasGrid.getGraphicsContext2D();
        layerHover = canvasHover.getGraphicsContext2D();

        wim = new WritableImage(imagesSize, imagesSize);

        canvasHover.toFront();

        drawGrid();
    }

    private void setEvents()
    {
        brush.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> brushScale = (int)newValue  + 1);

                canvasHover.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
                    layerHover.setFill(Color.WHITE);
                    layerHover.clearRect(0, 0, canvasHover.getWidth(), canvasHover.getHeight());

                    x = ((e.getX() - (brushSize / 2) * brushScale) / brushSize);
                    x = ((double) Math.round(x * 1) / 1) * brushSize;

                    y = ((e.getY() - (brushSize / 2) * brushScale) / brushSize);
                    y = ((double) Math.round(y * 1) / 1) * brushSize;

                    layerHover.setFill(color);
                    layerHover.fillRect(x, y, brushSize * brushScale, brushSize * brushScale);
                });

        canvasHover.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            layer.setFill(color);
            layer.fillRect(x, y, brushSize * brushScale, brushSize * brushScale);
        });

        canvasHover.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            layerHover.setFill(Color.WHITE);
            layerHover.clearRect(0, 0, canvasHover.getWidth(), canvasHover.getHeight());

            x = ((e.getX() -(brushSize/2)*brushScale) / brushSize);
            x = ((double)Math.round(x * 1) / 1) * brushSize;

            y = ((e.getY() -(brushSize/2)*brushScale) / brushSize);
            y = ((double)Math.round(y * 1) / 1) * brushSize;

            layerHover.setFill(color);
            layerHover.fillRect(x, y, brushSize * brushScale, brushSize * brushScale);


            x = ((e.getX() -(brushSize/2)*brushScale) / brushSize);
            x = ((double)Math.round(x * 1) / 1) * brushSize;

            y = ((e.getY() -(brushSize/2)*brushScale) / brushSize);
            y = ((double)Math.round(y * 1) / 1) * brushSize;

            layer.setFill(color);
            layer.fillRect(x, y, brushSize * brushScale, brushSize * brushScale);
        });

        canvasHover.addEventHandler(ScrollEvent.SCROLL, e -> {
            if(scale >= 0.5 && scale <= 1.5)
            {
                if(e.getDeltaY() > 3)
                    scale += 0.1;
                else
                    scale -= 0.1;

                setScale(scale);
            }
            else
            {
                if(scale <= 0.5)
                    scale += 0.1;
                else
                    scale -= 0.1;
            }
        });
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
    private void exit()
    {
        // TODO Check if saved
        System.exit(0);
    }

    private void drawGrid()
    {
        double w, h;
        w = canvasGrid.getWidth() / 20;
        h = canvasGrid.getHeight() / 20;

        layerGrid.setFill(Color.LIGHTGRAY);

        double x1, y1 = 0;

        for(int i = 0; i < w; i++)
        {
            x1 = w * i;

            layerGrid.strokeLine(x1, 0, x1, canvasGrid.getHeight());
        }

        for(int i = 0; i < w; i++)
        {
            y1 = h * i;

            layerGrid.strokeLine(0, y1, canvasGrid.getWidth(), y1);
        }
    }

}
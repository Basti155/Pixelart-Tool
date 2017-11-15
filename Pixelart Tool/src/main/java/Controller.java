import javafx.embed.swing.SwingFXUtils;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bastian Jarzombek on 14/11/2017.
 */
public class Controller implements Initializable
{
    private GraphicsContext layer,layerGrid, layerHover;
    private WritableImage wim;



    @FXML
    private Canvas canvas, canvasGrid ,canvasHover;

    @FXML
    private GridPane gp;

    private double x, y;

    public void initialize(URL location, ResourceBundle resources)
    {
        layer = canvas.getGraphicsContext2D();
        layerGrid = canvasGrid.getGraphicsContext2D();
        layerHover = canvasHover.getGraphicsContext2D();

        wim = new WritableImage(1000, 1000);


        canvasHover.toFront();

        drawGrid();



        canvasHover.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                layerHover.setFill(Color.WHITE);
                layerHover.clearRect(0, 0, canvasHover.getWidth(), canvasHover.getHeight());

                x = ((e.getX() -25) / 50);
                x = ((double)Math.round(x * 1) / 1) * 50;

                y = ((e.getY() -25) / 50);
                y = ((double)Math.round(y * 1) / 1) * 50;

                layerHover.setFill(Color.GREEN);
                layerHover.fillRect(x, y, 50, 50);
            }
        });

        canvasHover.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                layer.setFill(Color.GREEN);
                layer.fillRect(x, y, 50, 50);
            }
        });

        canvasHover.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                x = ((e.getX() -25) / 50);
                x = ((double)Math.round(x * 1) / 1) * 50;

                y = ((e.getY() -25) / 50);
                y = ((double)Math.round(y * 1) / 1) * 50;

                System.out.println("Draw at: " + x + " | " + y);

                layer.setFill(Color.GREEN);
                layer.fillRect(x, y, 50, 50);
            }
        });
    }

    @FXML
    private void saveImage()
    {
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        canvas.snapshot(sp, wim);

        FileChooser.ExtensionFilter filterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(filterPNG);
        fileChooser.setSelectedExtensionFilter(filterPNG);
        File file = fileChooser.showSaveDialog(gp.getScene().getWindow());

        try
        {
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
            System.out.println("Images saved at " + file.getAbsolutePath() + "!");
        }
        catch (Exception e)
        {
           // TODO ??
        }
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
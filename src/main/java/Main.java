import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Bastian Jarzombek on 14/11/2017.
 * Project Pixelart Tool
 */
public class Main extends Application {
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        primaryStage.setTitle("Pixelart Tool");
        Scene scene = new Scene(root, 750, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

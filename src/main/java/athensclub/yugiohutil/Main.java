package athensclub.yugiohutil;

import athensclub.yugiohutil.data.CardsData;
import athensclub.yugiohutil.ui.controller.YdkToPdfController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;

public class Main extends Application {

    public static final String WORKING_DIRECTORY = new File("").getAbsolutePath();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        BorderPane pane = loader.load(Objects.requireNonNull(getClass().getModule().getResourceAsStream("fxml/ydk_to_pdf.fxml")));
        YdkToPdfController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        Scene scene = new Scene(pane);

        primaryStage.setTitle("Yugioh Utility Tools - By Athensclub");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

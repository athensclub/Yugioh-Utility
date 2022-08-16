package athensclub.yugiohutil.ui.controller;

import athensclub.yugiohutil.PDFUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YdkToPdfController {

    @FXML
    private Button selectSourceButton;

    @FXML
    private Button selectDestButton;

    @FXML
    private Button generatePdfButton;

    @FXML
    private Text sourceFile;

    @FXML
    private Text destFile;

    @FXML
    private TextField cardWidthText;

    @FXML
    private TextField cardHeightText;

    private FileChooser fileChooser;

    private FileChooser.ExtensionFilter ydkFilter;

    private FileChooser.ExtensionFilter pdfFilter;

    private Alert errorAlert;

    private Alert infoAlert;

    private ObjectProperty<File> sourceFileProperty;
    private ObjectProperty<File> destFileProperty;

    private Stage primaryStage;

    public void initialize() {
        errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");

        infoAlert = new Alert(Alert.AlertType.INFORMATION);

        sourceFileProperty = new SimpleObjectProperty<>();
        sourceFileProperty.set(null);

        destFileProperty = new SimpleObjectProperty<>();
        destFileProperty.set(null);

        fileChooser = new FileChooser();
        ydkFilter = new FileChooser.ExtensionFilter("Yugioh deck file (.ydk)", "*.ydk");
        pdfFilter = new FileChooser.ExtensionFilter("PDF file (.pdf)", "*.pdf");

        sourceFile.textProperty().bind(Bindings.when(sourceFileProperty.isNull())
                .then("Not Selected")
                .otherwise(sourceFileProperty.asString()));
        destFile.textProperty().bind(Bindings.when(destFileProperty.isNull())
                .then("Not Selected")
                .otherwise(destFileProperty.asString()));
    }

    @FXML
    public void onSelectSourceClicked(ActionEvent e) {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(ydkFilter);
        File source = fileChooser.showOpenDialog(primaryStage);
        if (source == null)
            return;

        sourceFileProperty.set(source);
    }

    @FXML
    public void onSelectDestClicked(ActionEvent e) {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(pdfFilter);
        File source = fileChooser.showSaveDialog(primaryStage);
        if (source == null)
            return;

        destFileProperty.set(source);
    }

    @FXML
    public void onGeneratePdfClicked(ActionEvent e) {
        if (sourceFileProperty.get() == null) {
            errorAlert.setContentText("Please select source file first!");
            errorAlert.showAndWait();
            return;
        }

        if (destFileProperty.get() == null) {
            errorAlert.setContentText("Please select target file first!");
            errorAlert.showAndWait();
            return;
        }

        double cardWidth;
        try {
            cardWidth = Double.parseDouble(cardWidthText.getText());
            if (cardWidth <= 0) {
                errorAlert.setContentText("Card width must be a positive number!");
                errorAlert.showAndWait();
                return;
            }
        } catch (NumberFormatException ex) {
            errorAlert.setContentText("Card width must be a valid number!");
            errorAlert.showAndWait();
            return;
        }

        double cardHeight;
        try {
            cardHeight = Double.parseDouble(cardHeightText.getText());
            if (cardHeight <= 0) {
                errorAlert.setContentText("Card height must be a positive number!");
                errorAlert.showAndWait();
                return;
            }
        } catch (NumberFormatException ex) {
            errorAlert.setContentText("Card height must be a valid number!");
            errorAlert.showAndWait();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(sourceFileProperty.get()))) {
            List<String> cards = new ArrayList<>();
            String temp;
            while ((temp = br.readLine()) != null)
                if (Character.isDigit(temp.charAt(0)))
                    cards.add(temp);

            infoAlert.setContentText("Generating pdf file, Please wait");
            infoAlert.show();
            selectSourceButton.setDisable(true);
            selectDestButton.setDisable(true);
            generatePdfButton.setDisable(true);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println("" + cards.size() + " cards");
                    System.out.println(cards);
                    PDDocument doc = PDFUtils.createPDDocument(cards, cardWidth, cardHeight);
                    File dest = destFileProperty.get();
                    if (!dest.exists())
                        dest.createNewFile();
                    System.out.println("Saving");
                    doc.save(dest);
                    doc.close();
                    System.out.println("Saving Complete");
                    return null;
                }
            };

            task.setOnSucceeded(ev -> {
                infoAlert.close();
                infoAlert.setContentText("Pdf file created and saved!");
                infoAlert.show();
                selectSourceButton.setDisable(false);
                selectDestButton.setDisable(false);
                generatePdfButton.setDisable(false);
            });

            Thread thread = new Thread(task);
            thread.start();
        } catch (IOException ex) {
            errorAlert.setContentText("An error occurred while reading/writing file");
            errorAlert.showAndWait();
            return;
        }

    }

    /**
     * Assign the JavaFX primary stage to this controller.
     *
     * @param stage The JavaFX primary stage.
     */
    public void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

}

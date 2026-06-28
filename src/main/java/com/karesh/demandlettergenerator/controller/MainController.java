package com.karesh.demandlettergenerator.controller;

import com.karesh.demandlettergenerator.service.GenerationService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.awt.Desktop;
import java.io.IOException;
import java.util.Optional;

public class MainController {

    @FXML
    private TextField fileField;

    @FXML
    private Button generateButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Label customerLabel;


    private File selectedFile;

    private Path latestBatch;

    @FXML
    private void browseFile() {

        FileChooser chooser = new FileChooser();

        chooser.setTitle("Select Excel File");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Excel Files",
                        "*.xlsx"));

        selectedFile = chooser.showOpenDialog(null);

        if (selectedFile == null) {

            return;

        }

        String name =
                selectedFile.getName().toLowerCase();

        if (!name.endsWith(".xlsx")) {

            new Alert(

                    Alert.AlertType.ERROR,

                    "Please select a valid Excel (.xlsx) file."

            ).showAndWait();



            fileField.clear();
            selectedFile = null;

            generateButton.setDisable(true);

            return;

        }

        fileField.setText(
                selectedFile.getAbsolutePath());

        generateButton.setDisable(false);

    }

    @FXML
    private void generateLetters() {

        if (selectedFile == null) {

            new Alert(
                    Alert.AlertType.WARNING,
                    "Please select an Excel file.")
                    .showAndWait();

            return;
        }

        generateButton.setDisable(true);

        GenerationService task =
                new GenerationService(
                        selectedFile.toPath());


        statusLabel.textProperty()
                .bind(task.messageProperty());

        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(event -> {

            progressBar.progressProperty().unbind();

            statusLabel.textProperty().unbind();

            customerLabel.textProperty().unbind();


            latestBatch = task.getValue();

            generateButton.setDisable(false);

            statusLabel.setText("Finished");

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Generation Complete");

            alert.setHeaderText("Demand Letters Generated Successfully");

            alert.setContentText(
                    "The demand letters have been generated successfully.\n\n"
                            + "Output Folder:\n\n"
                            + latestBatch.toAbsolutePath()
            );

            ButtonType openButton =
                    new ButtonType("Open Batch Folder");

            ButtonType closeButton =
                    ButtonType.CLOSE;

            alert.getButtonTypes().setAll(
                    openButton,
                    closeButton);

            Optional<ButtonType> result =
                    alert.showAndWait();

            if (result.isPresent()
                    && result.get() == openButton) {

                try {

                    Desktop.getDesktop().open(
                            latestBatch.toFile());

                } catch (IOException e) {

                    Alert error = new Alert(Alert.AlertType.ERROR);

                    error.setTitle("Unable to Open Folder");

                    error.setHeaderText(null);

                    error.setContentText(
                            "Could not open the generated folder.");

                    error.showAndWait();

                    e.printStackTrace();

                }

            }

        });

        task.setOnFailed(event -> {

            progressBar.progressProperty().unbind();

            statusLabel.textProperty().unbind();

            customerLabel.textProperty().unbind();


            generateButton.setDisable(false);

            // Reset UI
            fileField.clear();
            selectedFile = null;
            generateButton.setDisable(true);

            task.getException().printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Generation Failed");

            alert.setHeaderText("An error occurred");

            alert.setContentText(
                    "An error occurred while generating the demand letters.\n\n"
                            + task.getException().getMessage());

            alert.showAndWait();

        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);

        thread.start();

    }

    @FXML
    private void openLatestBatch() {

        if (latestBatch == null) {

            new Alert(
                    Alert.AlertType.INFORMATION,
                    "No batch has been generated during this session.")
                    .showAndWait();

            return;

        }

        try {

            Desktop.getDesktop().open(
                    latestBatch.toFile());

        }

        catch (IOException e) {

            e.printStackTrace();

        }

    }

    @FXML
    private void openGeneratedFolder() {

        try {

            Path generated =
                    Path.of("generated");

            if (!generated.toFile().exists()) {

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "No generated folder exists yet.")
                        .showAndWait();

                return;

            }

            Desktop.getDesktop().open(
                    generated.toFile());

        }

        catch (IOException e) {

            e.printStackTrace();

        }

    }

}
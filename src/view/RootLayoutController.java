package view;


import javafx.scene.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;

import application.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import util.AlgorithmR;


/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 * 
 * @author Marco Jakob
 */
public class RootLayoutController {

    // Reference to the main application
    private MainApp mainApp;

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Opens a FileChooser to let the user select an address book to load.
     */
//    @FXML
//    private void handleOpen() {
//        FileChooser fileChooser = new FileChooser();
//
//        // Set extension filter
//        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
//                "MIDI or DNA files", "*.mid", "*.dna");
//        fileChooser.getExtensionFilters().add(extFilter);
//
//        // Show save file dialog
//        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
//
//        if (file != null) {
//            mainApp.loadPersonDataFromFile(file);
//        }
//    }

    /**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
//    @FXML
//    private void handleSave() {
//        File personFile = mainApp.getPersonFilePath();
//        if (personFile != null) {
//            mainApp.savePersonDataToFile(personFile);
//        } else {
//            handleSaveAs();
//        }
//    }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
//    @FXML
//    private void handleSaveAs() {
//        FileChooser fileChooser = new FileChooser();
//
//        // Set extension filter
//        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
//                "CSV files (*.csv)", "*.csv");
//        fileChooser.getExtensionFilters().add(extFilter);
//
//        // Show save file dialog
//        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
//
//        if (file != null) {
//            // Make sure it has the correct extension
//            if (!file.getPath().endsWith(".csv")) {
//                file = new File(file.getPath() + ".csv");
//            }
//           // mainApp.savePersonDataToFile(file);
//            AlgorithmR.saveCsv(mainApp.getRe(),file.getName());
//        }
//    }

    /**
     * Opens an about dialog.
     */
    
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Pattern Recognition");             
        alert.getDialogPane().setGraphic(new ImageView("file:resources/images/iconfinder1.png"));
        alert.setHeaderText("Pattern Recognition version 1.0 \nAuthors: Giovanni Santinelli, Luca Pretini");
        FlowPane fp = new FlowPane();
        Label lbl = new Label("For more tecnical information read the guide:");
        Hyperlink link = new Hyperlink("Guide");
        fp.getChildren().addAll( lbl, link);
        link.setOnAction( (evt) -> {
                    alert.close();
                    openReadMe();
        } );
        alert.getDialogPane().contentProperty().set( fp );
        //alert.setContentText("Author: Giovanni Santinelli, Luca Pretini\nWebsite: http://www.test.com" + link);
        alert.showAndWait();
    }
    

    private void openReadMe() {
    	if (Desktop.isDesktopSupported()) {
    		File readMe= new File(AlgorithmR.ReplaceSlash(System.getProperty("user.dir") + "/readMe.txt"));
    	    try {
				Desktop.getDesktop().edit(readMe);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    	    // dunno, up to you to handle this
    	}
		
	}

	/**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
    	mainApp.getRe().end();
        System.exit(0);

    }
    
    /**
     * Opens the birthday statistics.
     */
    @FXML
    private void handleLoadStatistics() {
    	if (mainApp.isLoading(mainApp)) return;
    	FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				".Csv" + " files", "*" + ".csv");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		mainApp.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
		MenuController.clearWindowPlot("windowGraph1.jpg");
		MenuController.clearWindowPlot("windowGraph2.jpg");
		MenuController.clearWindowPlot("windowGraph3.jpg");
		MenuController.clearWindowPlot("windowGraph4.jpg");
		MenuController.clearWindowPlot("windowGraph5.jpg");
		if(AlgorithmR.loadResults(mainApp.getRe(), file.getAbsolutePath())) {
			mainApp.showLoadedStatistics(file.getAbsolutePath());
			mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Wrong File");
			alert.setHeaderText("There was an error loading the file!");
			alert.setContentText("Please select another .Csv file to load!");
			alert.showAndWait();
		}
    }
}
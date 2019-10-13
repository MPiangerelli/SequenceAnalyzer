package application;

import java.io.File;
import java.io.IOException;
import org.rosuda.JRI.Rengine;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.FileExt;
import model.FileType;
import util.TextConsole;
import view.MenuController;
import view.PlotResController;
import view.PlotWindowResController;
import view.RootLayoutController;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    /**
     * The data as an observable list of Files.
     */
    private ObservableList<FileType> fileData = FXCollections.observableArrayList();
    private Rengine re = new Rengine(new String[0], false, new TextConsole());

	/**
     * Constructor
     */
    public MainApp() {
        String[] extMidi = {".mid"};
        fileData.add(new FileType("File Midi",FileExt.Midi,extMidi));
        String[] extDna = {".fna"};
        fileData.add(new FileType("File Dna",FileExt.Fasta,extDna));
        String[] extCsv = {".csv"};
        fileData.add(new FileType("File Csv",FileExt.Csv,extCsv));
    }
  
    public ObservableList<FileType> getFileData() {
        return fileData;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Pattern Recognition");
        
        // Set the application icon.
        this.primaryStage.getIcons().add(new Image("file:resources/images/iconfinder1.png"));
        this.primaryStage.setOnCloseRequest(event -> { re.end();});
        
        initRootLayout();
        
        showMenuOverview();

    }
    
    /**
     * Initializes the root layout and tries to load the last opened
     * person file.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("/view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Shows the person overview inside the root layout.
     */
    public void showMenuOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/Menu.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();
            
            // Set file overview into the center of root layout.
            rootLayout.setCenter(personOverview);

            // Give the controller access to the main app.
            MenuController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public boolean isLoading(MainApp mainApp) {
    	return (mainApp.getPrimaryStage().getScene().getCursor() == Cursor.WAIT);
    }
    
//carica il file, esegue l'elaborazione, pubblica il grafico/risultato
    public void loadDataFromFile(File file) {
    	
    }
 
    /**
     * Opens a dialog to show birthday statistics.
     */
    public void showStatistics(FileType file) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/PlotRes.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            // Set the file into the controller.
            PlotResController controller = loader.getController();
            controller.setMainApp(this);
            controller.setValue(file);
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data! 2");
            alert.showAndWait();
        }    	

    }
    
    
    public void showWindowStatistics(FileType file) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/PlotResWindow.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Window Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            // Set the file into the controller.
            PlotWindowResController controller = loader.getController();
            controller.setMainApp(this);
            controller.setValue(file);
            controller.setPlots();
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data! 2");
            alert.showAndWait();
        }    	
    }
    
    public void showLoadedStatistics(String path) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/PlotResWindow.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Loaded Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            // Set the file into the controller.
            PlotWindowResController controller = loader.getController();
            controller.setMainApp(this);
            controller.setPlots();
            controller.setFileExamined(path);
            controller.setNaValues("Not present");
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data! 2");
            alert.showAndWait();
        }    	
    }
    
    public Rengine getRe() {
    	return this.re;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

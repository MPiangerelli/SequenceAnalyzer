package view;

import java.io.File;

import application.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.FileType;

public class PlotWindowResController {

    @FXML
    private ImageView shannEntropy = new ImageView();
    @FXML
    private ImageView fractaldim = new ImageView();
    @FXML
    private ImageView sampleEntropy = new ImageView();
    @FXML
    private ImageView approxEntropy = new ImageView();
    @FXML
    private ImageView hurst = new ImageView();
    @FXML
    private Label FileExamined;
    @FXML
    private Label windowDim;
    @FXML
    private Label overlapping;
    @FXML
    private Label numOfLags;
    @FXML
    private Label boxSize;
    @FXML
    private Label entropyMethod;
    @FXML
    private Label numOfNeighbours;
	
    private MainApp mainApp;
    
    @FXML
    private void initialize() {
    	
    }
    
    public void setMainApp(MainApp mainApp) {
    	this.mainApp = mainApp;
    }
    
    public void setValue(FileType file) {
    	this.FileExamined.setText(file.getPath());
		this.overlapping.setText(file.getOverlapping());
		this.windowDim.setText(file.getWindowDim());
		this.entropyMethod.setText(file.getEntropyMethodSelected());
		this.numOfLags.setText(file.getnLagsSelected());
		this.numOfNeighbours.setText(file.getNumNeighbourSelected());
		this.boxSize.setText(file.getBoxSizeSelected());
    }
    
    public void setPlots() {
    	loadImage(fractaldim,"windowGraph1.jpg");
		loadImage(shannEntropy,"windowGraph2.jpg");
		loadImage(hurst,"windowGraph3.jpg");
		loadImage(sampleEntropy,"windowGraph4.jpg");
		loadImage(approxEntropy,"windowGraph5.jpg");
    }
    
    public void setFileExamined(String path) {
    	this.FileExamined.setText(path);
    }
    
    private void loadImage(ImageView iw, String name) {
    	File file = new File("resources/plots/" + name);
		Image image = new Image(file.toURI().toString());
		iw.setImage(image);
    }

	public void setNaValues(String na) {
		this.overlapping.setText(na);
		this.windowDim.setText(na);
		this.entropyMethod.setText(na);
		this.numOfLags.setText(na);
		this.numOfNeighbours.setText(na);
		this.boxSize.setText(na);
	}
    
}

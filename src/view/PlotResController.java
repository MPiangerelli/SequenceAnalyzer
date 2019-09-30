package view;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import application.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import model.FileType;
import util.AlgorithmR;

public class PlotResController {

	    @FXML
	    private Label FileExamined;
	    @FXML
	    private Label Entropy;
	    @FXML
	    private Label approximateEntropy;
	    @FXML
	    private Label sampleEntropy;
	    @FXML
	    private Label Fractaldim;
	    @FXML
	    private Label Hurst;
	    @FXML
	    private ImageView rgraph = new ImageView();
	    @FXML
	    private Label nLagsSelected;
	    @FXML
	    private Label boxSizeSelected;
	    @FXML
	    private Label numNeighbourSelected;
	    @FXML
	    private Label entropyMethodSelected;
	    @FXML
	    private Label partitionFromSelected;
	    @FXML
	    private Label partitionToSelected;
	    @FXML
	    private WebView nicePlot;
	    
	    private MainApp mainApp;
	    
	    @FXML
	    private void initialize() {
	    	
	    }
	    
	    public void setMainApp(MainApp mainApp) {
	    	this.mainApp = mainApp;
	    }
	    
	   
		public void setValue(FileType file){
	    	this.Entropy.setText(Double.toString(file.getEntropy()));
	    	this.Hurst.setText(Double.toString(file.getHurst()));
	    	this.Fractaldim.setText(Double.toString(file.getFractaldim()));
	    	this.rgraph.setImage(file.getImage());
	    	this.FileExamined.setText(file.getPath());	  
	    	this.boxSizeSelected.setText(file.getBoxSizeSelected());
	    	this.entropyMethodSelected.setText(file.getEntropyMethodSelected());
	    	this.nLagsSelected.setText(file.getnLagsSelected());
	    	this.numNeighbourSelected.setText(file.getNumNeighbourSelected());
	    	this.approximateEntropy.setText(Double.toString(file.getApproximateEntropy()));
	    	this.sampleEntropy.setText(Double.toString(file.getSampleEntropy()));
	    	this.partitionFromSelected.setText(Integer.toString(file.getPartitionFrom()));
	    	this.partitionToSelected.setText(Integer.toString(file.getPartitionTo()));
	    	//getResources parte da dentro src/
	    	try {
				TimeUnit.SECONDS.sleep(3);
				//this.nicePlot.getEngine().load(getClass().getResource("/interPlot.html").toExternalForm());
				File prova = new File(AlgorithmR.ReplaceSlash(System.getProperty("user.dir")) + "/src/interPlot.html");
				this.nicePlot.getEngine().load(prova.toURL().toExternalForm());
			} catch (InterruptedException | MalformedURLException e) {
				e.printStackTrace();
			}

	    }

		public WebView getNicePlot() {
			return nicePlot;
		}

		public void setNicePlot(WebView nicePlot) {
			this.nicePlot = nicePlot;
		}
	    
}

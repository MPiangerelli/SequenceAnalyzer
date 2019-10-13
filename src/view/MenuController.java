package view;

import java.io.File;
import java.util.Optional;
import java.util.function.UnaryOperator;
import application.MainApp;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.Image;
import model.FileType;
import util.AlgorithmR;

public class MenuController {
	@FXML    
	private TableView<FileType> fileTable;
	@FXML	
	private TableColumn<FileType, String> TypeColumn;
	@FXML
	private TableColumn<FileType, String> ExtColumn;

	@FXML
	private Label NameLabel;
	@FXML
	private Label ExtPrefLabel;
	@FXML
	private Label ExtSupportedLabel;
	@FXML
	private Label selectedFileLabel;
	@FXML
	private Label maxNotesLabel;
	@FXML
	private Label csvInformation;
	@FXML 
	private ComboBox<String> analysisCombo;
	@FXML
	private TextField nLags;
	@FXML
	private TextField boxSize;
	@FXML
	private TextField numNeighbour;
	@FXML
	private TextField windowDim;
	@FXML
	private TextField overlapping;
	@FXML
	private TextField numOfNodes;
	@FXML
	private TextField csvColumn;
	@FXML
	private CheckBox approxEntropy;
	@FXML
	private CheckBox sampleEntropy;
	@FXML
	private CheckBox saveAsCsv;
	@FXML
	private CheckBox directedGraph;
	@FXML
	private CheckBox showIsolatedNodes;
	@FXML
	private CheckBox analyzeRow;
	@FXML
	private TextField fileName;

	// Reference to the main application.
	private MainApp mainApp;

	public MenuController() {
	}

	@FXML
	private void initialize() {
		TypeColumn.setCellValueFactory(
				cellData -> cellData.getValue().nameProperty());
		ExtColumn.setCellValueFactory(
				cellData -> cellData.getValue().extPrefProperty());

		analysisCombo.getItems().removeAll(analysisCombo.getItems());
		analysisCombo.getItems().addAll("ML", "MM", "Jeffreys","Laplace","SG","minimax","CS","NSB");
		analysisCombo.getSelectionModel().select("ML");         

		// Clear person details.
		showPersonDetails(null);

		// Listen for selection changes and show the file details when changed.
		fileTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showPersonDetails(newValue));
		setIntFormatter(nLags,getNumberFilter());
		setIntFormatter(boxSize,getNumberFilter());
		setIntFormatter(numNeighbour,getNumberFilter());
		setIntFormatter(csvColumn,getNumberFilter());
		windowDim.setVisible(false);
		overlapping.setVisible(false);
		numOfNodes.setVisible(false);
		nLags.clear();
		boxSize.clear();
		numNeighbour.clear();
		csvColumn.clear();
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		fileTable.setItems(mainApp.getFileData());
		fileTable.getSelectionModel().selectFirst();
	}

	/**
	 * Fills all text fields to show details about the person.
	 * If the specified person is null, all text fields are cleared.
	 * 
	 * @param person the person or null
	 */ 
	private void showPersonDetails(FileType fileType) {
		if (fileType != null) {
			// Fill the labels with info from the file object.
			NameLabel.setText(fileType.getName());
			ExtPrefLabel.setText(fileType.getExtPref());
			ExtSupportedLabel.setText(fileType.getExtSupported());
			selectedFileLabel.setText(fileType.getPath());
			maxNotesLabel.setText(String.valueOf(fileType.getMaxElements()));
			if(fileType.getExtPref()=="Csv") {
				csvInformation.setVisible(true);
				analyzeRow.setVisible(true);
				csvColumn.setVisible(true);
			} else {
				csvInformation.setVisible(false);
				analyzeRow.setVisible(false);
				csvColumn.setVisible(false);
			}
		} else {
			// File is null, remove all the text.
			NameLabel.setText("");
			ExtPrefLabel.setText("");
			ExtSupportedLabel.setText("");
			selectedFileLabel.setText(""); 
			maxNotesLabel.setText("");
		}
	}

	private UnaryOperator<Change> getNumberFilter(){
		UnaryOperator<Change> integerFilter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches("([1-9][0-9]*)?")) {  //old: "-?([1-9][0-9]*)?"
				return change;
			}
			return null;
		};
		return integerFilter;
	}
	
	private UnaryOperator<Change> getNumberFilterWith0(){
		UnaryOperator<Change> integerFilter = change -> {
			String newText = change.getControlNewText();
			if (newText.matches("([0-9][0-9]*)?")) {  //old: "-?([1-9][0-9]*)?"
				return change;
			}
			return null;
		};
		return integerFilter;
	}

	private UnaryOperator<Change> getFromToFilter(int Max){
		UnaryOperator<Change> integerFilter = change -> {
			String newText = change.getControlNewText();
			String fullText = change.getControlText();
			String fullText2 = change.getText();
			if (newText.matches("([1-9][0-9]*)?")) {  
				if ((fullText+fullText2).length()>1) {
					if (Integer.parseInt(fullText+fullText2) < Max){
						return change;
					}
				} else return change;
			}
			return null;
		};
		return integerFilter;
	}


	private void setIntFormatter(TextField txt, UnaryOperator<Change> filter) {
		txt.setTextFormatter(
				new TextFormatter<Integer>(new IntegerStringConverter(), 0, filter));
	}

	public Optional<String> getExtensionByStringHandling(String filename) {
		return Optional.ofNullable(filename)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}

	@FXML
	private void handleNewPerson() {
		if (mainApp.isLoading(mainApp)) return;
		String selectedFilePath = fileTable.getSelectionModel().getSelectedItem().getPath();
		if (selectedFilePath != null ) {
			try {
				mainApp.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
				if (overlapping.getText().isEmpty()) {overlapping.setText("0");};
				String[] params =  { nLags.getText(),analysisCombo.getSelectionModel().getSelectedItem().toString(), boxSize.getText(), numNeighbour.getText(), windowDim.getText(),overlapping.getText(),fileName.getText(),numOfNodes.getText(),csvColumn.getText()};
				boolean[] opt = {approxEntropy.isSelected(),sampleEntropy.isSelected(),saveAsCsv.isSelected(),directedGraph.isSelected(),showIsolatedNodes.isSelected(),analyzeRow.isSelected()};
				clearWindowPlot("windowGraph1.jpg");
				clearWindowPlot("windowGraph2.jpg");
				clearWindowPlot("windowGraph3.jpg");
				clearWindowPlot("windowGraph4.jpg");
				clearWindowPlot("windowGraph5.jpg");
//				if(AlgorithmR.ReplaceSlash(selectedFileLabel.getText()) == "Csv" && csvColumn.getText().isEmpty()) {
//					Alert alert = new Alert(AlertType.WARNING);
//					alert.initOwner(mainApp.getPrimaryStage());
//					alert.setTitle("No Selection");
//					alert.setHeaderText("No Column Selected");
//					alert.setContentText("Please select a column to read!");
//					alert.showAndWait();
//				}
				double[] tmp = AlgorithmR.doAlgorithm(mainApp.getRe(), AlgorithmR.ReplaceSlash(selectedFileLabel.getText()), params,fileTable.getSelectionModel().getSelectedItem().getExtPref(),opt);
				fileTable.getSelectionModel().getSelectedItem().setFractaldim(tmp[0]);
				fileTable.getSelectionModel().getSelectedItem().setEntropy(tmp[1]);
				fileTable.getSelectionModel().getSelectedItem().setHurst(tmp[2]);
				fileTable.getSelectionModel().getSelectedItem().setApproximateEntropy(tmp[3]);
				fileTable.getSelectionModel().getSelectedItem().setSampleEntropy(tmp[4]);
				fileTable.getSelectionModel().getSelectedItem().setWindowDim(windowDim.getText());
				fileTable.getSelectionModel().getSelectedItem().setOverlapping(overlapping.getText());
				File file = new File("resources/plots/rimage.jpg");
				Image image = new Image(file.toURI().toString());
				fileTable.getSelectionModel().getSelectedItem().setImage(image);
				fileTable.getSelectionModel().getSelectedItem().setNicePlot("/interPlot/interPlot.html");
				fileTable.getSelectionModel().getSelectedItem().setSelections(params);
				
				if(!windowDim.getText().isEmpty() && (Integer.parseInt(windowDim.getText()) > Integer.parseInt(overlapping.getText()))) {
					mainApp.showWindowStatistics(fileTable.getSelectionModel().getSelectedItem());
				} else mainApp.showStatistics(fileTable.getSelectionModel().getSelectedItem());
				mainApp.getRe().end();
				fileTable.getSelectionModel().getSelectedItem().clear();
				mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No File Selected");
			alert.setContentText("Please select a file.");
			alert.showAndWait();
		}
	}

	/**
	 * Opens a FileChooser to let the user select a file to load.
	 */
	@FXML
	private void handleSelectFile() {
		if (mainApp.isLoading(mainApp)) return;
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = getFileChooser(fileTable.getSelectionModel().getSelectedItem());
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		if (file != null) {
			mainApp.getFileData().get(fileTable.getSelectionModel().getSelectedIndex()).setPath(file.getAbsolutePath());
			mainApp.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
			mainApp.getRe().eval("libpath <- paste(getwd(),\"/R/win-library/3.6\",sep=\"\")");
			mainApp.getRe().eval(".libPaths(libpath)");
			int elements = 0;
			switch (mainApp.getFileData().get(fileTable.getSelectionModel().getSelectedIndex()).getExtPref()) {
			case("Midi"):
				elements = AlgorithmR.HowManyNotes(mainApp.getRe(),file.getAbsolutePath());
				break;
			case("Fasta"):
				elements = AlgorithmR.HowManyFasta(mainApp.getRe(),file.getAbsolutePath());
				break;
			case("Csv"):
				if(csvColumn.getText().isEmpty()) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.initOwner(mainApp.getPrimaryStage());
					alert.setHeight(400);
					alert.setTitle("No Selection");
					alert.setHeaderText("No Column Selected");
					alert.setContentText("Please select the index of the column to analyze before choosing a file!");
					alert.showAndWait();
					mainApp.getFileData().get(fileTable.getSelectionModel().getSelectedIndex()).setPath(null);
				} else {
					elements = AlgorithmR.HowManyCsv(mainApp.getRe(),file.getAbsolutePath(),csvColumn.getText(),analyzeRow.isSelected());
					break;
				}
			 }
			if (elements>1) {
			mainApp.getFileData().get(fileTable.getSelectionModel().getSelectedIndex()).setMaxElements(elements);
			showPersonDetails(mainApp.getFileData().get(fileTable.getSelectionModel().getSelectedIndex()));
			windowDim.setVisible(true);
			overlapping.setVisible(true);
			numOfNodes.setVisible(true);
			setIntFormatter(windowDim,getFromToFilter(Integer.parseInt(maxNotesLabel.getText())));
			setIntFormatter(overlapping,getNumberFilterWith0());
			setIntFormatter(numOfNodes,getFromToFilter(Integer.parseInt(maxNotesLabel.getText())));
			windowDim.clear();
			overlapping.clear();
			numOfNodes.clear();
			} else if (!csvColumn.getText().isEmpty()){
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setHeight(400);
				alert.setTitle("Bad Selection");
				alert.setHeaderText("Wrong Column/Row Selected");
				alert.setContentText("The index you chose was wrong! Please select another index!");
				alert.showAndWait();
			}
			mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
			
		}
	}

	private FileChooser.ExtensionFilter getFileChooser(FileType file){
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				file.getExtPref() + " files", "*" + file.getExtSupported());
		return extFilter;
	}


	public String getSelectedFileLabel() {
		return AlgorithmR.ReplaceSlash(selectedFileLabel.getText());
	}
	
	public static void clearWindowPlot(String name) {
		File file = new File("resources/plots/" + name);
		if (file.exists()) {
			file.delete();
		}
	}
}
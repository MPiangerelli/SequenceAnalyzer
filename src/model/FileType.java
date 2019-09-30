package model;


import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class FileType {

	private final StringProperty name;
	private final StringProperty extPref;
	private final StringProperty path;
	private final ObservableList<String> supportedExt = FXCollections.observableList(new ArrayList<String>());
	private Image rimage ;
	private double entropy;
	private double approximateEntropy;
	private double sampleEntropy;
	private double fractaldim;
	private double hurst;
	private String entropyMethodSelected = "Entropy method = ML";
	private String boxSizeSelected = "Box size used = 50";
	private String numNeighbourSelected = "Number of neighbours used = 15";
	private String nLagsSelected = "Number of lags used = 20";
	private int maxElements;
	private int partitionFrom;
	private int partitionTo;
	private String nicePlot;
	
	
	public FileType(String name, FileExt extPref, String[] ext) {
		this.name = new SimpleStringProperty(name);
		this.extPref = new SimpleStringProperty(extPref.toString());
		this.supportedExt.addAll(ext);
		this.path = new SimpleStringProperty();
		this.rimage = null;
		this.entropy = 0;
		this.fractaldim = 0;
		this.hurst = 0;
	}
	
	public FileType(String name, FileExt extPref) {
		this(name,extPref,null);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() {
        return this.name;
    }
	
	public void setSupportedExt(String ext) {
		this.supportedExt.add(ext);
	}
	
	public ObservableList<String> supportedExtProperty() {
		return this.supportedExt;
	}
	
	public String getExtSupported() {
		String res = "";
		for (String ext : this.supportedExt) { res += ext ; }
		return res;
	}
	
	public void setExtSupported(String ext) {
		this.supportedExt.add(ext);
	}
	
	public String getExtPref() {
		return extPref.get();
	}

	public void setExtPref(String extPref) {
		this.extPref.set(extPref);
	}
	
	public StringProperty extPrefProperty() {
        return this.extPref;
    }
	
	public StringProperty pathProperty() {
		return this.path;
	}
	
	public String getPath() {
		return path.get();
	}
	
	public void setPath(String path) {
		this.path.set(path);
	}
	
	public Image getImage() {
		return this.rimage;
	}
	
	public String imageInfo() {
		return this.rimage.getUrl();
	}
	public void setImage(Image img) {
		this.rimage = img;
	}

	public double getEntropy() {
		return entropy;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public double getFractaldim() {
		return fractaldim;
	}

	public void setFractaldim(double fractaldim) {
		this.fractaldim = fractaldim;
	}

	public double getHurst() {
		return hurst;
	}

	public void setHurst(double hurst) {
		this.hurst = hurst;
	}
	
	public void clear() {
		this.entropy = 0;
		this.fractaldim = 0;
		this.hurst = 0;
		this.rimage = null;
		this.nicePlot = null;
	}

	public String getEntropyMethodSelected() {
		return entropyMethodSelected;
	}

	public void setEntropyMethodSelected(String entropyMethodSelected) {
		if (!entropyMethodSelected.isEmpty()) {
		this.entropyMethodSelected = "Entropy method = " + entropyMethodSelected;
		}else this.entropyMethodSelected = "Entropy method = ML";
	}

	public String getBoxSizeSelected() {
		return boxSizeSelected;
	}

	public void setBoxSizeSelected(String boxSizeSelected) {
		if (!boxSizeSelected.isEmpty()) {
		this.boxSizeSelected = "Box size used = " + boxSizeSelected;
		} else this.boxSizeSelected = "Box size used = 50";
	}

	public String getNumNeighbourSelected() {
		return numNeighbourSelected;
	}

	public void setNumNeighbourSelected(String numNeighbourSelected) {
		if (!numNeighbourSelected.isEmpty()) {
		this.numNeighbourSelected = "Number of neighbours used = " + numNeighbourSelected;
		} else this.numNeighbourSelected = "Number of neighbours used = 15";
	}

	public String getnLagsSelected() {
		return nLagsSelected;
	}

	public void setnLagsSelected(String nLagsSelected) {
		if (!nLagsSelected.isEmpty()) {
		this.nLagsSelected = "Number of lags used = " + nLagsSelected;
		} else this.nLagsSelected = "Number of lags used = 20";
	}
	
	//nlags-entropy-box-neigh
	public void setSelections(String[] par) {
		setnLagsSelected(par[0]);
		setEntropyMethodSelected(par[1]);
		setBoxSizeSelected(par[2]);
		setNumNeighbourSelected(par[3]);
		if ((!par[4].isEmpty() && !par[5].isEmpty()) && (Integer.parseInt(par[4]) < Integer.parseInt(par[5]))) {
		setPartitionFrom(par[4]);
		setPartitionTo(par[5]);
		} else {
			setPartitionFrom("");
			setPartitionTo("");
		}
	}

	public double getApproximateEntropy() {
		return approximateEntropy;
	}

	public void setApproximateEntropy(double approximateEntropy) {
		this.approximateEntropy = approximateEntropy;
	}

	public double getSampleEntropy() {
		return sampleEntropy;
	}

	public void setSampleEntropy(double sampleEntropy) {
		this.sampleEntropy = sampleEntropy;
	}

	public int getMaxElements() {
		return maxElements;
	}

	public void setMaxElements(int maxElements) {
		this.maxElements = maxElements;
	}

	public int getPartitionFrom() {
		return partitionFrom;
	}

	public void setPartitionFrom(String partitionFrom) {
		if (!partitionFrom.isEmpty()) {
		this.partitionFrom = Integer.parseInt(partitionFrom);
		} else this.partitionFrom = 0;
	}
		

	public int getPartitionTo() {
		return partitionTo;
	}

	public void setPartitionTo(String partitionTo) {
		if (!partitionTo.isEmpty()) {
		this.partitionTo = Integer.parseInt(partitionTo);
		} else this.partitionTo = 0;
	}

	public String getNicePlot() {
		return nicePlot;
	}

	public void setNicePlot(String nicePlot) {
		this.nicePlot = nicePlot;
	}
	
	
	
}

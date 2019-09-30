package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import org.rosuda.JRI.Rengine;

public class AlgorithmR {

	public static final String pkgName2 = "myRPackage";
	public static final String pkgPath2 = ReplaceSlash(System.getProperty("user.dir")) + "/win-library/3.6/myRPackage";
	


	public static String readInput() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String name = reader.readLine();
		return name;  
	}

	//install a custom Pkg "home-made"
	public static void installRPkg(Rengine re, String pkgName, String pkgPath) {
		re.eval("installPkg <- function(){ if (require(" + pkgName +  ") != TRUE){ "
				+ "install.packages(\"" + pkgPath + "\", repos = NULL, type=\"source\");"
				+ "}" 
				+ "else {require(" + pkgName + ");}"
				+ "}");
		re.eval("installPkg()");
		re.eval("require(" + pkgName + ");");
	}

	//install an official package
	public static void installRPkgOffic(Rengine re, String pkgName) {
		re.eval("installPkg <- function(){ if (require(" + pkgName +  ") != TRUE){ "
				+ "install.packages(\"" + pkgName + "\",repos=\"https://cran.stat.unipd.it/\");"
				+ "}" 
				+ "else {require(" + pkgName + ");}"
				+ "}");	
		re.eval("installPkg()");
		re.eval("require("+ pkgName + ");");
	}		

	public static String ReplaceSlash(String filepath) {
		filepath = filepath.replaceAll("\\\\", "/"); 
		return filepath;
	}

	private static void loadCommonLibrary(Rengine re) {
		installRPkg(re,pkgName2,pkgPath2);
		installRPkgOffic(re, "entropy");
		installRPkgOffic(re, "tuneR");
		installRPkgOffic(re, "fractaldim");
		installRPkgOffic(re, "pracma");
		installRPkgOffic(re, "tseriesChaos");
		installRPkgOffic(re, "plot3D");
		installRPkgOffic(re, "igraph");
		installRPkgOffic(re, "networkD3");
		installRPkgOffic(re, "htmlwidgets");
	}

	public static int HowManyNotes(Rengine re,String path) {
		loadCommonLibrary(re);
		re.eval("tmpIniziale <- componiSpartitoCompleto(\"" + ReplaceSlash(path) + "\")");
		return re.eval("elements <- howManyNotes(tmpIniziale)").asInt();	 
	}

	public static int HowManyFasta(Rengine re,String path) {
		loadCommonLibrary(re);
		installRPkgOffic(re, "seqinr");
		re.eval("tmpIniziale <- fastaTs(\"" + ReplaceSlash(path) + "\")");
		return re.eval("elements <- length(tmpIniziale)").asInt();	 
	}

	public static int HowManyCsv(Rengine re,String path,int n) {
		loadCommonLibrary(re);
		re.eval("tmpIniziale <- csvToTs(\"" + ReplaceSlash(path) + "\"," + n + ")");
		return re.eval("elements <- length(tmpIniziale)").asInt();	 
	}

	private static double[] algorithmMidi(Rengine re, String pathInput, String[] par,boolean[] opt) {
		return baseAlgorithm(re,pathInput,par,opt);
	}

	private static double[] algorithmFasta(Rengine re, String pathInput, String[] par,boolean[] opt) {
		return baseAlgorithm(re,pathInput,par,opt);
	}

	private static double[] algorithmCsv(Rengine re, String pathInput, String[] par,boolean[]opt) {
		return baseAlgorithm(re,pathInput,par,opt);
	}

	private static double[] baseAlgorithm(Rengine re, String pathInput, String[] par,boolean[]opt) {
		if ((!par[4].isEmpty() && !par[5].isEmpty()) && (Integer.parseInt(par[4]) < Integer.parseInt(par[5]))) {
			re.eval("tmp <- fromTo(tmpIniziale," + par[4] + "," + par[5] + ")");
		} else re.eval("tmp <- tmpIniziale");
		re.eval("res <- cal1(tmp," + par[0] + ",\"" + par[1] + "\"," + par[2] +")");
		re.eval("lyap <- lyapMia(tmp," + par[3] + ")");
		double approxEntropy = 0.0;
		re.eval("approximate_entropy <- 0.0");
		if (opt[0]==true) {
			re.eval("approximate_entropy <- approx_entropy(tmp)");
			approxEntropy = re.eval("approximate_entropy").asDouble();
		}
		double sampleEntropy = 0.0;
		re.eval("sample_entropy <- 0.0");
		if (opt[1]==true) {
			re.eval("sample_entropy <- sample_entropy(tmp)");
			sampleEntropy = re.eval("sample_entropy").asDouble();
		}
		double fractaldim = re.eval("res").asList().at(0).asDouble();
		double entropy = re.eval("res").asList().at(1).asDouble();
		double hurst = re.eval("res").asList().at(2).asDouble();
		re.eval("jpeg(\"rimage.jpg\", width=750)");
		re.eval("myPlot2d(lyap)");
		re.eval("dev.off()");
		if (!par[7].isEmpty()) {
			re.eval("graph <- tsToGraph(tmp," + par[7] + ", directed=\"" + String.valueOf(opt[3]) + "\")");
		} else re.eval("graph <- tsToGraph(tmp, directed=\"" + String.valueOf(opt[3]) + "\")");
		if (opt[4] == true) {
			re.eval("addIsolatedNodes(graph)");
		} else re.eval("interactivePlot(graph)");
		if(opt[2]==true) {
			re.eval("dfToSave <- data.frame(res[1],res[2],res[3],approximate_entropy,sample_entropy)");
			if(!par[6].isEmpty()) {
				re.eval("saveAsCsv(dfToSave,\"" + par[6] + "\")");
			} else re.eval("saveAsCsv(dfToSave)");
		}
		return new double[] {fractaldim,entropy,hurst,approxEntropy,sampleEntropy};
	}

	public static double[] doAlgorithm(Rengine re,String pathInput, String[] par,String fileExt,boolean[]opt) throws InterruptedException, ExecutionException {
		switch(fileExt) {
		case "Midi":
			installRPkgOffic(re, "tuneR");
			double[] res = algorithmMidi(re,pathInput,par,opt);
			return res;
		case "Csv":
			return algorithmCsv(re,pathInput,par,opt);
		case "Fasta":
			installRPkgOffic(re, "seqinr");
			return algorithmFasta(re,pathInput,par,opt);
		default:
			return null;
		} 
	}

}

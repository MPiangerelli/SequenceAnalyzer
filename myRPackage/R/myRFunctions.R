readNotes <- function (path)
{
  library("tuneR")
  content <- readMidi(path)
  content <- getMidiNotes(content)
  df <- data.frame(content[2],content[5],content[7])
  df
}

componiSpartitoCompleto<-function(path){
  library("tuneR")
  #library("questionr")
  x <- readMidi(path)
  x <- getMidiNotes(x)
  for (i in c(1:nrow(x))){
    x[i,2] <- x[i,1] + x[i,2]
  }
  start <- unlist(x[1],use.names=FALSE)
  end <- unlist(x[2],use.names=FALSE)
  startEndUnito <- union(start,end)
  startEndUnito <- sort(startEndUnito)
  risultatoFinale <- data.frame(startEndUnito,0)
  for (i in c(1:nrow(risultatoFinale))){
    valore <- risultatoFinale[i,1]
    tutteLeRighe <- x[which((x$length > valore) & (x$time <= valore)),]
    if (nrow(tutteLeRighe) >= 1){
      for (m in (c(1:nrow(tutteLeRighe)))){
        risultatoFinale[i,2] <- risultatoFinale[i,2] + tutteLeRighe[m,5]
      }
    }
  }
  return(ts(unlist(risultatoFinale[,2],use.names="FALSE")))
}

cal1 <- function(ts,nlags="auto", method="ML", d=50){
  library("fractaldim")
  library("entropy")
  library("pracma")
  library("tseriesChaos")
    fractaldim <- fd.estim.boxcount(ts, nlags = nlags)
    entropy <- entropy(ts, method = method)
    hurst <- hurstexp(ts, display=FALSE, d=d)
    df <- data.frame(fractaldim[2],entropy,hurst[2])
    df
}

lyapMia <- function(ts, k=15){
  lyapp <- lyap_k(ts, m = 1, d = 2, s = 200, t = 40, ref = length(ts)/1.5, k = k, eps = 3)
  lyapp <- lyapp[which(lyapp!="-Inf")]
  lyapp
}

myJointEntropy <- function(df){
  disc1 = discretize2d(ts(df[2]), ts(df[1]), numBins1=5, numBins2=5)
  disc2 = discretize2d(ts(df[2]), ts(df[3]), numBins1=5, numBins2=5)
  res <- discretize2d(disc1,disc2,numBins1=5, numBins2=5)
  res <- entropy(res)
  res
}

myPlot2d <- function(lyap){
  library("plot3D")
  riga <- c(1:length(lyap))
  col.v <- sqrt(riga^2 + lyap^2)
  scatter2D(riga, lyap, colvar = col.v, pch = 16, bty ="g",
   	type ="b",col = gg.col(100))
}

fastaTs <- function(path,n=1){
  x <- read.fasta(path)
  x <- x[1:n]
  x <- unlist(x,use.names=FALSE)
  x <- ts(x)
  x <- gsub("a","0",x)
  x <- gsub("c","1",x)
  x <- gsub("g","3",x)
  x <- gsub("t","2",x)
  x <- gsub("n","6",x)
  x <- as.double(x)
  x
}

csvToTs <- function(path,n=1){
 res <- read.csv(path)
 res <- ts(res[n])
 res
}

fromTo <- function(x,from,to){
  if (class(x) != "data.frame"){
  x <- data.frame(x)
}
  if (from < to) {
  return (x[c(from:to),])
 } else return(x)
}

howManyNotes <- function(x){
  return (length(x))
}

saveAsCsv <- function(df,name="myResults"){
  dest <- getwd()
  dest <- paste(dest,"/",sep="")
  dest <- paste(dest,name,sep="")
  dest <- paste(dest,".csv",sep="")
  write.csv(df,dest, row.names = FALSE)
}

tsToGraph <- function(ts,n=70,directed="FALSE"){
  visGraph <- function(x){
    matrix <- matrix(data = NA, nrow = 0, ncol = 4)  
    for (i in c(1:(nrow(x)-1))){
      j = 1;
      while (x[i,1] >= x[i+j,1]){
        #tmp <- paste(x[i,1],i,sep=",")
        matrix <- rbind(matrix,c(x[i,1],x[i+j,1],x[i,2],x[i+j,2]))
        if (i+j+1 < nrow(x)){
          j = j+1 
        } else break
      }
    }
    return (matrix)
  }
  
  if (class(ts) != "data.frame") {
    ts <- data.frame(ts)
  }
  y<-head(ts,n)
  for (i in c(1:(nrow(y)))){
    y[i,2] <- paste(y[i,1],i,sep=",")
    }
  res<-visGraph(y)
  archi <- data.frame(res[,3],res[,4])
  vertici <- y[,2]
  if (directed=="TRUE"){
    g <- graph_from_data_frame(archi, directed=TRUE, vertices=vertici)
  } else {
    g <- graph_from_data_frame(archi, directed=FALSE, vertices=vertici)
  }
  return (g)
}

interactivePlot <- function(graph){
  edges <- ends(graph,E(graph))
  res <- data.frame(edges[,1],edges[,2])
  res <- simpleNetwork(res, height="100px", width="100px",        
                       Source = 1,                 # column number of source
                       Target = 2,                 # column number of target
                       linkDistance = 240,          # distance between node. Increase this value to have more space between nodes
                       charge = -115,                # numeric value indicating either the strength of the node repulsion (negative value) or attraction (positive value)
                       fontSize = 10,               # size of the node names
                       fontFamily = "serif",       # font og node names
                       linkColour = "#666",        # colour of edges, MUST be a common colour for the whole graph
                       nodeColour = "#3f7ed1",     # colour of nodes, MUST be a common colour for the whole graph
                       opacity = 0.9,              # opacity of nodes. 0=transparent. 1=no transparency
                       zoom = T                    # Can you zoom on the figure?
  )
  path <- paste(getwd(),"/src/interPlot.html",sep="")
  newwd <- paste(getwd(),"/src",sep="")
  oldwd <- getwd()
  setwd(newwd)
  saveWidget(res, file=path,title="provaTitolo",selfcontained=FALSE)
  setwd(oldwd)
  return (res)
}

addIsolatedNodes<- function(graph){
  vertici <- as_ids(V(graph))
  edges <- ends(graph,E(graph))
  archiCompleti <- union(edges[,1],edges[,2])
  diff <- setdiff(vertici,archiCompleti)
  if(!length(diff)==0){
    for (i in c(1:length(diff))){
      edges <- rbind(edges, c(diff[i],NULL))
    } 
  }
  res <- data.frame(edges[,1],edges[,2])
  res <- simpleNetwork(res, height="100px", width="100px",        
                       Source = 1,                 # column number of source
                       Target = 2,                 # column number of target
                       linkDistance = 240,          # distance between node. Increase this value to have more space between nodes
                       charge = -115,                # numeric value indicating either the strength of the node repulsion (negative value) or attraction (positive value)
                       fontSize = 10,               # size of the node names
                       fontFamily = "serif",       # font og node names
                       linkColour = "#666",        # colour of edges, MUST be a common colour for the whole graph
                       nodeColour = "#3f7ed1",     # colour of nodes, MUST be a common colour for the whole graph
                       opacity = 0.9,              # opacity of nodes. 0=transparent. 1=no transparency
                       zoom = T                    # Can you zoom on the figure?
  )
  path <- paste(getwd(),"/src/interPlot.html",sep="")
  newwd <- paste(getwd(),"/src",sep="")
  oldwd <- getwd()
  setwd(newwd)
  saveWidget(res, file=path,title="provaTitolo",selfcontained=FALSE)
  setwd(oldwd)
  return (res)
}




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

#da cancellare
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

baseCal <- function(ts,nlags="auto", method="ML", d=50){
  if(class(ts)=="list"){
    df <- data.frame()
    for (i in c(1:(length(ts)-1))){
      fractaldim <- fd.estim.boxcount(unlist(ts[i],use.names="FALSE"), nlags = nlags)
      entropy <- entropy(unlist(ts[i],use.names="FALSE"), method = method)
      hurst <- hurstexp(unlist(ts[i],use.names="FALSE"), display=FALSE, d=d)
      df <- rbind(df,c(fractaldim[2],entropy,hurst[2]))
      df <- unname(df)
    }
    colnames(df) <- c("fractalDim","entropy","hurst")
    return(df)
}
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

myPlot2d <- function(lyap,title=NULL){
  library("plot3D")
  riga <- c(1:length(lyap))
  col.v <- sqrt(riga^2 + lyap^2)
  scatter2D(riga, lyap, colvar = col.v, pch = 16, bty ="g",
            type ="b",col = gg.col(100),main=toupper(title),xlab="Time",ylab="Value")
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

  x <- gsub("r","4",x)
  x <- gsub("y","3",x)
  x <- gsub("k","5",x)
  x <- gsub("m","2",x)
  x <- gsub("s","4",x)
  x <- gsub("w","3",x)
x <- gsub("b","3",x)
x <- gsub("d","3",x)
x <- gsub("h","3",x)
x <- gsub("v","3",x)
  x <- as.double(x)
  x
}

csvToTs <- function (path, n = 1, row = FALSE) 
{
  res <- read.csv(path)
  if (row == FALSE && n <= ncol(res)) {
    res <- ts(res[n])
  } else if (row==TRUE && n <= nrow(res)){
    res <- ts(res[n, ])
  } else res <- 0
  return(res)
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
  dest <- paste(dest,"resources/csvResults/",name,sep="")
  dest <- paste(dest,".csv",sep="")
  write.csv(df,dest, row.names = FALSE,quote=FALSE)
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
  path <- paste(getwd(),"/src/interPlot/interPlot.html",sep="")
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
  path <- paste(getwd(),"/src/interPlot/interPlot.html",sep="")
  newwd <- paste(getwd(),"/src",sep="")
  oldwd <- getwd()
  setwd(newwd)
  saveWidget(res, file=path,title="provaTitolo",selfcontained=FALSE)
  setwd(oldwd)
  return (res)
}

splitWithOverlap <- function(vec, seg.length, overlap) {
 #if(window <= overlpa){ return(NULL)}
  starts <- seq(1, length(vec), by=seg.length-overlap)
  ends   <- starts + seg.length - 1
  ends[ends > length(vec)] <- length(vec)
  lapply(1:length(starts), function(i) vec[starts[i]:ends[i]])
}

myApprox<-function(ts){
  if (class(ts)=="list"){
    df <- data.frame()
    for (i in c(1:(length(ts)-1))){
      appEnt <- approx_entropy(unlist(ts[i],use.names=FALSE))
      df <- rbind(df,c(appEnt))
    }
    colnames(df) <- c("approx_entropy")
    return(df)
  }
  appEnt <- approx_entropy(x)
  return(appEnt)
}

mySample<-function(ts){
  if (class(ts)=="list"){
    df <- data.frame()
    for (i in c(1:(length(ts)-1))){
      samEnt <- sample_entropy(unlist(ts[i],use.names = FALSE))
      df <- rbind(df,c(samEnt))
    }
    colnames(df) <- c("sample_entropy")
    return(df)
  }
  samEnt <- sample_entropy(x)
  return(samEnt)
}

mergeDf<-function(df1,df2){
  if (!is.null(df1) && !is.null(df2)){
    for (i in c(1:ncol(df2))){
      df1 <- cbind(df1,df2[i])
    }
  }
  return(df1)
}

saveWindowGraph <- function(df){
  for (i in c(1:ncol(df))){
    name <- paste("resources/plots/windowGraph",i,".jpg",sep="")
    jpeg(name, width=750, height=575)
    myPlot2d(ts(df[i]),colnames(df[i]))
    dev.off()
  }
}

loadResults <- function(path){
  out <- tryCatch(
    {
      res <- read.csv(path)
      if(!is.null(res)){
        saveWindowGraph(res) 
      }
      return(1)
    },
    error=function(cond) {
      message(paste("There was an error with the csv:"),path)
      message("Here's the original error message:")
      message(cond)
      return(NA)
    },
    warning=function(cond) {
      message(paste("There was a warning with the csv!"),path)
      message("Here's the original warning message:")
      message(cond)
      return(NULL)
    }
  )
}

windowTsToGraph <- function(lista){
  result <- vector("list",length(lista))
  for (i in c(1:length(lista))){
    result[[i]] <- tsToGraph(lista[i],length(lista[[1]]))
  }
  return(result)
}
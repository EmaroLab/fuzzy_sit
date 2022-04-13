function drawTimeDistributionConcepts(tag, X, X2, X4, X6, X8, X0, sceneNumber)
  labelX = "Number of Scenes in Memory";
  labelY = "Computation Time [seconds]";
  labelY = "Computation Time [log10 seconds]"; 

  figure
  subplot(2, 3, 1);
  boxplot(X2);
  title (strcat(tag, X, "-2"));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  subplot(2, 3, 2);
  boxplot(X4);
  title (strcat(tag, X, "-4"));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  subplot(2, 3, 3);
  boxplot(X6);
  title (strcat(tag, X, "-6"));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  subplot(2, 3, 4);
  boxplot(X8);
  title (strcat(tag, X, "-8"));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  subplot(2, 3, 5);
  boxplot(X0);
  title (strcat(tag, X, "-10"));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  [meanX2, meanX4, meanX6, meanX8, meanX0] = getAVG(X2, X4, X6, X8, X0, sceneNumber);  
  
  subplot(2, 3, 6);
  hold on;
  plot(meanX2, "*-");
  plot(meanX4, "*-");
  plot(meanX6, "*-");
  plot(meanX8, "*-");
  plot(meanX0, "*-");
  legend(strcat(X,"-2"), strcat(X,"-4"), strcat(X,"-6"), strcat(X,"-8"), strcat(X,"-0"),'location','northwest')
  title (strcat(tag, X, " AVG"));
  ylabel(labelY);
  xlabel(labelX);
endfunction
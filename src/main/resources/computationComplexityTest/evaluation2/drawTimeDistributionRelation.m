function drawTimeDistributionRelation(tag, X, X2, X4, X6, X8, X0, sceneNumber)
  labelX = "Number of Scenes in Memory";
  labelY = "Computation Time [seconds]";
  labelY = "Computation Time [log10 seconds]"; 

  figure
  subplot(2, 3, 1);
  boxplot(X2);
  title (strcat(tag, " 2-", X));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  subplot(2, 3, 2);
  boxplot(X4);
  title (strcat(tag, " 4-", X));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  subplot(2, 3, 3);
  boxplot(X6);
  title (strcat(tag, " 6-", X));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  subplot(2, 3, 4);
  boxplot(X8);
  title (strcat(tag, " 8-", X));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  subplot(2, 3, 5);
  boxplot(X0);
  title (strcat(tag, " 10-", X));
  ylabel(labelY);
  xlabel(labelX);
  set(gca,'yscale','log');

  [mean2X, mean4X, mean6X, mean8X, mean0X] = getAVG(X2, X4, X6, X8, X0, sceneNumber);  
  
  subplot(2, 3, 6);
  hold on;
  plot(mean2X, "*-");
  plot(mean4X, "*-");
  plot(mean6X, "*-");
  plot(mean8X, "*-");
  plot(mean0X, "*-");
  legend(strcat("2-", X), strcat("4-", X), strcat("6-", X), strcat("8-", X), strcat("0-", X),'location','northwest')
  title (strcat(tag, X, " AVG"));
  ylabel(labelY);
  xlabel(labelX);
endfunction
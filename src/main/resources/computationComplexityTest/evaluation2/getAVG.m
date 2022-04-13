function [meanX2 meanX4 meanX6 meanX8 meanX0] = getAVG(X2, X4, X6, X8, X0, sceneNumber)
  meanX2 = [];
  meanX4 = [];
  meanX6 = [];
  meanX8 = [];
  meanX0 = [];
  for r = 1:size(X2)(2)
    meanX2 = [meanX2; [median(X2{r}), std(X2{r})]];
    meanX4 = [meanX4; [median(X4{r}), std(X4{r})]];
    meanX6 = [meanX6; [median(X6{r}), std(X6{r})]];
    meanX8 = [meanX8; [median(X8{r}), std(X8{r})]];
    meanX0 = [meanX0; [median(X0{r}), std(X0{r})]];
  endfor
endfunction
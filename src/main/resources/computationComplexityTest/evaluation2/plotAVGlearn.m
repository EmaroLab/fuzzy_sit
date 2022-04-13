function plotAVGrec(data, legendTags, tag) 
  figure
  subplot(1, 5, 1:4);
  colormap (colorcube (64));
  hold on;
  cnt = 0;
  i = 1;
  markers = {"*-", "o-", "+-", "s-", "p-"};
  for m = 1:size(data)
    plot(data(m,:), markers{i}, "markersize", 15, "LineWidth", 1.5);
    cnt = cnt + 1;
    if mod(cnt, 5) == 0 % 5 is the number of test for each parameters
      i = i + 1;
      set(gca,'ColorOrderIndex',1); % plot empty to reset the colormap
    endif
  endfor
  title (strcat(tag, " AVG"),'fontsize',20);
  ylabel("Computation Time [seconds]",'fontsize',20);
  xlabel("Number of Scenes in Memory",'fontsize',20);
  %set(gca,'yscale','log');
  grid on 
  set(gca,'fontsize',18);
  xlim([1,22])
  ylim([0.5,1100]);
  set(gca, 'ytick', [0:100:1100]);  
  set(gca, 'xtick', [0:22]);
  %set(gca, 'xticklabel',({'0','1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22'}));

  l = legend(legendTags)%'location','northwest')
  hsub = subplot(1,5,5);
  set (l, "position", get (hsub, "position"))
  legend boxoff 
  delete (hsub)
%% Requires https://github.com/matlab2tikz/matlab2tikz on your class path  (does not work with the legend)
%  matlab2tikz('learningScale.tex', 'height', '\fheight', 'width', '\fwidth');
endfunction


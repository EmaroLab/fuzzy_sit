function [data_c_r, successed] = checkTest(data, concepts, relations, elements, scenes)
  % Compute number of iterations
  iterations = size(elements')(1);
  s = 0;
  for i = 1:size(scenes')
    s = s + scenes(i);
  endfor
  iterations = iterations * s;
  
  % retrieve data for a specific test
  data_c = data(data(:,2)==concepts,:);
  data_c_r = data_c(data_c(:,3)==relations,:);
  evaluated_elements = size(data_c_r(:,4))(1);
  evaluated_scenes = size(data_c_r(:,6))(1);
  
  % check if it is completed
  if evaluated_elements >= iterations && evaluated_scenes >= iterations
    printf ("Test %d-%d done for ALL the %d iterations (actual value, elements=%d, scenes=%d)\n", 
            concepts, relations, iterations, evaluated_elements, evaluated_scenes);
    successed = 1;
  else
    printf ("Test %d-%d NOT done for the %d iterations (actual value %d)\n", 
            concepts, relations, iterations, evaluated_elements);
    successed = 0;
  %endif  
  
    % logg to actually check missing data. The if before is an approximated check. 
    % Let elements={e1,e2,...} and scenes={s1,s2,...} It should print:
    % e1 s1   e1 s2   e1 s3  ...
    % e2 s1   e2 s2   e2 s3  ...
    % ...
    % In this matrix you should check for missing pairs based on input `elements` and `scenes`
    eisi = [data_c_r(:,4), data_c_r(:,6)];
    eisi_log = [];
    for s =1:size(scenes')
      eisi_log = [eisi_log, eisi(eisi(:,2)==scenes(s),:)];
    endfor
    eisi_log
  endif  % you might want to print the log above even if successed!
endfunction
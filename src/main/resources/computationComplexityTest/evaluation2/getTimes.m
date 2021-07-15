# returns encoding, recognition, learning and structuring time arranged by number of scenes
function [recognition learning] = getTimes(data, sceneNumbers, elementNumbers, concepts, relations)
  dataCR = sortrows(checkTest(data, concepts, relations, elementNumbers, sceneNumbers), [4,6]);
  
  recognition = {};
  learning = {};
  for i = 1:sceneNumbers(1)  % e.g., sceneNumbers(1)=22
    arranged = dataCR(dataCR(:,6)==i,:);  % arranged by scenes 1,2,...,22
    recognition = {recognition{:}, arranged(:,7) / 1000};  % convert from ms to sec
    learning = {learning{:}, arranged(:,8) / 1000};   % convert from ms to sec
  endfor
endfunction
% [encoding22 recognition22 learning22 structuring22] = getTimes(data22,SCENES_NUMBER(1)," 2-2");
% [encoding24 recognition24 learning24 structuring24] = getTimes(data24,SCENES_NUMBER(1)," 2-4");
% [encoding26 recognition26 learning26 structuring26] = getTimes(data26,SCENES_NUMBER(1)," 2-6");
% [encoding28 recognition28 learning28 structuring28] = getTimes(data28,SCENES_NUMBER(1)," 2-8");
% [encoding29 recognition20 learning20 structuring20] = getTimes(data20,SCENES_NUMBER(1)," 2-10");
% 
% [encoding42 recognition42 learning42 structuring42] = getTimes(data42,SCENES_NUMBER(1)," 4-2");
% [encoding44 recognition44 learning44 structuring44] = getTimes(data44,SCENES_NUMBER(1)," 4-4");
% [encoding46 recognition46 learning46 structuring46] = getTimes(data46,SCENES_NUMBER(1)," 4-6");
% [encoding48 recognition48 learning48 structuring48] = getTimes(data48,SCENES_NUMBER(1)," 4-8");
% [encoding49 recognition40 learning40 structuring40] = getTimes(data40,SCENES_NUMBER(1)," 4-10");
% 
% [encoding62 recognition62 learning62 structuring62] = getTimes(data62,SCENES_NUMBER(1)," 6-2");
% [encoding64 recognition64 learning64 structuring64] = getTimes(data64,SCENES_NUMBER(1)," 6-4");
% [encoding66 recognition66 learning66 structuring66] = getTimes(data66,SCENES_NUMBER(1)," 6-6");
% [encoding68 recognition68 learning68 structuring68] = getTimes(data68,SCENES_NUMBER(1)," 6-8");
% [encoding69 recognition60 learning60 structuring60] = getTimes(data60,SCENES_NUMBER(1)," 6-10");
% 
% [encoding82 recognition82 learning82 structuring82] = getTimes(data82,SCENES_NUMBER(1)," 8-2");
% [encoding84 recognition84 learning84 structuring84] = getTimes(data84,SCENES_NUMBER(1)," 8-4");
% [encoding86 recognition86 learning86 structuring86] = getTimes(data86,SCENES_NUMBER(1)," 8-6");
% [encoding88 recognition88 learning88 structuring88] = getTimes(data88,SCENES_NUMBER(1)," 8-8");
% [encoding89 recognition80 learning80 structuring80] = getTimes(data80,SCENES_NUMBER(1)," 8-10");
% 
% [encoding02 recognition02 learning02 structuring02] = getTimes(data02,SCENES_NUMBER(1)," 10-2");
% [encoding04 recognition04 learning04 structuring04] = getTimes(data04,SCENES_NUMBER(1)," 10-4");
% [encoding06 recognition06 learning06 structuring06] = getTimes(data06,SCENES_NUMBER(1)," 10-6");
% [encoding08 recognition08 learning08 structuring08] = getTimes(data08,SCENES_NUMBER(1)," 10-8");
%[encoding00 recognition00 learning00 structuring00] = getTimes(data00,SCENES_NUMBER(1)," 10-10");


function [encodingAll recognitionAll learningAll structuringAll] = getTimes(data, sceneNumber,tag)
  preEncodingAll = [];
  preRecognitionAll = [];
  learningAll = [];
  structuringAll = [];
  postEncodingAll = [];
  postRecognitionAll = [];
  for s = 1:sceneNumber
    dataScenes = data(data(:,6)==s,:);
    
    preEncodingAll = [preEncodingAll, dataScenes(:,7)];
    preRecognitionAll = [preRecognitionAll, dataScenes(:,8)];
    learningAll = [learningAll, dataScenes(:,9)];
    structuringAll = [structuringAll, dataScenes(:,10)];
    postEncodingAll = [postEncodingAll, dataScenes(:,11)];
    postRecognitionAll = [postRecognitionAll, dataScenes(:,12)];
  endfor

  encodingAll = {};
  recognitionAll = {};
  for s = 1:sceneNumber
    if s == 1
      encodingAll = {preEncodingAll(:,s)};
      recognitionAll = {preRecognitionAll(:,s)};
    else
      encodingAll = {encodingAll{:}, [preEncodingAll(:,s); postEncodingAll(:,s-1)]};
      recognitionAll = {recognitionAll{:}, [preRecognitionAll(:,s);postRecognitionAll(:,s-1)]};
    end
  end
  
  labelX = "Number of Scenes in Memory";
  labelY = "Computation Time [ms]"; 

  figure
  subplot (2,3,1, "align");
  b = boxplot(preEncodingAll);
  xlabel(labelX);
  ylabel(labelY);
  title (strcat("Pre Encoding", tag));

  subplot (2,3,2, "align");
  b = boxplot(postEncodingAll);
  xlabel(labelX);
  ylabel(labelY);
  title (strcat("Post Encoding", tag));

  subplot (2,3,3, "align");
  b = boxplot(encodingAll);
  xlabel(labelX);
  ylabel(labelY);
  title (strcat("Encoding", tag));

  subplot (2,3,4, "align");
  b = boxplot(preRecognitionAll);
  xlabel(labelX);
  ylabel(labelY);
  title (strcat("Pre Recognition", tag));

  subplot (2,3,5, "align");
  b = boxplot(postRecognitionAll);
  xlabel(labelX);
  ylabel(labelY);
  title (strcat("Post Recognition", tag));

  subplot (2,3,6, "align");
  b = boxplot(recognitionAll);
  xlabel(labelX);
  ylabel(labelY);
  title (strcat("Recognition", tag));

endfunction
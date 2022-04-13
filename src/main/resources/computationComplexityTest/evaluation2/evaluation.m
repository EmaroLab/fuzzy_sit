% ATTENTION! this script and related functions are not implemented generically for different size of paramaters array
clear all;

pkg load statistics

ELEMENTS_NUMBER = [4, 10, 19, 33];  % i.e., -Pelements
SCENES_NUMBER = [22, 22, 22];  % i.e.,-PConcepts  (its elements should be all equal!)

data = csvread('allComputationTime.csv');
data = data(2:end,:); % remove csv header

%% CSV columns
%  1) An ordered identified based on the creation timestamp,
%	 2) The Number of concepts in the ontology,
%	 3) The Number of relations in the ontology,
%	 4) Number of elements in the scene,
%	 5) Number of roles in the scene,
%	 6) Number of items in the memory,
%	    7) The encoding and recognition time in ms,
%	    8) The learning and structuring time in ms,,


%% TEST DATA DISTRIBUTION
% CONCEPTS = [2,4,6,8,10]; % i.e., the -Pconcepts
% RELATIONS = [10,8,6,4,2]; % i.e., the -Prelations
% 
% TEST_NUMBER = sum(SCENES_NUMBER)*size(ELEMENTS_NUMBER')(1);
% for c = 1:size(CONCEPTS') 
%   for r = 1:size(RELATIONS') 
%     [d, successed] = checkTest(data, CONCEPTS(c), RELATIONS(r), ELEMENTS_NUMBER, SCENES_NUMBER);
%     figure  
%     plot(d(:,5), d(:,7),'*');
%     ylabel ("Scene");
%     xlabel ("Elements");
%     if successed == 1
%       successed_str = "  (completed)";
%     else
%       successed_str = "  (UNcompleted)";
%     endif
%     title (strcat("Test ", num2str(CONCEPTS(c)), "-", num2str(RELATIONS(r)), successed_str));  
%   endfor
% endfor


% GET RECOGNITION AND LEARNING TIMES IN SECONDS
[recognition22 learning22] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 2, 2);
[recognition24 learning24] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 2, 4);
[recognition26 learning26] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 2, 6);
[recognition28 learning28] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 2, 8);
[recognition20 learning20] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 2, 10);
%%%%%%%%%%%%%%%%%%%
[recognition42 learning42] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 4, 2);
[recognition44 learning44] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 4, 4);
[recognition46 learning46] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 4, 6);
[recognition48 learning48] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 4, 8);
[recognition40 learning40] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 4, 10);
%%%%%%%%%%%%%%%%%%%
[recognition62 learning62] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 6, 2);
[recognition64 learning64] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 6, 4);
[recognition66 learning66] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 6, 6);
[recognition68 learning68] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 6, 8);
[recognition60 learning60] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 6, 10);
%%%%%%%%%%%%%%%%%%%
[recognition82 learning82] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 8, 2);
[recognition84 learning84] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 8, 4);
[recognition86 learning86] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 8, 6);
[recognition88 learning88] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 8, 8);
[recognition80 learning80] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 8, 10);
%%%%%%%%%%%%%%%%%%%
[recognition02 learning02] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 10, 2);
[recognition04 learning04] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 10, 4);
[recognition06 learning06] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 10, 6);
[recognition08 learning08] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 10, 8);
[recognition00 learning00] = getTimes(data, SCENES_NUMBER, ELEMENTS_NUMBER, 10, 10);


% DRAW TIME FOR ONTOLOGY COMPLEXITY
%drawTimeDistributionConcepts("Recognition", " 2", recognition22, recognition24, recognition26, recognition28, recognition20, SCENES_NUMBER(1))
%drawTimeDistributionConcepts("Recognition", " 4", recognition42, recognition44, recognition46, recognition48, recognition40, SCENES_NUMBER(1))
%drawTimeDistributionConcepts("Recognition", " 6", recognition62, recognition64, recognition66, recognition68, recognition60, SCENES_NUMBER(1))
%drawTimeDistributionConcepts("Recognition", " 8", recognition82, recognition84, recognition86, recognition88, recognition80, SCENES_NUMBER(1))
%drawTimeDistributionConcepts("Recognition", " 10", recognition02, recognition04, recognition06, recognition08, recognition00, SCENES_NUMBER(1))
%
%drawTimeDistributionConcepts("Learning", " 2", learning22, learning24, learning26, learning28, learning20, SCENES_NUMBER(1))
%drawTimeDistributionConcepts("Learning", " 4", learning42, learning44, learning46, learning48, learning40, SCENES_NUMBER(1))
%drawTimeDistributionConcepts("Learning", " 6", learning62, learning64, learning66, learning68, learning60, SCENES_NUMBER(1))
%drawTimeDistributionConcepts("Learning", " 8", learning82, learning84, learning86, learning88, learning80, SCENES_NUMBER(1))
%drawTimeDistributionConcepts("Learning", " 10", learning02, learning04, learning06, learning08, learning00, SCENES_NUMBER(1))
%
%drawTimeDistributionRelation("Recognition", "2", recognition22, recognition42, recognition62, recognition82, recognition02, SCENES_NUMBER(1))
%drawTimeDistributionRelation("Recognition", "4", recognition24, recognition44, recognition64, recognition84, recognition04, SCENES_NUMBER(1))
%drawTimeDistributionRelation("Recognition", "6", recognition26, recognition46, recognition66, recognition86, recognition06, SCENES_NUMBER(1))
%drawTimeDistributionRelation("Recognition", "8", recognition28, recognition48, recognition68, recognition88, recognition08, SCENES_NUMBER(1))
%drawTimeDistributionRelation("Recognition", "10", recognition20, recognition40, recognition60, recognition80, recognition00, SCENES_NUMBER(1))
%
%drawTimeDistributionRelation("Learning", "2", learning22, learning42, learning62, learning82, learning02, SCENES_NUMBER(1))
%drawTimeDistributionRelation("Learning", "4", learning24, learning44, learning64, learning84, learning04, SCENES_NUMBER(1))
%drawTimeDistributionRelation("Learning", "6", learning26, learning46, learning66, learning86, learning06, SCENES_NUMBER(1))
%drawTimeDistributionRelation("Learning", "8", learning28, learning48, learning68, learning88, learning08, SCENES_NUMBER(1))
%drawTimeDistributionRelation("Learning", "10", learning20, learning40, learning60, learning80, learning00, SCENES_NUMBER(1))


% GET RECOGNITION TIME AND STD AVERAGE FOR ALL SCENES ELEMENTS
[rec22avg, rec24avg, rec26avg, rec28avg, rec20avg] = getAVG(recognition22, recognition24, recognition26, recognition28, recognition20, SCENES_NUMBER(1));  
[rec42avg, rec44avg, rec46avg, rec48avg, rec40avg] = getAVG(recognition42, recognition44, recognition46, recognition48, recognition40, SCENES_NUMBER(1));  
[rec62avg, rec64avg, rec66avg, rec68avg, rec60avg] = getAVG(recognition62, recognition64, recognition66, recognition68, recognition60, SCENES_NUMBER(1));  
[rec82avg, rec84avg, rec86avg, rec88avg, rec80avg] = getAVG(recognition82, recognition84, recognition86, recognition88, recognition80, SCENES_NUMBER(1));  
[rec02avg, rec04avg, rec06avg, rec08avg, rec00avg] = getAVG(recognition02, recognition04, recognition06, recognition08, recognition00, SCENES_NUMBER(1));  
%% PLOT AGGREGATED RECOGNITION TIME COMPARISON FOR ONTOLOGY COMPLEXITY
plotAVGrec([rec22avg(:,1)'; rec24avg(:,1)'; rec26avg(:,1)'; rec28avg(:,1)'; rec20avg(:,1)'; 
         rec42avg(:,1)'; rec44avg(:,1)'; rec46avg(:,1)'; rec48avg(:,1)'; rec40avg(:,1)'; 
         rec62avg(:,1)'; rec64avg(:,1)'; rec66avg(:,1)'; rec68avg(:,1)'; rec60avg(:,1)'; 
         rec82avg(:,1)'; rec84avg(:,1)'; rec86avg(:,1)'; rec88avg(:,1)'; rec80avg(:,1)'; 
         rec02avg(:,1)'; rec04avg(:,1)'; rec06avg(:,1)'; rec08avg(:,1)'; rec00avg(:,1)'],
        {"2-2"; "2-4"; "2-6"; "2-8"; "2-10";
         "4-2"; "4-4"; "4-6"; "4-8"; "4-10";
         "6-2"; "6-4"; "6-6"; "6-8"; "6-10";    
         "8-2"; "8-4"; "8-6"; "8-8"; "8-10";
         "10-2"; "10-4"; "10-6"; "10-8"; "10-10"}, "All Recognition");
         

%% GET LEARNING TIME AND STD AVERAGE FOR ALL SCENES ELEMENTS
[learn22avg, learn24avg, learn26avg, learn28avg, learn20avg] = getAVG(learning22, learning24, learning26, learning28, learning20, SCENES_NUMBER(1));  
[learn42avg, learn44avg, learn46avg, learn48avg, learn40avg] = getAVG(learning42, learning44, learning46, learning48, learning40, SCENES_NUMBER(1));  
[learn62avg, learn64avg, learn66avg, learn68avg, learn60avg] = getAVG(learning62, learning64, learning66, learning68, learning60, SCENES_NUMBER(1));  
[learn82avg, learn84avg, learn86avg, learn88avg, learn80avg] = getAVG(learning82, learning84, learning86, learning88, learning80, SCENES_NUMBER(1));  
[learn02avg, learn04avg, learn06avg, learn08avg, learn00avg] = getAVG(learning02, learning04, learning06, learning08, learning00, SCENES_NUMBER(1));  
% PLOT AGGREGATED LEARNING TIME COMPARISON FOR ONTOLOGY COMPLEXITY 
plotAVGlearn([learn22avg(:,1)'; learn24avg(:,1)'; learn26avg(:,1)'; learn28avg(:,1)'; learn20avg(:,1)'; 
         learn42avg(:,1)'; learn44avg(:,1)'; learn46avg(:,1)'; learn48avg(:,1)'; learn40avg(:,1)'; 
         learn62avg(:,1)'; learn64avg(:,1)'; learn66avg(:,1)'; learn68avg(:,1)'; learn60avg(:,1)'; 
         learn82avg(:,1)'; learn84avg(:,1)'; learn86avg(:,1)'; learn88avg(:,1)'; learn80avg(:,1)'; 
         learn02avg(:,1)'; learn04avg(:,1)'; learn06avg(:,1)'; learn08avg(:,1)'; learn00avg(:,1)'],
        {"2-2"; "2-4"; "2-6"; "2-8"; "2-10";
         "4-2"; "4-4"; "4-6"; "4-8"; "4-10";
         "6-2"; "6-4"; "6-6"; "6-8"; "6-10";    
         "8-2"; "8-4"; "8-6"; "8-8"; "8-10";
         "10-2"; "10-4"; "10-6"; "10-8"; "10-10"}, "All Learning");

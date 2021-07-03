package it.emarolab.fuzzySIT.memoryLike;

import it.emarolab.fuzzySIT.semantic.SITTBox;
import it.emarolab.fuzzySIT.semantic.hierarchy.SceneHierarchyEdge;
import it.emarolab.fuzzySIT.semantic.hierarchy.SceneHierarchyVertex;
//import javafx.util.Pair;
import org.jgrapht.ListenableGraph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleMemory extends MemoryInterface{

    private static String SCENE_PREFIX = "Scene";

    // recognition = 1, similarity = 0 => it always consolidates on recognition
    private static final double ENCODE_RECOGNITION_TH = .9; // recognition threshold above which it consolidates [0,1]
    private static final double ENCODE_SIMILARITY_TH = .2; // similarity threshold above which it consolidates [0,1]

    // recognition = 1, similarity = 1 => it always learns on recognition
    private static final double LEARN_RECOGNITION_TH = .9; // recognition threshold below which it learns [0,1]
    private static final double LEARN_SIMILARITY_TH = .8; // similarity threshold below which it learns [0,>~1]

    private static final double SCORE_WEAK = .1; // threshold under which it forgets [0,1]
    private static final double LEARNED_SCORE = .5; // initial score, percentage of max score [0,1]

    private static final double ENCODE_REINFORCE = 10;//10; // reinforce factor for re-stored or re-retrieved experience [1,inf)

    private static int sceneCnt = 0;
    
    // actually remove node if true. 
    // Otherwise the node remains but it is not consolidated (i.e., frozen). 
    // REMARK: with false, SIT performances do not benefit from forgetting, 
    //         with true the Graph would not show forgotten nodes.
    private static boolean REMOVE_FORGET = false; 

    public SimpleMemory(SITTBox tbox) {
        super(tbox);
    }

    public SceneHierarchyVertex store() {
        return store( SCENE_PREFIX + sceneCnt++);
    }
    @Override
    public SceneHierarchyVertex store(String sceneName){
        Map<SceneHierarchyVertex, Double> rec = recognize();
        // if memory is empty, learn new encoded scene
        if( rec.keySet().isEmpty())
            return learn(sceneName, LEARNED_SCORE);

        // if encoded scene can be recognized, update score
        boolean shouldLearn = true;
        double maxScore = 0;
        for ( SceneHierarchyVertex recognisedScene : rec.keySet()){
            double recognisedValue = rec.get( recognisedScene);
            double similarityValue = getAbox().getSimilarity(recognisedScene);
            if( recognisedValue >= ENCODE_RECOGNITION_TH & similarityValue >= ENCODE_SIMILARITY_TH)  // update score
                updateScoreStoring(recognisedScene, recognisedValue);
            if( similarityValue >= LEARN_SIMILARITY_TH & recognisedValue >= LEARN_RECOGNITION_TH) // do not learn
                shouldLearn = false;
            double score = recognisedScene.getMemoryScore();
            if ( score > maxScore)
                maxScore = score;
        }
        // if encoded scene can not be recognized, learn new scene
        if ( shouldLearn)
            return learn(sceneName, LEARNED_SCORE * maxScore);
        return null;
    }
    protected void updateScoreStoring(SceneHierarchyVertex recognisedScene, double recognisedValue) {
        updateScorePolicy( recognisedScene, recognisedValue);
    }

    @Override
    public SceneHierarchyVertex retrieve() { // TODO very minimal retrieve support, adjust and implement it better!
        Map<SceneHierarchyVertex, Double> rec = recognize();
        SceneHierarchyVertex out = null;
        double bestOut = 0;
        // if memory is empty, do not do nothing
        if( ! rec.keySet().isEmpty()) {
            // update score of retrieved
            for (SceneHierarchyVertex recognisedScene : rec.keySet()) {
                double recognisedValue = rec.get( recognisedScene);
                if ( recognisedValue > bestOut){
                    out = recognisedScene;
                    bestOut = recognisedValue;
                }
                updateScoreRetrieve(recognisedScene, recognisedValue);
            }
            return out; // true if at least one score is updated
        }
        return out;
    }
    private void updateScoreRetrieve(SceneHierarchyVertex recognisedScene, double recognisedValue) {
        updateScorePolicy( recognisedScene, recognisedValue);
    }

    private void updateScorePolicy(SceneHierarchyVertex recognisedScene, double recognisedValue) {
        // TODO adjust and validate
        double score = recognisedScene.getMemoryScore();
        if ( recognisedScene.getMemoryScore() > 0) // not froze node
            score += ENCODE_REINFORCE * recognisedValue; // reinforce for re-stored or re-retrieved experiences
        // else score freeze (i.e., experience to remove)
        recognisedScene.setMemoryScore( score);
    }

    @Override
    public void consolidate() {
        // TODO reinforce score based on graph edges and nodes

        //Set<Pair<SceneHierarchyVertex, SceneHierarchyVertex>> removed = getTbox().simplify();
        //System.out.println( "\tsimplifying " + removed);

        normalizeScoreConsolidating();
    }
    public void normalizeScoreConsolidating(){
        ListenableGraph<SceneHierarchyVertex, SceneHierarchyEdge> h = getTbox().getHierarchy();
        double maxScore = 0;
        for( SceneHierarchyVertex scene : h.vertexSet()){
            double score = scene.getMemoryScore();
            if ( score > maxScore)
                maxScore = score;
        }
        if ( maxScore > 0)
            for (SceneHierarchyVertex experience : h.vertexSet())
                if (experience.getMemoryScore() >= 0)
                    experience.setMemoryScore(experience.getMemoryScore() / maxScore);
    }

    @Override
    public Set<SceneHierarchyVertex> forget(){
        ListenableGraph<SceneHierarchyVertex, SceneHierarchyEdge> h = getTbox().getHierarchy();
        Set<SceneHierarchyVertex> forgotten = new HashSet<>();
        // find weak score in the memory graph
        for( SceneHierarchyVertex scene : h.vertexSet()){
            if( scene.getMemoryScore() < SCORE_WEAK) {
                scene.setMemoryScore(-1); // score getter will be always 0 and is not consider on consolidation
                forgotten.add( scene);
            }
        }

        if( REMOVE_FORGET)
            for( SceneHierarchyVertex scene : forgotten)
                getTbox().removeScene( scene);
        return forgotten;
    }
}

package it.emarolab.runnableSITutility.example;

import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.fuzzySIT.core.SITABox;
import it.emarolab.fuzzySIT.core.SITTBox;
import it.emarolab.fuzzySIT.core.axioms.SpatialObject;
import it.emarolab.fuzzySIT.core.axioms.SpatialRelation;
import it.emarolab.fuzzySIT.core.hierarchy.SceneHierarchyVertex;

import java.util.HashSet;
import java.util.Set;

public class SITExamplePaper {

    public static Set<SpatialObject> objects = new HashSet<>();
    public static Set<SpatialRelation> relations = new HashSet<>();

    public static final String GLASS = "Glass";
    public static final String CUP = "Cup";
    public static final String FRONT = "isFrontOf";
    public static final String GAMMA1 = "g1";
    public static final String GAMMA2 = "g2";
    public static final String GAMMA3 = "g3";

    private static void clear(){
        objects.clear();
        relations.clear();
    }

    public static void main(String[] args) {
        // instantiate a T-Box with the default T-Box ontology and reasoner configuration file
        SITTBox h = new SITTBox(FuzzySITBase.RESOURCES_PATH + "ontologies/example_paper.fuzzydl");

        System.out.println("-------------------------  S1   ------------------------");
        // create S1 and recognise it
        formatS1();
        SITABox r1 = new SITABox(h, objects, relations);
        SceneHierarchyVertex learnedCategory1 = h.learn("Scene1", r1);
        System.out.println(" Learning category with definition: " + learnedCategory1.getDefinition());

        System.out.println("-------------------------  S2   ------------------------");
        // create S1 and recognise it
        formatS2();
        SITABox r2 = new SITABox(h, objects, relations);
        SceneHierarchyVertex learnedCategory2 = h.learn("Scene2", r2);
        System.out.println(" Learning category with definition: " + learnedCategory2.getDefinition());

        System.out.println("------------------------  showing ----------------------");
        // shows the inferred and learned SIT scene hierarchy
        h.show();
        // saved the augmented ontology
        h.saveTbox(FuzzySITBase.RESOURCES_PATH + "ontologies/learnedTest.fuzzydl");
    }

    private static void formatS1(){
        clear();

        objects.add( new SpatialObject( GLASS, GAMMA1, .8));
        objects.add( new SpatialObject( CUP, GAMMA2, .9));

        relations.add( new SpatialRelation( GAMMA1, FRONT, GAMMA2, .9));
    }

    private static void formatS2(){
        clear();

        objects.add( new SpatialObject( GLASS, GAMMA1, .8));
        objects.add( new SpatialObject( CUP, GAMMA2, .9));
        objects.add( new SpatialObject( GLASS, GAMMA3, .1));
        objects.add( new SpatialObject( CUP, GAMMA3, .7));

        relations.add( new SpatialRelation( GAMMA1, FRONT, GAMMA2, .6));
        relations.add( new SpatialRelation( GAMMA1, FRONT, GAMMA3, .5));
        relations.add( new SpatialRelation( GAMMA3, FRONT, GAMMA2, .9));
    }

}

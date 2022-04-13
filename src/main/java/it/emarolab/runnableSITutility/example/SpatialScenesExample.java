package it.emarolab.runnableSITutility.example;

import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.fuzzySIT.core.PlacedObject;
import it.emarolab.fuzzySIT.core.SITABox;
import it.emarolab.fuzzySIT.core.SITTBox;
import it.emarolab.fuzzySIT.core.SigmaCounters;
import it.emarolab.fuzzySIT.core.axioms.SpatialRelation;

import java.util.*;

public class SpatialScenesExample {

    // Constant names defined in the ontologies
    public static final String CONE = "Cone";
    public static final String CYLINDER = "Cylinder";
    public static final String PLANE = "Plane";
    public static final String SPHERE = "Sphere";
    public static final String RIGHT = "isRightOf";
    public static final String FRONT = "isFrontOf";

    private static SceneFacts formatS1(){
        SceneFacts facts = new SceneFacts();
        facts.addObject("g1", 1.492, 0.146,
                new ObjectType(CONE, .3),
                new ObjectType(CYLINDER, .9));
        facts.addObject("g2", 1.712, -0.096,
                new ObjectType(CONE, .8),
                new ObjectType(CYLINDER, .4));
        facts.addObject("g3", 1.371, -0.350,
                new ObjectType(PLANE, .7));
        facts.createSpatialRelations();
        return facts;
    }

    private static SceneFacts formatS2(){
        SceneFacts facts = new SceneFacts();
        facts.addObject("g4", 1.498, 0.145,
                new ObjectType(CONE, .4),
                new ObjectType(CYLINDER, .8));
        facts.addObject("g5", 1.375, -0.348,
                new ObjectType(PLANE, .8));
        facts.createSpatialRelations();
        return facts;
    }

    private static SceneFacts formatS3(){
        SceneFacts facts = new SceneFacts();
        facts.addObject("g6", 1.478, 0.339,
                new ObjectType(CONE, .1),
                new ObjectType(CYLINDER, .7));
        facts.addObject("g7", 1.278, 0.251,
                new ObjectType(SPHERE, .8));
        facts.addObject("g8", 1.609, 0.206,
                new ObjectType(SPHERE, .9));
        facts.addObject("g9", 1.493, 0.143,
                new ObjectType(CONE, .2),
                new ObjectType(CYLINDER, .7));
        facts.addObject("g10", 1.717, -0.091,
                new ObjectType(CONE, .9),
                new ObjectType(CYLINDER, .3));
        facts.addObject("g11", 1.368, -0.349,
                new ObjectType(PLANE, .8));
        facts.addObject("g12", 1.486, -0.544,
                new ObjectType(CONE, .2),
                new ObjectType(CYLINDER, .8));
        facts.addObject("g13", 1.741, -0.518,
                new ObjectType(SPHERE, 1));
        facts.createSpatialRelations();
        return facts;
    }

    private static SceneFacts formatS4(){
        SceneFacts facts = new SceneFacts();
        facts.addObject("g14", 1.284, 0.084,
                new ObjectType(SPHERE, .9));
        facts.addObject("g15", 1.518, -0.107,
                new ObjectType(SPHERE, .8));
        facts.addObject("g16", 1.717, -0.311,
                new ObjectType(SPHERE, 1));
        facts.createSpatialRelations();
        return facts;
    }

    private static SceneFacts formatS5(){
        SceneFacts facts = new SceneFacts();
        facts.addObject("g17", 1.278, 0.096,
                new ObjectType(SPHERE, .8));
        facts.addObject("g18", 1.596, -0.173,
                new ObjectType(SPHERE, 1));
        facts.addObject("g19", 1.709, -0.321,
                new ObjectType(SPHERE, .9));
        facts.createSpatialRelations();
        return facts;
    }

    public static void testScene(int identifier, SITTBox h, SceneFacts facts){
        System.out.println("-------------------------  S" + identifier + "   ------------------------");
        SITABox r = new SITABox(h, facts.getObjects(), facts.getRelations());
        System.out.println(facts);
        System.out.println("------------------------- learn ------------------------");
        // learn S1
        h.learn( "Scene" + identifier, r);
        System.out.println("Sigma Counters: " + r.getDefinition());
        System.out.println("--------------------------------------------------------");
    }

    private static void testScenes(double... fuzziness){
        List<SceneFacts> scenesFacts = new ArrayList<>();
        scenesFacts.add(formatS1());
        scenesFacts.add(formatS2());
        scenesFacts.add(formatS3());
        scenesFacts.add(formatS4());
        scenesFacts.add(formatS5());
        for (double f: fuzziness){
            System.out.println("Fuzziness: " + f);
            SITTBox onto = new SITTBox(FuzzySITBase.RESOURCES_PATH + "ontologies/Simple-RANSAC-Scene.fuzzydl");
            onto.setFuzziness(f * 100);
            int sceneIdentifier = 1;
            for(SceneFacts facts: scenesFacts){
                testScene(sceneIdentifier, onto, facts);
                sceneIdentifier += 1;
            }
            onto.show();
            System.out.println("\n################################################################\n");
        }
    }

    public static void main(String[] args) {
        SceneFacts.printNoiseSetup();
        System.out.println("\n################################################################\n");
        testScenes(0, 0.3, 0.5, 0.7, 1);
    }
}

class SceneFacts{
    private final Set<PlacedObject> objects = new HashSet<>(); // the set of objects in the SIT scene to test
    public final Set<SpatialRelation> relations = new HashSet<>(); // the set of relations in the SIT scene to test

    public void addObject(String id, double x, double y, ObjectType... types){
        x = spatialNoise(x);
        y = spatialNoise(y);
        for (ObjectType t : types){
            objects.add(new PlacedObject(t.getType(), id, x, y, fuzzyNoise(t.getDegree())));
        }
    }

    public void createSpatialRelations(){
        for(PlacedObject o1: objects){
            for(PlacedObject o2: objects){
                if(!o1.getObject().equals(o2.getObject())){
                    Set<SpatialRelation> relSet = o1.getRelations(o2);
                    for(SpatialRelation r: relSet){
                        if(r.getRelation().contains(SpatialScenesExample.RIGHT) || r.getRelation().contains(SpatialScenesExample.FRONT)){
                            // do not consider inverse relations, i.e., LEFT and BEHIND.
                            if(r.getDegree() > 0){
                                relations.add(r);
                            }
                        }
                    }
                }
            }
        }
    }

    public Set<PlacedObject> getObjects() {
        return objects;
    }
    public Set<SpatialRelation> getRelations() {
        return relations;
    }

    @Override
    public String toString(){
        return "Objects:   " + getObjects() + "\n" + "Relations: " + getRelations() + "\n";
    }

    private static final double SPATIAL_MIN_NOISE = 0.010; // in meters
    private static final double SPATIAL_MAX_NOISE = 0.100; // in meters
    private static final double FUZZY_MIN_NOISE = 0.2; // in [0,1]
    private static final double FUZZY_MAX_NOISE = 0.5; // in [0,1]
    private double spatialNoise(double degree) {// random with no 0 average
        return noise(degree, SPATIAL_MAX_NOISE, SPATIAL_MIN_NOISE);
    }
    private double fuzzyNoise(double degree) {// random with no 0 average
        double noised = noise(degree, FUZZY_MAX_NOISE, FUZZY_MIN_NOISE);
        if (noised <= 0)
            return 0.001;
        if (noised >= 1)
            return 1;
        else
            return noised;
    }
    private double noise(double degree, double max, double min) {// random with no 0 average
        Random rand = new Random();
        double rangeMax = min + (max - min) * rand.nextDouble();
        rand = new Random();
        double rangeMin = -1 * (min + (max - min) * rand.nextDouble());
        rand = new Random();
        double n = rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
        return degree + n;
    }
    public static void printNoiseSetup(){
        System.out.println("Spatial noise on object position (no 0 average): min = " + SPATIAL_MIN_NOISE + ", max = " + SPATIAL_MAX_NOISE + "[m]");
        System.out.println("Fuzzy noise on object type (no 0 average): min = " + FUZZY_MIN_NOISE + ", max = " + FUZZY_MAX_NOISE);
    }
}

class ObjectType{
    private final String type;
    private final double degree;
    public ObjectType(String type, double degree){
        this.type = type;
        this.degree = degree;
    }
    public String getType() {
        return type;
    }
    public double getDegree() {
        return degree;
    }
}
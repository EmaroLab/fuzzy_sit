package it.emarolab.runnableSITutility.example;

import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.fuzzySIT.core.PlacedObject;
import it.emarolab.fuzzySIT.core.SITABox;
import it.emarolab.fuzzySIT.core.SITTBox;
import it.emarolab.fuzzySIT.core.axioms.SpatialRelation;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SpatialScenesExample {
    private static final Set<PlacedObject> objects = new HashSet<>(); // the set of objects in the SIT scene to test
    private static final Set<SpatialRelation> relations = new HashSet<>(); // the set of relations in the SIT scene to test

    // the name of the types of objects in this example (π)
    private static final String CONE = "Cone";
    private static final String CYLINDER = "Cylinder";
    private static final String PLANE = "Plane";
    private static final String SPHERE = "Sphere";
    // the name of the spatial relations used in this example (ζ)
    private static final String RIGHT = "isRightOf";
    private static final String FRONT = "isFrontOf";

    // it is called before to format the scene. The input is the number of objects in the scene
    private static void clear(){
        objects.clear();
        relations.clear();
    }

    private static void defineObject(String id, double x, double y, ObjectType... types){
        x = spatialNoise(x);
        y = spatialNoise(y);
        for (ObjectType t : types){
            objects.add(new PlacedObject(t.getType(), id, x, y, fuzzyNoise(t.getDegree())));
        }
    }

    private static void formatS1(){
        clear();
        defineObject("g1", 1.492, 0.146,
                new ObjectType(CONE, .3),
                new ObjectType(CYLINDER, .9));
        defineObject("g2", 1.712, -0.096,
                new ObjectType(CONE, .8),
                new ObjectType(CYLINDER, .4));
        defineObject("g3", 1.371, -0.350,
                new ObjectType(PLANE, .7));
        addSpatialRelations();
    }

    private static void formatS2(){
        clear();
        defineObject("g4", 1.498, 0.145,
                new ObjectType(CONE, .4),
                new ObjectType(CYLINDER, .8));
        defineObject("g5", 1.375, -0.348,
                new ObjectType(PLANE, .8));
        addSpatialRelations();
    }

    private static void formatS3(){
        clear();
        defineObject("g6", 1.478, 0.339,
                new ObjectType(CONE, .1),
                new ObjectType(CYLINDER, .7));
        defineObject("g7", 1.278, 0.251,
                new ObjectType(SPHERE, .8));
        defineObject("g8", 1.609, 0.206,
                new ObjectType(SPHERE, .9));
        defineObject("g9", 1.493, 0.143,
                new ObjectType(CONE, .2),
                new ObjectType(CYLINDER, .7));
        defineObject("g10", 1.717, -0.091,
                new ObjectType(CONE, .9),
                new ObjectType(CYLINDER, .3));
        defineObject("g11", 1.368, -0.349,
                new ObjectType(PLANE, .8));
        defineObject("g12", 1.486, -0.544,
                new ObjectType(CONE, .2),
                new ObjectType(CYLINDER, .8));
        defineObject("g13", 1.741, -0.518,
                new ObjectType(SPHERE, 1));
        addSpatialRelations();
    }

    private static void formatS4(){
        clear();
        defineObject("g14", 1.284, 0.084,
                new ObjectType(SPHERE, .9));
        defineObject("g15", 1.518, -0.107,
                new ObjectType(SPHERE, .8));
        defineObject("g16", 1.717, -0.311,
                new ObjectType(SPHERE, 1));
        addSpatialRelations();
    }

    private static void formatS5(){
        clear();
        defineObject("g17", 1.278, 0.096,
                new ObjectType(SPHERE, .8));
        defineObject("g18", 1.596, -0.173,
                new ObjectType(SPHERE, 1));
        defineObject("g19", 1.709, -0.321,
                new ObjectType(SPHERE, .9));
        addSpatialRelations();
    }

    private static void addSpatialRelations(){
        for(PlacedObject o1: objects){
            for(PlacedObject o2: objects){
                if(!o1.getObject().equals(o2.getObject())){
                    Set<SpatialRelation> relSet = o1.getRelations(o2);
                    for(SpatialRelation r: relSet){
                        if(r.getRelation().contains(RIGHT) || r.getRelation().contains(FRONT)){
                            if(r.getDegree() > 0){
                                relations.add(r);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void testScene(int identifier, SITTBox h){
        String log = "";
        log += "-------------------------  S" + identifier + "   ------------------------\n";
        SITABox r = new SITABox(h, objects, relations);
        log += "Objects:   " + objects + "\n";
        log += "Relations: " + relations + "\n";
        log += "------------------------- learn ------------------------\n";
        // learn S1
        h.learn( "Scene" + identifier, r);
        log += "Sigma Counters: " + r.getDefinition() + "\n";
        log += "--------------------------------------------------------\n";
        System.out.println(log);
    }

    private static void testScenes(double fuzziness){
        SITTBox onto = new SITTBox(FuzzySITBase.RESOURCES_PATH + "ontologies/Simple-RANSAC-Scene.fuzzydl");
        onto.setFuzziness(fuzziness * 100);
        int identifier = 1;
        formatS1();
        testScene(identifier, onto);
        identifier += 1;
        formatS2();
        testScene(identifier, onto);
        identifier += 1;
        formatS3();
        testScene(identifier, onto);
        identifier += 1;
        formatS4();
        testScene(identifier, onto);
        identifier += 1;
        formatS5();
        testScene(identifier, onto);
        onto.show();
    }

    public static void main(String[] args) {
        testScenes(.3);
        System.out.println("################################################################");
        testScenes(.7);
        System.out.println("################################################################");
    }

    private static final double SPATIAL_MIN_NOISE = 0.010; // in meters
    private static final double SPATIAL_MAX_NOISE = 0.100; // in meters
    private static final double FUZZY_MIN_NOISE = 0.2; // in [0,1]
    private static final double FUZZY_MAX_NOISE = 0.5; // in [0,1]
    private static double spatialNoise(double degree) {// random with no 0 average
        return noise(degree, SPATIAL_MAX_NOISE, SPATIAL_MIN_NOISE);
    }
    private static double fuzzyNoise(double degree) {// random with no 0 average
        double noised = noise(degree, FUZZY_MAX_NOISE, FUZZY_MIN_NOISE);
        if (noised <= 0)
            return 0.001;
        if (noised >= 1)
            return 1;
        else return noised;
    }
    private static double noise(double degree, double max, double min) {// random with no 0 average
        Random rand = new Random();
        double rangeMax = min + (max - min) * rand.nextDouble();
        rand = new Random();
        double rangeMin = -1 * (min + (max - min) * rand.nextDouble());
        rand = new Random();
        double n = rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
        return degree + n;
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
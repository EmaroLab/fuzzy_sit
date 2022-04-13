package it.emarolab.runnableSITutility.example;

import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.fuzzySIT.core.hierarchy.SceneHierarchyEdge;
import it.emarolab.fuzzySIT.core.hierarchy.SceneHierarchyVertex;
import it.emarolab.runnableSITutility.sceneRecustructor.MonteCarloInterface;
import it.emarolab.fuzzySIT.core.SITABox;
import it.emarolab.fuzzySIT.core.SITTBox;
import it.emarolab.fuzzySIT.core.axioms.SpatialObject;
import it.emarolab.fuzzySIT.core.axioms.SpatialRelation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * The testing class for {@link SITTBox} and {@link SITABox}.
 * <p>
 *     This example show a possible scenario based on the scenes and hierarchies shown in the paper.
 *
 * <div style="text-align:center;"><small>
 * <b>File</b>:        it.emarolab.SITutility.example.SceneHierarchyTest <br>
 * <b>Licence</b>:     GNU GENERAL PUBLIC LICENSE. Version 3, 29 June 2007 <br>
 * <b>Author</b>:      Buoncompagni Luca (luca.buoncompagni@edu.unige.it) <br>
 * <b>affiliation</b>: EMAROLab, DIBRIS, University of Genoa. <br>
 * <b>date</b>:        27/06/17 <br>
 * </small></div>
 *
 * @see MonteCarloInterface
 */
public class SceneHierarchyTest {

    public static Set<SpatialObject> objects = new HashSet<>(); // the set of objects in the SIT scene to test
    public static Set<SpatialRelation> relations = new HashSet<>(); // the set of relations in the SIT scene to test
    public static int objectcount; // the counter of the amount of objects in the SIT scene to test

    // the name of the types of objects in this example (π)
    public static final String CONE = "Cone";
    public static final String CYLINDER = "Cylinder";
    public static final String PLANE = "Plane";
    public static final String SPHERE = "Sphere";
    // the name of the spatial relations used in this example (ζ)
    public static final String RIGHT = "isRightOf";
    public static final String FRONT = "isFrontOf";

    // it is called before to format the scene. The input is the number of objects in the scene
    private static void clear( int cnt){
        objects.clear();
        relations.clear();
        objectcount = cnt;
    }

    private static void formatS1() {
        clear(3);

        objects.add(new SpatialObject(CYLINDER, "g1", fuzzyNoise(.8)));
        objects.add(new SpatialObject(CONE, "g1", fuzzyNoise(.2)));

        objects.add(new SpatialObject(CONE, "g2", fuzzyNoise(.7)));
        objects.add(new SpatialObject(CYLINDER, "g2", fuzzyNoise(.2)));

        objects.add(new SpatialObject(PLANE, "g3", fuzzyNoise(.9)));
        objects.add(new SpatialObject(CYLINDER, "g3", fuzzyNoise(.08))); // ??
        objects.add(new SpatialObject(SPHERE, "g3", fuzzyNoise(.08))); // ??

        relations.add(new SpatialRelation("g1", RIGHT, "g2", fuzzyNoise(.65)));
        relations.add(new SpatialRelation("g1", RIGHT, "g3", fuzzyNoise(.88)));

        relations.add(new SpatialRelation("g1", FRONT, "g3", fuzzyNoise(.1)));

        relations.add(new SpatialRelation("g2", RIGHT, "g3", fuzzyNoise(.48)));

        relations.add(new SpatialRelation("g2", FRONT, "g1", fuzzyNoise(.74)));
        relations.add(new SpatialRelation("g2", FRONT, "g3", fuzzyNoise(.77)));
    }

    private static void formatS2(){
        clear(2);

        objects.add( new SpatialObject( CYLINDER, "g4", fuzzyNoise(.9)));
        objects.add( new SpatialObject( CONE, "g4", fuzzyNoise(.2)));

        objects.add( new SpatialObject( PLANE, "g5", fuzzyNoise(.8)));
        objects.add(new SpatialObject(CYLINDER, "g5", fuzzyNoise(.08))); // ??
        objects.add(new SpatialObject(SPHERE, "g5", fuzzyNoise(.08))); // ??

        relations.add( new SpatialRelation( "g4", RIGHT, "g5", fuzzyNoise(.93)));
        relations.add( new SpatialRelation( "g4", FRONT, "g5", fuzzyNoise(.11)));
    }

    private static void formatS3(){
        clear(8);

        objects.add( new SpatialObject( CYLINDER, "g6", fuzzyNoise(.7)));
        objects.add( new SpatialObject( CONE, "g6", fuzzyNoise(.1)));

        objects.add( new SpatialObject( SPHERE, "g7", fuzzyNoise(.93)));
        objects.add(new SpatialObject(CYLINDER, "g7", fuzzyNoise(.08))); // ??

        objects.add( new SpatialObject( SPHERE, "g8", fuzzyNoise(.87)));
        objects.add(new SpatialObject(PLANE, "g8", fuzzyNoise(.08))); // ??

        objects.add( new SpatialObject( CYLINDER, "g9", fuzzyNoise(.82))); // * union with scene 1 (ε1: 0.8)
        objects.add( new SpatialObject( CONE, "g9", fuzzyNoise(.19))); // * (ε1: 0.2)

        objects.add( new SpatialObject( CONE, "g10", fuzzyNoise(.7))); // * (ε1: 0.7)
        objects.add( new SpatialObject( CYLINDER, "g10", fuzzyNoise(.24))); // * (ε1: 0.2)

        objects.add( new SpatialObject( PLANE, "g11", fuzzyNoise(.88))); // * (ε1: 0.9)

        objects.add( new SpatialObject( CYLINDER, "g12", fuzzyNoise(.78)));
        objects.add( new SpatialObject( CONE, "g12", fuzzyNoise(.18)));
        objects.add(new SpatialObject(SPHERE, "g12", fuzzyNoise(.08))); // ??

        objects.add( new SpatialObject( SPHERE, "g13", fuzzyNoise(.92)));
        objects.add(new SpatialObject(CYLINDER, "g13", fuzzyNoise(.08))); // ??

        relations.add( new SpatialRelation( "g6", RIGHT, "g7", fuzzyNoise(.56)));
        relations.add( new SpatialRelation( "g6", RIGHT, "g8", fuzzyNoise(.72)));
        relations.add( new SpatialRelation( "g6", RIGHT, "g9", fuzzyNoise(.99)));
        relations.add( new SpatialRelation( "g6", RIGHT, "g10", fuzzyNoise(.76)));
        relations.add( new SpatialRelation( "g6", RIGHT, "g11", fuzzyNoise(.68)));
        relations.add( new SpatialRelation( "g6", RIGHT, "g12", fuzzyNoise(.99)));
        relations.add( new SpatialRelation( "g6", RIGHT, "g13", fuzzyNoise(.73)));

        relations.add( new SpatialRelation( "g6", FRONT, "g7", fuzzyNoise(.86)));
        relations.add( new SpatialRelation( "g6", FRONT, "g9", fuzzyNoise(.09)));
        relations.add( new SpatialRelation( "g6", FRONT, "g11", fuzzyNoise(.18)));
        relations.add( new SpatialRelation( "g6", FRONT, "g12", fuzzyNoise(.11)));

        relations.add( new SpatialRelation( "g7", RIGHT, "g8", fuzzyNoise(.66)));
        relations.add( new SpatialRelation( "g7", RIGHT, "g9", fuzzyNoise(.91)));
        relations.add( new SpatialRelation( "g7", RIGHT, "g10", fuzzyNoise(.72)));
        relations.add( new SpatialRelation( "g7", RIGHT, "g11", fuzzyNoise(.81)));
        relations.add( new SpatialRelation( "g7", RIGHT, "g12", fuzzyNoise(.97)));
        relations.add( new SpatialRelation( "g7", RIGHT, "g13", fuzzyNoise(.69)));

        relations.add( new SpatialRelation( "g8", RIGHT, "g9", fuzzyNoise(.56)));
        relations.add( new SpatialRelation( "g8", RIGHT, "g10", fuzzyNoise(.79)));
        relations.add( new SpatialRelation( "g8", RIGHT, "g11", fuzzyNoise(.57)));
        relations.add( new SpatialRelation( "g8", RIGHT, "g12", fuzzyNoise(.83)));
        relations.add( new SpatialRelation( "g8", RIGHT, "g13", fuzzyNoise(.61)));

        relations.add( new SpatialRelation( "g8", FRONT, "g6", fuzzyNoise(.25)));
        relations.add( new SpatialRelation( "g8", FRONT, "g7", fuzzyNoise(.39)));
        relations.add( new SpatialRelation( "g8", FRONT, "g9", fuzzyNoise(.72)));
        relations.add( new SpatialRelation( "g8", FRONT, "g11", fuzzyNoise(.54)));
        relations.add( new SpatialRelation( "g8", FRONT, "g12", fuzzyNoise(.40)));

        relations.add( new SpatialRelation( "g9", RIGHT, "g10", fuzzyNoise(.68))); // * (ε1: 0.65)
        relations.add( new SpatialRelation( "g9", RIGHT, "g11", fuzzyNoise(.88))); // * (ε1: 0.88) !!!
        relations.add( new SpatialRelation( "g9", RIGHT, "g12", fuzzyNoise(.93)));
        relations.add( new SpatialRelation( "g9", RIGHT, "g13", fuzzyNoise(.70)));

        relations.add( new SpatialRelation( "g9", FRONT, "g7", fuzzyNoise(.47)));
        relations.add( new SpatialRelation( "g9", FRONT, "g11", fuzzyNoise(.06))); // * (ε1: 0.1)
        relations.add( new SpatialRelation( "g9", FRONT, "g12", fuzzyNoise(.13)));

        relations.add( new SpatialRelation( "g10", RIGHT, "g11", fuzzyNoise(.43))); // * (ε1: 0.48)
        relations.add( new SpatialRelation( "g10", RIGHT, "g12", fuzzyNoise(.76)));
        relations.add( new SpatialRelation( "g10", RIGHT, "g13", fuzzyNoise(.82)));

        relations.add( new SpatialRelation( "g10", FRONT, "g6", fuzzyNoise(.98)));
        relations.add( new SpatialRelation( "g10", FRONT, "g7", fuzzyNoise(.72)));
        relations.add( new SpatialRelation( "g10", FRONT, "g8", fuzzyNoise(.49)));
        relations.add( new SpatialRelation( "g10", FRONT, "g9", fuzzyNoise(.76))); // * (ε1: 0.74)
        relations.add( new SpatialRelation( "g10", FRONT, "g11", fuzzyNoise(.75))); // * (ε1: 0.77)
        relations.add( new SpatialRelation( "g10", FRONT, "g12", fuzzyNoise(.54)));

        relations.add( new SpatialRelation( "g11", RIGHT, "g12", fuzzyNoise(.83)));
        relations.add( new SpatialRelation( "g11", RIGHT, "g13", fuzzyNoise(.71)));

        relations.add( new SpatialRelation( "g11", FRONT, "g7", fuzzyNoise(.48)));

        relations.add( new SpatialRelation( "g12", FRONT, "g7", fuzzyNoise(.43)));
        relations.add( new SpatialRelation( "g12", FRONT, "g11", fuzzyNoise(.60)));

        relations.add( new SpatialRelation( "g13", RIGHT, "g12", fuzzyNoise(.07)));

        relations.add( new SpatialRelation( "g13", FRONT, "g6", fuzzyNoise(.37)));
        relations.add( new SpatialRelation( "g13", FRONT, "g7", fuzzyNoise(.48)));
        relations.add( new SpatialRelation( "g13", FRONT, "g8", fuzzyNoise(.35)));
        relations.add( new SpatialRelation( "g13", FRONT, "g9", fuzzyNoise(.52)));
        relations.add( new SpatialRelation( "g13", FRONT, "g10", fuzzyNoise(.46)));
        relations.add( new SpatialRelation( "g13", FRONT, "g11", fuzzyNoise(.77)));
        relations.add( new SpatialRelation( "g13", FRONT, "g12", fuzzyNoise(.99)));
    }

    private static void formatS4(){
        clear(3);

        objects.add( new SpatialObject( SPHERE, "g14", fuzzyNoise(.96)));
        objects.add( new SpatialObject( SPHERE, "g15", fuzzyNoise(.89)));
        objects.add( new SpatialObject( SPHERE, "g16", fuzzyNoise(.92)));

        objects.add(new SpatialObject(CYLINDER, "g14", fuzzyNoise(.08))); // ??
        objects.add(new SpatialObject(CONE, "g16", fuzzyNoise(.08))); // ??

        relations.add( new SpatialRelation( "g14", RIGHT, "g15", fuzzyNoise(.52)));
        relations.add( new SpatialRelation( "g14", RIGHT, "g16", fuzzyNoise(.49)));

        relations.add( new SpatialRelation( "g15", RIGHT, "g16", fuzzyNoise(.54)));

        relations.add( new SpatialRelation( "g15", FRONT, "g14", fuzzyNoise(.49)));

        relations.add( new SpatialRelation( "g16", FRONT, "g14", fuzzyNoise(.51)));
        relations.add( new SpatialRelation( "g16", FRONT, "g15", fuzzyNoise(.48)));
    }

    private static void formatS5(){
        clear(3);

        objects.add( new SpatialObject( SPHERE, "g17", fuzzyNoise(.86)));
        objects.add( new SpatialObject( SPHERE, "g18", fuzzyNoise(.87)));
        objects.add( new SpatialObject( SPHERE, "g19", fuzzyNoise(.97)));

        objects.add(new SpatialObject(CYLINDER, "g18", fuzzyNoise(.08))); // ??
        objects.add(new SpatialObject(CONE, "g19", fuzzyNoise(.08))); // ??

        relations.add( new SpatialRelation( "g17", RIGHT, "g18", fuzzyNoise(.58)));
        relations.add( new SpatialRelation( "g17", RIGHT, "g19", fuzzyNoise(.46)));

        relations.add( new SpatialRelation( "g18", RIGHT, "g19", fuzzyNoise(.53)));

        relations.add( new SpatialRelation( "g18", FRONT, "g17", fuzzyNoise(.51)));

        relations.add( new SpatialRelation( "g19", FRONT, "g17", fuzzyNoise(.58)));
        relations.add( new SpatialRelation( "g19", FRONT, "g18", fuzzyNoise(.56)));
    }

    private static String log = "";
    public static SITABox testScene(int identifier, SITTBox h){
        log += "-------------------------  S" + identifier + "   ------------------------\n";
        SITABox r = new SITABox(h, objects, relations);
        log += "Objects:   " + objects + "\n";
        log += "Relations: " + relations + "\n";
        log += "------------------------- learn ------------------------\n";
        // learn S1
        h.learn( "Scene" + identifier, r);
        log += "Sigma Counters: " + r.getDefinition() + "\n";
        log += "--------------------------------------------------------\n";
        return r;
    }

    /**
     * The main function to show how to use fuzzy SIT recognition and learning API.
     * To log the operation made by those call manipulate the logging flags
     * {@link it.emarolab.fuzzySIT.FuzzySITBase#FLAG_LOG_SHOW} and
     * {@link it.emarolab.fuzzySIT.FuzzySITBase#FLAG_LOG_VERBOSE}
     * @param args not used!
     */
    public static void main(String[] args) {
        String fileName = System.currentTimeMillis() + "";
        File file = new File(FuzzySITBase.FILE_HIERARCHY_LOG + fileName + ".log");
        BufferedWriter bw = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while(true) {
            log = "";

            // instantiate a T-Box with the default T-Box ontology and reasoner configuration file
            SITTBox hA = new SITTBox(FuzzySITBase.RESOURCES_PATH + "ontologies/RANSACscene.fuzzydl");
            hA.setFuzziness(30);
            SITTBox hB = new SITTBox(FuzzySITBase.RESOURCES_PATH + "ontologies/RANSACscene.fuzzydl");
            hB.setFuzziness(70);

            int identifier = 1;

            formatS1();
            testScene(identifier, hA);
            testScene(identifier, hB);
            identifier += 1;

            formatS2();
            testScene(identifier, hA);
            testScene(identifier, hB);
            identifier += 1;

            formatS3();
            testScene(identifier, hA);
            testScene(identifier, hB);
            identifier += 1;

            formatS4();
            testScene(identifier, hA);
            testScene(identifier, hB);
            identifier += 1;

            formatS5();
            testScene(identifier, hA);
            testScene(identifier, hB);

            try {
                double wA1 = getWeight("Scene3", "Scene1", hA);
                double wB1 = getWeight("Scene3", "Scene1", hB);

                double wA2 = getWeight("Scene1", "Scene2", hA);
                double wB2 = getWeight("Scene1", "Scene2", hB);

                /*double wA31 = getWeight("Scene4", "Scene5", hA);
                double wA32 = getWeight("Scene5", "Scene4", hA);
                double wB31 = getWeight("Scene4", "Scene5", hB);
                double wB32 = getWeight("Scene5", "Scene4", hB);
                double wA3 = Math.abs(wA31 - wA32);
                double wB3 = Math.abs(wB31 - wB32);*/

                log += " weightA1 " + wA1 + ", weightB1 " + wB1 + "\n";
                log += " weightA2 " + wA2 + ", weightB2 " + wB2 + "\n";
                //log += " weightA3 " + wA3 + ", weightB3 " + wB3 + "\n";
                log += "----------------------------------------------------------\n";

                bw.write(log);
                System.out.println(log);

                if(((wA1 < 1 && wB1 >= 1) || (wA2 < 1 && wB2 >= 1))){ // && (wA3 >= 0.4) && (wB3 >= 0.4)){
                    hA.show();
                    hB.show();
                    bw.close();
                    break;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static double getWeight(String sourceName, String targetName, SITTBox h){
        SceneHierarchyVertex source = null;
        SceneHierarchyVertex target = null;
        for(SceneHierarchyVertex v : h.getHierarchy().vertexSet()) {
            if(v.getScene().equals(sourceName)) // "Scene3"
                source = v;
            if(v.getScene().equals(targetName)) // "Scene1"
                target = v;
        }
        SceneHierarchyEdge edge = h.getHierarchy().getEdge(source, target);
        return h.getHierarchy().getEdgeWeight(edge);
    }


    private static final Double MIN_NOISE = 0.08; // Set to null to avoid randomness
    private static final Double MAX_NOISE = 0.28; // Set to null to avoid randomness
    private static double fuzzyNoise( double degree) {
        if( MIN_NOISE == null || MAX_NOISE == null)
            return degree;
        double rangeMax = random(MIN_NOISE, MAX_NOISE);
        if(random(0,10) > 5)
            rangeMax *= -1;
        double noised = degree + rangeMax;
        double toReturn;
        if (noised <= 0)
            toReturn = degree;
        else if (noised >= 1)
            toReturn = 1;
        else toReturn = noised;
        //log += "noising " + degree + " to " + toReturn + " diff: " + (degree - toReturn) + "\n";
        return toReturn;
    }
    private static double random(double rangeMin, double rangeMax){
        Random rand = new Random();
        return rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
    }
}

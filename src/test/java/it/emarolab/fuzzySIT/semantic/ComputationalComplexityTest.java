package it.emarolab.fuzzySIT.semantic;

import it.emarolab.fuzzySIT.semantic.axioms.SpatialObject;
import it.emarolab.fuzzySIT.semantic.axioms.SpatialRelation;
import it.emarolab.fuzzySIT.semantic.hierarchy.SceneHierarchyVertex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// Logs are based on "A_B_C_D" where:
//    - A is the number of elements in each scene
//    - B is the number of tested scenes
//    - C is the number of element types in the ontology
//    - D is the number of relations in the ontology
public class ComputationalComplexityTest {

    public static Set<SpatialObject> objects = new HashSet<>(); // the set of objects in the SIT scene to test
    public static Set<SpatialRelation> relations = new HashSet<>(); // the set of relations in the SIT scene to test

    // the name of the types of objects in this example (π)
    public static final List<String> ALL_TYPES = new ArrayList<String>() {{ add("Move");add("Stop"); // at least 2
        add("Enter");add("Exit");add("Cross");add("Grub");}};
    // the name of the spatial relations used in this example (ζ)
    public static final List<String> ALL_RELATIONS = new ArrayList<String>() {{add("isAfter");add("isBefore"); //at least 2
        add("isOverlap");add("isInclude");add("isShort");add("isLong");}};

    public static final String BASE_PATH = "src/test/resources/computationComplexityTest/";

    // computation time evaluation span
    public static final List<Integer> NUMBER_OF_ELEMENTS = new ArrayList<Integer>() {{add(17);}};
    public static final List<Integer> NUMBER_OF_SCENES = new ArrayList<Integer>() {{add(18);add(18);}};

    private static FileWriter createFile(String logPath, int numberOfElement, int numberOfScenes,
                                         List<String> possibleTypes, List<String> possibleRelation){
        FileWriter writer = null;
        try {
            Format formatter = new SimpleDateFormat("dd-MM-yy_hh-mm-ss");
            File file = new File(logPath + formatter.format(new Date()) + "(" + numberOfElement + "_" + numberOfScenes
                    + "_" + possibleTypes.size() + "_" + possibleRelation.size() + ").log");
            boolean created = file.getParentFile().mkdirs();
            writer = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    public static double randomDegree() {
        return ThreadLocalRandom.current().nextDouble(0.0, 1.0);
    }
    public static int randomIdx(int max) {
        return ThreadLocalRandom.current().nextInt(0, max);
    }

    public static void log(FileWriter writer, String contents){
        try {
            System.out.println(contents);
            String l = contents + System.lineSeparator();
            writer.append(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void create_scene(int numberOfElements, List<String> possibleTypes, List<String> possibleRelation) {
        objects.clear();
        relations.clear();

        String lastElementName = null;
        for (int t = 0; t < numberOfElements; t++) {
            String elementName = "I" + t;
            String type = possibleTypes.get(randomIdx(possibleTypes.size()));
            SpatialObject element = new SpatialObject(type, elementName, randomDegree());
            if (lastElementName != null) {
                double degree = randomDegree();
                relations.add(new SpatialRelation(elementName, possibleRelation.get(0), lastElementName, degree));
                relations.add(new SpatialRelation(lastElementName, possibleRelation.get(1), elementName, 1-degree));

                if( possibleRelation.size() > 2 && randomIdx(2) == 0) {
                    String randomRelation = possibleRelation.get(2 + randomIdx(possibleRelation.size() - 2));
                    relations.add(new SpatialRelation(elementName, randomRelation, elementName, randomDegree()));
                }
            }
            lastElementName = elementName;
            objects.add(element);
        }
    }

    private static void testComputation(String ontologyPath, String logPath, int numberOfElements, int numberOfScenes,
                                        List<String> possibleTypes, List<String> possibleRelation) {
        FileWriter writer = createFile(logPath, numberOfElements, numberOfScenes, possibleTypes, possibleRelation);
        List<CSVLog> csvLogs = new ArrayList<>();
        long totTime = 0;
        SITTBox h = new SITTBox(ontologyPath);

        log(writer, "-------------------------  SUMMARY ------------------------");
        log(writer, "Number of elements in a scene: " + numberOfElements + ".");
        log(writer, "Number of scenes: " + numberOfScenes + ".");
        log(writer, "Elements in the ontology (size=" + possibleTypes.size() + "): " + possibleTypes + ".");
        log(writer, "Relations in the ontology (size=" + possibleRelation.size() + "): " + possibleRelation + ".");
        long start_time = System.currentTimeMillis();
        log(writer, "Starting Time: " + start_time);

        // instantiate a T-Box with the default T-Box ontology and reasoner configuration file
        for (int t = 0; t < numberOfScenes; t++) {
            log(writer, "-------------------------  S" + t + "   ------------------------ " +
                    "(at " + System.currentTimeMillis() + " in " + logPath + ")");
            // create S1 and recognise it
            create_scene(numberOfElements, possibleTypes, possibleRelation);
            log(writer, "New scene elements (size: " + objects.size() + ") "  + objects);
            log(writer, "New scene relations (size: " + relations.size() + ") "  + relations);

            // recognise before
            long tsR1 = System.currentTimeMillis();
            SITABox r1 = new SITABox(h, objects, relations);
            Map<SceneHierarchyVertex, Double> rec1 = r1.getRecognitions();
            long recCompSec1 = (System.currentTimeMillis()-tsR1);
            log(writer, "RECOGNISED Before Learning in " + recCompSec1 + "ms as: " + rec1);

            // learn
            long tsL = System.currentTimeMillis();
            SceneHierarchyVertex s = h.learn("Scene" + t, r1);
            long learnCompSec = (System.currentTimeMillis() - tsL);
            log(writer, "LEARNED in " + learnCompSec + "ms with: " + s.getDefinition());

            // recognise after
            long tsR2 = System.currentTimeMillis();
            SITABox r2 = new SITABox(h, objects, relations);
            Map<SceneHierarchyVertex, Double> rec2 = r2.getRecognitions();
            long recCompSec2 = (System.currentTimeMillis()-tsR2);
            log(writer, "RECOGNISED After Learning in " + recCompSec2 + "ms as: " + rec2);

            csvLogs.add(new CSVLog(t+1, recCompSec1, learnCompSec, recCompSec2));
        }

        log(writer, "-------------------------  CSV ------------------------");
        boolean addHeather = true;
        for( CSVLog l: csvLogs) {
            if( addHeather) {
                log(writer, l.getHeather());
                addHeather = false;
            }
            totTime += l.recognitionBefore + l.learning;
            log(writer, l.toString());
        }

        long endTime = System.currentTimeMillis();
        long computationTime = endTime-start_time;
        long otherTime = computationTime - totTime;
        log(writer, "End Time: " + endTime + "  (duration: " + (computationTime/1000.0) + "sec)" +
                " (time not spent for recognising and learning: " + (otherTime/1000.0) + "sec).");
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void simulate(List<String> possibleTypes, List<String> possibleRelations) {
        String simulationTag = possibleTypes.size() + "_" + possibleRelations.size();
        String ontologyPath = BASE_PATH + "ontologies/computation_tesT_SIT_kb_" + simulationTag + ".fuzzydl";
        String logPath = BASE_PATH + "log/x_x_" + simulationTag + "/";

        for (Integer e : NUMBER_OF_ELEMENTS) {
            System.err.println("Testing elements # " + e);
            for (Integer s: NUMBER_OF_SCENES) {
                System.err.println("Testing scene # " + s);
                testComputation(ontologyPath, logPath, e, s, possibleTypes, possibleRelations);
            }
        }
    }

    public static void main(String[] args) {
        List<String> possibleTypes = new ArrayList<>();
        possibleTypes.add(ALL_TYPES.get(0)); possibleTypes.add(ALL_TYPES.get(1));
        // TODO uncomment one line at a time and run them in parallel to log all configurations
        possibleTypes.add(ALL_TYPES.get(2)); possibleTypes.add(ALL_TYPES.get(3));
        //possibleTypes.add(ALL_TYPES.get(4)); possibleTypes.add(ALL_TYPES.get(5));

        List<String> possibleRelations = new ArrayList<>();
        possibleRelations.add(ALL_RELATIONS.get(0));possibleRelations.add(ALL_RELATIONS.get(1));
        /*try {
            simulate(possibleTypes, possibleRelations);
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("ER(A): with " + possibleTypes + "\n" + possibleRelations);
        }*/

        possibleRelations.add(ALL_RELATIONS.get(2));possibleRelations.add(ALL_RELATIONS.get(3));
        /*try {
            simulate(possibleTypes, possibleRelations);
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("ER(B): with " + possibleTypes + "\n" + possibleRelations);
        }*/

        possibleRelations.add(ALL_RELATIONS.get(4));possibleRelations.add(ALL_RELATIONS.get(5));
        try {
            simulate(possibleTypes, possibleRelations);
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("ER(C): with " + possibleTypes + "\n" + possibleRelations);
        }

        System.err.println("DONE!");
    }
}

class CSVLog{
    long recognitionBefore, learning, recognitionAfter;
    int idx;
    public CSVLog(int idx, long recognitionBefore, long learning, long recognitionAfter){
        this.idx = idx;
        this.recognitionBefore = recognitionBefore;
        this.learning = learning;
        this.recognitionAfter = recognitionAfter;
    }

    public String getHeather(){
        return "Number of scenes, " +
                "Recognition Time Before Learning, (a recognition is not assured), " +
                "Learning Time, " +
                "Recognition Time After Learning (a recognition is assured); [ms]";
    }

    @Override
    public String toString() {
        return idx + ", " + recognitionBefore +", " + learning + ", " + recognitionAfter + ";";
    }
}

package it.emarolab.runnableSITutility.complexity;

import com.sun.management.ThreadMXBean;
import fuzzydl.KnowledgeBase;
import it.emarolab.fuzzySIT.core.SITABox;
import it.emarolab.fuzzySIT.core.SITTBox;
import it.emarolab.fuzzySIT.core.axioms.SpatialObject;
import it.emarolab.fuzzySIT.core.axioms.SpatialRelation;
import it.emarolab.fuzzySIT.core.hierarchy.SceneHierarchyVertex;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

// Logs are based on "A_B" where:
//    - A is the number of element types in the ontology (i.e., concepts)
//    - B is the number of relations in the ontology
// Each combination among A and B with a given step is evaluated.
// Each evaluation is made for all provided number of elements and scenes
// (see `resources/computationComplexity/log/.../main.log` for more)
public class ComputationalComplexityTest {

    private static int tasksBufferSize = 10;

    public static void main(String[] args) {
        // prepare for logging
        Logger mainLogger = new Logger("main", true);
        System.out.println("Logging in: " + Logger.RELATIVE_PATH);

        mainLogger.log("-------------------- PARAMETERS --------------------------");
        // understand the parameter
        RawParameters rawParam = Parameter.getRawParam(args);
        tasksBufferSize = rawParam.taskBufferSize;
        int elementsSize = rawParam.getElements().size();
        int sceneSize = rawParam.getScenes().size();
        mainLogger.log("Running test with specification: " + rawParam);

        // generate a family of incremental parameters
        List<Parameter> params = Parameter.incrementalParam(rawParam, OntologyCreator.CONCEPTS_NAMES, OntologyCreator.RELATION_NAMES);
        mainLogger.log("I am going to do " + params.size() + "*" + elementsSize + "*" + sceneSize + "="
                + (params.size() * elementsSize * sceneSize) + " tests with parameters:");
        for (int i = 0; i < params.size(); i++)
            mainLogger.log("  " + (i + 1) + ".*(" + elementsSize + "." + sceneSize + "). " + params.get(i));

        mainLogger.log("------------------- CSV SETTING ---------------------------");
        mainLogger.log(CSVData.getHeader());

        // perform all tests
        mainLogger.log("------------------- TESTING ---------------------------");
        AtomicInteger globalCnt = new AtomicInteger(0);
        List<SimulationTask> tasks = Collections.synchronizedList(new ArrayList<>());
        for (Parameter p : params) {
            mainLogger.log(globalCnt.incrementAndGet() + ". Start new thread for <" + p.getTestLabel() + "> test");

            // do not launch too many tasks at the same time
            waitBuffer(tasks,mainLogger);

            // launch task
            try {
                SimulationTask task = new SimulationTask(globalCnt.get(), p, tasks, mainLogger);
                Thread t = new Thread(task);
                task.setThread(t);
                t.start();
                tasks.add(task);
            } catch (Exception e){
                e.printStackTrace();
                Logger.logError(e, "ERROR on main function");
            }
        }

        // wait the end of all tasks
        waitEnd(tasks, mainLogger);
        mainLogger.log("------------------- DONE! ---------------------------");

        mainLogger.close();
        Logger.csvClose();
    }

    public static void waitBuffer(List<SimulationTask> tasks, Logger mainLogger){
        boolean log = true;
        while (tasks.size() > tasksBufferSize) {
            try {
                Thread.sleep(500);
                if (log) {
                    mainLogger.log("Waiting for the end of a tasks since the thread buffer is full");
                    log = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Logger.logError(e, "Error on main function while waiting");
            }
        }
    }
    public static void waitEnd(List<SimulationTask> tasks, Logger mainLogger){
        boolean log = true;
        while (!tasks.isEmpty()) {
            try {
                Thread.sleep(500);
                if (log) {
                    mainLogger.log("Waiting for the end of all tasks.");
                    log = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Logger.logError(e, "Error on main function while waiting for ending tasks");
            }
        }
    }
}

class SimulationTask implements Runnable {

    private final List<SimulationTask> tasks;
    private final int globalCnt;
    private final Parameter parameter;
    private final Logger logger, mainLogger;

    private final Set<SpatialObject> objects = new HashSet<>(); // the set of objects in the SIT scene to test
    private final Set<SpatialRelation> relations = new HashSet<>(); // the set of relations in the SIT scene to test
    private Thread thread; // the thread where `this` is running


    public SimulationTask(int globalCnt, Parameter parameter, List<SimulationTask> tasks, Logger mainLogger) {
        this.globalCnt = globalCnt;
        this.parameter = parameter;
        this.tasks = tasks;
        this.logger = new Logger(parameter.getTestLabel(), false);
        this.mainLogger = mainLogger;
    }

    private static String configureRelation(String relationName){
        return "is" + relationName;
    }

    private void create_scene(int numberOfElements, List<String> possibleTypes, List<String> possibleRelation) {
        objects.clear();
        relations.clear();

        String lastElementName = null;
        for (int t = 0; t < numberOfElements; t++) {
            String elementName = "I" + t;
            String type = possibleTypes.get(randomIdx(possibleTypes.size()));
            SpatialObject element = new SpatialObject(type, elementName, randomDegree());
            if (lastElementName != null) {
                double degree = randomDegree();
                relations.add(new SpatialRelation(elementName, configureRelation(possibleRelation.get(0)), lastElementName, degree));
                relations.add(new SpatialRelation(lastElementName, configureRelation(possibleRelation.get(1)), elementName, 1 - degree));

                if (possibleRelation.size() > 2 && randomIdx(2) == 0) {
                    String randomRelation = possibleRelation.get(2 + randomIdx(possibleRelation.size() - 2));
                    relations.add(new SpatialRelation(elementName, configureRelation(randomRelation), elementName, randomDegree()));
                }
            }
            lastElementName = elementName;
            objects.add(element);
        }
    }

    private void simulate(int numberOfElements, int numberOfScenes, String testIdx){
        try {
            long totTime = 0;
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 1000));
            SITTBox h = new SITTBox(parameter.getOntology(), tasks);

            logger.log("----------------------  " + testIdx + "NEW TEST SUMMARY ---------------------");
            logger.log("Number of elements in a scene: " + numberOfElements + ".");
            logger.log("Number of scenes: " + numberOfScenes + ".");
            logger.log("Elements in the ontology (size=" + parameter.getConcepts().size() + "): " + parameter.getConcepts() + ".");
            logger.log("Relations in the ontology (size=" + parameter.getRelations().size() + "): " + parameter.getRelations() + ".");
            long start_time = System.currentTimeMillis();
            logger.log("Starting Time: " + start_time);

                // instantiate a T-Box with the default T-Box ontology and reasoner configuration file
            for (int t = 0; t < numberOfScenes; t++) {
                long st = System.currentTimeMillis();
                logger.log( "-------------------------  S" + t + "   ------------------------ ");
                // create S1 and recognise it
                create_scene(numberOfElements, parameter.getConcepts(), parameter.getRelations());
                logger.log("New scene elements (size: " + objects.size() + ") " + objects);
                logger.log("New scene relations (size: " + relations.size() + ") " + relations);

                // Pre encode (i.e., before learning)
                SITABox rPre = new SITABox(h, objects, relations);
                Map<SceneHierarchyVertex, Double> preRec = rPre.getRecognitions();
                logger.log("ENCODE before learning in " +  rPre.getEncodingTime() + "ms");
                logger.log("RECOGNISED before Learning in " + rPre.getRecognitionTime() + "ms as: " + preRec);

                // learning
                String newSceneName = "Scene" + t;
                long refTime = System.currentTimeMillis();
                SceneHierarchyVertex s = h.rawLearning(newSceneName, rPre);
                long learnTime = System.currentTimeMillis() - refTime;
                logger.log("LEARNED in " + learnTime + "ms with: " + s.getDefinition());
                // structuring
                KnowledgeBase kb = h.closeReopen(newSceneName, rPre, tasks); // solve FuzzyDL bug
                refTime = System.currentTimeMillis();
                h.updateEdges(kb); // structuring
                long structuringTime = System.currentTimeMillis() - refTime;
                logger.log("STRUCTURED in " + structuringTime + "ms");

                // Post encode (i.e., before learning)
                SITABox rPost = new SITABox(h, objects, relations);
                Map<SceneHierarchyVertex, Double> postRec = rPost.getRecognitions();
                logger.log("ENCODE after learning in " + rPost.getEncodingTime() + "ms");
                logger.log("RECOGNISED after Learning in " + rPost.getRecognitionTime() + "ms as: " + postRec);

                // store memory usage of this thread (it is an estimation)
                long memory = ((ThreadMXBean) ManagementFactory.getThreadMXBean()).getThreadAllocatedBytes(thread.getId());
                logger.log("used memory " + memory/1048576 + "MB");

                long total_time = System.currentTimeMillis() - st;
                Logger.csvWrite(new CSVData(parameter.getTestLabel(), parameter.getConcepts().size(),
                        parameter.getRelations().size(), objects.size(), relations.size(), t+1,
                        rPre.getEncodingTime(), rPre.getRecognitionTime(), learnTime, structuringTime,
                        rPost.getEncodingTime(), rPost.getRecognitionTime(), total_time, memory));
                Logger.csvFlush();
                logger.flush();
            }

            logger.log( "-------------------------- ENDING  ------------------------ ");
            long endTime = System.currentTimeMillis();
            long computationTime = endTime - start_time;
            long otherTime = computationTime - totTime;
            logger.log("End Time: " + endTime + "  (duration: " + (computationTime / 1000.0) + "sec)" +
                    " (time not spent for recognising and learning: " + (otherTime / 1000.0) + "sec).\n");
        } catch (Exception e){
            e.printStackTrace();
            Logger.logError(e, "Error on test " + parameter.getTestLabel());
        }
    }

    public void run() {
        int localCnt1 = 0;

        for (Integer scene : parameter.getScenes()) {
            ++localCnt1;
            int localCnt2 = 0;
            for (Integer elements : parameter.getElements()) {
                try {
                    mainLogger.log(globalCnt + "." + localCnt1 + "." + ++localCnt2 + ". " +
                            "Simulating " + parameter.getOntology() + " with " +
                            scene + " scenes and " + elements + " elements");

                    simulate(elements, scene, globalCnt + "." + localCnt1 + "." + localCnt2 + ". ");
                }catch (Exception e){
                    e.printStackTrace();
                    Logger.logError(e, "Error on test " + parameter.getTestLabel());
                }
            }
        }
        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory() - runtime.freeMemory(); // Calculate the used memory by the entire process
        mainLogger.log("Simulation " + globalCnt + "." + ".x..x" + " ended. The entire process used " + memory/1048576 + "MB");
        runtime.gc(); // suggests the JSM to run the garbage collector

        logger.close();
        synchronized (tasks) {
            tasks.remove(this);
        }
    }

    public static double randomDegree() {
        return ThreadLocalRandom.current().nextDouble(0.0, 1.0);
    }

    public static int randomIdx(int max) {
        return ThreadLocalRandom.current().nextInt(0, max);
    }

    public void setThread(Thread t) {
        this.thread = t;
    }
}




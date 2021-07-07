package it.emarolab.runnableSITutility.complexity;

import it.emarolab.fuzzySIT.core.SITABox;
import it.emarolab.fuzzySIT.core.SITTBox;
import it.emarolab.fuzzySIT.core.axioms.SpatialObject;
import it.emarolab.fuzzySIT.core.axioms.SpatialRelation;
import it.emarolab.fuzzySIT.core.hierarchy.SceneHierarchyVertex;

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
                new Thread(task).start();
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
            SITTBox h;
            synchronized (tasks){
                h = new SITTBox(parameter.getOntology());
            }

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

                // recognise before
                long tsR1 = System.currentTimeMillis();
                SITABox r1 = new SITABox(h, objects, relations);
                Map<SceneHierarchyVertex, Double> rec1 = r1.getRecognitions();
                long preRecTime = (System.currentTimeMillis() - tsR1);
                logger.log("RECOGNISED Before Learning in " + preRecTime + "ms as: " + rec1);

                // learn
                long tsL = System.currentTimeMillis();
                SceneHierarchyVertex s;
                synchronized (tasks) {
                    s = h.learn("Scene" + t, r1);
                }
                long learnTime = (System.currentTimeMillis() - tsL);
                logger.log("LEARNED in " + learnTime + "ms with: " + s.getDefinition());

                // recognise after
                long tsR2 = System.currentTimeMillis();
                SITABox r2 = new SITABox(h, objects, relations);
                Map<SceneHierarchyVertex, Double> rec2 = r2.getRecognitions();
                long postRecTime = (System.currentTimeMillis() - tsR2);
                logger.log("RECOGNISED After Learning in " + postRecTime + "ms as: " + rec2);

                //Logger.csvWrite(new CSVData(t + 1, preRecTime, learnTime, postRecTime));
                long total_time = System.currentTimeMillis() - st;
                Logger.csvWrite(new CSVData(parameter.getTestLabel(), parameter.getConcepts().size(),
                        parameter.getRelations().size(), objects.size(), relations.size(), t+1,
                        preRecTime, learnTime, postRecTime, total_time));
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
            Logger.logError(e, "Erroro on test " + parameter.getTestLabel());
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
        mainLogger.log("Simulation " + globalCnt + "." + ".x..x" + " ended");

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
}




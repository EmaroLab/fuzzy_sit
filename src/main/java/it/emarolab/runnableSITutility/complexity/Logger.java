package it.emarolab.runnableSITutility.complexity;


import it.emarolab.fuzzySIT.FuzzySITBase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class CSVData{
    private final String testTag;
    private final Integer numberOfElements, numberOfRoles, numberOfScenes, numberOfConcepts, numberOfRelations;
    private final long preRecognitionTime, learningTime, postRecognitionTime, idx, totalTime;

    public static String getHeader(){
        return "CSV data is all express in millisecond or integer (i.e., indices) and arranged as: `A,B,C,D,E,F,G,H,I` where \n" +
                "\t A) An ordered identified based on the creation timestamp,\n" +
                "\t B) A string identifying the ontology complexity (i.e., `C-D`),\n" +
                "\t C) The Number of concepts in the ontology,\n" +
                "\t D) The Number of relations in the ontology,\n" +
                "\t E) Number of elements in the scene,\n" +
                "\t F) Number of roles in the scene,\n" +
                "\t G) Number of items in the memory,\n" +
                "\t H) The recognition time before having learned a new scene, \n" +
                "\t I) The learning time,\n" +
                "\t J) The recognition time after having learned a new scene," +
                "\t K) The total computation time.";
    }

    public CSVData(String testTag, Integer numberOfConcepts, Integer numberOfRelations, Integer numberOfElements,
                   Integer numberOfRoles, Integer numberOfScenes, long preRecognitionTime, long learningTime, long postRecognitionTime, long totalTime) {
        this.idx = System.currentTimeMillis();
        this.testTag = testTag;
        this.numberOfElements = numberOfElements;
        this.numberOfRoles = numberOfRoles;
        this.numberOfScenes = numberOfScenes;
        this.preRecognitionTime = preRecognitionTime;
        this.learningTime = learningTime;
        this.postRecognitionTime = postRecognitionTime;
        this.numberOfConcepts = numberOfConcepts;
        this.numberOfRelations = numberOfRelations;
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return idx + ", " + testTag + ", " + numberOfConcepts + ", " + numberOfRelations + ", " + numberOfElements
                + ", " + numberOfRoles + ", " + numberOfScenes
                + ", " + preRecognitionTime + ", " + learningTime + ", " + postRecognitionTime + ", " + totalTime + ";";
    }
}

public class Logger {
    private final boolean VERBOSE = false;  // print on standard output
    private final String logPath;
    private FileWriter logWriter;
    private static FileWriter csvWriter;
    public static final String BASE_PATH, csvPath, RELATIVE_PATH;
    static {
        BASE_PATH = FuzzySITBase.RESOURCES_PATH + "computationComplexityTest/";
        RELATIVE_PATH = BASE_PATH + "log/" + getReadableTime() + "/";
        csvPath = RELATIVE_PATH + "computationTime.csv";
        try {
            csvWriter = new FileWriter(createFile(csvPath), true);
        } catch (IOException e) {
            e.printStackTrace();
            logError(e, "Error while creating CSV file " + csvPath);
        }
    }

    public Logger(String name){
        this.logPath = RELATIVE_PATH + name + ".log";
        try {
            this.logWriter = new FileWriter(createFile(this.logPath), true);
        } catch (IOException e) {
            e.printStackTrace();
            logError(e, "Error while opening file " + logPath);
        }
    }

    private static File createFile(String path){
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }

    public synchronized void log(String contents){
        try {
            String formatted = getReadableTime() + "| " + contents;
            this.logWriter.append(formatted).append(System.lineSeparator());
            if(VERBOSE)
                System.out.println(formatted);
        } catch (IOException e) {
            e.printStackTrace();
            logError(e, "Error while writing on file " + logPath);
        }
    }

    public synchronized void close(){
        try {
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            logError(e, "Error while closing file " + logPath);
        }
    }

    public synchronized void flush() {
        try {
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logError(e, "Error while flushing file " + logPath);
        }
    }

    public static void csvWrite(CSVData data){
        synchronized (BASE_PATH){
            try {
                csvWriter.append(data.toString()).append(System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
                logError(e, "Error while writing file " + csvPath);
            }
        }
    }

    public static void csvClose(){
        synchronized (BASE_PATH) {
            try {
                csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                logError(e, "Error while closing file " + csvPath);
            }
        }
    }

    public static void csvFlush(){
        synchronized (BASE_PATH) {
            try {
                csvWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                logError(e, "Error while flushing file " + csvPath);
            }
        }
    }

    public static String getReadableTime(){
        return new SimpleDateFormat("dd-MM-yy_HH-mm-ss-SSS").format(new Date());
    }

    public static String getTestDescription(List<String> concepts, List<String> relations){
        return OntologyCreator.getTestDescription(concepts.size(), relations.size());
    }

    public synchronized static void logError(Exception e, String contents){
        Logger logger = new Logger("ERROR-" + System.currentTimeMillis());
        if(!contents.isEmpty())
            logger.log(contents);
        logger.log(e.getCause() + "");
        logger.log(e.getLocalizedMessage() + "");
        logger.log(e.getMessage());
        logger.log("--------------------------------------------");
        PrintWriter pw = new PrintWriter(logger.logWriter);
        e.printStackTrace(pw);
        logger.close();
    }
}

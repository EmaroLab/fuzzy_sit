package it.emarolab.runnableSITutility.sceneRecustructor.simulation;

import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.runnableSITutility.sceneRecustructor.GuidedCarlo;
import it.emarolab.runnableSITutility.sceneRecustructor.MonteCarloInterface;
import it.emarolab.fuzzySIT.core.SITTBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class GuidedCarloTest {

    public static final String ONTOLOGY_LOAD_PATH = FuzzySITBase.RESOURCES_PATH + "ontologies/learnedComposedTable.fuzzydl";
    public static final int NUMBER_PARTICLES = 15;

    public static void main(String[] args) {

        Queue<String> toGenerates = new LinkedList<>();
        toGenerates.add("FK");
        toGenerates.add("FGK");
        toGenerates.add("FPGK");
        SITTBox h = new SITTBox(ONTOLOGY_LOAD_PATH);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");


        for ( int i = 0; i < 10; i++) {

            Calendar cal = Calendar.getInstance();
            String date = sdf.format(cal.getTime());
            GuidedCarlo.csvFilePath = FuzzySITBase.RESOURCES_PATH + "sceneReconstructionLog/" + date + ".log";

            MonteCarloInterface carlo = new GuidedCarlo(h, toGenerates, NUMBER_PARTICLES, null, new ArrayList<>());
            carlo.start();

            try {
                Thread.sleep(600000); // 10 min
                carlo.kill();
                carlo = null;
                Runtime.getRuntime().freeMemory();
                Thread.sleep( 18000); // 3 min
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
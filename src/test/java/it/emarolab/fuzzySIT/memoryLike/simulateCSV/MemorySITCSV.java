package it.emarolab.fuzzySIT.memoryLike.simulateCSV;

import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.fuzzySIT.memoryLike.MemoryExample;
import it.emarolab.fuzzySIT.memoryLike.perception.PerceptionBase;

import java.util.List;

public class MemorySITCSV {

    public static final String DATA_PATH = FuzzySITBase.PATH_BASE + "memory_log/positions.csv";

    public static final Integer CONSOLIDATE_FORGET_RATE = 5; // consolidates and forgets every n experiences (affects SimpleMemory.EXPERIENCE_REINFORCE)
    public static final Integer INPUT_RATE = 1; // process a scene every n experiences

    private static final String SCENE_NAME = "Scene";

    //      other related parameters:
    // LogReader.OBJECT_TYPE_DEGREE
    // FuzzySITBase.ROLE_SHOULDER_BOTTOM_PERCENT
    // ConnectObjectScene.CONNECTED_THRESHOLD
    // SimpleMemory.ENCODE_RECOGNITION_TH
    // SimpleMemory.ENCODE_SIMILARITY_TH
    // SimpleMemory.LEARN_RECOGNITION_TH
    // SimpleMemory.LEARN_SIMILARITY_TH
    // SimpleMemory.SCORE_WEAK
    // SimpleMemory.LEARNED_SCORE
    // SimpleMemory.EXPERIENCE_REINFORCE
    // SimpleMemory.EXPERIENCE_STRUCTURE

    private MemoryExample memory;
    private String nameSerial;

    public MemorySITCSV(String dataPath, MemoryExample memory){
        this.memory = memory;
        this.nameSerial = "";
        observe( new LogReader( dataPath).getScenes());
    }
    public MemorySITCSV(String dataPath, MemoryExample memory, String sceneNameSerial){
        this.memory = memory;
        this.nameSerial = "-" + sceneNameSerial;
        observe( new LogReader( dataPath).getScenes());
    }
    public MemorySITCSV(String dataPath, String ontologyPath, boolean synchConsilidateForget){
        memory = new MemoryExample(ontologyPath, synchConsilidateForget);
        observe( new LogReader( dataPath).getScenes());
    }

    public void observe(List<PerceptionBase> experiment){
        int experienceCnt = 0, consolidateCnt = 1;
        boolean consolidateEnd = false;
        for ( PerceptionBase scene : experiment) {
            consolidateEnd = true;
            if( (experienceCnt % INPUT_RATE) == 0){
                System.out.println("--------------  time: " + experienceCnt + "   ----------------");
                memory.storeExperience(scene, nameSerial + SCENE_NAME + experienceCnt);

                if ((consolidateCnt++ % CONSOLIDATE_FORGET_RATE) == 0) {
                    memory.consolidateAndForget();
                    consolidateEnd = false;
                }
            }
            experienceCnt++;
        }

        if ( consolidateEnd)
            memory.consolidateAndForget();


    }

    public static void main(String[] args) {
        MemoryExample memory = new MemoryExample(FuzzySITBase.PATH_BASE + "table_assembling_memory_example.fuzzydl", false);
        new MemorySITCSV( DATA_PATH, memory);

        System.out.println( memory.getTimings());
        memory.accessMemory().getTbox().show();
    }
}

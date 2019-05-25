package it.emarolab.fuzzySIT.memoryLike.simulateCSV;

import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.fuzzySIT.memoryLike.MemoryExample;
import it.emarolab.fuzzySIT.memoryLike.perception.PerceptionBase;
import it.emarolab.fuzzySIT.memoryLike.perception.simple2D.ConnectObjectScene;
import it.emarolab.fuzzySIT.semantic.hierarchy.SceneHierarchyVertex;

import java.util.List;
import java.util.Set;

public class MemorySITCSV {

    // todo make it relative
    public static final String DATA_PATH_1 = "/Data/tale_memorySIT_log/2_Table_Incoming_1/positions.txt";
    public static final String DATA_PATH_2 = "/Data/tale_memorySIT_log/2_Table_Incoming_2/positions.txt";
    public static final String DATA_PATH_3 = "/Data/tale_memorySIT_log/2_Table_Incoming_3/positions.txt";
    public static final String DATA_PATH_4 = "/Data/tale_memorySIT_log/2_Table_Incoming_4/positions.txt";
    public static final String DATA_PATH_5 = "/Data/tale_memorySIT_log/2_Table_Incoming_5/positions.txt";

    public static final Integer CONSOLIDATE_FORGET_RATE = 5;//1; // consolidates and forgets every n experiences (affects SimpleMemory.EXPERIENCE_REINFORCE)
    public static final Integer INPUT_RATE = 1;//3; // process a scene every n experiences

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

    public MemorySITCSV(String dataPath, MemoryExample memory, String sceneNameSerial){
        this.memory = memory;
        this.nameSerial = sceneNameSerial;
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
                memory.storeExperience(scene, nameSerial + "-Scene" + experienceCnt);

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
        //new MemorySITCSV( DATA_PATH_1, memory, "A");
        //new MemorySITCSV( DATA_PATH_2, memory, "B");
        //new MemorySITCSV( DATA_PATH_3, memory, "C");
        new MemorySITCSV( DATA_PATH_4, memory, "D");
        //new MemorySITCSV( DATA_PATH_5, memory, "E");

        System.out.println( memory.getTimings());
        memory.accessMemory().getTbox().show();
    }
}

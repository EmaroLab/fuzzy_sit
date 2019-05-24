package it.emarolab.fuzzySIT.memoryLike.simulateCSV;

import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.fuzzySIT.memoryLike.MemoryExample;
import it.emarolab.fuzzySIT.memoryLike.perception.PerceptionBase;

import java.util.List;

public class MemorySITCSV {

    // todo make it relative
    public static final String DATA_PATH_1 = "/Data/tale_memorySIT_log/1_Table_Posed_1/positions.txt";
    public static final String DATA_PATH_2 = "/Data/tale_memorySIT_log/1_Table_Posed_2/positions.txt";
    public static final String DATA_PATH_3 = "/Data/tale_memorySIT_log/1_Table_Posed_3/positions.txt";
    public static final String DATA_PATH_4 = "/Data/tale_memorySIT_log/1_Table_Posed_4/positions.txt";
    public static final String DATA_PATH_5 = "/Data/tale_memorySIT_log/1_Table_Posed_5/positions.txt";

    public static final Integer CONSOLIDATE_FORGET_RATE = 10; // consolidates and forgets every n experiences (affects SimpleMemory.EXPERIENCE_REINFORCE)
    public static final Integer INPUT_RATE = 2; // process a scene every n experiences

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
        int cnt = 1, consolidateCnt = 1;
        boolean consolidateEnd = false;
        for ( PerceptionBase scene : experiment) {
            consolidateEnd = true;
            if( (cnt % INPUT_RATE) == 0){
                System.out.println("--------------  time: " + cnt + "   ----------------");
                memory.storeExperience(scene, nameSerial + "-Scene" + cnt);

                if ((consolidateCnt++ % CONSOLIDATE_FORGET_RATE) == 0) {
                    memory.consolidateAndForget();
                    consolidateEnd = false;
                }
            }
            cnt++;
        }

        if ( consolidateEnd)
            memory.consolidateAndForget();
    }

    public static void main(String[] args) {
        MemoryExample memory = new MemoryExample(FuzzySITBase.PATH_BASE + "table_assembling_memory_example.fuzzydl", true);
        new MemorySITCSV( DATA_PATH_1, memory, "A");
        //new MemorySITCSV( DATA_PATH_2, memory, "B");
        //new MemorySITCSV( DATA_PATH_3, memory, "C");
        //new MemorySITCSV( DATA_PATH_4, memory, "D");
        //new MemorySITCSV( DATA_PATH_5, memory, "E");

        System.out.println( memory.getTimings());
        memory.accessMemory().getTbox().show();
    }
}

package it.emarolab.runnableSITutility.memoryLike.simulation.simulateCSV;

import it.emarolab.runnableSITutility.memoryLike.perception.PerceptionBase;
import it.emarolab.runnableSITutility.memoryLike.perception.simple2D.ConnectObjectScene;
//import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LogReader {

    private double OBJECT_TYPE_DEGREE = 1; // const degree

    private static final String SEP = ", ";
    private static final String SEP_LINE = ";"; // indeed ";\n"

    private static final String TABLE_FILED = capitlize( ConnectObjectScene.TABLE);
    private static final String LEG_FILED = capitlize( ConnectObjectScene.LEG);
    private static final String CONNECTOR_FILED = capitlize( ConnectObjectScene.CONNECTOR);
    private static final String CONTAINER_FILED = capitlize( ConnectObjectScene.CONTAINER);
    private static final String PEN_FILED = capitlize( ConnectObjectScene.PEN);
    private static String capitlize(String str){
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private List< String> fields = new ArrayList<>();
    private List< PerceptionBase> parsedScenes; // one item for each instant of time


    public LogReader(String filePath) {
    }
    /*    System.out.println("Loading scene from file " + filePath);
        List<List<String>> logs = parseCSV(filePath);
        //System.out.println("\t\t   CSV fields: " + fields);

        List<Set<Pair<String, Point2>>> scenesMap = formatData(logs);
        //System.out.println("\t\t    scene map: " + scenesMap);

        parsedScenes = parseScenes( scenesMap);
        //System.out.println("\t\tparsed scenes: " + parsedScenes);
    }

    private List<PerceptionBase> parseScenes(List<Set<Pair<String, Point2>>> scenesMap) {
        List< PerceptionBase> scenes = new ArrayList<>();
        for( Set<Pair<String,Point2>> s : scenesMap){
            ConnectObjectScene scene = new ConnectObjectScene();
            for( Pair<String,Point2> o : s){
                if ( o.getValue() != null) {
                    if (o.getKey().equals(TABLE_FILED))
                        scene.addTable(o.getValue().getX(), o.getValue().getY(), OBJECT_TYPE_DEGREE);
                    else if (o.getKey().equals(LEG_FILED))
                        scene.addLeg(o.getValue().getX(), o.getValue().getY(), OBJECT_TYPE_DEGREE);
                    else if (o.getKey().equals(PEN_FILED))
                        scene.addPen(o.getValue().getX(), o.getValue().getY(), OBJECT_TYPE_DEGREE);
                    else if (o.getKey().equals(CONTAINER_FILED))
                        scene.addContainer(o.getValue().getX(), o.getValue().getY(), OBJECT_TYPE_DEGREE);
                    else if (o.getKey().equals(CONNECTOR_FILED))
                        scene.addConnector(o.getValue().getX(), o.getValue().getY(), OBJECT_TYPE_DEGREE);
                    else System.err.println(" Not known key for " + o);
                }
            }
            scenes.add( scene);
        }
        return  scenes;
    }

    private List< Set< Pair< String,Point2>>> formatData(List<List<String>> logs) {
        int emptyPerceptionCnt = 0;
        List< Set< Pair< String,Point2>>> scenesMap = new ArrayList<>();
        if (logs != null){
            String warningLog = "";
            int w = 0;
            for ( List<String> scenesCsv : logs){
                Set< Pair< String,Point2>> objTypePose = new HashSet<>();
                String warningLog1 = "";
                for ( int i = 1; i < scenesCsv.size();){
                    try{
                        String unformattedKey = fields.get(i);
                        // remove numbers
                        unformattedKey = unformattedKey.replaceAll("\\d", "");
                        // take first word
                        int j = unformattedKey.indexOf(' ');
                        String key = unformattedKey;
                        if( j > 1)
                            key = unformattedKey.substring(0, j);
                        String o1 = "", o2 = "";
                        try {
                            o1 = scenesCsv.get( i++);
                            o2 = scenesCsv.get( i++);
                            Point2 value = new Point2( Double.valueOf( o1), Double.valueOf( o2));
                            objTypePose.add(new Pair<>(key, value));
                        } catch (NumberFormatException e){
                            warningLog1 += ", " + unformattedKey + "(\'" + o1 + ", " + o2  + "\')";
                            break;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        System.err.println( "ERROR: [idx " + w + "] impossible parse " + scenesCsv + ", scene \'" +  i + "\' discarded.");
                        break;
                    }
                }
                if ( ! warningLog1.isEmpty()) {
                    objTypePose.add( new Pair<>( "Empty-Perception" + emptyPerceptionCnt, null));
                    //scenesMap.add(objTypePose);
                    warningLog += "\t\t[" + w + "]" + warningLog1 + "\n";
                }
                if ( ! objTypePose.isEmpty())
                    scenesMap.add( objTypePose);
                w++;
            }
            if ( ! warningLog.isEmpty())
                System.err.println( "WARNING: empty object pose\n" + warningLog);
        }
        return scenesMap;
    }
*/
    private List<List<String>> parseCSV(String filePath) {
        try {
            List<List<String>> records = new ArrayList<>();
            try (Scanner scanner = new Scanner(new File(filePath))) {
                boolean header = true;
                while (scanner.hasNextLine()) {
                    List<String> rec = getRecordFromLine(scanner.nextLine());
                    if ( header) {
                        for ( String r : rec)
                            fields.add( r);
                        header = false;
                    } else records.add( rec);
                }
            }
            return records;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(SEP);
            while (rowScanner.hasNext()) {
                String contents = rowScanner.next().replace( SEP_LINE, "");
                values.add( contents);
            }
        }
        return values;
    }

    public List<PerceptionBase> getScenes() {
        return parsedScenes;
    }

    public static void main(String[] args) {
        new LogReader( "/Data/tale_memorySIT_log/1_Table_Posed_1/positions.txt"); // todo make it relative
    }
}

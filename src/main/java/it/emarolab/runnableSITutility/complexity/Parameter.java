package it.emarolab.runnableSITutility.complexity;

import java.util.ArrayList;
import java.util.List;


class RawParameters {

    protected List<Integer> elements = new ArrayList<>(), scenes = new ArrayList<>(),
            concepts = new ArrayList<>(), relations = new ArrayList<>();

    public RawParameters() {
    }

    public List<Integer> getConcepts() {
        return concepts;
    }

    public List<Integer> getRelations() {
        return relations;
    }

    public List<Integer> getElements() {
        return elements;
    }

    public List<Integer> getScenes() {
        return scenes;
    }

    @Override
    public String toString() {
        return "Raw{" +
                "-(P)elements=" + elements +
                ", -(P)scenes=" + scenes +
                ", -(P)concepts=" + concepts +
                ", -(P)relations=" + relations +
                '}';
    }
}

public class Parameter {
    private List<Integer> elements = new ArrayList<>(), scenes = new ArrayList<>();
    private List<String> concepts, relations;
    private String ontology;

    public Parameter(List<Integer> elements, List<Integer> scenes, List<String> concepts, List<String> relations){
        this.elements = new ArrayList<>(elements);
        this.scenes = new ArrayList<>(scenes);
        this.concepts = concepts;
        this.relations = relations;
        this.ontology = OntologyCreator.BASE_PATH + getTestLabel() + ".fuzzydl";
    }

    public String getTestLabel(){
        return Logger.getTestDescription(concepts, relations);
    }

    public List<Integer> getElements() {
        return elements;
    }

    public List<Integer> getScenes() {
        return scenes;
    }

    public List<String> getConcepts() {
        return concepts;
    }

    public List<String> getRelations() {
        return relations;
    }

    public String getOntology() {
        return ontology;
    }

    @Override
    public String toString() {
        return  "[(-P)concepts: " + concepts + ", (-P)relations: " + relations +
                ", (-P)elements: " + elements + ", (-P)scenes: " + scenes + ", onto:" + ontology + "]";
    }


    private static List<Integer> parseRawParameter(String tag, String rawParam) {
        List<Integer> out = new ArrayList<>();
        String[] parsed = rawParam.split("=");
        if(parsed[0].equals(tag)) {
            String[] values = parsed[1].split(",");
            for (int j = 0; j < values.length; j++)
                out.add(new Integer(values[j].replaceAll("'","")));
        }
        return out;
    }

    public static RawParameters getRawParam(String[] args) {
        RawParameters out = new RawParameters();
        try {
            for (String arg : args) {
                if( out.concepts.isEmpty())
                    out.concepts = parseRawParameter("-Pconcepts", arg);  // "-Pconcept" is defined in build.gradle
                if( out.relations.isEmpty())
                    out.relations = parseRawParameter("-Prelations", arg);  // "-Prelations" is defined in build.gradle
                if(out.elements.isEmpty())
                    out.elements = parseRawParameter("-Pelements", arg);  // "-Pelements" is defined in build.gradle
                if(out.scenes.isEmpty())
                    out.scenes = parseRawParameter("-Pscenes", arg);  // "-Pscenes" is defined in build.gradle
            }

            if(out.concepts.isEmpty() || out.relations.isEmpty() || out.elements.isEmpty() || out.scenes.isEmpty())
                throw new Exception();

        } catch (Exception e){
            String msg = "ERROR: wrong parameters inputs. See `build.gradle` comments for more.";
            System.err.println(msg);
            e.printStackTrace();
            Logger.logError(e, msg);
        }
        return out;
    }

    public static List<Parameter> incrementalParam(RawParameters rawParam, List<String> possibleConcepts, List<String> possibleRelations) {
        List<Parameter> out = new ArrayList<>();
        for(Integer c : rawParam.concepts){
            List<String> conceptName = possibleConcepts.subList(0, c);
            for (Integer r: rawParam.relations){
                List<String> relationName = possibleRelations.subList(0, r);
                out.add(new Parameter(rawParam.elements, rawParam.scenes, conceptName, relationName));
            }
        }
        return out;
    }

/*


    public static List<Integer> convertList(List<String> list){
        List<Integer> out = new ArrayList<>();
        for (String s: list)
            out.add(new Integer(s));
        return out;
    }


    //private static List<Integer> divideRange(int size, int step){
    //    List<Integer> out = new ArrayList<>();
    //    int i = 0;
    //    while (i < size){
    //        // min is equal to `i` here
    //        i += step;
    //        if( i > size)
    //            i = size;
    //        out.add(i); // Maz is equal to `i` here
    //    }
    //    return out; // parses in ranges [min,max] and returns only the max
    //}

    public static List<Parameters> incrementalParam(Parameters param, int step){
        Parameters incr = new Parameters(param);
        List<Parameters> out = new ArrayList<>();
        for (Integer conceptIdx: param.concepts){

        }

        for ( Integer conceptIdx : divideRange(param.concepts.size(), step)) {
            for (Integer relationIdx : divideRange(param.relations.size(), step)) {
                Parameters newPar = incr.copy();
                List<String> conceptSet = param.getConcepts().subList(0, conceptIdx);
                newPar.concepts = new ArrayList<>(conceptSet);
                List<String> relationSet = param.getRelations().subList(0, relationIdx);
                newPar.relations = new ArrayList<>(relationSet);
                out.add(newPar);
            }
        }
        return out;
    }*/
}

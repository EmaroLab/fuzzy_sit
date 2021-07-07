package it.emarolab.runnableSITutility.complexity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OntologyCreator {
    // parameters for the main function
    private static final int maxNumberOfConcepts = 25;
    private static final int maxNumberOfRelations = 25;

    // name of classes and relations in the ontology
    public static final List<String> CONCEPTS_NAMES = new ArrayList<String>() {{
        add("Ca");  add("Cb");  add("Cc");  add("Cd");  add("Ce");
        add("Cf");  add("Cg");  add("Ch");  add("Ci");  add("Cl");
        add("Cm");  add("Cn");  add("Co");  add("Ck");  add("Cp");
        add("Cq");  add("Cr");  add("Cs");  add("Ct");  add("Cu");
        add("Cv");  add("Cw");  add("Cx");  add("Cy");  add("Cz");
    }};
    public static final List<String> RELATION_NAMES = new ArrayList<String>() {{
        add("Ra");  add("Rb");  add("Rc");  add("Rd");  add("Re");
        add("Rf");  add("Rg");  add("Rh");  add("Ri");  add("Rl");
        add("Rm");  add("Rn");  add("Ro");  add("Rp");  add("Rq");
        add("Rr");  add("Rs");  add("Rt");  add("Ru");  add("Rv");
        add("Rz");  add("Rx");  add("Ry");  add("Rk");  add("Rw");
    }};

    private static final String HEADER = "(define-fuzzy-logic zadeh)" + System.lineSeparator() +
            "(define-primitive-concept Object *top*)" + System.lineSeparator() +
            "(define-primitive-concept Scene  *top*)" + System.lineSeparator() +
            "(disjoint Object Scene)" + System.lineSeparator() +
            "(disjoint SpatialObject Scene)" + System.lineSeparator();

    private String declareConcept(String name){
        return "(implies " + name + " Object)" + System.lineSeparator();
    }

    private String declareRelation(String name){
        return "(range  is" + name + " Object)" + System.lineSeparator() +
               "(domain is" + name + " SpatialObject)" + System.lineSeparator();
    }

    private String declrearRelatedObject(String concept, String relation){
        return "(define-concept " + concept + relation + " (and " + concept + " (some is" + relation + "  SpatialObject)))";
    }

    public static final String BASE_PATH = Logger.BASE_PATH + "ontologies/compTest_";

    public static String getTestDescription(Integer concepts, Integer relations){
        return concepts + "-" + relations;
    }

    // create a file with the ontology in the constructor
    public OntologyCreator(int numberOfConcepts, int numberOfRelations) throws IOException {
        File file = new File(BASE_PATH + numberOfConcepts + "-" + numberOfRelations + ".fuzzydl");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file, true);
        writer.append(composeOntology(numberOfConcepts, numberOfRelations));
        writer.close();
    }

    // create ontology as a string
    private String composeOntology(int numberOfConcepts, int numberOfRelations){
        String out = HEADER + System.lineSeparator();
        for (int i = 0; i < numberOfConcepts; i++)
            out += declareConcept(CONCEPTS_NAMES.get(i));
        out += System.lineSeparator();

        for (int i = 0; i < numberOfRelations; i++)
            out += declareRelation(RELATION_NAMES.get(i));
        out += System.lineSeparator();

        for (int i = 0; i < numberOfConcepts; i++) {
            for (int j = 0; j < numberOfRelations; j++) {
                out +=  declrearRelatedObject(CONCEPTS_NAMES.get(i), RELATION_NAMES.get(j));
                out += System.lineSeparator();
            }
        }
        return out;
    }

    // write several ontologies into files
    public static void main(String[] args) {
        try {
            if(maxNumberOfConcepts > CONCEPTS_NAMES.size() | maxNumberOfRelations > RELATION_NAMES.size())
                System.err.println("Max concepts and relation number is " + CONCEPTS_NAMES.size() +
                        " and " + RELATION_NAMES.size()  + ", respectively. Change them manually if you want more");
            else {
                for (int i = 0; i < maxNumberOfConcepts; i++)
                    for (int j = 0; j < maxNumberOfRelations; j++)
                        new OntologyCreator(i + 1, j + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

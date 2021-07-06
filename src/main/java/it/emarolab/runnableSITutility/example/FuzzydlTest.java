package it.emarolab.runnableSITutility.example;

import fuzzydl.ConfigReader;
import fuzzydl.KnowledgeBase;
import fuzzydl.exception.FuzzyOntologyException;
import fuzzydl.milp.Solution;
import fuzzydl.parser.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import fuzzydl.*;
import fuzzydl.exception.*;
import fuzzydl.parser.*;
import it.emarolab.fuzzySIT.FuzzySITBase;

public class FuzzydlTest {
    public static void main(String[] args) throws FuzzyOntologyException,
            InconsistentOntologyException,
            IOException, ParseException {

        ConfigReader.loadParameters(FuzzySITBase.RESOURCES_PATH + "fuzzyDL_CONFIG", new String[0]);
        Parser parser = new Parser(new FileInputStream(FuzzySITBase.RESOURCES_PATH + "ontologies/fuzzyDL_test.txt"));
        parser.Start();

        KnowledgeBase kb = parser.getKB();
        kb.solveKB();

        // make a query
        //kb.addConcept("S", new RightConcreteConcept("ATLEAST", 0, 100, 1, 2));
        MinSubsumesQuery query = new MinSubsumesQuery(kb.getConcept("Minor"), kb.getConcept("YoungPerson"),SubsumptionQuery.LUKASIEWICZ);
        Solution sol = query.solve( kb);
        if (sol.isConsistentKB())
            System.out.println(query.toString() + sol.getSolution());

        // solve all queries stated in the ontology
        ArrayList <Query> queries = parser.getQueries();
        for( Query q : queries) {
            Solution result = q.solve(kb);
            if (result.isConsistentKB())
                System.out.println(q.toString() + result.getSolution());
                // System.out.println(q.toString());
            else
               System.out.println("KB is inconsistent");
        }

        /*  // save on OWL to be opened on protege
        FuzzydlToOwl2 f = new FuzzydlToOwl2( "FuzzyOWL2Tools/FuzzyDL/kb.txt", "FuzzyOWL2Tools/FuzzyDL/kb.owl");
        f.run();
        */
    }
}

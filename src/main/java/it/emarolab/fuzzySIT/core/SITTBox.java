package it.emarolab.fuzzySIT.core;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import fuzzydl.*;
import fuzzydl.Query;
import fuzzydl.exception.FuzzyOntologyException;
import fuzzydl.exception.InconsistentOntologyException;
import fuzzydl.milp.Solution;
import fuzzydl.parser.ParseException;
import fuzzydl.parser.Parser;
import it.emarolab.fuzzySIT.FuzzySITBase;
import it.emarolab.fuzzySIT.core.axioms.SpatialProperty;
import it.emarolab.fuzzySIT.core.hierarchy.SceneHierarchyEdge;
import it.emarolab.fuzzySIT.core.hierarchy.SceneHierarchyVertex;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The fuzzy SIT manager for the ontological T-Box.
 * <p>
 *     It manages a fresh copy of a T-Box of the SIT ontology to be used with
 *     {@link SITABox} for learning and recognition of scenes.
 *     <br>
 *     It is able to read a <a url="http://www.umbertostraccia.it/cs/software/fuzzyDL/download/old/documents/documents.html">fuzzydl</a>
 *     syntax and parse some augmented information (i.e., object distribution) from it (see the above file for an example).
 *     It also manages a auxiliary copy of this file during learning. See an example
 *     of the T-Box at: {@code src/main/resources/example_SIT_kb.fuzzydl}.
 *     <br>
 *     Specifically, during construction it:
 *     <ul>
 *     <li> makes a copy of the fuzzydl T-Box and updates its reasoner. </li>
 *     <li> creates the {@link SpatialProperty}, by parsing:
 *          δ ≡ "hasΔ", where SpatialObject ⊃ Δ ≡ "TypeProperty" ≝ { π ≡ Type(Δ) ⊂ Object, ζ ≡ Property(Δ) → spatialProperty}</li>
 *     <li> queries the scene hierarchy (see: {@link SceneHierarchyVertex} and {@link SceneHierarchyEdge}).
 *          For this procedures it parses: the {@link #ROLE_ATLEAST}, values for computing the {@link SigmaCounters}, and
 *          the object distribution map (Ψ).</li>
 *     </ul>
 *     <br>
 *     Also this class augment the fuzzy ontology syntax with annotations. Those are lines composed as
 *     {@code {@link #ANNOTATION_PREFIX} + sceneName + {@link #ANNOTATION_CARDINALITY_SEPARATOR} + "π1=n1, " +
 *     "π1=n2, " + ... + ";" + "π2=n1, " + "π2=n2, " + ... + ";" + "π3=n1, " + "π3=n2, " + ... + {@link #NEW_LINE};}.
 *     Where the {@link Map} after the {@link #ANNOTATION_CARDINALITY_SEPARATOR} indicates, for each element
 *     delimited by {@code ;}, an object in the scene. This object has a type distribution which spans across
 *     {@code π1, π2, ...} types (i.e.: Book, Cup etc.), with a specific fuzzy value {@code n1, n2, ...}.
 *     This structure, also to be parsed from file, it is updated during {@link #learn(String, SITABox)}ing and
 *     stored while {@link #saveTbox(String)}. It is possible to address this map from API trhgouh
 *     {@link SceneHierarchyVertex#getObjectDistribution()}.
 *
 * <div style="text-align:center;"><small>
 * <b>File</b>:        it.emarolab.fuzzySIT.semantic.SITTBox <br>
 * <b>Licence</b>:     GNU GENERAL PUBLIC LICENSE. Version 3, 29 June 2007 <br>
 * <b>Author</b>:      Buoncompagni Luca (luca.buoncompagni@edu.unige.it) <br>
 * <b>affiliation</b>: EMAROLab, DIBRIS, University of Genoa. <br>
 * <b>date</b>:        27/06/17 <br>
 * </small></div>
 *
 * @see FuzzySITBase
 * @see SITABox
 */
public class SITTBox
        implements FuzzySITBase {

    // todo add noise on the learning (there must be all relations)

    private final ListenableDirectedWeightedGraph<SceneHierarchyVertex, SceneHierarchyEdge>
            hierarchy = new ListenableDirectedWeightedGraph<>( SceneHierarchyEdge.class); // the SIT scene class hierarchy
    private KnowledgeBase tbox; // the fresh fuzzy T-Box
    private Collection<Query> syntaxQueries; // the queries in the fuzzydl syntax (for debugging purposes)
    private Collection<String> scenes; // the name of all the scenes in the T-Box
    private Map<String, SpatialProperty> spatial; // Map<Δ,δ> between Scene Classes (Δ), given in the ontology; and Scene Properties (δ ≡ "hasΔ")
    private final Map<String, List<Map<String, Double>>> objectDistribution = new HashMap<>(); // Map<nameScene, List<ObjectType, objectCount>>
    private Collection<String> objectType; // the collection of all objects type Π = {..., πq, ...}
    private String syntaxFile; // the path to the fuzzydl syntax file
    private String configurationFile; // the path to the fuzzydl configuration file
    private String syntaxLearnedFile; // the path to the fuzzydl auxiliary syntax file, used while learning
    private JFrame frame; // used for visualising the hierarchy, debugging purposes.
    private static String toWrite  = NEW_LINE; // lines to add in the syntax to save the information about the object type distributions
    private final Object onSync; // used for synchronise the reading ad writing to file through fuzzyDL
    private double fuzziness = FuzzySITBase.ROLE_SHOULDER_BOTTOM_PERCENT;

    /**
     * Initialises this T-Box by using the default ontology ({@link #FILE_ONTOLOGY_LOAD}) and
     * fuzzydl configuration file ({@link #FILE_FUZZYDL_CONFIG}).
     * It queries a new {@link #getHierarchy()} by parsing the sigma counter based on the name of {@link #ROLE_ATLEAST}
     * fuzzy property. It also prepares an auxiliary file by appending in its name {@link #LEARNER_FILE_AUXILIARY_PATH}.
     * The latter is used during learning since the T-Box cannot be resolved directly. A fresh copy
     * of the T-Box is always kept in this object, while a copy of it can be retrieve (and solved) from {@link #getTboxCopy()}.
     * <br>
     * Also, this object manage the learning and maintaining of the terminological knowledge in the ontology
     * (that can be farther saved).
     */
    public SITTBox() {
        this.onSync = this; // e.g., `synchronize(this){..}`
        initialise(FILE_ONTOLOGY_LOAD, FILE_FUZZYDL_CONFIG);
    }
    public SITTBox(Object onSync) {
        this.onSync = onSync; // e.g., `synchronize(onSync){..}`
        initialise(FILE_ONTOLOGY_LOAD, FILE_FUZZYDL_CONFIG);
    }
    /**
     * Initialises this T-Box by using the given ontology and default
     * fuzzydl configuration file ({@link #FILE_FUZZYDL_CONFIG}).
     * It queries a new {@link #getHierarchy()} by parsing the sigma counter based on the name of {@link #ROLE_ATLEAST}
     * fuzzy property. It also prepares an auxiliary file by appending in its name {@link #LEARNER_FILE_AUXILIARY_PATH}.
     * The latter is used during learning since the T-Box cannot be resolved directly. A fresh copy
     * of the T-Box is always kept in this object, while a copy of it can be retrieve (and solved) from {@link #getTboxCopy()}.
     * <br>
     * Also, this object manage the learning and maintaining of the terminological knowledge in the ontology
     * (that can be farther saved).
     * @param tboxPath the path to the fuzzy SIT ontology to use.
     */
    public SITTBox(String tboxPath) {
        this.onSync = this; // e.g., `synchronize(this){..}`
        initialise( tboxPath, FILE_FUZZYDL_CONFIG);
    }
    public SITTBox(String tboxPath, Object onSync) {
        this.onSync = onSync; // e.g., `synchronize(onSync){..}`
        initialise( tboxPath, FILE_FUZZYDL_CONFIG);
    }
    /**
     * Initialises this T-Box by using the given ontology and fuzzydl configuration file.
     * It queries a new {@link #getHierarchy()} by parsing the sigma counter based on the name of {@link #ROLE_ATLEAST}
     * fuzzy property. It also prepares an auxiliary file by appending in its name {@link #LEARNER_FILE_AUXILIARY_PATH}.
     * The latter is used during learning since the T-Box cannot be resolved directly. A fresh copy
     * of the T-Box is always kept in this object, while a copy of it can be retrieve (and solved) from {@link #getTboxCopy()}.
     * <br>
     * Also, this object manage the learning and maintaining of the terminological knowledge in the ontology
     * (that can be farther saved).
     * @param tboxPath the path to the fuzzy SIT ontology to use.
     * @param fuzzydlConfigPath the configuration file for the fuzzydl reasoner.
     */
    public SITTBox(String tboxPath, String fuzzydlConfigPath) {
        this.onSync = this; // e.g., `synchronize(this){..}`
        initialise( tboxPath, fuzzydlConfigPath);
    }
    public SITTBox(String tboxPath, String fuzzydlConfigPath, Object onSync) {
        this.onSync = onSync; // e.g., `synchronize(onSync){..}`
        initialise( tboxPath, fuzzydlConfigPath);
    }
    // common constructor for implement this TBox manager
    private void initialise(String tboxPath, String confFile){
        synchronized (this.onSync) {
            try {
                // at the beginning set the auxiliary file as the original file
                this.syntaxLearnedFile = tboxPath;
                // never solve T-Box !!!! This make error when a new class in learned at run time.
                // After each leaning operation you should manipulated a fresh T-Box. This justify the auxiliary file.
                tbox = readFromFile(tboxPath, confFile);
                // use a copy of the T-Box, which can be solved
                KnowledgeBase kb = tbox.clone();
                kb.solveKB();
                // get the name of all the Scenes in the T-Box. Those are all the classes ψ ⊂ Scene
                setScenes(kb);
                // get the name of all spatial property in the T-Box. Those are obtained by all the classes Δ ⊂ SpatialObject.
                setSpatial(kb);
                // query the implication hierarchy between all the Scene ψ
                buildHierarchy(kb);
            } catch (FuzzyOntologyException | InconsistentOntologyException e) {
                e.printStackTrace();
            }
        }
    }

    // called in constructor, it reads the ontology from file.
    private KnowledgeBase readFromFile(String tboxPath, String confFile){
        long time = System.currentTimeMillis();
        syntaxFile = tboxPath;
        configurationFile = confFile;
        KnowledgeBase kb = null;
        FileInputStream is = null;
        Parser.reset();
        try {
            is = new FileInputStream(tboxPath);
            ConfigReader.loadParameters(confFile, new String[0]);
            Parser parser = new Parser(is);
            parser.Start();
            kb = parser.getKB();
            syntaxQueries = parser.getQueries();
        } catch (FileNotFoundException |
                ParseException |
                InconsistentOntologyException |
                FuzzyOntologyException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log(time, "load T-box syntax from " + tboxPath);
        return kb;
    }

    // it looks in the loaded file
    // hp restrictions are right shoulder
    // hp the LEARN_RESOLUTION is the same
    // returns Map<nameScene, Map< relationName, restrictionName>>
    // and sets also object number relations (objectDistribution)
    private Map<String,Map<String,String>> parseClassDefinition(){
        long time = System.currentTimeMillis();
        LineNumberReader lnr = null;
        Map<String,Map<String,String>> parsed = new HashMap<>();
        try {
            lnr = new LineNumberReader (new FileReader(syntaxLearnedFile));
            String line = "";
            while ( line != null) {
                // remove multiple space
                line = line.trim().replaceAll(" +", " ");
                // if it is a concept definition
                if ( line.contains( "define-concept")){
                    for ( String sceneName : scenes) {
                        Map<String,String> concrete = new HashMap<>();
                        // if it is a concept definition of a scene class
                        if (line.contains( "define-concept " + sceneName)){
                            // for all the spatial relations for scenes
                            for( String relation : tbox.concreteRoles){
                                if ( line.contains( relation.toString())){ // hp: only one roole for each type
                                    // get the restriction
                                    String a = line.substring( line.indexOf(relation.toString()), line.length() - 1);
                                    String restriction = a.split("\\)")[0].split(" ")[1];
                                    time = log( FLAG_LOG_VERBOSE, time,
                                            sceneName + " -> " + relation + " " + restriction);
                                    concrete.put( relation.toString(), restriction);
                                }
                            }
                            parsed.put( sceneName, concrete);
                            break;
                        }
                    }
                }
                // parse scene number of objects
                if ( line.startsWith( ANNOTATION_PREFIX)){
                    line = line.replace( ANNOTATION_PREFIX, "");
                    String[] tokens = line.split(Pattern.quote(ANNOTATION_CARDINALITY_SEPARATOR));
                    List<String> list = new ArrayList<>(Arrays.asList(tokens[1].split(";")));
                    List< Map<String,Double>> readedDistribution = new ArrayList<>();
                    for( String l :list) {
                        Map<String, Double> readMap = Arrays.stream(l.split(","))
                                .map(s -> s.split("="))
                                .collect(Collectors.toMap(
                                        a -> a[0],  //key
                                        a -> Double.valueOf(a[1])   //value
                                ));
                        readedDistribution.add( readMap);
                    }
                    objectDistribution.put( tokens[0], readedDistribution);
                    toWrite += ANNOTATION_PREFIX + tokens[0] + ANNOTATION_CARDINALITY_SEPARATOR + getObjectDistributionForFile( readedDistribution) + NEW_LINE;
                    time = log( FLAG_LOG_VERBOSE, time, "external info: " + toWrite);
                }
                line = lnr.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (lnr != null)
                    lnr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parsed;
    }

    // called on constructors, it uses the internal kb to build the class hierarchy
    // updates fields: scene, semantic
    private void buildHierarchy(KnowledgeBase kb) throws FuzzyOntologyException {
        long time = System.currentTimeMillis();
        // parse from file
        time = log( time, "parsing from fuzzydl syntax...");
        Map<String, Map<String, String>> parsedDef = parseClassDefinition();
        for( String sceneName : parsedDef.keySet()) {
            SigmaCounters def = parseSigmaDefinition( parsedDef.get(sceneName));
            List<Map<String, Double>> objectDistribution = this.objectDistribution.get( sceneName);
            time = log( time, "parsing " + sceneName + " definition: " + def + " with " + objectDistribution + " objects");
            //System.out.print( "reading: " + sceneName + " with def: " + def);
            // add vertexes
            SceneHierarchyVertex vertex = new SceneHierarchyVertex(sceneName, def, objectDistribution);
            hierarchy.addVertex( vertex);
        }
        // add scene edges
        updateEdges( kb);
        log( time, "create semantic from file: " + hierarchy);
    }
    // called by buildHierarchy it creates the SigmaCount definition from parsed information
    private SigmaCounters parseSigmaDefinition(Map<String,String> restrictions){
        SigmaCounters def = new SigmaCounters();
        for( String s : restrictions.keySet()){
            // get sigma object and relation
            String[] split = SpatialProperty.splitCamelCase( s);
            // get right shoulder limits
            String atLeastX = restrictions.get(s).replace(REPLACE_DOUBLE_POINT,".");
            double b = Double.parseDouble( atLeastX.replace(ROLE_ATLEAST, ""));
            assert split != null;
            def.add( split[0],split[1], b);
        }
        return def;
    }
    // it queries to the reason the subsunction between scene in order to create the class hierarchy graph.
    public void updateEdges(KnowledgeBase kb) { // it perform queries
        try {
            long time = System.currentTimeMillis();
            for (SceneHierarchyVertex v1 : hierarchy.vertexSet()) {
                for (SceneHierarchyVertex v2 : hierarchy.vertexSet()) {
                    if (!v1.getScene().equals(v2.getScene())) {
                        Query q = new MinSubsumesQuery(
                                kb.getConcept(v1.getScene()),
                                kb.getConcept(v2.getScene()),
                                SubsumptionQuery.LUKASIEWICZ);
                        Solution solution = q.solve(kb);
                        log(FLAG_LOG_VERBOSE, q.getTotalTime(), "\t?? " + v1 + " ⊂ " + v2 + " ("
                                + solution.getSolution() + ")");
                        if (solution.getSolution() > 0) {
                            SceneHierarchyEdge e = hierarchy.addEdge(v2, v1);
                            if (e != null)
                                hierarchy.setEdgeWeight(e, solution.getSolution());
                        }
                    }
                }
            }
            log( time, "Hierarchy computed: " + hierarchy);
        } catch (FuzzyOntologyException e) {
            e.printStackTrace();
        }
    }
    // called on construction it queries all spatial classes as: Δ ⊂ SpatialObject
    // it also creates properties: δ ≡ hasΔ as concrete functional properties with fuzzy limits: LIMIT_LOW, LIMIT_HIGH
    private void setSpatial(KnowledgeBase kb)
            throws FuzzyOntologyException {
        spatial = new HashMap<>();
        objectType = new HashSet<>();
        // get all Δ
        Set<String> subClasses = querySubConcept(CONCEPT_SPATIAL_TOP, kb);
        for (String subClass :  subClasses){
            // δ ≡ hasΔ
            if(querySubConcept(subClass, kb).isEmpty()) { // do not elaborate `Object` subclass but only `SpatialObjects`. The latter are always subclass of the former.
                SpatialProperty spatialProperty = new SpatialProperty(subClass);
                if (!spatialProperty.isNull()) {
                    spatial.put(subClass, spatialProperty);
                    objectType.add(spatialProperty.getType());

                    String roleName = spatialProperty.getSpatialProperty();
                    // (domain hasBookLeft Scene)
                    tbox.roleDomain(roleName, kb.getConcept(CONCEPT_SCENE_TOP));
                    // (functional hasBookLeft)
                    tbox.roleIsFunctional(roleName);
                    // (range hasBookLeft *real* 0.0 10.0)
                    tbox.defineRealConcreteFeature(roleName, LIMIT_LOW, LIMIT_HIGH);
                }
            }
        }
    }
    // called on constructor it queries all scene classes Λ ⊂ Spatial (≡ #CONCEPT_SCENE_TOP)
    private void setScenes(KnowledgeBase kb)
            throws FuzzyOntologyException {
        scenes = querySubConcept( CONCEPT_SCENE_TOP, kb);
    }
    // used by setScene(..) it returns all classes that subsume the given class
    // with a minimum fuzzy value equal to 1.
    private Set<String> querySubConcept( String superConcept, KnowledgeBase kb) throws FuzzyOntologyException {
        long time = System.currentTimeMillis();
        Hashtable<String, Concept> allConcepts = kb.atomicConcepts;
        Set<String> out = new HashSet<>();
        Concept superClass = kb.getConcept( superConcept);
        int logic = getQueryLogic();
        for ( String s : allConcepts.keySet()){
            if ( ! s.equals( superConcept)) {
                Concept c = kb.getConcept(s);
                Query q = new MinSubsumesQuery(superClass, c, logic);
                Solution sol = q.solve( kb);
                log( FLAG_LOG_VERBOSE, q.getTotalTime(), "\t?? " + c + " ⊂ " + superConcept + " (" + sol.getSolution() + ")");
                double minSubConceptDegree = sol.getSolution();
                if (minSubConceptDegree >= 1) {
                    out.add(s);
                }
            }
        }
        log( time, superConcept + "-s : " + out);
        return out;
    }

    /**
     * Given a representation of objects (A-Box), it creates a new scene class,
     * with the given name, that subsumes {@link #CONCEPT_SPATIAL_TOP}. This calls
     * updates the {@link #getHierarchy()} and {@code this} ontology by creating
     * an auxiliary syntax file.
     * @param newSceneName the unique name of the new scene to learn.
     * @param representation the scene representation to learn.
     * @return the learned node not structured in the memory graph
     */
    public SceneHierarchyVertex learn(String newSceneName, SITABox representation) {
        SceneHierarchyVertex learned = rawLearning(newSceneName, representation);
        KnowledgeBase kb = closeReopen(newSceneName, representation, this); // solve FuzzyDL bug
        updateEdges(kb); // structuring
        return learned;
    }

    public SceneHierarchyVertex rawLearning(String newSceneName, SITABox representation){
        if (scenes.contains(newSceneName)) {
            System.err.println(newSceneName + " already defined !!!");
            return null;
        }

        KnowledgeBase kb = tbox.clone();

        // add new base scene
        long time = System.currentTimeMillis();
        scenes.add(newSceneName);
        tbox.defineAtomicConcept(newSceneName, kb.getConcept(CONCEPT_SCENE_TOP), getQueryLogic(), 1);
        kb.defineAtomicConcept(newSceneName, kb.getConcept(CONCEPT_SCENE_TOP), getQueryLogic(), 1);
        time = log(time, "adding new " + newSceneName);

        // set right shoulder based on sigma count
        ArrayList<Concept> someRestrictions = new ArrayList<>();
        for (SigmaCounters.Sigma s : representation.getDefinition().getCounters())
            someRestrictions.add(getRestriction(kb, s));
        time = log(time, "create restrictions " + someRestrictions);

        // define new scene concept cardinality
        if (!someRestrictions.isEmpty()) {
            Concept andRestriction = Concept.and(someRestrictions);
            tbox.defineConcept(newSceneName, andRestriction);
            kb.defineConcept(newSceneName, andRestriction);
        } else System.err.println("SCENE CANNOT BE LEARNED !!!! ");
        log(time, newSceneName + " LEARNED");

        // add vertex to semantic and edges
        SceneHierarchyVertex learnedScene = new SceneHierarchyVertex(newSceneName, representation);
        hierarchy.addVertex(learnedScene);
        return learnedScene;
    }

    public KnowledgeBase closeReopen(String newSceneName, SITABox representation, Object onSynch) {
        // overcome bug that stores learned Scene class subsumption is not working if syntax not parsed again
        synchronized (onSynch) {
            long time = System.currentTimeMillis();
            if (!syntaxFile.contains(LEARNER_FILE_AUXILIARY_PATH))
                this.syntaxLearnedFile = syntaxFile + LEARNER_FILE_AUXILIARY_PATH;
            saveTbox(syntaxLearnedFile, newSceneName, representation.getObjectDistribution());
            tbox = readFromFile(syntaxLearnedFile, configurationFile);
            KnowledgeBase kb = tbox.clone();  // should it be synchronized statically among all threads using an instance of SITTBox?
            try {
                kb.solveKB();
            } catch (FuzzyOntologyException | InconsistentOntologyException e) {
                e.printStackTrace();
            }
            log(time, "Hierarchy updated from auxiliary file: " + hierarchy);
            return kb;
        }
    }

    // TODO solve issue: it does not remove axioms for fuzzydl file (store deleted scenes)
    // TODO assure new name when reopening file
    public Concept removeScene(SceneHierarchyVertex toRemove){
        synchronized (this.onSync) {
            try {
                String sceneNameToRemove = toRemove.getScene();
                if (!scenes.contains(sceneNameToRemove)) {
                    System.err.println(sceneNameToRemove + " not defined !!!");
                    return null;
                }

                KnowledgeBase kb = tbox.clone();
                // remove scene
                long time = System.currentTimeMillis();
                scenes.remove(sceneNameToRemove);
                Concept toRemoveConcept = tbox.getConcept(sceneNameToRemove);
                hierarchy.removeVertex(toRemove);
                kb.atomicConcepts.remove(sceneNameToRemove);
                tbox.atomicConcepts.remove(sceneNameToRemove);
                objectDistribution.remove(sceneNameToRemove);

                // overcome bug that stores learned Scene class
                // subsumption is not working if syntax not parsed again
                if (!syntaxFile.contains(LEARNER_FILE_AUXILIARY_PATH))
                    this.syntaxLearnedFile = syntaxFile + LEARNER_FILE_AUXILIARY_PATH;
                toWrite = toWrite.replaceAll(ANNOTATION_PREFIX + sceneNameToRemove + ANNOTATION_CARDINALITY_SEPARATOR + getObjectDistributionForFile(toRemove.getObjectDistribution()) + NEW_LINE, "");
                saveTbox(syntaxLearnedFile);
                kb = tbox.clone();
                kb.solveKB();
                time = log(time, "Hierarchy updated from auxiliary file: " + hierarchy);

                updateEdges(kb);
                log(time, "Hierarchy computed: " + hierarchy);

                return toRemoveConcept;
            } catch (InconsistentOntologyException | FuzzyOntologyException e) {
                e.printStackTrace(); // todo throw exception (for gui)
            }
        }
        return null;
    }

    // used during learn it creates the left shoulder description given a sigma counter
    private Concept getRestriction(KnowledgeBase kb, SigmaCounters.Sigma sigmaCnt) {
        try {
            // set right shoulder based on sigma count
            double top = sigmaCnt.getRoundedCount();
            double bottom = top - ((this.fuzziness * top) / 100);
            bottom = DoubleFormatter.roundDegree(bottom);
            // set the name (used also on parsing during semantic build)
            String t = String.valueOf(top).replace(".", REPLACE_DOUBLE_POINT);
            String atLeastName = ROLE_ATLEAST + t;
            RightConcreteConcept restriction;
            if (tbox.concreteConcepts.contains(atLeastName))
                restriction = (RightConcreteConcept)
                        tbox.concreteConcepts.get(atLeastName);
            else restriction = new RightConcreteConcept(atLeastName,
                    ROLE_SHOULDER_MIN, ROLE_SHOULDER_MAX, bottom, top);
            kb.addConcept(atLeastName, restriction);
            tbox.addConcept(atLeastName, restriction);
            // set as (some 'role' 'atLeastX')
            return Concept.some(sigmaCnt.getRole(), restriction);
        } catch (FuzzyOntologyException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Asks to the {@code fuzzydl} reasoner to solve the queries in the syntax file
     * load in {@code this} class during constructor.
     * Used mainly for debugging purposes.
     * The results will be printed on console.
     */
    public void solveSyntaxQueries() {
        try {
            KnowledgeBase kb = tbox.clone();
            kb.solveKB();
            solveSyntaxQueries( kb);
        } catch (FuzzyOntologyException |
                InconsistentOntologyException e) {
            e.printStackTrace();
        }
    }
    /**
     * Asks to the {@code fuzzydl} reasoner to solve the queries in the knowledge base
     * given on constructor, typically from syntax file.
     * Used mainly for debugging purposes.
     * The results will be printed on console.
     * @param kb the fuzzydl ontology containing the queries to solve.
     */
    public void solveSyntaxQueries(KnowledgeBase kb) {
        try {
            // evaluate other syntaxQueries on the ontology in fuzzy dl syntax
            for ( Query q : syntaxQueries) {
                Solution result = q.solve( kb);
                if ( result.isConsistentKB())
                    System.out.println( "\t[" + q.getTotalTime() + "s] \t" + q + result.getSolution());
                else System.err.println( "\t\t !!! KB is inconsistent !!! ");
            }
        } catch (FuzzyOntologyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the complete graph of scenes in the ontology (may also {@link #learn(String, SITABox)}ed).
     * In this graph each {@link SceneHierarchyEdge} stands for an fuzzy {@code implication} axiom between two
     * scene definitions. The latter, {@link SceneHierarchyVertex}s are fuzzy sets describing specific scenes.
     * @return the root to the scene class hierarchy, inferred from the ontology.
     */
    public ListenableGraph<SceneHierarchyVertex, SceneHierarchyEdge> getHierarchy() {
        return hierarchy;
    }

    /**
     * On {@link #learn(String, SITABox)} we are updating the T-Box, this requires
     * that the reasoner is update properly. To overcome this problem {@code this}
     * class stores always a fresh copy of the fuzzy T-Box, where the reasoner is not initialised.
     * A copy of such of T-Box can be accessed by this method, in this ontology, the reasoner
     * can be initialised (see {@link KnowledgeBase}) safely.
     * @return a copy of the SIT ontology.
     */
    public KnowledgeBase getTboxCopy() {
        return tbox.cloneWithoutABox();
    }

    /**
     * This class augment the fuzzy ontology syntax by adding some annotations.
     * This methods save the fuzzy {@link KnowledgeBase} and that it appends
     * also the object distribution annotations.
     * @param path the path where to save the augmented ontology.
     */
    public void saveTbox( String path){
        try {
            long time = System.currentTimeMillis();
            tbox.saveToFile( path);
            Files.write(Paths.get(path), toWrite.getBytes(), StandardOpenOption.APPEND);
            log( time, "saving T-box in: " + path);
        } catch (FuzzyOntologyException |
                IOException e) {
            e.printStackTrace();
        }
    }
    // used during learn, is updates the annotation to write and saves the T-Box to file
    private void saveTbox( String path, String sceneName, Collection<Map<String, Double>> distribution) {
        toWrite += ANNOTATION_PREFIX + sceneName + ANNOTATION_CARDINALITY_SEPARATOR + getObjectDistributionForFile( distribution) + NEW_LINE;
        saveTbox( path);
    }
    // converts the given Collection in a string to store annotating object distribution.
    // this method is in accord with the above parsing mechanisms.
    private String getObjectDistributionForFile(Collection<Map<String, Double>> objectDistribution){
        String mapToString = "";
        for ( Map<String,Double> distribution : objectDistribution)
            mapToString += distribution.toString().replaceAll( " ", "").replaceAll("\\{","").replaceAll("}","") + ";";
        return mapToString;
    }

    /**
     * @return all the names of the fuzzy classes that represents all the
     * type of objects (π ⊂ Object).
     */
    public Collection<String> getObjectType() {
        return objectType;
    }

    /**
     * @return all the names of the fuzzy class that represents a SIT scene
     * (Λ ⊂ Spatial).
     */
    public Collection<String> getScenes(){ // is null if not queried
        return scenes;
    }

    /**
     * @return the Map<Δ,δ> between classes for spatial relations within a
     * scene (Δ), given in the ontology; and Scene Properties (δ ≡ "hasΔ").
     */
    public Map<String, SpatialProperty> getSpatial() {
        return spatial;
    }
    /**
     * This is just a shortcut for {@code {@link #getSpatial()}.keySet()}.
     * @return the set of all spatial relation classes in the ontology (Δ).
     */
    public Set< String> getSpatialClasses() {
        return spatial.keySet();
    }
    /**
     * This is just a shortcut for {@code {@link #getSpatial()}.values()}.
     * @return the set of all spatial relation property in the ontology (δ).
     */
    public Collection< SpatialProperty> getSpatialRelation() {
        return spatial.values();
    }

    /**
     * @return the file path of the ontology syntax given on constructor.
     */
    public String getSyntaxFile() {
        return syntaxFile;
    }
    /**
     * @return the file path of the fuzzydl configuration file given on constructor.
     */
    public String getConfigurationFile() {
        return configurationFile;
    }
    /**
     * @return the file path to the T-Box auxiliary file used
     * during {@link #learn(String, SITABox)}. It also contains
     * all the annotation used by the SIT while, {@link #getSyntaxFile()}
     * returns the original ontology.
     */
    public String getSyntaxLearnedFile() {
        return syntaxLearnedFile;
    }

    /**
     * Used for debugging purposes, this method shows the
     * actual scene {@link #getHierarchy()}.
     */
    public void show() {
        SwingUtilities.invokeLater(() -> {

            frame = new JFrame("Scene Hierarchy (" + this.fuzziness/100 + ")");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


            /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int height = screenSize.height * 2 / 3;
            int width = screenSize.width * 2 / 3;
            frame.setPreferredSize(new Dimension(width, height));
            */
            updateShowing();
        });
    }
    // used by show for using a different thread.
    private void updateShowing() {
        SwingUtilities.invokeLater(() -> {
            if (frame == null) {
                System.err.println("null frame, call show before to update a graph!!");
                return;
            }
            if (hierarchy == null) {
                System.err.println("null semantic, build semantic before to update a graph!!");
                return;
            }

            JGraphXAdapter<SceneHierarchyVertex, SceneHierarchyEdge>
                    graphAdapter = new JGraphXAdapter<>(hierarchy);

            graphAdapter.setCellsDisconnectable( false);

            mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter);
            layout.execute(graphAdapter.getDefaultParent());


            mxGraphComponent graphPanel = new mxGraphComponent(graphAdapter);
            graphPanel.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
            frame.add( graphPanel);

            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }

    /**
     * @return {@link SubsumptionQuery#LUKASIEWICZ} or {@link SubsumptionQuery#ZADEH}
     * based on the type of fuzzy language is used in the ontology syntax file
     * given on constructor.
     */
    public int getQueryLogic(){
        int logic = SubsumptionQuery.LUKASIEWICZ;
        if( tbox.getLogic().toString().equalsIgnoreCase( "zadeh"))
            logic = SubsumptionQuery.ZADEH;
        return logic;
    }

    /**
     * This methods save the ontology in the OWL format.
     * To use this method you should not have a {@link SITTBox} loaded
     * in the same file. It is based on {@link FuzzydlToOwl2}.
     * @param fuzzydlPath the path to the fazzydl syntax to store.
     * @param owlPath the path to the new owl file.
     * @deprecated the {@ocde owlPath} is not considered,
     * the file will be always saved in the root of the project.
     * Also the OWL generation lends to error during data type
     * restriction generation.
     */
    @Deprecated
    public static void saveToOWL( String fuzzydlPath, String owlPath){
        // save as owl file (do not open this file with the fuzzy parser, otherwise gives ERROR)
        //Parser.reset();
        FuzzydlToOwl2 f = new FuzzydlToOwl2( fuzzydlPath, owlPath);
        f.run();
    }

    public void setFuzziness(double fuzziness){
        this.fuzziness = fuzziness;
    }

    /*
    public Set< Pair< SceneHierarchyVertex,SceneHierarchyVertex>> simplify(double minImplicationDegree) { // remove equivalent nodes (i.e., with double high implication)
        Set< Pair< SceneHierarchyVertex, SceneHierarchyVertex>> toRemove = new HashSet<>();
        for ( SceneHierarchyVertex vertex1 : hierarchy.vertexSet()) {
            if (!toRemove.contains(vertex1)){
                for (SceneHierarchyVertex vertex2 : hierarchy.vertexSet()) {
                    if (!toRemove.contains(vertex2) & !vertex1.equals(vertex2)) {
                        SceneHierarchyEdge edge1 = hierarchy.getEdge(vertex1, vertex2);
                        SceneHierarchyEdge edge2 = hierarchy.getEdge(vertex2, vertex1);
                        if ( edge1 != null & edge2 != null) {
                            double w1 = getHierarchy().getEdgeWeight(edge1);
                            double w2 = getHierarchy().getEdgeWeight(edge2);
                            if (w1 >= minImplicationDegree & w2 >= minImplicationDegree) { // todo make it constant
                                Pair<SceneHierarchyVertex, SceneHierarchyVertex> outPair;
                                if ( vertex1.getMemoryScore() > vertex2.getMemoryScore())
                                    outPair = new Pair<>( vertex2, vertex1);
                                else outPair = new Pair<>( vertex1, vertex2);
                                toRemove.add( outPair);
                                log( System.currentTimeMillis(), "simplify [remote,equivalent]:" + outPair );
                            }
                        }
                    }
                }
            }
        }
        for ( Pair< SceneHierarchyVertex, SceneHierarchyVertex> r: toRemove)
            removeScene( r.getKey());
        return toRemove; // return list of classes (removed, equivalent)
    }
    */
}

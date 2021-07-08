# The Fuzzy SIT Algorithm
The Scene Identification and Tagging (SIT) algorithm based on Fuzzy Description Logics.

The theoretical background of SIT can be found in
 - L. Buoncompagni, F. Mastrogiovanni and A. Saffiotti,
   ["Scene Learning, Recognition and Similarity Detection in a Fuzzy Ontology via Human Examples,"](http://ceur-ws.org/Vol-2054/paper2.pdf)
   in  the 4th Italian Workshop on Artificial Intelligence and Robotics.
   A workshop of the XVI International Conference of the Italian Association for Artificial Intelligence
   Bari, Italy, November 14-15, 2017, CEUR-WS, Vol-2054.
 - L. Buoncompagni and F. Mastrogiovanni, 
   ["Teaching a Robot how to Spatially Arrange Objects: Representation and Recognition Issues,"](https://ieeexplore.ieee.org/abstract/document/8956457)
   2019 28th IEEE International Conference on Robot and Human Interactive Communication (RO-MAN), 2019, pp. 1-8, doi: 10.1109/RO-MAN46459.2019.8956457. 
 - L. Buoncompagni and F. Mastrogiovanni, 
   ["Dialogue-Based Supervision and Explanation of Robot Spatial Beliefs: a Software Architecture Perspective,"](https://ieeexplore.ieee.org/abstract/document/8525828) 
   2018 27th IEEE International Symposium on Robot and Human Interactive Communication (RO-MAN), 2018, pp. 977-984, doi: 10.1109/ROMAN.2018.8525828.
 - L. Buoncompagni and F. Mastrogiovanni,
   ["A Framework Inspired by Cognitive Memory to Learn Planning Domains From Demonstrations,"](http://ceur-ws.org/Vol-2594/short4.pdf)
   in  Proceedings of the 6th Italian Workshop on Artificial Intelligence and Robotics
   co-located with the XVIII International Conference of the Italian Association for Artificial Intelligence (AIxIA 2019)
   Rende, Italy, November 22, 2019, CEUR-WS, Vol-2594.

# Installation

### Install Java8 Dependence
If you did not already, in an Ubuntu machine, install `Java JDK 8` with
```sh
sudo apt update
sudo apt install openjdk-8-jdk
```
Locate the Java virtual machine installed with
```sh
jrunscript -e 'java.lang.System.out.println(java.lang.System.getProperty("java.home"));' 
```
It might return `/usr/lib/jvm/java-8-openjdk-amd64/jre`, which will be used later.

### Install FuzzyDL and Gurobi Dependence
FuzzySIT depends on [FuzzyDL](https://www.umbertostraccia.it/cs/software/fuzzyDL/fuzzyDL.html), which have been tested at the version of the 09 January 2019.
The FuzzyDL library comes as a `jar` that should be placed in the `/libs` folder.

FuzzyDL depends on [Gurobi](https://www.gurobi.com/) which gives a free license for Academic and non-commercial purpose. 

After having a Gurobi account and the related license, download the [Gurobi Optimizer](https://www.gurobi.com/downloads/gurobi-software/) tools. 
We used the version 8.0.1, which can be downloaded in Ubuntu with
```sh
wget https://packages.gurobi.com/8.1/gurobi8.1.0_linux64.tar.gz
```
Then, uncompress it in the `/opt` folder with
```sh
sudo tar -xf gurobi8.1.0_linux64.tar.gz -C /opt/
```

Now add the following environmental variables in the `.bashrc` and configure `JAVA_HOME` as found previously
```sh
export GUROBI_HOME="/opt/gurobi810/linux64"
export PATH="${PATH}:${GUROBI_HOME}/bin"
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"
export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64/jre"
```
Close and reopen the terminal, then generate the license file with
```sh
grbgetkey <licence_id>
```
where `<licence_id>` is given by Gurobi when you activate your license.
The latter command should generate a file `gurobi.lic` in your home.

Now you can test FuzzyDL reasoner by referring to the `README` file of the [FuzzyDL](https://www.umbertostraccia.it/cs/software/fuzzyDL/fuzzyDL.html) package.

## Install FuzzySIT and Run an Example
To test FuzzySIT, clone it and (eventually) checkout in a relevant branch
```sh
git clone https://github.com/TheEngineRoom-UniGe/fuzzy_sit.git
```

To compile it go in the `fuzzy_sit` folder just cloned and run
```sh
gradle wrapper --gradle-version 6.8
```
which might require to install `Gradle` with `sudo apt install gradle`.
Then run 
```sh
./gradlew build
```

Finally, run the [`SceneHierarchyTest`](https://github.com/EmaroLab/fuzzy_sit/blob/master/src/test/java/it/emarolab/fuzzySIT/semantic/SceneHierarchyTest.java) example with
```sh
./gradlew runExample
```
If your machine does not have a graphical user interface, the example will fail at the end of the script 

To run other examples or to generate a jar of FuzzySIT as a library for your development we suggest to open the `fuzzy-sit` project with an IDE (e.g., [Intellij IDEA](https://www.jetbrains.com/idea/)) configured based on your `.bashrc`.

# FuzzySIT Ontology Setup
SIT requires a FuzzyDL ontology (i.e., a file `.fuzzydl`) that is formatted in a specific manner.

It requires a header as
```
(define-fuzzy-logic zadeh)

(define-primitive-concept Object *top*)
(define-primitive-concept Scene  *top*)

(disjoint Object        Scene)
(disjoint SpatialObject Scene)
```
Then you should define the type of elements in the scene, e.g., for `Spere` and `Cone` use
```
(implies Sphere Object)
(implies Cone   Object)
```
Later you should define the type of relations among elements, e.g., `Right` and `Left`
```
(range  isRightOf Object)
(domain isRightOf SpatialObject)

(range  isLeftOf  Object)
(domain isLeftOf  SpatialObject)
```
Note that this implementation of fuzzyDL is case-sensitive and parses based on the CamelCase standard
(a known issue occurs when variables ends with a number, e.g., `Right1`).
Always add `is` to the definition of the relations, while `Of` is optional.

Finally, we should define the features used by SIT to learn. 
Those are all the combinations of the types of elements and relations above obtained through *reification*, e.g.,
```
(define-concept SphereRight  (and Sphere (some isRightOf  SpatialObject)))
(define-concept SphereLeft   (and Sphere (some isLeftOf   SpatialObject)))

(define-concept ConeRight    (and Cone   (some isRightOf  SpatialObject)))
(define-concept ConeLeft     (and Cone   (some isLeftOf   SpatialObject)))
```
In this case, the `SphereRight` class represents a feature in the scene where
"a sphere as something on the right".

You can configure and add as many types of elements and relations you like but be aware that computation complexity scales exponentially.

# FuzzySIT API Structure

The core of FuzzySIT is composed by three main classes
 - `FuzzySITBase` which is a container for common constants and configuration, e.g., `FLAG_LOG_SHOW`.
 - `SITABox` which allows encoding and recognising a scene.
   A new `SITABox` should be instantiated for each observed scene.
 - `SITTBox` which contains the scene categories in the memory. 
   Only an instance of `SITTBox` should be used for each application. `SITTBox` allows 
     - learning new scenes by the means of `SITABox`,
     - access and visualize the hierarchy of scenes structured on the basis of the ontology,
     - save or lead ontologies.
   
## Runnables: Tests, Extensions and Examples

The FuzzySIT package comes also with some utilities used for testing purposes.
In particular, in the `runnableSITutility` contains the following runnable scripts.

Both the FuzzySIT core, and the utilities read and write on auxialry file stored in the `resources` folder.

#### 1. `FuzzydlTest`
It is a script to test the FuzzyDL reasoner by loading an ontology and solve queries.

#### 2. `SITExample`
Is an example to show the usage of the FuzzySIT API base on a toy scenario.

#### 3. `GUI`
Is a graphical user interface to simulate 2D arranges of objects.
It is based on the `JavaFx` library, and it is currently commented since deprecated.

#### 4. `SceneHierarchyTest`
Show a simulation of the results published in the paper concerning the hierarchy of spatial scenes in memory.

#### 5. `MemoryTest`
Is a simulation done to publish results about a cognitive like memory, involving consolidation and forgetting of scene categories learned during time.

#### 6. `MonteCarlo`
Is a preliminary simulation based on a Montecarlo approach to reconstruct a scene given its learned category.

#### 7. `ComputationComplexity` 
Is a simulation to log the SIT computation complexity for later evaluations.

You can run the computation complexity test with the command
```sh
gradle clean runComputationTest -Pconcepts='2,6' -Prelations='2,4' -Pscenes='2,2,2' -Pelements='2,3,4' -PtasksLimit='2'
```
where
 - `-Pconcepts` sets the number of possible types of object in the ontology,
 - `-Prelations` sets the number of possible relations among objects in the ontology,
 - `-Pscenes` sets the number of learning and recognition phase to perform (i.e., number of memory items at the end of the evaluation`),
 - `-Pelements` sets the number of objects in each scene to evaluate. From this the number of relations in a scene is derived having, between each object added consegutively, (i) two relations plus (2) another relation with a probability of 0.5.
 - `-PtasksLimit` sets the maximum number of threads to be used concurrently for the simulation.

The test is performed for each combination of `concepts`, `relations`, `scenes` and `elements`. 
The results are store as `csv` files in the folder `src/main/java/resources/ComputationComplexityTest/log`.
CSV data is all express in millisecond or integer (e.g., for indices) and arranged in columns ordered as
CSV data is all express in millisecond or integer (i.e., indices) and arranged in columns as: `A,B,C,...,O` where  
 - A) An ordered identified based on the creation timestamp,  
 - B) A string identifying the ontology complexity (i.e., `C-D`),  
 - C) The Number of concepts in the ontology,  
 - D) The Number of relations in the ontology,  
 - E) Number of elements in the scene,  
 - F) Number of roles in the scene,  
 - G) Number of items in the memory,  
 - H) The encoding time before having learned a new scene,  
 - I) The recognition time before having learned a new scene,  
 - J) The learning time,  
 - K) The structuring time,  
 - L) The encoding time after having learned a new scene,  
 - M) The recognition time after having learned a new scene,  
 - N) The total time,  
 - O) Computer memory usage in Byte,
 - P) Number of categories pre-recognised, 
 - Q) Number of categories post-recognised.

# Contacts
**Author**: Luca Buoncompagni,  
**Affiliation**: University of Genoa,  
**Date**: 2021  
**Email**: luca.buoncompagni@edu.unige.it (please, prefer GitHub issue if appropriate)  
**Licence**: The FuzzySIT packager are under the GNU General Public License v3.0, while the dependences (i.e., Java, 
FuzzyDL, Gurobi, and jgrapht) are subjected to their relative licences.
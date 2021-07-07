# The Fuzzy SIT Algorithm
The Scene Identification and Tagging (SIT) algorithm based on Fuzzy Description Logics.

# Installation

## Install Java8 Dependence
If you did not already, in an Ubuntu machine, install `Java JDK 8` with
```sh
sudo apt update
sudo apt install openjdk-8-jdk
```
Locate the Java virtual machine installed with
```sh
jrunscript -e 'java.lang.System.out.println(java.lang.System.getProperty("java.home"));' 
```
Which might return `/usr/lib/jvm/java-8-openjdk-amd64/jre`, which will be used later.

## Install FuzzyDL and Gurobi Dependence
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

Now add the following environmental variables in the `.bashrc` 
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
If your machine does not have a graphical user interface, the example might fail at the end of the script 






Find examples in the `test` [folders](https://github.com/EmaroLab/fuzzy_sit/tree/master/src/test/java/it/emarolab/fuzzySIT).

For more configuration see the constants defined (e.g., `FLAG_LOG_SHOW`) in [FuzzySITBase](https://github.com/TheEngineRoom-UniGe/fuzzy_sit/blob/master/src/main/java/it/emarolab/fuzzySIT/FuzzySITBase.java).

To run other examples or to generate a jar of FuzzySIT as a library for your development we suggest to open the `fuzzy-sit` project with an IDE (e.g., [Intellij IDEA](https://www.jetbrains.com/idea/)) configured based on your `.bashrc`.


# Runnable: Test, Extensions and Examples

### `FuzzydlTest`
### `SITExample`
### `GUI`
### `SceneHierarchyTest`
### `MemoryTest`
### `MonteCarlo`
### `ComputationComplexity` 
You can run it with the command
```sh
gradle clean runComputationTest -Pconcepts='2,6' -Prelations='2,4' -Pscenes='2,2,2' -Pelements='2,3,4' -PtasksLimit='2'
```
where
 - `concepts` sets the number of possible types of object in the ontology,
 - `relations` sets the number of possible relations among objects in the ontology,
 - `scenes` sets the number of learning and recognition phase to perform (i.e., number of memory items at the end of the evaluation`),
 - `elements` sets the number of objects in each scene to evaluate. From this the number of relations in a scene is derived having, between each object added consegutively, (i) two relations plus (2) another relation with a probability of 0.5.
 - `tasksLimit` sets the maximum number of threads to be used concurrently for the simulation.

The test is performed for each combination of `concepts`, `relations`, `scenes` and `elements`. 
The results are store as `csv` files in the folder `src/main/java/resources/ComputationComplexityTest/log`.
CSV data is all express in millisecond or integer (i.e., for indices) and arranged in columns ordered as
1. An ordered identified based on the creation timestamp,
2. A string identifying the ontology complexity (i.e., `C-D`),
3. The Number of concepts in the ontology,
4. The Number of relations in the ontology,
5. Number of elements in the scene,
6. Number of roles in the scene,
7. Number of items in the memory,
8. The recognition time before having learned a new scene,
9. The learning time,
10. The recognition time after having learned a new scene,
11. The total computation time.

# Ontology Setup and API Structure
...


# API Documentation and Reports

The `core` API of  FuzzySIT is provided with a preliminar Javadoc. 
More documentation and scientific papers will come soon.

# Contacts
**Author**: Luca Buoncompagni,  
**Affiliation**: University of Genoa,  
**Date**: 2021  
**Email**: luca.buoncompagni@edu.unige.it (please, prefer GitHub issue if appropriate)  
**Licence**: The FuzzySIT packager are under the GNU General Public License v3.0, while the dependences (i.e., Java, 
FuzzyDL, Gurobi, and jgrapht) are subjected to their relative licences.



# fuzzy_sit
The Scene Identification and Tagging (SIT) algorithm based on Fuzzy Description Logics.

More documentation and scientific papers will come shortly.

# Installation

FuzzySIT depends on `Java8` and [FuzzyDL](https://www.umbertostraccia.it/cs/software/fuzzyDL/fuzzyDL.html), which have been tested at the version of the 09 January 2019.
The FuzzyDL library comes as a `jar` that should be placed in the `/libs` folder.

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
Which might be `/usr/lib/jvm/java-8-openjdk-amd64/jre` and it will be used later.

## Install FuzzyDL and Gurobi Dependence
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
where <licence_id> is given by Gurobi when you activate your license.
The latter command should generate a file `gurobi.lic` in your home.

Now you can test FuzzyDL reasoner by referring to the `README` file of the [FuzzyDL](https://www.umbertostraccia.it/cs/software/fuzzyDL/fuzzyDL.html) package.

## Install FuzzySIT and Run an Example
Instead, to test FuzzySIT, clone it and (eventually) checkout in a relevant branch
```sh
git clone https://github.com/TheEngineRoom-UniGe/fuzzy_sit.git
git checkout ComputationTest
```

To compile it go in the `fuzzy_sit` folder just cloned and run
```sh
gradle wrapper --gradle-version 6.8
```
which might require to install `Gradle` with `sudo apt install gradle`.
Then compile it with 
```sh
./gradlew build
```

Finally, run the [`SceneHierarchyTest`](https://github.com/EmaroLab/fuzzy_sit/blob/master/src/test/java/it/emarolab/fuzzySIT/semantic/SceneHierarchyTest.java) example with
```sh

```
If your machine does not have a graphical user interface, the example might fail at the end of the script 

Find examples in the `test` [folders](https://github.com/EmaroLab/fuzzy_sit/tree/master/src/test/java/it/emarolab/fuzzySIT).

For more configuration see the constants defined (e.g., `FLAG_LOG_SHOW`) in [FuzzySITBase](https://github.com/TheEngineRoom-UniGe/fuzzy_sit/blob/master/src/main/java/it/emarolab/fuzzySIT/FuzzySITBase.java).

To run other examples or to generate a jar of FuzzySIT as a library for your development we suggest to open the `fuzzy-sit` project with an IDE (e.g., [Intellij IDEA](https://www.jetbrains.com/idea/)) configured based on your `.bashrc`.


# Ontology Setup and API Structure 

# Runnable: Test, Extensions and Examples

. `FuzzydlTest`
. `SITExample`
. `GUI`
. `SceneHierarchyTest`
. `MemoryTest`
. `MonteCarlo`
. `ComputationComplexity`

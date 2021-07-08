#!/bin/bash

# gradle clean build  runComputationTest -Pconcepts='2,4,6,8' -Prelations='8,6,4,2' -Pscenes='22,22,22' -Pelements='4,10,19,33' -PtasksLimit='13'

gradle clean build runComputationTest -Pconcepts='2,8' -Prelations='8,6,4,2' -Pscenes='22,22,22' -Pelements='4,10,19,33' -PtasksLimit='6' &
gradle clean build runComputationTest -Pconcepts='6,4' -Prelations='8,6,4,2' -Pscenes='22,22,22' -Pelements='4,10,19,33' -PtasksLimit='6'
# music108

This is my final project for Music 108: Cognitive Perception of Music

It is a program that works adjunct with jMIR software. It analyzes select features of a corpus of music (which are chosen when the jMIR files run),
and then it takes in another song and outputs scores that describe how similar that song is to the corpus of music for each of the chosen
features.

I have included featureResults.xml, sample.xml, and averages.txt in order to give an idea of the program output. When jMIR software 
is run on the corpus of music, it returns featureResults.xml, a file with all of the information and averages for the corpus of music.
Next, we run the jMIR software with just the one song that we are testing against the corpus. That outputs the feature values into 
sample.xml, and then my program strips the information form both of these xml files and compiles that information with calculated "scores" in averages.txt.

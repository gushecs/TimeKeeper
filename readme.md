# Chronos (TimeKeeper)

The idea of this project is to have an offline simple program for the creation of
chronometer and timer clusters to keep track of time in multiple activities. You
can use this program to create a family of independent timer or chronometer
objects to keep track of work and study. All objects can be saved individually 
and the program has persistent memory.

## Object types

### Chronometer

Simple object that start on time = 0 and keeps track of time spent. Has time
control manipulation features (so you can add or subtract time to the elapsed
time)

### Timer

Object that operates on a countdown style. An initial time must be set for use.
Once the starting time is defined, the Timer Object will count until it reaches
zero, when it will alert the user. The ending of time has a visual cue (time and
the program itself blinks when it reaches zero) and a sound alert that can be
turned off by the user.

### Common features from both

Both time objects can be individually saved, situation in which the set
parameters will be stored in a paused object. When the application is closed,
either object will be saved with the time it had on exit and will be paused on
start. Both objects have a log in which you can track the activity done on the
object. You can also log custom texts, by typing it on the lower text box of the
object and pressing the *log* button.

## About the program

Chronos was developed with organization in mind. You can order all time tracking
objects as well as save and load them individually. The *Silence* option turns off
the sound alert of expired timers. There is a *Dark Mode* that reflects on all
objects live. both configurations are persistent and will be load along with the
program just as you left them.

## How to use

There's a *.rar* file in this repository with an *.exe* extension file for simple
usage of Chronos on Windows. Alternately, you can use the *.jar* file, also 
located in the *.rar*, to execute the program, as long as you got *Java* installed
in your machine. The program was built with *Java 17* and might present unexpected
behaviours with other versions.

If you'd rather build the program yourself, it's recommended to use Maven to build 
this project. If you're not sure if your machine got Maven installed, open a 
terminal and type the command:

> mvn -v

If you got Maven installed, the computer should recognize the command and display 
its version.

To package the project into a *.jar* file, simply open the terminal on the
project's source folder and run the command:

> mvn package

Maven should recognize the *pom.xml* file, downloading the dependencies and
packaging the program. After that, a *target* folder will be created in the
directory, containing a *.jar* file which can be executed in your machine.

Alternately, you can compile the program with *javac* and then run it using the
*java* command. To do so, however, you'll need to set the encoding type to utf-8
(appending *-encoding utf-8* to the javac command) as well as download manually
the dependencies specified in the *pom.xml* file and appending its path to the
javac command with *-cd*. Running the command on the source folder, it should 
look something like this:

>javac -cd [your-dependencies-dir-path] -encoding utf-8 src/main/java/chronos/*.java
 
The sound on the program will only work if the *timeExpired.wav* file is on the
*mem* folder, inside the source folder.
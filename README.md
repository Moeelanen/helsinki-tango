![Helsinki Tango](https://raw.githubusercontent.com/Moeelanen/helsinki-tango/main/media/logo.png)
End project for Aalto O1 first year course. You play as a taxi driver in a time when there wasn't any navigators to help you along. Find your way to the finish with just the help of streetsigns and cardinal directions. 

# Installation
The game is openly available to be played online, at https://moelanen.xyz/helsinki-tango/. No installation required!

As per the requirements of Aalto O1 course, Helsinki Tango uses Scala version 2.13.6. As per the liberties I've taken, the following dependencies have been added:
```scala
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.10.0"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
```

The code is compiled into Javascript, and **does not** run in JVM

To build, 
```
git clone
cd helsinki-tango/
sbt fullLinkJS
```

Open the index.html file in your browser. Since the map data is accessed through a web request from my server, you'll have to have a way of passing a CORS check.

If you want to host the files yourself, the files are provided and the addresses can be changed.

# Walkthrough
Gameplay is simple, the challenge comes from not looking at Google Maps. To play, click the street names, when two or more options are shown, otherwise just enjoy the ride.
A name inside brackets ```()``` indicates a road that doesn't have a name, but leads to the one shown.

To get a better feeling of how the traversal works in Helsinki Tango, the following walkthrough will get you exploring.
- Start
- Click the dialogue
1. Tasetie (West)
2. Tasetie (West)
3. Lentoasemantie (North)
4. (Kehä III) (Southeast)
5. (Tuusulanväylä) (South)

#### Can you find your way around town?


# In-depth description
Helsinki Tango is built with scala, using scalajs to take use of the HTML Canvas to display a simple user interface.

## Map
![map](https://github.com/Moeelanen/helsinki-tango/blob/main/media/helsinki.png "Map of the entire play area")
---
This is an image created from the map data used for the game. Map data is exported from OpenStreetMap, and made into a machine readable graph format with [OsmRoadToGraph](https://github.com/AndGem/OsmToRoadGraph). All of the map related data - with the exception of the .osm file - can be viewed in their respective folder. Data is processed with python to generate easily digestable data for Scala. Overrall the game has a combined 250,000 rows of nodes and edges.

Traversing the map is just following the edges from one node to the other.

Unfortunately the data isn't perfect, some intersections are awkward and some streets might wrongly be dead ends.

## Come back for more; after I've had some rest! 

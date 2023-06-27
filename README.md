
# Final project of Software Engineering Group AM07 
![My Shelfie](https://github.com/ilboglions/IS23-AM07/blob/66395bdf3670a9df1a04b42146a1f867596e068f/src/main/resources/images/PublisherMaterial/Title_2000x618px.png)
## Overview
This repository contains the digital java implementation of the game **My Shelfie**, published by **Cranio Games**.
My Shelfie is a game where you and your friends have to set up your new library starting from a messy livingroom, following
specifics rules.

## Authors
Politecnico di Miliano - Prof. Margara Section - **Group AM07** 
-  **10719101 Filippo Balzarini** ([@filomba01](https://github.com/filomba01)) _filippo.balzarini@mail.polimi.it_
-  **10787158 Christian Biffi** ([@creix](https://github.com/creix)) _christian.biffi@mail.polimi.it_
-  **10706553 Michele Cavicchioli** ([@trueMaicol](https://github.com/trueMaicol)) _michele.cavicchioli@mail.polimi.it_
-  **10766461 Matteo Boglioni** ([@ilboglions](https://github.com/ilboglions)) _matteo.boglioni@mail.polimi.it_

# Development State

| Functionality   | State | Current | Comment                                         |
|-----------------|----| ---- |-------------------------------------------------|
| Model           | :green_circle: | |                                                 |
| Controller      | :green_circle: | |                                                 |
| Chat            | :green_circle: | |                                                 |
| Multi Game      | :green_circle: | |                                                 |
| Game Resiliance | :green_circle: | |                       |
| RMI             | :green_circle: | |                                                 |
| TCP             | :green_circle: | |                                                 |
| CLI             | :green_circle: | |  |
| GUI             | :green_circle: | |                                                 |

## Installation
In order to run My Shelfie, you should clone this repository
```
git clone https://github.com/ilboglions/IS23-AM07.git
```

## Running the game
The game consists of a single jar file by the name AM07-jar-with-dependencies.jar. It can be found in /target directory after building the project with Maven.
This file holds both the Server, the CLI and the GUI applications, one of which can be selected when booting.
To run the jar file, use the command
```
java -jar AM07-jar-with-dependencies.jar
```
from the command line in the jar's folder.

Based on the running configuration needed, some parameters should be passed during the launch of the application, if no one is given,
the jar will start as a Server with default configuration, written in the file _HostAndPort.json_

### Run as a Server
In order to correctly run the game as a server, you need to specify:
- the ```--SERVER``` parameter
- the host ip address ```10.53.2.4```
- both the port for TCP and RMI ```4567 1999```

an example of a complete launch configuration is:
```
java -jar AM07-jar-with-dependencies.jar --SERVER 10.53.2.4 4567 1999
```
### Run as a Client
The above described procedure is very similar for the client, you have to specify:
- either ```--GUI``` or ```--CLI```, based on which graphical interface you want to use
- the type of connection, which can be ```--TCP``` ```--RMI``` 
- the server ip address ```10.53.2.4```
- the port of the server where you want to connect, such as ```4567```

below an example of a complete launch configuration:
```
java -jar AM07-jar-with-dependencies.jar --GUI --TCP 10.53.2.4 4567
```
### License
All rights to My **Shelfie** are owned by Cranio Games,  which provided the graphical 
resources to be used for educational purposes only.
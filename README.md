# Song Sorter
**Data Structures and Algorithms Final Project**

## Description
A program that takes an input as a mp3 file, Spotify song link, or Spotify playlist link, and passes them through a [Long Short Term Memory Model](https://en.wikipedia.org/wiki/Long_short-term_memory) which can interpret the music clip and return a coordinate which represents the "arousal" and "valence" on [Russel's Circumplex Model of Affect](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2367156/). This model essentially describes the emotion a music clip induces in a listener quantitatively. Then, each song's metadata is linked to an emotional coordinate. These coordinates will be sorted such that every point is covered in the minimum possible distance (traveling salesman problem). This will be implemented with a heuristic algorithm. The newly sorted playlist will be sorted by emotion and returned to the user for a better listening experience. 

### Task Priority
1. Playlist/Song input via Spotify link or filepath in GUI
2. Making a command line application which allows for a filepath to be passed ot the model in Python
3. Calling command line application via Java
4. Parsing outputs
5. Sorting the songs
    * Implement it using a traditional sorting method in two dimensions
    * Implement a heuristic to solve it as a traveling salesman problem
6. Rendering outputs to screen 


## Inputs
A public playlist link or song link from spotify, or a mp3 file. 

## Outputs
The arousal and valence coordinates (see Russel's Circumplex Model of Affect) of the single song or series of songs in a playlist. The optimized and sorted playlist, if applicable. 

## Data Structure
### Queue
 - Used to process each `Song` object from a playlist one by one, in the order that they appear. 
 - Also used when creating the panels for each `Song` object
 - Computation occurs in background


## Sources
1. Geeksforgeeks Staff (2024) Stream In Java [Tutorial article]. https://www.geeksforgeeks.org/stream-in-java/. 

1. Gilbert, D. (2023) JFreeChart (Version 1.5.4) [Source Code as Library]. https://www.jfree.org/jfreechart/. 

1. Jhanji, E. (2023) Exploring and Applying Audio Based Sentiment Analysis (Version 1.0) [Source Code]. https://github.com/etashj/Exploring-and-Applying-Audio-Based-Sentiment-Analysis. 

1. Koether R. T. (2016) The Traveling Salesman Problem Nearest-Neighbor Algorithm (Lecture 33 Sections 6.4) [Presentation]. https://people.hsc.edu/faculty-staff/robbk/Math111/Lectures/Fall%202016/Lecture%2033%20-%20The%20Nearest-Neighbor%20Algorithm.pdf. 

1. Posner, J., et al. (2005) The circumplex model of affect: an integrative approach to affective neuroscience, cognitive development, and psychopathology. (Dev Psychopathol.*) [Scientific Article]. https://doi.org/10.1017%2FS0954579405050340. 

1. Thelin, M., Thelemann, J., et al. (2024) Spotify Web API Java (Version 8.4.1) [Source code as Library]. https://github.com/spotify-web-api-java/spotify-web-api-java.  

1. Yik, M., et al. (2011) A 12-Point Circumplex Structure of Core Affect (*Emotion* Vol. 11 4) [Scientific Article]. https://api.semanticscholar.org/CorpusID:17943400. 
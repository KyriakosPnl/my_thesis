# **Ranking spatio-textual large-scale data based on innovative ways of classification**


With the latest technologies aimed at wireless internet connection through smart phones and the
implementation of more and more actions through them, the need arises for the development of
innovative tools that meet the needs of users. Another important piece of modern technology is the
ability to find the location of each user, as well as the personalization offered by it. Smartphones
offer users the ability to search for points of interest, which are within a certain distance and meet
certain conditions, according to criteria set by the user. These points can be companies that offer
products or services. The purpose of this Thesis is the classification of spatial-textual data that
represent some points of interest, according to the searches performed for them by users from a
specific location. To rank the points of interest, we create a graph, where in combination with rank
algorithms, provide useful conclusions.

# General

This thesis / project was developed at the Information and Comunication Systems Department at the University of the Aegean (Greece).
The main idea was to create a graph based on spatio-textual large scale data, and also use some keywords to define a relationship between the nodes.
After the creation of the graph, we use ranking algorithms, to extract some useful information about the data. The project also provides some extra functionalities, such as:

- Randomly create a log file .
- Display the top-k answers refering to a specific query.
- Import / Export the graph.
- Visualize the final graph.

The implementation is in Java language, and the jgrapht (1.4 version) library (https://jgrapht.org).

# Data

 The term *spatio-textual data* refers to data that is described by geographical coordinates (X, Y), as well as by a set of keywords. In our case, 
we have a relatively large amount of spatio-textual data wich refer to restaurants ( files/restaurants.txt ). The following table consists of data like the ones we use:

ID    |Restaurant_name                    | Latitude (X)          | Longitude (Y)      | Keywords|
-----------|--------------------------|----------------|----------------------------|------------|
 1456|Carl's Jr  | 36.807414  |  -119.884527 |  American, Burgers, Fast Food |       
 1457|Picnic Garden | 40.765149 | -73.818978 |   Barbecue, Japanese, Korean |    
 1458|Star of India | 42.460714   | -83.136283 | Indian, Pakistan |

 An other type of Data that we use is the log file, wich consists of hypothetical queries that our users use to get a response from the. The log file data consists of:
- The possition of the user (Latitude(X) and Longitude(Y)).
- An amount of keywords that represent the type of food that the user wants to eat.
- An amount of restaurants that the user wants to recieve as a response (k).
- A radius which represents the max distance between the user and the restaurant(s). 

In the current implementation, we have 2 log files one small (files/smallLog.txt) and one bigger(files/largeLog.txt). The data of the log files were created randomly by the src/LogClass.java.

# Creation of the Graph

The creation of the graph the main contribution of the current thesis. The graph is created a specific way that takes advandage of the spatio-textual data. An example could be the best way to explain it.

- So, let's assume that we have the following data that represent 5 rows of the log file:

 ID    |k | Latitude (X)          | Longitude (Y)      | Keywords|
 -----------|--------------------------|----------------|----------------------------|------------|
 1| 3 | 33.672452 |  -118.004725 |  American |       
 2| 3 | 33.672452 | -118.004725 |  Deli |    
 3| 2 | 32.870138   | -120.084675| Pizza |
 4| 3 | 32.870138 | -120.084675 |   American, Burgers |    
 5| 1 | 30.862141   | -117.501512 | Smoothies, Seafood, Juices, Soup |


- Next, we take each query and search for restaurants that match . 
- When (and if) we find the restaurants that match, we create a graph based on the distance from the user.
- Of course, every node represents a restaurant, and every edge has tags (keywords that match)

The following picture, shows the graph that results from the table above:

![files/graph_example.PNG](/files/graph_example.PNG)

The implementation of the creation of the graph, is in the src/Graph.java class.

# Ranking the graph

Of course now that we created the graph, we are ready to run some ranking algorithms. In this case we decided to use the Page-Rank and also the weighted Page-Rank algorithm. It is obvious that since we have created the graph we can use every ranking algorithm that applies to graphs.

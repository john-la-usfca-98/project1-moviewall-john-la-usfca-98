# project1-moviewall-john-la-usfca-98
This is a project in Java. It simply displays a movie wall with a list of movies and roles. It also has a search function that allows you to search for a movie or actor, and print out the list of movies and roles that the actor has played in. I used a 2d array to extract the data, and used an ArrayList to make a movie list.
The algorithm that I used to predict the actor name from the wrong input, is to use an algorithm called Levenstein distance algorithm.

Levenstein distance algorithm is calculated by the number of characters that are different between the two strings. The lower the number, the more similar the two strings are. I used this algorithm to compare the input with the list of actors, and return the actor with the lowest Levenstein distance. The runtime of this algorithm is O(n * m), with n being the length of the input, and m being the length of the actor name. The space complexity is O(1), since I only used a few variables to store the data.

Notes: There are still a couple of errors such as weird context between the movie and the actor. Also, I did not use any libraries, since it results in a lot of bugs, so I just used the basic Java libraries. This can be the cause for weird context. I will try to implement libraries to fix the bugs in the future.
 

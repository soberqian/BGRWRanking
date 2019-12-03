# BGRWRanking introduction

I implement bipartite graph model with a random walk algorithm (BGRW) via Java.

Based on this algorithm, we can compute the relevance scores of all the nodes in a bipartite graph to a query node. 

We can use this scores for entity ranking and item recommendation.

# Usage
I put the input file in the "recommend/". The input of this algorithm contains two file: "train.txt" and "test.txt". The data structure of "train.txt" and "test.txt" is shown as follows:

```java
1 1 2
1 2 4
1 3 3.5
1 5 4
1 6 3.5
1 7 3.5
1 9 2.5
1 10 4
...
...

Running the "RecommendGra.java", you can obtain the result after some iterations.




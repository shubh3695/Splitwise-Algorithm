# Splitwise-Algorithm
Simple Java Application Project to minimize redundant cash transactions.
Simply Download the project. Build dependencies and run SplitwiseAlgorithm main class.

## Overview
1. This project involves a JSON Reader which reads and parses input into a directed graph component. 
2. Each Edge between userA, userB denotes money userA paid for userB.
3. The directed graph can handle multiple transactions between 2 users.

## Code
1. `SplitwiseAlgorithm` : Run this file to get result. Involves main algorithm + Binding with helper classes.
2. `Constants` : General Constant Variables
3. `CandidateInfo` : User Info mapping with Name + Id. Each Name is mapped to an unique ID.
4. `Node` : Node object denotes each Graph Node.

## Algorithm
1. Convert the JSon into a Adjacency List of users.
2. Identify each user in a group in a debt or receiver of cash from people in a direct connected component with this friends.
3. Distribute People in two separate groups i.e. Receivers + Senders
4. Do a map traversal of these groups in a linear manner to identify
  1. If a receiver(money > 0) can send more money to any user to whom he owes.
  2. Repeat step 1 until he has a currency count > 0
  3. Also add this new dependency into the graph to mark this new connection
5. At the end a new graph with newly connected nodes will be present.
6. This directed graph gives clear picture of how much is owed by whom.

## Project Dependencies
1. org.json https://mvnrepository.com/artifact/org.json/json [JAR ADDED]

package FriendRecommender;
/** FriendRecommender.java
 *
 * @author Joe Stewart
 * @version 4/7/2017
 *
 *This application implements a set of algorithms that recommends mutual friends based on different types of rankings. The purpose of this application
 * is to understand how to navigate through graphs, and make connections (edges) between vertices.
 *
 */

// FriendRecommender.java
// - Joe Stewart

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class FriendRecommender {
	private static final boolean OUTPUT_LIMITER = true; // Default: True  -- Determines if entries output to console should be limited
	private static final int LIMITER_THRESHOLD = 10;	// Default: 10	-- if Limiter is True, limit output to this many entries.
	private static final boolean RUN_FILE_LOCAL = false; // Default: False -- Runs the file locally if set to True
	private static String fileDIR = ""; // the file Directory where readGraph() inputs data from
	private static boolean onPC;	// Determines if application is running on home PC, or lab environment
	private static Graph friendGraph = new Graph(false,false);	// Graph that organizes friendships bases on social network data.
	private static HashSet<String> hSet = new HashSet<String>(); // hashSet that hold all the names of People input by file.


	public static void main(String[] args) {


	}

	public static void onPC() { // Determines if application is running on Home-Personal PC or other environment.


		/*
		 * Only used in main method, determines where to pull source file
		 */

		// If running on Home PC

		if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")			// ---For Debugging USE ONLY---
				&& (System.getProperty("user.name").toUpperCase().contains("MENOX")
						|| (System.getProperty("user.name").toUpperCase().contains("JOE")))) {
			onPC = true;
			if (!RUN_FILE_LOCAL) fileDIR = "U:/Classes/Cs340/Friend Recommender/";

		}
		//Otherwise set default location to Lab Directory
		else {

			onPC = false;
			if (!RUN_FILE_LOCAL) fileDIR = "/home/student/Classes/Cs340/Friend Recommender/";
		}



	}

	/**
	 * This method reads the file, and parses data into the graph data structure
	 * @param fileName the file that hosts the graph data for friends
	 */
	public static void readGraph(String fileName) {
		onPC(); // determines if application is running on PC.
		int index = 0; // index counter

		try {
			Scanner sc0 = new Scanner(new File(fileDIR + fileName));

			do { // While file input exists
				Scanner sc1 = new Scanner(sc0.nextLine());
				sc1.useDelimiter("\\s"); // use any whitespace as delimiter.

				String person1 = null;
				String person2 = null;
				String value = null;

				while(sc1.hasNext()) {

					if (person1 == null)
							person1 = sc1.next(); // Assign Name to person 1
					else if (person2 == null)
							person2 = sc1.next(); // Assign Name to person 2
					else if (value == null) {
						value = sc1.next();	  // Stores TimeStamp data as value. Not needed for assignment.

						//If all Fields are filled and not Null

						hSet.add(person1);
						hSet.add(person2);

						// Add Friendships to Graph
						if (!friendGraph.stringMap.containsKey(person1)) friendGraph.addVertex(index++, person1);
						if (!friendGraph.stringMap.containsKey(person2)) friendGraph.addVertex(index++, person2);


						if(!getConnectionsOfVtx(person1).contains(person2)) friendGraph.addEdge(person1, person2);
						if(!getConnectionsOfVtx(person2).contains(person1)) friendGraph.addEdge(person2, person1);




					}

				}
			} while(sc0.hasNextLine());
			sc0.close();




		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}


	}


	/**
	 *
	 * @param str input string of person name to find
	 * @return returns an ArrayList containing the "Friends List"/connections of a specific Vertex/"Person".
	 */
	public static ArrayList<String> getConnectionsOfVtx(String str) {
		ArrayList<String> outConnections = new ArrayList<>();

		if (str != null && friendGraph.stringMap.containsKey(str))
		for (Graph.Edge e:friendGraph.stringMap.get(str).edgeList)
			if (!e.vertices[1].name.equals(str)) outConnections.add(e.vertices[1].name);

		return outConnections;


	}
	/**
	 *
	 * @param str input string of person name
	 * @return returns all second degree friends, or friends of friends, removing duplicates.
	 */
	public static ArrayList<String> getSecondDegreeConnections(String str) {
		ArrayList<String> outConnections = new ArrayList<>();
		HashSet<String> tmpNameSet = new HashSet<>();
		if(str != null && friendGraph.stringMap.containsKey(str)) {
		for (Graph.Edge e:friendGraph.stringMap.get(str).edgeList) {
			tmpNameSet.addAll(getConnectionsOfVtx(e.vertices[1].name));
		}
		for (String s:tmpNameSet) outConnections.add(s);

		}
		return outConnections;
	}

	/**
	 *
	 * @param user name of whom to recommend friends for
	 * @return returns a list of friends of friends ranked by order of number mutual friends
	 */
	public static ArrayList<Person> recommendFriendsOfFriends(String user) {
		ArrayList<String> myFriends = new ArrayList<String>();
		HashMap<String,Integer> mtlFrndCntr = new HashMap<>();
		ArrayList<String> commonFrnd = new ArrayList<>();
		ArrayList<Person> friendsOfFriends = new ArrayList<>();
		PriorityQueue<Person> prQue = new PriorityQueue<>();
		if (user != null && friendGraph.stringMap.containsKey(user)) {
			myFriends = getConnectionsOfVtx(user); //sets friendsList of user
			for (String frd:myFriends) {
				commonFrnd.addAll(getConnectionsOfVtx(frd)); //finds common friends of Friends
			}

			for (String frd:commonFrnd) {
				if(frd != null && frd != user) {
				if (!mtlFrndCntr.containsKey(frd)) mtlFrndCntr.put(frd,1); //creates friend in map if !exists
				else mtlFrndCntr.put(frd,mtlFrndCntr.get(frd) + 1); // else increments friends weight in map

			}
			}

			for(String s:mtlFrndCntr.keySet()) { // Creates a person instance of each friend
				if (!myFriends.contains(s) && !s.equals(user)) {
				Person person = new Person(s,mtlFrndCntr.get(s));
				prQue.add(person);	// adds person to priority queue ranked by mutual friend weight
				}
			}

			while(!prQue.isEmpty()) {	// adds all friends in queue into a new ArrayList in order of mutual friends.
				friendsOfFriends.add(prQue.poll());
			}
		}

		if (OUTPUT_LIMITER) {	// removes friends from list if output limiter threshold is enabled. Default: 10
			for(int i=friendsOfFriends.size()-1;i>=LIMITER_THRESHOLD;i--) {
				friendsOfFriends.remove(i);
			}
		}


		return friendsOfFriends;


	}
	/**
	 *
	 * @param user name of whom to recommend friends for
	 * @return returns a list of friends of friends ranked by order of weighted-influence
	 */
	public static ArrayList<Person> recommendByInfluence (String user) {

		ArrayList<String> myFriends = new ArrayList<String>();
		HashMap<String,Number> mtlFrndTtlCnt = new HashMap<>();
		ArrayList<String> commonFrnd = new ArrayList<>();
		ArrayList<Person> friendsOfFriends = new ArrayList<>();
		ArrayList<Person> friendsByInf = new ArrayList<>();
		PriorityQueue<Person> prQue = new PriorityQueue<>();
		if (user != null && friendGraph.stringMap.containsKey(user)) {
			friendsOfFriends = recommendFriendsOfFriends(user); // finds all mutual friends ranked in order
			myFriends = getConnectionsOfVtx(user); //finds all friends of user
				for (Person p:friendsOfFriends) {
					commonFrnd = getConnectionsOfVtx(p.name);
					for(String frd:commonFrnd) {
						if (myFriends.contains(frd)) {
							if(!mtlFrndTtlCnt.containsKey(p.name)) {
								mtlFrndTtlCnt.put(p.name, (1.0)/getConnectionsOfVtx(frd).size()); //creates initial weight of each mutual friends influence
							}
							else {
								Number tmp = mtlFrndTtlCnt.get(p.name);
								mtlFrndTtlCnt.put(p.name,((double)tmp + (1.0)/getConnectionsOfVtx(frd).size())); //increments total weight by adding all mutual friends influence weight
							}

						}

					}

				}




			for(String s:mtlFrndTtlCnt.keySet()) { //adds all users into Priority Queue based on weighted influence rank.
				if (!myFriends.contains(s) && !s.equals(user)) {
				Person person = new Person(s,(double) mtlFrndTtlCnt.get(s));
				prQue.add(person);
				}
			}

			while(!prQue.isEmpty()) {	// adds all users in order into ArrayList
				friendsByInf.add(prQue.poll());
			}

			if (OUTPUT_LIMITER) {	// removes friends from ArrayList if Limiter is enable. For Instructor use
				for(int i=friendsByInf.size()-1;i>=LIMITER_THRESHOLD;i--) {
					friendsByInf.remove(i);
				}
			}
			}
		return friendsByInf;
	}

	/**
	 * Simple class to represent people in the FriendRecommender program
	 * Modified by Joe Stewart to allow for no weight to be entered.
	 * @author Stuart Hansen
	 * @version March 27, 2017
	 */
	private static class Person implements Comparable<Person> {
	    private String name;  // the name of the person
	    private double weight;  // the weight value used to test potential friendship for the person

	    /**
	     * The constructor
	     * @param name
	     * @param weight
	     */
	    public Person (String name, double weight) {
	        this.name = name;
	        this.weight = weight;
	    }

	    public Person(String name) {

	    	this.name = name;
	    	this.weight = 1;
		}

		/**
	     * get the person's name
	     * @return
	     */
	    public String getName() {
	        return name;
	    }

	    /**
	     * Get the weight of this vertex
	     * @return
	     */
	    public double getWeight() {
	        return weight;
	    }

	    /**
	     * Set the weight of this vertex
	     * @param weight
	     */
	    public void setWeight(double weight) {
	        this.weight = weight;
	    }

	    /**
	     * compareTo allows us to find which potential vertex is more promising as a friend
	     * @param o
	     * @return
	     */
	    public int compareTo(Person o) {
	        double c = o.weight - weight;
	        if (c == 0)
	            return name.compareTo(o.name);
	        else if (c > 0)
	            return 1;
	        else
	            return -1;
	    }

	    /**
	     * toString returns a string representation of the
	     * potential name and the number of common names.
	     * @return
	     */
	    public String toString() {
	        return "[" + name + ": " + String.format("%5.2f", weight) + "]";
	    }

	    public boolean equals(Person P) {
	    	if ((this.name == P.name) && (this.weight == P.weight))
	    		return true;
	    	else return false;
	    }
	}

	public static class Graph {

	    private boolean directed;  // whether the graph is directed or not
	    private boolean weighted;  // whether the graph is weighted or not

	    // We maintain two maps so we can quickly look up vertices by number or
	    // by name
	    public HashMap<Integer, Vertex> intMap;
	    public HashMap<String, Vertex> stringMap;

	    /**
	     * A simple constructor
	     *
	     * @param directed whether the graph is directed or not
	     * @param weighted whether the graph is weighted or not
	     */
	    public Graph(boolean directed, boolean weighted) {
	        this.directed = directed;
	        this.weighted = weighted;
	        intMap = new HashMap<>();
	        stringMap = new HashMap<>();
	    }

	    // Add a vertex to the graph
	    public void addVertex(int index, String label) {
	        Vertex v = new Vertex(index, label);
	        intMap.put(index, v);
	        stringMap.put(label, v);
	    }

	    // Add an edge to the graph
	    public void addEdge(String s1, String s2) {
	        addEdge(s1, s2, 1);
	    }

	    // Add a weighted edge to the graph
	    public void addEdge(String s1, String s2, int weight) {
	        Vertex v = stringMap.get(s1);
	        Vertex w = stringMap.get(s2);
	        v.edgeList.add(new Edge(v, w, weight));
	        if (!directed) {
	            w.edgeList.add(new Edge(w, v, weight));
	        }
	    }

	    /**
	     * A vertex has an index a name, and an edgeList.
	     */
	    public class Vertex {

	        public int index;  // the index
	        public String name;  // the name
	        public ArrayList<Edge> edgeList;  // the edges for this vertice

	        public int pathWeight;   // used only with Dijkstras

	        /**
	         * A simple constructor
	         *
	         * @param index is the vertex's index number
	         * @param name is the name associated with the vertex
	         */
	        public Vertex(int index, String name) {
	            this.index = index;
	            this.name = name;
	            edgeList = new ArrayList<>();
	            pathWeight = 0;
	        }

	        public ArrayList<String> getEdgeList() {
	        	ArrayList<String> out = new ArrayList<>();
	        	for(Edge e:edgeList) {
	        		out.add(e.vertices[0].name);
	        	}
	        	return out;
	        }

	        public String toString() {
	            return "[V" + index +":"+ name+"]";
	        }
	    }

	    /**
	     * Our edge data structure
	     */
	    public class Edge {

	        // Each edge has two vertices
	        public Vertex[] vertices;
	        // Weighted graphs also have edge weights
	        public int weight;

	        /**
	         * A simple edge constructor
	         *
	         * @param v the outgoing vertex
	         * @param w the incoming vertex
	         */
	        public Edge(Vertex v, Vertex w) {
	            this (v, w, 1);
	        }

	        /**
	         * A second constructor with an edge weight
	         *
	         * @param v the outgoing vertex
	         * @param w the incoming vertex
	         * @param weight the edge's weight
	         */
	        public Edge(Vertex v, Vertex w, int weight) {
	            vertices = new Vertex[2];
	            vertices[0] = v;
	            vertices[1] = w;
	            this.weight = weight;
	        }

	        /**
	         * A toString method to make printing edges easier If the graph is
	         * weighted, the edge weight is also printed
	         *
	         * @return the edge represented as a String
	         */
	        @Override
	        public String toString() {
	            return vertices[0].name + "-" + vertices[1].name + (weighted ? " " + weight : "");
	        }
	    }

	}
}



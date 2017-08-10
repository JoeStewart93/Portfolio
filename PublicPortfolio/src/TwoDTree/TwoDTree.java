package TwoDTree;
/**
 * @title TwoDTree.java
 * @author Joe Stewart
 * @version 1.0
 * @date 3/23/2017
 * 
 * TwoDTree.java is a data structure that that builds a binary tree structure based on two-dimensional "Point" Coordinates.
 * This application organizes/sorts the binary tree by alternating which axis-coordinate value is compared. On the first level
 * of the tree, Each child nodes Y-values are compared. On the second level of the tree, X-Values are compared. Smaller values 
 * are placed as the left child node, while larger values are placed as the right child node. This creates an intuitive structure
 * to easily sort through point values.
 * 
 * --------------------------------------------------------------------------------------------------------------------------------
 * 
 * 
 */

// - TwoDTree.java

import java.util.ArrayList;
import java.awt.Point;

 
public class TwoDTree {

	private static TwoDTreeNode root;				// Creates a static TwoDTreeNode
	ArrayList<Point> lstOfPnts = new ArrayList<>(); // ArrayList of Points to hold points. Used for Debugging Only

	public static void main(String[] args) {

		// 404 Error, Main Method not found

	}
	

	/**
	 * Creates Empty TwoDTree
	 */
	
	public TwoDTree() {
		root = null;
	}

	/**
	 * 
	 * @param p takes in ArrayList of Points, to insert into Tree Structure
	 */
	public TwoDTree(ArrayList<Point> p) {


		for(int i=0;i<p.size();i++) {
			root.insert(p.get(i)); //Inserts all Points into 
			lstOfPnts.add(p.get(i));
		}

	}

	
	/**
	 * 
	 * @param p takes in Point, and inserts individual Point into Tree Structure
	 */
	
	public void insert (Point p) {

			if (root != null) { // if root isn't null
			root.insert(p);		// Insert Point into Tree
			lstOfPnts.add(p);	// Add Point into List of Points for Debugging use ONLY.
		}
			
		else {
			root = new TwoDTreeNodeY(p); // Else Create new TreeNodeY As primary TreeNode();
			lstOfPnts.add(p);			// Add Point into List of Points #For Debugging use ONLY.
			
			
			
		}
		}
	

	/**
	 * 
	 * @param p1 LowerBound Point
	 * @param p2 UpperBound Point
	 * @return Outputs ArrayList of all points between LowerBound and UpperBound points inclusively.
	 */
	public ArrayList<Point> searchRange(Point p1,Point p2) {
		ArrayList<Point> range = new ArrayList<>();
		int min_x_val = 0,max_x_val = 0,min_y_val = 0,max_y_val = 0; //Creates min & max values to create point bounds

		/*
		 * In order to use SearchRange effectively, We create two Pseudo-Points that represent lower bounds, 
		 * and upper bounds to search through. This 
		 */
		
		// Sets the x value lower bound and upper bound values
		if (p1.x <= p2.x) {  
			min_x_val = p1.x;
			max_x_val = p2.x;
		}
		else if(p1.x > p2.x) {
			min_x_val = p2.x;
			max_x_val = p1.x;
		}
		
		// Sets the y value lower bound and upper bound values
		if (p1.y <= p2.y) {
			min_y_val = p1.y;
			max_y_val = p2.y;
		}
		else if(p1.y > p2.y) {
			min_y_val = p2.y;
			max_y_val = p1.y;
		}
		
		//Creates new Point objects using rearranged Min and Max values to aid in Search Range. 
		
		Point LowBound = new Point(min_x_val,min_y_val); 
		Point UpBound = new Point(max_x_val,max_y_val);
	
		range.addAll(root.searchRange(LowBound,UpBound)); //recursively calls searchRange from root, through all child nodes
		return range;
		
		}



	/**
	 * 
	 * @param p Point to check if exists
	 * @return returns TRUE if Point exists in Tree, or FALSE if tree does not exist in Tree.
	 */
	public boolean search(Point p) {

		return root.search(p); // returns boolean from recursive search method.

	}





/**
 * 
 * Interface to Ensure TreeNodeX and TreeNodeY use approved methods.
 *
 */
	private interface TwoDTreeNode {


			// Mandatory methods required for TreeNodes.
		
			boolean search(Point point); // Searches Tree Structure
			void insert(Point point);	// Inserts Points into Tree Structure
			ArrayList<Point> searchRange(Point p1, Point p2);	// Searches Tree Structure for Points within Range defined.
			TwoDTreeNode getLeftChild(); 	// Method to retrieve Left Child Node
			TwoDTreeNode getRightChild();	// Method to retrieve Right Child Node
			Point getPoint();	//Method to retrieve Point from Node.



	}
	
	/**
	 * 
	 * TwoDTreeNodeX is a TreeNode that compares only X-Values, but contains two TreeNodeY Children
	 *
	 */

	private class TwoDTreeNodeX implements TwoDTreeNode{

		private TwoDTreeNodeY L_Child;	//Left Tree Node
		private TwoDTreeNodeY R_Child;	//Right Tree Node
		private Point pt;				//Point of TreeNode

		public TwoDTreeNodeX(Point p) { // Takes a point, and assigns it to a Node.
			this.pt = p;
		}
		
		
		public boolean search(Point point) { // Searches child nodes of Node for Point
			boolean result = false;
			int x = point.x;
			
			//If Point is current point, set result to true
			if (pt.equals(point))
				result = true;

			//Otherwise check Right Child for Point
			else if ( x > pt.x && point != null) {

				if (R_Child != null) {
				if (R_Child.pt.equals(point)) {

					result = true;
				}
				else if (!R_Child.pt.equals(point)){
					result = R_Child.search(point); // Recursively call On Right Child
				}
				}
			}
			
			// Otherwise Check Left Child for Point
			else if (x <= pt.x && point != null) {
				if (L_Child != null) {
				if (L_Child.pt.equals(point)) {

					result = true;

				}

				else if (!L_Child.pt.equals(point)){
					result = L_Child.search(point); // Recursively Call on Left Child
				}
				}
			}



			return result;

		}

		public ArrayList<Point> searchRange(Point p1, Point p2) {
			ArrayList<Point> results = new ArrayList<Point>();
			
			
			// If Point is in Range of P1 and P2
			if ((pt.x >= p1.x && pt.y >= p1.y)
					&& (pt.x <= p2.x && pt.y <= p2.y) && pt != null)
					{
				// Add to result list
				results.add(this.pt);
			
				//If children isn't null recursively search Children Nodes
				
				if (L_Child != null)
				results.addAll(L_Child.searchRange(p1,p2));
				if (R_Child != null)
				results.addAll(R_Child.searchRange(p1,p2));
			}
			
				//If children isn't null recursively search Children Nodes
			
			else {
				if (L_Child != null)
					results.addAll(L_Child.searchRange(p1,p2));
				if (R_Child != null)
					results.addAll(R_Child.searchRange(p1,p2));
			}
		
			return results;

		}

		public void insert(Point point) {

			//If Point inserted > Point at NODE, insert to right child.
			if (point.x > pt.x) {


				if(R_Child != null) {
					R_Child.insert(point);
				}
				else {
					R_Child = new TwoDTreeNodeY(point);
				}
			}
			
			//Otherwise Insert to Left Child Node
			else if (point.x <= pt.x) {

				if (L_Child != null) {
					L_Child.insert(point);
				}
				else {
					L_Child = new TwoDTreeNodeY(point);
				}

			}



		}

		public TwoDTreeNode getLeftChild() {
			return L_Child;
		}

		public TwoDTreeNode getRightChild() {
			return R_Child;
		}

		public Point getPoint() {
			return pt;
		}
	}

	
	/**
	 * 
	 * TwoDTreeNodeY is a TreeNode that compares only X-Values, but contains two TreeNodeX Children
	 *
	 */
	private class TwoDTreeNodeY implements TwoDTreeNode {

		private TwoDTreeNodeX L_Child;	// Left TreeNode
		private TwoDTreeNodeX R_Child;	// Right TreeNode
		private Point pt;				// Point of TreeNode

		public TwoDTreeNodeY(Point p) {
			this.pt = p;				// Constructor to set point of TreeNode
		}


		// Search function for Y values
		public boolean search(Point point) {

			
			boolean result = false;

			int y = point.y;

			//If Point is current point, set result to true
			if (pt.equals(point))
			result = true;

			//Otherwise check Right Child for Point
			else if ( y > pt.y && point != null) {
				if (R_Child != null) {
				if (R_Child.pt.equals(point)) {
					result = true;
				}
				else if (!R_Child.pt.equals(point)){
					result = R_Child.search(point);
				}
				}
			}
			
			//Otherwise check Left Child for Point
			else if (y <= pt.y && point != null) {
				if (L_Child != null) {
				if (L_Child.pt.equals(point)) {

					result = true;

				}

				else if (!L_Child.equals(point)){
					result = L_Child.search(point);
				}
				}

			}


			return result;


		}

		public ArrayList<Point> searchRange(Point p1, Point p2) {
			ArrayList<Point> results = new ArrayList<Point>();
			
			// If Point is in Range of P1 and P2
			if ((pt.x >= p1.x && pt.y >= p1.y)
					&& (pt.x <= p2.x && pt.y <= p2.y) && pt != null)
					{

				// Add to result list
				results.add(this.pt);
				
				//If children isn't null recursively search Children Nodes
				
				if (L_Child != null)
				results.addAll(L_Child.searchRange(p1,p2));
				if (R_Child != null)
				results.addAll(R_Child.searchRange(p1,p2));
			}

			else {
				if (L_Child != null)
					results.addAll(L_Child.searchRange(p1,p2));
				if (R_Child != null)
					results.addAll(R_Child.searchRange(p1,p2));
			}
		
			return results;

		}

		public void insert(Point point) {

			//If Point inserted > Point at NODE, insert to Right child.
			if (point.y > pt.y) {


				if(R_Child != null) {
					R_Child.insert(point);
				}
				else {
					R_Child = new TwoDTreeNodeX(point);
				}
			}

			//If Point inserted <= Point at NODE, insert to Left child.
			else if (point.y <= pt.y) {

				if (L_Child != null) {
					L_Child.insert(point);
				}
				else {
					L_Child = new TwoDTreeNodeX(point);
				}

			}



		}

		public TwoDTreeNode getLeftChild() {
			return L_Child;
		}

		public TwoDTreeNode getRightChild() {
			return R_Child;
		}

		public Point getPoint() {
			return pt;
		}
		
		}
	
	

	}


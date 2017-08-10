package PageRank;
/*
 *----------------------
 *
 *    PageRank.java
 * Author: Joe Stewart
 *
 * ---------------------
 *
 */

/**
 * @author JoeStewart
 * @version 1.0
 */


import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;

public class PageRank {
public static void main (String[] args) {

	// Final Variables
	final double threshold = 0.0000005;			// Threshold to meet PageRank accuracy of (10^-6)
	final boolean numIterPrntSwitch = false;	// When True, Prints number of iterations of PageRank algorithm to meet threshold
	final boolean outputDebug = false;			// When True, Prints additional information for Every Page. DEFAULT = false;



	int numIter = 0;		// Tracks number of iterations of PageRank algorithm to meet threshold
	int numOfPages = 0; 	// Tracks number of pages in text document
	Scanner sc0 = null;  	// Instantiates Scanner Object to NULL;
	File fl0 = null; 		// Instantiates File Object to NULL;

	HashMap<String,Page> pageLog = new HashMap<String,Page>();	// HashMap to store the URL as KEY, and Page Object.
	ArrayList<String> URLlist = new ArrayList<String>(); 		// ArrayList to keep a list of all Primary URL's in source file.
	ArrayList<String> outLinksTotal = new ArrayList<String>();	// ArrayList to track all out-bound links (as one arrayList)



	// -----------------------Start of File Input---------------------------

	try {
		if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")			// ---For Debugging USE ONLY---
				&& (System.getProperty("user.name").toUpperCase().contains("MENOX") ||
						System.getProperty("user.name").toUpperCase().contains("JOE"))) {	// Checks OS and Username of System to
																						// determine where to read source file.
				fl0 = new File("CSnew2.txt");	// READS LOCALLY
		}

		else{														  			// IF running on LAB
				fl0 = new File("/home/student/Classes/Cs340/PageRank/CS.txt"); // READ source file from Course DIR
		}
	sc0 = new Scanner(fl0);  										// Opens Source file into Scanner

	}

	catch(IOException ioe)  {									// Catches any Input/Output Errors
	System.err.println("Error reading file!");
	ioe.printStackTrace();
	}


	String url = null;											//Stores URL for file input
	String prevLine = null;										//Stores previous line of input for file input
	String pgtext = null;										//Stores page text of each URL
	int outRef = 0;

	// ----- Start of File Input -----

	while(sc0.hasNextLine()) {									//Ensures the file has data to process

		String input = sc0.nextLine();							// input from scanner

		if (input.toUpperCase().contentEquals("PAGE") && prevLine == null) { // IF First line of File

			numOfPages++;	// increments numOfPages
			prevLine = input; // Sets previous Line as current line for next loop iteration

		}

		else if (!input.toUpperCase().contentEquals("PAGE") 				//IF Input = Primary URL
				&& input.toUpperCase().startsWith("HTTP")
				&& prevLine.toUpperCase().contentEquals("PAGE")) {

			url = input;													// Set URL
			URLlist.add(url);												// Add to URL List
			prevLine = input;

		}

		else if (!input.toUpperCase().contentEquals("PAGE") 				//IF Input = Page Text
				&& prevLine.toUpperCase().startsWith("HTTP")
				&& !input.toUpperCase().startsWith("HTTP")) {

			pgtext = input;
			prevLine = input;
		}

		else if (input.toUpperCase().startsWith("HTTP") 					//If Input = Outgoing Links/URLs
				&& !prevLine.toUpperCase().contentEquals("PAGE")) {

			outLinksTotal.add(input);	//adds out-bound link to outLinksTotal ArrayList.
			outRef++;
			prevLine = input;
		}

		else if (input.toUpperCase().contentEquals("PAGE") 					//If Input = NEW Page
				&& prevLine.toUpperCase().startsWith("HTTP")) {

			numOfPages++;
			pageLog.put(url, new Page (url,0,outRef,pgtext,0.0));					//Files previous Page to PageLog
			outRef = 0;

			prevLine = input;

			}

	}

	pageLog.put(url, new Page (url,0,outRef,pgtext,0.0));							//Files last Web Data to PageLog
	outRef = 0;



	//------------- END OF FILE INPUT/PARSE-------------------







	for (int i=0;i<URLlist.size();i++) {								// Determines the number of incoming Links
		for (int j=0;j<outLinksTotal.size();j++) {						// to a given URL

			if (URLlist.get(i).contentEquals(outLinksTotal.get(j))) {

				Page tempPage = pageLog.get(URLlist.get(i));
				tempPage.setInRef(tempPage.getInRef() + 1);
			}
		}
	}



	for (int i=0;i<URLlist.size();i++) {

		Page tempPage = pageLog.get(URLlist.get(i));
		tempPage.setPR((1.0/numOfPages));								// Sets Default PageRank for all Pages
	}



	/*
	 * File is Rescanned below to determine outgoing Links
	 * and store them in an ArrayList as part of the Page Object.
	 * This is done separate from the first scan because the Page Object
	 * isn't created until the end of the previous scan.
	 */


	// -------------- Second File Scan --------------------
	try {

		Scanner sc1 = new Scanner(fl0);								// New Scanner to Re-Scan File
		String url2 = null;
		String input2 = null;
		String prevLine2 = null;
		while(sc1.hasNextLine()) {
			input2 = sc1.nextLine();

			if (input2.toUpperCase().contentEquals("PAGE") && prevLine2 == null) { // IF First line of File

				prevLine2 = input2;

			}

			else if (!input2.toUpperCase().contentEquals("PAGE") 				//IF Input = Primary URL
					&& input2.toUpperCase().startsWith("HTTP")
					&& prevLine2.toUpperCase().contentEquals("PAGE")) {

				url2 = input2;													// Set URL											// Add to URL List
				prevLine2 = input2;

			}

			else if (!input2.toUpperCase().contentEquals("PAGE") 				//IF Input = Page Text
					&& prevLine2.toUpperCase().startsWith("HTTP")
					&& !input2.toUpperCase().startsWith("HTTP")) {
				prevLine2 = input2;
			}

			else if (input2.toUpperCase().startsWith("HTTP") 					//If Input = Outgoing Links/URLs
					&& !prevLine2.toUpperCase().contentEquals("PAGE")) {

				Page tempPage = pageLog.get(url2);
				if (tempPage != null) {
				tempPage.addOutLink(input2);

				}
				prevLine2 = input2;
			}


			else {
				prevLine2 = input2;
			}

		}

	} catch (IOException ioe) {
		System.err.println("Error reading file");
		ioe.printStackTrace();
	}

	// ------------ END OF SECOND FILE SCAN -------------




	/*
	 * -------------------- In-bound Links -----------------------
	 *
	 * Algorithm below finds all In-bound Pages that reference a given Page.
	 *
	 */

	for (int i=0;i<URLlist.size();i++){											// Finds all In-bound Links for each Page
		Page tempPage = pageLog.get(URLlist.get(i));							// based on previously found Out-bound Links.
		for (int j=0;j<URLlist.size();j++) {
			Page tempPage2 = pageLog.get(URLlist.get(j));

			if (i!=j) {															// Ensures Pages aren't compared to themselves.
			for(int k=0;k < tempPage2.getOutLinks().size();k++) {
			if (tempPage2.outLinks.get(k).equalsIgnoreCase(tempPage.getPageURL())) {
				tempPage.addInLink(tempPage2.getPageURL());
				}
			}
			}

		}
	}



	/*
	 * --------------- Parsing Page Text from String -------------------
	 *
	 * Algorithm below takes the String of each Page Text and first parses it into
	 * a string array, then adds to a HashSet to remove duplicates.
	 *
	 */


	for(int i=0;i<URLlist.size();i++) {											// Parses Large String of Raw text into HashSet
		Page tempPage = pageLog.get(URLlist.get(i));							// of individual words For each Page.
		String txtStr[] = tempPage.getPtext().split(" ");
		for(int j=0;j<txtStr.length;j++) {
			tempPage.addText(txtStr[j]);
		}
	}



	//---------------------- Computation of PageRank Below -----------------------

	boolean thresholdSwitch = false;		// Switch to track when PageRage accuracy threshold met


	while (!thresholdSwitch) {
		numIter++;
	for (int i=0;i<URLlist.size();i++) {

		double tempPR = 0;
		Page tempPage = pageLog.get(URLlist.get(i));
		double constant = ((1.0-0.15)/numOfPages);			// constant of PageRank formula
		double calcPR = 0;

		for (int k=0; k<tempPage.getInLinks().size();k++) {
			tempPR = tempPage.getPR();
			Page tempPage2 = pageLog.get(tempPage.getInLinks().get(k));
			calcPR += (tempPage2.getPR())/(tempPage2.getOutLinks().size());
		}
		calcPR *= (0.15);
		calcPR += constant;
		tempPage.setPR(calcPR);

		if ((tempPage.getPR() <= (tempPR + threshold))
				&& (tempPage.getPR() >= (tempPR - threshold))) {
			thresholdSwitch = true;
		}


	}
	}

	//--------- Start of Interactive, Search Prompt ----------
	System.out.println("");
	System.out.println("Enter your search terms: ");
	System.out.println("");
	ArrayList<Page> searchList = new ArrayList<Page>();

	Scanner sc2 = new Scanner(System.in);
	String searchInput = sc2.nextLine();



	// ------------ Searches all pages that contain Search Contents -----------
	String[] inpStr1 = searchInput.split(" "); // Separates search terms by " "
	for (int i=0;i<URLlist.size();i++) {
		Page tempPage = pageLog.get(URLlist.get(i));

		for(int j=0;j<inpStr1.length;j++) {
			if(tempPage.getText().contains(inpStr1[j])) {
			searchList.add(tempPage);
			}
		}
	}




	// ------------- Converts Search ArrayList Results into Array of Pages -----------

	Page tmPage;
	Page slPage[] = new Page[searchList.size()];
	for(int i=0;i<searchList.size();i++) {
		slPage[i] = searchList.get(i);

	}




	// ------------ BubbleSort: to sort Result Pages in order of descending PageRank ------------

	boolean flag = true;
	while(flag) {
		flag = false;
	for (int i=0;i<slPage.length - 1;i++) {
		if (slPage[i].getPR() < slPage[i+1].getPR()) {
			tmPage = slPage[i];
			slPage[i] = slPage[i+1];
			slPage[i+1] = tmPage;
			flag = true;
	}
	}
	}

	// --------------- Removes duplicate results from resulting array of Pages ------------------

	int lim = slPage.length;
	for(int i=0; i<lim;i++){
		for(int j=i+1; j < lim;j++) {
			if(slPage[i] == slPage[j]) {
				int shiftLft = j;
				for (int k=j+1; k < lim; k++, shiftLft++) {
					slPage[shiftLft] = slPage[k];
				}
				lim--;
				j--;
			}
		}
	}

	// creates new array with removed duplicates
	Page slPageFinal[] = new Page[lim];
		for(int i=0;i<slPageFinal.length;i++) {
			slPageFinal[i] = slPage[i];
		}

	// -------------------------------------------------------------------------





	// ------------------- Application Output of results  ----------------------


	System.out.println("");
	System.out.println("There were " + slPageFinal.length + " hits.");

	if (numIterPrntSwitch){
		System.out.println("");
	if (numIter==1) {
		System.out.println(numIter + " iteration of PageRank until stable");
	}
	else if (numIter > 1) {
		System.out.println(numIter + " iterations of PageRank until stable");
	}
	}
	System.out.println("");
	System.out.print("Rank:");
	System.out.println("			URL:");
	System.out.println("");
	for(int i=0;i<slPageFinal.length;i++) {
	System.out.print(slPageFinal[i].getPR());
	System.out.println("	" + slPageFinal[i].getPageURL());
	}




	//---------- OUTPUT TEST: -------  FOR DEBUGGING -----------
	if (outputDebug) {
	System.out.println("----------------------------------------------------------------------------");
	System.out.println("");
	System.out.println("Number of Pages: " + numOfPages);
	System.out.println("");
	System.out.println("PAGES Listed below: ");
	System.out.println("");
	for (int i=0;i<URLlist.size();i++) {
		Page tempPage = pageLog.get(URLlist.get(i));
		System.out.println("");
		System.out.println("Page URL: " + tempPage.getPageURL());
		System.out.println("");
		System.out.print("# of Unique Words: ");
		System.out.println(tempPage.getText().size());
		System.out.println(tempPage.getPtext());
		System.out.print("Inbound Links: ");
		System.out.println(tempPage.getInRef());
		System.out.println(tempPage.getInLinks().toString());
		System.out.print("OutBound Links: ");
		System.out.println(tempPage.getOutRef());
		System.out.println(tempPage.getOutLinks().toString());
		System.out.print("PageRank: ");
		System.out.println(tempPage.getPR());
		System.out.println("");
		System.out.println("______________________________________");
	}
	}

}
}

// ------------------    END OF PAGERANK    ------------------------
// -----------------------------------------------------------------



// --- Page Object ---

class Page {

	String pageURL;			// stores the URL of each Page.
	int inReferences;		// tracks the number of other pages that reference given Page.
	String ptext;			// stores all text in page as one string
	double PR;				// PageRank of Page
	int  outReferences;		// tracks the number of pages that current page references
	ArrayList<String> outLinks = new ArrayList<String>();		//stores all URLlinks PAGE references
	ArrayList<String> inLinks = new ArrayList<String>();		//stores all URLlinks that reference PAGE
	HashSet<String> textSet = new HashSet<String>();			// stores text of each page


	public Page(String pageURL, int inReferences, int outReferences, ArrayList<String> outLinks, String ptext, double PR) {
		this.pageURL = pageURL;
		this.inReferences = inReferences;
		this.ptext = ptext;
		this.outReferences = outReferences;
		this.PR = PR;
}

	public Page(String pageURL, int inReferences, int outReferences, String ptext, double PR) {
		this.pageURL = pageURL;
		this.inReferences = inReferences;
		this.ptext = ptext;
		this.outReferences = outReferences;
		this.PR = PR;
}

	public Page() {
		this.pageURL = "";
		this.inReferences = 0;
		this.ptext = "";
		this.PR = 0.25;
		this.outReferences = 0;
	}

	public String getPageURL() {
		return this.pageURL;
	}

	public int getInRef() {
		return this.inReferences;
	}

	public String getPtext() {
		return this.ptext;
	}

	public double getPR() {
		return this.PR;
	}


	public int getOutRef() {
		return outReferences;
	}

	public ArrayList<String> getOutLinks() {
		return outLinks;
	}

	public ArrayList<String> getInLinks() {
		return inLinks;
	}

	public HashSet<String> getText() {
		return textSet;
	}

	public void addText(String str) {
		textSet.add(str);
	}
	public void addInLink(String str) {
		inLinks.add(str);
	}

	public void addOutLink(String str) {
		outLinks.add(str);
	}

	public void setPageURL(String URL){
		this.pageURL = URL;
	}
	public void setInRef(int inRef) {
		this.inReferences = inRef;
	}

	public void setOutRef(int outRef) {
		this.outReferences = outRef;
	}

	public void setPtext(String PageText) {
		this.ptext = PageText;
	}

	public void setPR(double PR) {
		this.PR = PR;
	}

}

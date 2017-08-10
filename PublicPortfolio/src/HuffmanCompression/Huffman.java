package HuffmanCompression;
/**
 *
 *
 * @title Huffman.java
 * @author Joe Stewart
 * @version 3/1/2017
 *
 *
 * This application takes a file and compresses it using a Huffman code algorithm.
 * The Huffman algorithm is a (lossless) algorithm that assigns characters a prefix-code
 * based on their frequency. Characters that occur more frequently have a shorter code than ones that occur
 * less frequently. This allows us to express these characters in fewer bits, and allows us to compress raw
 * text very easily and efficiently. This application outputs the translation of each characters code set to a code file, and then
 * a compressed version of the file. This application can then reverse the compression and return the original
 * uncompressed file.
 *
 *
 */

/*
 *  - Huffman.java -
 *
 *  - Joe Stewart -
 *
 * _________________________________________________________________________________________________________________________________
 *
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.io.RandomAccessFile;
import java.io.*;

public class Huffman {


	// ------------  HardCoded Debug Options  ------------

	final static boolean DEBUG_OUTPUT = false; // Hard coded debug option. Enable to print debug information to console.
	final static boolean RUNTIME_OUTPUT = true; // Hard coded debug option. Enable to print Runtime information to console.
	//----------------------------------------------------

	// Only Relevant in main method for assignment example:
	static boolean onPC = false;	// Toggle switch that determines if application is running on lab-machine or Personal Computer
	static String fileDIR;			// ^ Depends on onPC toggle switch to set file directory location
	//----------------------------------------------------


	static File f0;					// File for reading in primary file to determine character frequency
	static RandomAccessFile raf0;	// ^ Used in conjunction with f0 File to determine character frequency

	static int[] charArrCnt = new int[256 + 1]; // Array of int's to store the count of each characters Frequency
	static HashMap<Character,Integer> charFreq = new HashMap<Character,Integer>(); // Stores every Character and it's Integers Frequency
	static HashMap<Character,Integer> charMap = new HashMap<Character,Integer>(); // Stores every Character (USED) and it's Integer Frequency
	static ArrayList<TreeNode> TnAL = new ArrayList<>(); // Stores a List of all TreeNodes, used for Array Implementation
	private static PriorityQueue<TreeNode> TnQue = new PriorityQueue<>(); // PriortyQueue of TreeNodes to sort TreeNodes and build Tree.
	private static PriorityQueue<Value> ValQue = new PriorityQueue<>(); // PriortyQueue of Values to output Code File with sorted Values.
	private static TreeMap<Character, Value> treeMap = new TreeMap<Character, Value>(); // TreeMap of Characters and their Value. (Value holds Huffman data)
	private static TreeMap<Character,Value> codeFileTreeMap = new TreeMap<Character,Value>(); // ^^ Same as above, but uses CodeFile input as data source
	private static TreeMap<String,Character> revTreeMap = new TreeMap<String,Character>(); // TreeMap Reversal: Stores String of HuffmanCodes as Key, and their corresponding character.

//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public static void main(String[] args) { // --- Main method to test Huffman Codes based on example McGee.txt
		double main_S_time= System.currentTimeMillis();
		onPC(); // checks if reading file locally, or from lab directory
		encode(fileDIR + "mcgee.txt","mcgee.cde","mcgee.out"); // Encodes mcgee.txt, and outputs as mcgee.out && Also outputs Huffman Code file to mcgee.cde
		decode("mcgee.out","mcgee.cde","mcgee_uncompressed.txt");
		double main_E_time = System.currentTimeMillis();

		if (RUNTIME_OUTPUT) { // if RUNTIME_OUTPUT switch is true, outputs runtime of application
			runtimeOpt(main_S_time,main_E_time);
		}

		System.exit(0); // Terminates application

	}


	/**
	 * runTimeOpt():
	 * -
	 *
	 * @param startTime
	 * @param endTime
	 */
	private static void runtimeOpt(double startTime, double endTime) { // If runtime output is enabled, runs this method that checks the runtime.
		double runTime = (endTime - startTime); // subtracts beginning time from start time, to get the programs runtime.
		runTime = runTime/1000.0; 				// converts runtime to seconds
		System.out.println("");
		System.out.println("");
		System.out.println("-- RUNTIME EXECUTION --");
		System.out.println("");
		System.out.println("Time: " + runTime + " seconds");
		System.out.println("");
		System.out.println("");
	}

	/**
	 * encode() :
	 *  -	Method for Encoding & compressing file using Huffman Code.
	 *
	 * @param fileName Original filename to compress
	 * @param codeFileName	The code File name that stores characters as (int) and their corresponding Huffman Code.
	 * @param compressedFileName	The compressed file name to output.
	 */
	public static void encode(String fileName, String codeFileName, String compressedFileName) { // Method for Encoding file with Huffman generated code.



		charArrCnt = readCharFreq(fileName); // counts characters and places them into array of characters.
		addToTreeMap();						// add each character to a treeMap.
		generateTree();						// generates the Tree Structures based on PriorityQueue.
		traverseTree();						// Traverses Tree Structure to determine Huffman Codes.
		addToHufStr();						// Converts Huffman Code from Array, into String values.
		sortVal();							// Sorts the output Values into PriortyQueue to order the CodeFile output.
		if (DEBUG_OUTPUT) {
		testTree();							// Prints tree Structures to console if Debug Enabled.
		}

		outputCode(codeFileName);					// Outputs codes from treeMap to CodeFile.
		compressFile(fileName,compressedFileName); // compresses & Encodes file using Huffman Code.



	}

/**
 * // decode():
 * -	 Method for Decoding file compressed with Huffman code.
 *
 * @param CompressedfileName File name of the Compressed file to decode
 * @param codeFile	File name of code file, that contains Huffman Codes to compare against Compressed file.
 * @param decompressedFileName	Desired file name of the output file, that is the uncompressed version of CompresesedfileName.
 */
	public static void decode(String CompressedfileName,String codeFile,String decompressedFileName) { // Method for Decoding file compressed with Huffman code.

		readCodeFile(codeFile);										 // Reads codeFile into a treeMap
		readWriteFinalFile(CompressedfileName,decompressedFileName); // Reads Compressed File, translates it, and outputs de-compressed file.

	}

	/**
	 * readCharFreq() :
	 * -	Reads each characters frequency
	 *
	 * @param fileName the file name/location of where to read characters frequencies
	 * @return returns an array of type (int) thats index's correspond to ASCII values, and array (int) that stores each characters frequency.
	 *
	 */
	public static int[] readCharFreq(String fileName) { // Reads frequency of characters in given file
		/*
		 Reads file in based on fileName , and counts the frequency of each character used.
		 Returns int[] array where index = ASCII number code/Character,
		 and value at array[index] = count/frequency of char at index value.
		*/
		int[] charFreqArr = new int[256 + 1]; // Array Size is 257, so array index 1-256 are valid.

		f0 = new File(fileName);
		try {
			raf0 = new RandomAccessFile(f0, "r");
		} catch (FileNotFoundException e) {
			System.err.println("File not found error!");
			e.printStackTrace();
		}

		try {
				int inp = raf0.read();
				// While file has input, increment charFreqArr counter.
				while(inp != -1) {
					charFreqArr[inp]++;
					inp = raf0.read();
				}

			} catch (IOException e) {
				System.err.println("File input read error!");
				e.printStackTrace();
			}

			// Places character and their frequency into a map.
			for (int i=1;i<charFreqArr.length;i++) {
				charFreq.put((Character)((char)i), charFreqArr[i]);
				if (charFreqArr[i] != 0) {
					charMap.put((Character)((char)i), charFreqArr[i]);
				}
			}

			//returns an array of frequencies, with their index equivalent to their ASCII character value.
			return charFreqArr;

	}



	public static void onPC() { // Determines if application is running on Home-Personal PC or other environment.

		/*
		 * Only used in main method, determines where to pull source file  (McGee.txt)
		 */

		// If running on Home PC
		if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")			// ---For Debugging USE ONLY---
				&& System.getProperty("user.name").toUpperCase().contains("MENOX")) {
			onPC = true;
			fileDIR = "U:/Classes/Cs340/Huffman/";
		}
		//Otherwise set default location to Lab Directory
		else {

			onPC = false;
			fileDIR = "/home/student/Classes/Cs340/Huffman/";
		}



	}


	public static void addToTreeMap() { // Adds each Value into a TreeMap to be later searched.


		//Iterates through TreeNode Queue
		Iterator<TreeNode> itr = TnQue.iterator();
		while (itr.hasNext()) {
			TreeNode a = itr.next();
			if ((Character)a.ch != null) {
				// If Character is valid, Create new value and insert into TreeMap.
			Value hc = new Value((Character)(char)a.ch,a.freq);
			treeMap.put(a.ch,hc);
			}
		}

	}

	public static void addToHufStr() { // Used to convert ArrayList Implementation of Huffman Codes -> String of Huffman Codes.

		//Run through array of Characters
		for (int i=0;i<charArrCnt.length;i++) {

			// If character is present in file
			if (charArrCnt[i] != 0) {

				Value val = treeMap.get((char)i);
				val.setHufStr(); // Set Huffman Value String.




			}

		}

	}

	public static void generateTree() { // Generates Tree using TreeNodes. Sets the stage for Finding Huffman Values.

		//Run through array of Characters
		for (int i=1;i<charArrCnt.length;i++) {

			// If character is present in file
			if (charArrCnt[i] != 0) {
			TreeNode tmpTN = new TreeNode((char)i);
			tmpTN.freq = charArrCnt[i];
			TnQue.add(tmpTN); // Add to PriorityQueue
			}

		}


		int n = TnQue.size();
		for (int i=1; i<n;i++) {
			TreeNode z = new TreeNode(); // Creates new TreeNode()
			TreeNode x = TnQue.poll(); // Extracts minimum value TreeNode from Queue
			TreeNode y = TnQue.poll(); // Extracts second lowest value from Queue

			z.L_Child = x;			// New Nodes Left Child =  lowest
			z.R_Child = y;			// New Nodes Right Child = second-lowest Value
			z.children.add(x);
			z.children.add(y);
			z.freq = (x.freq + y.freq);		// Frequency of New Node is Left Child Frequency + Right Child Frequency
			x.Parent = z;					// Sets L-Child Parent Relationship
			y.Parent = z;					// Sets R-Child Parent Relationship

			TnQue.add(z);					// Inserts new node to TreeNode Queue.
		}


		if (DEBUG_OUTPUT) { // If Debug-Enabled output contents of TreeNode PriorityQueue to console using iterator.
		Iterator<TreeNode> itr2 = TnQue.iterator();
		while (itr2.hasNext()) {
			System.out.println(itr2.next().toString());

		}
		}




	}

	public static void traverseTree() { // Creates an Iterator to iterate through every Node in TnQue Priority Queue.

			Iterator<TreeNode> itr;
			itr = TnQue.iterator();

			while(itr.hasNext()) { // While TreeNode Queue has data:
			traverse(itr.next()); // Call Traverse of Head/Root Node. (traverse() is Recursively defined)
			}

	}

	/**
	 * traverse():
	 * -	Takes a node from the tree and follows it down each branch (recursively) to assign a huffman value.
	 *
	 *
	 * @param root takes the node and traverses it down the tree to assign huffman values.
	 */
	public static void traverse(TreeNode root) { //Take a Node from the tree, and follows it down each branch, assigning a Huffman Value for each node.
												//*Recursively Defined*
		// If Left Child Exists traverse Left Child.
		if(root.L_Child != null) {
			root.L_Child.addToHuffman(root, 0);

			if (root.L_Child.ch != null) {
				Value val = new Value(root.L_Child.ch,root.L_Child.freq,root.L_Child.Huffman);
				treeMap.put(root.L_Child.ch,val);
			}
			traverse(root.L_Child);
		}
		// If Right Child Exists traverse Right Child.
		if(root.R_Child != null) {
			root.R_Child.addToHuffman(root, 1);
			if ((Character)root.R_Child.ch != null) {
				Value val = new Value(root.R_Child.ch,root.R_Child.freq,root.R_Child.Huffman);
				treeMap.put(root.R_Child.ch,val);
			}
			traverse(root.R_Child);
		}


	}

	public static void testTree() { // Tests the treeMap to ensure all Values & Huffman codes are accurate. Only actived when DEBUG_OUTPUT is true!

		// runs through array of characters
		for (int i=0;i<charArrCnt.length;i++) {

			if (charArrCnt[i] != 0) { // for characters that appear at least once

				Value val = treeMap.get((char)i);
				System.out.println(val.toString());

			}
		}

		System.out.println("");
		System.out.println("--------------------------------------------");
		System.out.println("");
	}

	public static void addHufToTreeNode() { // Copies Huffman Code from Value Objects, to TreeNode Objects.

		// runs through array of characters
		for (int i=0;i<charArrCnt.length;i++) {

			if (charArrCnt[i] != 0) {				// for characters that appear at least once
				Value val = treeMap.get((char)i); //Takes the Value from TreeMap and copies the values to TreeNode object
				TreeNode trN;
				for (int j=0;j<TnAL.size();j++) {
					if (TnAL.get(j).ch == (char)i) {
					trN = TnAL.get(j);
					trN.Huffman = val.Huffman; // (Copies Value => TreeNode)
				}
			}

		}

	}
	}

	/**
	 * outputCode() :
	 * -	Outputs Code File of characters as ASCII ints, and their corresponding Huffman Values
	 *
	 * @param codeFileName the file name/location to output the code file to.
	 */
	public static void outputCode(String codeFileName) { //Outputs Code File which is later used to uncompress file.

		try {
			FileOutputStream codeFl = new FileOutputStream(codeFileName);
			PrintStream out = new PrintStream(codeFl);

			// runs through array of characters
			for (int i=0;i<charArrCnt.length;i++) {

				if (charArrCnt[i] != 0) {	// for characters that appear at least once


					Value val = ValQue.poll();	// extract highest-order (most frequent) Value
					out.println((int)val.ch + "," + val.HuffmanStr); // Print Value character, and Huffman to File
				}
			}

			out.close();
			codeFl.close();
		} catch (IOException e) {
			System.err.println("File Read/Write Error!");
			e.printStackTrace();
		}
	}

	public static void compressFile(String originalFile,String compressedFileName) { //Compresses original file, and outputs it to designated compressed file
		try {

			RandomAccessFile fin = new RandomAccessFile(new File(originalFile),"r");
			BitOutputStream btFileOut = new BitOutputStream(compressedFileName);

			int chIn = (int) fin.read();
			while (chIn != -1) { 						 // While input exists,
				Value val = treeMap.get((char)chIn);    // get value and
				btFileOut.writeString(val.HuffmanStr); // print code value to file
				chIn=fin.read();
			}
			btFileOut.close();

			fin.close();

		} catch (IOException e) {
			System.err.println("File Read/Write Error!");
			e.printStackTrace();
		}


	}

	public static void sortVal() { // Organizes Values in Highest-Order Priority for later use to output to CodeFile

		// runs through array of characters
		for (int i=0;i<charArrCnt.length;i++) {

			// for all characters that appear at least once
			if (charArrCnt[i] != 0) {
				Value val = treeMap.get((char)i); // Get Value at characters index.
				ValQue.add(val);				// Insert into PriortyQueue of Values.
			}
		}
	}

	public static void readWriteFinalFile(String compressedFile, String uncompressedFileName) { // Reads the compressed file, and Outputs the uncompressed File. Used in Decode().
		try {
		BitInputStream bis = new BitInputStream(compressedFile);
		FileOutputStream unCompFile = new FileOutputStream(uncompressedFileName);
		PrintStream outPrStr = new PrintStream(unCompFile);
		String input = ""; // blank input.
		int bin = bis.nextBit();
		do {			// While file input exists:
			input += bin; 							// input = previous value... + current value.
			if (codeExist(input)) { 				// if that code exists & is a valid Huffman Code
					char ch = revTreeMap.get(input);
					outPrStr.print(ch); // retrieve the character with that code.
					input = "";			// reset the input String for next code

			}

			bin = bis.nextBit();
		} while(bin != -1) ;

		outPrStr.close();



		} catch(IOException e) {

			System.err.println("File Read/Write Error!");
			e.printStackTrace();
		}

	}

	public static void readCodeFile(String codeFile) { // Reads in the Code File into a TreeMap.
		try {
			File fl0 = new File(codeFile);
			Scanner sc0 = new Scanner(fl0);
			String input = null;
			int chr = 0;
			String huffCode;

			/*
			 * The code below uses two scanners to read in the text Code File. One scanner to retrieve each line
			 * in the code file, and One scanner to parse that String into two, using the delimiter function.
			 */

			while(sc0.hasNextLine()) {
				input = sc0.nextLine();

				Scanner sc1 = new Scanner(input);
				sc1.useDelimiter(",");

				chr = Integer.parseInt(sc1.next());
				huffCode = sc1.next();

					Value tmpVal = new Value();
					tmpVal.ch = (char)chr;
					tmpVal.HuffmanStr = huffCode;
					codeFileTreeMap.put((char)chr, tmpVal); // Adds Value to new TreeMap
					revTreeMap.put(huffCode, (char)chr); // Adds Huffman Code & Character to different TreeMap to allow HuffmanCode to be the Key
				sc1.close();
			}

			if (DEBUG_OUTPUT) {
				testReadCodeFile();
			}
			sc0.close();




		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testReadCodeFile() { // Tests the content of the Code File which contains Characters, and their corresponding Huffman Code.
		Set<Character> keySet = codeFileTreeMap.keySet();
		Iterator<Character> itr;
		itr = keySet.iterator();
		while (itr.hasNext()) {
			char chr = itr.next();
			Value tmpVal = codeFileTreeMap.get(chr);
			System.out.println(tmpVal.toString()); // Prints Code File Contents to console. (Use for Debugging)


		}

	}


	public static boolean codeExist(String huffCode) { //Determines if a String of Binary values is a valid Huffman Code
		return revTreeMap.containsKey(huffCode);

	}

	private static class TreeNode implements Comparable<TreeNode> { //TreeNode Class that represents a Node in a Tree.

		/*
		 * TreeNode Class is a structure that holds two child nodes, for use with building
		 * a Binary Tree. This gives us a method to Traverse the Tree to find the accurate Huffman Code Values.
		 *
		 */

		private Character ch = null; // character that a node can optionally represent
		private int freq; // frequency of character or (Node)
		private ArrayList<Integer> Huffman = new ArrayList<Integer>(); //Huffman Code Implementation as an ArrayList of Integers
		private Integer nodeASCIIindx;	// Nodes ASCII index value
		private ArrayList<TreeNode> children = new ArrayList<TreeNode>(); // ArrayList of all the TreeNodes Children
		private TreeNode Parent = null;	// Parent Node of TreeNode
		private TreeNode L_Child = null; // Left Child of TreeNode
		private TreeNode R_Child = null; // Right Child of TreeNode


		private TreeNode(Character ch) {
			this.ch = ch;
			this.nodeASCIIindx = (Integer)(int)ch;
		}

		private TreeNode() {


		}

		public String toString() {

			return " char:  " + ch + "      ASCII:   " + nodeASCIIindx +
					"      Occurs:   " + freq + "     Children: " + children.toString();

		}


		private void addToHuffman(TreeNode val, Integer intg) { // Adds a Integer to ArrayList Implementation of Huffman Code
			for (int i=0;i<val.Huffman.size();i++) {
				this.Huffman.add(val.Huffman.get(i));
			}
			this.Huffman.add(intg); // Adds (0 or 1) to ArrayList

		}

		@Override
		public int compareTo(TreeNode tNode) { // ComareTo method to allow comparison for PriorityQueue.
			// TODO Auto-generated method stub
			int compare = 0;
			if(this.freq > tNode.freq) {
				compare = 1;
			}
			else if (this.freq < tNode.freq) {
				compare = -1;
			}

			else if (this.freq == tNode.freq) {
				compare = 0;
			}

			else {
				System.err.println("Compare Error");
			}

			return compare;

		}

	}

	private static class Value implements Comparable<Value>{ // Value stores Character, Frequency of Character , and Huffman Code

		/*
		 * Differs from TreeNode Object in the sense that Value's must store a character.
		 * While TreeNodes can optionally store a character. This is done so TreeNodes can be used
		 * and implemented as a Tree.
		 *
		*/

		private static final boolean ARR_LIST_OUT = false; // Output Debug Huffman Code as ArrayList or as String. (Default = false => String)

		private Character ch = null; // character of Value
		private Integer freq;		// Frequency of Character
		private ArrayList<Integer> Huffman = new ArrayList<Integer>(); // Huffman Code as ArrayList
		private String HuffmanStr = ""; // Huffman Code as String


		private Value(Character ch, Integer freq, ArrayList<Integer> Huffman) {
			this.ch = ch;
			this.freq = freq;
			this.Huffman = Huffman;
		}

		private Value(Character ch, Integer freq) {
			this.ch = ch;
			this.freq = freq;

		}

		public Value() {

		}



		private void setHufStr() { // Converts Huffman ArrayList => Huffman String.
			for (int i=0;i<Huffman.size();i++) {
				HuffmanStr += Huffman.get(i);
			}
		}


		@Override
		public String toString() { // Override To-String Method. Uses Hard Coded switch to determine which HuffCode implementation to use
			String out = "";
			if (ARR_LIST_OUT) {
			out = " Char: " + this.ch + "   Freq: " + this.freq + "   Huff: " + Huffman.toString();
			}
			else if (!ARR_LIST_OUT) {
			out = " Char: " + this.ch + "   Freq: " + this.freq + "   Huff: " + HuffmanStr;
			}
			return out;
		}

		@Override
		public int compareTo(Value val) { // Allows Values to be compared with each other, for use in PriorityQueue.
			int compare = 0;
			if(this.freq > val.freq) {
				compare = -1;
			}
			else if (this.freq < val.freq) {
				compare = 1;
			}

			else if (this.freq == val.freq) {
				compare = 0;
			}

			else {
				System.err.println("Compare Error");
			}

			return compare;

		}



	}


}


// -------- Necessary classes written by Dr. Hansen for BitInput and BitOutput --------

 	class BitOutputStream {

    BufferedOutputStream out;
    int byt;
    int offset;

    /** constructs a BitOutStream.
     * Program exits if there is a problem
     * opening the file.
     */
    public BitOutputStream(String fileName) {
        try {
            out = new BufferedOutputStream(
                    new FileOutputStream(
                    new File(fileName)));


            offset = 8;
        } catch (Exception e) {
            System.err.println("Problem opening BitOutputStream");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /** Writes one bit to the file */
    public void writeBit(int bit) {
        byt <<= 1;
        byt += bit;
        offset -= 1;

        try {
            if (offset == 0) {
                offset = 8;
                out.write(byt);
                byt = 0;
            }
        } catch (Exception e) {
            System.err.println("Problem writing to BitOutputStream");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Write the bits contained in a String to the file.  The String is
     * interpreted as "1" is a 1 bit.  Any other character is a 0 bit.
     * */
    public void writeString(String bits) {
        for (int i = 0; i < bits.length(); i++) {
            int bit = (bits.charAt(i) == '1') ? 1 : 0;
            writeBit(bit);
        }
    }

    /** Closes the file.  Since the file must contain a even number of bytes,
    the final byte is padded with 0s. */
    public void close() throws IOException {
        while (offset != 8) {
            writeBit(0);
        }

        out.close();
    }

    /** A small test main */
    public static void main(String[] args) throws Exception {
        BitOutputStream bs = new BitOutputStream("Junk.bits");

        for (char ch = 'a'; ch <= 'z'; ch++) {
            for (int i = 8; i > 0; i--) {
                int res = getBit(ch, i);
                bs.writeBit(res);
            }
        }
        bs.close();

    }

    /** A helper method to help with the testing */
    public static int getBit(char ch, int bitPos) {
        int b = ch << (32 - bitPos);
        b >>>= (31);
        return (int) b;
    }
}

/** BitInputStream contains methods to read one bit from a file at a time.
 * @author Stuart Hansen
 * @version February 24, 2008
 */
 	class BitInputStream {

    private BufferedInputStream in;
    private int byt;
    private int bitMask;


    public BitInputStream(String fileName) {
        try {
            in = new BufferedInputStream(
                    new FileInputStream(
                    new File(fileName)));

            byt = in.read();
            bitMask = 0x80;
        } catch (Exception e) {
            System.err.println("Problem opening bitStream");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /** readBit() returns a 0 or 1.
     *  -1 is returned at the end of file.<br><br>
     *	 There is a quirk that padded 0s are returned from
     *	 the last byte, if it is not full.
     */
    public int readBit() {
        try {
            if (bitMask == 0) {
                bitMask = 0x80;
                byt = in.read();
                if (byt == -1) {
                    return -1;
                }
            }
        } catch (Exception e) {
            System.err.println("Problem reading from BitStream");
            System.err.println(e.getMessage());
        }
        int result = ((bitMask & byt) != 0) ? 1 : 0;
        bitMask >>>= 1;
        return result;
    }

    /** nextBit() is an alias for readBit() */
    public int nextBit() {
        return readBit();
    }

    /**
     * A small test main
     * @param args
     */
    public static void main(String[] args) {
        BitInputStream fin = new BitInputStream("mcgee.txt");
        int next = fin.nextBit();
        int count =0;
        while (next != -1) {
            System.out.print(next + " ");
            next = fin.nextBit();
            count++;
            if (count%8 == 0)
                System.out.println();
        }
    }
}









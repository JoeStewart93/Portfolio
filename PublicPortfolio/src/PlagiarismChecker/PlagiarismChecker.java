package PlagiarismChecker;
/**
 * @title Plagiarism Checker
 * @author Joe Stewart
 * 
 * This application uses a dynamic programming algorithm that checks for the longest common substring length across
 * multiple files to detect potential plagiarism. 
 * 
 */

// - Joe Stewart -
// -- Plagiarism Checker --
                                                                                                                      
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
                                                                                                                       
                                                                                                                      
                                                                                                                        
public class PlagiarismChecker {

	public static final boolean RUN_FILE_LOCAL = true; // TRUE forcefully runs file locally
	public static final boolean PRINT_ONLY_SUSPICIOUS = true; // Toggle on to print only suspicious files
	private static boolean onPC = false;	// S= If running on local machine/lab directory
	public static String fileDIR = ""; // Path to active directory

	public static void onPC() { // Determines if application is running on Home-Personal PC or other environment.


		/*
		 * Only used in main method, determines where to pull source file
		 */

		// If running on Home PC
		
		

		if (RUN_FILE_LOCAL) {
			fileDIR = "C:/Users/menox/workspace/CSCI_340 Data Structures/Plagiarism/";
		}

		if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")			// ---For Debugging USE ONLY---
				&& (System.getProperty("user.name").toUpperCase().contains("MENOX")
						|| (System.getProperty("user.name").toUpperCase().contains("JOE")))) {
			onPC = true;
			if (!RUN_FILE_LOCAL) fileDIR = "U:/Classes/Cs340/Plagiarism/";

		}
		//Otherwise set default location to Lab Directory
		else {

			onPC = false;
			if (!RUN_FILE_LOCAL) fileDIR = "/home/student/Classes/Cs340/Plagiarism/";
		}



	}



	/**
	 * plagiarismScore() 
	 * 
	 * @param filename1 First file to compare
	 * @param filename2 Second file to compare
	 * @return returns a value that ranks the likelihood of plagiarism. Based on LCS similarity.
	 * 
	 */
	public double plagiarismScore(String filename1, String filename2) {
		onPC(); // Runs method to determine application path
		
		String source1 ="";String source2 = "";
		double val = 0;
		double length = 0;
		try {

			RandomAccessFile f0 = new RandomAccessFile(new File(fileDIR + filename1),"r");

			int inp = f0.read();

			while(inp != -1) {
				source1 = source1 + (char)inp;
				inp = f0.read();
			}

				f0 = new RandomAccessFile(new File(fileDIR + filename2),"r");


			int inp2 = f0.read();

			while(inp2 != -1) {
				source2 = source2 + (char)inp2;
				inp2 = f0.read();
			}

			f0.close();
			
			// Calculates the Plagiarism score:
			length = (double) lcsLength(source1,source2);
			val = (double) ((200.0 * length) / (source1.length() * 1.0 + source2.length() * 1.0) );




		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return val;



	}

	/**
	 * 
	 * @param filenames array of filenames to compare against each other
	 * @param threshold threshold to determine suspicious
	 */
	public void plagiarismChecker(String[] filenames, double threshold) {
		HashSet<FilePair> fileSet = new HashSet<>();

		for (int i=0;i<filenames.length;i++) {
			for (int j=0;j<filenames.length;j++) {
				if (i!=j) { // If files aren't equal

				double val = plagiarismScore(filenames[i],filenames[j]); // Generates Plagiarism Score. (Memoized)
				FilePair temp;

				if (round(val,2) >= threshold) { // If score is above threshold create filepair with suspicion
					FilePair tmpFP = new FilePair(filenames[i],filenames[j],round(val,2),true);
					temp = tmpFP;
				}

				else  { // Otherwise create filepair without suspicion.
					FilePair tmpFP = new FilePair(filenames[i],filenames[j],round(val,2),false);
					temp = tmpFP;
				}

				if (!fileSet.contains(temp)) {
					fileSet.add(temp);
					if (!PRINT_ONLY_SUSPICIOUS) 
						System.out.println(temp.toString()); // Prints all Files
					
					else { // Otherwise Print only Suspicious Files
						if (temp.score >= threshold) {
							System.out.println(temp.toString());
						}
					}
				}



			}
		}
		}

		System.exit(0);

	}
	
	
	/**
	 * 
	 * @param value value to round
	 * @param places decimal places to round to
	 * @return rounded double value by specifications provided
	 */
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}


	/**
	 * 
	 * @param prog1 Program 1 contents
	 * @param prog2 Program 2 contents
	 * @return returns the longest common substring length between the two programs.
	 */
	public int lcsLength(String prog1, String prog2) {


		int n = prog1.length(); int m = prog2.length();
		int length = 0;
		int max = 0;

		int aX;
		int bX;
		int[][] X = new int[2][m+1];
		
		for(int j=0; j<n ;j++) {
			for (int k=0;k<m;k++) {
				aX = j%2;
				bX = (j+1)%2;
				if (prog1.charAt(j) == prog2.charAt(k))
					X[bX][k+1] = ++X[aX][k]; // Aligns the match
				else
					X[bX][k+1] = Math.max(X[aX][k+1], X[bX][k]); // Choose to ignore a character
			}
		}
		return X[1][m];
	}

	
	/**
	 * FilePair is an Object that store two pairs of file names, their Plagiarism score, and if they are suspicious.
	 * 
	 * @author Joe Stewart
	 *
	 */
	
	private class FilePair {
		private String[] files = new String[2];
		private double score;
		private boolean suspicious = false;

		private FilePair(String fileA, String fileB, double pScore, boolean sus)  {
			files[0] = fileA;
			files[1] = fileB;
			score = pScore;
			this.suspicious = sus;
		}

		/**
		 * 
		 * @param file file to check if it's in file pair
		 * @return if the file is in the file pair
		 */
		private boolean containsFile(String file) {

			if (files[0].equals(file) || files[1].equals(file)) {
				return true;
		}
			else return false;
	}
		
		/**
		 * 
		 * @param args KeyPair to compare against
		 * @return if KeyPair is equal to another Keypair, return true
		 */
		public boolean equals(Object args) {
			FilePair fp = (FilePair) args;
			boolean output = false;
			String file1 = fp.files[0];
			String file2 = fp.files[1];
			double scoreVal = fp.score;

			if (this.score == scoreVal) {

				if (this.files[0].equals(file1) && this.files[1].equals(file2)) {
					output = true;
				}

				if (this.files[1].equals(file1) && this.files[0].equals(file2)) {
					output = true;
				}

			}
			return output;
		}

		/**
		 * @return returns to string of KeyPair, includes files, Plagiarism Score, and if the file is Suspicious.
		 */
		public String toString() {
			if (suspicious)
			return (this.files[0] + " & " + this.files[1] + "\t Score: " + this.score + "\t SUSPICIOUS");
			else
				return ( this.files[0] + " & " + this.files[1] + "\t Score: " + this.score);
		}
	}
}

package PlagiarismChecker;
import java.util.ArrayList;

public class TestPlagiarism {

	public static void main(String[] args) {
		//PlagiarismChecker pg = new PlagiarismChecker();
		PlagiarismChecker pc = new PlagiarismChecker();
        ArrayList<String> programs2 = new ArrayList<>();
        for (int i = 1; i < 30; i++){
            programs2.add("" + i);
        }                                                                                                         
        String [] programs = new String [programs2.size()];
        programs = programs2.toArray(programs);
                                                                                                    
        //String [] test = {"Combinations.java", "Fibonacci.java","LCS.java","Person.java"};
                                                                                                  
        //programs2.toArray();
        //System.out.println(pc.plagiarismScore("Combinations.java", "Fibonacci.java"));
        long start = System.currentTimeMillis();
        pc.plagiarismChecker(programs, 50.0);
        long end = System.currentTimeMillis();
        System.out.println("Took : " + ((end - start) / 1000));
        System.exit(0);
		}
	}
                                                                                      
                                                                                        
                                                                                         

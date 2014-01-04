import java.util.Scanner;
import java.util.TreeSet;

public class FormattedDFA {
	private String[] states;
	private String alphabet;
	private int[][] transFunc;
	private int start;
	private boolean[] accepts;

	/**
	 * Takes in parameters necessary to establish a state machine. Makes sure the input is valid.
	 * @param states - Takes a String[]. Checks for uniqueness.
	 * @param alphabet - Takes in a String. Each character represents an element in the alphabet. Checks for uniqueness
	 * @param transFunc - Takes in an array of the form int[states.length][alphabet.length]. Checks that values are a valid index for a state.
	 * @param start - Takes an int. Checks that it is a valid index for a state.
	 * @param accepts - Takes an array of booleans that represents whether state[i] is an accept state. Checks that length of this array is equal to length of states.
	 * @throws Exception - If there is at least one error in the parameters, will throw an exception with information in order of input.
	 */
	public FormattedDFA(String[] states,
			String alphabet, 
			int[][] transFunc, //table representation, states & alphabet as put in as indices 
			int start, 
			boolean[] accepts) throws Exception {
		//check parameters
		if ( !checkStates(states) ) {
			throw new Exception("Incorrect States");
		} else if ( !checkAlphabet(alphabet) ) {
			throw new Exception("Incorrect Alphabet");
		} else if ( !checkTransFunc(transFunc, states.length, alphabet.length()) ) {
			throw new Exception("Incorrect Tranfer Function");
		} else if ( !checkStart(start, states.length) ) {
			throw new Exception("Incorrect Start Value");
		} else if ( !checkAccepts(accepts, states.length)) {
			throw new Exception("Incorrect Accept States");
		}
		
		//set the DFA's parameters
		this.states = states;
		this.alphabet = alphabet;
		this.transFunc = transFunc;
		this.start = start;
		this.accepts = accepts;
	}
	
	/**
	 * Checks states for uniqueness.
	 * @param states
	 * @return  true - only if all states are unique
	 */
	private boolean checkStates(String[] states) {
		TreeSet<String> tester = new TreeSet<String>();
		
		for ( String s: states) {
			if (!tester.add(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks alphabet for uniqueness.
	 * @param alphabet
	 * @return true - only if all characters are unique
	 */
	private boolean checkAlphabet(String alphabet) {
		TreeSet<Character> tester = new TreeSet<Character>();
		
		for (char c : alphabet.toCharArray()) {
			if (!tester.add(c)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks range boundaries on the transfer function. All variables taken or immediately derived from constructor parameters.
	 * @param transFunc
	 * @param numStates
	 * @param numAlphabet
	 * @return a boolean representing if the transfer function is acceptable.
	 */
	private boolean checkTransFunc( int[][] transFunc, int numStates, int numAlphabet) {
		//check if states index
		if ( numStates != transFunc.length ) {
			//System.out.println("Failed numStates check");
			return false;
		}
		
		for ( int[] transGivSrc : transFunc ) {
			//check each alphabet index
			if ( transGivSrc.length != numAlphabet ) {
				//System.out.println("Failed numAlphabet check");
				return false;
			}
			
			for ( int dest : transGivSrc ) {
				//check each dest entry
				if ( dest >= numStates || dest < 0 ) {
					//System.out.println("Failed dest check");
					return false;
				}
			}
		
		}
		return true;
	}
	
	/**
	 * Checks range boundaries on start state. All variables taken or immediately derived from constructor parameters.
	 * @param start
	 * @param numStates
	 * @return a boolean representing if the start state is acceptable.
	 */
	private boolean checkStart(int start, int numStates) {
		return ( start < numStates && start >= 0 );
	}
	
	/**
	 * Makes sure that the list of accept states is the same length as the list of states.
	 * @param accepts
	 * @param numStates
	 * @return a boolean representing if the list of accepted states is acceptable. 
	 */
	private boolean checkAccepts(boolean[] accepts, int numStates){
		return (accepts.length == numStates);
	}
	
	/**
	 * Takes a string, makes sure it is acceptable, and runs it through the DFA.
	 * Prints starting state, then pairs of input and new state. Prints "Valid" or "Invalid" depending on final state.
	 * @param input - string composed of alphabet of DFA.
	 * @throws Exception if the input string does not conform to alphabet.
	 */
	public void feed( String input ) throws Exception {
		//check if input is made of alphabet
		for ( int i = 0; i < input.length(); i++) {
			if ( this.alphabet.indexOf( input.charAt(i) ) < 0) {
				throw new Exception("String not compatible with this alphabet.");
			}
		}
		
		//initialize DFA and print out starting state
		int currentState = this.start;
		System.out.println("Initial State: " + states[currentState]);
		
		//feed each character individually
		for ( int i = 0; i < input.length(); i++ ) {
			currentState = step(currentState, input.charAt(i));
			System.out.println("Current State: " + states[currentState]);
		}
		
		//check if it's an accepted state and output so
		if (this.accepts[currentState]) {
			System.out.println("Valid");
		} else {
			System.out.println("Invalid");
		}
	}

	/**
	 * Takes a state and a character and uses the transfer function to determine the next state.
	 * @param currentState
	 * @param inp
	 * @return the index of the next state
	 */
	private int step(int currentState, char inp) {
		//need to convert char into its index in alphabet for transFunc to work
		System.out.print("Input: " + inp + " ");
		return this.transFunc[currentState][this.alphabet.indexOf(inp)];
	}

	public static void main(String[] args) throws Exception {
		String[] states = {
				"q0",  //trap
				"q1",  //s
				"q2","q3","q4",  //w1,w2,w3
				"q5","q6","q7","q8",  //cL2_e,cL2_d,cL2_u,cL2_dot
				"q9",  //L2
				"q10","q11","q12",  //edu_e,edu_d,edu_e
				"q13"  //accept
		};
		String alphabet = "abcdefghijklmnopqrstuvwxyz.";
		//Trust me on this one, this is the whole table in my format. I had python do a lot of the work for me.
		//Could've made it simpler by compressing this into some other form. 
		int[][] transFunc = {
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},  //trap
				{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 9, 9, 0},  //s
				{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 3, 9, 9, 9, 10},  //w1
				{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 4, 9, 9, 9, 10},  //w2
				{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 5},  //w3
				{9, 9, 9, 9, 6, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0},  //cL2_e
				{9, 9, 9, 7, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10},  //cL2_d
				{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 9, 9, 9, 9, 9, 10},  //cL2_u
				{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10},  //cL2_dot
				{9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10},  //L2
				{0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},  //edu_e
				{0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},  //edu_d
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0},  //edu_u
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},  //accept
		};
		int start = 1;
		boolean[] accepts = {
				false,
				false,
				false, false, false,
				false,false,false,true,  //cL2_dot
				false,
				false,false,false,
				true  //accept
		};
		
		String input;
		Scanner userInput = new Scanner(System.in);
		
		FormattedDFA Assignment1 = new FormattedDFA(states,alphabet,transFunc,start,accepts);
		//Assignment1.feed("www.abcd.educ");
		System.out.println("Would you like to put in a string? y/n");
		
		while ( !"n".equals((input = userInput.next()))) {
			if ("y".equals(input)) {
				System.out.println("Which string would you like to put it over our alphabet of \"abcdefghijklmnopqrstuvwxyz.\"?");
				input = userInput.next();
				System.out.println(input);
				Assignment1.feed(input);
				System.out.println("\nWould you like to put in another?");
			} else {
				System.out.println("Please input y or n.");
			}
		}
		System.out.println("Thank you for your time.");
		userInput.close();
		
	}

}

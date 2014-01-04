import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;


public class dumbPDA {
	String[] states;
	String stringAlphabet, stackAlphabet;
	HashMap<Integer, ArrayList<Integer>> transFunc= new HashMap<Integer, ArrayList<Integer>>();	
	int start;
	boolean[] accepts;

	/**
	 * Checks validity of each input and initializes our PDA
	 * transition function - States X (StackAlph + eps) X (StringAlph + eps) --> P( StatesX(StringAlph + eps)). The domain String and Stack argument have been switched.
	 */
	public dumbPDA(String[] states,
			String stringAlphabet,
			String stackAlphabet,
			String[] transition,
			int start,
			boolean[] accepts) throws Exception {
		if (!checkStates(states)){
			throw new Exception("Incorrect States");
		} else if (!checkStringAlphabet(stringAlphabet)) {
			throw new Exception("Incorrect String Alphabet");
		} else if (!checkStackAlphabet(stackAlphabet)){
			throw new Exception("Incorrect Stack Alphabet");
		} else if (!checkTransFunc(transition, states.length,stringAlphabet.length(), stackAlphabet.length())) {
			throw new Exception("Incorrect Transition");
		} else if (!checkStart(start, states.length)) {
			throw new Exception("Incorrect Start");
		} else if (!checkAccepts(accepts,states.length)){
			throw new Exception("Incorrect Accepts");
		}
	}

	/**
	 * Makes sure the transition function is valid. Also initializes the PDA transfer function.
	 * @param transFunc - Each transition is going to be a string of numbers separated by spaces.
	 */
	private boolean checkTransFunc(String[] transFunc, int stateLen, int stringLen, int stackLen) 
	throws NumberFormatException {
		String[] transStr;
		int[] transInt = new int[5];
		int[] checkStart = {-1,-2,-2,-2,-2};
		int[] checkEnd = {stateLen,stackLen,stringLen,stateLen,stackLen};
		int key, val;
		
		//check each rule
		for ( String rule : transFunc) {
			//check rule length 
			transStr = rule.split(" ");
			if (transStr.length != 5) {
				return false;
			}
			
			//transfer to integer version
			for (int i = 0; i < 5; i++ ) {
				transInt[i] = Integer.parseInt(transStr[i]);
				if (transInt[i] == -1) {
					transInt[i] = checkEnd[i];
				}
			}
			
			//now check each int for limits
			//state stack input state stack
			for (int i = 0; i < 5; i++) {
				if ( checkStart[i] > transInt[i] || checkEnd[i] + 1 < transInt[i]) {
					return false;
				}
			}
			
			//now that it's okay, use it to initialize our transition function
			//eps is represented as the length of that alphabet for stack and string
			key = numsToKey(transInt[0],transInt[1],transInt[2]);
			val = transInt[3]*(stackLen+1)
					+(transInt[4]);
			if ( this.transFunc.get(key) == null ){
				this.transFunc.put(key, new ArrayList<Integer>());
			}
			if (this.transFunc.get(key).contains(val))
				return false;
			this.transFunc.get(key).add(val);
		}
		return true;
	}
	
	/**
	 * Takes a domain value and returns the key.
	 */
	private int numsToKey(Integer state, Integer stack, Integer string){
		Integer stackLen = this.stackAlphabet.length(), stringLen = this.stringAlphabet.length();
		return state*(stackLen+1)*(stringLen+1)
				+(stack)*(stringLen+1)
				+(string);
	}
	
	/**
	 * Makes sure each state only appears once.
	 */
	private boolean checkStates(String[] states) {
		TreeSet<String> tester = new TreeSet<String>();
		
		for ( String s: states) {
			if (!tester.add(s)) {
				return false;
			}
		}
		this.states = states;
		return true;
	}
	
	/**
	 * Makes sure each character in the string alphabet only appears once.
	 */
	private boolean checkStringAlphabet(String alphabet){
		TreeSet<Character> tester = new TreeSet<Character>();
		
		for (char c : alphabet.toCharArray()) {
			if (!tester.add(c)){
				return false;
			}
		}
		this.stringAlphabet = alphabet;
		return true;
	}
	
	/**
	 * Makes sure each character in the stack alphabet only appears once.
	 */
	private boolean checkStackAlphabet(String alphabet) {
		TreeSet<Character> tester = new TreeSet<Character>();
		
		for (char c : alphabet.toCharArray()) {
			if (!tester.add(c)){
				return false;
			}
		}
		this.stackAlphabet = alphabet;
		return true;
	}
	
	/**
	 * Makes sure the start value is an index of the states.
	 */
	private boolean checkStart(int start, int numStates) {
		this.start = start;
		return ( start < numStates && start >= 0 );
	}
	
	/**
	 * Makes sure the length of accepts is the same as the states.
	 */
	private boolean checkAccepts(boolean[] accepts, int numStates){
		this.accepts = accepts;
		return (accepts.length == numStates);
	}
	
	/**
	 * Takes a machine state String and converts it to an equivalent Integer[].
	 */
	private Integer[] stateStrToIntArray(String state){
		String[] temp;
		Integer[] current;
		
		//Just need to split by " " characters and then convert to integers
		temp = state.split(" ");
		current = new Integer[temp.length];
		for (int i = 0; i < temp.length;i++)
			current[i] = Integer.parseInt(temp[i]);
		return current;
	}
	
	/**
	 * Prints out if input string is valid or not under this PDA. Depending on construction of PDA, may loop indefinitely.
	 */
	public void feed(String input) {
		//Create machine state strings queue and add initial state
		LinkedList<String> machineStates = new LinkedList<String>();
		String initialState = String.format("%d 0", this.start), currentState;
		machineStates.add(initialState);
		
		Integer[] current, forprint;
		Integer key;
		boolean hasNoRules;
		
		System.out.printf(" Input String: %s%n",input);
		System.out.printf(" Initial State: %s%n", this.states[this.start]);
		
		//check for empty string
		if (input.length() == 0){
			if (this.accepts[this.start])
				System.out.println(" Valid");
			else
				System.out.println(" Invalid");
			return;
		}
		
		while (! machineStates.isEmpty()) {
			hasNoRules = true; //keeps track of whether this node has any applicable rules
			
			//get next state, pop from queue
			currentState = machineStates.removeFirst();
			//convert to form that can be operated on, Integer[]
			current = stateStrToIntArray(currentState);
			
			//If machine has a stack, check for rules that need to pop from the stack
			if (current.length > 2){
				//input element != epsilon
				key = getKey(current[0], current[current.length-1], current[1], input);
				//Do we have this key?
				if ( this.transFunc.containsKey(key)) {
					hasNoRules = false;
					//For each element in the range
					for (Integer newOutcome : this.transFunc.get(key)){
						//convert each state stack pair to equivalent Integer[] representation
						forprint = getValue(newOutcome);
						
						System.out.printf(" Read: %3c,  Popped: %3c,  New State: %s,  ", input.charAt(current[1]), this.stackAlphabet.charAt(current[current.length-1]), this.states[forprint[0]]);
						if (forprint[1] == this.stackAlphabet.length())
							System.out.println("Pushed: eps");
						else
							System.out.printf("Pushed: %3c%n", this.stackAlphabet.charAt(forprint[1]));
						
						//Add to machine and check if we have reached a valid leaf
						if (appendToMachineStates(machineStates,
								current,
								newOutcome,
								current[current.length-1],  // stack int
								current[1], // string int
								input.length())) {
							System.out.println(" Valid");
							return;
						}
					}
				}
				//input element = epsilon
				key = getKey(current[0], current[current.length-1], -1, input);
				//Do we have this key?
				if ( this.transFunc.containsKey(key)) {
					hasNoRules = false;
					//For each element in the range
					for (Integer newOutcome : this.transFunc.get(key)){
						//convert each state stack pair to equivalent Integer[] representation
						forprint = getValue(newOutcome);
						
						System.out.printf(" Read: eps,  Popped: %3c,  New State: %s,  ", this.stackAlphabet.charAt(current[current.length-1]),this.states[forprint[0]]);
						if (forprint[1] == this.stackAlphabet.length())
							System.out.println("Pushed: eps");
						else
							System.out.printf("Pushed: %3c%n", this.stackAlphabet.charAt(forprint[1]));
						
						//Add to machine and check if we have reached a valid leaf
						if (appendToMachineStates(machineStates,
								current,
								newOutcome,
								current[current.length-1],  // stack int
								-1, // string int
								input.length())) {
							System.out.println(" Valid");
							return;
						}
					}
				}
			}
			//stack element = epsilon
			key = getKey(current[0], this.stackAlphabet.length(), current[1], input);
			//Do we have this key?
			if ( this.transFunc.containsKey(key)) {
				hasNoRules = false;
				//For each element in the range
				for (Integer newOutcome : this.transFunc.get(key)){
					//convert each state stack pair to equivalent Integer[] representation
					forprint = getValue(newOutcome);
					
					System.out.printf(" Read: %3c,  Popped: eps,  New State: %s  ", input.charAt(current[1]), this.states[forprint[0]]);
					if (forprint[1] == this.stackAlphabet.length())
						System.out.println("Pushed: eps");
					else
						System.out.printf("Pushed: %3c%n", this.stackAlphabet.charAt(forprint[1]));
					
					//Add to machine and check if we have reached a valid leaf
					if (appendToMachineStates(machineStates,
							current,
							newOutcome,
							this.stackAlphabet.length(),  // stack int
							current[1], // string int
							input.length())) {
						System.out.println(" Valid");
						return;
					}
				}
			}
			//stack element = input element = epsilon
			key = getKey(current[0], this.stackAlphabet.length(), -1, input);
			//Do we have this key?
			if ( this.transFunc.containsKey(key)) {
				hasNoRules = false;
				//For each element in the range
				for (Integer newOutcome : this.transFunc.get(key)){
					//convert each state stack pair to equivalent Integer[] representation
					forprint = getValue(newOutcome);
					
					System.out.printf(" Read: eps,  Popped: eps,  New State: %s  ", this.states[forprint[0]]);
					if (forprint[1] == this.stackAlphabet.length())
						System.out.println("Pushed: eps");
					else
						System.out.printf("Pushed: %3c%n", this.stackAlphabet.charAt(forprint[1]));
					
					//Add to machine and check if we have reached a valid leaf
					if (appendToMachineStates(machineStates,
							current,
							newOutcome,
							this.stackAlphabet.length(),  // stack int
							-1, // string int
							input.length())) {
						System.out.println(" Valid");
						return;
					}
				}
			}
			//No applicable rules?
			if (hasNoRules)
				System.out.println("  No more applicable rules. Crashed before reaching end of input.");
		}
		//No more machine states -> Invalid input
		System.out.println(" Invalid");
		return;
		
	}
	
	/**
	 * Takes in a state and the rule to be applied to it and adds the resulting state to the queue.
	 * @param machineStates - Queue of Machine State Strings (necessary for nondeterminism)
	 * @param current - The current node being worked on
	 * @param newOutcome - the range element of the rule being applied
	 * @param stack - length of alphabet
	 * @param string - length of alphabet
	 * @param inputLength
	 * @return - if we have reached a valid leaf
	 */
	private boolean appendToMachineStates(LinkedList<String> machineStates,
			Integer[] current, Integer newOutcome, Integer stack, Integer string, Integer inputLength) {
		int length = current.length, staLen = this.stackAlphabet.length(), stackCopyLimit = length;
		Integer[] rule = getValue(newOutcome);;
		
		//determine length of new machine state array and if stack is popped
		//if stack element of domain value is epsilon
		if ( stack  != staLen) {
			length--;
			stackCopyLimit--;
		}
		//if stack element of range value is epsilon
		if (rule[1] != staLen)
			length++;
		
		
		//GENERATE new machine state
		Integer[] newState = new Integer[length];
		// state
		newState[0] = rule[0];
		
		// index
		// if eps, index stays the same
		newState[1] = current[1];
		if (string != -1)
			newState[1] ++;
		
		// stack
		if (newState.length > 2) {
			if (rule[1] != this.stackAlphabet.length())
				newState[newState.length -1] = rule[1];
			for (int i = 2; i < stackCopyLimit; i++)
				newState[i] = current[i];
		}		
		
		//ARE we at the end of the input?
		if (newState[1] == inputLength) {
		// if in accept state: return true
			if ( this.accepts[newState[0]]) {
				return true;
			}
		// else, don't change machineStates
			System.out.println("  End of input, not in accept state.");
			return false;
		}
		
		//CREATE the machine string from it and add to machine states
		StringBuilder stateString = new StringBuilder(newState[0].toString());
		for (int i = 1; i < newState.length; i++ ) {
			stateString.append(" " + newState[i].toString());
		}
		machineStates.add(stateString.toString());
		return false;
	}

	/**
	 * Converts Integer representation of element in the range of the transfer function into equivalent Integer[] representation.
	 */
	private Integer[] getValue(Integer newOutcome) {
		Integer[] value = new Integer[2];
		Integer staLen = this.stackAlphabet.length();
		value[1] = newOutcome % (staLen+1);
		value[0] = (newOutcome - value[1]) / (staLen+1);
		return value;
	}

	/**
	 * Given the domain as three Integers representing indices, returns the Integer representation of an element in the domain.
	 */
	private Integer getKey(Integer state, Integer stack, Integer string, String input){
		int z;
		
		// to determine if string is eps or being advanced
		if (string == -1 ) {
			z = this.stringAlphabet.length();
		} else {
			z = this.stringAlphabet.indexOf(input.charAt(string));
		}
		
		return numsToKey(state, stack, z);
	}
	
	public static void main(String[] args) throws Exception {
		String[] states= {"q0","q1","q2","q3","q4"};
		String stringAlphabet = "abcdefghijklmnopqrstuvwxyz0123456789+-*/()$"; //eps = 43
		String stackAlphabet = "$("; //eps = 2
		//a -> 0
		//0 -> 26
		//+ -> 36
		//$ -> 42
		String[] transition={
				"0 -1 42 1 0", // q0, $, eps -> q1, $
				"3 0 42 4 -1", // q3, $, $ -> q4, eps
				"1 -1 40 1 1", // q1, (, eps -> q1, (
				"3 1 41 3 -1", // q3, ), ( -> q3, eps
				"3 -1 36 1 -1", // q3, +, eps -> q1, eps
				"3 -1 37 1 -1", // q3, -, eps -> q1, eps
				"3 -1 38 1 -1", // q3, *, eps -> q1, eps
				"3 -1 39 1 -1", // q3, /, eps -> q1, eps
				"1 -1 0 2 -1", // q1 a eps -> q2 eps
				"2 -1 0 3 -1", // q2 a eps -> q3 eps
				"1 -1 1 2 -1", // q1 b eps -> q2 eps
				"2 -1 1 3 -1", // q2 b eps -> q3 eps
				"1 -1 2 2 -1", // q1 c eps -> q2 eps
				"2 -1 2 3 -1", // q2 c eps -> q3 eps
				"1 -1 3 2 -1", // q1 d eps -> q2 eps
				"2 -1 3 3 -1", // q2 d eps -> q3 eps
				"1 -1 4 2 -1", // q1 e eps -> q2 eps
				"2 -1 4 3 -1", // q2 e eps -> q3 eps
				"1 -1 5 2 -1", // q1 f eps -> q2 eps
				"2 -1 5 3 -1", // q2 f eps -> q3 eps
				"1 -1 6 2 -1", // q1 g eps -> q2 eps
				"2 -1 6 3 -1", // q2 g eps -> q3 eps
				"1 -1 7 2 -1", // q1 h eps -> q2 eps
				"2 -1 7 3 -1", // q2 h eps -> q3 eps
				"1 -1 8 2 -1", // q1 i eps -> q2 eps
				"2 -1 8 3 -1", // q2 i eps -> q3 eps
				"1 -1 9 2 -1", // q1 j eps -> q2 eps
				"2 -1 9 3 -1", // q2 j eps -> q3 eps
				"1 -1 10 2 -1", // q1 k eps -> q2 eps
				"2 -1 10 3 -1", // q2 k eps -> q3 eps
				"1 -1 11 2 -1", // q1 l eps -> q2 eps
				"2 -1 11 3 -1", // q2 l eps -> q3 eps
				"1 -1 12 2 -1", // q1 m eps -> q2 eps
				"2 -1 12 3 -1", // q2 m eps -> q3 eps
				"1 -1 13 2 -1", // q1 n eps -> q2 eps
				"2 -1 13 3 -1", // q2 n eps -> q3 eps
				"1 -1 14 2 -1", // q1 o eps -> q2 eps
				"2 -1 14 3 -1", // q2 o eps -> q3 eps
				"1 -1 15 2 -1", // q1 p eps -> q2 eps
				"2 -1 15 3 -1", // q2 p eps -> q3 eps
				"1 -1 16 2 -1", // q1 q eps -> q2 eps
				"2 -1 16 3 -1", // q2 q eps -> q3 eps
				"1 -1 17 2 -1", // q1 r eps -> q2 eps
				"2 -1 17 3 -1", // q2 r eps -> q3 eps
				"1 -1 18 2 -1", // q1 s eps -> q2 eps
				"2 -1 18 3 -1", // q2 s eps -> q3 eps
				"1 -1 19 2 -1", // q1 t eps -> q2 eps
				"2 -1 19 3 -1", // q2 t eps -> q3 eps
				"1 -1 20 2 -1", // q1 u eps -> q2 eps
				"2 -1 20 3 -1", // q2 u eps -> q3 eps
				"1 -1 21 2 -1", // q1 v eps -> q2 eps
				"2 -1 21 3 -1", // q2 v eps -> q3 eps
				"1 -1 22 2 -1", // q1 w eps -> q2 eps
				"2 -1 22 3 -1", // q2 w eps -> q3 eps
				"1 -1 23 2 -1", // q1 x eps -> q2 eps
				"2 -1 23 3 -1", // q2 x eps -> q3 eps
				"1 -1 24 2 -1", // q1 y eps -> q2 eps
				"2 -1 24 3 -1", // q2 y eps -> q3 eps
				"1 -1 25 2 -1", // q1 z eps -> q2 eps
				"2 -1 25 3 -1", // q2 z eps -> q3 eps
				"2 -1 26 3 -1", // q2 0 eps -> q3 eps
				"2 -1 27 3 -1", // q2 1 eps -> q3 eps
				"2 -1 28 3 -1", // q2 2 eps -> q3 eps
				"2 -1 29 3 -1", // q2 3 eps -> q3 eps
				"2 -1 30 3 -1", // q2 4 eps -> q3 eps
				"2 -1 31 3 -1", // q2 5 eps -> q3 eps
				"2 -1 32 3 -1", // q2 6 eps -> q3 eps
				"2 -1 33 3 -1", // q2 7 eps -> q3 eps
				"2 -1 34 3 -1", // q2 8 eps -> q3 eps
				"2 -1 35 3 -1", // q2 9 eps -> q3 eps
		};
		int start = 0;
		boolean[] accept = {false,false,false,false,true};
		dumbPDA Assignment2 = new dumbPDA(states, stringAlphabet,stackAlphabet,transition,start,accept);

		String input;
		Scanner userInput = new Scanner(System.in);
		System.out.println("Would you like to put in a string? y/n");
		
		while ( !"n".equals((input = userInput.next()))) {
			if ("y".equals(input)) {
				System.out.println("Which string would you like to put it over our alphabet of \""+stringAlphabet+"\"\"?");
				input = userInput.next();
				Assignment2.feed(input);
				System.out.println("\nWould you like to put in another?");
			} else {
				System.out.println("Please input y or n.");
			}
		}
		System.out.println("Thank you for your time.");
		userInput.close();
	}

}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class CNF {
	private ArrayList<String> vars;
	private ArrayList<Rule> rules;
	private ArrayList<ArrayList<Integer>[]> varToRuleIndices;
	private int varsSize, rulesSize;
	private boolean print = true;
//	private int varIndex,	//current tree depth
//		ruleIndex,	//rules satisfied at current tree node
//		varsSize,rulesSize;
//	private boolean[] varTree;	//tree to recurse on
	
	
	public CNF() {
		//set up to store information first
		this.vars = new ArrayList<String>();
		this.rules = new ArrayList<Rule>();
		this.varToRuleIndices = new ArrayList<ArrayList<Integer>[]>();
		this.varsSize = 0;
	}

	public String getVar(int i) {
		return this.vars.get(i);
	}

	public int getVarSize() {
		return this.varsSize;
	}

	public Rule getRule(int i) {
		return this.rules.get(i);
	}

	public int getRulesSize() {
		return this.rulesSize;
	}

	//MIGHT consider returning copy instead.
	public ArrayList<Integer> getVar2RInds(int varInd, int state) {
		return this.varToRuleIndices.get(varInd)[state];
	}

	public boolean isStillSatisfiable(int varIndex, int state, int ruleIndex) {
		Iterator<Integer> toCheck = this.getVar2RInds(varIndex, state).iterator();
		int ruleInd;
		Rule rule; //for printing
		StringBuilder output; //for printing

		while (toCheck.hasNext()) {
			ruleInd = toCheck.next();
			
			if (ruleInd >= ruleIndex) {
				if (!this.print) {

					if (getRule(ruleInd).isUnsatisfiable(varIndex)) {
						return false;
					}

				} else {

					rule = getRule(ruleInd);
					if (rule.isUnsatisfiable(varIndex)) {
						output = new StringBuilder(" UNSATISFIABLE\n  ");
						for (int j = 0; j < rule.getLength();j++){ 
							output.append(String.format("%b %s, ",rule.getBool(j), getVar(rule.getIndex(j))));
						}
						output.append(String.format("Max: %s", getVar(rule.getMax())));
						System.out.println(output.toString());
						return false;
					}

				}
			}

		}

		return true;
	}

	private void swap(int ix, int state, int iy) {
		if (this.print) {
			System.out.printf("    SWAPPING %d %d, state %d\n", ix, iy, state);
		}
		ArrayList<Integer> V2RIEntry;
		
		Rule rx = this.getRule(ix), ry = this.getRule(iy); 
		this.rules.set(ix, ry);
		this.rules.set(iy, rx);
		
		//update varToRuleIndices
		// use rule to get arrays
		for (int i = 0; i < rx.getLength(); i++){
			//use arrays to get all other varToRuleIndices entries
			V2RIEntry = this.getVar2RInds( rx.getIndex(i), boolToInt( rx.getBool(i) ) );
			V2RIEntry.remove( (Integer) ix);
			V2RIEntry.add( (Integer) iy);
		}
		for (int i = 0; i < ry.getLength(); i++){
			V2RIEntry = this.getVar2RInds( ry.getIndex(i), boolToInt( ry.getBool(i) ) );
			V2RIEntry.remove( (Integer) iy);
			V2RIEntry.add( (Integer) ix);
		}
		
	}

	private static int boolToInt(boolean bool) {
		return bool? 1: 0;
	}

	public void addConstant(String constant, boolean val) {
		//keep track of its value
		if ( val )
			this.addRule(constant);
		else
			this.addRule("!" + constant);
	}

	/**
	 * 
	 * @param rule
	 */
	public void addRule(String rule){
		String[] ruleStrArray = rule.split(" ");					//throw away, only to be used in function
		int[] ruleIndices = new int[ruleStrArray.length];			//stores which vars we refer to
		boolean[] ruleBoolArray = new boolean[ruleStrArray.length];	//stores the bools required for satisfiability
		int ruleIndex = this.rules.size(), maxIndex = -1;

		for (int i = 0; i < ruleStrArray.length; i++) {
			/*
			 * ruleBoolArray
			 * Get bool element and remove ! if necessary for proper var name referral
			 */
			if ( ruleStrArray[i].startsWith("!")) {
				ruleStrArray[i] = ruleStrArray[i].substring(1);
				ruleBoolArray[i] = false;
			} else {
				ruleBoolArray[i] = true;
			}
	
			/* 
			 * ruleIndices
			 * get index of name and add to variable list if necessary
			 * also initialize the varToRuleIndices entry
			 * 
			 */
			if (this.vars.contains(ruleStrArray[i])){
				ruleIndices[i] = this.vars.indexOf(ruleStrArray[i]);
			} else {
				//Only code that adds to this.vars
				ruleIndices[i] = this.vars.size();
				this.vars.add(ruleStrArray[i]);	//add to vars
				
				@SuppressWarnings("unchecked")
				ArrayList<Integer>[] newVTRIEntry = new ArrayList[2]; 
				newVTRIEntry[0] = new ArrayList<Integer>(); 
				newVTRIEntry[1] = new ArrayList<Integer>(); 
				this.varToRuleIndices.add(newVTRIEntry);  //add new varToRuleIndices entry
				
				this.varsSize++;
			}
	
			/*
			 * maxIndex
			 */
			if (maxIndex < ruleIndices[i]) {
				maxIndex = ruleIndices[i];
			}
			
			//associate this var to the new Rule
			this.getVar2RInds(ruleIndices[i], boolToInt(ruleBoolArray[i])).add(ruleIndex);
		}
		
		this.rules.add(new Rule(ruleIndices,ruleBoolArray, maxIndex));
		this.rulesSize++;
	}

	public int update(int varIndex, int state, int satisfied) {
		/*
		 * get VarToRuleIndices from varInd + state
		 * for each rule,
		 *  if rule not yet marked as true
		 *   switch with this.rules[satisfied]
		 *   
		 *   //update references
		 *   
		 *   
		 *   satisfied++
		 */
//		System.out.printf(" Updating:");
//		System.out.printf(" varIndex: %d, state: %d, satisfied: %d\n",varIndex, state, satisfied);
		
		ArrayList<Integer> toBeCopied = this.getVar2RInds(varIndex,state);
		
		Integer[] RuleIndices = new Integer[toBeCopied.size()];
		Arrays.sort(toBeCopied.toArray(RuleIndices));
		
		
		for (Integer ruleInd: RuleIndices) {//this.getVar2RInds(varIndex,state)) {
			if (this.print) { 
				System.out.printf("  Rule %d ", ruleInd);
			}
			if (ruleInd < satisfied) {
				if (this.print){ 
					System.out.printf("already satisfied\n");
				}
				continue;
			} else if (ruleInd > satisfied) {
				if (this.print){ 
					System.out.printf("needs swap w/ Rule %d\n", satisfied);
				}
				this.swap(ruleInd, state, satisfied);
//				System.out.println("Swapped");
			} else {
				if (this.print){ 
					System.out.printf("is next in line\n");
				}
			}
			
			satisfied++;
		}
		
		return satisfied;
	}

	/**
	 * Prints the variables, rules, and varToRuleIndices
	 */
	public void printInfo(){
		StringBuilder output = new StringBuilder();
		
		//VARIABLES
		output.append(String.format("%d Variables: ", getVarSize()));
		for (String var: this.vars){
			output.append(String.format("%s, ",var));
		}
		output.append("\n\n");
		
		//RULES
		output.append(String.format("%d RULES\n", getRulesSize()));
		Rule rule;
		for (int i = 0; i < this.rulesSize;i++) {
			output.append(String.format("Rule %d: ", i));
			
			rule = getRule(i);
			for (int j = 0; j < rule.getLength();j++){ 
				output.append(String.format("%b %s, ",rule.getBool(j), getVar(rule.getIndex(j))));
			}
			output.append(String.format("\n"));//"Max: %s\n", getVar(rule.getMax())));
		}
		output.append("\n");
		
		//VARTORULEINDICES
		output.append("VAR TO RULE INDICES\n");
		ArrayList<Integer> indices;
		for (int i = 0; i < this.vars.size();i ++){
			
			for (int k = 0; k < 2; k++) { 
				output.append(this.vars.get(i) + " " + Integer.toString(k) + ": ");
				
				//indices = this.varToRuleIndices.get(i)[k];
				indices = getVar2RInds(i,k);
				for (int j = 0; j < indices.size(); j++){
					output.append(String.format("%d ",indices.get(j)));
				}
				output.append("\n");
			}
		}
		
		System.out.println(output);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CNF boo = new CNF();
		CNFSolver foo;
		boolean[] coo;
		
		boo.addRule("a");
		boo.addRule("b !c");
		boo.addRule("b a");
		boo.addRule("d");
		boo.addRule("b c");
		
//		boo.printInfo();
		foo = new CNFSolver(boo);
		coo = foo.solve();
		
		
		System.out.printf("SOLUTION: ");
		for (int i = 0; i < coo.length;i++){
			if (coo[i]){
				System.out.printf("%s ", boo.getVar(i));
			}
		}
		System.out.printf("\n");
	}

}

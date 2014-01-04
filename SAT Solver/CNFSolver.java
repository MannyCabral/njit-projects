
public class CNFSolver {
	private CNF cnf;
	private boolean[] varTree;
	private int varIndex,	//current tree depth
		ruleIndex,	//rules satisfied at current tree node
		varsSize,rulesSize;
	private boolean print = true;

	
	public CNFSolver(CNF cnf) {
		this.cnf = cnf;
	}
	
	private int not(int i) {
		return (i+1) %2;
	}

	private static boolean intToBool(int i) {
		return (i != 0);
	}

	private boolean advance() {
		//reached a leaf
		if (this.varIndex >= this.varsSize) {
			return (this.ruleIndex == this.rulesSize);
		}

		/*
		 * otherwise, at a node
		 * Check for 0, then for 1
		 */
		for (int i = 0; i < 2; i ++){
			if (this.print) {
				System.out.println("\n-------------------------------\n");
				this.cnf.printInfo();
				System.out.println("\n-------------------------------\n");
				System.out.printf("ADVANCING: %s w/ %d\n", this.cnf.getVar(this.varIndex),i);
			}

			if (cnf.isStillSatisfiable(this.varIndex, this.not(i), this.ruleIndex)){

				int oldRuleIndex = this.ruleIndex;
				this.ruleIndex = cnf.update(this.varIndex, i, this.ruleIndex);
				this.varTree[this.varIndex++] = CNFSolver.intToBool(i);
				
				if (this.print) {
					System.out.printf("++#Satisfied %d -> %d\n",oldRuleIndex,this.ruleIndex);
				}
				
				if (this.advance()){
					return true;
				} else {
					if (this.print) {
						System.out.printf("--#Satisfied %d -> %d\n",this.ruleIndex,oldRuleIndex);
					}
					this.ruleIndex = oldRuleIndex;
					this.varIndex--;
				}
			} //else {
//				if (this.print) { 
//					System.out.printf(" UNSATISFIABLE\n");
//				}
//			}
		}

		return false;
	}

	public boolean[] solve() {
		/*
		 * initialize boolean array tree
		 * set the constants to their values
		 * determine which rules are already solved
		 */
		this.varIndex = 0; 
		this.ruleIndex = 0;
		this.varsSize = cnf.getVarSize();
		this.rulesSize = cnf.getRulesSize();
		this.varTree = new boolean[cnf.getVarSize()];
		
		//recursively check tree
		if (advance()) {
			System.out.println("SOLUTION FOUND.");
		} else {
			System.out.println("NO SOLUTION.");
		}
		
		return this.varTree;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i = 0;
		System.out.printf("%d\n",i++);
	}

}


public class Sudoku {
	CNF cnf;
	CNFSolver solver;
	boolean[] results;

	public Sudoku() {
		this.cnf = new CNF();
		
		//ROWS - at least one of each num in each row
		// num -> row
		for (int i = 1;i <=9;i++ ){
			for (int j = 1; j <= 9; j++ ){
				this.cnf.addRule(String.format("%1$d%2$d1 %1$d%2$d2 %1$d%2$d3 %1$d%2$d4 %1$d%2$d5 %1$d%2$d6 %1$d%2$d7 %1$d%2$d8 %1$d%2$d9", i,j));
			}
		}
		
		//COLUMNS - at least one of each num in each col
		// num -> col
		for (int i = 1; i <= 9; i++) {
			for (int j = 1; j <= 9; j++ ){
				this.cnf.addRule(String.format("%1$d1%2$d %1$d2%2$d %1$d3%2$d %1$d4%2$d %1$d5%2$d %1$d6%2$d %1$d7%2$d %1$d8%2$d %1$d9%2$d", i,j));
			}
		}
		
		//BLOCKS - at least one of each num in each block
		//
		String[] placeholder = new String[9];
		for (int rowBlock = 0; rowBlock < 3; rowBlock ++){
			for (int colBlock = 0; colBlock < 3; colBlock ++){
				//get number
				for (int n = 1; n <= 9; n++){
					//now we just need a 3 by 3 block
					// generate the 9 variables, then format to string
					for (int i = 1; i < 4; i ++){
						for (int j = 1; j < 4; j++) {
							placeholder[3*(i-1)+j-1] = String.format("%d%d%d",n,i+3*rowBlock,j+3*colBlock);
						}
					}
					this.cnf.addRule(String.format("%s %s %s %s %s %s %s %s %s", (Object[]) placeholder));
				}
			}
		}
		
		//CELLS - at most one num each cell
//		int c = 0;
		for (int i = 1; i <= 9; i++){
			for (int j = 1; j <= 9; j++){
				//have cell, now choose 2 numbers
				for (int n1 = 1; n1 < 9; n1 ++) {
					for (int n2=n1+1; n2 <= 9; n2++) {
//						System.out.println(String.format("%5$d: !%d%3$d%4$d|!%d%3$d%4$d",n1,n2,i,j,++c));
						this.cnf.addRule(String.format("!%d%3$d%4$d !%d%3$d%4$d",n1,n2,i,j));
					}
				}
			}
		}
		
	}


	public void setCell(int n, int i, int j) {
		this.cnf.addConstant(String.format("%d%d%d", n,i,j), true);
	}
	
	public void solve() {
		this.solver = new CNFSolver(this.cnf);
		this.results = this.solver.solve();
		
		System.out.printf("SOLUTION: ");
		for (int i = 0; i < this.results.length;i++){
			if (this.results[i]){
				System.out.printf("%s ", this.cnf.getVar(i));
			}
		}
		System.out.printf("\n");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sudoku foo = new Sudoku();
		foo.setCell(5,1,1);
		foo.setCell(2,1,3);
		foo.setCell(8,1,5);
		foo.setCell(9,1,6);
		foo.setCell(4,1,7);
		foo.setCell(4,2,4);
		foo.setCell(1,2,8);
		foo.setCell(5,2,9);
		foo.setCell(6,3,1);
		foo.setCell(3,3,4);
		foo.setCell(9,3,7);
		foo.setCell(8,4,2);
		foo.setCell(4,4,3);
		foo.setCell(6,4,6);
		foo.setCell(1,5,5);
		foo.setCell(9,6,4);
		foo.setCell(8,6,7);
		foo.setCell(6,6,8);
		foo.setCell(9,7,3);
		foo.setCell(8,7,6);
		foo.setCell(3,7,9);
		foo.setCell(4,8,1);
		foo.setCell(2,8,2);
		foo.setCell(3,8,6);
		foo.setCell(8,9,3);
		foo.setCell(2,9,4);
		foo.setCell(6,9,5);
		foo.setCell(1,9,7);
		foo.setCell(4,9,9);
//		foo.cnf.printInfo();
		foo.solve();
		
		
	}

}

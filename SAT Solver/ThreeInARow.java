
public class ThreeInARow {
	private CNF cnf;
	private CNFSolver solver;
	private boolean[] results;
	private int len;
	
	public ThreeInARow(int x) throws Exception {
		if ( (x % 2) == 1 || x < 4) {
			throw new Exception("Not a multiple of 2 greater than 4");
		}
		
		this.cnf = new CNF();
		this.len = x;
		
		//3 in a row
		for (int i = 0; i < x; i++){
			for (int j = 0; j < x-2; j++){
				//ROWS
//				String.format("%1$d%2$d %1$d%3$d %1$d%4$d",i,j,j+1,j+2);
				this.cnf.addRule(String.format("%1$d,%2$d %1$d,%3$d %1$d,%4$d",i,j,j+1,j+2));
				
				//COLUMNS
//				String.format("%2$d%1$d %3$d%1$d %4$d%1$d",i,j,j+1,j+2);
				this.cnf.addRule(String.format("%2$d,%1$d %3$d,%1$d %4$d,%1$d",i,j,j+1,j+2));
			}
		}

		/*
		 * Equal numbers
		 * any set of more than x/2 cannot be the same
		 */
		StringBuilder s;
		
		int[] half = new int[x/2+1];
		for (int i = 0; i < half.length; i++){
			half[i] = i;
		}
		half[half.length-1] = half[half.length-2];
		
		while (nextComb(half)) {
			for (int i = 0; i < x; i++){
				//ROWS true
				s= new StringBuilder();
				for (int l = 0; l < half.length; l++){
					s.append(String.format("%d,%d ",i,half[l]));
				}
				s.deleteCharAt(s.length()-1);
//				System.out.println(s.toString());
				this.cnf.addRule(s.toString());
				
				
				//ROWS false
				s= new StringBuilder();
				for (int l = 0; l < half.length; l++){
					s.append(String.format("!%d,%d ",i,half[l]));
				}
				s.deleteCharAt(s.length()-1);
//				System.out.println(s.toString());
				this.cnf.addRule(s.toString());
				
				
				//COLUMNS true
				s= new StringBuilder();
				for (int l = 0; l < half.length; l++){
					s.append(String.format("%d,%d ",half[l],i));
				}
				s.deleteCharAt(s.length()-1);
//				System.out.println(s.toString());
				this.cnf.addRule(s.toString());
				
				
				//COLUMNS false
				s= new StringBuilder();
				for (int l = 0; l < half.length; l++){
					s.append(String.format("!%d,%d ",half[l],i));
				}
				s.deleteCharAt(s.length()-1);
//				System.out.println(s.toString());
				this.cnf.addRule(s.toString());
				
			}
		}
		
	}

	private static boolean nextComb(int[] permute){
		//find the leftmost int to upgrade
		int max = (permute.length-1)*2, 
				i = permute.length -1;
		
		while (permute[i] >= max/2+i-1) {
			i--;
			if (i < 0) {
				return false;
			}
		}
		
//		System.out.printf(" %d\n",i);
		
		permute[i++]++;
		while (i < permute.length){
			permute[i] = permute[i-1] + 1;
			i++;
		}
		
		return true;
	}
	
	public void setCell(boolean bool, int i, int j){
		this.cnf.addConstant(String.format("%d,%d", i,j), bool);
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

	public void print() {
		boolean[] ordered = new boolean[this.len*this.len];
		String[] SArray;
		int[] inds = new int[2];
		StringBuilder sb;
		int newline;
		
		for (int i = 0; i < this.results.length; i++){
			SArray =  this.cnf.getVar(i).split(",");
			
			for (int j = 0; j< 2; j++){
				inds[j] = Integer.parseInt(SArray[j]);
			}
			
			ordered[inds[0]*this.len+inds[1]] = 
					this.results[i];
		}
		
		sb = new StringBuilder();
		newline = 0;
		for (Boolean bool: ordered){
			if (newline >= this.len){
				sb.append('\n');
				newline = 0;
			}
			
			if (bool) {
				sb.append('X');
			} else {
				sb.append('O');
			}
			
			newline++;
		}
		
		System.out.println(sb.toString());
	}

	public static void main(String[] args) throws Exception {
		ThreeInARow test = new ThreeInARow(8);
		
//		test.setCell(true, 0, 0);
//		test.setCell(false, 0, 1);
		test.solve();
		test.print();
	}
}

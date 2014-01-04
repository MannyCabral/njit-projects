
public class Rule {
	final private int[] indices;
	final private boolean[] bools;
	final private int length, max;
	

	public Rule(int[] indices, boolean[] bools, int max) {
		//NEED to sort
		this.indices = indices;
		this.bools = bools;
		this.length = indices.length;
		this.max = max;
	}

	public boolean getBool(int i) {
		return this.bools[i];
	}
	
	public int getLength(){
		return this.length;
	}
	
	public int getIndex(int i) {
		return this.indices[i];
	}
	
	public int getMax(){
		return this.max;
	}
	
	public boolean isUnsatisfiable(int varInd) {
//		System.out.printf("varInd: %d, rule.max: %d,  ", varInd, this.max);
//		for (int i: indices) {
//			System.out.printf("%d ", i);
//		}
//		System.out.println();
		return (varInd >= this.max);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

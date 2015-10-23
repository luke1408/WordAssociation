package at.shark.association;

public class AssociationPointer {
	String blob1;
	String blob2;
	private int cardinality;
	
	public AssociationPointer(String blob1, String blob2){
		this.blob1 = blob1;
		this.blob2 = blob2;
		cardinality = 1;
	}
	
	public void raise(){
		cardinality++;
	}
	
	public int getCardinality(){
		return cardinality;
	}
}

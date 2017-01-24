package part;

import java.util.HashSet;
import java.util.Set;

public class Part {
	private Set<Part> childParts;
	private Part parent;
	private int quantity;
	private String name,partNumber,title,material,type,item;
	
	public Part(String name,Part parent, int quantity) {
		this.parent = parent;
		this.quantity = quantity;
		this.name = name;
		childParts=new HashSet<Part>();
	}
	
	public Part(String name){
		this.name = name;
		childParts=new HashSet<Part>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Part> getChild() {
		return childParts;
	}

	public void setChild(Set<Part> child) {
		this.childParts = child;
	}
	
	public void addChild(Part newChild){
		this.childParts.add(newChild);
	}
	
	public Boolean hasChild(Part child){
		return this.childParts.contains(child);
	}
	
	public Part getParent() {
		return parent;
	}

	public void setParent(Part parent) {
		this.parent = parent;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

}

package siahu.iso14496.type;

import java.util.ArrayList;

public class ContainerBox extends Box {

	protected ArrayList<Box> children;
	
	public ContainerBox() {
		this.children = new ArrayList<Box>();
	}
	
	public void addBox(Box box) {
		children.add(box);
		box.setParent(this);
	}
}

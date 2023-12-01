package mvc;

import java.util.ArrayList;
import geometry.*;
import java.io.Serializable;

public class DrawingModel implements Serializable{
	private ArrayList<Shape> shapes = new ArrayList<Shape>();
	
	public ArrayList<Shape> getShapes() {
		return shapes;
	}
	
	public void addShape(int index, Shape newShape) {
		shapes.add(index, newShape);
	}
	
	public Boolean addShape(Shape newShape) {
		return shapes.add(newShape);
	}
	
	public Boolean removeShape(Shape shape) {
		return shapes.remove(shape);
	}

	public void add(Shape s) {
		shapes.add(s);
	}
	
	public void remove(Shape s) {
		shapes.remove(s);
	}

	public Shape get(int index) {
		return shapes.get(index);
	}
	
	
	public int getIndexOfShape(Shape shape) {
		return shapes.indexOf(shape);
	}
	
	public boolean isShapeSelected() {
		for(Shape s:shapes) {
			if (s.isSelected())
				return true;
		}
		
		return false;
	}
	
	public ArrayList<Shape> getSelectedShapes() {
		ArrayList<Shape> selectedShapes = new ArrayList<Shape>();
		
		for(Shape s:shapes) {
			if(s.isSelected())
				selectedShapes.add(s);
		}
		
		return selectedShapes;
	}
}

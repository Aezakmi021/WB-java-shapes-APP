package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import geometry.Circle;
import geometry.Shape;
import mvc.DrawingModel;

public class SelectShapeCommand implements Command{

	private Shape shape;
	private DrawingModel model;

	public SelectShapeCommand(Shape shape, DrawingModel model) {
		this.shape = shape;
		this.model = model;	
	}

	@Override
	public void execute() {
		int index=model.getIndexOfShape(shape);
		model.get(index).setSelected(true);
	}

	@Override
	public void unexecute() {	
		int index=model.getIndexOfShape(shape);
		model.get(index).setSelected(false);	
	}
	
	@Override
	public String toString() {
		return this.shape.getClass().getSimpleName() + " selected:" + this.shape + "\n";
	}
	 
	 
	 public void setModel(DrawingModel model) {
			this.model = model;
		}
	 
	 public void updateOldStateObjectReference() {
		 int index=model.getIndexOfShape(shape);
		 this.shape = model.get(index);
	 }
}
package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import geometry.Circle;
import geometry.Shape;
import mvc.DrawingModel;

public class RemoveShapeCommand implements Command {
	private Shape shape;
	private DrawingModel model;
	private int index;

	public RemoveShapeCommand(Shape shape, DrawingModel model) {
		this.shape = shape;
		this.model = model;
		this.index = model.getIndexOfShape(shape);
	}

	@Override
	public void execute() {
	    model.remove(shape);
	}

	@Override
	public void unexecute() {
	    model.addShape(index, shape);
	}
	
	 
	 @Override
		public String toString() {
		 return shape.getClass().getSimpleName() + " removed:" + shape.toString() + "\n";
		}
	 
	 public void setModel(DrawingModel model) {
			this.model = model;
	}
	 
	 public void updateOldStateObjectReference() {
		 int index = model.getIndexOfShape(shape);
		 if(index > -1) {
			 this.shape = model.get(index);
		 }
	 }
	 
	    public Shape getShape() {
	        return this.shape;
	    }
	 
	    public DrawingModel getModel() {
	        return this.model;
	    }
	 
	    public int getIndex() {
	        return this.index;
	    }
	 
}

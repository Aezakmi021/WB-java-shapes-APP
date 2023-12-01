package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import geometry.Circle;
import geometry.Shape;
import mvc.DrawingModel;

public class BringToBackCommand implements Command {
    private Shape shape;
    private DrawingModel model;
    private int initialIndex;

    public BringToBackCommand(Shape shape, DrawingModel model) {
        this.shape = shape;
        this.model = model;
        this.initialIndex = model.getIndexOfShape(shape);
    }

    @Override
    public void execute() {
        if (initialIndex > 0) {
            model.removeShape(shape);
            model.addShape(0, shape);
        }
    }

    @Override
    public void unexecute() {
        model.removeShape(shape);
        model.addShape(initialIndex, shape);
    }

    @Override
    public String toString() {
        return shape.getClass().getSimpleName() + " bring to Back: " + shape.toString() + "\n";
    }
    
    
    public void setModel(DrawingModel model) {
		this.model = model;
	}
    
    public void updateOldStateObjectReference() {
		 int index=model.getIndexOfShape(shape);
		 this.shape = model.get(index);
	 }
	
}

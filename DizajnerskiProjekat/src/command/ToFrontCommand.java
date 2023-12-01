package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import geometry.Shape;
import mvc.DrawingModel;

public class ToFrontCommand implements Command {
    private Shape shape;
    private ArrayList<Shape> shapes;
	private DrawingModel model;
    private int initialIndex;
    private boolean first;

    public ToFrontCommand(Shape shape, DrawingModel model) {
        this.shape = shape;
        this.shapes = model.getShapes();
        this.initialIndex = model.getIndexOfShape(shape);
        this.first = (initialIndex < shapes.size() - 1);
    }

    @Override
    public void execute() {
    		Collections.swap(shapes, initialIndex+1, initialIndex);	
			
    }

    @Override
    public void unexecute() {
    		Collections.swap(shapes, initialIndex, initialIndex+1);
    }

    @Override
    public String toString() {
        return shape.getClass().getSimpleName() + " to front: " + shape.toString()+ "\n";
    }
    
    
    public void setModel(DrawingModel model) {
		this.model = model;
	}
    
    public boolean getFirst()
    {
    	return first;
    }
    
}

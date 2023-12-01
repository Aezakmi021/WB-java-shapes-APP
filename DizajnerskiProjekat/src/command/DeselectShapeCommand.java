package command;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import geometry.Shape;
import mvc.DrawingModel;

public class DeselectShapeCommand implements Command{

	private Shape shape;
	private DrawingModel model;

	public DeselectShapeCommand(Shape shape, DrawingModel model) {
		this.shape = shape;
		this.model = model;	
	}

	@Override
	public void execute() {
		int index=model.getIndexOfShape(shape);
		model.get(index).setSelected(false);
	}

	@Override
	public void unexecute() {	
		
		int index=model.getIndexOfShape(shape);
		model.get(index).setSelected(true);
	}

	@Override
	public String toString() {
		return this.shape.getClass().getSimpleName() + " deselected:" + this.shape + "\n";
	}
	 
	 
	 public void setModel(DrawingModel model) {
			this.model = model;
		}
}

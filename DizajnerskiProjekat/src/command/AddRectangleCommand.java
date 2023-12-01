package command;

import mvc.DrawingModel;
import geometry.Rectangle;
import geometry.Shape;

public class AddRectangleCommand implements Command {
	
	private Rectangle rectangle;
	private DrawingModel model;

	public AddRectangleCommand(Rectangle rectangle, DrawingModel model) {
		this.rectangle = rectangle;
		this.model = model;
	}

	@Override
	public void execute() {
		model.add(rectangle);
	}

	@Override
	public void unexecute() {
		model.remove(rectangle);
	}
	
	@Override
	public String toString() {
		return "Rectangle added:" + this.rectangle + "\n";
	}

}
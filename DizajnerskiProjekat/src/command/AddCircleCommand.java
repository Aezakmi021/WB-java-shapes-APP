package command;

import geometry.Circle;
import geometry.Shape;
import mvc.DrawingModel;

public class AddCircleCommand implements Command {
	
	private Circle circle;
	private DrawingModel model;

	public AddCircleCommand(Circle circle, DrawingModel model) {
		this.circle = circle;
		this.model = model;
	}

	@Override
	public void execute() {
		model.add(circle);
	}

	@Override
	public void unexecute() {
		model.remove(circle);
	}
	
	@Override
	public String toString() {
		return "Circle added:" + this.circle + "\n";
	}

}
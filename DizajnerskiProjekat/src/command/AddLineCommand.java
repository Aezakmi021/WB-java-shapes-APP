package command;

import geometry.Line;
import geometry.Shape;
import mvc.DrawingModel;

public class AddLineCommand implements Command {
	
	private Line line;
	private DrawingModel model;

	public AddLineCommand(Line line, DrawingModel model) {
		this.line = line;
		this.model = model;
	}

	@Override
	public void execute() {
		model.add(line);
	}

	@Override
	public void unexecute() {
		model.remove(line);
	}
	
	@Override
	public String toString() {
		return "Line added:" + this.line + "\n";
	}

}
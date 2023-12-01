package command;

import geometry.Donut;
import geometry.Shape;
import mvc.DrawingModel;

public class AddDonutCommand implements Command {
	
	private Donut donut;
	private DrawingModel model;

	public AddDonutCommand(Donut donut, DrawingModel model) {
		this.donut = donut;
		this.model = model;
	}

	@Override
	public void execute() {
		model.add(donut);
	}

	@Override
	public void unexecute() {
		model.remove(donut);
	}
	
	@Override
	public String toString() {
		return "Donut added:" + this.donut + "\n";
	}

}
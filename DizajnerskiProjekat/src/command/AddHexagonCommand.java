package command;

import adapter.HexagonAdapter;
import geometry.Shape;
import mvc.DrawingModel;

public class AddHexagonCommand implements Command {
	
	private HexagonAdapter hexAdapter;
	private DrawingModel model;

	public AddHexagonCommand(HexagonAdapter hexAdapter, DrawingModel model) {
		this.hexAdapter = hexAdapter;
		this.model = model;
	}

	@Override
	public void execute() {
		model.add(hexAdapter);
	}

	@Override
	public void unexecute() {
		model.remove(hexAdapter);
	}
	
	@Override
	public String toString() {
		return "Hexagon added:" + this.hexAdapter + "\n";
	}

}
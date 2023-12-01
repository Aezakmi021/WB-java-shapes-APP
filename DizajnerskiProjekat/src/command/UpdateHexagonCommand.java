package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import adapter.HexagonAdapter;
import geometry.Circle;
import mvc.DrawingModel;

public class UpdateHexagonCommand implements Command{
	private HexagonAdapter oldState;
	private HexagonAdapter newState;
	private HexagonAdapter original = new HexagonAdapter();
	private DrawingModel model;

	
	public UpdateHexagonCommand(HexagonAdapter oldState, HexagonAdapter newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	@Override
	public void execute() {
		
		original=oldState.clone();
		
		oldState.getHexagon().setY(newState.getHexagon().getY());
		oldState.getHexagon().setX(newState.getHexagon().getX());
		oldState.getHexagon().setR(newState.getHexagon().getR());
		oldState.getHexagon().setBorderColor(newState.getHexagon().getBorderColor());
		oldState.getHexagon().setAreaColor(newState.getHexagon().getAreaColor());
	
	}

	@Override
	public void unexecute() {
		oldState.getHexagon().setY(original.getHexagon().getY());
		oldState.getHexagon().setX(original.getHexagon().getX());
		oldState.getHexagon().setR(original.getHexagon().getR());
		oldState.getHexagon().setBorderColor(original.getHexagon().getBorderColor());
		oldState.getHexagon().setAreaColor(original.getHexagon().getAreaColor());
			
	}

	@Override
	public String toString() {
		return "Hexagon updated:" + "{" + this.original.toString() + "} " + ">>> " + "{" + this.newState.toString() + "}" + "\n";
	}
	 
	
	 
	 public void setModel(DrawingModel model) {
			this.model = model;
		}
	 
	 public void updateOldStateObjectReference() {
		 int index=model.getIndexOfShape(oldState);
		 this.oldState = (HexagonAdapter) model.get(index);
	 }
}

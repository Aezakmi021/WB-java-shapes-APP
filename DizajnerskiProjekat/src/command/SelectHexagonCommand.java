package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import adapter.HexagonAdapter;

import mvc.DrawingModel;

public class SelectHexagonCommand implements Command{

	private HexagonAdapter hexagon;
	private DrawingModel model;

	public SelectHexagonCommand(HexagonAdapter hexagon, DrawingModel model) {
		this.hexagon = hexagon;
		this.model = model;	
	}

	@Override
	public void execute() {
		int index=model.getIndexOfShape(hexagon);
		model.get(index).setSelected(true);
	}

	@Override
	public void unexecute() {	
		int index=model.getIndexOfShape(hexagon);
		model.get(index).setSelected(false);	
	}
	
	@Override
	public String toString() {
		return this.hexagon.getClass().getSimpleName() + " selected:" + this.hexagon + "\n";
		
		
	}
	 
	
	 
	 public void updateOldStateObjectReference() {
		 int index=model.getIndexOfShape(hexagon);
		 this.hexagon = (HexagonAdapter) model.get(index);
	 }
}
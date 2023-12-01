package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import geometry.Circle;
import geometry.Donut;
import mvc.DrawingModel;

public class UpdateDonutCommand implements Command {

	private Donut oldState;
	private Donut newState;
	private Donut original = new Donut();
	private DrawingModel model;

	public UpdateDonutCommand(Donut oldState, Donut newState) {
		this.oldState = oldState;
		this.newState = newState;
		
	} 
	
	
	@Override
	public void execute() {
		
		original = oldState.clone();
		
		oldState.getCenter().setX(newState.getCenter().getX());
		oldState.getCenter().setY(newState.getCenter().getY());
		try {
			oldState.setRadius(newState.getRadius());
		} catch (Exception e) {
			e.printStackTrace();
		}
		oldState.setInnerRadius(newState.getInnerRadius());
		oldState.setColor(newState.getColor());
		oldState.setInnerColor(newState.getInnerColor());

	}

	@Override
	public void unexecute() {
		oldState.getCenter().setX(original.getCenter().getX());
		oldState.getCenter().setY(original.getCenter().getY());
		try {
			oldState.setRadius(original.getRadius());
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		oldState.setInnerRadius(original.getInnerRadius());
		oldState.setColor(original.getColor());
		oldState.setInnerColor(original.getInnerColor());

	}
	@Override
	public String toString() {
		return "Donut updated:" + "{" + this.original.toString() + "} " + ">>> " + "{" + this.newState.toString() + "}" + "\n";
	}
	 
	 
	 
	 public void setModel(DrawingModel model) {
			this.model = model;
		}
	 
	 public void updateOldStateObjectReference() {
		 int index=model.getIndexOfShape(oldState);
		 this.oldState = (Donut) model.get(index);
	 }

}

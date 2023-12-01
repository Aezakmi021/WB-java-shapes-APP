package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import geometry.Circle;
import mvc.DrawingModel;

public class UpdateCircleCommand implements Command {
	private Circle oldState;
	private Circle newState;
	private Circle original = new Circle();
	private DrawingModel model;
	
	public UpdateCircleCommand(Circle oldState, Circle newState) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oldState.setColor(original.getColor());
		oldState.setInnerColor(original.getInnerColor());
		

	}
	 
	 @Override
		public String toString() {
			return "Circle updated:" + "{" + this.original.toString() + "} " + ">>> " + "{" + this.newState.toString() + "}" + "\n";
		}
	 
	 
	 public void setModel(DrawingModel model) {
			this.model = model;
		}
	 
	 public void updateOldStateObjectReference() {
		 int index=model.getIndexOfShape(oldState);
		 this.oldState = (Circle) model.get(index);
	 }

}

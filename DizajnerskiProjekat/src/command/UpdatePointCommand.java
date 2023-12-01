package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import geometry.Circle;
import geometry.Point;
import mvc.DrawingModel;

public class UpdatePointCommand implements Command {

	private Point oldState;
	private Point newState;
	private Point original = new Point();
	private DrawingModel model;

	
	public UpdatePointCommand(Point oldState, Point newState) {
		this.oldState=oldState;
		this.newState=newState;
	}

	@Override
	public void execute() {
		original = oldState.clone();
		
		oldState.setX(newState.getX());
		oldState.setY(newState.getY());
		oldState.setColor(newState.getColor());
	}

	@Override
	public void unexecute() {
		oldState.setX(original.getX());
		oldState.setY(original.getY());
		oldState.setColor(original.getColor());

	}
	@Override
	public String toString() {
		return "Point updated:" + "{" + this.original.toString() + "} " + ">>> " + "{" + this.newState.toString() + "}" + "\n";
	}
	 
	 
	 public void setModel(DrawingModel model) {
			this.model = model;
		}
	 
	 public void updateOldStateObjectReference() {
		 int index=model.getIndexOfShape(oldState);
		 this.oldState = (Point) model.get(index);
	 }

}

package command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import geometry.Circle;
import geometry.Line;
import mvc.DrawingModel;

public class UpdateLineCommand implements Command {
	private Line oldState;
	private Line newState;
	private Line original = new Line();
	private DrawingModel model;

	public UpdateLineCommand(Line oldState, Line newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	@Override
	public void execute() {
		original = oldState.clone();
		
		oldState.getStartPoint().setX(newState.getStartPoint().getX());
		oldState.getStartPoint().setY(newState.getStartPoint().getY());
		oldState.getEndPoint().setX(newState.getEndPoint().getX());
		oldState.getEndPoint().setY(newState.getEndPoint().getY());
		oldState.setColor(newState.getColor());

	}

	@Override
	public void unexecute() {
		oldState.getStartPoint().setX(original.getStartPoint().getX());
		oldState.getStartPoint().setY(original.getStartPoint().getY());
		oldState.getEndPoint().setX(original.getEndPoint().getX());
		oldState.getEndPoint().setY(original.getEndPoint().getY());
		oldState.setColor(original.getColor());

	}
	@Override
	public String toString() {
		return "Line updated:" + "{" + this.original.toString() + "} " + ">>> " + "{" + this.newState.toString() + "}" + "\n";
	}
	
	 
	 public void setModel(DrawingModel model) {
			this.model = model;
		}
	 
	 public void updateOldStateObjectReference() {
		 int index=model.getIndexOfShape(oldState);
		 this.oldState = (Line) model.get(index);
	 }


}
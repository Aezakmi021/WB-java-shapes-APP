package observer;
import mvc.DrawingFrame;

import java.util.ArrayList;
import java.util.Stack;

import command.Command;


public class RedoUndoObserver{
	
	DrawingFrame frame;

	public RedoUndoObserver() {}
	
	public RedoUndoObserver(DrawingFrame frame) {
		this.frame = frame;
	}


	public void onChange(Stack<Command> undoCommandsStack,Stack<Command> redoCommandsStack) {
		if (undoCommandsStack.size() > 0)
			enableUndo();
		else {
			disableUndo();
		}
		
		if (redoCommandsStack.size() > 0)
			enableRedo();
		else {
			disableRedo();
		}
		
	}
	
	public void disable() {
		frame.getBtnRedo().setEnabled(false);
		frame.getBtnUndo().setEnabled(false);
	}
	

	public void enableUndo() {
		frame.getBtnUndo().setEnabled(true);
	}

	public void enableRedo() {
		frame.getBtnRedo().setEnabled(true);
	}
	
	public void disableUndo() {
		frame.getBtnUndo().setEnabled(false);
	}

	public void disableRedo() {
		frame.getBtnRedo().setEnabled(false);
	}


}

package observer;
import mvc.DrawingFrame;

public class ShapeObserver{
	
	DrawingFrame frame;

	public ShapeObserver() {}
	
	public ShapeObserver(DrawingFrame frame) {
		this.frame = frame;
	}


	public void onChange(int numberOfShapesSelected) {
		if (numberOfShapesSelected == 0)
			disable();
		else if (numberOfShapesSelected > 0)
			enable();
			if(numberOfShapesSelected > 1)
			{
				disableModify();
			}
	}
	
	public void disable() {
		frame.getBtnDelete().setEnabled(false);
		frame.getBtnFront().setEnabled(false);
		frame.getBtnBack().setEnabled(false);
		frame.getBtnBringFront().setEnabled(false);
		frame.getBtnBringBack().setEnabled(false);
		frame.getBtnModify().setEnabled(false);
	}
	
	public void enable() {
		frame.getBtnFront().setEnabled(true);
		frame.getBtnBack().setEnabled(true);
		frame.getBtnBringFront().setEnabled(true);
		frame.getBtnBringBack().setEnabled(true);
		frame.getBtnDelete().setEnabled(true);
		frame.getBtnModify().setEnabled(true);
	}
	

	public void enableDelete() {
		frame.getBtnDelete().setEnabled(true);
	}
	
	public void disableModify() {
		frame.getBtnModify().setEnabled(false);
	}


}

package mvc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import adapter.HexagonAdapter;
import command.AddCircleCommand;
import command.AddDonutCommand;
import command.AddHexagonCommand;
import command.AddLineCommand;
import command.AddPointCommand;
import command.AddRectangleCommand;
import command.BringToBackCommand;
import command.BringToFrontCommand;
import command.Command;
import command.DeselectShapeCommand;
import command.RemoveShapeCommand;
import command.SelectHexagonCommand;
import command.SelectShapeCommand;
import command.ToBackCommand;
import command.ToFrontCommand;
import command.UpdateCircleCommand;
import command.UpdateDonutCommand;
import command.UpdateHexagonCommand;
import command.UpdateLineCommand;
import command.UpdatePointCommand;
import command.UpdateRectangleCommand;
import dialogs.DlgCircle;
import dialogs.DlgDonut;
import dialogs.DlgHexagon;
import dialogs.DlgLine;
import dialogs.DlgPoint;
import dialogs.DlgRectangle;
import geometry.Circle;
import geometry.Donut;
import geometry.Line;
import geometry.Point;
import geometry.Rectangle;
import geometry.Shape;
import geometry.SurfaceShape;
import hexagon.Hexagon;
import observer.RedoUndoObserver;
import observer.ShapeObserver;
import strategy.DrawingSave;
import strategy.LogSave;
import strategy.Save;
import strategy.SavingManager;

public class DrawingController implements Serializable{
	
	private DrawingModel model;
	private DrawingFrame frame;
	Point startPoint; //pomocna promenljiva za iscrtavanje linije
	private RedoUndoObserver redoUndoObserver;
	private ShapeObserver shapeObserver;
	
	private int logCounter = 0;
	private boolean print = true;
	private boolean backFrontLog = true;
	
	private Stack<Command> undoCommandsStack = new Stack<Command>(); 
	private Stack<Command> redoCommandsStack = new Stack<Command>();
	private Stack<Command> logItems = new Stack<Command>();
	
	
	private ArrayList<String> log = new ArrayList<String>(); 
	//lista izvrsenih komandi koje se nalaze u log-u
	
	public DrawingController(DrawingModel model, DrawingFrame frame) {
		this.model = model;
		this.frame = frame;
		this.shapeObserver = new ShapeObserver(this.frame);
		this.redoUndoObserver = new RedoUndoObserver(this.frame);
	}
	

	public void mouseClicked(MouseEvent e) {
		if (frame.getBtnSelection().isSelected())
			this.selection(frame.getView().getGraphics(), e.getX(), e.getY(), print); 
		else 
			this.paint(frame.getView().getGraphics(), e.getX(), e.getY());
	}
	
	public void paint(Graphics g, int x, int y) {
		if (frame.getBtnPoint().isSelected())
		{	
			Point p = new Point(x, y, false, frame.getColor());
			AddPointCommand apc = new AddPointCommand(p, model);
			undoCommandsStack.push(apc);
			redoCommandsStack.clear();
			redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
			apc.execute();
			frame.repaint();
			log(apc.toString());
			
		} else if (frame.getBtnLine().isSelected()) {
			if (startPoint == null)
			{
				startPoint = new Point(x, y);
			}
			else
			{
				Point eP = new Point(x, y);
				Line l = new Line(startPoint, eP, false, frame.getColor());
				AddLineCommand alc = new AddLineCommand(l, model);
				undoCommandsStack.push(alc);
				redoCommandsStack.clear();
				redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
				alc.execute();
				startPoint = null;
				frame.repaint();
				log(alc.toString());
			}

		} else if (frame.getBtnRectangle().isSelected()) {
			DlgRectangle dlgR = new DlgRectangle();			
			dlgR.getTxtX().setText(String.valueOf(x));
			dlgR.getTxtY().setText(String.valueOf(y));
			dlgR.getTxtX().setEditable(false);
			dlgR.getTxtY().setEditable(false);
			dlgR.getBtnColor().setBackground(this.frame.getColor()); //setovanje boje dugmeta na osnovu aktivne boje
			dlgR.getBtnInnerColor().setBackground(this.frame.getInnerColor()); //setovanje boje dugmeta na osnovu aktivne boje
			dlgR.setVisible(true);
			if (dlgR.isOk()) {
				try {
					String width = dlgR.getTxtWidth().getText().toString();
					int intWidth = Integer.parseInt(width);
					String height = dlgR.getTxtHeight().getText().toString();
					int intHeight = Integer.parseInt(height);	
					Color c = dlgR.getBtnColor().getBackground(); //setovanje boje na osnovu boje dugmeta iz dijaloga
					Color innerC = dlgR.getBtnInnerColor().getBackground(); //setovanje boje na osnovu boje dugmeta iz dijaloga
					Rectangle r = new Rectangle(new Point(x, y), intWidth, intHeight, false, c, innerC);
					AddRectangleCommand arc = new AddRectangleCommand(r, model);
					undoCommandsStack.push(arc);
					redoCommandsStack.clear();
					redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
					arc.execute();
					frame.repaint();
					log(arc.toString());
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(null, "You need to enter numbers!");
				}
						
			}	
			
		} else if (frame.getBtnCircle().isSelected()) {
			DlgCircle dlgC = new DlgCircle();
			dlgC.getTxtXc().setText(String.valueOf(x));
			dlgC.getTxtYc().setText(String.valueOf(y));
			dlgC.getTxtXc().setEditable(false);
			dlgC.getTxtYc().setEditable(false);
			dlgC.getBtnColor().setBackground(this.frame.getColor());
			dlgC.getBtnInnerColor().setBackground(this.frame.getInnerColor());
			dlgC.setVisible(true);
			
			if (dlgC.isOk()) {
				String radius = dlgC.getTxtR().getText().toString();
				int intRadius = Integer.parseInt(radius);	
				Color c = dlgC.getBtnColor().getBackground();
				Color innerC = dlgC.getBtnInnerColor().getBackground();
				Circle circle = new Circle(new Point(x, y), intRadius, false, c, innerC);
				AddCircleCommand acc = new AddCircleCommand(circle, model);
				undoCommandsStack.push(acc);
				redoCommandsStack.clear();
				redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
				acc.execute();
				frame.repaint();
				log(acc.toString());
			}
		} else if (frame.getBtnDonut().isSelected()) {
			DlgDonut dlgD = new DlgDonut();
			dlgD.getTxtXc().setText(String.valueOf(x));
			dlgD.getTxtYc().setText(String.valueOf(y));
			dlgD.getTxtXc().setEditable(false);
			dlgD.getTxtYc().setEditable(false);
			dlgD.getBtnColor().setBackground(this.frame.getColor());
			dlgD.getBtnInnerColor().setBackground(this.frame.getInnerColor());
			dlgD.setVisible(true);
			
			if (dlgD.isOk()) {
				Color c = dlgD.getBtnColor().getBackground();
				Color innerC = dlgD.getBtnInnerColor().getBackground();
				Donut d = new Donut(new Point(x, y), Integer.parseInt(dlgD.getTxtR().getText().toString()), Integer.parseInt(dlgD.getTxtIR().getText().toString()), false, c, innerC);
				AddDonutCommand adc = new AddDonutCommand(d, model);
				undoCommandsStack.push(adc);
				redoCommandsStack.clear();
				redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
				adc.execute();
				frame.repaint();
				log(adc.toString());
			}
		} else if (frame.getBtnSelection().isSelected())
			this.selection(frame.getView().getGraphics(), x, y, print);
		else if (frame.getBtnHexagon().isSelected()) {
			DlgHexagon dlgH = new DlgHexagon();
			dlgH.getTxtX().setText(String.valueOf(x));
			dlgH.getTxtY().setText(String.valueOf(y));
			dlgH.getTxtX().setEditable(false);
			dlgH.getTxtY().setEditable(false);
			dlgH.getBtnColor().setBackground(this.frame.getColor());
			dlgH.getBtnInnerColor().setBackground(this.frame.getInnerColor());
			dlgH.setVisible(true);
			
			if (dlgH.isOk()) {
				Color c = dlgH.getBtnColor().getBackground();
				Color innerC = dlgH.getBtnInnerColor().getBackground();
				HexagonAdapter hexAdapter = new HexagonAdapter(new Point(Integer.parseInt(dlgH.getTxtX().getText().toString()), Integer.parseInt(dlgH.getTxtY().getText().toString())),
						Integer.parseInt(dlgH.getTxtR().getText().toString()), false, c, innerC);
				AddHexagonCommand ahc = new AddHexagonCommand(hexAdapter, model);
				undoCommandsStack.push(ahc);
				redoCommandsStack.clear();
				redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
				ahc.execute();
				frame.repaint();
				log(ahc.toString());
			}
		}
		
	}
	
	public void selection(Graphics g, int x, int y, boolean print) {
		for (int i = model.getShapes().size() - 1; i >= 0; i--) {
			if (model.get(i).contains(x, y)) {
				if (model.get(i).isSelected()) {
					DeselectShapeCommand deselectShapeCmd = new DeselectShapeCommand(model.get(i), model);
					deselectShapeCmd.execute();
					if(print == true)
					{
						log("Click:(" + x + "," + y + ") " + deselectShapeCmd.toString());
					}	
					undoCommandsStack.push(deselectShapeCmd);
					redoCommandsStack.clear();
				} else {
					if(model.get(i) instanceof HexagonAdapter) {
						SelectHexagonCommand selectShapeCmd = new SelectHexagonCommand((HexagonAdapter) model.get(i), model);
						selectShapeCmd.execute();
						if(print == true)
						{
							log("Click:(" + x + "," + y + ") " + selectShapeCmd.toString());
						}
						undoCommandsStack.push(selectShapeCmd);
						redoCommandsStack.clear();
					} else {
						SelectShapeCommand selectShapeCmd = new SelectShapeCommand(model.get(i), model);
						selectShapeCmd.execute();
						if(print == true)
						{
							log("Click:(" + x + "," + y + ") " + selectShapeCmd.toString());
						}				
						undoCommandsStack.push(selectShapeCmd);
						redoCommandsStack.clear();
					}
				}

				frame.repaint();
				shapeObserver.onChange(model.getSelectedShapes().size());
				redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
				return;
			}

		}
	}
	
	
	public void log(String command) {
	        
	       
		log.add(command);
		this.frame.getTextArea().append(command);
		
	}
	
	
	public void modify() {		
		if (model.getShapes().isEmpty())
			JOptionPane.showMessageDialog(null, "You need to add a shape first!");
		else
		{
			for (int i = 0; i < model.getShapes().size(); i++) { 		      
				if (model.getShapes().get(i).isSelected() && model.getShapes().get(i) instanceof Circle && (model.getShapes().get(i) instanceof Donut == false))
				{
					DlgCircle dlg = new DlgCircle();
					Circle temp = (Circle) model.getShapes().get(i);		
					dlg.getTxtXc().setText(String.valueOf(temp.getCenter().getX()));
					dlg.getTxtYc().setText(String.valueOf(temp.getCenter().getY()));
					dlg.getTxtR().setText(String.valueOf(temp.getRadius()));
					dlg.getBtnColor().setBackground(temp.getColor());
					dlg.getBtnInnerColor().setBackground(temp.getInnerColor());
					dlg.setTitle("Modify circle");
					dlg.setVisible(true);
					if (dlg.isOk()) {
					Circle c = new Circle(new Point(Integer.parseInt(dlg.getTxtXc().getText().toString()), Integer.parseInt(dlg.getTxtYc().getText().toString())), 
							Integer.parseInt(dlg.getTxtR().getText().toString()), 
							false, dlg.getBtnColor().getBackground(), dlg.getBtnInnerColor().getBackground());
					UpdateCircleCommand ucc = new UpdateCircleCommand(temp, c);
					undoCommandsStack.push(ucc);
					redoCommandsStack.clear();
					redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
					ucc.execute();
					log(ucc.toString());
					
				}
			} else if (model.getShapes().get(i).isSelected() && model.getShapes().get(i) instanceof Donut) {				
					DlgDonut dlg = new DlgDonut();
					Donut temp = (Donut) model.getShapes().get(i);		
					dlg.getTxtXc().setText(String.valueOf(temp.getCenter().getX()));
					dlg.getTxtYc().setText(String.valueOf(temp.getCenter().getY()));
					dlg.getTxtR().setText(String.valueOf(temp.getRadius()));
					dlg.getTxtIR().setText(String.valueOf(temp.getInnerRadius()));
					dlg.getBtnColor().setBackground(temp.getColor());
					dlg.getBtnInnerColor().setBackground(temp.getInnerColor());
					dlg.setTitle("Modify donut");
					dlg.setVisible(true);
					if (dlg.isOk()) {
					Donut d = new Donut(new Point(Integer.parseInt(dlg.getTxtXc().getText().toString()), Integer.parseInt(dlg.getTxtYc().getText().toString())), Integer.parseInt(dlg.getTxtR().getText().toString()), Integer.parseInt(dlg.getTxtIR().getText().toString()), false, dlg.getBtnColor().getBackground(), dlg.getBtnInnerColor().getBackground());
					UpdateDonutCommand udc = new UpdateDonutCommand(temp, d);
					undoCommandsStack.push(udc);
					redoCommandsStack.clear();
					redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
					udc.execute();
					log(udc.toString());
				}
			} else if (model.getShapes().get(i).isSelected() && model.getShapes().get(i) instanceof Rectangle)
			{
				DlgRectangle dlg = new DlgRectangle();			
				Rectangle temp = (Rectangle) model.getShapes().get(i);			
				dlg.getTxtY().setText(String.valueOf(temp.getUpperLeftPoint().getY()));
				dlg.getTxtX().setText(String.valueOf(temp.getUpperLeftPoint().getX()));
				dlg.getTxtWidth().setText(String.valueOf(temp.getWidth()));
				dlg.getTxtHeight().setText(String.valueOf(temp.getHeight()));
				dlg.getBtnColor().setBackground(temp.getColor());
				dlg.getBtnInnerColor().setBackground(temp.getInnerColor());
				dlg.setTitle("Modify rectangle");
				dlg.setVisible(true);
				if (dlg.isOk()) {
					Rectangle r = new Rectangle(new Point(Integer.parseInt(dlg.getTxtX().getText().toString()), Integer.parseInt(dlg.getTxtY().getText().toString())), Integer.parseInt(dlg.getTxtWidth().getText().toString()), Integer.parseInt(dlg.getTxtHeight().getText().toString()), false, dlg.getBtnColor().getBackground(), dlg.getBtnInnerColor().getBackground());
					UpdateRectangleCommand urc = new UpdateRectangleCommand(temp, r);
					undoCommandsStack.push(urc);
					redoCommandsStack.clear();
					redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
					urc.execute();
					log(urc.toString());
				}
			} else if (model.getShapes().get(i).isSelected() && model.getShapes().get(i) instanceof Point) {
				DlgPoint dlg = new DlgPoint();
				Point temp = (Point) model.getShapes().get(i);	
				//tacka cije vrednosti menjamo
				dlg.getTxtX().setText(String.valueOf(temp.getX()));
				dlg.getTxtY().setText(String.valueOf(temp.getY()));
				dlg.getBtnColor().setBackground(temp.getColor());
				dlg.setTitle("Modify point");
				dlg.setVisible(true);
				if (dlg.isOk()) {
						Point p3 = new Point(Integer.parseInt(dlg.getTxtX().getText().toString()), Integer.parseInt(dlg.getTxtY().getText().toString()), false, dlg.getBtnColor().getBackground());

						UpdatePointCommand upc = new UpdatePointCommand(temp, p3);
						undoCommandsStack.push(upc);
						redoCommandsStack.clear();
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						upc.execute();
						log(upc.toString());
				}
			} else if (model.getShapes().get(i).isSelected() && model.getShapes().get(i) instanceof Line) {
				DlgLine dlg = new DlgLine();
				Line temp = (Line) model.getShapes().get(i);		
				dlg.getTxtX1().setText(String.valueOf(temp.getStartPoint().getX()));
				dlg.getTxtX2().setText(String.valueOf(temp.getEndPoint().getX()));
				dlg.getTxtY1().setText(String.valueOf(temp.getStartPoint().getY()));
				dlg.getTxtY2().setText(String.valueOf(temp.getEndPoint().getY()));
				dlg.getBtnColor().setBackground(temp.getColor());
				dlg.setTitle("Modify line");
				dlg.setVisible(true);
				if (dlg.isOk()) {
						Line l = new Line(new Point(Integer.parseInt(dlg.getTxtX1().getText().toString()), Integer.parseInt(dlg.getTxtY1().getText().toString())), new Point(Integer.parseInt(dlg.getTxtX2().getText().toString()), Integer.parseInt(dlg.getTxtY2().getText().toString())), false, dlg.getBtnColor().getBackground());
						UpdateLineCommand ulc = new UpdateLineCommand(temp, l);
						undoCommandsStack.push(ulc);
						redoCommandsStack.clear();
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						ulc.execute();
						log(ulc.toString());
						frame.repaint();
				}
			} else if (model.getShapes().get(i).isSelected() && model.getShapes().get(i) instanceof HexagonAdapter) {
				DlgHexagon dlg = new DlgHexagon();
				HexagonAdapter temp = (HexagonAdapter) model.getShapes().get(i);		
				dlg.getTxtX().setText(String.valueOf(temp.getHexagon().getX()));
				dlg.getTxtY().setText(String.valueOf(temp.getHexagon().getY()));
				dlg.getTxtR().setText(String.valueOf(temp.getHexagon().getR()));
				dlg.getBtnColor().setBackground(temp.getHexagon().getBorderColor());
				dlg.getBtnInnerColor().setBackground(temp.getHexagon().getAreaColor());
				dlg.setTitle("Modify hexagon");
				dlg.setVisible(true);
				if (dlg.isOk()) {
					Color c = dlg.getBtnColor().getBackground();
					Color innerC = dlg.getBtnInnerColor().getBackground();
					HexagonAdapter hexAdapter = new HexagonAdapter(new Point(Integer.parseInt(dlg.getTxtX().getText().toString()), Integer.parseInt(dlg.getTxtY().getText().toString())),
							Integer.parseInt(dlg.getTxtR().getText().toString()), false, c, innerC);
					UpdateHexagonCommand uhc = new UpdateHexagonCommand(temp, hexAdapter);
					undoCommandsStack.push(uhc);
					redoCommandsStack.clear();
					redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
					uhc.execute();
					log(uhc.toString());
				}
			}
			}
			frame.repaint();
		}
	}
	
	public void delete() {
		if (!model.isShapeSelected())
			JOptionPane.showMessageDialog(null, "Please select what shape should be deleted!");
		else {
			int confirmation = JOptionPane.showConfirmDialog(null, "This action is permanent, proceed?", "Delete shape",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (confirmation == 0) {
				for (Shape sh : model.getSelectedShapes()) {
					RemoveShapeCommand removeShapeCommand = new RemoveShapeCommand(sh, model);
					removeShapeCommand.execute();
					undoCommandsStack.push(removeShapeCommand);
					redoCommandsStack.clear();
					log(removeShapeCommand.toString());
					frame.repaint();
				}
				redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
				shapeObserver.onChange(model.getSelectedShapes().size());
			}
		}
	}
	
	public void undo() {
		if (!undoCommandsStack.isEmpty()) {
			Command cmd = undoCommandsStack.pop();
			cmd.unexecute();
			log("Undo " + cmd.toString());
			
			redoCommandsStack.push(cmd);
			redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
			frame.repaint();
		}
	}

	public void redo() {
		if (!redoCommandsStack.isEmpty()) {
			Command cmd = redoCommandsStack.pop();
			log("Redo " + cmd.toString());
			cmd.execute();
			undoCommandsStack.push(cmd);
			redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
			frame.repaint();
		}
	}
	
	public void bringToBack() {
		ArrayList<Shape> selectedShapes = model.getSelectedShapes();
		if (selectedShapes.size() == 1) {
			Shape selectedShape = selectedShapes.get(0);
			BringToBackCommand bringToBackCommand = new BringToBackCommand(selectedShape, model);
			bringToBackCommand.execute();
			undoCommandsStack.push(bringToBackCommand);
			redoCommandsStack.clear();
			redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
			frame.repaint();
			if(backFrontLog == true)
			{
				log(bringToBackCommand.toString());
			}
			
		}
	}

	public void bringToFront() {
		ArrayList<Shape> selectedShapes = model.getSelectedShapes();
		if (selectedShapes.size() == 1) {
			Shape selectedShape = selectedShapes.get(0);
			BringToFrontCommand bringToFrontCommand = new BringToFrontCommand(selectedShape, model);
			bringToFrontCommand.execute();
			undoCommandsStack.push(bringToFrontCommand);
			redoCommandsStack.clear();
			redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
			frame.repaint();
			if(backFrontLog == true)
			{
				log(bringToFrontCommand.toString());
			}
			
		}
	}

	public void toBack() {
		ArrayList<Shape> selectedShapes = model.getSelectedShapes();
		if (selectedShapes.size() == 1) {
			Shape selectedShape = selectedShapes.get(0);
			ToBackCommand toBackCommand = new ToBackCommand(selectedShape, model);
			if (toBackCommand.getLast())
			{
				toBackCommand.execute();
				undoCommandsStack.push(toBackCommand);
				redoCommandsStack.clear();
				redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
				if(backFrontLog == true)
				{
					log(toBackCommand.toString());
				}	
				frame.repaint();
			}
			else
			{
				JOptionPane.showMessageDialog(frame, "Shape is already in the back!");
			}
		}
	}

	public void toFront() {
		ArrayList<Shape> selectedShapes = model.getSelectedShapes();
		if (selectedShapes.size() == 1) {
			Shape selectedShape = selectedShapes.get(0);
			ToFrontCommand toFrontCommand = new ToFrontCommand(selectedShape, model);
			if(toFrontCommand.getFirst())
			{
				toFrontCommand.execute();
				undoCommandsStack.push(toFrontCommand);
				redoCommandsStack.clear();
				redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
				if(backFrontLog == true)
				{
					log(toFrontCommand.toString());
				}
				frame.repaint();
			}
			else
			{
				JOptionPane.showMessageDialog(frame, "Shape is already in the front!");
			}
			
		}
	}
	
	public void saveLog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Log");
		
		
		FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter(".txt", "txt");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(fileNameExtensionFilter);

		if (fileChooser.showSaveDialog(frame.getParent()) == JFileChooser.APPROVE_OPTION) {
			
			
			File file = fileChooser.getSelectedFile();
			
			String filePath = file.getAbsolutePath();
			
			File log = new File(filePath + ".txt");

			SavingManager manager = new SavingManager(new LogSave());
			manager.save(frame, log);
			
			System.out.println(fileChooser.getSelectedFile().getName() + "log successfully saved " + " file!");
		}
		frame.getView().repaint();
	}
	
	public void openLog() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open log");
		FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter(".txt", "txt");
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(fileNameExtensionFilter);

		int userSelection = fileChooser.showOpenDialog(null);
		
		log.clear();
		model.getShapes();
		model.getSelectedShapes().clear();
		undoCommandsStack.clear();
		redoCommandsStack.clear();
		frame.repaint();
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File logToLoad = fileChooser.getSelectedFile();
			loadLog(logToLoad);
			
		}
		
		
	}
	
	public void loadLog(File logToLoad) throws IOException {
		try {
			frame.getTextArea().setText("");
			if (logToLoad.length() == 0) {
				System.out.println("\"" + logToLoad.getName() + "\" file is empty");
				return;
			}
			BufferedReader br = new BufferedReader(new FileReader(logToLoad));
			String stringLine;
			
			
			while ((stringLine = br.readLine()) != null) {
				log.add(stringLine);
			}
			br.close();
			frame.getBtnPoint().setEnabled(false);
			frame.getBtnLine().setEnabled(false);
			frame.getBtnDonut().setEnabled(false);
			frame.getBtnCircle().setEnabled(false);
			frame.getBtnHexagon().setEnabled(false);
			frame.getBtnUndo().setEnabled(false);
			frame.getBtnRedo().setEnabled(false);
			frame.getBtnRectangle().setEnabled(false);
			frame.getBtnNext().setEnabled(true);

		} catch (Exception e) {
			System.err.println("There has been an error: " + e.getMessage());
		}
	}
	
	
	 
	public void saveDrawing() throws IOException, NotSerializableException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Painting");
		
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".bin", "bin");
		fileChooser.setFileFilter(filter);

		int selection = fileChooser.showSaveDialog(null);

		if (selection == JFileChooser.APPROVE_OPTION) {
			File log;
			
			File painting = fileChooser.getSelectedFile();
			
			
			
			String filePath = painting.getAbsolutePath();
			if (!filePath.contains(".") && !filePath.endsWith(".bin")) {
				log = new File(filePath + ".txt");
				
				painting = new File(filePath + ".bin");
				
			}

			String fileName = painting.getPath();
			
			
			if (fileName.substring(fileName.lastIndexOf("."), fileName.length()).contains(".bin")) {
				fileName = painting.getAbsolutePath().substring(0, fileName.lastIndexOf(".")) + ".txt";
				
				SavingManager savePainting = new SavingManager(new DrawingSave());
				SavingManager saveLog = new SavingManager(new LogSave());
				
				log = new File(fileName);
				
				savePainting.save(model, painting);
				saveLog.save(frame, log);
				
				System.out.println("Painting saved, location: " + painting.getAbsolutePath());
				
			} else {
				JOptionPane.showMessageDialog(null, "Wrong file extension!");
			}
		}
	}
	 
	 public void loadDrawing(File paintingToLoad) throws FileNotFoundException, IOException, ClassNotFoundException {
			frame.getTextArea().setText("");

			File file = new File(paintingToLoad.getAbsolutePath().replace("bin", "txt"));

			if (file.length() == 0) {
				System.out.println("\"" + paintingToLoad.getName() + "\" file is empty!");
				return;
			}

			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String logLine;

			while ((logLine = bufferedReader.readLine()) != null) {
				frame.getTextArea().append(logLine + "\n");
			}
			bufferedReader.close();

			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(paintingToLoad));
			try {

				model.getShapes().addAll((ArrayList<Shape>) objectInputStream.readObject());
				objectInputStream.close();

			} catch (InvalidClassException ice) {
				ice.printStackTrace();
			} catch (SocketTimeoutException ste) {
				ste.printStackTrace();
			} catch (EOFException eofe) {
				eofe.printStackTrace();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
			frame.getView().repaint();
		}

	public void openDrawing() throws IOException, ClassNotFoundException {
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setAcceptAllFileFilterUsed(false);
			
			FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter(".bin", "bin");
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(extensionFilter);

			fileChooser.setDialogTitle("Open Painting");
			int userSelection = fileChooser.showOpenDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION) {
				
				File loadingDrawing = fileChooser.getSelectedFile();
				
				loadDrawing(loadingDrawing);

			}
		}
	
	public void loadNext() {
		
		Shape shape = null;
		
		if (logCounter < log.size()) {
			String row = log.get(logCounter);
			
			if (row.contains("added")) {
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
					Command cmd = undoCommandsStack.pop();
					cmd.unexecute();
					
					redoCommandsStack.push(cmd);
					redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
					frame.repaint();
				}
			    	
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	if (row.contains("Line")) {
			    		Pattern pattern = Pattern.compile("Line added:Point: \\((-?\\d+),(-?\\d+)\\)-->Point: \\((-?\\d+),(-?\\d+)\\),color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			    		Matcher matcher = pattern.matcher(row);

			    		int startPointX = 0;
			    		int startPointY = 0;
			    		int endPointX = 0;
			    		int endPointY = 0;
			    		Color color = null; // Initialize the Color variable

			    		if (matcher.find()) {
			    		    startPointX = Integer.parseInt(matcher.group(1));
			    		    startPointY = Integer.parseInt(matcher.group(2));
			    		    endPointX = Integer.parseInt(matcher.group(3));
			    		    endPointY = Integer.parseInt(matcher.group(4));
			    		    int red = Integer.parseInt(matcher.group(5));
			    		    int green = Integer.parseInt(matcher.group(6));
			    		    int blue = Integer.parseInt(matcher.group(7));
			    		    color = new Color(red, green, blue);
			    		    
			    		    shape = new Line(new Point(startPointX, startPointY),new Point(endPointX, endPointY), color);
			        	    AddLineCommand alc = new AddLineCommand((Line)shape, model);
			        	    alc.execute();
			        	    undoCommandsStack.push(alc);
			    			redoCommandsStack.clear();
			    			frame.repaint();
			    		}
			    		
			        } else if (row.contains("Rectangle")) {
			        	Pattern pattern = Pattern.compile("Point: \\((-?\\d+),(-?\\d+)\\),widht=(\\d+),height=(\\d+),edge color:java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row);

			        	int upperLeftPointX = 0;
			        	int upperLeftPointY = 0;
			        	int width = 0;
			        	int height = 0;
			        	Color edgeColor = null; // Initialize the edge color variable
			        	Color innerColor = null; // Initialize the inner color variable

			        	if (matcher.find()) {
			        	    upperLeftPointX = Integer.parseInt(matcher.group(1));
			        	    upperLeftPointY = Integer.parseInt(matcher.group(2));
			        	    width = Integer.parseInt(matcher.group(3));
			        	    height = Integer.parseInt(matcher.group(4));
			        	    int edgeRed = Integer.parseInt(matcher.group(5));
			        	    int edgeGreen = Integer.parseInt(matcher.group(6));
			        	    int edgeBlue = Integer.parseInt(matcher.group(7));
			        	    int innerRed = Integer.parseInt(matcher.group(8));
			        	    int innerGreen = Integer.parseInt(matcher.group(9));
			        	    int innerBlue = Integer.parseInt(matcher.group(10));

			        	    // Create the Color objects with the extracted RGB values
			        	    edgeColor = new Color(edgeRed, edgeGreen, edgeBlue);
			        	    innerColor = new Color(innerRed, innerGreen, innerBlue);
			        	    
			        	    shape = new Rectangle(new Point(upperLeftPointX,upperLeftPointY), width, height, edgeColor, innerColor);
			        	    AddRectangleCommand arc = new AddRectangleCommand((Rectangle)shape, model);
			        	    arc.execute();
			        	    undoCommandsStack.push(arc);
			    			redoCommandsStack.clear();
			    			frame.repaint();
			        	}
			        } else if (row.contains("Hexagon")) {
			        	Pattern pattern = Pattern.compile("Hexagon added:Hexagon:Point: \\((-?\\d+),(-?\\d+)\\),(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");

			        	Matcher matcher = pattern.matcher(row);

			        	int centerX = 0;
			        	int centerY = 0;
			        	int radius = 0;
			        	Color edgeColor = null; // Initialize the edge color variable
			        	Color innerColor = null; // Initialize the inner color variable

			        	if (matcher.find()) {
			        	    centerX = Integer.parseInt(matcher.group(1));
			        	    centerY = Integer.parseInt(matcher.group(2));
			        	    radius = Integer.parseInt(matcher.group(3));
			        	    int edgeRed = Integer.parseInt(matcher.group(4));
			        	    int edgeGreen = Integer.parseInt(matcher.group(5));
			        	    int edgeBlue = Integer.parseInt(matcher.group(6));
			        	    int innerRed = Integer.parseInt(matcher.group(7));
			        	    int innerGreen = Integer.parseInt(matcher.group(8));
			        	    int innerBlue = Integer.parseInt(matcher.group(9));

			        	    // Create the Color objects with the extracted RGB values
			        	    edgeColor = new Color(edgeRed, edgeGreen, edgeBlue);
			        	    innerColor = new Color(innerRed, innerGreen, innerBlue);
			        	    
			        	    shape = new HexagonAdapter(new Point(centerX,centerY), radius, edgeColor, innerColor);
			        	    AddHexagonCommand ahc = new AddHexagonCommand((HexagonAdapter)shape, model);
			        	    ahc.execute();
			        	    undoCommandsStack.push(ahc);
			    			redoCommandsStack.clear();
			    			frame.repaint();
			        	}
			        } else if (row.contains("Circle")) {
			        	Pattern pattern = Pattern.compile("Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),edge color:java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row);

			        	int centerX = 0;
			        	int centerY = 0;
			        	int radius = 0;
			        	Color edgeColor = null; // Initialize the edge color variable
			        	Color innerColor = null; // Initialize the inner color variable

			        	if (matcher.find()) {
			        	    centerX = Integer.parseInt(matcher.group(1));
			        	    centerY = Integer.parseInt(matcher.group(2));
			        	    radius = Integer.parseInt(matcher.group(3));
			        	    int edgeRed = Integer.parseInt(matcher.group(4));
			        	    int edgeGreen = Integer.parseInt(matcher.group(5));
			        	    int edgeBlue = Integer.parseInt(matcher.group(6));
			        	    int innerRed = Integer.parseInt(matcher.group(7));
			        	    int innerGreen = Integer.parseInt(matcher.group(8));
			        	    int innerBlue = Integer.parseInt(matcher.group(9));

			        	    // Create the Color objects with the extracted RGB values
			        	    edgeColor = new Color(edgeRed, edgeGreen, edgeBlue);
			        	    innerColor = new Color(innerRed, innerGreen, innerBlue);
			        	    
			        	    shape = new Circle(new Point(centerX,centerY), radius, false, edgeColor, innerColor);
			        	    AddCircleCommand acc = new AddCircleCommand((Circle)shape, model);
			        	    acc.execute();
			        	    undoCommandsStack.push(acc);
			    			redoCommandsStack.clear();
			    			frame.repaint();
			        	}
			        } else if (row.contains("Donut")) {
			        	Pattern pattern = Pattern.compile("Donut added:Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),inner radius=(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row);

			        	int centerX = 0;
			        	int centerY = 0;
			        	int radius = 0;
			        	int innerRadius = 0;
			        	Color edgeColor = null; // Initialize the edge color variable
			        	Color innerColor = null; // Initialize the inner color variable

			        	if (matcher.find()) {
			        	    centerX = Integer.parseInt(matcher.group(1));
			        	    centerY = Integer.parseInt(matcher.group(2));
			        	    radius = Integer.parseInt(matcher.group(3));
			        	    innerRadius = Integer.parseInt(matcher.group(4)); // Extract inner radius
			        	    int edgeRed = Integer.parseInt(matcher.group(5));
			        	    int edgeGreen = Integer.parseInt(matcher.group(6));
			        	    int edgeBlue = Integer.parseInt(matcher.group(7));
			        	    int innerRed = Integer.parseInt(matcher.group(8));
			        	    int innerGreen = Integer.parseInt(matcher.group(9));
			        	    int innerBlue = Integer.parseInt(matcher.group(10));

			        	    // Create the Color objects with the extracted RGB values
			        	    edgeColor = new Color(edgeRed, edgeGreen, edgeBlue);
			        	    innerColor = new Color(innerRed, innerGreen, innerBlue);
			        	    
			        	    shape = new Donut(new Point(centerX,centerY), radius, innerRadius, false, edgeColor, innerColor);
			        	    AddDonutCommand adc = new AddDonutCommand((Donut)shape, model);
			        	    adc.execute();
			        	    undoCommandsStack.push(adc);
			    			redoCommandsStack.clear();
			    			frame.repaint();
			    			
			        	}
			        } else
			        {
			        	// Use regular expressions to extract specific values
			        	Pattern pattern = Pattern.compile("\\((-?\\d+),(-?\\d+)\\),color:java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row);

			        	if (matcher.find()) {
			        	    int x = Integer.parseInt(matcher.group(1));
			        	    int y = Integer.parseInt(matcher.group(2));
			        	    int red = Integer.parseInt(matcher.group(3));
			        	    int green = Integer.parseInt(matcher.group(4));
			        	    int blue = Integer.parseInt(matcher.group(5));
			        	    Color color = new Color(red, green, blue);
			        	    
			        	    shape = new Point(x, y, color);
			        	    AddPointCommand apc = new AddPointCommand((Point)shape, model);
			        	    apc.execute();
			        	    undoCommandsStack.push(apc);
			    			redoCommandsStack.clear();
			    			frame.repaint();
			        	}

			        }
			    }
			} else if (row.contains("updated")) {
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
						Command cmd = undoCommandsStack.pop();
						cmd.unexecute();
						
						redoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
			    	}
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	if (row.contains("Line")) {
			    		 String regex = "Line updated:\\{.*?Point: \\((-?\\d+),(-?\\d+)\\).*?Point: \\((-?\\d+),(-?\\d+)\\).*?color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\}.*?\\{.*?Point: \\((-?\\d+),(-?\\d+)\\).*?Point: \\((-?\\d+),(-?\\d+)\\).*?color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\}";
			    		 Pattern pattern = Pattern.compile(regex);
			    		 Matcher matcher = pattern.matcher(row);

			    		if (matcher.find()) {
			    		    int startPointXBefore = Integer.parseInt(matcher.group(1));
			    		    int startPointYBefore = Integer.parseInt(matcher.group(2));
			    		    int endPointXBefore = Integer.parseInt(matcher.group(3));
			    		    int endPointYBefore = Integer.parseInt(matcher.group(4));
			    		    int redBefore = Integer.parseInt(matcher.group(5));
			    		    int greenBefore = Integer.parseInt(matcher.group(6));
			    		    int blueBefore = Integer.parseInt(matcher.group(7));
			    		    Color colorBefore = new Color(redBefore, greenBefore, blueBefore);
			    		    
			    		    int startPointXAfter = Integer.parseInt(matcher.group(8));
			    		    int startPointYAfter = Integer.parseInt(matcher.group(9));
			    		    int endPointXAfter = Integer.parseInt(matcher.group(10));
			    		    int endPointYAfter = Integer.parseInt(matcher.group(11));
			    		    int redAfter = Integer.parseInt(matcher.group(12));
			    		    int greenAfter = Integer.parseInt(matcher.group(13));
			    		    int blueAfter = Integer.parseInt(matcher.group(14));
			    		    Color colorAfter = new Color(redAfter, greenAfter, blueAfter);
			    		    
			    		    Line shapeBefore = new Line(new Point(startPointXBefore, startPointYBefore),new Point(endPointXBefore, endPointYBefore), colorBefore);
			    		    Line shapeAfter = new Line(new Point(startPointXAfter, startPointYAfter),new Point(endPointXAfter, endPointYAfter), colorAfter);
			    		    
			    		    
			    		    
			    		    for (int i = 0; i < model.getShapes().size(); i++)
			        	    {
			        	    	
			        	    	if(shapeBefore.equals(model.get(i)))
			        	    	{
			        	    		Line p = new Line();
			        	    		p = (Line)model.get(i);
			        	    		UpdateLineCommand upc = new UpdateLineCommand(p, shapeAfter);
					        	    upc.execute();
					        	    undoCommandsStack.push(upc);
					    			frame.repaint();
			        	    	}
			        	    		
			        	    }
			    		    
			    		}
			        } else if (row.contains("Rectangle")) {
			        	 Pattern pattern = Pattern.compile("Rectangle updated:\\{UpperLeftPoint=Point: \\((-?\\d+),(-?\\d+)\\),widht=(\\d+),height=(\\d+),edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\} >>> \\{UpperLeftPoint=Point: \\((-?\\d+),(-?\\d+)\\),widht=(\\d+),height=(\\d+),edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\}");
			             Matcher matcher = pattern.matcher(row);
			             
			             if (matcher.find()) {
			                 int upperLeftXBefore = Integer.parseInt(matcher.group(1));
			                 int upperLeftYBefore = Integer.parseInt(matcher.group(2));
			                 int widthBefore = Integer.parseInt(matcher.group(3));
			                 int heightBefore = Integer.parseInt(matcher.group(4));
			                 int edgeRedBefore = Integer.parseInt(matcher.group(5));
			                 int edgeGreenBefore = Integer.parseInt(matcher.group(6));
			                 int edgeBlueBefore = Integer.parseInt(matcher.group(7));
			                 int innerRedBefore = Integer.parseInt(matcher.group(8));
			                 int innerGreenBefore = Integer.parseInt(matcher.group(9));
			                 int innerBlueBefore = Integer.parseInt(matcher.group(10));
			                 
			                 int upperLeftXAfter = Integer.parseInt(matcher.group(11));
			                 int upperLeftYAfter = Integer.parseInt(matcher.group(12));
			                 int widthAfter = Integer.parseInt(matcher.group(13));
			                 int heightAfter = Integer.parseInt(matcher.group(14));
			                 int edgeRedAfter = Integer.parseInt(matcher.group(15));
			                 int edgeGreenAfter = Integer.parseInt(matcher.group(16));
			                 int edgeBlueAfter = Integer.parseInt(matcher.group(17));
			                 int innerRedAfter = Integer.parseInt(matcher.group(18));
			                 int innerGreenAfter = Integer.parseInt(matcher.group(19));
			                 int innerBlueAfter = Integer.parseInt(matcher.group(20));
			                 
			                 Color edgeColorBefore = new Color(edgeRedBefore, edgeGreenBefore, edgeBlueBefore);
			                 Color innerColorBefore = new Color(innerRedBefore, innerGreenBefore, innerBlueBefore);
			                 Color edgeColorAfter = new Color(edgeRedAfter, edgeGreenAfter, edgeBlueAfter);
			                 Color innerColorAfter = new Color(innerRedAfter, innerGreenAfter, innerBlueAfter);
			                 
			                 	Rectangle shapeBefore = new Rectangle(new Point(upperLeftXBefore, upperLeftYBefore),widthBefore, heightBefore, edgeColorBefore, innerColorBefore);
				    		    Rectangle shapeAfter = new Rectangle(new Point(upperLeftXAfter, upperLeftYAfter),widthAfter, heightAfter, edgeColorAfter, innerColorAfter);
				    		    
				    		    for (int i = 0; i < model.getShapes().size(); i++)
				        	    {
				        	    	
				        	    	if(shapeBefore.equals(model.get(i)))
				        	    	{
				        	    		Rectangle p = new Rectangle();
				        	    		p = (Rectangle)model.get(i);
				        	    		UpdateRectangleCommand upc = new UpdateRectangleCommand(p, shapeAfter);
						        	    upc.execute();
						        	    undoCommandsStack.push(upc);
						    			frame.repaint();
				        	    	}
				        	    		
				        	    }
				    		    
			             }
			        } else if (row.contains("Hexagon")) {
			        	Pattern pattern = Pattern.compile("Hexagon updated:\\{Hexagon:Point: \\((-?\\d+),(-?\\d+)\\),(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\} >>> \\{Hexagon:Point: \\((-?\\d+),(-?\\d+)\\),(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\}");
			        	Matcher matcher = pattern.matcher(row);

			        	if (matcher.find()) {
			        		 	int centerXBefore = Integer.parseInt(matcher.group(1));
			        		    int centerYBefore = Integer.parseInt(matcher.group(2));
			        		    int radiusBefore = Integer.parseInt(matcher.group(3));
			        		    int edgeRedBefore = Integer.parseInt(matcher.group(4));
			        		    int edgeGreenBefore = Integer.parseInt(matcher.group(5));
			        		    int edgeBlueBefore = Integer.parseInt(matcher.group(6));
			        		    int innerRedBefore = Integer.parseInt(matcher.group(7));
			        		    int innerGreenBefore = Integer.parseInt(matcher.group(8));
			        		    int innerBlueBefore = Integer.parseInt(matcher.group(9));

			        		    
			        		    int centerXAfter = Integer.parseInt(matcher.group(10));
			        		    int centerYAfter = Integer.parseInt(matcher.group(11));
			        		    int radiusAfter = Integer.parseInt(matcher.group(12));
			        		    int edgeRedAfter = Integer.parseInt(matcher.group(13));
			        		    int edgeGreenAfter = Integer.parseInt(matcher.group(14));
			        		    int edgeBlueAfter = Integer.parseInt(matcher.group(15));
			        		    int innerRedAfter = Integer.parseInt(matcher.group(16));
			        		    int innerGreenAfter = Integer.parseInt(matcher.group(17));
			        		    int innerBlueAfter = Integer.parseInt(matcher.group(18));

			        		    
			        		    Color edgeColorBefore = new Color(edgeRedBefore, edgeGreenBefore, edgeBlueBefore);
			        		    Color innerColorBefore = new Color(innerRedBefore, innerGreenBefore, innerBlueBefore);
			        		    Color edgeColorAfter = new Color(edgeRedAfter, edgeGreenAfter, edgeBlueAfter);
			        		    Color innerColorAfter = new Color(innerRedAfter, innerGreenAfter, innerBlueAfter);
			        	    
			        	    HexagonAdapter shapeBefore = new HexagonAdapter(new Point(centerXBefore, centerYBefore), radiusBefore,edgeColorBefore, innerColorBefore);
			    		    HexagonAdapter shapeAfter = new HexagonAdapter(new Point(centerXAfter, centerYAfter),radiusAfter,edgeColorAfter, innerColorAfter);
			    		    
			    		    for (int i = 0; i < model.getShapes().size(); i++)
			        	    {
			        	    	
			        	    	if(shapeBefore.equals(model.get(i)))
			        	    	{
			        	    		HexagonAdapter p = new HexagonAdapter();
			        	    		p = (HexagonAdapter)model.get(i);
			        	    		UpdateHexagonCommand upc = new UpdateHexagonCommand(p, shapeAfter);
					        	    upc.execute();
					        	    undoCommandsStack.push(upc);
					    			frame.repaint();
			        	    	}
			        	    		
			        	    }
			        	    
			        	}
			        } else if (row.contains("Circle")) {
			        	Pattern pattern = Pattern.compile("Circle updated:\\{Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\} >>> \\{Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\}");
			        	Matcher matcher = pattern.matcher(row);

			        	if (matcher.find()) {
			        	    int centerXBefore = Integer.parseInt(matcher.group(1));
			        	    int centerYBefore = Integer.parseInt(matcher.group(2));
			        	    int radiusBefore = Integer.parseInt(matcher.group(3));
			        	    int edgeRedBefore = Integer.parseInt(matcher.group(4));
			        	    int edgeGreenBefore = Integer.parseInt(matcher.group(5));
			        	    int edgeBlueBefore = Integer.parseInt(matcher.group(6));
			        	    int innerRedBefore = Integer.parseInt(matcher.group(7));
			        	    int innerGreenBefore = Integer.parseInt(matcher.group(8));
			        	    int innerBlueBefore = Integer.parseInt(matcher.group(9));

			        	    
			        	    int centerXAfter = Integer.parseInt(matcher.group(10));
			        	    int centerYAfter = Integer.parseInt(matcher.group(11));
			        	    int radiusAfter = Integer.parseInt(matcher.group(12));
			        	    int edgeRedAfter = Integer.parseInt(matcher.group(13));
			        	    int edgeGreenAfter = Integer.parseInt(matcher.group(14));
			        	    int edgeBlueAfter = Integer.parseInt(matcher.group(15));
			        	    int innerRedAfter = Integer.parseInt(matcher.group(16));
			        	    int innerGreenAfter = Integer.parseInt(matcher.group(17));
			        	    int innerBlueAfter = Integer.parseInt(matcher.group(18));

			        	    
			        	    Color edgeColorBefore = new Color(edgeRedBefore, edgeGreenBefore, edgeBlueBefore);
			        	    Color innerColorBefore = new Color(innerRedBefore, innerGreenBefore, innerBlueBefore);
			        	    Color edgeColorAfter = new Color(edgeRedAfter, edgeGreenAfter, edgeBlueAfter);
			        	    Color innerColorAfter = new Color(innerRedAfter, innerGreenAfter, innerBlueAfter);

			        	    Circle shapeBefore = new Circle(new Point(centerXBefore, centerYBefore),radiusBefore,true ,edgeColorBefore, innerColorBefore);
			    		    Circle shapeAfter = new Circle(new Point(centerXAfter, centerYAfter),radiusAfter, true, edgeColorAfter, innerColorAfter);
			    		    
			    		    for (int i = 0; i < model.getShapes().size(); i++)
			        	    {
			        	    	
			        	    	if(shapeBefore.equals(model.get(i)))
			        	    	{
			        	    		Circle p = new Circle();
			        	    		p = (Circle)model.get(i);
			        	    		UpdateCircleCommand upc = new UpdateCircleCommand(p, shapeAfter);
					        	    upc.execute();
					        	    undoCommandsStack.push(upc);
					    			frame.repaint();
			        	    	}
			        	    		
			        	    }
			    		    
			        	}
			        } else if (row.contains("Donut")) {
			        	Pattern pattern = Pattern.compile("Donut updated:\\{Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),inner radius=(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\} >>> \\{Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),inner radius=(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\}");
			        	Matcher matcher = pattern.matcher(row);

			        	if (matcher.find()) {
			        	    int centerXBefore = Integer.parseInt(matcher.group(1));
			        	    int centerYBefore = Integer.parseInt(matcher.group(2));
			        	    int radiusBefore = Integer.parseInt(matcher.group(3));
			        	    int innerRadiusBefore = Integer.parseInt(matcher.group(4));
			        	    int edgeRedBefore = Integer.parseInt(matcher.group(5));
			        	    int edgeGreenBefore = Integer.parseInt(matcher.group(6));
			        	    int edgeBlueBefore = Integer.parseInt(matcher.group(7));
			        	    int innerRedBefore = Integer.parseInt(matcher.group(8));
			        	    int innerGreenBefore = Integer.parseInt(matcher.group(9));
			        	    int innerBlueBefore = Integer.parseInt(matcher.group(10));

			        	    
			        	    int centerXAfter = Integer.parseInt(matcher.group(11));
			        	    int centerYAfter = Integer.parseInt(matcher.group(12));
			        	    int radiusAfter = Integer.parseInt(matcher.group(13));
			        	    int innerRadiusAfter = Integer.parseInt(matcher.group(14));
			        	    int edgeRedAfter = Integer.parseInt(matcher.group(15));
			        	    int edgeGreenAfter = Integer.parseInt(matcher.group(16));
			        	    int edgeBlueAfter = Integer.parseInt(matcher.group(17));
			        	    int innerRedAfter = Integer.parseInt(matcher.group(18));
			        	    int innerGreenAfter = Integer.parseInt(matcher.group(19));
			        	    int innerBlueAfter = Integer.parseInt(matcher.group(20));

			        	    // Create Color objects
			        	    Color edgeColorBefore = new Color(edgeRedBefore, edgeGreenBefore, edgeBlueBefore);
			        	    Color innerColorBefore = new Color(innerRedBefore, innerGreenBefore, innerBlueBefore);
			        	    Color edgeColorAfter = new Color(edgeRedAfter, edgeGreenAfter, edgeBlueAfter);
			        	    Color innerColorAfter = new Color(innerRedAfter, innerGreenAfter, innerBlueAfter);

			        	    
			        	    Donut shapeBefore = new Donut(new Point(centerXBefore, centerYBefore), radiusBefore, innerRadiusBefore, edgeColorBefore, innerColorBefore);
			    		    Donut shapeAfter = new Donut(new Point(centerXAfter, centerYAfter),radiusAfter, innerRadiusAfter, edgeColorAfter, innerColorAfter);
			    		    
			    		    for (int i = 0; i < model.getShapes().size(); i++)
			        	    {
			        	    	
			        	    	if(shapeBefore.equals(model.get(i)))
			        	    	{
			        	    		Donut p = new Donut();
			        	    		p = (Donut)model.get(i);
			        	    		UpdateDonutCommand upc = new UpdateDonutCommand(p, shapeAfter);
					        	    upc.execute();
					        	    undoCommandsStack.push(upc);
					    			frame.repaint();
			        	    	}
			        	    		
			        	    }
			    		   
			        	}
			        } else
			        {
			        	
			        	Pattern pattern = Pattern.compile("Point updated:\\{Point: \\((-?\\d+),(-?\\d+)\\),color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\} >>> \\{Point: \\((-?\\d+),(-?\\d+)\\),color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]\\}");

			        	Matcher matcher = pattern.matcher(row);

			        	if (matcher.find()) {
			        		int xBefore = Integer.parseInt(matcher.group(1));
			                int yBefore = Integer.parseInt(matcher.group(2));
			                int redBefore = Integer.parseInt(matcher.group(3));
			                int greenBefore = Integer.parseInt(matcher.group(4));
			                int blueBefore = Integer.parseInt(matcher.group(5));

			                int xAfter = Integer.parseInt(matcher.group(6));
			                int yAfter = Integer.parseInt(matcher.group(7));
			                int redAfter = Integer.parseInt(matcher.group(8));
			                int greenAfter = Integer.parseInt(matcher.group(9));
			                int blueAfter = Integer.parseInt(matcher.group(10));
			                
			                Color colorBefore = new Color(redBefore, greenBefore, blueBefore);
			                Color colorAfter = new Color(redAfter, greenAfter, blueAfter);
			        	    
			        	    Point OldShape = new Point(xBefore, yBefore, colorBefore);
			        	    Point NewShape = new Point(xAfter, yAfter, colorAfter);
			        	    
			        	    for (int i = 0; i < model.getShapes().size(); i++)
			        	    {
			        	    	
			        	    	if(OldShape.equals(model.get(i)))
			        	    	{
			        	    		Point p = new Point();
			        	    		p = (Point)model.get(i);
			        	    		UpdatePointCommand upc = new UpdatePointCommand(p, NewShape);
					        	    upc.execute();
					        	    undoCommandsStack.push(upc);
					    			frame.repaint();
			        	    	}
			        	    		
			        	    }
			        	    
			        	}
			        }
			    }
			} else if (row.contains("deselected")) {
				print = false;
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
						Command cmd = undoCommandsStack.pop();
						cmd.unexecute();
						redoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	if (row.contains("Line")) {
			    		Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Line deselected:Point: \\((-?\\d+),(-?\\d+)\\)-->Point: \\((-?\\d+),(-?\\d+)\\),color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			    		Matcher matcher = pattern.matcher(row);
			    		if (matcher.find()) {
			    			int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			    		    int startX = Integer.parseInt(matcher.group(3));
			    		    int startY = Integer.parseInt(matcher.group(4));
			    		    int endX = Integer.parseInt(matcher.group(5));
			    		    int endY = Integer.parseInt(matcher.group(6));
			    		    int red = Integer.parseInt(matcher.group(7));
			    		    int green = Integer.parseInt(matcher.group(8));
			    		    int blue = Integer.parseInt(matcher.group(9));

			    		    Point start = new Point(startX, startY);
			    		    Point end = new Point(endX, endY);
			    		    Color color = new Color(red, green, blue);
			    		    Line l1 = new Line(start, end, color);
			    		    
			    		    shape = (Shape) l1;
			    		    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			    		    
			    		}
			        } else if (row.contains("Rectangle")) {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Rectangle deselected:UpperLeftPoint=Point: \\((-?\\d+),(-?\\d+)\\),widht=(\\d+),height=(\\d+),edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row);

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int upperLeftX = Integer.parseInt(matcher.group(3));
			        	    int upperLeftY = Integer.parseInt(matcher.group(4));
			        	    int width = Integer.parseInt(matcher.group(5));
			        	    int height = Integer.parseInt(matcher.group(6));
			        	    int edgeRed = Integer.parseInt(matcher.group(7));
			        	    int edgeGreen = Integer.parseInt(matcher.group(8));
			        	    int edgeBlue = Integer.parseInt(matcher.group(9));
			        	    int innerRed = Integer.parseInt(matcher.group(10));
			        	    int innerGreen = Integer.parseInt(matcher.group(11));
			        	    int innerBlue = Integer.parseInt(matcher.group(12));

			        	   Point p = new Point(upperLeftX, upperLeftY);
			        	   Color edge = new Color(edgeRed, edgeGreen, edgeBlue);
			        	   Color inner = new Color(innerRed, innerGreen, innerBlue);
			        	   Rectangle r = new Rectangle(p, width, height, edge, inner);
			        	   shape = (Shape)r;
			        	   
			        	   this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	}

			        } else if (row.contains("Hexagon")) {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) HexagonAdapter deselected:Hexagon:Point: \\((-?\\d+),(-?\\d+)\\),(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row);

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int centerX = Integer.parseInt(matcher.group(3));
			        	    int centerY = Integer.parseInt(matcher.group(4));
			        	    int radius = Integer.parseInt(matcher.group(5));
			        	    int edgeRed = Integer.parseInt(matcher.group(6));
			        	    int edgeGreen = Integer.parseInt(matcher.group(7));
			        	    int edgeBlue = Integer.parseInt(matcher.group(8));
			        	    int innerRed = Integer.parseInt(matcher.group(9));
			        	    int innerGreen = Integer.parseInt(matcher.group(10));
			        	    int innerBlue = Integer.parseInt(matcher.group(11));

			        	   Point p = new Point(centerX, centerY);
			        	   Color edge = new Color(edgeRed, edgeGreen, edgeBlue);
			        	   Color inner = new Color(innerRed, innerGreen, innerBlue);
			        	   HexagonAdapter h = new HexagonAdapter(p, radius, edge, inner);
			        	   shape = (Shape)h;
			        	   
			        	   this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	}

			        } else if (row.contains("Circle")) {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Circle deselected:Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row); 

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int centerX = Integer.parseInt(matcher.group(3));
			        	    int centerY = Integer.parseInt(matcher.group(4));
			        	    int radius = Integer.parseInt(matcher.group(5));
			        	    int edgeRed = Integer.parseInt(matcher.group(6));
			        	    int edgeGreen = Integer.parseInt(matcher.group(7));
			        	    int edgeBlue = Integer.parseInt(matcher.group(8));
			        	    int innerRed = Integer.parseInt(matcher.group(9));
			        	    int innerGreen = Integer.parseInt(matcher.group(10));
			        	    int innerBlue = Integer.parseInt(matcher.group(11));

			        	    Point p = new Point(centerX, centerY);
			        	    Color edge = new Color(edgeRed, edgeGreen, edgeBlue);
			        	    Color inner = new Color(innerRed, innerGreen, innerBlue);
			        	    Circle c = new Circle(p, radius, true, edge, inner);
			        	    shape = (Shape)c;
			        	    
			        	    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	}

			        } else if (row.contains("Donut")) {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Donut deselected:Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),inner radius=(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row); 

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int centerX = Integer.parseInt(matcher.group(3));
			        	    int centerY = Integer.parseInt(matcher.group(4));
			        	    int radius = Integer.parseInt(matcher.group(5));
			        	    int innerRadius = Integer.parseInt(matcher.group(6));
			        	    int edgeRed = Integer.parseInt(matcher.group(7));
			        	    int edgeGreen = Integer.parseInt(matcher.group(8));
			        	    int edgeBlue = Integer.parseInt(matcher.group(9));
			        	    int innerRed = Integer.parseInt(matcher.group(10));
			        	    int innerGreen = Integer.parseInt(matcher.group(11));
			        	    int innerBlue = Integer.parseInt(matcher.group(12));

			        	    
			        	    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	}

			        } else
			        {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Point deselected:Point: \\((-?\\d+),(-?\\d+)\\),color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row);
			        	
			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int x = Integer.parseInt(matcher.group(3));
			        	    int y = Integer.parseInt(matcher.group(4));
			        	    int redValue = Integer.parseInt(matcher.group(5));
			        	    int greenValue = Integer.parseInt(matcher.group(6));
			        	    int blueValue = Integer.parseInt(matcher.group(7));
			        	    
			        	    Color color = new Color(redValue, greenValue, blueValue);
			        	    
			        	    Point p = new Point(x, y, color);
			        	    shape = (Shape)p;
			        	    
			        	    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	    
			        	    
			        	}
			        	
			        }
			    	print = true;
			    }
			} else if (row.contains("selected")) {
				print = false;
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
						Command cmd = undoCommandsStack.pop();
						cmd.unexecute();
						redoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	if (row.contains("Line")) {
			    		Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Line selected:Point: \\((-?\\d+),(-?\\d+)\\)-->Point: \\((-?\\d+),(-?\\d+)\\),color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			    		Matcher matcher = pattern.matcher(row); 

			    		if (matcher.find()) {
			    			int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			    		    int startX = Integer.parseInt(matcher.group(3));
			    		    int startY = Integer.parseInt(matcher.group(4));
			    		    int endX = Integer.parseInt(matcher.group(5));
			    		    int endY = Integer.parseInt(matcher.group(6));
			    		    int red = Integer.parseInt(matcher.group(7));
			    		    int green = Integer.parseInt(matcher.group(8));
			    		    int blue = Integer.parseInt(matcher.group(9));

			    		    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			    		    
			    		}

			        } else if (row.contains("Rectangle")) {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Rectangle selected:UpperLeftPoint=Point: \\((-?\\d+),(-?\\d+)\\),widht=(\\d+),height=(\\d+),edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row); 

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int upperLeftX = Integer.parseInt(matcher.group(3));
			        	    int upperLeftY = Integer.parseInt(matcher.group(4));
			        	    int width = Integer.parseInt(matcher.group(5));
			        	    int height = Integer.parseInt(matcher.group(6));
			        	    int edgeRed = Integer.parseInt(matcher.group(7));
			        	    int edgeGreen = Integer.parseInt(matcher.group(8));
			        	    int edgeBlue = Integer.parseInt(matcher.group(9));
			        	    int innerRed = Integer.parseInt(matcher.group(10));
			        	    int innerGreen = Integer.parseInt(matcher.group(11));
			        	    int innerBlue = Integer.parseInt(matcher.group(12));

			        	    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	}

			        } else if (row.contains("Hexagon")) {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) HexagonAdapter selected:Hexagon:Point: \\((-?\\d+),(-?\\d+)\\),(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row); 

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int x = Integer.parseInt(matcher.group(3));
			        	    int y = Integer.parseInt(matcher.group(4));
			        	    int radius = Integer.parseInt(matcher.group(5));
			        	    int edgeRed = Integer.parseInt(matcher.group(6));
			        	    int edgeGreen = Integer.parseInt(matcher.group(7));
			        	    int edgeBlue = Integer.parseInt(matcher.group(8));
			        	    int innerRed = Integer.parseInt(matcher.group(9));
			        	    int innerGreen = Integer.parseInt(matcher.group(10));
			        	    int innerBlue = Integer.parseInt(matcher.group(11));

			        	    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	}

			        } else if (row.contains("Circle")) {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Circle selected:Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row); 

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int centerX = Integer.parseInt(matcher.group(3));
			        	    int centerY = Integer.parseInt(matcher.group(4));
			        	    int radius = Integer.parseInt(matcher.group(5));
			        	    int edgeRed = Integer.parseInt(matcher.group(6));
			        	    int edgeGreen = Integer.parseInt(matcher.group(7));
			        	    int edgeBlue = Integer.parseInt(matcher.group(8));
			        	    int innerRed = Integer.parseInt(matcher.group(9));
			        	    int innerGreen = Integer.parseInt(matcher.group(10));
			        	    int innerBlue = Integer.parseInt(matcher.group(11));

			        	    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	}

			        } else if (row.contains("Donut")) {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Donut selected:Center=Point: \\((-?\\d+),(-?\\d+)\\), radius=(\\d+),inner radius=(\\d+), edge color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\],inner color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row); 

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int centerX = Integer.parseInt(matcher.group(3));
			        	    int centerY = Integer.parseInt(matcher.group(4));
			        	    int radius = Integer.parseInt(matcher.group(5));
			        	    int innerRadius = Integer.parseInt(matcher.group(6));
			        	    int edgeRed = Integer.parseInt(matcher.group(7));
			        	    int edgeGreen = Integer.parseInt(matcher.group(8));
			        	    int edgeBlue = Integer.parseInt(matcher.group(9));
			        	    int innerRed = Integer.parseInt(matcher.group(10));
			        	    int innerGreen = Integer.parseInt(matcher.group(11));
			        	    int innerBlue = Integer.parseInt(matcher.group(12));
			        	    
			        	    
			        	    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	    
			        	}

			        } else
			        {
			        	Pattern pattern = Pattern.compile("Click:\\((-?\\d+),(-?\\d+)\\) Point selected:Point: \\((-?\\d+),(-?\\d+)\\),color:java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			        	Matcher matcher = pattern.matcher(row); 

			        	if (matcher.find()) {
			        		int clickX = Integer.parseInt(matcher.group(1));
			        	    int clickY = Integer.parseInt(matcher.group(2));
			        	    int x = Integer.parseInt(matcher.group(3));
			        	    int y = Integer.parseInt(matcher.group(4));
			        	    int red = Integer.parseInt(matcher.group(5));
			        	    int green = Integer.parseInt(matcher.group(6));
			        	    int blue = Integer.parseInt(matcher.group(7));
			        	    Color color = new Color(red, green, blue);

			        	    Point p = new Point(x, y, color);
			        	    shape = (Shape)p;
			        	    
			        	    this.selection(frame.getView().getGraphics(), clickX, clickY, print); 
			        	    
			        	
			        	}

			        }
			    	print = true;
			    }
			} else if (row.contains("removed")) {
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
						Command cmd = undoCommandsStack.pop();
						cmd.unexecute();
						redoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	for (int i = 0; i < model.getShapes().size(); i++)
			    	{
			    		if(model.get(i).isSelected())
			    		{
			    			RemoveShapeCommand r = new RemoveShapeCommand(model.get(i), model);
			    			r.execute();
			    			undoCommandsStack.push(r);
							redoCommandsStack.clear();
							frame.repaint();
			    		}
			    	}
			    }
			} else if (row.contains("to back")) {
				backFrontLog = false;
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
						Command cmd = undoCommandsStack.pop();
						cmd.unexecute();
						redoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	toBack();
			    }
			    backFrontLog = true;
			} else if (row.contains("to front")) {
				backFrontLog = false;
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
						Command cmd = undoCommandsStack.pop();
						cmd.unexecute();
						redoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	toFront();
			    }
			    backFrontLog = true;
			} else if (row.contains("bring to Back")) {
				backFrontLog = false;
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
						Command cmd = undoCommandsStack.pop();
						cmd.unexecute();
						redoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	bringToBack();
			    }
			    backFrontLog = true;
			} else if (row.contains("bring to Front")) {
				backFrontLog = false;
			    if (row.contains("Undo")) {
			    	if (!undoCommandsStack.isEmpty()) {
						Command cmd = undoCommandsStack.pop();
						cmd.unexecute();
						redoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else if (row.contains("Redo")) {
			    	if (!redoCommandsStack.isEmpty()) {
						Command cmd = redoCommandsStack.pop();
						cmd.execute();
						undoCommandsStack.push(cmd);
						redoUndoObserver.onChange(undoCommandsStack, redoCommandsStack);
						frame.repaint();
					}
			    } else {
			    	bringToFront();
			    }
			    backFrontLog = true;
			}
			
			logCounter++;
			
		}
		
		

	}

		
	
	public ArrayList getLog() {
		return log;
	}
	
	

}

package mvc;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Dimension;

public class DrawingFrame extends JFrame {
	
	private DrawingView view = new DrawingView();
	private DrawingController controller;
	
	private JButton btnPoint;
	private JButton btnLine;
	private JButton btnRectangle;
	private JButton btnCircle;
	private JButton btnDonut;
	private JButton btnSelection;
	private JToolBar toolBar;
	private JButton btnDelete; //observer tj. objekat kojem znaci promena stanja nekog drugog objekta
	private JButton btnModify; //observer 
	private JButton btnUndo;
	private JButton btnRedo;
	private JButton btnHexagon;
	private JTextArea textArea;
	private JLabel lblLog;
	private JButton btnSave;
	private JButton btnOpen;
	private JButton btnNext;
	private JButton btnColor;
	private JButton btnInnerColor;
	private JToolBar toolBar2;
	private Color color; //aktivna boja ivice
	private Color innerColor; //aktivna boja unutrasnjosti
	private JButton btnFront;
	private JButton btnBack;
	private JButton btnBringFront;
	private JButton btnBringBack;
	private JButton btnSaveDrawing;
	private JButton btnOpenDrawing;
	
	public DrawingFrame() {
		
		view.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.mouseClicked(e);
			}
		});
		
		setTitle("Aleksa Kulevski IT38-2020");
		setIconImage(new ImageIcon("Icon.png").getImage());
		view.setBorder(new EmptyBorder(5, 5, 5, 5));
		view.setLayout(new BorderLayout(0, 0));
		setContentPane(view);
		setPreferredSize(new Dimension(1000, 400));
			
		getContentPane().setBackground(new Color(255, 255, 255));
		
		toolBar = new JToolBar();
		view.add(toolBar, BorderLayout.NORTH);
		
		btnSelection = new JButton("Selection");
		btnSelection.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				btnSelection.setSelected(true);
				btnPoint.setSelected(false);
				btnLine.setSelected(false);
				btnRectangle.setSelected(false);
				btnCircle.setSelected(false);
				btnDonut.setSelected(false);
				btnHexagon.setSelected(false);
			}
		});
		toolBar.add(btnSelection);
		
		btnPoint = new JButton("Point");
		btnPoint.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				btnPoint.setSelected(true);
				btnSelection.setSelected(false);
				btnLine.setSelected(false);
				btnRectangle.setSelected(false);
				btnCircle.setSelected(false);
				btnDonut.setSelected(false);
				btnHexagon.setSelected(false);
				
			}
		});
		toolBar.add(btnPoint);
		
		btnLine = new JButton("Line");
		btnLine.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				btnLine.setSelected(true);
				btnSelection.setSelected(false);
				btnPoint.setSelected(false);
				btnRectangle.setSelected(false);
				btnCircle.setSelected(false);
				btnDonut.setSelected(false);
				btnHexagon.setSelected(false);
				
			}
		});
		toolBar.add(btnLine);
		
		btnRectangle = new JButton("Rectangle");
		btnRectangle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				btnRectangle.setSelected(true);
				btnSelection.setSelected(false);
				btnPoint.setSelected(false);
				btnLine.setSelected(false);
				btnCircle.setSelected(false);
				btnDonut.setSelected(false);
				btnHexagon.setSelected(false);
				
			}
		});
		toolBar.add(btnRectangle);
		
		btnCircle = new JButton("Circle");
		btnCircle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				btnCircle.setSelected(true);
				btnSelection.setSelected(false);
				btnPoint.setSelected(false);
				btnLine.setSelected(false);
				btnRectangle.setSelected(false);
				btnDonut.setSelected(false);
				btnHexagon.setSelected(false);
				
			}
		});
		toolBar.add(btnCircle);
		
		btnDonut = new JButton("Donut");
		btnDonut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				btnDonut.setSelected(true);
				btnSelection.setSelected(false);
				btnPoint.setSelected(false);
				btnLine.setSelected(false);
				btnRectangle.setSelected(false);
				btnCircle.setSelected(false);
				btnHexagon.setSelected(false);
				
			}
		});
		toolBar.add(btnDonut);
		
		btnModify = new JButton("Modify");
		btnModify.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(btnModify.isEnabled())
				{
					controller.modify();
					btnModify.setSelected(true);
					btnDonut.setSelected(false);
					btnPoint.setSelected(false);
					btnLine.setSelected(false);
					btnRectangle.setSelected(false);
					btnCircle.setSelected(false);
					btnHexagon.setSelected(false);
				}
				else
				{
					JOptionPane.showMessageDialog(btnModify, "You can only modify one shape at a time!");
				}
				
			}
		});
		
		btnHexagon = new JButton("Hexagon");
		btnHexagon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				btnHexagon.setSelected(true);
				btnSelection.setSelected(false);
				btnPoint.setSelected(false);
				btnLine.setSelected(false);
				btnRectangle.setSelected(false);
				btnCircle.setSelected(false);
				btnDonut.setSelected(false);
				
			}
		});
		toolBar.add(btnHexagon);
		
		btnSaveDrawing = new JButton("Save drawing");
		btnSaveDrawing.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					controller.saveDrawing();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		btnOpenDrawing = new JButton("Open drawing");
		btnOpenDrawing.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					controller.openDrawing();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		toolBar.add(btnOpenDrawing);
		toolBar.add(btnSaveDrawing);

		toolBar.add(btnModify);
		
		btnDelete = new JButton("Delete");
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				controller.delete();
				
				btnHexagon.setSelected(false);
				btnDelete.setSelected(true);
				btnPoint.setSelected(false);
				btnLine.setSelected(false);
				btnRectangle.setSelected(false);
				btnCircle.setSelected(false);
				btnDonut.setSelected(false);
			}
		});
		
		toolBar.add(btnDelete);
		
		btnUndo = new JButton("Undo");
		btnUndo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				controller.undo();
			}
		});
		toolBar.add(btnUndo);
		
		btnRedo = new JButton("Redo");
		btnRedo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				controller.redo();
			}
		});
		toolBar.add(btnRedo);
		
		textArea = new JTextArea(5,10);
		textArea.setEditable(false);

		JScrollPane sp = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		view.add(sp, BorderLayout.SOUTH);
		
		lblLog = new JLabel("Log");
		lblLog.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblLog.setHorizontalAlignment(SwingConstants.LEFT);
		lblLog.setVerticalAlignment(SwingConstants.BOTTOM);
		view.add(lblLog, BorderLayout.CENTER);
		
		btnSave = new JButton("Save log");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
					controller.saveLog();
				
			}
		});
		toolBar.add(btnSave);
		
		btnOpen = new JButton("Open log");
		btnOpen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					controller.openLog();
				}
				 catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		toolBar.add(btnOpen);
		
		btnNext = new JButton("Next");
		btnNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					controller.loadNext();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		toolBar.add(btnNext);
		
		toolBar2 = new JToolBar();
		toolBar2.setOrientation(SwingConstants.VERTICAL);
		view.add(toolBar2, BorderLayout.EAST);
		
		btnInnerColor = new JButton("Inner color");
		btnInnerColor.setBackground(new Color(200, 228, 113));
		innerColor = btnInnerColor.getBackground();
		btnInnerColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				innerColor = JColorChooser.showDialog(null, "Choose a color", Color.GRAY);
				btnInnerColor.setBackground(innerColor);
			}
		});
		toolBar2.add(btnInnerColor);
		
		btnColor = new JButton("Color");
		btnColor.setBackground(new Color(158, 33, 224));
		color = btnColor.getBackground();
		btnColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				color = JColorChooser.showDialog(null, "Choose a color", Color.BLUE);
				btnColor.setBackground(color);
			}
		});
		btnColor.setHorizontalAlignment(SwingConstants.RIGHT);
		toolBar2.add(btnColor);
		
		btnFront = new JButton("To Front");
		btnFront.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.toFront();
			}
		});
		toolBar2.add(btnFront);
		
		btnBack = new JButton("To Back");
		btnBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.toBack();
			}
		});
		toolBar2.add(btnBack);
		
		btnBringFront = new JButton("Bring to Front");
		btnBringFront.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.bringToFront();
			}
		});
		toolBar2.add(btnBringFront);
		
		btnBringBack = new JButton("Bring to Back");
		btnBringBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.bringToBack();
				//ako prosledimo 0 metodi, hocemo da se ispisuje u log
			}
		});
		toolBar2.add(btnBringBack);
		
		btnModify.setEnabled(false);
		btnDelete.setEnabled(false);
		btnBack.setEnabled(false);
		btnBringBack.setEnabled(false);
		btnFront.setEnabled(false);
		btnBringFront.setEnabled(false);
		btnRedo.setEnabled(false);
		btnUndo.setEnabled(false);
	}

	public JButton getBtnHexagon() {
		return btnHexagon;
	}

	public JButton getBtnPoint() {
		return btnPoint;
	}

	public DrawingController getController() {
		return controller;
	}

	public JButton getBtnFront() {
		return btnFront;
	}

	public JButton getBtnBack() {
		return btnBack;
	}

	public JButton getBtnBringFront() {
		return btnBringFront;
	}

	public JButton getBtnBringBack() {
		return btnBringBack;
	}

	public JButton getBtnLine() {
		return btnLine;
	}

	public JButton getBtnRectangle() {
		return btnRectangle;
	}


	public JButton getBtnCircle() {
		return btnCircle;
	}


	public JButton getBtnDonut() {
		return btnDonut;
	}


	public JButton getBtnSelection() {
		return btnSelection;
	}

	public JToolBar getToolBar() {
		return toolBar;
	}

	public void setToolBar(JToolBar toolBar) {
		this.toolBar = toolBar;
	}

	public void setController(DrawingController controller) {
		this.controller = controller;
	}

	public DrawingView getView() {
		return view;
	}

	public JButton getBtnDelete() {
		return btnDelete;
	}

	public JButton getBtnModify() {
		return btnModify;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public Color getColor() {
		return color;
	}

	public Color getInnerColor() {
		return innerColor;
	}

	public JButton getBtnRedo() {
		return btnRedo;
	}
	
	public JButton getBtnUndo() {
		return btnUndo;
	}
	
	public JButton getBtnNext() {
		return btnNext;
	}
	
	
	

	
	}
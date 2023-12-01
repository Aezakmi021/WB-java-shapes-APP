package drawing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import geometry.Circle;
import geometry.Donut;
import geometry.Line;
import geometry.Point;
import geometry.Rectangle;
import geometry.Shape;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;

public class FrmDrawing extends JFrame {
	
	private final int OPERATION_DRAWING = 1;
	private final int OPERATION_EDIT_DELETE = 0;
	private int activeOperation = OPERATION_DRAWING;

	private JPanel contentPane;
	
	private PnlDrawing pnlDrawing = new PnlDrawing();
	private ButtonGroup btnsOperations = new ButtonGroup();
	private ButtonGroup btnsShapes = new ButtonGroup();
	private JToggleButton btnDrawing = new JToggleButton("Drawing");
	private JToggleButton btnSelect = new JToggleButton("Select");
	private JToggleButton btnEdit = new JToggleButton("Edit");
	private JToggleButton btnRemove = new JToggleButton("Remove");
	private JToggleButton btnPoint = new JToggleButton("Point");
	private JToggleButton btnLine = new JToggleButton("Line");
	private JToggleButton btnRectangle = new JToggleButton("Rectangle");
	private JToggleButton btnCircle = new JToggleButton("Circle");
	private JToggleButton btnDonut = new JToggleButton("Donut");
	private JToggleButton btnColorEdge = new JToggleButton("Edge");
	private JToggleButton btnColorInner = new JToggleButton("Inner");
	
	
	private Color edgeColor = Color.BLACK, innerColor = Color.WHITE;
	boolean lineWaitingForEndPoint = false;
	private Point startPoint;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmDrawing frame = new FrmDrawing();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FrmDrawing() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("IT38/2020 - Aleksa Kulevski");
		setBounds(100, 100, 1011, 714);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(1100, 700));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		pnlDrawing.addMouseListener(pnlDrawingClickListener());
		contentPane.add(pnlDrawing, BorderLayout.CENTER);
		Border blackline = BorderFactory.createLineBorder(Color.black);
		String spaces = "         ";
		pnlDrawing.add(new JLabel(spaces + "Border to JPanel" + spaces));  
	      pnlDrawing.setBorder(blackline);
		
		JPanel pnlLeft = new JPanel();
		contentPane.add(pnlLeft, BorderLayout.WEST);
		GridBagLayout gbl_pnlLeft = new GridBagLayout();
		gbl_pnlLeft.columnWidths = new int[]{71, 61, 0};
		gbl_pnlLeft.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pnlLeft.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_pnlLeft.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pnlLeft.setLayout(gbl_pnlLeft);
		btnsOperations.add(btnDrawing);
		
		
		
		
		btnDrawing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOperationDrawing();
				
			}
		});
		
		GridBagConstraints gbc_btnDrawing = new GridBagConstraints();
		gbc_btnDrawing.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnDrawing.insets = new Insets(0, 0, 5, 5);
		gbc_btnDrawing.gridx = 0;
		gbc_btnDrawing.gridy = 0;
		pnlLeft.add(btnDrawing, gbc_btnDrawing);
		btnsOperations.add(btnSelect);
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOperationEditDelete();
			}
		});
		
		
		GridBagConstraints gbc_btnSelect = new GridBagConstraints();
		gbc_btnSelect.insets = new Insets(0, 0, 5, 0);
		gbc_btnSelect.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSelect.gridx = 1;
		gbc_btnSelect.gridy = 0;
		pnlLeft.add(btnSelect, gbc_btnSelect);
		
		btnEdit = new JToggleButton("Edit");
		GridBagConstraints gbc_btnEdit = new GridBagConstraints();
		gbc_btnEdit.insets = new Insets(0, 0, 5, 5);
		gbc_btnEdit.gridx = 0;
		gbc_btnEdit.gridy = 2;
		pnlLeft.add(btnEdit, gbc_btnEdit);
		btnEdit.addActionListener(btnEditClickListener());
		
		btnRemove = new JToggleButton("Remove");
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 2;
		pnlLeft.add(btnRemove, gbc_btnRemove);
		setOperationDrawing();
		btnRemove.addActionListener(btnRemoveClickListener());
		
		btnPoint = new JToggleButton("Point");
		btnPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnPoint.setSelected(true);
			}
		});
		GridBagConstraints gbc_btnPoint = new GridBagConstraints();
		gbc_btnPoint.insets = new Insets(0, 0, 5, 5);
		gbc_btnPoint.gridx = 0;
		gbc_btnPoint.gridy = 5;
		pnlLeft.add(btnPoint, gbc_btnPoint);
		btnsShapes.add(btnPoint);
		
		btnLine = new JToggleButton("Line");
		btnLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLine.setSelected(true);
			}
		});
		GridBagConstraints gbc_btnLine = new GridBagConstraints();
		gbc_btnLine.insets = new Insets(0, 0, 5, 0);
		gbc_btnLine.gridx = 1;
		gbc_btnLine.gridy = 5;
		pnlLeft.add(btnLine, gbc_btnLine);
		btnsShapes.add(btnLine);
		
		btnRectangle = new JToggleButton("Rectangle");
		btnRectangle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRectangle.setSelected(true);
			}
		});
		GridBagConstraints gbc_btnRectangle = new GridBagConstraints();
		gbc_btnRectangle.insets = new Insets(0, 0, 5, 5);
		gbc_btnRectangle.gridx = 0;
		gbc_btnRectangle.gridy = 6;
		pnlLeft.add(btnRectangle, gbc_btnRectangle);
		btnsShapes.add(btnRectangle);
		
		btnCircle = new JToggleButton("Circle");
		btnCircle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCircle.setSelected(true);
			}
		});
		GridBagConstraints gbc_btnCircle = new GridBagConstraints();
		gbc_btnCircle.insets = new Insets(0, 0, 5, 0);
		gbc_btnCircle.gridx = 1;
		gbc_btnCircle.gridy = 6;
		pnlLeft.add(btnCircle, gbc_btnCircle);
		btnsShapes.add(btnCircle);
		
		btnDonut = new JToggleButton("Donut");
		btnDonut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDonut.setSelected(true);
			}
		});
		GridBagConstraints gbc_btnDonut = new GridBagConstraints();
		gbc_btnDonut.gridwidth = 2;
		gbc_btnDonut.gridx = 0;
		gbc_btnDonut.gridy = 7;
		pnlLeft.add(btnDonut, gbc_btnDonut);
		btnsShapes.add(btnDonut);
		
		btnDrawing.setSelected(true);
		setOperationDrawing();
		
	}
	
	private MouseAdapter pnlDrawingClickListener() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point mouseClick = new Point(e.getX(), e.getY());
				pnlDrawing.deselect();
				
				if (activeOperation == OPERATION_EDIT_DELETE) {
					pnlDrawing.select(mouseClick);
					return;
				}
				
				if (btnPoint.isSelected()) {
					DlgPoint dlgPoint = new DlgPoint();
					dlgPoint.setPoint(mouseClick);
					//
					dlgPoint.setVisible(true);
					if(dlgPoint.getPoint() != null) pnlDrawing.addShape(dlgPoint.getPoint());
					btnPoint.setSelected(false);
					return;
					
				} else if (btnLine.isSelected()) {
					if(lineWaitingForEndPoint) {
						
						DlgLine dlgLine = new DlgLine();
						Line line = new Line(startPoint,mouseClick);
						dlgLine.setLine(line);
						//
						System.out.println(edgeColor);
						dlgLine.setVisible(true);
						if(dlgLine.getLine()!= null) pnlDrawing.addShape(dlgLine.getLine());
						lineWaitingForEndPoint=false;
						btnLine.setSelected(false);
						return;
					}
					startPoint = mouseClick;
					lineWaitingForEndPoint=true;
					return;
					
		
				} else if (btnRectangle.isSelected()) {
					DlgRectangle dlgRectangle = new DlgRectangle();
					dlgRectangle.setPoint(mouseClick);
					
					dlgRectangle.setVisible(true);
					
					if(dlgRectangle.getRectangle() != null) pnlDrawing.addShape(dlgRectangle.getRectangle());
					btnRectangle.setSelected(false);
					return;
				} else if (btnCircle.isSelected()) {
					DlgCircle dlgCircle = new DlgCircle();
					dlgCircle.setPoint(mouseClick);
					
					dlgCircle.setVisible(true);
					
					if(dlgCircle.getCircle() != null) pnlDrawing.addShape(dlgCircle.getCircle());
					btnCircle.setSelected(false);
					return;
				} else if (btnDonut.isSelected()) {
					DlgDonut dlgDonut = new DlgDonut();
					dlgDonut.setPoint(mouseClick);
					dlgDonut.setColors(edgeColor, innerColor);
					dlgDonut.setVisible(true);
					
					if(dlgDonut.getDonut() != null) pnlDrawing.addShape(dlgDonut.getDonut());
					btnDonut.setSelected(false);
					return;
				}
			}
		};
	}
	
	private ActionListener btnEditClickListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = pnlDrawing.getSelected();
				if (index == -1) return;
				
				Shape shape = pnlDrawing.getShape(index);
				
				if (shape instanceof Point) {
					DlgPoint dlgPoint = new DlgPoint();
					dlgPoint.setPoint((Point)shape);
					dlgPoint.setVisible(true);
					
					if(dlgPoint.getPoint() != null) {
						pnlDrawing.setShape(index, dlgPoint.getPoint());
						pnlDrawing.repaint();
					}
				} else if (shape instanceof Line) {
					DlgLine dlgLine = new DlgLine();
					dlgLine.setLine((Line)shape);
					dlgLine.setVisible(true);
					
					if(dlgLine.getLine() != null) {
						pnlDrawing.setShape(index, dlgLine.getLine());
						pnlDrawing.repaint();
					}
				} else if (shape instanceof Rectangle) {
					DlgRectangle dlgRectangle = new DlgRectangle();
					dlgRectangle.setRectangle((Rectangle)shape);
					dlgRectangle.setVisible(true);
					
					if(dlgRectangle.getRectangle() != null) {
						pnlDrawing.setShape(index, dlgRectangle.getRectangle());
						pnlDrawing.repaint();
					}
				
				}else if (shape instanceof Donut) {
						DlgDonut dlgDonut = new DlgDonut();
						dlgDonut.setDonut((Donut)shape);
						dlgDonut.setVisible(true);
						
						if(dlgDonut.getDonut() != null) {
							pnlDrawing.setShape(index, dlgDonut.getDonut());
							pnlDrawing.repaint();
						}
				} else if (shape instanceof Circle) {
					DlgCircle dlgCircle = new DlgCircle();
					dlgCircle.setCircle((Circle)shape);
					dlgCircle.setVisible(true);
					
					if(dlgCircle.getCircle() != null) {
						pnlDrawing.setShape(index, dlgCircle.getCircle());
						pnlDrawing.repaint();
					}
				} 
			}
		};
	}
	
	private ActionListener btnRemoveClickListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pnlDrawing.isEmpty()) return;
				if (JOptionPane.showConfirmDialog(null, "Are you sure?\nDeleting the shape cannot be undone?", "Yes", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) pnlDrawing.removeSelected();
			}
		};
	}
	
	//ovde
	
	private void setOperationDrawing() {
		activeOperation = OPERATION_DRAWING;
		
		pnlDrawing.deselect();
		
		btnEdit.setEnabled(false);
		btnRemove.setEnabled(false);
		
		btnPoint.setEnabled(true);
		btnLine.setEnabled(true);
		btnRectangle.setEnabled(true);
		btnCircle.setEnabled(true);
		btnDonut.setEnabled(true);
		
		btnColorEdge.setEnabled(true);
		btnColorInner.setEnabled(true);
	}
	
	private void setOperationEditDelete() { //makes it so the buttons are not usable in the different mode
		activeOperation = OPERATION_EDIT_DELETE;
		
		btnEdit.setEnabled(true);
		btnRemove.setEnabled(true);
		
		btnPoint.setEnabled(false);
		btnLine.setEnabled(false);
		btnRectangle.setEnabled(false);
		btnCircle.setEnabled(false);
		btnDonut.setEnabled(false);
		
		btnColorEdge.setEnabled(false);
		btnColorInner.setEnabled(false);
	}
	
	

}
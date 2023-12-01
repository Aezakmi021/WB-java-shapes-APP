package drawing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import geometry.Line;
import geometry.Point;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DlgLine extends JDialog {

	private final JPanel pnlCenter = new JPanel();
	private JTextField txtStartX;
	private JTextField txtStartY;
	private JTextField txtEndX;
	private JTextField txtEndY;
	private Color color = null;
	private Line line = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DlgLine dialog = new DlgLine();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DlgLine() {
		setBounds(100, 100, 364, 307);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		pnlCenter.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pnlCenter, BorderLayout.CENTER);
		GridBagLayout gbl_pnlCenter = new GridBagLayout();
		gbl_pnlCenter.columnWidths = new int[]{0, 0, 0, 0};
		gbl_pnlCenter.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pnlCenter.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_pnlCenter.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pnlCenter.setLayout(gbl_pnlCenter);
		
		{
			JLabel lblStartX = new JLabel("Starting X:");
			GridBagConstraints gbc_lblStartX = new GridBagConstraints();
			gbc_lblStartX.anchor = GridBagConstraints.EAST;
			gbc_lblStartX.insets = new Insets(0, 0, 5, 5);
			gbc_lblStartX.gridx = 1;
			gbc_lblStartX.gridy = 1;
			pnlCenter.add(lblStartX, gbc_lblStartX);
		}
		{
			txtStartX = new JTextField();
			GridBagConstraints gbc_txtStartX = new GridBagConstraints();
			gbc_txtStartX.anchor = GridBagConstraints.WEST;
			gbc_txtStartX.insets = new Insets(0, 0, 5, 0);
			gbc_txtStartX.gridx = 2;
			gbc_txtStartX.gridy = 1;
			pnlCenter.add(txtStartX, gbc_txtStartX);
			txtStartX.setColumns(10);
		}
		{
			JLabel lblStartY = new JLabel("Starting Y:");
			GridBagConstraints gbc_lblStartY = new GridBagConstraints();
			gbc_lblStartY.anchor = GridBagConstraints.EAST;
			gbc_lblStartY.insets = new Insets(0, 0, 5, 5);
			gbc_lblStartY.gridx = 1;
			gbc_lblStartY.gridy = 2;
			pnlCenter.add(lblStartY, gbc_lblStartY);
		}
		{
			txtStartY = new JTextField();
			GridBagConstraints gbc_txtStartY = new GridBagConstraints();
			gbc_txtStartY.anchor = GridBagConstraints.WEST;
			gbc_txtStartY.insets = new Insets(0, 0, 5, 0);
			gbc_txtStartY.gridx = 2;
			gbc_txtStartY.gridy = 2;
			pnlCenter.add(txtStartY, gbc_txtStartY);
			txtStartY.setColumns(10);
		}
		{
			JLabel lblEndX = new JLabel("End X:");
			GridBagConstraints gbc_lblEndX = new GridBagConstraints();
			gbc_lblEndX.anchor = GridBagConstraints.EAST;
			gbc_lblEndX.insets = new Insets(0, 0, 5, 5);
			gbc_lblEndX.gridx = 1;
			gbc_lblEndX.gridy = 3;
			pnlCenter.add(lblEndX, gbc_lblEndX);
		}
		{
			txtEndX = new JTextField();
			GridBagConstraints gbc_txtEndX = new GridBagConstraints();
			gbc_txtEndX.anchor = GridBagConstraints.WEST;
			gbc_txtEndX.insets = new Insets(0, 0, 5, 0);
			gbc_txtEndX.gridx = 2;
			gbc_txtEndX.gridy = 3;
			pnlCenter.add(txtEndX, gbc_txtEndX);
			txtEndX.setColumns(10);
		}
		{
			JLabel lblEndY = new JLabel("End Y:");
			GridBagConstraints gbc_lblEndY = new GridBagConstraints();
			gbc_lblEndY.anchor = GridBagConstraints.EAST;
			gbc_lblEndY.insets = new Insets(0, 0, 5, 5);
			gbc_lblEndY.gridx = 1;
			gbc_lblEndY.gridy = 4;
			pnlCenter.add(lblEndY, gbc_lblEndY);
		}
		{
			txtEndY = new JTextField();
			GridBagConstraints gbc_txtEndY = new GridBagConstraints();
			gbc_txtEndY.insets = new Insets(0, 0, 5, 0);
			gbc_txtEndY.anchor = GridBagConstraints.WEST;
			gbc_txtEndY.gridx = 2;
			gbc_txtEndY.gridy = 4;
			pnlCenter.add(txtEndY, gbc_txtEndY);
			txtEndY.setColumns(10);
		}
		{
			JButton btnChoseColor = new JButton("Chose Color");
			btnChoseColor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					color = JColorChooser.showDialog(null, "Choose a color", Color.BLUE);
					if (color == null) color = Color.BLACK;
					btnChoseColor.setBackground(color);
					System.out.println(color);
				}
			});
			GridBagConstraints gbc_btnChoseColor = new GridBagConstraints();
			gbc_btnChoseColor.gridx = 2;
			gbc_btnChoseColor.gridy = 6;
			pnlCenter.add(btnChoseColor, gbc_btnChoseColor);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							int newFirstX = Integer.parseInt(txtStartX.getText());
							int newFirstY = Integer.parseInt(txtStartY.getText());
							int newSecondX = Integer.parseInt(txtEndX.getText());
							int newSecondY = Integer.parseInt(txtEndY.getText());

							if(newFirstX < 0 || newFirstY < 0 || newSecondX < 0 || newSecondY < 0) {
								JOptionPane.showMessageDialog(null,"You entered wrong value!", "Error!", JOptionPane.ERROR_MESSAGE);
								return;
							}
							line = new Line(new Point(newFirstX, newFirstY), new Point(newSecondX, newSecondY),false, color);
							dispose();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null, "You entered wrong data type!", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public Line getLine() {
		return line;
	}
	
	public void setStartPoint(Point point) {
		txtStartX.setText("" + point.getX());
		txtStartY.setText("" + point.getY());
	}
	public void setEndPoint(Point point) {
		txtEndX.setText("" + point.getX());
		txtEndY.setText("" + point.getY());
	}
	public void setColor(Color color) {
		this.color = color; //my brackets are all right also dont know what the problem here is
		//System.out.println(edgeColor);
	}

	public void setLine(Line line) {
		txtStartX.setText("" + line.getStartPoint().getX());
		txtStartY.setText("" + line.getStartPoint().getY());
		txtEndX.setText("" + line.getEndPoint().getX());
		txtEndY.setText("" + line.getEndPoint().getY());
		color = line.getColor();
	}

}
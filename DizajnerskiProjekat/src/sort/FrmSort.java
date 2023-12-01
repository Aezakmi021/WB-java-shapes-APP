package sort;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import geometry.Rectangle;

public class FrmSort extends JFrame {

	private JPanel contentPane;
	private DefaultListModel<Rectangle> dlm= new DefaultListModel<Rectangle>();
	private ArrayList <Rectangle> arrayList=new ArrayList<Rectangle>();
	JList<Rectangle> lstSort = new JList<Rectangle>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmSort frame = new FrmSort();
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
	public FrmSort() {
		setTitle("Aleksa Kulevski IT-38/2020");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel pnlCenter = new JPanel();
		contentPane.add(pnlCenter, BorderLayout.CENTER);
		GridBagLayout gbl_pnlCenter = new GridBagLayout();
		gbl_pnlCenter.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pnlCenter.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pnlCenter.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_pnlCenter.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		pnlCenter.setLayout(gbl_pnlCenter);
		
		JScrollPane scrlPanel = new JScrollPane();
		GridBagConstraints gbc_scrlPanel = new GridBagConstraints();
		gbc_scrlPanel.gridheight = 4;
		gbc_scrlPanel.gridwidth = 7;
		gbc_scrlPanel.fill = GridBagConstraints.BOTH;
		gbc_scrlPanel.gridx = 0;
		gbc_scrlPanel.gridy = 0;
		pnlCenter.add(scrlPanel, gbc_scrlPanel);
		
		JList list = new JList();
		list.setModel(dlm);
		scrlPanel.setViewportView(list);
		
		JPanel pnlDown = new JPanel();
		contentPane.add(pnlDown, BorderLayout.SOUTH);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DlgSort dlgSort = new DlgSort();
				dlgSort.setVisible(true);
				if(dlgSort.isOk)
				{
					arrayList.add(dlgSort.getRectangle());
				}
				arrayList.sort(null);
				list.setModel(sort());
				
			}
		});
		pnlDown.add(btnAdd);
		
		JButton btnDelete = new JButton("Cancel");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				
			}
		});
		pnlDown.add(btnDelete);
		
	}
	private DefaultListModel<Rectangle> sort()
			{
				Iterator<Rectangle> iterator = arrayList.iterator();
				DefaultListModel<Rectangle> dlm = new DefaultListModel<Rectangle>();
				while(iterator.hasNext()) {
					dlm.addElement(iterator.next());
				}	
				
				return dlm;
			}
}
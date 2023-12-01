package geometry;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Drawing extends JPanel {

	public static void main(String[] args) {

		JFrame frame = new JFrame("Drawing");
		frame.setSize(800, 600);
		Drawing drawing = new Drawing();
		frame.getContentPane().add(drawing);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
															

	}

	public void paint(Graphics g) {

		Point p = new Point(50, 50, true, Color.BLUE);
		p.draw(g);

		Line l1 = new Line(new Point(70, 70), new Point(70, 200), true, Color.RED);
		l1.draw(g);

		Circle c = new Circle(new Point(200, 120), 40, true, Color.RED, Color.GREEN);
		c.draw(g);

		Rectangle r = new Rectangle(new Point(300, 60), 70, 120, true, Color.BLUE, Color.MAGENTA);
		r.draw(g);

		Donut d = new Donut(new Point(400, 300), 80, 50, true, Color.BLACK, Color.YELLOW);
		d.draw(g);
		
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		shapes.add(p);
		shapes.add(l1);
		shapes.add(c);
		shapes.add(r);
		shapes.add(d);

		Iterator<Shape> it = shapes.iterator();
		while (it.hasNext()) {
			System.out.println("Selected:" + it.next().isSelected());
		}
		shapes.get(2).draw(g);
		
		shapes.get(shapes.size()-1).draw(g);
		
		shapes.get(3).draw(g);
		
		Line l2 = new Line(new Point(450,200), new Point(550,200));
		shapes.add(3, l2);
		shapes.get(3).draw(g);
		
		for (Shape s : shapes) {
			s.draw(g);
		}

		for (Shape s : shapes) {
			if(s instanceof SurfaceShape) {
				s.draw(g);
			}
		}
		
	}

}

package geometry;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Donut extends Circle {

    private int innerRadius;

    public Donut() {}

    public Donut(Point center, int radius, int innerRadius) {
        super(center, radius);
        this.innerRadius = innerRadius;
    }

    public Donut(Point center, int radius, int innerRadius, boolean isSelected) {
        this(center, radius, innerRadius);
        setSelected(isSelected);
    }

    public Donut(Point center, int radius, int innerRadius, Color col) {
        this(center, radius, innerRadius);
        setColor(col);
    }

    public Donut(Point center, int radius, int innerRadius, boolean isSelected, Color col) {
        this(center, radius, innerRadius, isSelected);
        setColor(col);
    }

    public Donut(Point center, int radius, int innerRadius, Color col, Color innerCol) {
        this(center, radius, innerRadius, col);
        setInnerColor(innerCol);
    }

    public Donut(Point center, int radius, int innerRadius, boolean isSelected, Color col, Color innerCol) {
        this(center, radius, innerRadius, isSelected, col);
        setInnerColor(innerCol);
    }

    @Override
    public int compareTo(Object other) {
        if (other instanceof Donut) {
            return (int) (this.area() - ((Donut) other).area());
        }
        return 0;
    }

    @Override
    public void fill(Graphics gfx) {
        Graphics2D g2d = (Graphics2D) gfx.create();
        drawAndFillShape(g2d);
    }

    @Override
    public void draw(Graphics gfx) {
        Graphics2D g2d = (Graphics2D) gfx.create();
        drawAndFillShape(g2d);
        drawSelection(gfx);
    }

    private void drawAndFillShape(Graphics2D g2d) {
        Area fullDonut = createEllipseArea(getRadius());
        Area hole = createEllipseArea(innerRadius);
        fullDonut.subtract(hole);
        g2d.setColor(getInnerColor());
        g2d.fill(fullDonut);
        g2d.setColor(getColor());
        g2d.draw(fullDonut);
    }

    private Area createEllipseArea(int radius) {
        return new Area(new Ellipse2D.Double(
                getCenter().getX() - radius, getCenter().getY() - radius,
                radius * 2, radius * 2));
    }

    private void drawSelection(Graphics gfx) {
        if (isSelected()) {
    		gfx.drawRect(getCenter().getX() + getInnerRadius() - 3, getCenter().getY() - 3, 6, 6);
    		gfx.drawRect(getCenter().getX() - 3, getCenter().getY() - 3, 6, 6);
    		gfx.drawRect(getCenter().getX()- 3, getCenter().getY() + getInnerRadius() - 3, 6, 6);
    		gfx.drawRect(getCenter().getX() - getInnerRadius() - 3, getCenter().getY() - 3, 6, 6);
    		gfx.drawRect(getCenter().getX() - 3, getCenter().getY() - getInnerRadius() - 3, 6, 6);
    		gfx.setColor(Color.BLUE);
        	gfx.drawRect(getCenter().getX() - getRadius() - 3, getCenter().getY() - 3, 6, 6);
        	gfx.drawRect(getCenter().getX() - 3, getCenter().getY() - 3, 6, 6);
    		gfx.drawRect(getCenter().getX() + getRadius() - 3, getCenter().getY() - 3, 6, 6);
    		gfx.drawRect(getCenter().getX() - 3, getCenter().getY() - getRadius() - 3, 6, 6);
    		gfx.drawRect(getCenter().getX()- 3, getCenter().getY() + getRadius() - 3, 6, 6);
    		gfx.setColor(Color.BLUE);
        }
    }

    public double area() {
        return super.area() - Math.PI * innerRadius * innerRadius;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Donut) {
            Donut other = (Donut) obj;
            return getCenter().equals(other.getCenter()) &&
                   getRadius() == other.getRadius() &&
                   innerRadius == other.innerRadius;
        }
        return false;
    }

    public boolean contains(int x, int y) {
        double dist = getCenter().distance(x, y);
        return super.contains(x, y) && dist > innerRadius;
    }

    public boolean contains(Point p) {
        return contains(p.getX(), p.getY());
    }

    public int getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(int innerRadius) {
        this.innerRadius = innerRadius;
    }

    public Donut clone() {
        Point newCenter = new Point(this.getCenter().getX(), this.getCenter().getY());
        Donut clone = new Donut(newCenter, this.getRadius(), this.innerRadius, this.isSelected(), this.getColor(), this.getInnerColor());

        return clone;
    }
    
    public String toString() {
    	
    	return "Center=" + getCenter() + ", radius=" + getRadius() + "," + "inner radius=" + innerRadius + ", edge color:" + getColor() + "," + "inner color:" + getInnerColor();
    }


}

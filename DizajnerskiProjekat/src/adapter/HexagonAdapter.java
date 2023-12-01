package adapter;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Objects;

import geometry.Point;
import geometry.SurfaceShape;
import hexagon.Hexagon;

public class HexagonAdapter extends SurfaceShape {

    private Hexagon hexagon;
    private Point center = new Point();

    public HexagonAdapter() {
    	
    }
    
    public HexagonAdapter(Point center, int radius, Color color, Color innerColor) {
        this.center = center;
        this.hexagon = new Hexagon(center.getX(), center.getY(), radius);
        this.hexagon.setBorderColor(color);
        this.hexagon.setAreaColor(innerColor);
    }
    
    public HexagonAdapter(Point center, int radius, Boolean selected, Color color, Color innerColor) {
    	this(center, radius, color, innerColor);
        this.hexagon.setSelected(selected);
    }

    @Override
    public void moveBy(int moveXBy, int moveYBy) {
        center.setX(center.getX() + moveXBy);
        center.setY(center.getY() + moveYBy);
        hexagon.setX(hexagon.getX() + moveXBy);
        hexagon.setY(hexagon.getY() + moveYBy);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Hexagon) {
            Hexagon h = (Hexagon) o;
            return (int) (hexagon.getR() - h.getR());
        } else {
            return 0;
        }
    }

    public boolean contains(Point P) {
        return hexagon.doesContain(P.getX(), P.getY());
    }

    @Override
    public void fill(Graphics s) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean contains(int x, int y) {
        return hexagon.doesContain(x, y);
    }

    @Override
    public void draw(Graphics gfx) {
        hexagon.paint(gfx);
        hexagon.setSelected(isSelected());
    }

    @Override
    public void setSelected(boolean selected) {
        hexagon.setSelected(selected);
    }

    public boolean isSelected() {
        return hexagon.isSelected();
    }

    public Hexagon getHexagon() {
        return hexagon;
    }

    public void setHexagon(Hexagon hexagon) {
        this.hexagon = hexagon;
    }

    public Color getColor() {
        return hexagon.getBorderColor();
    }

    public Color getInnerColor() {
        return hexagon.getAreaColor();
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void setX(int x) {
        center.setX(x);
    }

    public int getX() {
        return center.getX();
    }

    public void setY(int y) {
        center.setY(y);
    }

    public int getY() {
        return center.getY();
    }

    public int getRadius() {
        return hexagon.getR();
    }

    public void setRadius(int radius) {
        hexagon.setR(radius);
    }

    public HexagonAdapter clone() {
        HexagonAdapter hexagonClone = new HexagonAdapter(
            new Point(center.getX(), center.getY()),
            hexagon.getR(),
            hexagon.isSelected(),
            hexagon.getBorderColor(),
            hexagon.getAreaColor()
        );
        return hexagonClone;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(center, hexagon);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HexagonAdapter other = (HexagonAdapter) obj;
        // Objects.equals handles null values gracefully
        return Objects.equals(center, other.center);
    }

    public String toString() {
        return "Hexagon:" + center + "," + getRadius() + "," + " edge color:" + getColor() + ",inner color:" + getInnerColor();
    }
   
}

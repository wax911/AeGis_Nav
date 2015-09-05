package aegis.com.aegis.activity;

public class Circle {

	private XYPoint center;
	private float radius;

	public Circle(XYPoint center, float radius) {
		setCenter(center);
		setRadius(radius);
	}

	public Circle(int x, int y, float radius) {
		this(new XYPoint(x,y),radius);
	}

	public Circle(float radius) {
		this(0,0,radius);
	}

	public Circle() {
		this(0);
	}

	public XYPoint getCenter() {
		return center;
	}

	public float getCenterX() {
		return center.getX();
	}

	public float getCenterY() {
		return center.getY();
	}

	public float getRadius() {
		return radius;
	}

	public float getDiameter() {
		return 2 * getRadius();
	}

	public double getCircumference() {
		return Math.PI * getDiameter();
	}

	public double getArea() {
		return Math.PI * Math.pow(getRadius(),2);
	}

	public boolean overlap(Circle otherCircle) {
		return (getRadius() + otherCircle.getRadius()) >= 
			XYPoint.distance(getCenter(),otherCircle.getCenter());
	}

	public void setCenter(XYPoint center) {
		this.center = center;
	}

	public void setCenterX(int x) {
		center.setX(x);
	}

	public void setCenterY(int y) {
		center.setY(y);
	}

	public void setCenter(int x, int y) {
		setCenterX(x);
		setCenterY(y);
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public String toString() {
		return "Circle Centre at: " + center + " and Radius of: " + radius;
	}

}
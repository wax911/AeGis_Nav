package aegis.com.aegis.activity;

public class XYPoint {

	private float x;
	private float y;

	public XYPoint(float x, float y) {
		setX(x);
		setY(y);
	}

	public XYPoint() {
		this(0,0);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

        public static double distance(XYPoint p1, XYPoint p2) {
                return Math.sqrt( Math.pow( p1.getX()-p2.getX() ,2) +
                                  Math.pow( p1.getY()-p2.getY() ,2));
        }

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String toString() {
		return "(" + getX() + "," + getY() + ")";
	}

}

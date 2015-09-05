package aegis.com.aegis.activity;

public class ApolloniusSolver {

public static Circle solveApollonius(Circle c1, Circle c2, Circle c3, int s1, int s2, int s3){
float x1 = c1.getCenterX();
float y1 = c1.getCenterY();
float r1 = c1.getRadius();
float x2 = c2.getCenterX();
float y2 = c2.getCenterY();
float r2 = c2.getRadius();
float x3 = c3.getCenterX();
float y3 = c3.getCenterY();
float r3 = c3.getRadius();

float v11 = 2*x2 - 2*x1;
float v12 = 2*y2 - 2*y1;
float v13 = x1*x1 - x2*x2 + y1*y1 - y2*y2 - r1*r1 + r2*r2;
float v14 = 2*s2*r2 - 2*s1*r1;

float v21 = 2*x3 - 2*x2;
float v22 = 2*y3 - 2*y2;
float v23 = x2*x2 - x3*x3 + y2*y2 - y3*y3 - r2*r2 + r3*r3;
float v24 = 2*s3*r3 - 2*s2*r2;

float w12 = v12/v11;
float w13 = v13/v11;
float w14 = v14/v11;

float w22 = v22/v21-w12;
float w23 = v23/v21-w13;
float w24 = v24/v21-w14;

float P = -w23/w22;
float Q = w24/w22;
float M = -w12*P-w13;
float N = w14 - w12*Q;

float a = N*N + Q*Q - 1;
float b = 2*M*N - 2*N*x1 + 2*P*Q - 2*Q*y1 + 2*s1*r1;
float c = x1*x1 + M*M - 2*M*x1 + P*P + y1*y1 - 2*P*y1 - r1*r1;

float D = b * b - 4 * a * c;
float rs = (float) ((-b - Math.sqrt(D)) / (2 * a));
float xs = M + N * rs;
float ys = P + Q * rs;

return new Circle(new XYPoint(xs,ys), rs);
}

}

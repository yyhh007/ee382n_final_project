package ee382n;

public class Point2D {
    public double X;
    public double Y;
    public Point2D(double x, double y)
    {
        X = x;
        Y = y;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public void setX(double x) {
        X = x;
    }

    public void setY(double y) {
        Y = y;
    }
}

class R_and_v{
    public int R;
    public Point2D init_v;
    public R_and_v(int r, Point2D v){
        init_v = v;
        R=r;
    }

    public int getR() {
        return R;
    }

    public Point2D getInit_v() {
        return init_v;
    }

    public void setInit_v(Point2D init_v) {
        this.init_v = init_v;
    }

    public void setR(int r) {
        R = r;
    }
}

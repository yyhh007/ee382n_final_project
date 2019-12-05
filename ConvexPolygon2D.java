package ee382n;

//import static ee382n.GeometryHelper.IsEqual;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ConvexPolygon2D {
    public Point2D[] Corners;
    static double EquityTolerance = 0.000000001;

    public static void main(String[] args) {
        double[][] subjPoints = {{50, 150}, {200, 50}, {350, 150}, {350, 300},
                {250, 300}, {200, 250}, {150, 350}, {100, 250}, {100, 200}};

        double[][] clipPoints = {{100, 100}, {300, 100}, {300, 300}, {100, 300}};

        Point2D[] p1_corners = new Point2D[]{new Point2D(50, 150)
                , new Point2D(200, 50)
                , new Point2D(350, 150)
                , new Point2D(350, 300)
                , new Point2D(250, 300)
                , new Point2D(200, 250)
                , new Point2D(150, 350)
                , new Point2D(100, 250)
                , new Point2D(100, 200)};

        Point2D[] p2_corners = new Point2D[]{new Point2D(100, 100)
                , new Point2D(300, 100)
                , new Point2D(300, 300)
                , new Point2D(100, 300)};
        ConvexPolygon2D poly1 = new ConvexPolygon2D(p1_corners);
        ConvexPolygon2D poly2 = new ConvexPolygon2D(p2_corners);

        ConvexPolygon2D poly_return;
        poly_return = GetIntersectionOfPolygons(poly1,poly2);
        //Get safe area sample code:
        Point2D[] p3_corners = new Point2D[]{new Point2D(100, 100)
                , new Point2D(100, 300)
                , new Point2D(300, 100)
                , new Point2D(300, 300)
                , new Point2D(200, 400)};
        //ConvexPolygon2D poly3 = new ConvexPolygon2D(p3_corners);
        ArrayList<Point2D> safe_input_p3 = new ArrayList<Point2D>(Arrays.asList(p3_corners));
        ConvexPolygon2D poly3_safe;
        poly3_safe = GetSafeArea(OrderClockwise(safe_input_p3));


        //get mid safe and safe area sample code:
        Point2D ori_output_v = new Point2D(111,111);
        update_output_mid_safe(poly3_safe,ori_output_v);
        Point2D berycenter_output;
        berycenter_output = computeCentroid(poly2);
        double test_range = GetMaxRange(poly3_safe);
        System.out.println("Received");
    }
    public static void update_output_mid_safe(ConvexPolygon2D safe_corners, Point2D ori_output_v){

        ArrayList<Point2D> poly_corners = new ArrayList<Point2D>(Arrays.asList(safe_corners.Corners));
        ArrayList<Double> poly_x = new ArrayList<Double>(poly_corners.size());
        ArrayList<Double> poly_y = new ArrayList<Double>(poly_corners.size());
        for(Point2D p :poly_corners ){
            poly_x.add(p.X);
            poly_y.add(p.Y);
        }
        ori_output_v.setX(0.5*(Collections.max(poly_x)+Collections.min(poly_x)));
        ori_output_v.setY(0.5*(Collections.max(poly_y)+Collections.min(poly_y)));

    }

    public static Point2D computeCentroid(ConvexPolygon2D safe_corners)
    {
        ArrayList<Point2D> poly_corners = new ArrayList<Point2D>(Arrays.asList(safe_corners.Corners));
        double centroidX = 0, centroidY = 0;
        double det = 0, tempDet = 0;
        int j = 0;
        int nVertices = poly_corners.size();

        for (int i = 0; i < nVertices; i++)
        {
            // closed polygon
            if (i + 1 == nVertices)
                j = 0;
            else
                j = i + 1;

            // compute the determinant
            tempDet = poly_corners.get(i).X * poly_corners.get(j).Y - poly_corners.get(j).X*poly_corners.get(i).Y;
            det += tempDet;

            centroidX += (poly_corners.get(i).X + poly_corners.get(j).X)*tempDet;
            centroidY += (poly_corners.get(i).Y +poly_corners.get(j).Y)*tempDet;
        }

        // divide by the total mass of the polygon
        centroidX /= 3*det;
        centroidY /= 3*det;

        //centroid->x = centroidX;
        //centroid->y = centroidY;
        return new Point2D(centroidX, centroidY);
    }

    public static double GetMaxRange(ConvexPolygon2D safe_corners)
    {
        ArrayList<Point2D> poly_corners = new ArrayList<Point2D>(Arrays.asList(safe_corners.Corners));
        ArrayList<Double> poly_x = new ArrayList<Double>(poly_corners.size());
        ArrayList<Double> poly_y = new ArrayList<Double>(poly_corners.size());
        for (int i =0; i<poly_corners.size();i++){
            poly_x.add(poly_corners.get(i).X);
            poly_y.add(poly_corners.get(i).Y);
        }
        double x_range =Math.abs(Collections.max(poly_x)-Collections.min(poly_x));
        double y_range =Math.abs(Collections.max(poly_y)-Collections.min(poly_y));



        return Math.max(x_range,y_range);

    }

    public static ConvexPolygon2D GetSafeArea(Point2D[] p_corners)
    {
        ArrayList<Point2D> poly_corners = new ArrayList<Point2D>(Arrays.asList(p_corners));

        ArrayList<Point2D> init_poly = new ArrayList<Point2D>(poly_corners.size()-1);
        ArrayList<ArrayList<Point2D>> n_minors_f_comb = new ArrayList<ArrayList<Point2D>>(poly_corners.size());





        for (int i =0; i<poly_corners.size(); i++){
            ArrayList<Point2D> n_minors_f_poly = new ArrayList<Point2D>(poly_corners.size()-1);
            for(int j = 0; j<i; j++){
                n_minors_f_poly.add(poly_corners.get(j));
            }
            for(int k=i+1;k<poly_corners.size();k++){
                n_minors_f_poly.add(poly_corners.get(k));
            }
            n_minors_f_comb.add(n_minors_f_poly);
        }

        Point2D[] p1_corners = new Point2D[poly_corners.size()-1];
        p1_corners = n_minors_f_comb.get(0).toArray(p1_corners);

        Point2D[] p2_corners = new Point2D[poly_corners.size()-1];
        p2_corners = n_minors_f_comb.get(1).toArray(p2_corners);

        ConvexPolygon2D poly1 = new ConvexPolygon2D(p1_corners);
        ConvexPolygon2D poly2 = new ConvexPolygon2D(p2_corners);
        ConvexPolygon2D poly_return;
        poly_return = GetIntersectionOfPolygons(poly1,poly2);

        for(int i =2; i<poly_corners.size(); i++){

            p1_corners = n_minors_f_comb.get(i).toArray(p1_corners);
            poly_return = GetIntersectionOfPolygons(poly1,poly_return);
        }
        return poly_return;
    }

    public static boolean IsEqual(double d1, double d2)
    {

        return (Math.abs(d1-d2) <= EquityTolerance);
    }

    public ConvexPolygon2D(Point2D[] corners)
    {
        Corners = corners;
    }

    //math logic from http://www.wyrmtale.com/blog/2013/115/2d-line-intersection-in-c
    public static Point2D GetIntersectionPoint(Point2D l1p1, Point2D l1p2, Point2D l2p1, Point2D l2p2)
    {
        double A1 = l1p2.Y - l1p1.Y;
        double B1 = l1p1.X - l1p2.X;
        double C1 = A1 * l1p1.X + B1 * l1p1.Y;

        double A2 = l2p2.Y - l2p1.Y;
        double B2 = l2p1.X - l2p2.X;
        double C2 = A2 * l2p1.X + B2 * l2p1.Y;

        //lines are parallel
        double det = A1 * B2 - A2 * B1;
        if (IsEqual(det, 0d))
        {
            return null; //parallel lines
        }
        else
        {
            double x = (B2 * C1 - B1 * C2) / det;
            double y = (A1 * C2 - A2 * C1) / det;
            boolean online1 = ((Math.min(l1p1.X, l1p2.X) < x || IsEqual(Math.min(l1p1.X, l1p2.X), x))
                    && (Math.max(l1p1.X, l1p2.X) > x || IsEqual(Math.max(l1p1.X, l1p2.X), x))
                    && (Math.min(l1p1.Y, l1p2.Y) < y || IsEqual(Math.min(l1p1.Y, l1p2.Y), y))
                    && (Math.max(l1p1.Y, l1p2.Y) > y || IsEqual(Math.max(l1p1.Y, l1p2.Y), y))
            );
            boolean online2 = ((Math.min(l2p1.X, l2p2.X) < x || IsEqual(Math.min(l2p1.X, l2p2.X), x))
                    && (Math.max(l2p1.X, l2p2.X) > x || IsEqual(Math.max(l2p1.X, l2p2.X), x))
                    && (Math.min(l2p1.Y, l2p2.Y) < y || IsEqual(Math.min(l2p1.Y, l2p2.Y), y))
                    && (Math.max(l2p1.Y, l2p2.Y) > y || IsEqual(Math.max(l2p1.Y, l2p2.Y), y))
            );

            if (online1 && online2)
                return new Point2D(x, y);
        }
        return null; //intersection is at out of at least one segment.
    }

    // taken from https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
    public static boolean IsPointInsidePoly(Point2D test, ConvexPolygon2D poly)
    {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = poly.Corners.length - 1; i < poly.Corners.length; j = i++)
        {
            if ((poly.Corners[i].Y > test.Y) != (poly.Corners[j].Y > test.Y) &&
                    (test.X < (poly.Corners[j].X - poly.Corners[i].X) * (test.Y - poly.Corners[i].Y) / (poly.Corners[j].Y - poly.Corners[i].Y) + poly.Corners[i].X))
            {
                result = !result;
            }
        }
        return result;
    }

    public static Point2D[] GetIntersectionPoints(Point2D l1p1, Point2D l1p2, ConvexPolygon2D poly)
    {
        ArrayList<Point2D> intersectionPoints = new ArrayList<Point2D>();
        for (int i = 0; i < poly.Corners.length; i++)
        {

            int next = (i + 1 == poly.Corners.length) ? 0 : i + 1;

            Point2D ip = GetIntersectionPoint(l1p1, l1p2, poly.Corners[i], poly.Corners[next]);

            if (ip != null) intersectionPoints.add(ip);

        }

        Point2D[] returned_point2d_array = new Point2D[intersectionPoints.size()];
        returned_point2d_array = intersectionPoints.toArray(returned_point2d_array);

        return returned_point2d_array;
    }
    private static void AddPoints(ArrayList<Point2D> pool, Point2D[] newpoints)
    {
        for (Point2D np : newpoints)
        {
            boolean found = false;
            for (Point2D p : pool)
            {
                if (IsEqual(p.X, np.X) && IsEqual(p.Y, np.Y))
                {
                    found = true;
                    break;
                }
            }
            if (!found) pool.add(np);
        }
    }


    public static Point2D[] OrderClockwise(ArrayList<Point2D> points)
    {
        double mx = 0;
        double my = 0;
        for (Point2D p : points)
        {
            mx += p.X;
            my += p.Y;
        }
        mx /= points.size();
        my /= points.size();

        //return points.OrderBy(v => Math.Atan2(v.Y - my, v.X - mX)).ToArray();

        Point2D center = new Point2D(mx,my);
        //ArrayList<Point2D> clippedCorners = new ArrayList<Point2D>();

        Collections.sort(points, (a, b) -> {
            double a1 = (Math.toDegrees(Math.atan2(a.X - center.X, a.Y - center.Y)) + 360) % 360;
            double a2 = (Math.toDegrees(Math.atan2(b.X - center.X, b.Y - center.Y)) + 360) % 360;
            return (int) (a1 - a2);
        });

        Point2D[] returned_array = new Point2D[points.size()];
        returned_array = points.toArray(returned_array);
        return returned_array;

    }


    public static ConvexPolygon2D GetIntersectionOfPolygons(ConvexPolygon2D poly1, ConvexPolygon2D poly2)
    {
        ArrayList<Point2D> clippedCorners = new ArrayList<Point2D>();

        //Add  the corners of poly1 which are inside poly2
        for (int i = 0; i < poly1.Corners.length; i++)
        {
            if (IsPointInsidePoly(poly1.Corners[i], poly2))
                AddPoints(clippedCorners, new Point2D[] { poly1.Corners[i] });
        }

        //Add the corners of poly2 which are inside poly1
        for (int i = 0; i < poly2.Corners.length; i++)
        {
            if (IsPointInsidePoly(poly2.Corners[i],poly1))
                AddPoints(clippedCorners, new Point2D[]{ poly2.Corners[i]});
        }

        //Add  the intersection points
        for (int i = 0, next = 1; i < poly1.Corners.length; i++, next = (i + 1 == poly1.Corners.length) ? 0 : i + 1)
        {
            AddPoints(clippedCorners, GetIntersectionPoints(poly1.Corners[i], poly1.Corners[next], poly2));
        }

        //Point2D[] returned_array = new Point2D[clippedCorners.size()];
        //returned_array = clippedCorners.toArray(returned_array);
        return new ConvexPolygon2D(OrderClockwise(clippedCorners));
    }
}

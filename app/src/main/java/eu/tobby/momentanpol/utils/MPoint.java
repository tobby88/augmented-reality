package eu.tobby.momentanpol.utils;

import android.util.Log;

import org.opencv.core.Point;

import java.util.Vector;

/**
 * Class for static operations with the datatype CvPoint
 * @author janna
 * @author tobby
 * @author fabian
 * @version 1.0
 */
public class MPoint {
    /**
     * Method to calculate the determinant of the matrix of these two vectors [v1 v2]
     * @param v1: first point
     * @param v2: second point
     * @return: determinant of the matrix
     */
    public static double cross(Point v1, Point v2) {
        return v1.x*v2.y - v1.y*v2.x;
    }

    /**
     * method to calculate the length of the vector (end1-start1)
     * @param start1: start point
     * @param end1: end point
     * @return: length of the vector
     */
    public static double length(Point start1, Point end1) {
        return Math.sqrt(Math.pow(end1.x - start1.x, 2) + Math.pow(end1.x - start1.x, 2));

    }

    /**
     * Multiplication of a vector with a scalar
     * @param v1: Vector
     * @param scalar: Scalar
     * @return: scaled vector
     */
    public static Point mulScalar(Point v1, double scalar) {
        return new Point(v1.x*scalar,v1.y*scalar);
    }

    /**
     * Method to separate adjacent points in groups so that there is only one intersection point on
     * a found crosspoint of two rods
     * @param points
     * @return
     */
    public static Vector<Point> reducePointVec(Vector<Point> points) {
        int before, after;
        before = points.size();
        Log.i("Punktfilter - vorher: ", Integer.toString(before));
        Vector<Point> filtered = new Vector<>();
        for (int i = 0; i < points.size(); i++) {
            // temporary vector to store the adjacent points
            Vector<Point> temppoints = new Vector<>();
            temppoints.add(points.get(i));
            for (int j = 0; j < points.size(); j++) {
                // check if the the point i and the point j are adjacent with a maximum euklidean distance of 20 pixels
                if ((length(points.get(i), points.get(j)) < 20) && (i != j)) {
                    temppoints.add(points.get(j));
                }
            }

            int x = 0, y = 0, j;
            // calculate the centroid of the point cloud
            for (j = 0; j < temppoints.size(); j++) {
                x += (int) temppoints.get(j).x;
                y += (int) temppoints.get(j).y;
            }
            x /= j;
            y /= j;
            temppoints.get(0).x = x;
            temppoints.get(0).y = y;
            // store the centroid in the filtered vector
            filtered.add(temppoints.get(0));
        }

        for (int i = 0; i < filtered.size() - 1; i++) {
            for (int j = i + 1; j < filtered.size(); j++) {
                //remove points if they are twice in the filtered list
                if (((int) filtered.get(i).x) == ((int) filtered.get(j).x) && ((int) filtered.get(i).y) == ((int) filtered.get(j).y)) {
                    filtered.remove(j);
                    j--;
                }
            }
        }
        after = filtered.size();
        Log.i("Punktfilter - nachher: ", Integer.toString(after));
        //check if this method has reduced the number of points
        if (before == after) {
            return filtered;
        } else {
            // recursive call of this method to do the next optimization step
            return reducePointVec(filtered);
        }
    }

    /**
     * Method to check if there are intersections between two lines
     * @param start1: start point of the first line segment
     * @param end1: end point of the first line segment
     * @param start2: start point of the second line segment
     * @param end2: end point of the second line segment
     * @return: the intersection point. If there is no intersection, than null will be returned
     */
    public static Point getIntersection(Point start1, Point end1, Point start2, Point end2) {
        Point normVec1 = new Point(end1.x - start1.x, end1.y - start1.y);
        Point normVec2 = new Point(end2.x - start2.x, end2.y - start2.y);
        Point diffStart = new Point(start2.x - start1.x, start2.y - start1.y);
        // checks the angle between this to lines, if it is smaller than 0.7 the two lines are nearly parallel
        if(MPoint.cross(normVec1, normVec2)<0.7){
            return null;
        }
        /* t is the scale parameter for the first line segment, if it is between 0 and 1 the two line segments intersect,
           else the two lines intersects but not in the range of the two line segments --> return null
         */
        double t = MPoint.cross(diffStart, normVec2)/MPoint.cross(normVec1, normVec2);
        if (t < 1.1 && t > 0) {
            return new Point(start1.x + t * normVec1.x, start1.y + t * normVec1.y);
        }
        else {
            return null;
        }
    }


}

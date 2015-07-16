package eu.tobby.momentanpol.utils;

import org.opencv.core.Point;

/**
 * Created by fabian on 16.07.15.
 * Helper class for CVPoint Operations
 */


public class MPoint extends Point{

    public static double cross(Point v1, Point v2) {
        return v1.x*v2.y - v1.y*v2.x;
    }

    public static double length(Point start1, Point end1) {
        return (double) Math.sqrt(Math.pow(end1.x-start1.x,2)+Math.pow(end1.x-start1.x,2));

    }

    public static Point mulScalar(Point v1, double scalar) {
        return new Point(v1.x*scalar,v1.y*scalar);
    }
}

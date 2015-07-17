package eu.tobby.momentanpol.utils;

import android.util.Log;

import org.opencv.core.Point;

import java.util.Vector;

/**
 * Created by fabian on 16.07.15.
 * Helper class for CVPoint Operations
 */


public class MPoint {

    public static double cross(Point v1, Point v2) {
        return v1.x*v2.y - v1.y*v2.x;
    }

    public static double length(Point start1, Point end1) {
        return Math.sqrt(Math.pow(end1.x - start1.x, 2) + Math.pow(end1.x - start1.x, 2));

    }

    public static Point mulScalar(Point v1, double scalar) {
        return new Point(v1.x*scalar,v1.y*scalar);
    }


    public static Vector<Point> reducePointVec(Vector<Point> points) {
        int before, after;
        before = points.size();
        Log.i("Punktfilter - vorher: ", Integer.toString(before));
        Vector<Point> filtered = new Vector<>();
        for (int i = 0; i < points.size(); i++) {
            Vector<Point> temppoints = new Vector<>();
            temppoints.add(points.get(i));
            for (int j = 0; j < points.size(); j++) {
                if ((length(points.get(i), points.get(j)) < 20) && (i != j)) {
                    temppoints.add(points.get(j));
                }
            }

            int x = 0, y = 0, j;
            for (j = 0; j < temppoints.size(); j++) {
                x += (int) temppoints.get(j).x;
                y += (int) temppoints.get(j).y;
            }
            x /= j;
            y /= j;
            temppoints.get(0).x = x;
            temppoints.get(0).y = y;
            filtered.add(temppoints.get(0));
        }

        for (int i = 0; i < filtered.size() - 1; i++) {
            for (int j = i + 1; j < filtered.size(); j++) {
                if (((int) filtered.get(i).x) == ((int) filtered.get(j).x) && ((int) filtered.get(i).y) == ((int) filtered.get(j).y)) {
                    filtered.remove(j);
                    j--;
                }
            }
        }
        after = filtered.size();
        Log.i("Punktfilter - nachher: ", Integer.toString(after));
        if (before == after) {
            return filtered;
        } else {
            return reducePointVec(filtered);
        }
    }

}

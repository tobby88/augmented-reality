package eu.tobby.momentanpol.utils;

import org.opencv.core.Point;

/**
 * Class for line sections in OpenCV
 * @author janna
 * @author tobby
 * @author fabian
 * @version 1.0
 */
public class Line {

    public Point start;
    public Point end;

    /**
     * Constructor for two elements of type CvPoint
     * @param start
     * @param end
     */
    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Constructor for two points given in Integer values
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Line(int x1, int y1, int x2, int y2) {
        start = new Point(x1, y1);
        end = new Point(x2, y2);
    }
}

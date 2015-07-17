package eu.tobby.momentanpol.utils;

import org.opencv.core.Point;

/**
 * Created by tobby on 17.07.15.
 */
public class Line {

    public Point start;
    public Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Line(int x1, int y1, int x2, int y2) {
        start = new Point(x1, y1);
        end = new Point(x2, y2);
    }
}

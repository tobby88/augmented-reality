package eu.tobby.momentanpol.utils;

import android.util.Log;

import org.opencv.core.Point;

import java.util.Vector;

/**
 * Created by tobby on 17.07.15.
 */
public class MLine {

    public static Vector<Line> reduceLineVec(Vector<Line> lines) {
        int before, after;
        before = lines.size();
        Log.i("Linienfilter - vorher: ", Integer.toString(before));
        Vector<Line> filtered = new Vector<>();
        for (int i = 0; i < lines.size(); i++) {
            Vector<Line> templines = new Vector<>();
            templines.add(lines.get(i));
            for (int j = 0; j < lines.size(); j++) {
                Point start1, start2, end1, end2;
                start1 = lines.get(i).start;
                end1 = lines.get(i).end;
                start2 = lines.get(j).start;
                end2 = lines.get(j).end;
                if ((i != j) && (MPoint.length(start1, start2) < 20) && (MPoint.length(end1, end2) < 20)) {
                    templines.add(lines.get(j));
                }
            }

            double startx = 0, starty = 0, endx = 0, endy = 0;
            int j;
            for (j = 0; j < templines.size(); j++) {
                startx += templines.get(j).start.x;
                starty += templines.get(j).start.y;
                endx += templines.get(j).end.x;
                endy += templines.get(j).end.y;
            }
            startx /= j;
            starty /= j;
            endx /= j;
            endy /= j;
            templines.get(0).start.x = (int) startx;
            templines.get(0).start.y = (int) starty;
            templines.get(0).end.x = (int) endx;
            templines.get(0).end.y = (int) endy;
            filtered.add(templines.get(0));
        }

        for (int i = 0; i < filtered.size() - 1; i++) {
            for (int j = i + 1; j < filtered.size(); j++) {
                if ((filtered.get(i).start.x == filtered.get(j).start.x) && (filtered.get(i).start.y == filtered.get(j).start.y) && (filtered.get(i).end.x == filtered.get(j).end.x) && (filtered.get(i).end.y == filtered.get(j).end.y)) {
                    filtered.remove(j);
                    j--;
                }
            }
        }
        after = filtered.size();
        Log.i("Linienfilter - nachher: ", Integer.toString(after));
        if (before == after) {
            return filtered;
        } else {
            return reduceLineVec(filtered);
        }
    }
}

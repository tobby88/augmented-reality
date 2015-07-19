package eu.tobby.momentanpol.OpenCVMarker;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;
import eu.tobby.momentanpol.utils.Line;
import eu.tobby.momentanpol.utils.MLine;
import eu.tobby.momentanpol.utils.MPoint;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 05.07.15.
 */
public class MomentanpolOpenCVMarker implements MomentanpolState {

    private final String LOGTAG = "MomentalpolOpenCV";
    String string1 = "Festlager.png";
    private OpenCVMarkerRenderer mRenderer;
    private Vector<Texture> mTextures;
    private Activity mActivity;
    private MatOfKeyPoint keypointstest = new MatOfKeyPoint();
    private MatOfKeyPoint keypointstemplate = new MatOfKeyPoint();
    private Mat testDescriptors = new Mat();
    private Mat templateDescriptors = new Mat();
    private MatOfDMatch matches = new MatOfDMatch();

    private MomentanpolOpenCVView mOpenCvCameraView;
    private Mat temp;
    private Mat grayImg;
    private Mat intenseImg = new Mat();
    private Mat lines = new Mat();
    private Mat circles = new Mat();
    private SeekBar minThresh;
    private SeekBar maxThresh;
    private SeekBar hough;
    private TextView minThreshText;
    private TextView maxThreshText;
    private TextView houghText;
    private int minThreshold = 50;
    private int maxThreshold = 300;
    private int houghValue = 100;


    public MomentanpolOpenCVMarker(Activity activity) {
        mActivity = activity;
        mRenderer = new OpenCVMarkerRenderer(this);
        mTextures = new Vector<>();
        loadTextures();
        mRenderer.setTextures(mTextures);
    }

    public boolean doLoadTrackersData() {
        return false;
    }


    public void loadTextures() {
    }


    public boolean doInitTrackers() {
        return false;
    }


    public MomentanpolRenderer getRenderer() {
        return mRenderer;
    }


    public void isActionDown() {
        //doImageProcessing();
        findGreen();
        Log.d(LOGTAG, "ButtonDown");
    }


    public Mat loadImageOpenCV(String filename) {
        //Bild muss im Assets-Ordner sein!!
        AssetManager assets = mActivity.getAssets();
        InputStream iStream = null;
        try {
            iStream = assets.open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedInputStream ioStream = new BufferedInputStream(iStream);
        Bitmap tempImage = BitmapFactory.decodeStream(ioStream);
        Mat retMat = new Mat();
        Utils.bitmapToMat(tempImage, retMat);
        return retMat;
    }


    public void showImage(Bitmap bm) {
        ImageView imageView = new ImageView(mActivity);
        Matrix rotation = new Matrix();
        //Hotfix: weil Vuforia die Bilder immer 90° verdreht aufnimmt
        rotation.postRotate(90);
        Bitmap rotatedBM = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotation, true);
        imageView.setImageBitmap(rotatedBM);
        mActivity.addContentView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    public void doImageProcessing() {
        Mat template = loadImageOpenCV(string1);
        Mat cameraImage = mRenderer.getCameraImage();
        FeatureDetector fast = FeatureDetector.create(FeatureDetector.FAST);
        Bitmap viewTest = Bitmap.createBitmap(cameraImage.cols(), cameraImage.rows(), Bitmap.Config.ARGB_8888);

        fast.detect(cameraImage, keypointstest);
        fast.detect(template, keypointstemplate);
        Log.i("Inhalt der Keypoints1", "Test ob leer" + keypointstemplate.toList());
        Log.i("Inhalt der Keypoints1", "Test ob leer" + keypointstest.toList());

        //DescriptorExtractor FastExtractor = DescriptorExtractor.create(FeatureDetector.SURF);
        DescriptorExtractor FastExtractor = DescriptorExtractor.create(DescriptorExtractor.FREAK);
        FastExtractor.compute(cameraImage, keypointstest, testDescriptors);
        FastExtractor.compute(template, keypointstemplate, templateDescriptors);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        matcher.match(templateDescriptors, testDescriptors, matches);
        if (matches.empty()) return;
        Log.i("Anzeige der Matches", "Gefundene Matches" + matches.toList());
        Mat imageOut = new Mat(cameraImage.rows(), cameraImage.cols(), cameraImage.type());
        /*Features2d.drawMatches(cameraImage, keypointstest, template, keypointstemplate, matches, imageOut);
        Scalar redcolor = new Scalar(255, 0, 0);
        Features2d.drawKeypoints(imageOut, keypointstest, imageOut, redcolor, 3);
        Utils.matToBitmap(imageOut, viewTest);*/
        Utils.matToBitmap(cameraImage, viewTest);
        showImage(viewTest);
    }

    void findGreen() {
        Log.e("in findGreen", "starte Funktion");
        //Kamerabild laden
        Mat cameraImage = mRenderer.getCameraImage();
        //Mat cameraImage = loadImageOpenCV(string1);
        //Kommentare zu Bildtyp und Bildmaßen
        Log.e("in findGreen", "Kamerabild wurde geladen hoehe " + cameraImage.rows());
        Log.e("in findGreen", "Kamerabild wurde geladen breite " + cameraImage.cols());
        Log.e("in findGreen", "Typ des Bildes " + cameraImage.type());
        Log.e("ind findGreen", String.format("Anzahl der Kanäle %d", cameraImage.channels()));

        int x, y;
        Point p = new Point();
        double coords2[] = {0, 0};
        p.set(coords2);

        //Grüne Pixel finden
        int i, j = 1, sumgreen = 0, z = 0, e = 0, f = 0, zaehler = 0, abbruch = 0, erster = 1;
        double rgb[];
        rgb = cameraImage.get(0, 0);
        double coords1[] = new double[2];
        //Reihenfolge der Kanäle bei 4 Kanälen: RGB alpha
        for (i = 0; i < cameraImage.rows(); i++) {
            /*if (abbruch == 1){
                break;
            }*/
            for (j = 0; j < cameraImage.cols(); j++) {
                //Farbkanäle für einzelnen Pixel speichern
                rgb = cameraImage.get(i, j);
                //Grüner Pixel?
                if (rgb[0] <= 50) {
                    if (rgb[1] > 80) {
                        if (rgb[2] <= 50) {
                            //Wenn ja: Speichere Koordinaten in q
                            Log.e("in findGreen", "ein grüner Pixel wurde gefunden");
                            coords2[0] = (double) i;
                            coords2[1] = (double) j;
                            //Sind die daneben liegenden Pixel auch grün? 400 Pixel werden untersucht
                            for (e = -10; e < 10; e++) {
                                for (f = -10; f < 10; f++) {
                                    rgb = cameraImage.get(i + e, j + f);
                                    if (rgb[0] <= 50) {
                                        if (rgb[1] > 80) {
                                            if (rgb[2] <= 50) {
                                                zaehler = zaehler + 1;
                                                Log.e("in findGreen", "Zaehler wurde erhoeht");
                                            }
                                        }
                                    }
                                }

                            }
                            //Wenn mindestens sieben daneben liegende Pixel ebenfalls grün sind,
                            // werden die Koordinaten als richtig angenommen
                            if (zaehler > 4) {
                                if (erster == 1) {
                                    coords1[0] = (double) i;
                                    coords1[1] = (double) j;
                                    erster = erster + 1;
                                } else {
                                    coords2[0] = (double) i;
                                    coords2[1] = (double) j;
                                }

                            } else {
                                zaehler = 0;
                            }
                        }
                    }
                }

            }
        }
        Log.e("in findGreen", "nach Detektion der grünen Pixel");
        for (i = (int) coords2[0]; i < coords2[0] + 50; i++) {
            for (j = (int) coords2[1]; j < (int) coords2[1] + 50; j++) {
                if (coords2[0] + 50 >= cameraImage.rows()) {
                    break;
                }
                if (coords2[1] + 50 >= cameraImage.cols()) {
                    break;
                }

                rgb = cameraImage.get(i, j);

                rgb[0] = 200;
                cameraImage.put(i, j, rgb);
            }
        }
        for (i = (int) coords1[0]; i < coords1[0] + 50; i++) {
            for (j = (int) coords1[1]; j < (int) coords1[1] + 50; j++) {
                if (coords1[0] + 50 >= cameraImage.rows()) {
                    break;
                }
                if (coords1[1] + 50 >= cameraImage.cols()) {
                    break;
                }

                rgb = cameraImage.get(i, j);
                rgb[0] = 200;
                cameraImage.put(i, j, rgb);
            }
        }
        // Pfeilspitze finden
        zaehler = 0;
        for (i = (int) coords1[0] - 30; i < coords1[0] + 30; i++) {
            for (j = (int) coords1[1] - 30; j < coords1[1] + 30; j++) {
                for (e = -10; e < 10; e++) {
                    for (f = -10; f < 10; f++) {
                        rgb = cameraImage.get(i + e, j + f);
                        if (rgb[0] <= 50) {
                            if (rgb[1] > 80) {
                                if (rgb[2] <= 50) {
                                    zaehler = zaehler + 1;
                                }
                            }
                        }
                    }

                }
            }
        }
        int zaehler2 = 0;
        for (i = (int) coords2[0] - 30; i < coords2[0] + 30; i++) {
            for (j = (int) coords2[1] - 30; j < coords2[1] + 30; j++) {
                for (e = -10; e < 10; e++) {
                    for (f = -10; f < 10; f++) {
                        rgb = cameraImage.get(i + e, j + f);
                        if (rgb[0] <= 50) {
                            if (rgb[1] > 80) {
                                if (rgb[2] <= 50) {
                                    zaehler = zaehler + 1;
                                }
                            }
                        }
                    }

                }
            }
        }

        Log.e("in findGreen", "Koordinaten der Pixel y" + coords2[0]);
        Log.e("in findGreen", "Koordinaten der Pixel x" + coords2[1]);
        double zw[] = new double[2];
        if (zaehler > zaehler2) {
            Log.e("in findGreen", "Pfeilspitze bei coords1");
        } else {
            Log.e("in findGreen", "Pfeilspitze bei q");
            zw[0] = coords1[0];
            zw[1] = coords1[1];
            coords1[0] = coords2[0];
            coords1[1] = coords2[1];
            coords2[0] = zw[0];
            coords2[1] = zw[1];

        }
        Core.line(cameraImage, new Point(coords2[1], coords2[0]), new Point(coords1[1], coords1[0]), new Scalar(0, 0, 255));
        Log.i("Koordinaten: ", Double.toString(coords2[0]) + Double.toString(coords2[1]) + Double.toString(coords1[0]) + Double.toString(coords1[1]));
        //Steigungsdreick berechnen
        double s;
        s = (coords1[1] - coords2[1]) / (coords1[0] - coords2[0]);
        Point p0 = new Point();
        p0.x = coords2[1];
        p0.y = coords2[0];
        Point p1 = new Point();
        p1.x = coords2[1] + coords1[0] - coords2[0];
        p1.y = coords2[0] - coords1[1] + coords2[1];
        Core.line(cameraImage, p0, p1, new Scalar(255, 0, 0));


        Bitmap bmp;
        bmp = Bitmap.createBitmap(cameraImage.cols(), cameraImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cameraImage, bmp);
        showImage(bmp);
    }

    public Mat onCameraFrame(Mat temp, Mat grayImg) {
        //temp = inputFrame.rgba();
        //Imgproc.cvtColor(temp, grayImg, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(grayImg, grayImg, new Size(5, 5), 0, 0);
        Imgproc.Canny(grayImg, intenseImg, minThreshold, maxThreshold);
        Imgproc.HoughLinesP(intenseImg, lines, 1, Math.PI / 180, houghValue, 20, 10);
        Imgproc.cvtColor(intenseImg, intenseImg, Imgproc.COLOR_GRAY2RGBA);
        Log.d("Anzahl cols", "cols= " + lines.cols());
        if (lines.cols() > 0) {

            Vector<Line> lineVector = new Vector<>();
            for (int x = 0; x < lines.cols(); x++) {
                double[] vec = lines.get(0, x);
                int x1 = (int) vec[0];
                int y1 = (int) vec[1];
                int x2 = (int) vec[2];
                int y2 = (int) vec[3];
                lineVector.add(new Line(x1, y1, x2, y2));
            }
            lineVector = MLine.reduceLineVec(lineVector);

            for (int i = 0; i < lineVector.size(); i++) {
                Core.line(temp, lineVector.get(i).start, lineVector.get(i).end, new Scalar(255, 0, 0), 4);
            }

            Vector<Point> points = new Vector<>();
            for (int i = 0; i < lineVector.size() - 1; i++) {
                for (int j = i + 1; j < lineVector.size(); j++) {
                    Point intersec = getIntersection(lineVector.get(i).start, lineVector.get(i).end, lineVector.get(j).start, lineVector.get(j).end);
                    if (intersec != null) {
                        points.add(intersec);
                    }
                }
            }
            points = MPoint.reducePointVec(points);
            for (int i = 0; i < points.size(); i++) {
                Core.circle(temp, points.get(i), 6, new Scalar(0, 255, 0), -1, 8, 0);
            }
        }

        /*for (int i = 0; i<circles.cols();i++)
        {
            double vCircle[]=circles.get(0,i);

            Point center = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int)Math.round(vCircle[2]);
            Core.circle(intenseImg,center,3, new Scalar(0,255,0), -1, 8, 0);
            Core.circle(intenseImg,center, radius, new Scalar(0,0,255), 3, 8, 0);
        }*/
        return temp;
    }

    private Point getIntersection(Point start1, Point end1, Point start2, Point end2) {
        Point normVec1 = new Point(end1.x - start1.x, end1.y - start1.y);
        Point normVec2 = new Point(end2.x - start2.x, end2.y - start2.y);
        Point diffStart = new Point(start2.x - start1.x, start2.y - start1.y);
        if (MPoint.cross(normVec1, normVec2) < 0.7) {
            return null;
        }

        double t = MPoint.cross(diffStart, normVec2) / MPoint.cross(normVec1, normVec2);
        if (t < 1.1 && t > 0) {
            return new Point(start1.x + t * normVec1.x, start1.y + t * normVec1.y);
        } else {
            return null;
        }
    }


}

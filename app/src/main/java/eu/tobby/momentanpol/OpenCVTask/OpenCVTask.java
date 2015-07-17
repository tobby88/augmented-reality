package eu.tobby.momentanpol.OpenCVTask;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import eu.tobby.momentanpol.R;
import eu.tobby.momentanpol.utils.MPoint;


public class OpenCVTask extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {



    private MomentanpolOpenCVView mOpenCvCameraView;
    private Mat temp;
    private Mat grayImg;
    private Mat intenseImg;
    private Mat lines;
    private Mat circles;
    private SeekBar minThresh;
    private SeekBar maxThresh;
    private SeekBar hough;
    private TextView minThreshText;
    private TextView maxThreshText;
    private TextView houghText;
    private int minThreshold=50;
    private int maxThreshold=300;
    private int houghValue=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenCVLoader.initDebug();
        setContentView(R.layout.activity_open_cvtask);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_open_cvtask);
        maxThresh = (SeekBar) findViewById(R.id.maxThresh);
        minThresh = (SeekBar) findViewById(R.id.minThresh);
        hough = (SeekBar) findViewById(R.id.hough);
        minThreshText = (TextView) findViewById(R.id.minThreshText);
        maxThreshText = (TextView) findViewById(R.id.maxThreshText);
        houghText = (TextView) findViewById(R.id.houghText);
        minThreshText.setText("minThresh: " + minThreshold);
        maxThreshText.setText("maxThresh: " + maxThreshold);
        houghText.setText("houghValue:" + houghValue);
        minThresh.setProgress(minThreshold);
        maxThresh.setProgress(maxThreshold);
        hough.setProgress(houghValue);
        mOpenCvCameraView = (MomentanpolOpenCVView) findViewById(R.id.OpenCVCamView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableView();

        hough.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                houghValue = progress;
                houghText.setText("houghValue: " + houghValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(houghValue);
                houghText.setText("houghvalue: " + houghValue);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        minThresh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minThreshold = progress;
                minThreshText.setText("minThresh: " + minThreshold);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(minThreshold);
                minThreshText.setText("minThresh: " + minThreshold);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maxThresh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxThreshold = progress;
                maxThreshText.setText("maxThresh: " + maxThreshold);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(maxThreshold);
                maxThreshText.setText("maxThresh: " + maxThreshold);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        temp = new Mat(width, height, CvType.CV_8UC4);
        grayImg = new Mat(width, height, CvType.CV_8UC1);
        lines = new Mat();
        intenseImg = new Mat(width, height, CvType.CV_8UC1);
        circles = new Mat(width, height, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        temp = inputFrame.rgba();
        Imgproc.cvtColor(temp, grayImg, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.GaussianBlur(grayImg, grayImg, new Size(5, 5), 0, 0);
        Imgproc.Canny(grayImg, intenseImg, minThreshold, maxThreshold);
        Imgproc.HoughLinesP(intenseImg, lines, 1, Math.PI / 180, houghValue, 20, 10);
        Imgproc.cvtColor(intenseImg, intenseImg, Imgproc.COLOR_GRAY2RGBA);
        Log.d("Anzahl cols","cols= " + lines.cols());
        if(lines.cols()>0) {
            Point starts[] = new Point[lines.cols()];
            Point ends[] = new Point[lines.cols()];
            for (int x = 0; x < lines.cols(); x++) {
                double[] vec = lines.get(0, x);
                double x1 = vec[0],
                        y1 = vec[1],
                        x2 = vec[2],
                        y2 = vec[3];
                starts[x] = new Point(x1, y1);
                ends[x] = new Point(x2, y2);

                Core.line(temp, starts[x], ends[x], new Scalar(255, 0, 0), 3);
            }

            for (int i=0;i<lines.cols()-1;i++) {
                for(int j=i+1;j<lines.cols();j++) {
                    Point intersec = getIntersection(starts[i], ends[i], starts[j], ends[j]);
                    if (intersec != null) {
                        Core.circle(temp, intersec, 3, new Scalar(0, 255, 0), -1, 8, 0);
                    }
                }
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
        if(MPoint.cross(normVec1, normVec2)<0.7){
            return null;
        }

        double t = MPoint.cross(diffStart, normVec2)/MPoint.cross(normVec1, normVec2);
        if(t<1.05 && t>0){
            return new Point(start1.x + t * normVec1.x, start1.y + t * normVec1.y);
        }
        else {
            return null;
        }
    }




}

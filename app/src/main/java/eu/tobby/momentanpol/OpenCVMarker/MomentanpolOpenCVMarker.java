package eu.tobby.momentanpol.OpenCVMarker;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qualcomm.vuforia.Frame;
import com.qualcomm.vuforia.Image;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Vector;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 05.07.15.
 */
public class MomentanpolOpenCVMarker implements MomentanpolState {
    private final String LOGTAG = "MomentalpolOpenCV";
    private OpenCVMarkerRenderer mRenderer;
    private Vector<Texture> mTextures;
    private Activity mActivity;
    private MatOfKeyPoint keypointstest = new MatOfKeyPoint();
    private MatOfKeyPoint keypointstemplate = new MatOfKeyPoint();
    private Mat testDescriptors = new Mat();
    private Mat templateDescriptors = new Mat();
    private MatOfDMatch matches = new MatOfDMatch();
    private Image image;
    private ByteBuffer bb;
    private Mat test;

    public MomentanpolOpenCVMarker(Activity activity) {
        mActivity = activity;
        mRenderer = new OpenCVMarkerRenderer();
        mTextures = new Vector<>();
        loadTextures();
        mRenderer.setTextures(mTextures);

    }
    String string1 = "Festlager.png";
    public boolean doLoadTrackersData(){return false;}
    public void loadTextures(){}
    public boolean doInitTrackers(){return false;}
    public MomentanpolRenderer getRenderer() {return mRenderer;}

    public void isActionDown() {
        Log.d(LOGTAG, "ButtonDown");
        getimage(string1);
    }

    public void getimage(String string1){
        AssetManager assets = mActivity.getAssets();
        Log.e("Funktion getimage()", "Funktion wurde gestartet");
        InputStream inputStream1 = null;
        try {
            inputStream1 = assets.open(string1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.d("get object", "Datei konnte nicht geoeffnet werden");

        }
    Log.e("getimage()", "Bild wurde geladen");

        BufferedInputStream bufferedInputStream1 = new BufferedInputStream(inputStream1);
        Bitmap bitmap1 = BitmapFactory.decodeStream(bufferedInputStream1);


        Mat matrix1 = new Mat();
        Utils.bitmapToMat(bitmap1, matrix1);
        Log.d("matrix findObject", "Height : " + matrix1.cols() + "x" + matrix1.rows());


        //teste OpenCV-Funktionen zur Feature Detektion mit FAST mit dem geladenen Bild
        mRenderer.findObject1(matrix1);

    }



    public void findObject1(Mat template){
        FeatureDetector fast = FeatureDetector.create(FeatureDetector.FAST);
        State state = Renderer.getInstance().begin();
        Frame frame = state.getFrame();
        Log.e("in findObject1","vor der Schleife");
        for(int i=0;i<frame.getNumImages()-1;i++) {
            Log.d("findObject", "Height : " + frame.getImage(i).getWidth() + "x" + frame.getImage(i).getHeight());
            Log.d("template findObject", "Height : " + template.cols() + "x" + template.rows());

            image = frame.getImage(i);
            bb = image.getPixels();
            test = new Mat(image.getBufferHeight(),image.getBufferWidth(), CvType.CV_8UC1);
            test.put(image.getBufferHeight(), image.getBufferWidth(), bb.get());


        }

        Renderer.getInstance().end();

        fast.detect(test, keypointstest);
        fast.detect(template, keypointstemplate);
        Log.e("Inhalt der Keypoints1", "Test ob leer" + keypointstemplate.toList());
        Log.e("Inhalt der Keypoints1", "Test ob leer" + keypointstest.toList());


        DescriptorExtractor FastExtractor = DescriptorExtractor.create(FeatureDetector.SURF);
        FastExtractor.compute(test, keypointstest, testDescriptors);
        FastExtractor.compute(template, keypointstemplate, templateDescriptors);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        matcher.match(templateDescriptors, testDescriptors, matches);
        Log.e("Anzeige der Matches", "Gefundene Matches" + matches.toList());
        /*Mat imageOut = test.clone();
        Mat mRgba= test.clone();
        Imgproc.cvtColor(test, mRgba, Imgproc.COLOR_RGBA2RGB, 4);
        // Features2d.drawMatches(test, keypointstest, template, keypointstemplate, matches, imageOut);
        Scalar redcolor = new Scalar(255,0,0);


        //Features2d.drawKeypoints(mRgba, keypointstest, mRgba, redcolor, 3);*/
    }
}

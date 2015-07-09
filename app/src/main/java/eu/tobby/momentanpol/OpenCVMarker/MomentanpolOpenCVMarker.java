package eu.tobby.momentanpol.OpenCVMarker;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qualcomm.vuforia.Image;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;

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
    String string1 = "Festlager.png";
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
        doImageProcessing();
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
        Log.e("Inhalt der Keypoints1", "Test ob leer" + keypointstemplate.toList());
        Log.e("Inhalt der Keypoints1", "Test ob leer" + keypointstest.toList());


        DescriptorExtractor FastExtractor = DescriptorExtractor.create(FeatureDetector.SURF);
        FastExtractor.compute(cameraImage, keypointstest, testDescriptors);
        FastExtractor.compute(template, keypointstemplate, templateDescriptors);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        if (matches.empty()) return;
        matcher.match(templateDescriptors, testDescriptors, matches);
        Log.e("Anzeige der Matches", "Gefundene Matches" + matches.toList());
        Mat imageOut = new Mat();
        Features2d.drawMatches(cameraImage, keypointstest, template, keypointstemplate, matches, imageOut);
        Scalar redcolor = new Scalar(255, 0, 0);
        Features2d.drawKeypoints(imageOut, keypointstest, imageOut, redcolor, 3);
        Utils.matToBitmap(imageOut, viewTest);
        showImage(viewTest);
    }
}

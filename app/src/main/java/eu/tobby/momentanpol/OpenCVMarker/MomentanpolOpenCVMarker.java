package eu.tobby.momentanpol.OpenCVMarker;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        showImage(mRenderer.viewTest);

    }

    public void showImage(Bitmap bm) {
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageBitmap(bm);
        mActivity.addContentView(imageView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}

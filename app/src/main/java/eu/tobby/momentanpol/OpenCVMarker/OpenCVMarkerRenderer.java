package eu.tobby.momentanpol.OpenCVMarker;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Frame;
import com.qualcomm.vuforia.Image;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Vuforia;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.nio.ByteBuffer;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.utils.CubeShaders;
import eu.tobby.momentanpol.utils.SampleUtils;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 05.07.15.
 */
public class OpenCVMarkerRenderer implements MomentanpolRenderer {

    public Mat mImage = new Mat();
    public Bitmap viewTest;
    //private Vector<Texture> mTextures;
    private Vector<Texture> mTextures;
    // OpenGL ES 2.0 specific:
    private Matrix44F mProjectionMatrix;
    private MatOfKeyPoint keypointstest = new MatOfKeyPoint();
    private MatOfKeyPoint keypointstemplate = new MatOfKeyPoint();
    private Mat testDescriptors = new Mat();
    private Mat templateDescriptors = new Mat();

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRendering();
        Vuforia.onSurfaceCreated();
    }


    public void onDrawFrame(GL10 gl) {
        renderFrame();
    }


    private void renderFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Renderer.getInstance().drawVideoBackground();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        Renderer.getInstance().end();
    }


    private void initRendering() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        for (Texture t : mTextures) {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        SampleUtils.createProgramFromShaderSrc(CubeShaders.CUBE_MESH_VERTEX_SHADER, CubeShaders.CUBE_MESH_FRAGMENT_SHADER);
    }


    public void setProjectionMatrix() {
        CameraCalibration camCal = CameraDevice.getInstance().getCameraCalibration();
        mProjectionMatrix = Tool.getProjectionGL(camCal, 10.0f, 5000.0f);
    }


    public Matrix44F getProjectionMatrix()
    {
        return mProjectionMatrix;
    }


    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Call Vuforia function to handle render surface size changes:
        Vuforia.onSurfaceChanged(width, height);
    }


    public void setTextures(Vector<Texture> textures) {
        mTextures = textures;
    }


    public void findObject1(Mat template) {
        FeatureDetector fast = FeatureDetector.create(FeatureDetector.FAST);
        State state = Renderer.getInstance().begin();
        Frame frame = state.getFrame();
        Log.e("in Funktion findObject1", "vor der Schleife" + frame.getNumImages());

        Image image;
        image = frame.getImage(0);
        Log.d("Bildformat", " " + image.getFormat());
        ByteBuffer bb;
        bb = image.getPixels();
        byte[] testByte = new byte[bb.remaining()];
        bb.get(testByte);
        Log.d("OpenCV", " " + testByte.length);
        mImage = new Mat(image.getBufferWidth(), image.getBufferHeight(), CvType.CV_8UC3);
        mImage.put(image.getBufferHeight(), image.getBufferWidth(), testByte);
        viewTest = Bitmap.createBitmap(mImage.cols(), mImage.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mImage, viewTest);
        Log.d("Angezeigtes Bild", " " + viewTest.getByteCount());
        Log.e("Groesse von Test", "Test" + mImage.cols() + "x" + mImage.rows());
        Log.d("findObject", "Height : " + frame.getImage(0).getWidth() + "x" + frame.getImage(0).getHeight());
        Log.d("template findObject", "Height : " + template.cols() + "x" + template.rows());

        fast.detect(mImage, keypointstest);
        fast.detect(template, keypointstemplate);
        Log.e("Inhalt der Keypoints1", "Test ob leer" + keypointstemplate.toList());
        Log.e("Inhalt der Keypoints1", "Test ob leer" + keypointstest.toList());

        DescriptorExtractor FastExtractor = DescriptorExtractor.create(FeatureDetector.SURF);
        FastExtractor.compute(mImage, keypointstest, testDescriptors);
        FastExtractor.compute(template, keypointstemplate, templateDescriptors);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);

        Renderer.getInstance().end();
    }

}

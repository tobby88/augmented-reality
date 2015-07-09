package eu.tobby.momentanpol.OpenCVMarker;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Frame;
import com.qualcomm.vuforia.Image;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.PIXEL_FORMAT;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Vuforia;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;

import java.nio.ByteBuffer;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.objects.Plane;
import eu.tobby.momentanpol.utils.CubeShaders;
import eu.tobby.momentanpol.utils.SampleUtils;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 05.07.15.
 */
public class OpenCVMarkerRenderer implements MomentanpolRenderer {


    public Mat mImage = new Mat();
    public Bitmap viewTest;
    public MomentanpolOpenCVMarker mTask;
    //private Vector<Texture> mTextures;
    private Vector<Texture> mTextures;
    // OpenGL ES 2.0 specific:
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int normalHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    private int texSampler2DHandle = 0;
    private Matrix44F mProjectionMatrix;
    private Plane plane = new Plane(110, 85);
    private MatOfKeyPoint keypointstest = new MatOfKeyPoint();
    private MatOfKeyPoint keypointstemplate = new MatOfKeyPoint();
    private Mat testDescriptors = new Mat();
    private Mat templateDescriptors = new Mat();
    private MatOfDMatch matches = new MatOfDMatch();
    private ByteBuffer bb;

    public OpenCVMarkerRenderer(MomentanpolOpenCVMarker task) {
        mTask = task;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRendering();
        Vuforia.onSurfaceCreated();
    }

    public void onDrawFrame(GL10 gl) {
        renderFrame();
        //mTask.doImageProcessing();
    }

    private void renderFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        State state = Renderer.getInstance().begin();
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

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);

        for (Texture t : mTextures) {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, t.mData);
        }

        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
                CubeShaders.CUBE_MESH_VERTEX_SHADER,
                CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexPosition");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
                "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
                "texSampler2D");

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

    public Mat getCameraImage() {
        State state = Renderer.getInstance().begin();
        Frame frame = state.getFrame();
        Image tempImage = null;
        for (int i = 0; i < frame.getNumImages(); i++) {
            tempImage = frame.getImage(i);
            if (tempImage.getFormat() == PIXEL_FORMAT.RGB888) {
                break;
            }
        }
        Log.d("Bildformat", " " + tempImage.getFormat() + "Width: " + tempImage.getWidth() + "Height: " + tempImage.getHeight() + tempImage.getStride());
        bb = tempImage.getPixels();
        byte[] testByte = new byte[bb.remaining()];
        bb.duplicate().get(testByte, 0, testByte.length);
        Mat retImage = new Mat(tempImage.getHeight(), tempImage.getWidth(), CvType.CV_8UC3);
        retImage.put(0, 0, testByte);
        Renderer.getInstance().end();
        return retImage;

    }

}

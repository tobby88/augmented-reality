package eu.tobby.momentanpol;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Vector;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Frame;
import com.qualcomm.vuforia.Image;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.Marker;
import com.qualcomm.vuforia.MarkerResult;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;

import eu.tobby.momentanpol.objects.AObject;
import eu.tobby.momentanpol.objects.CObject;
import eu.tobby.momentanpol.objects.Plane;
import eu.tobby.momentanpol.objects.QObject;
import eu.tobby.momentanpol.objects.RObject;
import eu.tobby.momentanpol.utils.Texture;
import eu.tobby.momentanpol.utils.SampleUtils;
import eu.tobby.momentanpol.utils.CubeShaders;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by tobby on 11.06.15.
 */
public class MomentanpolGLRenderer implements GLSurfaceView.Renderer
{
    private Vector<Texture> mTextures;
// OpenGL ES 2.0 specific:
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int normalHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    private int texSampler2DHandle = 0;
    private Matrix44F mProjectionMatrix;


    // Constants:
    static private float kLetterScale = 1.0f;
    static private float kLetterTranslateX = 50.0f;
    static private float kLetterTranslateY = -84.0f;

    private QObject qObject = new QObject();
    private CObject cObject = new CObject();
    private AObject aObject = new AObject();
    private RObject rObject = new RObject();
    private Plane plane = new Plane();
    public volatile float mAngle=0;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }


    public MomentanpolGLRenderer(){}

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRendering();
        Vuforia.onSurfaceCreated();
    }

    public void onDrawFrame(GL10 gl) {

        renderFrame();
        findObject();
    }

    private void renderFrame() {

        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();
        //Log.e("renderFrame","state.getFrame" + state.getFrame());
        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        for(int trackingIndex = 0; trackingIndex < state.getNumTrackableResults(); trackingIndex++) {
            TrackableResult trackableResult = state.getTrackableResult(trackingIndex);

            float[] modelViewMatrix = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
            MarkerResult markerResult = (MarkerResult) (trackableResult);
            Marker marker = (Marker) markerResult.getTrackable();
            int textureIndex = marker.getMarkerId();
            Texture thisTexture = mTextures.get(textureIndex);

            Buffer vertices;
            Buffer normals;
            Buffer indices;
            Buffer texCoords;
            int numIndices;

            switch (marker.getMarkerId()) {
                case 0:
                    vertices = qObject.getVertices();
                    normals = qObject.getNormals();
                    indices = qObject.getIndices();
                    texCoords = qObject.getTexCoords();
                    numIndices = qObject.getNumObjectIndex();
                    break;
                case 1:
                    vertices = cObject.getVertices();
                    normals = cObject.getNormals();
                    indices = cObject.getIndices();
                    texCoords = cObject.getTexCoords();
                    numIndices = cObject.getNumObjectIndex();
                    break;
                case 2:
                    vertices =  aObject.getVertices();
                    normals = aObject.getNormals();
                    indices = aObject.getIndices();
                    texCoords = aObject.getTexCoords();
                    numIndices = aObject.getNumObjectIndex();
                    break;
                case 3:
                    vertices = rObject.getVertices();
                    normals = rObject.getNormals();
                    indices = rObject.getIndices();
                    texCoords = rObject.getTexCoords();
                    numIndices = rObject.getNumObjectIndex();
                    break;
                default:
                    vertices = plane.getVertices();
                    normals = plane.getNormals();
                    indices = plane.getIndices();
                    texCoords = plane.getTexCoords();
                    numIndices = plane.getNumObjectIndex();
            }
            float[] modelViewProjection = new float[16];


            Matrix.translateM(modelViewMatrix, 0, kLetterTranslateX,
                    kLetterTranslateY, 0.f);
            Matrix.rotateM(modelViewMatrix, 0, mAngle, 0.f, 0.f, -1);
            Matrix.translateM(modelViewMatrix, 0, -kLetterScale, -kLetterScale, 0);
            Matrix.scaleM(modelViewMatrix, 0, kLetterScale, kLetterScale,
                    kLetterScale);
            Matrix.multiplyMM(modelViewProjection, 0, getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

            GLES20.glUseProgram(shaderProgramID);
            //Hot Fix
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                    false, 0, vertices);
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
                    false, 0, normals);
            GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                    GLES20.GL_FLOAT, false, 0, texCoords);

            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, thisTexture.mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices,
                    GLES20.GL_UNSIGNED_SHORT, indices);

            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(normalHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);

        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        Renderer.getInstance().end();
    }


    private void findObject() {
        State state = Renderer.getInstance().begin();
        Frame frame = state.getFrame();
        for(int i=0;i<frame.getNumImages();i++) {
            Image image = frame.getImage(i);
            ByteBuffer bb = image.getPixels();
        }
        Renderer.getInstance().end();
    }


    // Called when the surface changed size.
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Call Vuforia function to handle render surface size changes:
        Vuforia.onSurfaceChanged(width, height);
    }

    public void setProjectionMatrix() {
        CameraCalibration camCal = CameraDevice.getInstance().getCameraCalibration();
        mProjectionMatrix = Tool.getProjectionGL(camCal, 10.0f, 5000.0f);
    }

    public Matrix44F getProjectionMatrix()
    {
        return mProjectionMatrix;
    }


    private void initRendering() {
        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);
        // Now generate the OpenGL texture objects and add settings
        for (Texture t : mTextures)
        {
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

    public void setTextures(Vector<Texture> textures) {
        mTextures = textures;

    }
}

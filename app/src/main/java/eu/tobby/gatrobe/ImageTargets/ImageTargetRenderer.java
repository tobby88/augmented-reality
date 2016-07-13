package eu.tobby.gatrobe.ImageTargets;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.vuforia.CameraCalibration;
import com.vuforia.CameraDevice;
import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import eu.tobby.gatrobe.Booobs;
import eu.tobby.gatrobe.interfaces.GatrobeRenderer;
import eu.tobby.gatrobe.objects.Plane;
import eu.tobby.gatrobe.utils.CubeShaders;
import eu.tobby.gatrobe.utils.SampleUtils;
import eu.tobby.gatrobe.utils.Texture;

/**
 * Specific Renderer for the Image Target Task
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 * @see GatrobeRenderer
 */
public class ImageTargetRenderer implements GatrobeRenderer {

    // OpenGL ES 2.0 specific:
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int normalHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    private int texSampler2DHandle = 0;
    private Matrix44F mProjectionMatrix;
    //Rendering Plane
    private Plane plane = new Plane();
    private Booobs booobs;
    private int lastID = -1;

    /**
     * Constructor which handles the acitivity context
     * @param activity: current activity
     */
    public ImageTargetRenderer(Activity activity) {
        booobs = new Booobs(activity);
    }

    /**
     * Getter method which returns the booobs
     * @return: current booobs
     */
    public Booobs getBooobs() {
        return booobs;
    }

    /**
     * Getter method for the last ID
     * @return: last ID
     */
    public int getLastID() {
        return lastID;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRendering();
        Vuforia.onSurfaceCreated();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        renderFrame();
    }

    /**
     * Method which gets the found markers and renders the objects relative to that
     */
    private void renderFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        State state = Renderer.getInstance().begin();
        Renderer.getInstance().drawVideoBackground();
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        // did we find any trackables this frame?
        if (state.getNumTrackableResults() > 0) {
            TrackableResult result = state.getTrackableResult(0);
            Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
            float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();
            // deal with the modelview and projection matrices
            float[] modelViewProjection = new float[16];
            Matrix.multiplyMM(modelViewProjection, 0, getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
            lastID = result.getTrackable().getId();
            Matrix.scaleM(modelViewProjection, 0, booobs.getBooobObj(lastID).getPlaneX(), booobs.getBooobObj(lastID).getPlaneY(), 0);
            // activate the shader program and bind the vertex/normal/tex coords
            GLES20.glUseProgram(shaderProgramID);
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, plane.getVertices());
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, plane.getNormals());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, plane.getTexCoords());
            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            Texture texture;
            texture = booobs.getBooobObj(lastID).getCurrentTexture();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, plane.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT, plane.getIndices());
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_BLEND);
        Renderer.getInstance().end();
    }

    /**
     * Initialization of the OpenGL 2.0 renderer
     */
    private void initRendering() {
        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(CubeShaders.CUBE_MESH_VERTEX_SHADER, CubeShaders.CUBE_MESH_FRAGMENT_SHADER);
        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexPosition");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID, "texSampler2D");
        setTextureSettings();
    }

    /**
     * Method that gets the specific parameters and configures OpenGL for every pic
     */
    private void setTextureSettings() {
        Texture texture;
        for (int i = 0; i < booobs.getNrOfBooobs(); i++) {
            for (int j = 1; j <= booobs.getBooobObj(booobs.getID(i)).getPics(); j++) {
                booobs.getBooobObj(booobs.getID(i)).setCurrentPic(j);
                texture = booobs.getBooobObj(booobs.getID(i)).getCurrentTexture();
                // Now generate the OpenGL texture object and add settings
                GLES20.glGenTextures(1, texture.mTextureID, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.mTextureID[0]);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texture.mWidth, texture.mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, texture.mData);
            }
            booobs.getBooobObj(booobs.getID(i)).setCurrentPic(1);
        }
    }

    @Override
    public void setProjectionMatrix() {
        CameraCalibration camCal = CameraDevice.getInstance().getCameraCalibration();
        mProjectionMatrix = Tool.getProjectionGL(camCal, 10.0f, 5000.0f);
    }

    @Override
    public Matrix44F getProjectionMatrix() {
        return mProjectionMatrix;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Call Vuforia function to handle render surface size changes:
        Vuforia.onSurfaceChanged(width, height);
    }

}

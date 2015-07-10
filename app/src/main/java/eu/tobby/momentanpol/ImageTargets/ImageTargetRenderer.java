package eu.tobby.momentanpol.ImageTargets;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.Vuforia;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import eu.tobby.momentanpol.Exercises;
import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.objects.Plane;
import eu.tobby.momentanpol.utils.CubeShaders;
import eu.tobby.momentanpol.utils.SampleUtils;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 02.07.15.
 */
public class ImageTargetRenderer implements MomentanpolRenderer {

    // OpenGL ES 2.0 specific:
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int normalHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    private int texSampler2DHandle = 0;
    private Matrix44F mProjectionMatrix;
    private Plane plane = new Plane();
    private Exercises exercises;
    private int lastID = -1;


    public ImageTargetRenderer(Activity activity) {
        exercises = new Exercises(activity);
    }


    public Exercises getExercises() {
        return exercises;
    }


    public int getLastID() {
        return lastID;
    }


    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRendering();
        Vuforia.onSurfaceCreated();
    }


    public void onDrawFrame(GL10 gl) {
        renderFrame();
    }


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
            Matrix.scaleM(modelViewProjection, 0, 11.0f, 8.5f, 0);
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
            lastID = result.getTrackable().getId();
            Texture texture;
            // Synchronize IDs of FrameMarker and ImageTargets (ID 1 of ImageTargets is ID 4 of FrameMarkers)
            texture = exercises.getExercise(lastID + 3).getCurrentTexture();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, plane.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT, plane.getIndices());
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_BLEND);
        Renderer.getInstance().end();
    }


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


    private void setTextureSettings() {
        Texture texture;
        for (int i = 0; i < exercises.getNrOfExercises(); i++) {
            for (int j = 1; j <= exercises.getExercise(exercises.getID(i)).getSteps(); j++) {
                exercises.getExercise(exercises.getID(i)).setCurrentStep(j);
                texture = exercises.getExercise(exercises.getID(i)).getCurrentTexture();
                // Now generate the OpenGL texture object and add settings
                GLES20.glGenTextures(1, texture.mTextureID, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.mTextureID[0]);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texture.mWidth, texture.mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, texture.mData);
            }
            exercises.getExercise(exercises.getID(i)).setCurrentStep(1);
        }
    }


    public void setProjectionMatrix() {
        CameraCalibration camCal = CameraDevice.getInstance().getCameraCalibration();
        mProjectionMatrix = Tool.getProjectionGL(camCal, 10.0f, 5000.0f);
    }


    public Matrix44F getProjectionMatrix() {
        return mProjectionMatrix;
    }


    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Call Vuforia function to handle render surface size changes:
        Vuforia.onSurfaceChanged(width, height);
    }

}

package eu.tobby.momentanpol.FrameMarker;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Marker;
import com.qualcomm.vuforia.MarkerResult;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.Vuforia;

import java.nio.Buffer;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import eu.tobby.momentanpol.exercises.Exercise1;
import eu.tobby.momentanpol.interfaces.ExerciseSheet;
import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.objects.Plane;
import eu.tobby.momentanpol.utils.CubeShaders;
import eu.tobby.momentanpol.utils.SampleUtils;
import eu.tobby.momentanpol.utils.Texture;


/**
 * Created by tobby on 11.06.15.
 */
public class FrameMarkerRenderer implements MomentanpolRenderer {
    public volatile float mAngle = 0;
    // OpenGL ES 2.0 specific:
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int normalHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    private int texSampler2DHandle = 0;
    private Matrix44F mProjectionMatrix;
    private Plane plane = new Plane();
    private Vector<ExerciseSheet> exercises = new Vector<>();


    public FrameMarkerRenderer(Activity activity) {
        exercises.add(new Exercise1(activity));
    }


    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRendering();
        Vuforia.onSurfaceCreated();
    }


    public void onDrawFrame(GL10 gl) {
        renderFrame();
    }


    private void renderFrame() {
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // Get the state from Vuforia and mark the beginning of a rendering section
        State state = Renderer.getInstance().begin();
        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        for(int trackingIndex = 0; trackingIndex < state.getNumTrackableResults(); trackingIndex++) {
            TrackableResult trackableResult = state.getTrackableResult(trackingIndex);

            float[] modelViewMatrix = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
            MarkerResult markerResult = (MarkerResult) (trackableResult);
            Marker marker = (Marker) markerResult.getTrackable();

            float kLetterScaleX;
            float kLetterScaleY;
            float kLetterTranslateX;
            float kLetterTranslateY;
            Buffer vertices = plane.getVertices();
            Buffer normals = plane.getNormals();
            Buffer indices = plane.getIndices();
            Buffer texCoords = plane.getTexCoords();
            int numIndices = plane.getNumObjectIndex();
            Texture texture;

            switch (marker.getMarkerId()) {
                case 4:
                    kLetterScaleX = exercises.get(0).getScaleX();
                    kLetterScaleY = exercises.get(0).getScaleY();
                    kLetterTranslateX = exercises.get(0).getTranslateX();
                    kLetterTranslateY = exercises.get(0).getTranslateY();
                    texture = exercises.get(0).getCurrentTexture();
                    break;
                default:
                    kLetterScaleX = 14.7f;
                    kLetterScaleY = 11.1f;
                    kLetterTranslateY = -85.0f;
                    kLetterTranslateX = 49.0f;

                    // just for testing purposes - please change this line!
                    texture = exercises.get(0).getCurrentTexture();
            }
            float[] modelViewProjection = new float[16];

            Matrix.translateM(modelViewMatrix, 0, kLetterTranslateX, kLetterTranslateY, 0.f);
            Matrix.rotateM(modelViewMatrix, 0, mAngle, 0.f, 0.f, -1);
            Matrix.scaleM(modelViewMatrix, 0, kLetterScaleX, kLetterScaleY, 25);
            Matrix.multiplyMM(modelViewProjection, 0, getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

            GLES20.glUseProgram(shaderProgramID);
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, vertices);
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normals);
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoords);
            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.mTextureID[0]);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
            GLES20.glUniform1i(texSampler2DHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, indices);
            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(normalHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_BLEND);
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


    public Matrix44F getProjectionMatrix() {
        return mProjectionMatrix;
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
        setTextureSettings(exercises.get(0));
    }

    private void setTextureSettings(ExerciseSheet exercise) {
        Texture texture;
        for (int i = 1; i <= exercise.getSteps(); i++) {
            exercise.setCurrentStep(i);
            texture = exercise.getCurrentTexture();
            // Now generate the OpenGL texture object and add settings
            GLES20.glGenTextures(1, texture.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texture.mWidth, texture.mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, texture.mData);
        }
        exercise.setCurrentStep(1);
    }

}

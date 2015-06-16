package eu.tobby.momentanpol;

/**
 * Created by tobby on 16.06.15.
 */
/*public class Vuforia_Renderer {
}*/

/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

//package com.qualcomm.vuforia.samples.SampleApplication.utils;

        import javax.microedition.khronos.egl.EGL10;
        import javax.microedition.khronos.egl.EGLConfig;
        import javax.microedition.khronos.egl.EGLContext;
        import javax.microedition.khronos.egl.EGLDisplay;

        import android.content.Context;
        import android.graphics.PixelFormat;
        import android.opengl.GLSurfaceView;
        import android.util.Log;
        import java.nio.Buffer;
        import java.util.Vector;

        import javax.microedition.khronos.egl.EGLConfig;
        import javax.microedition.khronos.opengles.GL10;

        import android.opengl.GLES20;
        import android.opengl.GLSurfaceView;
        import android.opengl.Matrix;
        import android.util.Log;

        import com.qualcomm.vuforia.Marker;
        import com.qualcomm.vuforia.MarkerResult;
        import com.qualcomm.vuforia.MarkerTracker;
        import com.qualcomm.vuforia.Renderer;
        import com.qualcomm.vuforia.State;
        import com.qualcomm.vuforia.Tool;
        import com.qualcomm.vuforia.TrackableResult;
        import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
        import com.qualcomm.vuforia.Vuforia;
        /*import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
        import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeShaders;
        import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
        import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;*/


// Support class for the Vuforia sample applications
// Responsible for setting up and configuring the OpenGL surface view.
// This class does not contain any Vuforia specific code.
// You can use your own OpenGL implementation.
class SampleApplicationGLView extends GLSurfaceView
{
    private static final String LOGTAG = "Vuforia_SampleGLView";


    // Constructor.
    public SampleApplicationGLView(Context context)
    {
        super(context);
    }


    // Initialization.
    public void init(boolean translucent, int depth, int stencil)
    {
        // By default GLSurfaceView tries to find a surface that is as close
        // as possible to a 16-bit RGB frame buffer with a 16-bit depth buffer.
        // This function can override the default values and set custom values.

        // By default, GLSurfaceView() creates a RGB_565 opaque surface.
        // If we want a translucent one, we should change the surface's
        // format here, using PixelFormat.TRANSLUCENT for GL Surfaces
        // is interpreted as any 32-bit surface with alpha by SurfaceFlinger.

        Log.i(LOGTAG, "Using OpenGL ES 2.0");
        Log.i(LOGTAG, "Using " + (translucent ? "translucent" : "opaque")
                + " GLView, depth buffer size: " + depth + ", stencil size: "
                + stencil);

        // If required set translucent format to allow camera image to
        // show through in the background
        if (translucent)
        {
            this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }

        // Setup the context factory for 2.0 rendering
        setEGLContextFactory(new ContextFactory());

        // We need to choose an EGLConfig that matches the format of
        // our surface exactly. This is going to be done in our
        // custom config chooser. See ConfigChooser class definition
        // below.
        setEGLConfigChooser(translucent ? new ConfigChooser(8, 8, 8, 8, depth,
                stencil) : new ConfigChooser(5, 6, 5, 0, depth, stencil));
    }

    // Creates OpenGL contexts.
    private static class ContextFactory implements
            GLSurfaceView.EGLContextFactory
    {
        private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;


        public EGLContext createContext(EGL10 egl, EGLDisplay display,
                                        EGLConfig eglConfig)
        {
            EGLContext context;

            Log.i(LOGTAG, "Creating OpenGL ES 2.0 context");
            checkEglError("Before eglCreateContext", egl);
            int[] attrib_list_gl20 = { EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL10.EGL_NONE };
            context = egl.eglCreateContext(display, eglConfig,
                    EGL10.EGL_NO_CONTEXT, attrib_list_gl20);

            checkEglError("After eglCreateContext", egl);
            return context;
        }


        public void destroyContext(EGL10 egl, EGLDisplay display,
                                   EGLContext context)
        {
            egl.eglDestroyContext(display, context);
        }
    }


    // Checks the OpenGL error.
    private static void checkEglError(String prompt, EGL10 egl)
    {
        int error;
        while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS)
        {
            Log.e(LOGTAG, String.format("%s: EGL error: 0x%x", prompt, error));
        }
    }

    // The config chooser.
    private static class ConfigChooser implements
            GLSurfaceView.EGLConfigChooser
    {
        public ConfigChooser(int r, int g, int b, int a, int depth, int stencil)
        {
            mRedSize = r;
            mGreenSize = g;
            mBlueSize = b;
            mAlphaSize = a;
            mDepthSize = depth;
            mStencilSize = stencil;
        }


        private EGLConfig getMatchingConfig(EGL10 egl, EGLDisplay display,
                                            int[] configAttribs)
        {
            // Get the number of minimally matching EGL configurations
            int[] num_config = new int[1];
            egl.eglChooseConfig(display, configAttribs, null, 0, num_config);

            int numConfigs = num_config[0];
            if (numConfigs <= 0)
                throw new IllegalArgumentException("No matching EGL configs");

            // Allocate then read the array of minimally matching EGL configs
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, configAttribs, configs, numConfigs,
                    num_config);

            // Now return the "best" one
            return chooseConfig(egl, display, configs);
        }


        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
        {
            // This EGL config specification is used to specify 2.0
            // rendering. We use a minimum size of 4 bits for
            // red/green/blue, but will perform actual matching in
            // chooseConfig() below.
            final int EGL_OPENGL_ES2_BIT = 0x0004;
            final int[] s_configAttribs_gl20 = { EGL10.EGL_RED_SIZE, 4,
                    EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4,
                    EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                    EGL10.EGL_NONE };

            return getMatchingConfig(egl, display, s_configAttribs_gl20);
        }


        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
                                      EGLConfig[] configs)
        {
            for (EGLConfig config : configs)
            {
                int d = findConfigAttrib(egl, display, config,
                        EGL10.EGL_DEPTH_SIZE, 0);
                int s = findConfigAttrib(egl, display, config,
                        EGL10.EGL_STENCIL_SIZE, 0);

                // We need at least mDepthSize and mStencilSize bits
                if (d < mDepthSize || s < mStencilSize)
                    continue;

                // We want an *exact* match for red/green/blue/alpha
                int r = findConfigAttrib(egl, display, config,
                        EGL10.EGL_RED_SIZE, 0);
                int g = findConfigAttrib(egl, display, config,
                        EGL10.EGL_GREEN_SIZE, 0);
                int b = findConfigAttrib(egl, display, config,
                        EGL10.EGL_BLUE_SIZE, 0);
                int a = findConfigAttrib(egl, display, config,
                        EGL10.EGL_ALPHA_SIZE, 0);

                if (r == mRedSize && g == mGreenSize && b == mBlueSize
                        && a == mAlphaSize)
                    return config;
            }

            return null;
        }


        private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                     EGLConfig config, int attribute, int defaultValue)
        {

            if (egl.eglGetConfigAttrib(display, config, attribute, mValue))
                return mValue[0];

            return defaultValue;
        }

        // Subclasses can adjust these values:
        protected int mRedSize;
        protected int mGreenSize;
        protected int mBlueSize;
        protected int mAlphaSize;
        protected int mDepthSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];
    }
}

/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

// The renderer class for the FrameMarkers sample.
class FrameMarkerRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "FrameMarkerRenderer";

    SampleApplicationSession vuforiaAppSession;
    OpenGL_Renderer mActivity;

    public boolean mIsActive = false;

    // OpenGL ES 2.0 specific:
    private int shaderProgramID = 0;
    private int vertexHandle = 0;
    private int normalHandle = 0;
    private int textureCoordHandle = 0;
    private int mvpMatrixHandle = 0;
    private int texSampler2DHandle = 0;

    // Constants:
    static private float kLetterScale = 25.0f;
    static private float kLetterTranslate = 25.0f;


    public FrameMarkerRenderer(OpenGL_Renderer activity,
                               SampleApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        // Call function to initialize rendering:
        initRendering();

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }


    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;

        // Call our function to render content
        renderFrame();
    }


    void initRendering()
    {
        Log.d(LOGTAG, "initRendering");

        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);


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


    void renderFrame()
    {
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();

        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // We must detect if background reflection is active and adjust the
        // culling direction.
        // If the reflection is active, this means the post matrix has been
        // reflected as well,
        // therefore standard counter clockwise face culling will result in
        // "inside out" models.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW);  // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW);   // Back camera

        // Did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
        {
            // Get the trackable:
            TrackableResult trackableResult = state.getTrackableResult(tIdx);
            float[] modelViewMatrix = Tool.convertPose2GLMatrix(
                    trackableResult.getPose()).getData();

            // Choose the texture based on the target name:
            int textureIndex = 0;

            // Check the type of the trackable:
            assert (trackableResult.getType() == MarkerTracker.getClassType());
            MarkerResult markerResult = (MarkerResult) (trackableResult);
            Marker marker = (Marker) markerResult.getTrackable();

            textureIndex = marker.getMarkerId();


            // Select which model to draw:
            Buffer vertices = null;
            Buffer normals = null;
            Buffer indices = null;
            Buffer texCoords = null;
            int numIndices = 0;


            float[] modelViewProjection = new float[16];

            Matrix.translateM(modelViewMatrix, 0, -kLetterTranslate,
                    -kLetterTranslate, 0.f);
            Matrix.scaleM(modelViewMatrix, 0, kLetterScale, kLetterScale,
                    kLetterScale);
            Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession
                    .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

            GLES20.glUseProgram(shaderProgramID);

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

            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                    modelViewProjection, 0);
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

}
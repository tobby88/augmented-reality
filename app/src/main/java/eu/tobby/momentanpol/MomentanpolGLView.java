package eu.tobby.momentanpol;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import eu.tobby.momentanpol.interfaces.MomentanpolState;

/**
 * Created by fabian on 30.06.15.
 */
public class MomentanpolGLView extends GLSurfaceView {

    private GLSurfaceView.Renderer mRenderer;
    private MomentanpolState mState;


    public MomentanpolGLView(Context context, GLSurfaceView.Renderer renderer, MomentanpolState state) {
        super(context);
        mRenderer = renderer;
        mState = state;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean retVal = false;
        if(e.getAction() == MotionEvent.ACTION_DOWN) {
            //Aufruf falls Screen berührt wurde
            mState.isActionDown();
            retVal = true;
        }
        return retVal;
    }

}

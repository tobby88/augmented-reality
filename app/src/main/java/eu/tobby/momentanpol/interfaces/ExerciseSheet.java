package eu.tobby.momentanpol.interfaces;

import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by tobby on 08.07.15.
 */
public interface ExerciseSheet {

    void setCurrentStep(int step);

    int getSteps();

    float getScaleX();

    float getScaleY();

    float getTranslateX();

    float getTranslateY();

    Texture getCurrentTexture();
}

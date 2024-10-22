package com.jws.jwsapi.scale;

import com.jws.jwsapi.shared.WeighRepository;

import javax.inject.Inject;

public class ScaleConformationManager {

    private final WeighRepository weighRepository;
    private final ScalePreferences scalePreferences;
    private int counterStable = 0;
    private boolean weightConformed = false;
    private ScaleConformationListener scaleConformationListener;

    @Inject
    public ScaleConformationManager(WeighRepository weighRepository, ScalePreferences scalePreferences) {
        this.weighRepository = weighRepository;
        this.scalePreferences = scalePreferences;
    }

    void evaluateWeightConformation(boolean stable) {
        double grossWeight = weighRepository.getGross();
        double zeroBand = scalePreferences.getZeroBand();
        int stableCountThreshold = scalePreferences.getStableCountThreshold();

        if (stable && grossWeight > zeroBand && !weightConformed) {
            counterStable++;
        } else {
            counterStable = 0;
        }

        if (counterStable >= stableCountThreshold) {
            if (scaleConformationListener != null) {
                scaleConformationListener.onWeightConformed();
            }
            weightConformed = true;
            counterStable = 0;
        }

        if (grossWeight < zeroBand) {
            weightConformed = false;
        }
    }

    public void setWeightListener(ScaleConformationListener scaleConformationListener) {
        this.scaleConformationListener = scaleConformationListener;
    }


}

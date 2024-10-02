package com.jws.jwsapi.service;

import com.jws.jwsapi.shared.WeighRepository;

import javax.inject.Inject;

public class WeightConformationManager {

    private final WeighRepository weighRepository;
    private final WeightPreferences weightPreferences;
    private int counterStable = 0;
    private boolean weightConformed = false;
    private WeightListener weightListener;

    @Inject
    public WeightConformationManager(WeighRepository weighRepository, WeightPreferences weightPreferences) {
        this.weighRepository = weighRepository;
        this.weightPreferences = weightPreferences;
    }

    void evaluateWeightConformation(boolean stable) {
        double grossWeight = weighRepository.getGross();
        double zeroBand = weightPreferences.getZeroBand();
        int stableCountThreshold = weightPreferences.getStableCountThreshold();

        if (stable && grossWeight > zeroBand && !weightConformed) {
            counterStable++;
        } else {
            counterStable = 0;
        }

        if (counterStable >= stableCountThreshold) {
            if (weightListener != null) {
                weightListener.onWeightConformed();
            }
            weightConformed = true;
            counterStable = 0;
        }

        if (grossWeight < zeroBand) {
            weightConformed = false;
        }
    }

    public void setWeightListener(WeightListener weightListener) {
        this.weightListener = weightListener;
    }


}

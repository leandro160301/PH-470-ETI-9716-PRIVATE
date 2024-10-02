package com.jws.jwsapi.service;

import com.jws.jwsapi.shared.WeighRepository;

public class WeightConformationManager {

    private static final int STABLE_COUNT_THRESHOLD = 15;
    private static final double ZERO_BAND = 49.0;
    private final WeighRepository weighRepository;
    private int counterStable = 0;
    private boolean weightConformed = false;
    private WeightListener weightListener;

    public WeightConformationManager(WeighRepository weighRepository) {
        this.weighRepository = weighRepository;
    }

    void evaluateWeightConformation(boolean stable) {
        double grossWeight = weighRepository.getGross();
        if (stable && grossWeight > ZERO_BAND && !weightConformed) {
            counterStable++;
        } else {
            counterStable = 0;
        }

        if (counterStable >= STABLE_COUNT_THRESHOLD) {
            if (weightListener != null) {
                weightListener.onWeightConformed();
            }
            weightConformed = true;
            counterStable = 0;
        }

        if (grossWeight < ZERO_BAND) {
            weightConformed = false;
        }
    }

    public void setWeightListener(WeightListener weightListener) {
        this.weightListener = weightListener;
    }


}

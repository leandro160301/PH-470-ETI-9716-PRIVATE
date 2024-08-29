package com.jws.jwsapi.feature.formulador.manager;

import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.utils.Utils;

public class BalanzaManager {
    private PreferencesManager preferencesManager;

    public BalanzaManager(PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }
    public int determinarBalanza(String kilos) {
        int modo = preferencesManager.getModoBalanza();
        int num = 1;
        if (Utils.isNumeric(kilos)) {
            float kilosFloat = Float.parseFloat(kilos);
            float bza1Limite = Float.parseFloat(preferencesManager.getBza1Limite());
            float bza2Limite = Float.parseFloat(preferencesManager.getBza2Limite());
            if (kilosFloat > bza2Limite && modo > 1) {
                num = 3;
            } else if (kilosFloat < bza2Limite && modo > 0) {
                num = 2;
            } else if (kilosFloat < bza1Limite) {
                num = 1;
            }
        }
        if (num == 1) {
            num = determinarBalanzaPorModo(modo);
        }

        return num;
    }

    private int determinarBalanzaPorModo(int modo) {
        int num = 0;
        switch (modo) {
            case 0:
                num = 1;
                break;
            case 1:
                num = 2;
                break;
            case 2:
                num = 3;
                break;
        }
        return num;
    }


}

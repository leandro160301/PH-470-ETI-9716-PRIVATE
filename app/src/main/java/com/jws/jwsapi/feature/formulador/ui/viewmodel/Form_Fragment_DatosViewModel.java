package com.jws.jwsapi.feature.formulador.ui.viewmodel;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class Form_Fragment_DatosViewModel extends ViewModel {
    private MutableLiveData<String> campo1;
    private MutableLiveData<String> campo2;
    private MutableLiveData<String> campo3;
    private MutableLiveData<String> campo4;
    private MutableLiveData<String> campo5;
    private MutableLiveData<Boolean> campo1_b = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> campo2_b = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> campo3_b = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> campo4_b = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> campo5_b = new MutableLiveData<>(false);
    private final PreferencesManager preferencesManager;

    @Inject
    public Form_Fragment_DatosViewModel(Application application) {
        this.preferencesManager = new PreferencesManager(application.getApplicationContext());
        campo1 = new MutableLiveData<>(preferencesManager.getCampo1());
        campo2 = new MutableLiveData<>(preferencesManager.getCampo2());
        campo3 = new MutableLiveData<>(preferencesManager.getCampo3());
        campo4 = new MutableLiveData<>(preferencesManager.getCampo4());
        campo5 = new MutableLiveData<>(preferencesManager.getCampo5());
        campo1_b.setValue(!campo1.getValue().isEmpty());
        campo2_b.setValue(!campo2.getValue().isEmpty());
        campo3_b.setValue(!campo3.getValue().isEmpty());
        campo4_b.setValue(!campo4.getValue().isEmpty());
        campo5_b.setValue(!campo5.getValue().isEmpty());
    }

    public MutableLiveData<String> getCampo1() {return campo1;}
    public MutableLiveData<String> getCampo2() {return campo2;}
    public MutableLiveData<String> getCampo3() {return campo3;}
    public MutableLiveData<String> getCampo4() {return campo4;}
    public MutableLiveData<String> getCampo5() {return campo5;}

    public void disableCampo1() {
        if(campo1_b.getValue() != null && campo1_b.getValue())setCampo1("");
    }
    public void disableCampo2() {
        if(campo2_b.getValue() != null && campo2_b.getValue())setCampo2("");
    }
    public void disableCampo3() {
        if(campo3_b.getValue() != null && campo3_b.getValue())setCampo3("");
    }
    public void disableCampo4() {
        if(campo4_b.getValue() != null && campo4_b.getValue())setCampo4("");
    }
    public void disableCampo5() {
        if(campo5_b.getValue() != null && campo5_b.getValue())setCampo5("");
    }

    public void setCampo1(String value) {
        campo1.setValue(value);
        campo1_b.setValue(!value.isEmpty());
        preferencesManager.setCampo1(value);
    }

    public void setCampo2(String value) {
        campo2.setValue(value);
        campo2_b.setValue(!value.isEmpty());
        preferencesManager.setCampo2(value);
    }

    public void setCampo3(String value) {
        campo3.setValue(value);
        campo3_b.setValue(!value.isEmpty());
        preferencesManager.setCampo3(value);
    }

    public void setCampo4(String value) {
        campo4.setValue(value);
        campo4_b.setValue(!value.isEmpty());
        preferencesManager.setCampo4(value);
    }

    public void setCampo5(String value) {
        campo5.setValue(value);
        campo5_b.setValue(!value.isEmpty());
        preferencesManager.setCampo5(value);
    }

}
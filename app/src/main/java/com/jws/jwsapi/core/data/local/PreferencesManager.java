package com.jws.jwsapi.core.data.local;

import com.jws.jwsapi.R;

import javax.inject.Inject;

public class PreferencesManager {

    private final PreferencesHelper preferencesHelper;

    @Inject
    public PreferencesManager(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public String consultaPIN() {
        return preferencesHelper.getString("PIN","1111");
    }

    public void nuevoPin(String pin){
        preferencesHelper.putString("PIN",pin);
    }

    public Boolean getCorreccionRemoto() {
        return preferencesHelper.getBoolean("Display",false);
    }

    public void setCorreccionRemoto(Boolean display){
        preferencesHelper.putBoolean("Display",display);
    }

    public int consultaTema() {
        return preferencesHelper.getInt("theme", R.style.AppTheme_NoActionBar);
    }

    public void nuevoTema(int tema){
        preferencesHelper.putInt("theme",tema);
    }

    public int getEthMode() {
        return preferencesHelper.getInt("ethmode",0);// 0=DHCP  1= ESTATICO
    }

    public void setEthMode(int modo){
        preferencesHelper.putInt("ethmode",modo);
    }

    public Boolean getAuthorization() {
        return preferencesHelper.getBoolean("AuthorizationEnable",false);
    }

    public void setAuthorization(Boolean auth){
        preferencesHelper.putBoolean("AuthorizationEnable",auth);
    }

    public String getIPstatic() {
        return preferencesHelper.getString("ethip","10.41.0.47");
    }

    public void setIPstatic(String ip){
        preferencesHelper.putString("ethip",ip);
    }

    public String getSubnet() {
        return preferencesHelper.getString("ethsub","255.255.255.0");
    }

    public void setSubnet(String ip){
        preferencesHelper.putString("ethsub",ip);
    }

    public String getGateway() {
        return preferencesHelper.getString("ethgateway","10.41.0.254");
    }

    public void setGateway(String ip){
        preferencesHelper.putString("ethgateway",ip);
    }

    public String getDNS1() {
        return preferencesHelper.getString("ethDNS1","10.41.0.112");
    }

    public void setDNS1(String ip){
        preferencesHelper.putString("ethDNS1",ip);
    }

    public String getDNS2() {
        return preferencesHelper.getString("ethDNS2","10.41.0.112");
    }

    public void setDNS2(String ip){
        preferencesHelper.putString("ethDNS2",ip);
    }

}

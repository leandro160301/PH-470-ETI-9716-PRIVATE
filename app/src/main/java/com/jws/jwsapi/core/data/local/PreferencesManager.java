package com.jws.jwsapi.core.data.local;

import com.jws.jwsapi.R;

import javax.inject.Inject;

public class PreferencesManager {

    private final PreferencesHelper preferencesHelper;
    private final static String KEY_PIN="PIN";
    private final static String KEY_REMOTE_FIX="REMOTE_FIX";
    private final static String KEY_THEME="THEME";
    private final static String KEY_AUTHORIZATION="AUTHORIZATION";
    private final static String KEY_ETH_MODE="ETH_MODE";
    private final static String KEY_ETH_IP="ETH_IP";
    private final static String KEY_ETH_SUBNET="ETH_SUBNET";
    private final static String KEY_ETH_GATEWAY="ETH_GATEWAY";
    private final static String KEY_ETH_DNS1="ETH_DNS1";
    private final static String KEY_ETH_DNS2="ETH_DNS2";

    @Inject
    public PreferencesManager(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public String getPin() {
        return preferencesHelper.getString(KEY_PIN,"1111");
    }

    public void setPin(String pin){
        preferencesHelper.putString(KEY_PIN,pin);
    }

    public boolean getRemoteFix() {
        return preferencesHelper.getBoolean(KEY_REMOTE_FIX,false);
    }

    public void setRemoteFix(Boolean enable){
        preferencesHelper.putBoolean(KEY_REMOTE_FIX,enable);
    }

    public int getTheme() {
        return preferencesHelper.getInt(KEY_THEME, R.style.AppTheme_NoActionBar);
    }

    public void setTheme(int tema){
        preferencesHelper.putInt(KEY_THEME,tema);
    }

    public int getEthMode() {
        return preferencesHelper.getInt(KEY_ETH_MODE,0);// 0=DHCP  1= ESTATICO
    }

    public void setEthMode(int modo){
        preferencesHelper.putInt(KEY_ETH_MODE,modo);
    }

    public boolean getAuthorization() {
        return preferencesHelper.getBoolean(KEY_AUTHORIZATION,false);
    }

    public void setAuthorization(Boolean auth){
        preferencesHelper.putBoolean(KEY_AUTHORIZATION,auth);
    }

    public String getIpStatic() {
        return preferencesHelper.getString(KEY_ETH_IP,"10.41.0.47");
    }

    public void setIpStatic(String ip){
        preferencesHelper.putString(KEY_ETH_IP,ip);
    }

    public String getSubnet() {
        return preferencesHelper.getString(KEY_ETH_SUBNET,"255.255.255.0");
    }

    public void setSubnet(String ip){
        preferencesHelper.putString(KEY_ETH_SUBNET,ip);
    }

    public String getGateway() {
        return preferencesHelper.getString(KEY_ETH_GATEWAY,"10.41.0.254");
    }

    public void setGateway(String ip){
        preferencesHelper.putString(KEY_ETH_GATEWAY,ip);
    }

    public String getDns1() {
        return preferencesHelper.getString(KEY_ETH_DNS1,"10.41.0.112");
    }

    public void setDns1(String ip){
        preferencesHelper.putString(KEY_ETH_DNS1,ip);
    }

    public String getDns2() {
        return preferencesHelper.getString(KEY_ETH_DNS2,"10.41.0.112");
    }

    public void setDns2(String ip){
        preferencesHelper.putString(KEY_ETH_DNS2,ip);
    }

}

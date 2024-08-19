package com.jws.jwsapi.base.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.jws.JwsManager;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.R;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

public class EthernetFragment extends Fragment  {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    LinearLayout ln0,ln1,ln2,ln3,ln4,ln5,ln6;
    TextView tvip,tvsubnet,tvgateway,tvdns1,tvdns2,tvMAC;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    private JwsManager jwsobject;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_ethernet,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();
        jwsobject = JwsManager.create(getContext());

        mainActivity=(MainActivity)getActivity() ;
        RadioGroup rGroup=view.findViewById(R.id.toggle);
        RadioGroup rGroup2=view.findViewById(R.id.toggle2);
        ln0=view.findViewById(R.id.ln0);
        ln1=view.findViewById(R.id.ln1);
        ln2=view.findViewById(R.id.ln2);
        ln3=view.findViewById(R.id.ln3);
        ln4=view.findViewById(R.id.ln4);
        ln5=view.findViewById(R.id.ln5);
        ln6=view.findViewById(R.id.ln6);
        tvMAC=view.findViewById(R.id.tvMAC);
        tvip=view.findViewById(R.id.tvip);
        tvsubnet=view.findViewById(R.id.tvsubnet);
        tvgateway=view.findViewById(R.id.tvgateway);
        tvdns1=view.findViewById(R.id.tvdns1);
        tvdns2=view.findViewById(R.id.tvdns2);

        tvip.setText(mainActivity.preferencesManagerBase.getIPstatic());
        tvsubnet.setText(mainActivity.preferencesManagerBase.getSubnet());
        tvgateway.setText(mainActivity.preferencesManagerBase.getGateway());
        tvdns1.setText(mainActivity.preferencesManagerBase.getDNS1());
        tvdns2.setText(mainActivity.preferencesManagerBase.getDNS2());
        tvMAC.setText(jwsobject.jwsGetEthMacAddress());

        tvip.setOnClickListener(view1 -> Teclado(tvip,"Ingrese la ip estatica"));
        tvsubnet.setOnClickListener(view12 -> Teclado(tvsubnet,"Ingrese la subnet"));
        tvgateway.setOnClickListener(view13 -> Teclado(tvgateway,"Ingrese el gateway"));
        tvdns1.setOnClickListener(view14 -> Teclado(tvdns1,"Ingrese la dns 1"));
        tvdns2.setOnClickListener(view15 -> Teclado(tvdns2,"Ingrese la dns 2"));

        if(jwsobject.jwsGetEthernetState()){
            rGroup.check(R.id.btON);
        }
        else{
            rGroup.check(R.id.btOFF);
            ln0.setVisibility(View.INVISIBLE);
            ln1.setVisibility(View.INVISIBLE);
            ln2.setVisibility(View.INVISIBLE);
            ln3.setVisibility(View.INVISIBLE);
            ln4.setVisibility(View.INVISIBLE);
            ln5.setVisibility(View.INVISIBLE);
            ln6.setVisibility(View.INVISIBLE);
        }

        if(mainActivity.preferencesManagerBase.getEthMode()==0){
            rGroup2.check(R.id.btON2);
        }
        else{
            rGroup2.check(R.id.btOFF2);
        }

        rGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if(rGroup.getCheckedRadioButtonId()==R.id.btON){
                jwsobject.jwsSetEthernetState(true);
                ln0.setVisibility(View.VISIBLE);
                ln1.setVisibility(View.VISIBLE);
                ln2.setVisibility(View.VISIBLE);
                ln3.setVisibility(View.VISIBLE);
                ln4.setVisibility(View.VISIBLE);
                ln5.setVisibility(View.VISIBLE);
                ln6.setVisibility(View.VISIBLE);
            }else{
                jwsobject.jwsSetEthernetState(false);
                ln0.setVisibility(View.INVISIBLE);
                ln1.setVisibility(View.INVISIBLE);
                ln2.setVisibility(View.INVISIBLE);
                ln3.setVisibility(View.INVISIBLE);
                ln4.setVisibility(View.INVISIBLE);
                ln5.setVisibility(View.INVISIBLE);
                ln6.setVisibility(View.INVISIBLE);
            }
        });
        rGroup2.setOnCheckedChangeListener((radioGroup, i) -> {
            if(rGroup2.getCheckedRadioButtonId()==R.id.btON2){
                mainActivity.preferencesManagerBase.setEthMode(0);// dinamico
                jwsobject.jwsSetEthDHCPAddress();
            }else{
                mainActivity.preferencesManagerBase.setEthMode(1);//estatico
                jwsobject.jwsSetEthStaticIPAddress(tvip.getText().toString(),tvsubnet.getText().toString(),tvgateway.getText().toString(),
                        tvdns1.getText().toString(),tvdns2.getText().toString());
            }
        });
    }

    public void Teclado(TextView View,String texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopcionespuntos, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        TextView textView=mView.findViewById(R.id.textViewt);
        LinearLayout lndelete_text=mView.findViewById(R.id.lndelete_text);
        textView.setText(texto);
        userInput.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        userInput.setKeyListener(DigitsKeyListener.getInstance(".0123456789"));

        userInput.setOnLongClickListener(v -> true);

        userInput.setText(View.getText().toString());
        userInput.requestFocus();

        if(!View.getText().toString().equals("") && !View.getText().toString().equals("-")){
            userInput.setSelection(userInput.getText().length());
        }
        lndelete_text.setOnClickListener(view -> userInput.setText(""));
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {

            if(View==tvip){
                if(!userInput.getText().toString().equals("")) {
                    mainActivity.preferencesManagerBase.setIPstatic(userInput.getText().toString());
                    View.setText(userInput.getText().toString());
                    if(mainActivity.preferencesManagerBase.getEthMode()==1){
                        jwsobject.jwsSetEthStaticIPAddress(tvip.getText().toString(),tvsubnet.getText().toString(),tvgateway.getText().toString(),
                                tvdns1.getText().toString(),tvdns2.getText().toString());
                    }
                }
            }
            if(View==tvsubnet){
                if(!userInput.getText().toString().equals("")) {
                    mainActivity.preferencesManagerBase.setSubnet(userInput.getText().toString());
                    View.setText(userInput.getText().toString());
                    if(mainActivity.preferencesManagerBase.getEthMode()==1){
                        jwsobject.jwsSetEthStaticIPAddress(tvip.getText().toString(),tvsubnet.getText().toString(),tvgateway.getText().toString(),
                                tvdns1.getText().toString(),tvdns2.getText().toString());
                    }
                }
            }
            if(View==tvgateway){
                if(!userInput.getText().toString().equals("")) {
                    mainActivity.preferencesManagerBase.setGateway(userInput.getText().toString());
                    View.setText(userInput.getText().toString());
                    if(mainActivity.preferencesManagerBase.getEthMode()==1){
                        jwsobject.jwsSetEthStaticIPAddress(tvip.getText().toString(),tvsubnet.getText().toString(),tvgateway.getText().toString(),
                                tvdns1.getText().toString(),tvdns2.getText().toString());
                    }
                }
            }
            if(View==tvdns1){
                if(!userInput.getText().toString().equals("")) {
                    mainActivity.preferencesManagerBase.setDNS1(userInput.getText().toString());
                    View.setText(userInput.getText().toString());
                    if(mainActivity.preferencesManagerBase.getEthMode()==1){
                        jwsobject.jwsSetEthStaticIPAddress(tvip.getText().toString(),tvsubnet.getText().toString(),tvgateway.getText().toString(),
                                tvdns1.getText().toString(),tvdns2.getText().toString());
                    }
                }
            }
            if(View==tvdns2){
                if(!userInput.getText().toString().equals("")) {
                    mainActivity.preferencesManagerBase.setDNS2(userInput.getText().toString());
                    View.setText(userInput.getText().toString());
                    if(mainActivity.preferencesManagerBase.getEthMode()==1){
                        jwsobject.jwsSetEthStaticIPAddress(tvip.getText().toString(),tvsubnet.getText().toString(),tvgateway.getText().toString(),
                                tvdns1.getText().toString(),tvdns2.getText().toString());
                    }
                }
            }

            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("ETHERNET");
            bt_1.setBackgroundResource(R.drawable.boton_atras_i);
            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_home.setOnClickListener(view -> mainActivity.openFragmentPrincipal());

        }
    }


    @Override
    public void onDestroyView() {
      //  stoped=true;
        super.onDestroyView();
    }


}



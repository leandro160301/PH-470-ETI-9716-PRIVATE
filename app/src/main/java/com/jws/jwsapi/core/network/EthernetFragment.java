package com.jws.jwsapi.core.network;

import static com.jws.jwsapi.dialog.DialogUtil.keyboardIpAdress;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.jws.JwsManager;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.databinding.StandarEthernetBinding;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EthernetFragment extends Fragment  {

    private static final int DYNAMIC_MODE = 0;
    private static final int STATIC_MODE = 1;
    private JwsManager jwsObject;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    private StandarEthernetBinding binding;
    @Inject
    PreferencesManager preferencesManagerBase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = StandarEthernetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        setupButtons();

        mainActivity=(MainActivity)getActivity() ;

        jwsObject = JwsManager.create(getContext());

        setupTextView();

        setOnClickListeners();

        setupToggle();

        handleToggle();
    }

    private void handleToggle() {
        binding.toggle.setOnCheckedChangeListener((radioGroup, i) -> {
            boolean isEthernetOn = binding.toggle.getCheckedRadioButtonId()==R.id.btON;
            jwsObject.jwsSetEthernetState(isEthernetOn);
            setupLinearVisibility(binding.toggle.getCheckedRadioButtonId()==R.id.btON ? View.VISIBLE : View.INVISIBLE);
        });
        binding.toggle2.setOnCheckedChangeListener((radioGroup, i) -> {
            int mode = binding.toggle2.getCheckedRadioButtonId()==R.id.btON2 ? DYNAMIC_MODE : STATIC_MODE;
            preferencesManagerBase.setEthMode(mode);
            if(mode == DYNAMIC_MODE) {
                jwsObject.jwsSetEthDHCPAddress();
            }else{
                setJwsStatic();
            }
        });
    }

    private void setupToggle() {
        if(jwsObject.jwsGetEthernetState()){
            binding.toggle.check(R.id.btON);
        }
        else{
            binding.toggle.check(R.id.btOFF);
            setupLinearVisibility(View.INVISIBLE);
        }
        binding.toggle2.check(preferencesManagerBase.getEthMode()==DYNAMIC_MODE? R.id.btON2 : R.id.btOFF2);
    }

    private void setOnClickListeners() {
        binding.tvip.setOnClickListener(v -> keyboardIpAdress(binding.tvip, getString(R.string.dialog_ethernet_ip), requireContext(), ip -> {
            preferencesManagerBase.setIPstatic(ip);
            setupStaticNetwork();
        }));

        binding.tvsubnet.setOnClickListener(v -> keyboardIpAdress(binding.tvip, getString(R.string.dialog_ethernet_subnet), requireContext(), subnet -> {
            preferencesManagerBase.setSubnet(subnet);
            setupStaticNetwork();
        }));

        binding.tvgateway.setOnClickListener(v -> keyboardIpAdress(binding.tvgateway, getString(R.string.dialog_ethernet_gateway), requireContext(), gateway -> {
            preferencesManagerBase.setGateway(gateway);
            setupStaticNetwork();
        }));

        binding.tvdns1.setOnClickListener(v -> keyboardIpAdress(binding.tvdns1, getString(R.string.dialog_ethernet_dns1), requireContext(), dns1 -> {
            preferencesManagerBase.setDNS1(dns1);
            setupStaticNetwork();
        }));

        binding.tvdns2.setOnClickListener(v -> keyboardIpAdress(binding.tvdns1, getString(R.string.dialog_ethernet_dns2), requireContext(), dns2 -> {
            preferencesManagerBase.setDNS2(dns2);
            setupStaticNetwork();
        }));
    }

    private void setupTextView() {
        binding.tvip.setText(preferencesManagerBase.getIPstatic());
        binding.tvsubnet.setText(preferencesManagerBase.getSubnet());
        binding.tvgateway.setText(preferencesManagerBase.getGateway());
        binding.tvdns1.setText(preferencesManagerBase.getDNS1());
        binding.tvdns2.setText(preferencesManagerBase.getDNS2());
        binding.tvMAC.setText(jwsObject.jwsGetEthMacAddress());
    }

    private void setupLinearVisibility(int visible) {
        binding.ln0.setVisibility(visible);
        binding.ln1.setVisibility(visible);
        binding.ln2.setVisibility(visible);
        binding.ln3.setVisibility(visible);
        binding.ln4.setVisibility(visible);
        binding.ln5.setVisibility(visible);
        binding.ln6.setVisibility(visible);
    }

    private void setupStaticNetwork() {
        if(preferencesManagerBase.getEthMode()==STATIC_MODE){
            setJwsStatic();
        }
    }

    private void setJwsStatic() {
        jwsObject.jwsSetEthStaticIPAddress(binding.tvip.getText().toString(),binding.
                        tvsubnet.getText().toString(),binding.tvgateway.getText().toString(),
                binding.tvdns1.getText().toString(),binding.tvdns2.getText().toString());
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getTitulo().setText(R.string.title_fragment_ethernet);
            buttonProvider.getButton1().setBackgroundResource(R.drawable.boton_atras_i);
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);

            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
        }
    }

}



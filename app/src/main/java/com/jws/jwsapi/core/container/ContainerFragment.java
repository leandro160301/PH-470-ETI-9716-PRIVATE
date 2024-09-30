package com.jws.jwsapi.core.container;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.jws.JwsManager;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.navigation.NavigationFragment;
import com.jws.jwsapi.core.storage.StorageDialogHandler;
import com.jws.jwsapi.core.storage.StorageService;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.databinding.ContainFragmentBinding;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.utils.NetworkUtils;
import com.jws.jwsapi.utils.date.DateUtils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContainerFragment extends Fragment implements ButtonProvider, ContainerData {

    ContainFragmentBinding binding;
    @Inject
    UserManager userManager;
    @Inject
    UserRepository userRepository;
    @Inject
    StorageService storageService;
    private int iconFlag = -1;
    private MainActivity mainActivity;
    private JwsManager jwsManager;
    private boolean stoped = false;

    public static ContainerFragment newInstance(Class<? extends Fragment> fragmentClass) {
        ContainerFragment fragment = new ContainerFragment();
        Bundle args = new Bundle();
        args.putString("FRAGMENT_CLASS", fragmentClass.getName());
        fragment.setArguments(args);
        return fragment;
    }

    public static ContainerFragment newInstanceService(Class<? extends Fragment> fragmentClass, Bundle arg, Boolean programador) {
        ContainerFragment fragment = new ContainerFragment();
        Bundle args = new Bundle();
        args.putString("FRAGMENT_CLASS", fragmentClass.getName());
        args.putBoolean("NIVEL", programador);
        args.putSerializable("instance", arg.getSerializable("instance"));
        args.putSerializable("instanceService", arg.getSerializable("instanceService"));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ContainFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initInstances();

        handleClickListeners();
        openFragment();
        startRunnable();
    }

    private void initInstances() {
        mainActivity = (MainActivity) getActivity();
        jwsManager = JwsManager.create(requireActivity());
    }

    private void handleClickListeners() {
        binding.lnMenu.setOnClickListener(view1 -> mainActivity.mainClass.openFragment(new NavigationFragment()));
        binding.lnUsuario.setOnClickListener(view13 -> userManager.loginDialog(mainActivity));
        binding.btWifi.setOnClickListener(view12 -> new ContainerDataDialog(this, mainActivity).showDialog());
    }

    @SuppressWarnings("unchecked")
    private void openFragment() {
        ButtonProviderSingleton.getInstance().setButtonProvider(this);
        if (getArguments() == null) return;
        String fragmentClassName = getArguments().getString("FRAGMENT_CLASS");
        if (fragmentClassName == null) return;

        try {
            Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) Class.forName(fragmentClassName);
            Fragment fragment = fragmentClass.newInstance();
            if (getArguments() != null) {
                Bundle args = getArguments();
                fragment.setArguments(args);
            }
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.nuevofragment, fragment)
                    .commit();
        } catch (ClassNotFoundException | java.lang.InstantiationException |
                 IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void startRunnable() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateUserUi();
                updateNetworkUi();
                updateDate();
                if (!stoped) {
                    handler.postDelayed(this, 100);
                }
            }
        };

        handler.post(runnable);
    }

    private void updateDate() {
        binding.btUsb.setVisibility(storageService.getState() ? View.VISIBLE : View.INVISIBLE);
        binding.tvFecha.setText(String.format("%s %s", DateUtils.getDate(), DateUtils.getHour()));
    }

    private void updateUserUi() {
        setupIconProgrammer(4, R.drawable.icono_programador);
        setupIconProgrammer(3, R.drawable.icono_administrador);
        setupIconProgrammer(2, R.drawable.icono_supervisor);
        setupIconProgrammer(1, R.drawable.icon_user);
        setupIconProgrammer(0, R.drawable.icono_nologin);
        binding.tvUsuario.setText(userRepository.getCurrentUser());
    }

    private void updateNetworkUi() {
        String type = jwsManager.jwsGetCurrentNetType();
        if (type == null) type = "";
        switch (type) {
            case "ETH":
                binding.btWifi.setBackgroundResource(R.drawable.icono_ethernet_white);
                break;
            case "WIFI":
                binding.btWifi.setBackgroundResource(R.drawable.wifi_white);
                break;
            default:
                binding.btWifi.setBackgroundResource(R.color.transparente);
                break;
        }
    }

    private void setupIconProgrammer(int x, int icono_programador) {
        if (userRepository.getLevelUser() == x && iconFlag != x) {
            binding.imuser.setImageResource(icono_programador);
            iconFlag = x;
        }
    }

    @Override
    public Button getButton1() {
        return binding.bt1;
    }

    @Override
    public Button getButton2() {
        return binding.bt2;
    }

    @Override
    public Button getButton3() {
        return binding.bt3;
    }

    @Override
    public Button getButton4() {
        return binding.bt4;
    }

    @Override
    public Button getButton5() {
        return binding.bt5;
    }

    @Override
    public Button getButton6() {
        return binding.bt6;
    }

    @Override
    public TextView getTitle() {
        return binding.tvTitulo;
    }

    @Override
    public Button getButtonHome() {
        return binding.btHome;
    }

    @Override
    public void onDestroyView() {
        stoped = true;
        super.onDestroyView();
    }

    @Override
    public String getIp() {
        return NetworkUtils.getIPAddress(true);
    }

    @Override
    public String getVersion() {
        return MainActivity.VERSION;
    }

    @Override
    public void openStorage() {
        StorageDialogHandler storageDialogHandler = new StorageDialogHandler(getContext());
        storageDialogHandler.showDialog();
    }

    @Override
    public boolean getStorageState() {
        return storageService.getState();
    }
}

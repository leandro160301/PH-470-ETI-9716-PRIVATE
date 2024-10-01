package com.jws.jwsapi.core.container;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jws.JwsManager;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.storage.StorageService;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.utils.date.DateUtils;

import javax.inject.Inject;

public class ContainerUI {

    private final UserRepository userRepository;
    private final StorageService storageService;
    private int iconFlag = -1;

    @Inject
    public ContainerUI(UserRepository userRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    void updateUserUi(ImageView imUser, TextView tvUser) {
        setupIconProgrammer(4, R.drawable.icono_programador, imUser);
        setupIconProgrammer(3, R.drawable.icono_administrador, imUser);
        setupIconProgrammer(2, R.drawable.icono_supervisor, imUser);
        setupIconProgrammer(1, R.drawable.icon_user, imUser);
        setupIconProgrammer(0, R.drawable.icono_nologin, imUser);
        tvUser.setText(userRepository.getCurrentUser());
    }

    private void setupIconProgrammer(int x, int icono_programador, ImageView imUser) {
        if (userRepository.getLevelUser() == x && iconFlag != x) {
            imUser.setImageResource(icono_programador);
            iconFlag = x;
        }
    }

    void updateNetworkUi(LinearLayout lnWifi, JwsManager jwsManager) {
        String type = jwsManager.jwsGetCurrentNetType();
        if (type == null) type = "";
        switch (type) {
            case "ETH":
                lnWifi.setBackgroundResource(R.drawable.icono_ethernet_white);
                break;
            case "WIFI":
                lnWifi.setBackgroundResource(R.drawable.wifi_white);
                break;
            default:
                lnWifi.setBackgroundResource(R.color.transparente);
                break;
        }
    }

    void updateDate(LinearLayout lnUsb, TextView tvDate) {
        lnUsb.setVisibility(storageService.getState() ? View.VISIBLE : View.INVISIBLE);
        tvDate.setText(String.format("%s %s", DateUtils.getDate(), DateUtils.getHour()));
    }

}


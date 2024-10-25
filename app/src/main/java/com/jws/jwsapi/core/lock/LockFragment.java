package com.jws.jwsapi.core.lock;

import static com.jws.jwsapi.core.lock.LockConstants.DAYS_1;
import static com.jws.jwsapi.core.lock.LockConstants.DAYS_2;
import static com.jws.jwsapi.core.lock.LockConstants.DAYS_3;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_PROGRAMMER;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardInt;
import static com.jws.jwsapi.utils.Utils.randomNumber;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.FragmentLockerBinding;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.jws.jwsapi.utils.date.DateUtils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LockFragment extends Fragment {

    private static final int randomCode = randomNumber();
    private static final String code1 = LockManager.getCode1(randomCode);
    private static final String code2 = LockManager.getCode2(randomCode);
    private static final String code3 = LockManager.getCode3(randomCode);
    private static final String code4 = LockManager.getCode4(randomCode);
    @Inject
    LockPreferences preferences;
    @Inject
    UserRepository userRepository;
    @Inject
    LockManager lockManager;
    private FragmentLockerBinding binding;
    private ButtonProvider buttonProvider;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = FragmentLockerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        if (userRepository.getLevelUser() < ROLE_PROGRAMMER) {
            binding.lnDay.setVisibility(View.GONE);
        } else {
            binding.lnLockCode.setVisibility(View.GONE);
            binding.lnLockMessage.setVisibility(View.GONE);
        }

        if (!lockManager.isLocked()) {
            if (!lockManager.isLockedEnabled()) {
                binding.lnLockCode.setVisibility(View.GONE);
                binding.lnLockMessage.setVisibility(View.GONE);
            } else {
                String message = "Quedan " + lockManager.remainingDays() + " dias restantes, envie el numero "
                        + randomCode + " a servicio tecnico para que le brinden el codigo";
                binding.tvLockMessage.setText(message);
            }

        } else {
            String message = "El equipo se encuentra bloqueado, envie el numero " + randomCode + " a servicio tecnico para que le brinden el codigo";
            binding.tvLockMessage.setText(message);
        }

        binding.tvDay.setText(preferences.getDay());

        binding.btCode.setOnClickListener(v -> keyboardInt(null, "Ingrese el codigo de desbloqueo", requireContext(),
                code -> {
                    if (code.equals(code1)) {
                        handleLockDay(String.valueOf(DAYS_1));
                        ToastHelper.message("Código válido", R.layout.item_customtoastok, requireContext());
                    } else if (code.equals(code2)) {
                        handleLockDay(String.valueOf(DAYS_2));
                        ToastHelper.message("Código válido", R.layout.item_customtoastok, requireContext());
                    } else if (code.equals(code3)) {
                        handleLockDay(String.valueOf(DAYS_3));
                        ToastHelper.message("Código válido", R.layout.item_customtoastok, requireContext());
                    } else if (code.equals(code4)) {
                        binding.tvDay.setText("");
                        preferences.setDay("");
                        ToastHelper.message("Código válido", R.layout.item_customtoastok, requireContext());
                    } else {
                        ToastHelper.message("Código no válido", R.layout.item_customtoasterror, requireContext());
                    }
                }));

        binding.tvDay.setOnClickListener(v -> keyboardInt(binding.tvDay, "Ingrese los dias a partir de hoy a vencer", requireContext(), days -> {
            if (Utils.isNumeric(days) && Integer.parseInt(days) > 0) {
                handleLockDay(days);
            } else {
                binding.tvDay.setText("");
                preferences.setDay("");
            }

        }));
        setupButtons();

    }

    private void handleLockDay(String days) {
        String dateFormatted = LockManager.calculateLockDay(days);
        preferences.setDay(dateFormatted);
        binding.tvDay.setText(dateFormatted);
        preferences.setLastDate(DateUtils.getDate());
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getButtonHome().setOnClickListener(v -> mainActivity.mainClass.openFragmentPrincipal());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
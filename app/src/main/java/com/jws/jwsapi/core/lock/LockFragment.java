package com.jws.jwsapi.core.lock;

import static com.jws.jwsapi.core.user.UserConstants.ROLE_PROGRAMMER;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardInt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.databinding.FragmentLockerBinding;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LockFragment extends Fragment {

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
        }

        if (!lockManager.isLocked()) {
            binding.lnLockCode.setVisibility(View.GONE);
            binding.lnLockMessage.setVisibility(View.GONE);
        }

        binding.tvDay.setText(preferences.getDay());

        binding.btCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.tvDay.setOnClickListener(v -> keyboardInt(binding.tvDay, "Ingrese los dias a partir de hoy a vencer", requireContext(), days -> {
            if (Utils.isNumeric(days) && Integer.parseInt(days) > 0) {
                Calendar calendar = Calendar.getInstance();
                int daysNumber = Integer.parseInt(days);
                calendar.add(Calendar.DAY_OF_YEAR, daysNumber);
                Date newDate = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String dateFormatted = dateFormat.format(newDate);
                binding.tvDay.setText(dateFormatted);
                preferences.setDay(dateFormatted);
            } else {
                binding.tvDay.setText("");
                preferences.setDay("");
            }

        }));
        setupButtons();

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
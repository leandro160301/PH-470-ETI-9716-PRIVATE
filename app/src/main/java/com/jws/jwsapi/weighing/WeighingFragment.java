package com.jws.jwsapi.weighing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.FragmentWeighingBinding;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WeighingFragment extends Fragment {

    MainActivity mainActivity;
    private WeighingAdapter weighingAdapter;
    private FragmentWeighingBinding binding;
    private ButtonProvider buttonProvider;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = FragmentWeighingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        WeighingViewModel weighingViewModel = new ViewModelProvider(this).get(WeighingViewModel.class);
        setupButtons();

        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        weighingAdapter = new WeighingAdapter(new ArrayList<>());
        binding.recycler.setAdapter(weighingAdapter);

        weighingViewModel.getWeighings().observe(getViewLifecycleOwner(), pallets -> {
            if (pallets != null) {
                weighingAdapter.updateData(pallets);
            }
        });

    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getButtonHome().setOnClickListener(view -> openHome());
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getTitle().setText(requireContext().getString(R.string.title_weighings));
        }
    }

    private void openHome() {
        mainActivity.mainClass.openFragmentPrincipal();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
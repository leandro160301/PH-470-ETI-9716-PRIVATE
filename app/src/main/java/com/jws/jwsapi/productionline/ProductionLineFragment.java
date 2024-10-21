package com.jws.jwsapi.productionline;

import static com.jws.jwsapi.dialog.DialogUtil.keyboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.FragmentProductionLineBinding;
import com.jws.jwsapi.databinding.FragmentWeighingBinding;
import com.jws.jwsapi.dialog.DialogInputInterface;
import com.jws.jwsapi.weighing.WeighingAdapter;
import com.jws.jwsapi.weighing.WeighingViewModel;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductionLineFragment extends Fragment {

    MainActivity mainActivity;
    @Inject
    ProductionLineManager productionLineManager;
    private FragmentProductionLineBinding binding;
    private ButtonProvider buttonProvider;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = FragmentProductionLineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        setupButtons();
        setupProductLine(productionLineManager.getProductionLineOne(), binding.tvProduct1,
                binding.tvBatch1, binding.tvCaliber1, binding.tvDestinatation1, binding.tvExpirateDate1);

        setupProductLine(productionLineManager.getProductionLineTwo(), binding.tvProduct2,
                binding.tvBatch2, binding.tvCaliber2, binding.tvDestinatation2, binding.tvExpirateDate2);

        setOnClickListeners();

    }

    private void setOnClickListeners() {
        binding.tvBatch1.setOnClickListener(v ->
                keyboard(binding.tvBatch1, "Ingrese el Lote 1", requireContext(), value ->
                productionLineManager.updateProductionLineBatchOne(value)));
        binding.tvCaliber1.setOnClickListener(v ->
                keyboard(binding.tvCaliber1, "Ingrese el Calibre 1", requireContext(), value ->
                productionLineManager.updateProductionLineCaliberOne(value)));
        binding.tvDestinatation1.setOnClickListener(v ->
                keyboard(binding.tvDestinatation1, "Ingrese el Destino 1", requireContext(), value ->
                productionLineManager.updateProductionLineDestinationOne(value)));
        binding.tvExpirateDate1.setOnClickListener(v ->
                keyboard(binding.tvExpirateDate1, "Ingrese el Vencimiento 1", requireContext(), value ->
                productionLineManager.updateProductionLineExpirateDateOne(value)));
        binding.tvProduct1.setOnClickListener(v ->
                keyboard(binding.tvProduct1, "Ingrese el Producto 1", requireContext(), value ->
                productionLineManager.updateProductionLineProductOne(value)));

        binding.tvBatch2.setOnClickListener(v ->
                keyboard(binding.tvBatch2, "Ingrese el Lote 2", requireContext(), value ->
                        productionLineManager.updateProductionLineBatchTwo(value)));
        binding.tvCaliber2.setOnClickListener(v ->
                keyboard(binding.tvCaliber2, "Ingrese el Calibre 2", requireContext(), value ->
                        productionLineManager.updateProductionLineCaliberTwo(value)));
        binding.tvDestinatation2.setOnClickListener(v ->
                keyboard(binding.tvDestinatation2, "Ingrese el Destino 2", requireContext(), value ->
                        productionLineManager.updateProductionLineDestinationTwo(value)));
        binding.tvExpirateDate2.setOnClickListener(v ->
                keyboard(binding.tvExpirateDate2, "Ingrese el Vencimiento 2", requireContext(), value ->
                        productionLineManager.updateProductionLineExpirateDateTwo(value)));
        binding.tvProduct2.setOnClickListener(v ->
                keyboard(binding.tvProduct2, "Ingrese el Producto 2", requireContext(), value ->
                        productionLineManager.updateProductionLineProductTwo(value)));

    }

    private void setupProductLine(ProductionLine productionLineManager, TextView binding, TextView binding1, TextView binding2, TextView binding3, TextView binding4) {
        binding.setText(productionLineManager.getProduct());
        binding1.setText(productionLineManager.getBatch());
        binding2.setText(productionLineManager.getCaliber());
        binding3.setText(productionLineManager.getDestinatation());
        binding4.setText(productionLineManager.getExpirateDate());
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getTitle().setText(requireContext().getString(R.string.title_weighings));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
package com.jws.jwsapi.line;

import static com.jws.jwsapi.dialog.DialogUtil.keyboard;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.caliber.CaliberRepository;
import com.jws.jwsapi.databinding.FragmentProductionLineBinding;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LineFragment extends Fragment {

    private MainActivity mainActivity;
    private FragmentProductionLineBinding binding;
    private LineDataViewModel viewModel;
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

        viewModel = new ViewModelProvider(this).get(LineDataViewModel.class);

        mainActivity = (MainActivity) getActivity();
        setupButtons();

        setupSpinners();

        setupResetButtons();

        setupProductLine(viewModel.getLineOne(), binding.tvProduct1,
                binding.tvBatch1, binding.tvDestinatation1, binding.tvExpirateDate1);

        setupProductLine(viewModel.getLineTwo(), binding.tvProduct2,
                binding.tvBatch2, binding.tvDestinatation2, binding.tvExpirateDate2);

        setOnClickListeners();

    }

    private void setupResetButtons() {
        if (viewModel.getCurrentLine() == 1) {
            binding.btReset2.setVisibility(View.INVISIBLE);
        } else {
            binding.btReset1.setVisibility(View.INVISIBLE);
        }
    }

    private void setupSpinners() {
        try {
            List<String> caliberElements = CaliberRepository.getCalibers(requireContext());
            setupSpinner(binding.spCaliber1, requireContext(), caliberElements);
            setupSpinner(binding.spCaliber2, requireContext(), caliberElements);
            binding.spCaliber1.setSelection(caliberElements.indexOf(viewModel.getLineOne().getCaliber()));
            binding.spCaliber2.setSelection(caliberElements.indexOf(viewModel.getLineTwo().getCaliber()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOnClickListeners() {
        binding.tvBatch1.setOnClickListener(v ->
                keyboard(binding.tvBatch1, "Ingrese el Lote 1", requireContext(), value ->
                        viewModel.updateBatchOne(value)));
        binding.tvDestinatation1.setOnClickListener(v ->
                keyboard(binding.tvDestinatation1, "Ingrese el Destino 1", requireContext(), value ->
                        viewModel.updateDestinationOne(value)));
        binding.tvExpirateDate1.setOnClickListener(v ->
                keyboard(binding.tvExpirateDate1, "Ingrese el Vencimiento 1", requireContext(), value ->
                        viewModel.updateExpirateOne(value)));
        binding.tvProduct1.setOnClickListener(v ->
                keyboard(binding.tvProduct1, "Ingrese el Producto 1", requireContext(), value ->
                        viewModel.updateProductOne(value)));

        binding.tvBatch2.setOnClickListener(v ->
                keyboard(binding.tvBatch2, "Ingrese el Lote 2", requireContext(), value ->
                        viewModel.updateBatchTwo(value)));

        binding.tvDestinatation2.setOnClickListener(v ->
                keyboard(binding.tvDestinatation2, "Ingrese el Destino 2", requireContext(), value ->
                        viewModel.updateDestinationTwo(value)));
        binding.tvExpirateDate2.setOnClickListener(v ->
                keyboard(binding.tvExpirateDate2, "Ingrese el Vencimiento 2", requireContext(), value ->
                        viewModel.updateExpirateTwo(value)));
        binding.tvProduct2.setOnClickListener(v ->
                keyboard(binding.tvProduct2, "Ingrese el Producto 2", requireContext(), value ->
                        viewModel.updateProductTwo(value)));

        View.OnClickListener onClickListener = v -> viewModel.finishWeight();

        binding.btReset1.setOnClickListener(onClickListener);
        binding.btReset2.setOnClickListener(onClickListener);

        binding.spCaliber1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    viewModel.updateCaliberOne(CaliberRepository.getCalibers(requireContext()).get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spCaliber2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    viewModel.updateCaliberTwo(CaliberRepository.getCalibers(requireContext()).get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setupProductLine(Line line, TextView bindingProduct, TextView bindingBatch, TextView bindingDestination, TextView bindingExpirateDate) {
        bindingProduct.setText(line.getProduct());
        bindingBatch.setText(line.getBatch());
        bindingDestination.setText(line.getDestinatation());
        bindingExpirateDate.setText(line.getExpirateDate());
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
            buttonProvider.getTitle().setText(requireContext().getString(R.string.title_production_line));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
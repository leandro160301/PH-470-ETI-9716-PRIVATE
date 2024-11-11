package com.jws.jwsapi.line;

import static com.jws.jwsapi.dialog.DialogUtil.keyboard;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardInt;
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
import com.jws.jwsapi.caliber.CaliberConstants;
import com.jws.jwsapi.caliber.CaliberRepository;
import com.jws.jwsapi.databinding.FragmentProductionLineBinding;
import com.jws.jwsapi.dialog.DialogInputInterface;
import com.jws.jwsapi.utils.date.DatePickerDialogFragment;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LineFragment extends Fragment implements DatePickerDialogFragment.DatePickerListener {

    List<String> caliberElements = new ArrayList<>();
    List<String> destinationElements = new ArrayList<>();
    private MainActivity mainActivity;
    private FragmentProductionLineBinding binding;
    private LineDataViewModel viewModel;
    private ButtonProvider buttonProvider;
    private Integer expirateDateNumber;
    private boolean isSpinnerInicializated1 = false;
    private boolean isSpinnerInicializated2 = false;


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
                binding.tvBatch1, binding.tvExpirateDate1, binding.tvPartsQuantity1);

        setupProductLine(viewModel.getLineTwo(), binding.tvProduct2,
                binding.tvBatch2, binding.tvExpirateDate2, binding.tvPartsQuantity2);

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
            caliberElements = CaliberRepository.getCalibers(requireContext(), CaliberConstants.NAME_CALIBER);
            destinationElements = CaliberRepository.getCalibers(requireContext(), CaliberConstants.NAME_DESTINATION);
            setupSpinner(binding.spCaliber1, requireContext(), caliberElements);
            setupSpinner(binding.spCaliber2, requireContext(), caliberElements);
            setupSpinner(binding.spDestination1, requireContext(), destinationElements);
            setupSpinner(binding.spDestination2, requireContext(), destinationElements);
            binding.spCaliber1.setSelection(caliberElements.indexOf(viewModel.getLineOne().getCaliber()));
            binding.spCaliber2.setSelection(caliberElements.indexOf(viewModel.getLineTwo().getCaliber()));
            binding.spDestination1.setSelection(destinationElements.indexOf(viewModel.getLineOne().getDestinatation()));
            binding.spDestination2.setSelection(destinationElements.indexOf(viewModel.getLineTwo().getDestinatation()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOnClickListeners() {
        setTextViewClickListener(binding.tvBatch1, "Ingrese el Lote 1", viewModel::updateBatchOne);
        setDateClickListener(binding.tvExpirateDate1, 1);
        setTextViewClickListener(binding.tvProduct1, "Ingrese el Producto 1", viewModel::updateProductOne);
        setTextViewClickListenerInt(binding.tvPartsQuantity1, "Ingrese la cantidad de piezas de la linea 1", viewModel::updatePartsQuantityOne);

        setTextViewClickListener(binding.tvBatch2, "Ingrese el Lote 2", viewModel::updateBatchTwo);
        setDateClickListener(binding.tvExpirateDate2, 2);
        setTextViewClickListener(binding.tvProduct2, "Ingrese el Producto 2", viewModel::updateProductTwo);
        setTextViewClickListenerInt(binding.tvPartsQuantity2, "Ingrese la cantidad de piezas de la linea 2", viewModel::updatePartsQuantityTwo);

        View.OnClickListener onClickListener = v -> viewModel.finishWeight();

        binding.btReset1.setOnClickListener(onClickListener);
        binding.btReset2.setOnClickListener(onClickListener);

        binding.spCaliber1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.updateCaliberOne(caliberElements.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spCaliber2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.updateCaliberTwo(caliberElements.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spDestination1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerInicializated1) {
                    viewModel.updateDestinationOne(destinationElements.get(position));
                    viewModel.resetLineQuantity(1);
                } else {
                    isSpinnerInicializated1 = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spDestination2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerInicializated2) {
                    viewModel.updateDestinationTwo(destinationElements.get(position));
                    viewModel.resetLineQuantity(2);
                } else {
                    isSpinnerInicializated2 = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setTextViewClickListener(TextView textView, String message, DialogInputInterface updateFunction) {
        textView.setOnClickListener(v -> keyboard(textView, message, requireContext(), updateFunction));
    }

    private void setTextViewClickListenerInt(TextView textView, String message, DialogInputInterface updateFunction) {
        textView.setOnClickListener(v -> keyboardInt(textView, message, requireContext(), updateFunction));
    }

    private void setDateClickListener(TextView textView, int id) {
        textView.setOnClickListener(v -> showDatePicker(id));
    }


    private void setupProductLine(Line line, TextView bindingProduct, TextView bindingBatch,
                                  TextView bindingExpirateDate, TextView bindingPartsQuantity) {
        bindingProduct.setText(line.getProduct());
        bindingBatch.setText(line.getBatch());
        bindingExpirateDate.setText(line.getExpirateDate());
        bindingPartsQuantity.setText(String.valueOf(line.getPartsQuantity()));
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

    public void showDatePicker(Integer number) {
        expirateDateNumber = number;
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setDatePickerListener(this);
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDateSelected(String selectedDate) {
        if (expirateDateNumber != null) {
            if (expirateDateNumber == 1) {
                binding.tvExpirateDate1.setText(selectedDate);
                viewModel.updateExpirateOne(selectedDate);
            }
            if (expirateDateNumber == 2) {
                binding.tvExpirateDate2.setText(selectedDate);
                viewModel.updateExpirateTwo(selectedDate);
            }
        }
    }
}
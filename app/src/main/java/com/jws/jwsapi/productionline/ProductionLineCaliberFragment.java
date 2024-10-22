package com.jws.jwsapi.productionline;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaIngredientesBinding;
import com.jws.jwsapi.dialog.DialogUtil;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.utils.ToastHelper;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductionLineCaliberFragment extends Fragment implements CaliberInterface {

    MainActivity mainActivity;
    CaliberAdapter adapter;
    List<String> elements;
    ProgFormuladorPantallaIngredientesBinding binding;
    @Inject
    UserRepository userRepository;
    private ButtonProvider buttonProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaIngredientesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        elements = ProductionLineCaliberRepository.getCalibers(requireContext());
        configuracionBotones();
        setupRecyclerView();

    }


    private void setupRecyclerView() {
        binding.listaIngredientes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CaliberAdapter(getContext(), elements, this);
        binding.listaIngredientes.setAdapter(adapter);

    }

    public void dialogNewElement() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_nuevo_calibre, null);
        TextView tvCaliber = mView.findViewById(R.id.tv_codigoingrediente);
        tvCaliber.setOnClickListener(view -> DialogUtil.keyboard(tvCaliber, "Ingrese el nuevo calibre", mainActivity, null));

        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);

        Guardar.setText("CREAR");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            String caliber = tvCaliber.getText().toString();
            if (!caliber.isEmpty()) {
                List<String> newList = ProductionLineCaliberRepository.getCalibers(requireContext());
                boolean exist = false;
                for (int i = 0; i < newList.size(); i++) {
                    if (caliber.equals(newList.get(i))) {
                        exist = true;
                    }
                }
                if (!exist) {
                    newList.add(caliber);
                    ProductionLineCaliberRepository.setCalibers(newList);
                    elements.add(caliber);
                    adapter.refreshList(elements);
                    binding.listaIngredientes.smoothScrollToPosition(elements.size() - 1);
                } else {
                    ToastHelper.message("Ya existe un ingrediente con el codigo ingresado", R.layout.item_customtoasterror, requireContext());
                }
            } else {
                ToastHelper.message("Los valores ingresados no son validos", R.layout.item_customtoasterror, requireContext());
            }
            dialog.cancel();

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }


    private void configuracionBotones() {
        if (buttonProvider != null) {
            Button bt_home = buttonProvider.getButtonHome();
            Button bt_1 = buttonProvider.getButton1();
            Button bt_2 = buttonProvider.getButton2();
            Button bt_3 = buttonProvider.getButton3();
            Button bt_4 = buttonProvider.getButton4();
            Button bt_5 = buttonProvider.getButton5();
            Button bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitle().setText(R.string.fragment_title_caliber);

            bt_1.setBackgroundResource(R.drawable.boton_add_i);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

            bt_1.setOnClickListener(view -> dialogNewElement());

        }
    }

    private void dialogDeleteElement(int posicion, List<String> mData) {
        DialogUtil.dialogText(mainActivity, "¿Quiere eliminar el ingrediente " + mData.get(posicion) + "?", "ELIMINAR", () -> {
            mData.remove(posicion);
            ProductionLineCaliberRepository.setCalibers(mData);
            adapter.refreshList(mData);
        });
    }

    @Override
    public void deleteElement(List<String> mData, int posicion) {
        if (mData.size() > 1) {
            if (userRepository.isEnabled()) {
                dialogDeleteElement(posicion, mData);
            } else {
                ToastHelper.message("No esta habilitado para modificar datos", R.layout.item_customtoasterror, mainActivity);
            }
        } else {
            ToastHelper.message("No puede eliminar mas ingredientes", R.layout.item_customtoasterror, mainActivity);
        }
    }
}



package com.jws.jwsapi.core.navigation;

import static com.jws.jwsapi.dialog.DialogUtil.keyboardInt;
import static com.jws.jwsapi.utils.Utils.randomNumber;
import static com.jws.jwsapi.utils.Utils.isNumeric;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.jws.JwsManager;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.core.label.LabelProgramFragment;
import com.jws.jwsapi.core.printer.PrinterFragment;
import com.jws.jwsapi.core.storage.StorageFragment;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.label.LabelFragment;
import com.jws.jwsapi.core.network.EthernetFragment;
import com.jws.jwsapi.core.user.UserFragment;
import com.jws.jwsapi.core.network.WifiFragment;
import com.jws.jwsapi.databinding.DialogoFechayhoraBinding;
import com.jws.jwsapi.databinding.DialogoPinBinding;
import com.jws.jwsapi.utils.ToastHelper;
import com.service.Balanzas.Fragments.ServiceFragment;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NavigationFragment extends Fragment implements NavigationAdapter.ItemClickListener {

    RecyclerView recycler1,recycler2,recycler3;
    LinearLayout lr_dinamico1,lr_dinamico2;

    private int menuElegido =0;
    private int menuElegido2 =2;
    NavigationAdapter adapter;
    JwsManager jwsManager;
    NavigationDynamicAdapter adapterDinamicos1, adapterDinamicos2;


    List<String> ListElementsArrayList=new ArrayList<>();
    List<String> ListElementsArrayListdinamicos1=new ArrayList<>();
    List<String> ListElementsArrayListdinamicos2=new ArrayList<>();
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    int num=0;
    @Inject
    UserManager userManager;
    @Inject
    PreferencesManager preferencesManagerBase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_menu,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onItemClick(View view, int position) {
        menuElegido =position;
        if(position==0){
            if(userManager.getLevelUser()>1){
                ServiceFragment fragment = ServiceFragment.newInstance(mainActivity.mainClass.service);
                Bundle args = new Bundle();
                args.putSerializable("instanceService", mainActivity.mainClass.service);
                mainActivity.mainClass.openFragmentService(fragment,args);
            }else{
                toastLoginError();
            }

        }
        if(position==1){
            ListElementsArrayListdinamicos1=new ArrayList<>();
            ListElementsArrayListdinamicos1.add("Programa");
            ListElementsArrayListdinamicos1.add("Balanzas");
            ListElementsArrayListdinamicos1.add("Turnos");
            ListElementsArrayListdinamicos1.add("Datos");
            ListElementsArrayListdinamicos1.add("Etiquetas");
            CargarDatosADinamico(ListElementsArrayListdinamicos1);
        }
        if(position==2){
            ListElementsArrayListdinamicos1=new ArrayList<>();
            ListElementsArrayListdinamicos1.add("Wifi");
            ListElementsArrayListdinamicos1.add("Ethernet");
            CargarDatosADinamico(ListElementsArrayListdinamicos1);
        }
        if(position==3){
            if(userManager.getLevelUser()>2){
                mainActivity.mainClass.openFragment(new UserFragment());
            }else{
                toastLoginError();
            }
        }
        if(position==4){
            ListElementsArrayListdinamicos1=new ArrayList<>();
            ListElementsArrayListdinamicos1.add("Fecha y Hora");
            ListElementsArrayListdinamicos1.add("Tema");
            CargarDatosADinamico(ListElementsArrayListdinamicos1);
        }
        if(position==5){
            ListElementsArrayListdinamicos1=new ArrayList<>();
            ListElementsArrayListdinamicos1.add("Impresora");
            ListElementsArrayListdinamicos1.add("Escaner");
            CargarDatosADinamico(ListElementsArrayListdinamicos1);
        }
        if(position==6){
            mainActivity.mainClass.openFragment(new StorageFragment());
        }
        if(position==7){
            mainActivity.clearCache();
        }
        if(position==8){
            newPinDialog();
        }

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        setupButtons();
        initializeViews(view);

    }


    private void initializeViews(View view) {
        recycler1=view.findViewById(R.id.recycler1);
        recycler2=view.findViewById(R.id.recycler2);
        recycler3=view.findViewById(R.id.recycler3);
        lr_dinamico1=view.findViewById(R.id.lr_dinamico1);
        lr_dinamico2=view.findViewById(R.id.lr_dinamico2);

        lr_dinamico1.setVisibility(View.INVISIBLE);
        lr_dinamico2.setVisibility(View.INVISIBLE);
        jwsManager= JwsManager.create(requireActivity());

        ListElementsArrayList=new ArrayList<>();
        ListElementsArrayList.add("Menu service");
        ListElementsArrayList.add("Menu configuracion");
        ListElementsArrayList.add("Comunicacion");
        ListElementsArrayList.add("Usuarios");
        ListElementsArrayList.add("Indicador");
        ListElementsArrayList.add("Dispositivos");
        ListElementsArrayList.add("Manuales/archivos");
        ListElementsArrayList.add("RESET");
        ListElementsArrayList.add("Nueva clave administrador");

        recycler1.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NavigationAdapter(getContext(), ListElementsArrayList);
        adapter.setClickListener(this);
        recycler1.setAdapter(adapter);


    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getTitulo().setText(R.string.title_fragment_menu);
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);

            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
        }
    }

    public void CargarDatosADinamico(List<String> lista){
        lr_dinamico1.setVisibility(View.VISIBLE);
        ListElementsArrayListdinamicos2=new ArrayList<>();
        CargarDatosADinamico2(ListElementsArrayListdinamicos2);
        recycler2.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDinamicos1 = new NavigationDynamicAdapter(getContext(), lista);
        recycler2.setAdapter(adapterDinamicos1);
        adapterDinamicos1.setClickListener((view, position) -> {
            menuElegido2=position;
            if(menuElegido ==1){
                if(userManager.getLevelUser()>1){
                    if(position==0){
//                        mainActivity.mainClass.openFragment(new FormFragmentConfiguracionPrograma());
                    }
                    if(position==1){
//                        mainActivity.mainClass.openFragment(new FormFragmentConfiguracionBalanza());
                    }
                    if(position==2){
//                        mainActivity.mainClass.openFragment(new FormFragmentConfiguracionTurnos());
                    }
                    if(position==3){
//                        mainActivity.mainClass.openFragment(new FormFragmentDatos());
                    }
                    if(position==4){
                        mainActivity.mainClass.openFragment(new LabelProgramFragment());
                    }
                }else{
                    toastLoginError();
                }

            }
            if(menuElegido ==2){
                if(position==0){
                    mainActivity.mainClass.openFragment(new WifiFragment());
                }
                if(position==1){
                    mainActivity.mainClass.openFragment(new EthernetFragment());
                }
            }

            if(menuElegido ==4){
                if(position==0){
                    if(userManager.getLevelUser()>1){
                        setDateDialog();
                    }
                    else{
                        toastLoginError();
                    }
                }
                if(position==1){
                    if(userManager.getLevelUser()>1){
                        setThemeDialog();
                    }
                    else{
                        toastLoginError();
                    }
                }
            }
            if(menuElegido ==5){
                if(position==0){
                    ListElementsArrayListdinamicos2=new ArrayList<>();
                    ListElementsArrayListdinamicos2.add("Configuracion");
                    ListElementsArrayListdinamicos2.add("Etiquetas");
                    CargarDatosADinamico2(ListElementsArrayListdinamicos2);

                }
                if(position==1){
                    ListElementsArrayListdinamicos2=new ArrayList<>();
                    CargarDatosADinamico2(ListElementsArrayListdinamicos2);

                }
                if(position==2){

                }

            }

        });

    }

    private void toastLoginError() {
        ToastHelper.message("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
    }

    public void CargarDatosADinamico2(List<String> lista){
        lr_dinamico2.setVisibility(View.VISIBLE);
        if(lista.size()<4){
            lr_dinamico2.setBackgroundResource(R.drawable.banner_menu_con_trasparencia_);
        }
        recycler3.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDinamicos2 = new NavigationDynamicAdapter(getContext(), lista);
        recycler3.setAdapter(adapterDinamicos2);
        adapterDinamicos2.setClickListener((view, position) -> {
            if(menuElegido ==5){
                if(userManager.getLevelUser()>1){
                    if(menuElegido2==0){
                        if(position==0){
                            mainActivity.mainClass.openFragment(new PrinterFragment());

                        }
                        if(position==1){
                            mainActivity.mainClass.openFragment(new LabelFragment());
                        }
                    }
                }
                else{
                    toastLoginError();
                }

            }
             if(menuElegido ==1&&menuElegido2==1){
                if(position==0){
                    mainActivity.mainClass.openFragment(new WifiFragment());
                }
                if(position==1){

                    if(userManager.getCurrentUser().equals("*Programador*")){
                        mainActivity.mainClass.openFragment(new EthernetFragment());
                    }
                    else{
                        ToastHelper.message("Debe ingresar la clave de programador para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
                    }

                }
            }

        });

    }


    public void setThemeDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_temas, null);

        Button Cancelar =  mView.findViewById(R.id.buttonc);
        TextView tvTema1 =  mView.findViewById(R.id.tvTema1);
        TextView tvTema2 =  mView.findViewById(R.id.tvTema2);
        TextView tvTema3 =  mView.findViewById(R.id.tvTema3);

        if(preferencesManagerBase.getTheme()==R.style.AppTheme_NoActionBar){
            setupTextTheme(tvTema1, "Tema Rojo (actual)");

        }
        if(preferencesManagerBase.getTheme()==R.style.AppTheme2_NoActionBar){
            setupTextTheme(tvTema2, "Tema Azul (actual)");

        }
        if(preferencesManagerBase.getTheme()==R.style.AppTheme4_NoActionBar){
            setupTextTheme(tvTema3, "Tema Negro (actual)");
        }

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        tvTema1.setOnClickListener(view -> setupTheme(R.style.AppTheme_NoActionBar, tvTema1, tvTema2, tvTema3));
        tvTema2.setOnClickListener(view -> setupTheme(R.style.AppTheme2_NoActionBar, tvTema2, tvTema1, tvTema3));
        tvTema3.setOnClickListener(view -> setupTheme(R.style.AppTheme4_NoActionBar, tvTema3, tvTema2, tvTema1));

        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    private static void setupTextTheme(TextView tvTema1, String text) {
        tvTema1.setText(text);
        tvTema1.setBackgroundResource(R.drawable.fondoinfoprincipal);
    }

    private void setupTheme(int appTheme_NoActionBar, TextView tvTema1, TextView tvTema2, TextView tvTema3) {
        preferencesManagerBase.setTheme(appTheme_NoActionBar);
        ToastHelper.message("Apague el equipo y vuelva a encenderlo para cambiar el tema",R.layout.item_customtoast,mainActivity);
        tvTema1.setBackgroundResource(R.drawable.fondoinfoprincipal);
        tvTema2.setBackgroundResource(R.drawable.stylekeycor3);
        tvTema3.setBackgroundResource(R.drawable.stylekeycor3);
    }


    public void setDateDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        DialogoFechayhoraBinding binding = DialogoFechayhoraBinding.inflate(getLayoutInflater());
        View mView = binding.getRoot();

        binding.tvMinutos.setOnLongClickListener(view -> {
            num=num+1;
            if(num==2){
                preferencesManagerBase.setRemoteFix(!preferencesManagerBase.getRemoteFix());
            }
            return false;
        });
        binding.tvMinutos.setOnClickListener(view -> keyboardInt(binding.tvMinutos, "Ingrese los minutos", requireContext(), minutes -> checkMinutes(minutes,binding.tvMinutos)));
        binding.tvHora.setOnClickListener(view -> keyboardInt(binding.tvHora, "Ingrese la hora", requireContext(), hour -> checkHour(hour,binding.tvHora)));
        binding.tvDia.setOnClickListener(view -> keyboardInt(binding.tvDia, "Ingrese el dia", requireContext(), day -> checkDay(day,binding.tvDia)));
        binding.tvMes.setOnClickListener(view -> keyboardInt(binding.tvMes, "Ingrese el mes", requireContext(), month -> checkMonth(month,binding.tvMes)));
        binding.tvAno.setOnClickListener(view -> keyboardInt(binding.tvAno, "Ingrese el año", requireContext(), year -> checkYear(year,binding.tvAno)));

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        binding.buttons.setOnClickListener(view -> {
            String day=binding.tvDia.getText().toString();
            String hour=binding.tvHora.getText().toString();
            String minutes=binding.tvMinutos.getText().toString();
            String month=binding.tvMes.getText().toString();
            String year=binding.tvAno.getText().toString();
            if(isNumeric(day)&&isNumeric(hour)&&isNumeric(minutes)&&isNumeric(month)&&isNumeric(year)){
                jwsManager.jwsSetTime(getContext(),Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day)
                        ,Integer.parseInt(hour),Integer.parseInt(minutes));
                dialog.cancel();
            }
        });
        binding.buttonc.setOnClickListener(view -> dialog.cancel());

    }

    private void checkMinutes(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) > 59) {
            textView.setText("");
        }
    }

    private void checkYear(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) >= 2200) {
            textView.setText("");
        }
    }

    private void checkMonth(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) > 12) {
            textView.setText("");
        }
    }

    private void checkDay(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) > 31) {
            textView.setText("");
        }
    }

    private void checkHour(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) > 24) {
            textView.setText("");
        }
    }


    public void newPinDialog() {
        final String[] pin = {"error"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        DialogoPinBinding binding = DialogoPinBinding.inflate(getLayoutInflater());

        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        binding.tvpin.setOnClickListener(view -> keyboardInt(binding.tvpin, null, requireContext(), null));
        binding.btGenerar.setOnClickListener(view -> {
            int Codigo = randomNumber();
            pin[0] = String.valueOf(((Codigo + 3031) * 6) / 4);
            binding.tvCodigo.setText(String.valueOf(Codigo));
        });

        binding.buttonc.setOnClickListener(view -> dialog.cancel());
        binding.buttons.setOnClickListener(view -> {
            if (!binding.tvpin.getText().toString().equals("error")) {
                if (binding.tvpin.getText().toString().equals(pin[0])) {
                    preferencesManagerBase.setPin(pin[0]);
                    ToastHelper.message("PIN CORRECTO", R.layout.item_customtoastok, requireContext());
                    dialog.cancel();
                }
            }
        });
    }


}



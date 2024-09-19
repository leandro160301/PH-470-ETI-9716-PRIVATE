package com.jws.jwsapi.core.navigation;

import static com.jws.jwsapi.utils.Utils.isNumeric;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.jws.JwsManager;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.core.label.LabelFragment;
import com.jws.jwsapi.core.label.LabelProgramFragment;
import com.jws.jwsapi.core.network.EthernetFragment;
import com.jws.jwsapi.core.network.WifiFragment;
import com.jws.jwsapi.core.printer.PrinterFragment;
import com.jws.jwsapi.core.storage.StorageFragment;
import com.jws.jwsapi.core.user.UserFragment;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.core.user.UserPinDialog;
import com.jws.jwsapi.core.user.UserPinInterface;
import com.jws.jwsapi.utils.AdapterCommon;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.date.DateDialog;
import com.jws.jwsapi.utils.date.DateInterface;
import com.service.Balanzas.Fragments.ServiceFragment;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NavigationFragment extends Fragment implements AdapterCommon.ItemClickListener, DateInterface, UserPinInterface, ThemeInterface {

    RecyclerView recycler1,recycler2,recycler3;
    LinearLayout lr_dinamico1,lr_dinamico2;
    private int menuElegido =0;
    private int menuElegido2 =2;
    AdapterCommon adapter;
    JwsManager jwsManager;
    NavigationAdapter adapterDinamicos1, adapterDinamicos2;

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
            new UserPinDialog(requireContext(),this);
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
        adapter = new AdapterCommon(getContext(), ListElementsArrayList);
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
        adapterDinamicos1 = new NavigationAdapter(getContext(), lista);
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
                        new DateDialog(this,getContext()).showDialog();
                    }
                    else{
                        toastLoginError();
                    }
                }
                if(position==1){
                    if(userManager.getLevelUser()>1){
                        new ThemeDialog(requireContext(),this).showDialog();
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
        adapterDinamicos2 = new NavigationAdapter(getContext(), lista);
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


    @Override
    public int getPreferencesManagerBaseTheme() {
        return preferencesManagerBase.getTheme();
    }

    @Override
    public void setupTheme(int appTheme_NoActionBar) {
        preferencesManagerBase.setTheme(appTheme_NoActionBar);
    }

    @Override
    public void setRemoteFix() {
        preferencesManagerBase.setRemoteFix(!preferencesManagerBase.getRemoteFix());
    }

    @Override
    public boolean setDate(String day, String hour, String minutes, String month, String year) {
        if(isNumeric(day)&&isNumeric(hour)&&isNumeric(minutes)&&isNumeric(month)&&isNumeric(year)){
            jwsManager.jwsSetTime(getContext(),Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day)
                    ,Integer.parseInt(hour),Integer.parseInt(minutes));
            return true;
        }
        return false;
    }


    @Override
    public boolean setupPin(String newPin, String pinFromTv) {
        if (!pinFromTv.equals("error")) {
            if (pinFromTv.equals(newPin)) {
                preferencesManagerBase.setPin(newPin);
                ToastHelper.message("PIN CORRECTO", R.layout.item_customtoastok, mainActivity);
                return true;
            }
        }
        return false;
    }
}



package com.jws.jwsapi.general.navigation;

import static com.jws.jwsapi.general.utils.Utils.devuelveCodigoUnico;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.jws.JwsManager;
import com.jws.jwsapi.general.label.LabelProgramFragment;
import com.jws.jwsapi.general.printer.PrinterFragment;
import com.jws.jwsapi.general.files.ManualesArchivosFragment;
import com.jws.jwsapi.general.MainActivity;
import com.jws.jwsapi.general.user.UsersManager;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.label.LabelFragment;
import com.jws.jwsapi.general.network.EthernetFragment;
import com.jws.jwsapi.general.user.UsersFragment;
import com.jws.jwsapi.general.network.WifiFragment;
import com.jws.jwsapi.general.utils.Utils;
import com.service.Balanzas.Fragments.ServiceFragment;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NavigationFragment extends Fragment implements NavigationAdapter.ItemClickListener {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    RecyclerView recycler1,recycler2,recycler3;
    LinearLayout lr_dinamico1,lr_dinamico2;
    private int minutos=0, hora=0,
            dia=0, mes=0, anio=0, cantidadUsuarios =0, menuElegido =0,menuElegido2 =2;
    NavigationAdapter adapter;
    JwsManager jwsManager;
    NavigationDynamicAdapter adapterDinamicos1, adapterDinamicos2;
    String pin="error";
    String[] ListElements = new String[] {},ListElementsdinamicos1 = new String[] {},
            ListElementsdinamicos2 = new String[] {};
    List<String> ListElementsArrayList=new ArrayList<>(Arrays.asList(ListElements));
    List<String> ListElementsArrayListdinamicos1=new ArrayList<>(Arrays.asList(ListElementsdinamicos1));
    List<String> ListElementsArrayListdinamicos2=new ArrayList<>(Arrays.asList(ListElementsdinamicos2));
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    TextView tv_minutos, tv_hora, tv_dia, tv_mes, tv_anio;
    int num=0;
    @Inject
    UsersManager usersManager;

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
            if(usersManager.getNivelUsuario()>1){
                ServiceFragment fragment = ServiceFragment.newInstance(mainActivity.mainClass.service);
                Bundle args = new Bundle();
                args.putSerializable("instanceService", mainActivity.mainClass.service);
                mainActivity.mainClass.openFragmentService(fragment,args);
            }else{
                Utils.Mensaje("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(position==1){
            ListElementsArrayListdinamicos1=new ArrayList<>(Arrays.asList(ListElementsdinamicos1));
            ListElementsArrayListdinamicos1.add("Programa");
            ListElementsArrayListdinamicos1.add("Balanzas");
            ListElementsArrayListdinamicos1.add("Turnos");
            ListElementsArrayListdinamicos1.add("Datos");
            ListElementsArrayListdinamicos1.add("Etiquetas");
            CargarDatosADinamico(ListElementsArrayListdinamicos1);
        }
        if(position==2){
            ListElementsArrayListdinamicos1=new ArrayList<>(Arrays.asList(ListElementsdinamicos1));
            ListElementsArrayListdinamicos1.add("Wifi");
            ListElementsArrayListdinamicos1.add("Ethernet");
           // ListElementsArrayListdinamicos1.add("Acceso Remoto");
            CargarDatosADinamico(ListElementsArrayListdinamicos1);
        }
        if(position==3){
            if(usersManager.getNivelUsuario()>2){
                mainActivity.mainClass.openFragment(new UsersFragment());
            }else{
                Utils.Mensaje("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
            }
        }
        if(position==4){
            ListElementsArrayListdinamicos1=new ArrayList<>(Arrays.asList(ListElementsdinamicos1));
            ListElementsArrayListdinamicos1.add("Fecha y Hora");
            ListElementsArrayListdinamicos1.add("Tema");
            CargarDatosADinamico(ListElementsArrayListdinamicos1);
        }
        if(position==5){
            ListElementsArrayListdinamicos1=new ArrayList<>(Arrays.asList(ListElementsdinamicos1));
            ListElementsArrayListdinamicos1.add("Impresora");
            ListElementsArrayListdinamicos1.add("Escaner");
            CargarDatosADinamico(ListElementsArrayListdinamicos1);
        }
        if(position==6){
            mainActivity.mainClass.openFragment(new ManualesArchivosFragment());
        }
        if(position==7){
            mainActivity.clearCache();
        }
        if(position==8){
            DialogoNuevaPIN();
        }

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        initializeViews(view);
        setOnClickListeners();

    }

    private void setOnClickListeners() {

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

        cantidadUsuarios = usersManager.cantidadUsuarios();


        ListElementsArrayList=new ArrayList<>(Arrays.asList(ListElements));
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

    private void configuracionBotones() {
        if (buttonProvider != null) {
            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("MENU");

            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }

    public void CargarDatosADinamico(List<String> lista){
        lr_dinamico1.setVisibility(View.VISIBLE);
        ListElementsArrayListdinamicos2=new ArrayList<>(Arrays.asList(ListElementsdinamicos2));
        CargarDatosADinamico2(ListElementsArrayListdinamicos2);
        recycler2.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDinamicos1 = new NavigationDynamicAdapter(getContext(), lista);
        recycler2.setAdapter(adapterDinamicos1);
        adapterDinamicos1.setClickListener((view, position) -> {
            menuElegido2=position;
            if(menuElegido ==1){
                if(usersManager.getNivelUsuario()>1){
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
                    Utils.Mensaje("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
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
                    if(usersManager.getNivelUsuario()>1){
                        DialogoCambiarHorayFecha();
                    }
                    else{
                        Utils.Mensaje("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
                    }
                }
                if(position==1){
                    if(usersManager.getNivelUsuario()>1){
                        DialogoCambiarTema();
                    }
                    else{
                        Utils.Mensaje("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
                    }
                }
            }
            if(menuElegido ==5){
                if(position==0){
                    ListElementsArrayListdinamicos2=new ArrayList<>(Arrays.asList(ListElementsdinamicos2));
                    ListElementsArrayListdinamicos2.add("Configuracion");
                    ListElementsArrayListdinamicos2.add("Etiquetas");
                    CargarDatosADinamico2(ListElementsArrayListdinamicos2);

                }
                if(position==1){
                    ListElementsArrayListdinamicos2=new ArrayList<>(Arrays.asList(ListElementsdinamicos2));
                    CargarDatosADinamico2(ListElementsArrayListdinamicos2);

                }
                if(position==2){

                }

            }

        });

    }

    public void CargarDatosADinamico2(List<String> lista){
       // lastVisiblePosition2=-1;
        lr_dinamico2.setVisibility(View.VISIBLE);
        if(lista.size()<4){
            lr_dinamico2.setBackgroundResource(R.drawable.banner_menu_con_trasparencia_);
        }
        recycler3.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDinamicos2 = new NavigationDynamicAdapter(getContext(), lista);
        recycler3.setAdapter(adapterDinamicos2);
        adapterDinamicos2.setClickListener((view, position) -> {
            if(menuElegido ==5){
                if(usersManager.getNivelUsuario()>1){
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
                    Utils.Mensaje("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
                }

            }
             if(menuElegido ==1&&menuElegido2==1){
                if(position==0){
                    mainActivity.mainClass.openFragment(new WifiFragment());
                }
                if(position==1){

                    if(usersManager.getUsuarioActual().equals("*Programador*")){
                        mainActivity.mainClass.openFragment(new EthernetFragment());
                    }
                    else{
                        Utils.Mensaje("Debe ingresar la clave de programador para acceder a esta configuracion",R.layout.item_customtoasterror,mainActivity);
                    }

                }
            }

        });

    }


    public void DialogoCambiarTema(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_temas, null);

        Button Cancelar =  mView.findViewById(R.id.buttonc);
        TextView tvTema1 =  mView.findViewById(R.id.tvTema1);
        TextView tvTema2 =  mView.findViewById(R.id.tvTema2);
        TextView tvTema3 =  mView.findViewById(R.id.tvTema3);
        //tvTema3.setVisibility(View.INVISIBLE);

        if(mainActivity.preferencesManagerBase.consultaTema()==R.style.AppTheme_NoActionBar){
            tvTema1.setText("Tema Rojo (actual)");
            tvTema1.setBackgroundResource(R.drawable.fondoinfoprincipal);

        }
        if(mainActivity.preferencesManagerBase.consultaTema()==R.style.AppTheme2_NoActionBar){
            tvTema2.setText("Tema Azul (actual)");
            tvTema2.setBackgroundResource(R.drawable.fondoinfoprincipal);

        }
        if(mainActivity.preferencesManagerBase.consultaTema()==R.style.AppTheme4_NoActionBar){
            tvTema3.setText("Tema Negro (actual)");
            tvTema3.setBackgroundResource(R.drawable.fondoinfoprincipal);
        }

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        tvTema1.setOnClickListener(view -> {
            mainActivity.preferencesManagerBase.nuevoTema(R.style.AppTheme_NoActionBar);
            Utils.Mensaje("Apague el equipo y vuelva a encenderlo para cambiar el tema",R.layout.item_customtoast,mainActivity);
            tvTema1.setBackgroundResource(R.drawable.fondoinfoprincipal);
            tvTema2.setBackgroundResource(R.drawable.stylekeycor3);
            tvTema3.setBackgroundResource(R.drawable.stylekeycor3);
        });
        tvTema2.setOnClickListener(view -> {
            mainActivity.preferencesManagerBase.nuevoTema(R.style.AppTheme2_NoActionBar);
            Utils.Mensaje("Apague el equipo y vuelva a encenderlo para cambiar el tema",R.layout.item_customtoast,mainActivity);
            tvTema2.setBackgroundResource(R.drawable.fondoinfoprincipal);
            tvTema1.setBackgroundResource(R.drawable.stylekeycor3);
            tvTema3.setBackgroundResource(R.drawable.stylekeycor3);
        });
        tvTema3.setOnClickListener(view -> {
            mainActivity.preferencesManagerBase.nuevoTema(R.style.AppTheme4_NoActionBar);
            Utils.Mensaje("Apague el equipo y vuelva a encenderlo para cambiar el tema",R.layout.item_customtoast,mainActivity);
            tvTema3.setBackgroundResource(R.drawable.fondoinfoprincipal);
            tvTema2.setBackgroundResource(R.drawable.stylekeycor3);
            tvTema1.setBackgroundResource(R.drawable.stylekeycor3);
        });

        Cancelar.setOnClickListener(view -> dialog.cancel());

    }


    public void DialogoCambiarHorayFecha(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_fechayhora, null);

        tv_minutos =  mView.findViewById(R.id.tvMinutos);
        tv_hora =  mView.findViewById(R.id.tvHora);
        tv_dia =  mView.findViewById(R.id.tvDia);
        tv_mes =  mView.findViewById(R.id.tvMes);
        tv_anio =  mView.findViewById(R.id.tvAno);

        tv_minutos.setOnLongClickListener(view -> {
            num=num+1;
            if(num==2){
                mainActivity.preferencesManagerBase.setCorreccionRemoto(!mainActivity.preferencesManagerBase.getCorreccionRemoto());
            }
            return false;
        });
        tv_minutos.setOnClickListener(view -> DialogoSeteoVariables(tv_minutos));
        tv_hora.setOnClickListener(view -> DialogoSeteoVariables(tv_hora));
        tv_dia.setOnClickListener(view -> DialogoSeteoVariables(tv_dia));
        tv_mes.setOnClickListener(view -> DialogoSeteoVariables(tv_mes));
        tv_anio.setOnClickListener(view -> DialogoSeteoVariables(tv_anio));

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            if(dia!=0&&mes!=0&&anio!=0){
                jwsManager.jwsSetTime(getContext(),anio,mes,dia,hora,minutos);
                dialog.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> {
            minutos=0;
            hora=0;
            dia=0;
            mes=0;
            anio=0;
            dialog.cancel();
        });

    }

    public void DialogoSeteoVariables(TextView textViewelegido){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(view -> userInput.setText(""));
        TextView textView=mView.findViewById(R.id.textViewt);
        textView.setText("Ingrese");

        userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
          /*  userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
            userInput.requestFocus();

            userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_SIGNED);
            userInput.requestFocus();*/


        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textViewelegido== tv_minutos){
                    if(!userInput.getText().toString().equals("")&& Utils.isNumeric(userInput.getText().toString())){
                        if (Integer.parseInt(userInput.getText().toString()) <= 59) {
                            minutos=Integer.parseInt(userInput.getText().toString());
                            tv_minutos.setText(userInput.getText().toString());
                        }
                    }

                }
                if(textViewelegido== tv_hora){
                    if(!userInput.getText().toString().equals("")&& Utils.isNumeric(userInput.getText().toString())){
                        if (Integer.parseInt(userInput.getText().toString()) <= 24) {
                            tv_hora.setText(userInput.getText().toString());
                            hora=Integer.parseInt(userInput.getText().toString());
                        }
                    }

                }
                if(textViewelegido== tv_dia){
                    if(!userInput.getText().toString().equals("")&& Utils.isNumeric(userInput.getText().toString())) {
                        if (Integer.parseInt(userInput.getText().toString()) <= 31) {
                            dia = Integer.parseInt(userInput.getText().toString());
                            tv_dia.setText(userInput.getText().toString());
                        }
                    }

                }
                if(textViewelegido== tv_mes){
                    if(!userInput.getText().toString().equals("")&& Utils.isNumeric(userInput.getText().toString())) {
                        if (Integer.parseInt(userInput.getText().toString()) <= 12) {
                            mes = Integer.parseInt(userInput.getText().toString());
                            tv_mes.setText(userInput.getText().toString());
                        }

                    }

                }
                if(textViewelegido== tv_anio){
                    if(!userInput.getText().toString().equals("")&& Utils.isNumeric(userInput.getText().toString())) {
                        if (Integer.parseInt(userInput.getText().toString()) < 2200) {
                            anio = Integer.parseInt(userInput.getText().toString());
                            tv_anio.setText(userInput.getText().toString());
                        }

                    }

                }
                dialog.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }


    public void DialogoNuevaPIN(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_pin, null);

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Button bt_generar =  mView.findViewById(R.id.bt_generar);
        TextView tvCodigo = mView.findViewById(R.id.tvCodigo);
        TextView tvpin = mView.findViewById(R.id.tvpin);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        tvpin.setOnClickListener(view -> DialogoTeclado(tvpin));
        bt_generar.setOnClickListener(view -> {
            int Codigo=devuelveCodigoUnico();
            pin=String.valueOf(((Codigo+3031)*6)/4);
            tvCodigo.setText(String.valueOf(Codigo));
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
        Guardar.setOnClickListener(view -> {
            if(!tvpin.getText().toString().equals("error")){
                if(tvpin.getText().toString().equals(pin)){
                    mainActivity.preferencesManagerBase.nuevoPin(pin);
                    Utils.Mensaje("PIN CORRECTO",R.layout.item_customtoastok,mainActivity);
                    dialog.cancel();
                }
            }
        });




    }

    public void DialogoTeclado(TextView textView){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");
            }
        });
        TextView titulo=mView.findViewById(R.id.textViewt);
        titulo.setText("");
        userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        userInput.requestFocus();

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            textView.setText(userInput.getText().toString());
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());


    }


}



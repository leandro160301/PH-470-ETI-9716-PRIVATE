package com.jws.jwsapi.general.container;


import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.jws.JwsManager;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.general.storage.StorageService;
import com.jws.jwsapi.general.storage.StorageDialogHandler;
import com.jws.jwsapi.general.navigation.NavigationFragment;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.user.UserManager;
import com.jws.jwsapi.general.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContainerFragment extends Fragment implements ButtonProvider {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6,bt_arriba;
    LinearLayout bt_wifi,bt_usb,bt_grabando,ln_alarmas;
    MainActivity mainActivity;
    JwsManager jwsManager;
    TextView tv_usuario,tv_fecha,tv_alarmas,tv_titulo;
    LinearLayout lr_botonera;
    LinearLayout lr_usuario;
    int DURACION_ANIMACION=400;
    LinearLayout ln_nav,ln_menu;
    private Fragment fragmentActual;
    Boolean stoped=false;
    public static Runnable runnable;
    String Ip="",Tipo="";
    int bandera=0;
    Animation blinkAnimation;
    ImageView imuser;
    int iconflag=-1;
    @Inject
    UserManager userManager;
    @Inject
    StorageService storageService;

    public static ContainerFragment newInstance(Class<? extends Fragment> fragmentClass) {
        ContainerFragment fragment = new ContainerFragment();
        Bundle args = new Bundle();
        args.putString("FRAGMENT_CLASS", fragmentClass.getName());
        fragment.setArguments(args);
        return fragment;
    }
    public static ContainerFragment newInstanceService(Class<? extends Fragment> fragmentClass, Bundle arg,Boolean programador) {
        ContainerFragment fragment = new ContainerFragment();
        Bundle args = new Bundle();
        args.putString("FRAGMENT_CLASS", fragmentClass.getName());
        args.putBoolean("NIVEL", programador);
        args.putSerializable("instance",arg.getSerializable("instance"));
        args.putSerializable("instanceService",arg.getSerializable("instanceService"));
        fragment.setArguments(args);
        return fragment;
    }

    public void setFragmentActual(Fragment fragmentActual) {
        this.fragmentActual = fragmentActual;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contain_fragment,container,false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        jwsManager= JwsManager.create(requireActivity());
        bt_home=view.findViewById(R.id.bt_home);
        bt_1=view.findViewById(R.id.bt_1);
        bt_2=view.findViewById(R.id.bt_2);
        bt_3=view.findViewById(R.id.bt_3);
        bt_4=view.findViewById(R.id.bt_4);
        bt_5=view.findViewById(R.id.bt_5);
        bt_6=view.findViewById(R.id.bt_6);
        bt_arriba=view.findViewById(R.id.bt_arriba);
        ln_nav=view.findViewById(R.id.ln_nav);
        tv_titulo=view.findViewById(R.id.tv_titulo);
        imuser=view.findViewById(R.id.imuser);
        lr_botonera=view.findViewById(R.id.lr_botonera);

        bt_wifi=view.findViewById(R.id.bt_wifi);
        bt_usb=view.findViewById(R.id.bt_usb);
        tv_fecha=view.findViewById(R.id.tv_fecha);
        tv_usuario=view.findViewById(R.id.tv_usuario);
        lr_usuario=view.findViewById(R.id.tvUsuario);
        bt_grabando=view.findViewById(R.id.bt_grabando);
        ln_alarmas=view.findViewById(R.id.lnalarma);
        tv_alarmas= view.findViewById(R.id.tvalarmas);
        ln_menu=view.findViewById(R.id.ln_menu);

        bt_grabando.setVisibility(View.INVISIBLE);
        ln_alarmas.setVisibility(View.INVISIBLE);
        blinkAnimation = new AlphaAnimation(1, 0);
        blinkAnimation.setDuration(500);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);

        bt_arriba.setOnClickListener(view14 -> {
            if(ln_nav.getVisibility()==View.GONE){
                AnimacionBotoneraInvisible ();
            }else{
                AnimacionBotoneraVisible ();
            }
        });

        ln_menu.setOnClickListener(view1 -> mainActivity.mainClass.openFragment(new NavigationFragment()));
        lr_usuario.setOnClickListener(view13 -> userManager.BotonLogeo(mainActivity,mainActivity));
        bt_wifi.setOnClickListener(view12 -> DialogoInformacion());

        ButtonProvider buttonProvider = this;
        ButtonProviderSingleton.getInstance().setButtonProvider(buttonProvider);


        if (getArguments() != null) {
            String fragmentClassName = getArguments().getString("FRAGMENT_CLASS");
            if (fragmentClassName != null) {
                try {
                    Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) Class.forName(fragmentClassName);
                    Fragment fragment = fragmentClass.newInstance();
                    if (getArguments()!=null) {
                        Bundle args = getArguments();
                        fragment.setArguments(args);
                    }

                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.nuevofragment, fragment)
                            .commit();
                } catch (ClassNotFoundException | java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        startRunnable();
    }

    public void DialogoInformacion(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_informacion, null);


        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        TextView tvIP = mView.findViewById(R.id.tvIP);
        TextView tvVersion = mView.findViewById(R.id.tvVersion);
        if(!storageService.getUsbState()){
            Guardar.setVisibility(View.INVISIBLE);
        }
        tvIP.setText(Utils.getIPAddress(true));
        tvVersion.setText(MainActivity.VERSION);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        Cancelar.setOnClickListener(view -> dialog.cancel());
        Guardar.setOnClickListener(view -> {
            StorageDialogHandler storageDialogHandler = new StorageDialogHandler(mainActivity);
            storageDialogHandler.showDialog();
            dialog.cancel();
        });



    }

    private void startRunnable() {
        Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(userManager.getUserLevel()==4&&iconflag!=4){
                    imuser.setImageResource(R.drawable.icono_programador);
                    iconflag=4;
                }
                if(userManager.getUserLevel()==3&&iconflag!=3){
                    imuser.setImageResource(R.drawable.icono_administrador);
                    iconflag=3;
                }
                if(userManager.getUserLevel()==2&&iconflag!=2){
                    imuser.setImageResource(R.drawable.icono_supervisor);
                    iconflag=2;
                }
                if(userManager.getUserLevel()==1&&iconflag!=1){
                    imuser.setImageResource(R.drawable.icon_user);
                    iconflag=1;
                }
                if(userManager.getUserLevel()==0&&iconflag!=0){
                    imuser.setImageResource(R.drawable.icono_nologin);
                    iconflag=0;
                }
                /*int cant=0;
                for(int i=0;i<mainActivity.mainClass.alarmas.size();i++){
                    if(mainActivity.mainClass.alarmas.get(i).estado){
                        cant++;
                    }
                }
                tv_alarmas.setText(String.valueOf(cant));
                if(cant>0&&bandera!=1){
                    bandera=1;
                    tv_alarmas.setVisibility(View.VISIBLE);
                    ln_alarmas.setVisibility(View.VISIBLE);
                    ln_alarmas.startAnimation(blinkAnimation);
                }
                if(cant==0&&bandera!=2){
                    bandera=2;
                    tv_alarmas.setVisibility(View.INVISIBLE);
                    ln_alarmas.setVisibility(View.INVISIBLE);
                    ln_alarmas.clearAnimation();
                }*/


                Ip= Utils.getIPAddress(true);
                if(jwsManager.jwsGetCurrentNetType()!=null){
                    Tipo=jwsManager.jwsGetCurrentNetType();
                }else{
                    bandera=0;
                    Tipo="";
                }
                if(storageService.getUsbState()){
                    bt_usb.setVisibility(View.VISIBLE);
                }else{
                    bt_usb.setVisibility(View.INVISIBLE);
                }

                if(Tipo.equals("ETH")){
                    bt_wifi.setBackgroundResource(R.drawable.icono_ethernet_white);
                }
                if(Tipo.equals("WIFI")){
                    bt_wifi.setBackgroundResource(R.drawable.wifi_white);
                }
                if(Tipo.equals("")){
                    bandera=0;
                    bt_wifi.setBackgroundResource(R.color.transparente);
                }
                tv_fecha.setText(Utils.getFecha()+" "+ Utils.getHora());
                tv_usuario.setText(userManager.getUsuarioActual());

                if(!stoped){
                    handler.postDelayed(this, 50);
                }


            }
        };

        handler.post(runnable);
    }



    @Override
    public Button getButtonHome() {
        return bt_home;
    }

    @Override
    public Button getButton1() {
        return bt_1;
    }

    @Override
    public Button getButton2() {
        return bt_2;
    }

    @Override
    public Button getButton3() {
        return bt_3;
    }

    @Override
    public Button getButton4() {
        return bt_4;
    }

    @Override
    public Button getButton5() {
        return bt_5;
    }

    @Override
    public Button getButton6() {
        return bt_6;
    }

    @Override
    public TextView getTitulo() {
        return tv_titulo;
    }


    public void AnimacionBotoneraInvisible (){
        Animation translateAnimation = new TranslateAnimation(0, 0, 0, lr_botonera.getHeight());
        Animation translateAnimation1 = new TranslateAnimation(0, 0, 0, -ln_nav.getHeight());

        translateAnimation.setDuration(DURACION_ANIMACION);
        translateAnimation1.setDuration(DURACION_ANIMACION);
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(DURACION_ANIMACION);
        AnimationSet animationSet = new AnimationSet(true);
        AnimationSet animationSet1 = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet1.addAnimation(translateAnimation1);
        animationSet1.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                lr_botonera.setVisibility(View.GONE);
                ln_nav.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        lr_botonera.startAnimation(animationSet);
        ln_nav.startAnimation(animationSet1);

    }

    public void AnimacionBotoneraVisible (){
        int botoneraHeight = lr_botonera.getHeight();
        int navHeight= ln_nav.getHeight();
        Animation translateAnimation = new TranslateAnimation(0, 0, botoneraHeight, 0);
        Animation translateAnimation1 = new TranslateAnimation(0, 0, -navHeight, 0);
        translateAnimation.setDuration(DURACION_ANIMACION);
        translateAnimation1.setDuration(DURACION_ANIMACION);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(DURACION_ANIMACION);
        AnimationSet animationSet = new AnimationSet(true);
        AnimationSet animationSet1 = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet1.addAnimation(translateAnimation1);
        animationSet1.addAnimation(alphaAnimation);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                lr_botonera.setVisibility(View.VISIBLE);
                ln_nav.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        lr_botonera.startAnimation(animationSet);
        ln_nav.startAnimation(animationSet1);
    }

    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }

}

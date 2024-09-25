package com.jws.jwsapi.core.navigation;

import static com.jws.jwsapi.core.navigation.NavigationItems.ITEM_ETHERNET;
import static com.jws.jwsapi.core.navigation.NavigationItems.ITEM_ETIQUETAS;
import static com.jws.jwsapi.core.navigation.NavigationItems.ITEM_FECHA_Y_HORA;
import static com.jws.jwsapi.core.navigation.NavigationItems.ITEM_IMPRESORA;
import static com.jws.jwsapi.core.navigation.NavigationItems.ITEM_TEMA;
import static com.jws.jwsapi.core.navigation.NavigationItems.ITEM_WIFI;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_COMMUNICATION;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_DEVICES;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_NEW_PIN;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_RESET;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_SCALE;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_SERVICE;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_SETTINGS;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_STORAGE;
import static com.jws.jwsapi.core.navigation.NavigationMenu.MENU_USERS;
import static com.jws.jwsapi.core.navigation.NavigationSubItems.SUBITEM_LABEL;
import static com.jws.jwsapi.core.navigation.NavigationSubItems.SUBITEM_SETTINGS;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_OPERATOR;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_SUPERVISOR;
import static com.jws.jwsapi.utils.Utils.isNumeric;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.jws.jwsapi.core.user.UserPinDialog;
import com.jws.jwsapi.core.user.UserPinInterface;
import com.jws.jwsapi.databinding.StandarMenuBinding;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.utils.AdapterCommon;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.date.DateDialog;
import com.jws.jwsapi.utils.date.DateInterface;
import com.service.Balanzas.Fragments.ServiceFragment;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NavigationFragment extends Fragment implements AdapterCommon.ItemClickListener, DateInterface, UserPinInterface, ThemeInterface {

    @Inject
    UserRepository userRepository;
    @Inject
    PreferencesManager preferencesManagerBase;
    private int currentMenu = 0;
    private int currentItem = 0;
    private MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    StandarMenuBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = StandarMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onItemClick(View view, int position) {
        currentMenu = position;
        switch (position) {
            case MENU_SERVICE:
                handleService();
                break;
            case MENU_SETTINGS:
                loadItem(R.array.menu_items_settings);
                break;
            case MENU_COMMUNICATION:
                loadItem(R.array.menu_items_comunicacion);
                break;
            case MENU_USERS:
                handleUsers();
                break;
            case MENU_SCALE:
                loadItem(R.array.menu_items_scale);
                break;
            case MENU_DEVICES:
                loadItem(R.array.menu_items_dispositivos);
                break;
            case MENU_STORAGE:
                mainActivity.mainClass.openFragment(new StorageFragment());
                break;
            case MENU_RESET:
                mainActivity.clearCache();
                break;
            case MENU_NEW_PIN:
                new UserPinDialog(requireContext(), this).showDialog();
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setupButtons();
        setupMenuRecycler();

    }

    private void setupMenuRecycler() {
        binding.recycler1.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterCommon adapter = new AdapterCommon(getContext(), Arrays.asList(getResources().getStringArray(R.array.menu)));
        adapter.setClickListener(this);
        binding.recycler1.setAdapter(adapter);
    }


    private void loadItem(int menu) {
        setupItems(Arrays.asList(getResources().getStringArray(menu)));
    }

    private void handleUsers() {
        handleUserAction(() -> mainActivity.mainClass.openFragment(new UserFragment()), ROLE_SUPERVISOR);
    }

    private void handleService() {
        handleUserAction(() -> {
            ServiceFragment fragment = ServiceFragment.newInstance(mainActivity.mainClass.service);
            Bundle args = new Bundle();
            args.putSerializable("instanceService", mainActivity.mainClass.service);
            mainActivity.mainClass.openFragmentService(fragment, args);
        }, ROLE_OPERATOR);
    }

    private void setupItems(List<String> list) {
        binding.lrDinamico1.setVisibility(View.VISIBLE);
        setupSubItems(new ArrayList<>());
        NavigationAdapter adapter = setupItemRecycler(binding.recycler2, list);
        adapter.setClickListener((view, position) -> {
            currentItem = position;
            switch (currentMenu) {
                case MENU_SETTINGS:
                    handleSettingsMenu(position);
                    break;
                case MENU_COMMUNICATION:
                    handleCommunicationMenu(position);
                    break;
                case MENU_SCALE:
                    handleScaleMenu(position);
                    break;
                case MENU_DEVICES:
                    handleDevicesMenu(position);
                    break;
            }
        });
    }

    private void setupSubItems(List<String> lista) {
        binding.lrDinamico2.setVisibility(View.VISIBLE);
        NavigationAdapter adapter = setupItemRecycler(binding.recycler3, lista);
        adapter.setClickListener((view, position) -> {
            if (currentMenu == MENU_DEVICES) {
                handleUserAction(() -> {
                    if (currentItem == ITEM_IMPRESORA) {
                        switch (position) {
                            case SUBITEM_SETTINGS:
                                mainActivity.mainClass.openFragment(new PrinterFragment());
                                break;
                            case SUBITEM_LABEL:
                                mainActivity.mainClass.openFragment(new LabelFragment());
                                break;
                        }
                    }
                }, ROLE_OPERATOR);
            }

        });

    }

    private void handleDevicesMenu(int position) {
        if (position == ITEM_IMPRESORA) {
            setupSubItems(Arrays.asList(getResources().getStringArray(R.array.menu_subitems_printer)));
        }
    }

    private void handleScaleMenu(int position) {
        switch (position) {
            case ITEM_FECHA_Y_HORA:
                handleUserAction(() -> new DateDialog(this, getContext()).showDialog(), ROLE_OPERATOR);
                break;
            case ITEM_TEMA:
                handleUserAction(() -> new ThemeDialog(requireContext(), this).showDialog(), ROLE_OPERATOR);
                break;
        }
    }

    private void handleCommunicationMenu(int position) {
        switch (position) {
            case ITEM_WIFI:
                mainActivity.mainClass.openFragment(new WifiFragment());
                break;
            case ITEM_ETHERNET:
                mainActivity.mainClass.openFragment(new EthernetFragment());
                break;
        }
    }

    private void handleSettingsMenu(int position) {
        if (position == ITEM_ETIQUETAS) {
            handleLabelItem();
        }
    }

    private void handleLabelItem() {
        handleUserAction(() -> mainActivity.mainClass.openFragment(new LabelProgramFragment()), ROLE_OPERATOR);
    }

    private NavigationAdapter setupItemRecycler(RecyclerView recyclerView, List<String> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NavigationAdapter adapter = new NavigationAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        return adapter;
    }

    private boolean hasPermission(int requiredLevel) {
        return userRepository.getLevelUser() > requiredLevel;
    }

    private void handleUserAction(Runnable action, int requiredLevel) {
        if (hasPermission(requiredLevel)) {
            action.run();
        } else {
            toastLoginError();
        }
    }

    private void toastLoginError() {
        ToastHelper.message(getString(R.string.toast_navigation_login_error), R.layout.item_customtoasterror, mainActivity);
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getTitle().setText(R.string.title_fragment_menu);
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);

            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
        }
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
        if (isNumeric(day) && isNumeric(hour) && isNumeric(minutes) && isNumeric(month) && isNumeric(year)) {
            JwsManager jwsManager = JwsManager.create(requireActivity());
            jwsManager.jwsSetTime(getContext(), Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)
                    , Integer.parseInt(hour), Integer.parseInt(minutes));
            return true;
        }
        return false;
    }

    @Override
    public boolean setupPin(String newPin, String pinFromTv) {
        if (pinFromTv != null && pinFromTv.equals(newPin)) {
            preferencesManagerBase.setPin(newPin);
            ToastHelper.message(getString(R.string.toast_navigation_new_pin_ok), R.layout.item_customtoastok, mainActivity);
            return true;
        }
        return false;
    }
}



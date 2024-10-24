package com.jws.jwsapi.core.services;

import static com.jws.jwsapi.core.user.UserConstants.USERS_LIST;

import android.content.Context;
import android.os.Environment;

import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.core.user.UserModel;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.util.ArrayList;
import java.util.List;

public class FtpServer {

    private final PreferencesManager preferencesManager;
    Context context;
    List<UserModel> userElements;

    public FtpServer(Context context, List<UserModel> userElements, PreferencesManager preferencesManager) {
        this.context = context;
        this.userElements = userElements;
        this.preferencesManager = preferencesManager;
    }

    public void init() {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2221);
        serverFactory.addListener("default", factory.createListener());
        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(false);
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        BaseUser usuarioGregoArchivos = new BaseUser();
        usuarioGregoArchivos.setName(USERS_LIST[1]);
        usuarioGregoArchivos.setPassword("3031");
        usuarioGregoArchivos.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(usuarioGregoArchivos);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        BaseUser user = new BaseUser();
        user.setName(USERS_LIST[0]);
        user.setPassword(preferencesManager.getPin());
        user.setHomeDirectory(Environment.getExternalStorageDirectory().toString() + "/Memoria");
        user.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(user);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        cargadeUsuariosFtp(serverFactory);
        org.apache.ftpserver.FtpServer server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }

    }

    public void cargadeUsuariosFtp(FtpServerFactory serverFactory) {
        List<UserModel> lista = userElements;
        for (UserModel usuario : lista) {
            BaseUser user = new BaseUser();
            user.setName(usuario.getUser());
            user.setPassword(usuario.getPassword());
            user.setHomeDirectory(Environment.getExternalStorageDirectory().getAbsolutePath());
            try {
                serverFactory.getUserManager().save(user);
            } catch (FtpException e) {
                e.printStackTrace();
            }
        }

    }

}

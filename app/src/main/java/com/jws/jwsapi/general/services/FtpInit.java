package com.jws.jwsapi.general.services;

import android.content.Context;
import android.os.Environment;
import com.jws.jwsapi.general.data.local.PreferencesManagerBase;
import com.jws.jwsapi.general.user.UserManager;
import com.jws.jwsapi.general.user.UserModel;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import java.util.ArrayList;
import java.util.List;

public class FtpInit {

    Context context;
    List<UserModel> listaUsuarios;

    public FtpInit(Context context, List<UserModel> listaUsuarios){
        this.context=context;
        this.listaUsuarios=listaUsuarios;
    }

    public void ftpServer(){
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
        usuarioGregoArchivos.setName(UserManager.USUARIOS[1]);
        usuarioGregoArchivos.setPassword("3031");
        usuarioGregoArchivos.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(usuarioGregoArchivos);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        BaseUser user = new BaseUser();
        user.setName(UserManager.USUARIOS[0]);
        user.setPassword(new PreferencesManagerBase(context).consultaPIN());
        user.setHomeDirectory(Environment.getExternalStorageDirectory().toString()+"/Memoria");
        user.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(user);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        cargadeUsuariosFtp(serverFactory);
        FtpServer server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }

    }
    public void cargadeUsuariosFtp(FtpServerFactory serverFactory){
        List<UserModel> lista= listaUsuarios;
        for (UserModel usuario : lista) {
            BaseUser user = new BaseUser();
            user.setName(usuario.user);
            user.setPassword(usuario.password);
            user.setHomeDirectory(Environment.getExternalStorageDirectory().getAbsolutePath());
            try {
                serverFactory.getUserManager().save(user);
            } catch (FtpException e) {
                e.printStackTrace();
            }
        }

    }

}

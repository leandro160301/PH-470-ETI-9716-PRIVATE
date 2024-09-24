package com.jws.jwsapi.core.user;

import android.app.AlertDialog;
import com.jws.jwsapi.databinding.DialogoUsuarioBinding;
import java.util.function.Predicate;

public interface UserCreateInterface {
    void handleCreateUserButton(DialogoUsuarioBinding binding, AlertDialog dialog);
    boolean searchUserOrCode(Predicate<UserModel> predicate);
}

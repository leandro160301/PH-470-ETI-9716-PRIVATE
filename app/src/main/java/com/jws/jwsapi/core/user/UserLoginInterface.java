package com.jws.jwsapi.core.user;

import java.util.List;

public interface UserLoginInterface {
    boolean login(String password, String user);
    void logout();
    List<String> getUsersSpinner();
}

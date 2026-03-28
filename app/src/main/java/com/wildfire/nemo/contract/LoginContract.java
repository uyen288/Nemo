package com.wildfire.nemo.contract;

import com.wildfire.nemo.data.model.User;

public interface LoginContract {
    interface View {
        void showLoading();
        void hideLoading();
        void onLoginSuccess(User user);
        void onLoginFailure(String message);
        void navigateToHome();
    }

    interface Presenter {
        void login(String username, String password);
    }
}

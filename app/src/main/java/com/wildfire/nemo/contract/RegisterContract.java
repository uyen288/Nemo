package com.wildfire.nemo.contract;

import com.wildfire.nemo.data.model.User;

public interface RegisterContract {
    interface View {
        void showLoading();
        void hideLoading();
        void onRegisterSuccess(User user);
        void onRegisterFailure(String message);
        void navigateToLogin();
    }

    interface Presenter {
        void register(String username, String email, String password, String confirmPassword);
    }
}

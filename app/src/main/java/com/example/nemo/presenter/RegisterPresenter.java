package com.example.nemo.presenter;

import androidx.annotation.NonNull;

import com.example.nemo.constract.RegisterContract;
import com.example.nemo.data.model.User;
import com.example.nemo.data.remote.ApiService;
import com.example.nemo.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPresenter implements RegisterContract.Presenter {
    private RegisterContract.View view;
    private ApiService apiService;

    public RegisterPresenter(RegisterContract.View view) {
        this.view = view;
        this.apiService = RetrofitClient.getApiService();
    }

    @Override
    public void register(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.onRegisterFailure("Vui lòng điền đầy đủ thông tin");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.onRegisterFailure("Mật khẩu xác nhận không khớp");
            return;
        }

        view.showLoading();
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        apiService.register(user).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    User registeredUser = null;
                    
                    for (User u : users) {
                        if (u.getUsername().equals(username)) {
                            registeredUser = u;
                            break;
                        }
                    }
                    
                    if (registeredUser != null) {
                        view.onRegisterSuccess(registeredUser);
                    } else {
                        view.onRegisterSuccess(user);
                    }
                } else {
                    view.onRegisterFailure("Đăng ký thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onRegisterFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}

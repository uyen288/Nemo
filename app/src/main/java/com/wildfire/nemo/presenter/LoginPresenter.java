package com.wildfire.nemo.presenter;

import com.wildfire.nemo.contract.LoginContract;
import com.wildfire.nemo.data.model.User;
import com.wildfire.nemo.data.remote.ApiService;
import com.wildfire.nemo.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private ApiService apiService;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.apiService = RetrofitClient.getApiService();
    }

    // Sửa tại file: D:/016_PXU/2526_spring/mobile/0_final/Nemo/app/src/main/java/com/example/nemo/presenter/LoginPresenter.java

    @Override
    public void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            view.onLoginFailure("Please enter username and password");
            return;
        }

        view.showLoading();
        User userRequest = new User(username, password);

        apiService.login(userRequest).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    User foundUser = null;

                    for (User u : users) {
                        if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                            foundUser = u;
                            break;
                        }
                    }

                    if (foundUser != null) {
                        if (foundUser.getToken() == null) foundUser.setToken("mock_token_" + foundUser.getId());
                        view.onLoginSuccess(foundUser);
                    } else {
                        view.onLoginFailure("Invalid username or password");
                    }
                } else {
                    view.onLoginFailure("Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                view.hideLoading();
                view.onLoginFailure("Network error: " + t.getMessage());
            }
        });
    }
}

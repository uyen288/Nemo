package com.example.nemo.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.nemo.MainActivity;
import com.example.nemo.R;
import com.example.nemo.constract.LoginContract;
import com.example.nemo.data.model.User;
import com.example.nemo.presenter.LoginPresenter;
import com.example.nemo.util.SharePrefManager;

public class LoginActivity extends BaseActivity implements LoginContract.View {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private LoginContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        presenter = new LoginPresenter(this);

        if (SharePrefManager.getInstance(this).isLoggedIn()) {
            navigateToHome();
        }

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            presenter.login(username, password);
        });
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
    }

    @Override
    public void onLoginSuccess(User user) {
        SharePrefManager.getInstance(this).saveUserLogin(user);
        navigateToHome();
    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

package com.example.nemo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nemo.adapter.ViewPagerAdapter;
import com.example.nemo.util.SharePrefManager;
import com.example.nemo.view.activity.BaseActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private SharePrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = SharePrefManager.getInstance(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setupStatusBarIcons();
        initGUI();
        Refresh();
    }

    private void setupStatusBarIcons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                if (pref.isDarkMode()) {
                    controller.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                } else {
                    controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pref.isDarkMode()) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(0);
            }
        }
    }

    private void initGUI() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Home"); tab.setIcon(R.drawable.ic_home); break;
                case 1: tab.setText("Grammar"); tab.setIcon(R.drawable.ic_grammar); break;
                case 2: tab.setText("Quiz"); tab.setIcon(R.drawable.ic_quiz); break;
                case 3: tab.setText("Setting"); tab.setIcon(R.drawable.ic_setting); break;
            }
        }).attach();
    }

    private void Refresh() {
        swipeRefresh = findViewById(R.id.swipe_refresh_main);
        swipeRefresh.setColorSchemeResources(R.color.bg_primary);

        swipeRefresh.setOnRefreshListener(() -> {
            performGlobalReset();
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                swipeRefresh.setEnabled(state == ViewPager2.SCROLL_STATE_IDLE);
            }
        });
    }

    private void performGlobalReset() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {

            // Fix: Không gọi clearAll() vì nó sẽ xóa luôn session đăng nhập (is_logged_in = false)
            // Thay vào đó chỉ reset các settings nếu cần, hoặc đơn giản là tải lại Activity
            // SharePrefManager.getInstance(this).clearAll();

            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }

            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            Toast.makeText(this, "Refreshed!", Toast.LENGTH_SHORT).show();

        }, 1000);
    }
}

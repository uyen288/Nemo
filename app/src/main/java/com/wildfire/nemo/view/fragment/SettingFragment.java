package com.wildfire.nemo.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import java.util.Calendar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.wildfire.nemo.MainActivity;
import com.wildfire.nemo.R;
import com.wildfire.nemo.util.NotificationReceiver;
import com.wildfire.nemo.util.SharePrefManager;
import com.wildfire.nemo.view.activity.CollectionActivity;
import com.wildfire.nemo.view.activity.LoginActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

public class SettingFragment extends Fragment {
    private SwitchMaterial switchReminder;
    private TextView tvReminderTime;
    private SharePrefManager pref;
    private MaterialCardView btnAuthCard, btnLogoutCard, btnCollectionCard, layoutUserInfo;
    private TextView tvUserId, tvUserName;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    setupReminderOn();
                    Toast.makeText(getContext(), "Quyền thông báo đã được cấp!", Toast.LENGTH_SHORT).show();
                } else {
                    switchReminder.setChecked(false);
                    Toast.makeText(getContext(), "Bạn cần cấp quyền để nhận nhắc nhở học tập", Toast.LENGTH_LONG).show();
                }
            });

    public SettingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pref = SharePrefManager.getInstance(getContext());

        Spinner spinner = view.findViewById(R.id.spinner_language);
        btnCollectionCard = view.findViewById(R.id.btn_collection_card);
        SwitchMaterial switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        
        btnAuthCard = view.findViewById(R.id.btn_auth_card);
        btnLogoutCard = view.findViewById(R.id.btn_logout_card);
        layoutUserInfo = view.findViewById(R.id.layout_user_info);
        tvUserId = view.findViewById(R.id.tv_user_id);
        tvUserName = view.findViewById(R.id.tv_user_name);

        updateUIBasedOnAuth();

        // --- Setup Language ---
        String[] languages = {"English", "Tiếng Việt"};

// Khởi tạo Adapter với Anonymous Class để can thiệp vào màu chữ
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, languages) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                // Ép màu chữ hiển thị trên Spinner thành màu TRẮNG
                TextView tv = (TextView) v;
                tv.setTextColor(androidx.core.content.ContextCompat.getColor(getContext(), android.R.color.white));
                tv.setTextSize(14f);
                // Thêm padding để chữ không dính sát mép (nếu cần)
                tv.setPadding(10, 10, 10, 10);

                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) v;

                tv.setTextColor(androidx.core.content.ContextCompat.getColor(getContext(), R.color.text_primary));

                int p = (int) (16 * getContext().getResources().getDisplayMetrics().density);
                tv.setPadding(p, p, p, p);

                return v;
            }
        };

// Gán Adapter vào Spinner
        spinner.setAdapter(adapter);

// Thiết lập vị trí chọn hiện tại từ Preference

// Thiết lập vị trí chọn hiện tại từ Preference
        spinner.setSelection(pref.getLanguage().equals("vi") ? 1 : 0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = (position == 1) ? "vi" : "en";
                if (!selectedLang.equals(pref.getLanguage())) {
                    changeLanguage(selectedLang);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // --- Setup Dark Mode ---
        switchDarkMode.setChecked(pref.isDarkMode());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                pref.setDarkMode(isChecked);
                AppCompatDelegate.setDefaultNightMode(isChecked ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), getActivity().getClass());
                    intent.putExtra("OPEN_SETTING", true);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        btnCollectionCard.setOnClickListener(v -> startActivity(new Intent(getContext(), CollectionActivity.class)));

        btnAuthCard.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));

        btnLogoutCard.setOnClickListener(v -> {
            pref.logout();
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
            updateUIBasedOnAuth();
            

            // Redirect to Login
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // --- Setup Notification ---
        switchReminder = view.findViewById(R.id.switch_reminder);
        tvReminderTime = view.findViewById(R.id.tv_reminder_time);

        switchReminder.setChecked(pref.isReminderOn());
        tvReminderTime.setVisibility(pref.isReminderOn() ? View.VISIBLE : View.GONE);
        updateTimeText(pref.getReminderHour(), pref.getReminderMinute());

        switchReminder.setOnClickListener(v -> {
            if (switchReminder.isChecked()) {
                if (checkNotiPermission()) {
                    setupReminderOn();
                } else {
                    switchReminder.setChecked(false);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                    }
                }
            } else {
                pref.setReminderOn(false);
                tvReminderTime.setVisibility(View.GONE);
                cancelAlarm();
            }
        });

        tvReminderTime.setOnClickListener(v -> {
            new TimePickerDialog(getContext(), (tp, hour, min) -> {
                pref.setReminderTime(hour, min);
                updateTimeText(hour, min);
                startAlarm(hour, min);
            }, pref.getReminderHour(), pref.getReminderMinute(), true).show();
        });
    }

    private void updateUIBasedOnAuth() {
        if (pref.isLoggedIn()) {
            btnAuthCard.setVisibility(View.GONE);
            btnLogoutCard.setVisibility(View.VISIBLE);
            tvUserId.setText("User ID: " + pref.getUserId());
            tvUserName.setText(pref.getUsername());
        } else {
            btnAuthCard.setVisibility(View.VISIBLE);
            btnLogoutCard.setVisibility(View.GONE);
            tvUserId.setText("User ID: 0");
            tvUserName.setText("Guest");
        }
        // Luôn hiển thị khung user
        // Luôn hiển thị khung user
        layoutUserInfo.setVisibility(View.VISIBLE);
    }

    private void changeLanguage(String langCode) {
        pref.setLanguage(langCode);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void setupReminderOn() {
        switchReminder.setChecked(true);
        pref.setReminderOn(true);
        tvReminderTime.setVisibility(View.VISIBLE);
        startAlarm(pref.getReminderHour(), pref.getReminderMinute());
    }

    private void updateTimeText(int h, int m) {
        tvReminderTime.setText(String.format("Reminder at: %02d:%02d", h, m));
    }

    private void startAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 101, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void cancelAlarm() {
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 101, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) alarmManager.cancel(pendingIntent);
    }

    private boolean checkNotiPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return androidx.core.content.ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}

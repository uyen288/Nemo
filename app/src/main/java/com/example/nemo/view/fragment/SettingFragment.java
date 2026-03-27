package com.example.nemo.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import com.example.nemo.MainActivity;
import com.example.nemo.R;
import com.example.nemo.util.NotificationReceiver;
import com.example.nemo.util.SharePrefManager;
import com.example.nemo.view.activity.CollectionActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingFragment extends Fragment {
    private SwitchMaterial switchReminder;
    private TextView tvReminderTime;
    private SharePrefManager pref;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Nếu người dùng đồng ý cấp quyền
                    setupReminderOn();
                    Toast.makeText(getContext(), "Quyền thông báo đã được cấp!", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu từ chối
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
        Button btnCollection = view.findViewById(R.id.btn_collection);
        SwitchMaterial switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        // --- Setup Language ---
        String[] languages = {"English", "Tiếng Việt"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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

                String msg = isChecked ? "Applying Dark Mode..." : "Applying Light Mode...";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                AppCompatDelegate.setDefaultNightMode(isChecked ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), getActivity().getClass());
                    intent.putExtra("OPEN_SETTING", true);

                    startActivity(intent);
                    getActivity().finish();

                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

        btnCollection.setOnClickListener(v -> startActivity(new Intent(getContext(), CollectionActivity.class)));

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
                    switchReminder.setChecked(false); // Tạm tắt để đợi xin quyền
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

    private void changeLanguage(String langCode) {
        SharePrefManager.getInstance(getContext()).setLanguage(langCode);

        String msg = langCode.equals("vi") ? "Đang áp dụng tiếng Việt..." : "Applying English...";
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            // Dùng setAndAllowWhileIdle để không bị crash trên Android 12-14
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

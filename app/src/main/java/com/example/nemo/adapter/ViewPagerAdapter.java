package com.example.nemo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nemo.view.fragment.GrammarFragment;
import com.example.nemo.view.fragment.HomeFragment;
import com.example.nemo.view.fragment.SettingFragment;
import com.example.nemo.view.fragment.QuizFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new GrammarFragment();
            case 2: return new QuizFragment();
            case 3: return new SettingFragment();
            default: return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}

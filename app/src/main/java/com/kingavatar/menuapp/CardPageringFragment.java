package com.kingavatar.menuapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class CardPageringFragment extends Fragment {
    private int current_position = 1;

    public CardPageringFragment() {
        // Required empty public constructor
    }

    private void setupViewPager(ViewPager viewPager) {
        CardPager adapter = new CardPager(getChildFragmentManager());
        adapter.addFrag(new BreakFragment(), "Breakfast");
        adapter.addFrag(new BreakFragment(), "Lunch");
        adapter.addFrag(new BreakFragment(), "Dinner");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(current_position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setcurrentitem(int pos) {
        current_position = pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cardpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewPager mImageViewPager = view.findViewById(R.id.pager);
        setupViewPager(mImageViewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mImageViewPager, true);
        super.onViewCreated(view, savedInstanceState);
    }
}

class CardPager extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public CardPager(FragmentManager fm) {
        super(fm);
    }

    public void addFrag(BreakFragment frag, String text) {
        frag.addParam(text);
        BreakFragment.settransitionname(text);
        mFragmentList.add(frag);
    }

    @Override
    public androidx.fragment.app.Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}

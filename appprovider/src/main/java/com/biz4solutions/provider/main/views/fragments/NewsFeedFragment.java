package com.biz4solutions.provider.main.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.provider.R;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.databinding.FragmentNewsFeedBinding;

public class NewsFeedFragment extends Fragment {

    public static final String fragmentName = "NewsFeedFragment";
    private MainActivity mainActivity;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    public static NewsFeedFragment newInstance() {
        return new NewsFeedFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentNewsFeedBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news_feed, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_news_feed);
            mainActivity.toolbarTitle.setText(R.string.news_feed);
        }
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

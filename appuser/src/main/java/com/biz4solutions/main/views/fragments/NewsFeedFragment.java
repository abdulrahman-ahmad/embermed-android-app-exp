package com.biz4solutions.main.views.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.databinding.FragmentNewsFeedBinding;
import com.biz4solutions.activities.OpenTokActivity;

public class NewsFeedFragment extends Fragment implements View.OnClickListener {

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
        binding.btStartVideoCall.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void startVideoCall() {
        String SESSION_ID = "1_MX40NjE2OTQ0Mn5-MTUzNDQwMTQyODA2OH4wOWpyK1IxTlk0KzBrOVoyZG5lTldQVWV-fg";
        String TOKEN = "T1==cGFydG5lcl9pZD00NjE2OTQ0MiZzaWc9ZTIzNDdkNmExMWRlNDYzZjkzNmVmMDM3NjM3YWY3MGZhNmJkNzQyYTpzZXNzaW9uX2lkPTFfTVg0ME5qRTJPVFEwTW41LU1UVXpORFF3TVRReU9EQTJPSDR3T1dweUsxSXhUbGswS3pCck9Wb3laRzVsVGxkUVZXVi1mZyZjcmVhdGVfdGltZT0xNTM1MDMzNzcyJm5vbmNlPTAuNzQzODY5NjgxNTExODYmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTUzNzYyNTc3MCZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";
        Intent intent = new Intent(getActivity(), OpenTokActivity.class);
        intent.putExtra(OpenTokActivity.OPENTOK_SESSION_ID, SESSION_ID);
        intent.putExtra(OpenTokActivity.OPENTOK_PUBLISHER_TOKEN, TOKEN);
        startActivityForResult(intent, OpenTokActivity.RC_OPENTOK_ACTIVITY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_video_call:
                startVideoCall();
                break;
        }
    }
}

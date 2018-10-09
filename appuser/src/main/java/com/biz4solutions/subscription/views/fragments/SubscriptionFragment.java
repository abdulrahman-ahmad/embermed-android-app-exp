package com.biz4solutions.subscription.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.apiservices.ApiServices;
import com.biz4solutions.databinding.FragmentSubscriptionBinding;
import com.biz4solutions.interfaces.CustomOnItemClickListener;
import com.biz4solutions.interfaces.RestClientResponse;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.SubscriptionCardDetails;
import com.biz4solutions.models.response.EmptyResponse;
import com.biz4solutions.subscription.adapters.SubscriptionOffersRecyclerAdapter;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.NavigationUtil;

import java.util.ArrayList;

public class SubscriptionFragment extends Fragment implements CustomOnItemClickListener {

    public static final String fragmentName = "SubscriptionFragment";
    private MainActivity mainActivity;
    private FragmentSubscriptionBinding binding;
    private final static String REQUESTED_DATA = "REQUESTED_DATA";
    private ArrayList<SubscriptionCardDetails> offersList;

    public SubscriptionFragment() {
        // Required empty public constructor
    }

    public static SubscriptionFragment newInstance(ArrayList<SubscriptionCardDetails> offerList) {
        SubscriptionFragment fragment = new SubscriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUESTED_DATA, offerList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            offersList = (ArrayList<SubscriptionCardDetails>) getArguments().getSerializable(REQUESTED_DATA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subscription, container, false);
        initActivity();
        initViews();
        return binding.getRoot();
    }

    private void initViews() {
        //init recycler
        binding.rvSubscriptionOffer.setLayoutManager(new LinearLayoutManager(mainActivity));
        if (offersList == null || offersList.size() == 0) {
            binding.rvSubscriptionOffer.setVisibility(View.GONE);
            binding.tvEmptyOffersList.setVisibility(View.VISIBLE);
        } else {
            binding.rvSubscriptionOffer.setVisibility(View.VISIBLE);
            binding.tvEmptyOffersList.setVisibility(View.GONE);
            SubscriptionOffersRecyclerAdapter rvAdapter = new SubscriptionOffersRecyclerAdapter(offersList, this);
            binding.rvSubscriptionOffer.setHasFixedSize(true);
            binding.rvSubscriptionOffer.setAdapter(rvAdapter);
        }
    }

    private void initActivity() {
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_subscription);
            mainActivity.toolbarTitle.setText(R.string.subscription);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCustomItemClick(int position, Object obj) {
        SubscriptionCardDetails cardDetails = (SubscriptionCardDetails) obj;
        subscribeUser(cardDetails.getId());
    }

    private void subscribeUser(String id) {
        if (id != null) {

            if (CommonFunctions.getInstance().isOffline(mainActivity)) {
                Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
                return;
            }
            CommonFunctions.getInstance().loadProgressDialog(mainActivity);
            new ApiServices().subscribeUser(mainActivity, id, new RestClientResponse() {
                @Override
                public void onSuccess(Object response, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Toast.makeText(mainActivity, ((EmptyResponse) response).getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage, int statusCode) {
                    CommonFunctions.getInstance().dismissProgressDialog();
                    Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
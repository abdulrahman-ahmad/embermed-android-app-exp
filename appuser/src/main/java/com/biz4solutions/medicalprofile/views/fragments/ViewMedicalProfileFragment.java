package com.biz4solutions.medicalprofile.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentViewMedicalProfileBinding;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.medicalprofile.adapters.MedicalProfileRecyclerAdapter;
import com.biz4solutions.models.MedicalDisease;
import com.biz4solutions.utilities.NavigationUtil;

import java.util.ArrayList;
public class ViewMedicalProfileFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "ViewMedicalProfileFragment";
    private MainActivity mainActivity;
    private FragmentViewMedicalProfileBinding binding;
    private final static String REQUESTED_DATA = "REQUESTED_DATA";
    private ArrayList<MedicalDisease> diseaseArrayList;

    public ViewMedicalProfileFragment() {
        // Required empty public constructor

    }

    public static ViewMedicalProfileFragment newInstance(
            ArrayList<MedicalDisease> diseaseArrayList) {
        ViewMedicalProfileFragment fragment = new ViewMedicalProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUESTED_DATA, diseaseArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            diseaseArrayList = (ArrayList<MedicalDisease>) getArguments()
                    .getSerializable(REQUESTED_DATA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_medical_profile);
            mainActivity.toolbarTitle.setText(R.string.medical_profile);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
            mainActivity.btnEditMedicalProfile.setVisibility(View.VISIBLE);
            mainActivity.btnEditMedicalProfile.setOnClickListener(this);
        }
        initBinding(inflater, container);
        return binding.getRoot();
    }

    private void initBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_view_medical_profile, container, false);
//        viewModel = ViewModelProviders.of(this, new ViewMedicalProfileViewModel.ViewMedicalProfileViewModelFactory(mainActivity.getApplication())).get(ViewMedicalProfileViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        //init recycler
        binding.rvViewMedicalProfile.setLayoutManager(new LinearLayoutManager(mainActivity));
        MedicalProfileRecyclerAdapter rvAdapter = new MedicalProfileRecyclerAdapter(
                diseaseArrayList, null, true);
        binding.rvViewMedicalProfile.setHasFixedSize(true);
        binding.rvViewMedicalProfile.setAdapter(rvAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.btnEditMedicalProfile.setVisibility(View.GONE);
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mainActivity.btnEditMedicalProfile.getId()) {
            mainActivity.openMedicalProfileFragment(diseaseArrayList);
        }
    }
}
package com.biz4solutions.medicalprofile.views.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.biz4solutions.databinding.FragmentMedicalProfileBinding;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.medicalprofile.adapters.MedicalProfileRecyclerAdapter;
import com.biz4solutions.medicalprofile.viewmodels.ViewMedicalProfileViewModel;
import com.biz4solutions.models.MedicalDisease;
import com.biz4solutions.utilities.NavigationUtil;

import java.util.ArrayList;

public class ViewMedicalProfileFragment extends Fragment {

    public static final String fragmentName = "ViewMedicalProfileFragment";
    private MainActivity mainActivity;
    private FragmentMedicalProfileBinding binding;
    private MedicalProfileRecyclerAdapter rvAdapter;
    private ViewMedicalProfileViewModel viewModel;

    public ViewMedicalProfileFragment() {
        // Required empty public constructor
    }

    public static ViewMedicalProfileFragment newInstance() {
        return new ViewMedicalProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initActivity();
        initBinding(inflater, container);
        return binding.getRoot();
    }

    private void initBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_medical_profile, container, false);
        viewModel = ViewModelProviders.of(this, new ViewMedicalProfileViewModel.ViewMedicalProfileViewModelFactory(mainActivity.getApplication())).get(ViewMedicalProfileViewModel.class);
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
        initListeners();
        initViews();
    }

    private void initViews() {
        //init recycler
//        binding.rvMedicalProfile.setLayoutManager(new LinearLayoutManager(mainActivity));
//        rvAdapter = new MedicalProfileRecyclerAdapter(new ArrayList<MedicalDisease>(), null,true);
//        binding.rvMedicalViewProfile.setHasFixedSize(true);
//        binding.rvMedicalViewProfile.setAdapter(rvAdapter);
    }

    private void initListeners() {
        viewModel.getDiseaseMutableLiveData().observe(this, new Observer<ArrayList<MedicalDisease>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MedicalDisease> medicalDiseases) {
                rvAdapter.setNewMedicalData(medicalDiseases);
            }
        });

        viewModel.getToastMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initActivity() {
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_medical_profile);
            mainActivity.toolbarTitle.setText(R.string.medical_profile);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getDiseaseMutableLiveData().removeObservers(this);
        viewModel.getToastMsg().removeObservers(this);
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
    }
}
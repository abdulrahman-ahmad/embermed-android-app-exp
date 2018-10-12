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

import java.util.ArrayList;

public class ViewMedicalProfileFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "ViewMedicalProfileFragment";
    private MainActivity mainActivity;
    private FragmentViewMedicalProfileBinding binding;
    private final static String USER_DISEASE_DATA = "USER_DISEASE_DATA";
    private ArrayList<MedicalDisease> diseaseArrayList;
    private MedicalProfileRecyclerAdapter rvAdapter;

    public ViewMedicalProfileFragment() {
        // Required empty public constructor
    }

    public static ViewMedicalProfileFragment newInstance(ArrayList<MedicalDisease> diseaseArrayList) {
        ViewMedicalProfileFragment fragment = new ViewMedicalProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_DISEASE_DATA, diseaseArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            diseaseArrayList = (ArrayList<MedicalDisease>) getArguments().getSerializable(USER_DISEASE_DATA);
            if (diseaseArrayList == null) {
                diseaseArrayList = new ArrayList<>();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_medical_profile, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_medical_profile);
            mainActivity.toolbarTitle.setText(R.string.medical_profile);
            mainActivity.btnEditMedicalProfile.setVisibility(View.VISIBLE);
            mainActivity.btnEditMedicalProfile.setOnClickListener(this);
        }
        initViews();
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        if (diseaseArrayList != null && !diseaseArrayList.isEmpty()) {
            binding.emptyAlertLayout.setVisibility(View.GONE);
            binding.layoutListView.setVisibility(View.VISIBLE);
            if (rvAdapter == null) {
                rvAdapter = new MedicalProfileRecyclerAdapter(diseaseArrayList, null, true);
            }
            binding.rvViewMedicalProfile.setLayoutManager(new LinearLayoutManager(mainActivity));
            binding.rvViewMedicalProfile.setHasFixedSize(true);
            binding.rvViewMedicalProfile.setAdapter(rvAdapter);
        } else {
            binding.emptyAlertLayout.setVisibility(View.VISIBLE);
            binding.layoutListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.btnEditMedicalProfile.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mainActivity.btnEditMedicalProfile.getId()) {
            mainActivity.openMedicalProfileFragment(diseaseArrayList);
        }
    }
}
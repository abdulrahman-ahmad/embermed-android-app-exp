package com.biz4solutions.medicalprofile.views.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentMedicalProfileBinding;
import com.biz4solutions.interfaces.CustomOnItemClickListener;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.medicalprofile.adapters.MedicalAutoCompleteAdapter;
import com.biz4solutions.medicalprofile.adapters.MedicalProfileRecyclerAdapter;
import com.biz4solutions.medicalprofile.viewmodels.MedicalProfileViewModel;
import com.biz4solutions.models.MedicalDisease;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.NavigationUtil;

import java.util.ArrayList;

public class MedicalProfileFragment extends Fragment implements AdapterView.OnItemClickListener, CustomOnItemClickListener {

    public static final String fragmentName = "MedicalProfileFragment";
    private MainActivity mainActivity;
    private FragmentMedicalProfileBinding binding;
    private MedicalProfileRecyclerAdapter rvAdapter;
    private ArrayAdapter<MedicalDisease> autoCompleteTvAdapter;
    private MedicalProfileViewModel viewModel;

    public MedicalProfileFragment() {
        // Required empty public constructor
    }

    public static MedicalProfileFragment newInstance() {
        return new MedicalProfileFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_medical_profile, container, false);
        viewModel = ViewModelProviders.of(this, new MedicalProfileViewModel.MedicalProfileViewModelFactory(mainActivity.getApplication())).get(MedicalProfileViewModel.class);
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
        binding.rvMedicalProfile.setLayoutManager(new LinearLayoutManager(mainActivity));
        rvAdapter = new MedicalProfileRecyclerAdapter(new ArrayList<MedicalDisease>(), this, false);
        binding.rvMedicalProfile.setHasFixedSize(true);
        binding.rvMedicalProfile.setAdapter(rvAdapter);

        //init AutoCompleteEdittext
        binding.actvMedicalProfile.setThreshold(1);
//        autoCompleteTvAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, new ArrayList<MedicalDisease>());
        autoCompleteTvAdapter = new MedicalAutoCompleteAdapter(mainActivity, R.layout.item_medical_profile, new ArrayList<MedicalDisease>());
        binding.actvMedicalProfile.setAdapter(autoCompleteTvAdapter);
        binding.actvMedicalProfile.setOnItemClickListener(this);
        binding.actvMedicalProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.getSearchTextList(s.toString());
            }
        });
    }

    private void initListeners() {

        binding.btnSubmitMedicalProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataToViewModel();
            }
        });

        viewModel.getDiseaseMutableLiveData().observe(this, new Observer<ArrayList<MedicalDisease>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MedicalDisease> medicalDiseases) {
                autoCompleteTvAdapter.clear();
                if (medicalDiseases != null) {
                    autoCompleteTvAdapter.addAll(medicalDiseases);
                }
                autoCompleteTvAdapter.notifyDataSetChanged();
            }
        });


        viewModel.getToastMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToViewModel() {
        if (rvAdapter.getMedicalList() != null && rvAdapter.getMedicalList().size() > 0) {
            //api call
            CommonFunctions.getInstance().hideSoftKeyBoard(mainActivity);
            viewModel.updateDataToServer(mainActivity, rvAdapter.getMedicalList());
        } else {
            Toast.makeText(mainActivity, "No diseases selected.", Toast.LENGTH_SHORT).show();
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MedicalDisease disease = autoCompleteTvAdapter.getItem(position);
        if (disease.getId() != null && !disease.getId().equalsIgnoreCase("0")) {
            rvAdapter.addItems(disease);
            binding.btnSubmitMedicalProfile.setVisibility(View.VISIBLE);
        }
        binding.actvMedicalProfile.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getDiseaseMutableLiveData().removeObservers(this);
        viewModel.getToastMsg().removeObservers(this);
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
    }

    @Override
    public void onCustomItemClick(int position, Object obj) {
        if (rvAdapter.getMedicalList() == null || rvAdapter.getMedicalList().size() == 0) {
            binding.btnSubmitMedicalProfile.setVisibility(View.GONE);
        }
    }
}
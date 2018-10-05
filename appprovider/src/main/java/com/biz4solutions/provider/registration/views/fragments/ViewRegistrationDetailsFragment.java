package com.biz4solutions.provider.registration.views.fragments;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentViewRegistrationDetailsBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.registration.viewmodels.ViewRegistrationDetailsViewModel;
import com.biz4solutions.provider.utilities.FileUtils;
import com.biz4solutions.utilities.Constants;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ViewRegistrationDetailsFragment extends Fragment {

    public static final String fragmentName = "ViewRegistrationDetailsFragment";
    private MainActivity mainActivity;
    private ViewRegistrationDetailsViewModel viewModel;
    private FragmentViewRegistrationDetailsBinding binding;

    public ViewRegistrationDetailsFragment() {
        // Required empty public constructor
    }

    public static ViewRegistrationDetailsFragment newInstance() {
        return new ViewRegistrationDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_registration_details, container, false);
        viewModel = ViewModelProviders.of(this, new ViewRegistrationDetailsViewModel.ViewRegistrationDetailsFactory(mainActivity.getApplication())).get(ViewRegistrationDetailsViewModel.class);
        binding.setViewModel(viewModel);
        initActivity();
        initListeners();
//        setUserData();
        registerReceiver();
        return binding.getRoot();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mainActivity.registerReceiver(receiver, intentFilter);
    }

    private void initListeners() {
        binding.llToastCpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ext;
                File file = new File(Environment.getExternalStorageDirectory()
                        + "/Ember_Docs/" + Constants.CPR_FILE_NAME + ".pdf");
                File file2 = new File(Environment.getExternalStorageDirectory()
                        + "/Ember_Docs/" + Constants.CPR_FILE_NAME + ".jpg");

                if (file.exists()) {
                    openFiles(Uri.fromFile(file).toString(), "application/pdf");
                } else if (file2.exists()) {
                    openFiles(Uri.fromFile(file).toString(), "image/*");
                } else {
                    if (viewModel.registration.get().getCprCertificateLink().contains(".pdf")) {
                        ext = "pdf";
                    } else {
                        ext = "jpg";
                    }
                    FileUtils.downloadFile(mainActivity, viewModel.registration.get().getCprCertificateLink(), true, ext);
                }
            }
        });
        binding.llToastMedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory()
                        + "/Ember_Docs/" + Constants.MEDICAL_FILE_NAME + ".pdf");
                File file2 = new File(Environment.getExternalStorageDirectory()
                        + "/Ember_Docs/" + Constants.MEDICAL_FILE_NAME + ".jpg");


                if (file.exists()) {
                    openFiles(Uri.fromFile(file).toString(), "application/pdf");
                } else if (file2.exists()) {
                    openFiles(Uri.fromFile(file).toString(), "image/*");
                } else {
                    String ext;
                    if (viewModel.registration.get().getMedicalLiceneceLink().contains(".pdf")) {
                        ext = "pdf";
                    } else {
                        ext = "jpg";
                    }
                    FileUtils.downloadFile(mainActivity, viewModel.registration.get().getMedicalLiceneceLink(), false, ext);
                }
            }
        });
        viewModel.getToastMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getIsDataChanged().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isDataChanged) {
                setUpUi();
            }
        });
    }

    private void setUpUi() {
        binding.llStatus.setBackgroundColor(getResources().getColor(viewModel.getStatusBackgroundColor()));
        binding.tvStatus.setText(viewModel.getStatus());
        binding.tvStatusDesc.setTextColor(getResources().getColor(viewModel.getStatusDescTextColor()));
        binding.tvStatus.setTextColor(getResources().getColor(viewModel.getStatusTextColor()));
    }

    private void initActivity() {
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_registration);
            mainActivity.toolbarTitle.setText(R.string.registration);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

//    public void setUserData() {
//        User user = SharedPrefsManager.getInstance().retrieveUserPreference(mainActivity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
//        viewModel.setUser(user);
//    }

    //todo:need to implement internet & permisssion check

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getToastMsg().removeObservers(this);
        mainActivity.unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                DownloadManager downloadManager = (DownloadManager) mainActivity.getSystemService(DOWNLOAD_SERVICE);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                        if (uriString.contains(".pdf")) {
                            openFiles(uriString, "application/pdf");
                        } else {
                            openFiles(uriString, "image/*");
                        }
                    }
                }
            }
        }
    };

    private void openFiles(String uriString, String s) {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.parse(uriString), s);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent2 = Intent.createChooser(target, "Open File");
        startActivity(intent2);
    }
}
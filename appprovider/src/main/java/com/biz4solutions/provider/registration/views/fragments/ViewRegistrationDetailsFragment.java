package com.biz4solutions.provider.registration.views.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz4solutions.data.FileTypes;
import com.biz4solutions.data.RequestCodes;
import com.biz4solutions.models.ProviderRegistration;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentViewRegistrationDetailsBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.registration.viewmodels.ViewRegistrationDetailsViewModel;
import com.biz4solutions.provider.utilities.FileUtils;
import com.biz4solutions.provider.utilities.NavigationUtil;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ViewRegistrationDetailsFragment extends Fragment {

    public static final String fragmentName = "ViewRegistrationDetailsFragment";
    private final static String REGISTRATION_DETAILS = "REGISTRATION_DETAILS";
    private MainActivity mainActivity;
    private ViewRegistrationDetailsViewModel viewModel;
    private FragmentViewRegistrationDetailsBinding binding;
    private ProviderRegistration registrationDetails;

    public ViewRegistrationDetailsFragment() {
        // Required empty public constructor
    }

    public static ViewRegistrationDetailsFragment newInstance(ProviderRegistration data) {
        ViewRegistrationDetailsFragment fragment = new ViewRegistrationDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(REGISTRATION_DETAILS, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            registrationDetails = (ProviderRegistration) getArguments().getSerializable(REGISTRATION_DETAILS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_registration_details, container, false);
        viewModel = ViewModelProviders.of(this, new ViewRegistrationDetailsViewModel.ViewRegistrationDetailsFactory(mainActivity.getApplication(), registrationDetails)).get(ViewRegistrationDetailsViewModel.class);
        binding.setViewModel(viewModel);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_registration);
            mainActivity.toolbarTitle.setText(R.string.registration);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
        }
        initListeners();
        registerReceiver();
        setUpUi();
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
                processCprFile();
            }
        });
        binding.llToastMedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processMedicalFile();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void processCprFile() {
        if (viewModel.registration.get() != null && viewModel.registration.get().getCprCertificateLink() != null) {
            if (checkPermission(mainActivity, RequestCodes.PERMISSION_FILE_CPR, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                if (CommonFunctions.getInstance().isOffline(mainActivity)) {
                    Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
                    return;
                }
                String ext;
                File file = new File(Environment.getExternalStorageDirectory()
                        + "/Ember_Docs/" + Constants.CPR_FILE_NAME + ".pdf");
                File file2 = new File(Environment.getExternalStorageDirectory()
                        + "/Ember_Docs/" + Constants.CPR_FILE_NAME + ".jpg");

                if (file.exists()) {
                    openFiles(Uri.fromFile(file).toString(), FileTypes.mimePdf);
                } else if (file2.exists()) {
                    openFiles(Uri.fromFile(file2).toString(), FileTypes.mimeImage);
                } else {
                    if (viewModel.registration.get().getCprCertificateLink().contains(FileTypes.pdf)) {
                        ext = "pdf";
                    } else {
                        ext = "jpg";
                    }
                    FileUtils.downloadFile(mainActivity, viewModel.registration.get().getCprCertificateLink(), true, ext);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void processMedicalFile() {
        if (viewModel.registration.get() != null && viewModel.registration.get().getMedicalLicenseLink() != null) {
            if (checkPermission(mainActivity, RequestCodes.PERMISSION_FILE_MEDICAL, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {

                if (CommonFunctions.getInstance().isOffline(mainActivity)) {
                    Toast.makeText(mainActivity, getString(R.string.error_network_unavailable), Toast.LENGTH_LONG).show();
                    return;
                }
                File file = new File(Environment.getExternalStorageDirectory()
                        + "/Ember_Docs/" + Constants.MEDICAL_FILE_NAME + ".pdf");
                File file2 = new File(Environment.getExternalStorageDirectory()
                        + "/Ember_Docs/" + Constants.MEDICAL_FILE_NAME + ".jpg");


                if (file.exists()) {
                    openFiles(Uri.fromFile(file).toString(), FileTypes.mimePdf);
                } else if (file2.exists()) {
                    openFiles(Uri.fromFile(file2).toString(), FileTypes.mimeImage);
                } else {
                    String ext;
                    if (viewModel.registration.get().getMedicalLicenseLink().contains(FileTypes.pdf)) {
                        ext = "pdf";
                    } else {
                        ext = "jpg";
                    }
                    FileUtils.downloadFile(mainActivity, viewModel.registration.get().getMedicalLicenseLink(), false, ext);
                }

            }
        } else {
            Toast.makeText(mainActivity, R.string.error_file_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpUi() {
        binding.llStatus.setBackgroundColor(getResources().getColor(viewModel.getStatusBackgroundColor()));
        binding.tvStatus.setText(viewModel.getStatus());
        binding.tvStatusDesc.setTextColor(getResources().getColor(viewModel.getStatusDescTextColor()));
        binding.tvStatus.setTextColor(getResources().getColor(viewModel.getStatusTextColor()));
        if(registrationDetails.getOptForTriage()){
            binding.switchTelehealthParticipation.setThumbDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_circle));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.unregisterReceiver(receiver);
        NavigationUtil.getInstance().hideBackArrow(mainActivity);
    }

    @SuppressWarnings("ConstantConditions")
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
                        if (uriString.contains(FileTypes.pdf)) {
                            openFiles(uriString, FileTypes.mimePdf);
                        } else {
                            openFiles(uriString, FileTypes.mimeImage);
                        }
                    }
                }
            }
        }
    };

    private void openFiles(String uriString, String s) {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.parse(uriString), s);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent2 = Intent.createChooser(target, "Open File");
        startActivity(intent2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean userAllowedAllRequestPermissions = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                userAllowedAllRequestPermissions = false;
            }
        }
        if (userAllowedAllRequestPermissions) {
            switch (requestCode) {
                case RequestCodes.PERMISSION_FILE_CPR:
                    processCprFile();
                    break;
                case RequestCodes.PERMISSION_FILE_MEDICAL:
                    processMedicalFile();
                    break;
            }
        }
    }

    public boolean checkPermission(Activity activity, int requestCode, String[] permissions) {
        boolean flag = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= 23) { // if lollipop or marshmallow version.
                if (flag) {
                    return true;
                } else {
                    requestPermissions(permissions, requestCode);
                }
            } else { // for version < 23 API
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
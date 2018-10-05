package com.biz4solutions.provider.registration.views.fragments;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.biz4solutions.data.RequestCodes;
import com.biz4solutions.models.CprInstitute;
import com.biz4solutions.models.Occupation;
import com.biz4solutions.models.User;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.profile.BuildConfig;
import com.biz4solutions.profile.databinding.DialogPickMediaBinding;
import com.biz4solutions.provider.R;
import com.biz4solutions.provider.databinding.FragmentRegistrationBinding;
import com.biz4solutions.provider.main.views.activities.MainActivity;
import com.biz4solutions.provider.registration.adapters.PlaceAutoCompleteAdapter;
import com.biz4solutions.provider.registration.adapters.SpinnerAdapter;
import com.biz4solutions.provider.registration.viewmodels.RegistrationViewModel;
import com.biz4solutions.provider.utilities.FileUtils;
import com.biz4solutions.utilities.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class RegistrationFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    public static final String fragmentName = "RegistrationFragment";
    private MainActivity mainActivity;
    private RegistrationViewModel viewModel;
    private FragmentRegistrationBinding binding;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;
    private SpinnerAdapter<Occupation> occupationAdapter;
    private SpinnerAdapter<CprInstitute> cprInstituteAdapter;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false);
        viewModel = ViewModelProviders.of(this, new RegistrationViewModel.RegistrationFactory(mainActivity)).get(RegistrationViewModel.class);
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        initSpinner();
        initActivity();
        initListeners();
        initViews();
        setUserData();
        return binding.getRoot();
    }

    private void initViews() {
        binding.tvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvTermsOfService.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initSpinner() {
        occupationAdapter = new SpinnerAdapter<>(mainActivity, R.layout.spinner_item_selected, new ArrayList<Occupation>(), R.layout.spinner_item_drop_down);
        cprInstituteAdapter = new SpinnerAdapter<>(mainActivity, R.layout.spinner_item_selected, new ArrayList<CprInstitute>(), R.layout.spinner_item_drop_down);
        binding.spinnerOccupation.setAdapter(occupationAdapter);
        binding.spinnerCprInstitute.setAdapter(cprInstituteAdapter);
        binding.spinnerOccupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setOccupation(occupationAdapter.getItem(position).getName());
                if (occupationAdapter.getItem(position).getName().equalsIgnoreCase("others") || occupationAdapter.getItem(position).getName().equalsIgnoreCase("other")) {
                    binding.edtOtherOccupation.setEnabled(true);
                } else {
                    binding.edtOtherOccupation.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerCprInstitute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setCprInstitute(cprInstituteAdapter.getItem(position).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initListeners() {
        viewModel.getToastMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOccupationLiveList().observe(this, new Observer<ArrayList<Occupation>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Occupation> occupations) {
                occupationAdapter.updateData(occupations);
            }
        });
        viewModel.getCprInstituteLiveList().observe(this, new Observer<ArrayList<CprInstitute>>() {
            @Override
            public void onChanged(@Nullable ArrayList<CprInstitute> cprInstitutes) {
                cprInstituteAdapter.updateData(cprInstitutes);
            }
        });
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
        enableEditScrolling();
        initPlaces();
    }

    private void initPlaces() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                    .enableAutoManage(mainActivity, 2 /* clientId */, this)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        binding.edtAddress.setOnItemClickListener(mAutocompleteClickListener);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry(Constants.BOUNDS_COUNTRY)
                .build();
        mAdapter = new PlaceAutoCompleteAdapter(mainActivity, mGoogleApiClient, null,
                typeFilter);
        binding.edtAddress.setAdapter(mAdapter);
        binding.edtAddress.setThreshold(Constants.AUTO_COMPLETE_THRESHOLD);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiClient.stopAutoManage(mainActivity);
        mGoogleApiClient.disconnect();
        viewModel.getToastMsg().removeObservers(this);
    }

    private void onPlaceSelected(Place place) {
//        Log.d("address",""+place.getAddress());
        viewModel.setAddress(place.getAddress().toString());
    }

    public void setUserData() {
        User user = SharedPrefsManager.getInstance().retrieveUserPreference(mainActivity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        viewModel.setUser(user);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void removeMedicalFile(View v) {
        viewModel.setMedicalFileExt("");
        viewModel.setMedicalCertificateUri(null);
        binding.fileNameMedical.setText(getString(R.string.txt_upload_medical_certificate));
        binding.ivMedicalRemove.setVisibility(View.GONE);
    }

    private void enableEditScrolling() {


        binding.edtSpeciality.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.edt_speciality) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void removeCprFile(View v) {
        viewModel.setCprFileExt("");
        viewModel.setCprCertificateUri(null);
        binding.fileNameCpr.setText(getString(R.string.txt_upload_cpr_certificate));
        binding.ivCprRemove.setVisibility(View.GONE);
    }

    public void captureCprFile(View v) {
        if (checkPermission(mainActivity, RequestCodes.PERMISSION_FILE_CPR,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            captureFile(101);
        }

    }

    public void captureMedicalFile(View v) {
        if (checkPermission(mainActivity, RequestCodes.PERMISSION_FILE_MEDICAL,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            captureFile(102);
        }
    }

    private void captureFile(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*|application/pdf");
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        if (requestCode == 101) {
            if (viewModel.getCprCertificateUri() == null) {
                startActivityForResult(intent, RequestCodes.RESULT_FILE_CPR);
            }
        } else {
            if (viewModel.getMedicalCertificateUri() == null) {
                startActivityForResult(intent, RequestCodes.RESULT_FILE_MEDICAL);
            }
        }
    }

    //for capturing image from device
    public void showAddMediaBottomSheet(View view) {
        final BottomSheetDialog dialogMedia = new BottomSheetDialog(view.getContext());
        final DialogPickMediaBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(view.getContext()), com.biz4solutions.profile.R.layout.dialog_pick_media, null, false);
        dialogMedia.setContentView(dialogBinding.getRoot());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == com.biz4solutions.profile.R.id.ll_capture) {
                    captureImage();
                } else if (i == com.biz4solutions.profile.R.id.ll_gallery) {
                    getMediaFromGallery();
                }
                dialogMedia.dismiss();
            }
        };

        dialogBinding.llCapture.setOnClickListener(clickListener);
        dialogBinding.llGallery.setOnClickListener(clickListener);
        dialogMedia.show();
    }

    public void getMediaFromGallery() {
        if (checkPermission(mainActivity, RequestCodes.PERMISSION_GALLERY, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            Intent mediaPicker = new Intent(Intent.ACTION_PICK);
            mediaPicker.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(Intent.createChooser(mediaPicker, "Select Image"), RequestCodes.RESULT_GALLERY);
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

    public void captureImage() {
        if (checkPermission(mainActivity, RequestCodes.PERMISSION_CAMERA,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA})) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, RequestCodes.RESULT_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(activity, "" + requestCode, Toast.LENGTH_SHORT).show();
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.RESULT_CAMERA:
                    try {
                        if (data != null && data.hasExtra("data")) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            if (photo != null) {
                                //binding.profileImage.setImageBitmap(photo);
                                Uri tempUri = getImageUri(mainActivity.getApplicationContext(), photo);
                                setImage(tempUri);
                                viewModel.setProfileImageUri(tempUri);

                            }
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case RequestCodes.RESULT_GALLERY:
                    Uri uri = data.getData();
                    try {
                        if (uri != null) {
                            setImage(uri);
                            viewModel.setProfileImageUri(uri);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG)
                            e.printStackTrace();
                    }
                    break;

                case RequestCodes.RESULT_FILE_CPR:
                    Uri fileUri = data.getData();
//                    viewModel.setCprCertificateUri(fileUri);
                    if (processFile(fileUri, RequestCodes.RESULT_FILE_CPR)) {
                        viewModel.setCprCertificateUri(fileUri);
                    }
                    break;
                case RequestCodes.RESULT_FILE_MEDICAL:
                    Uri fileUri2 = data.getData();
//                    viewModel.setMedicalCertificateUri(fileUri2);
                    if (processFile(fileUri2, RequestCodes.RESULT_FILE_MEDICAL)) {
                        viewModel.setMedicalCertificateUri(fileUri2);
                    }
                    break;
            }
        }
    }

    private boolean processFile(Uri fileUri, int resultFileCpr) {
        if (fileUri != null) {
            if (fileUri.getPath() != null) {
                String filePath = FileUtils.getPath(mainActivity, fileUri);
                if (filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);
                    if (FileUtils.isLargeSize(file)) {
                        String ext = FileUtils.getFileExtension(mainActivity, file);
                        if (ext != null && FileUtils.isValidFile(mainActivity, ext)) {
                            if (resultFileCpr == RequestCodes.RESULT_FILE_CPR) {
                                binding.fileNameCpr.setText(file.getName());
                                viewModel.setCprFileExt(ext);
                                binding.ivCprRemove.setVisibility(View.VISIBLE);
                            } else {
                                binding.fileNameMedical.setText(file.getName());
                                binding.ivMedicalRemove.setVisibility(View.VISIBLE);
                                viewModel.setMedicalFileExt(ext);
                            }
                            return true;
                        } else {
                            Toast.makeText(mainActivity, "Illegal file. Please select an image or a pdf file.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mainActivity, "Please select file under 2MB.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mainActivity, "Unable to process file, Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }


    private void setImage(Uri uri) {
        Glide.with(mainActivity).asBitmap().apply(new RequestOptions().circleCrop()).load(uri).into(binding.profileImage);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
                case RequestCodes.PERMISSION_GALLERY:
                    getMediaFromGallery();
                    break;
                case RequestCodes.PERMISSION_CAMERA:
                    captureImage();
                    break;
                case RequestCodes.PERMISSION_FILE_CPR:
                    captureCprFile(getView());
                    break;
                case RequestCodes.PERMISSION_FILE_MEDICAL:
                    captureMedicalFile(getView());
                    break;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(mainActivity, "" + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            onPlaceSelected(place);
            places.release();
        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
}

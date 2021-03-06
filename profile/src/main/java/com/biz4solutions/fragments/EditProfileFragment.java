package com.biz4solutions.fragments;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.biz4solutions.activities.ProfileActivity;
import com.biz4solutions.adapters.PlaceAutoCompleteAdapter;
import com.biz4solutions.data.RequestCodes;
import com.biz4solutions.models.User;
import com.biz4solutions.preferences.SharedPrefsManager;
import com.biz4solutions.profile.R;
import com.biz4solutions.profile.databinding.DialogPickMediaBinding;
import com.biz4solutions.profile.databinding.FragmentEditProfileBinding;
import com.biz4solutions.utilities.BindingUtils;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.viewmodels.EditProfileViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    public static final String fragmentName = "EditProfileFragment";
    private ProfileActivity activity;
    private EditProfileViewModel viewModel;
    private FragmentEditProfileBinding binding;
    private Uri photoURI;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        binding.setLifecycleOwner(this);
        binding.setFragment(this);
        activity = (ProfileActivity) getActivity();
        if (activity != null) {
            activity.toolbarTitle.setText(R.string.edit_profile);
        }
        viewModel = ViewModelProviders.of(this, new EditProfileViewModel.EditProfileViewModelFactory(getContext())).get(EditProfileViewModel.class);
        binding.setViewModel(viewModel);
        User user = SharedPrefsManager.getInstance().retrieveUserPreference(activity, Constants.USER_PREFERENCE, Constants.USER_PREFERENCE_KEY);
        if (user != null) {
            viewModel.setUserData(user);
            photoURI = CommonFunctions.getInstance().getProfileImageUri(activity,
                    viewModel.isProvider() ? Constants.ROLE_NAME_PROVIDER : Constants.ROLE_NAME_USER);
        }
        initListeners();
        initPlaces();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiClient.stopAutoManage(activity);
        mGoogleApiClient.disconnect();
        viewModel.getToastMsg().removeObservers(this);
    }

    private void initListeners() {
        viewModel.getToastMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPlaces() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity, 2 /* clientId */, this)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        binding.edtAddress.setOnItemClickListener(mAutocompleteClickListener);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry(Constants.BOUNDS_COUNTRY)
                .build();
        mAdapter = new PlaceAutoCompleteAdapter(activity, mGoogleApiClient, null,
                typeFilter);
        binding.edtAddress.setAdapter(mAdapter);
        binding.edtAddress.setThreshold(Constants.AUTO_COMPLETE_THRESHOLD);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(activity, "" + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
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

    private void onPlaceSelected(Place place) {
        try {
            viewModel.setAddress(place.getAddress().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAddMediaBottomSheet(View view) {
        final BottomSheetDialog dialogMedia = new BottomSheetDialog(view.getContext());
        final DialogPickMediaBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(view.getContext()), R.layout.dialog_pick_media, null, false);
        dialogMedia.setContentView(dialogBinding.getRoot());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.ll_capture) {
                    captureImage();
                } else if (i == R.id.ll_gallery) {
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
        if (checkPermission(activity, RequestCodes.PERMISSION_GALLERY, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
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
        if (checkPermission(activity, RequestCodes.PERMISSION_CAMERA,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA})) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (photoURI != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            startActivityForResult(intent, RequestCodes.RESULT_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodes.RESULT_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        if (photoURI != null) {
                            startCropActivity(photoURI);
                        } else if (data != null && data.hasExtra("data")) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            Uri tempUri = getImageUri(photo);
                            startCropActivity(tempUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case RequestCodes.RESULT_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        if (uri != null) {
                            startCropActivity(uri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    setImage(resultUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
                break;
        }
    }

    private void startCropActivity(Uri tempUri) {
        CropImage.activity(tempUri)
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(100)
                .start(activity, this);
    }

    private void setImage(Uri uri) {
        if (uri != null) {
            viewModel.setCapturedUri(uri);
            BindingUtils.loadImage(binding.profileImage, uri.toString());
        }
    }

    private Uri getImageUri(Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), inImage, "Title", null);
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
            }
        }
    }
}
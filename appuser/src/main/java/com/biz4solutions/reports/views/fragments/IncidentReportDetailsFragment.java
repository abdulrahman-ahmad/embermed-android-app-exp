package com.biz4solutions.reports.views.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.biz4solutions.R;
import com.biz4solutions.databinding.FragmentIncidentReportDetailsBinding;
import com.biz4solutions.main.views.activities.MainActivity;
import com.biz4solutions.models.EmsRequest;
import com.biz4solutions.utilities.Constants;
import com.biz4solutions.utilities.NavigationUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class IncidentReportDetailsFragment extends Fragment implements View.OnClickListener {

    public static final String fragmentName = "IncidentReportDetailsFragment";
    private MainActivity mainActivity;
    private FragmentIncidentReportDetailsBinding binding;
    private final static String REQUEST_DETAILS = "REQUEST_DETAILS";
    private EmsRequest request;
    private SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
    private SimpleDateFormat formatterTime = new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();
    private boolean isViewDraw = false;
    private boolean isViewDraw1 = false;

    public IncidentReportDetailsFragment() {
        // Required empty public constructor
    }

    public static IncidentReportDetailsFragment newInstance(EmsRequest request) {
        IncidentReportDetailsFragment fragment = new IncidentReportDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_DETAILS, request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            request = (EmsRequest) getArguments().getSerializable(REQUEST_DETAILS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_incident_report_details, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.navigationView.setCheckedItem(R.id.nav_incident_reports);
            mainActivity.toolbarTitle.setText(R.string.incident_report);
            NavigationUtil.getInstance().showBackArrow(mainActivity);
            if (mainActivity.feedbackRequest != null) {
                if (request != null) {
                    request.setProviderRating((float) mainActivity.feedbackRequest.getRating());
                    request.setCommentForProvider(mainActivity.feedbackRequest.getComment());
                }
                mainActivity.feedbackRequest = null;
            }
        }
        initListeners();
        initView();
        return binding.getRoot();
    }

    private void initView() {
        if (request != null) {
            String name = request.getUserDetails().getFirstName() + " " + request.getUserDetails().getLastName();
            String genderAge = request.getProviderSpecialization();
            String requestDate = "";
            String requestTime = "";
            String requestCompletedTime = "";
            if (request.getRequestTime() > 0) {
                calendar.setTimeInMillis(request.getRequestTime());
                requestDate = ("" + formatterDate.format(calendar.getTime()));
                requestTime = ("" + formatterTime.format(calendar.getTime())).replaceAll("\\.", "").toUpperCase();
            }

            if (Constants.STATUS_IMMEDIATE.equals("" + request.getPriority())) {
                binding.requestListTriageItem.cardView.setVisibility(View.GONE);
                binding.requestListCardiacItem.txtName.setText(name);
                binding.requestListCardiacItem.txtGenderAge.setText(genderAge);
                binding.requestListCardiacItem.distanceLoader.setVisibility(View.GONE);
                binding.requestListCardiacItem.txtDistance.setVisibility(View.GONE);
                binding.requestListCardiacItem.txtTime.setText(requestDate);
                binding.requestListCardiacItem.txtBottomTime.setVisibility(View.VISIBLE);
                binding.requestListCardiacItem.txtBottomTime.setText(requestTime);
                binding.cardiacPatientDiseaseItem.cardView.setVisibility(View.GONE);
                binding.layoutIncidentAmount.tvIncidentDuration.setVisibility(View.GONE);
                binding.layoutIncidentAmount.tvIncidentDurationTitle.setVisibility(View.GONE);
                binding.layoutIncidentAmount.space.setVisibility(View.GONE);
                binding.layoutCallerPending.mainLayout.setVisibility(View.GONE);
                binding.layoutRatedCallerDetails.mainLayout.setVisibility(View.GONE);
                binding.layoutCallerVisit.llCallerVisit.setVisibility(View.GONE);
            } else {
                binding.requestListCardiacItem.cardView.setVisibility(View.GONE);
                binding.requestListTriageItem.txtName.setText(name);
                binding.requestListTriageItem.txtGenderAge.setText(genderAge);
                binding.requestListTriageItem.distanceLoader.setVisibility(View.GONE);
                binding.requestListTriageItem.txtDistance.setVisibility(View.GONE);
                binding.requestListTriageItem.txtTime.setText(requestDate);
                binding.requestListTriageItem.txtBottomTime.setVisibility(View.VISIBLE);
                binding.requestListTriageItem.txtBottomTime.setText(requestTime);
                binding.cardiacPatientDiseaseItem.txtPatientDiseaseTitle.setText(R.string.patient_symptoms);
                if (request.getPatientSymptoms() != null) {
                    binding.cardiacPatientDiseaseItem.txtPatientDisease.setText(request.getPatientSymptoms());
                }
                binding.layoutIncidentAmount.imgIncidentMap.setVisibility(View.GONE);
                binding.layoutIncidentAmount.tvIncidentDuration.setText(getCallDuration());
                if (request.getProviderRating() > 0) {
                    binding.layoutCallerPending.mainLayout.setVisibility(View.GONE);
                    binding.layoutRatedCallerDetails.mainLayout.setVisibility(View.VISIBLE);
                    binding.layoutRatedCallerDetails.rbRatingBar.setRating(request.getProviderRating());
                    if (request.getCommentForProvider() != null && !request.getCommentForProvider().isEmpty()) {
                        binding.layoutRatedCallerDetails.tvRatedCallerComment.setVisibility(View.VISIBLE);
                        binding.layoutRatedCallerDetails.tvRatedCallerComment.setText(request.getCommentForProvider());
                    } else {
                        binding.layoutRatedCallerDetails.tvRatedCallerComment.setVisibility(View.GONE);
                    }
                } else {
                    binding.layoutCallerPending.mainLayout.setVisibility(View.VISIBLE);
                    binding.layoutRatedCallerDetails.mainLayout.setVisibility(View.GONE);
                }
                initLayoutCallerVisit();
            }

            setIncidentReportView();

            String amount = "$" + request.getAmount();
            binding.layoutIncidentAmount.tvAmountValue.setText(amount);
            if (request.getCompletedAt() > 0) {
                calendar.setTimeInMillis(request.getCompletedAt());
                requestDate = ("" + formatterDate.format(calendar.getTime()));
                requestTime = ("" + formatterTime.format(calendar.getTime())).replaceAll("\\.", "").toUpperCase();
                requestCompletedTime = requestDate + ", " + requestTime;
            }
            binding.layoutIncidentAmount.tvIncidentDateTime.setText(requestCompletedTime);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (request != null && request.getProviderRating() > 0) {
            binding.layoutRatedCallerDetails.rbRatingBar.setRating(request.getProviderRating());
        }
    }

    private void setIncidentReportView() {
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            binding.layoutCardiacIncidentReport.mainLayout.setVisibility(View.GONE);
            binding.layoutTriageIncidentReport.mainLayout.setVisibility(View.GONE);
            binding.layoutViewIncidentReport.mainLayout.setVisibility(View.VISIBLE);
            if (Constants.STATUS_IMMEDIATE.equals("" + request.getPriority())) {
                binding.layoutViewIncidentReport.llVictimSaved.setVisibility(View.VISIBLE);
                if (request.getIsVictimLifeSaved()) {
                    binding.layoutViewIncidentReport.tvVictimSaved.setText(R.string.yes);
                } else {
                    binding.layoutViewIncidentReport.tvVictimSaved.setText(R.string.no);
                }
            } else {
                binding.layoutViewIncidentReport.llVictimSaved.setVisibility(View.GONE);
            }
            binding.layoutViewIncidentReport.tvReportTitle.setText(request.getTitle());
            binding.layoutViewIncidentReport.tvReportComment.setText(request.getDescription());
            binding.layoutViewIncidentReport.tvReportComment.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    try {
                        if (!isViewDraw) {
                            if (binding.layoutViewIncidentReport.tvReportComment.getLayout() != null && binding.layoutViewIncidentReport.tvReportComment.getLayout().getLineCount() <= 5) {
                                isViewDraw = true;
                                binding.layoutViewIncidentReport.tvSeeMore.performClick();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
            String requestCompletedTime = "";
            if (request.getIncidentReportSubmittedAt() > 0) {
                calendar.setTimeInMillis(request.getIncidentReportSubmittedAt());
                String requestDate = ("" + formatterDate.format(calendar.getTime()));
                String requestTime = ("" + formatterTime.format(calendar.getTime())).replaceAll("\\.", "").toUpperCase();
                requestCompletedTime = requestDate + ", " + requestTime;
            }
            binding.layoutViewIncidentReport.tvReportDateTime.setText(requestCompletedTime);
        } else {
            binding.layoutViewIncidentReport.mainLayout.setVisibility(View.GONE);
            if (Constants.STATUS_IMMEDIATE.equals("" + request.getPriority())) {
                binding.layoutCardiacIncidentReport.mainLayout.setVisibility(View.VISIBLE);
                binding.layoutTriageIncidentReport.mainLayout.setVisibility(View.GONE);
            } else {
                binding.layoutCardiacIncidentReport.mainLayout.setVisibility(View.GONE);
                binding.layoutTriageIncidentReport.mainLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initLayoutCallerVisit() {
        if (request != null) {
            if (request.getProviderFeedbackReason() != null) {
                binding.layoutCallerVisit.tvReason.setText(request.getProviderFeedbackReason());
            }
            binding.layoutCallerVisit.tvReason.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    try {
                        if (!isViewDraw1) {
                            if (binding.layoutCallerVisit.tvReason.getLayout() != null && binding.layoutCallerVisit.tvReason.getLayout().getLineCount() <= 4) {
                                isViewDraw1 = true;
                                binding.layoutCallerVisit.tvSeeMore.performClick();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            switch ("" + request.getProviderFeedback()) {
                case Constants.TRIAGE_FEEDBACK_ER:
                    binding.layoutCallerVisit.tvProviderReason.setText(R.string.go_to_er);
                    break;
                case Constants.TRIAGE_FEEDBACK_URGENT_CARE:
                    binding.layoutCallerVisit.tvProviderReason.setText(R.string.go_to_urgent_care);
                    break;
                case Constants.TRIAGE_FEEDBACK_PCP:
                    binding.layoutCallerVisit.tvProviderReason.setText(R.string.go_to_pcp);
                    break;
            }
        }
    }

    private String getCallDuration() {
        long hr = TimeUnit.MILLISECONDS.toHours(request.getTriageCallDuration());
        long min = TimeUnit.MILLISECONDS.toMinutes(request.getTriageCallDuration()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(request.getTriageCallDuration()));
        long sec = TimeUnit.MILLISECONDS.toSeconds(request.getTriageCallDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(request.getTriageCallDuration()));
        String callDuration = "";
        if (hr > 0) {
            if (hr == 1) {
                callDuration = "0" + hr + " hr ";
            } else if (hr < 10) {
                callDuration = "0" + hr + " hrs ";
            } else {
                callDuration = hr + " hrs ";
            }
        }

        if (min > 0) {
            if (min == 1) {
                callDuration = callDuration + "0" + min + " min ";
            } else if (min < 10) {
                callDuration = callDuration + "0" + min + " mins ";
            } else {
                callDuration = callDuration + min + " mins ";
            }
        }

        if (sec > 0) {
            if (sec < 10) {
                callDuration = callDuration + "0" + sec + " sec";
            } else {
                callDuration = callDuration + sec + " sec";
            }
        }
        return callDuration;
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

    private void initListeners() {
        binding.layoutCallerPending.mainLayout.setOnClickListener(this);
        binding.layoutIncidentAmount.imgIncidentMap.setOnClickListener(this);
        binding.layoutCallerVisit.tvSeeMore.setOnClickListener(reasonOnclickListener());
        binding.layoutViewIncidentReport.tvSeeMore.setOnClickListener(commentOnclickListener());
    }

    private View.OnClickListener reasonOnclickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.layoutCallerVisit.tvSeeMore.setVisibility(View.GONE);
                binding.layoutCallerVisit.ivThreeDots.setVisibility(View.GONE);
                binding.layoutCallerVisit.tvReason.setMaxLines(100);
            }
        };
    }

    private View.OnClickListener commentOnclickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.layoutViewIncidentReport.tvSeeMore.setVisibility(View.GONE);
                binding.layoutViewIncidentReport.ivThreeDots.setVisibility(View.GONE);
                binding.layoutViewIncidentReport.tvReportComment.setMaxLines(100);
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.layoutCallerPending.mainLayout.getId()) {
            openFeedbackFragment();
        } else if (v.getId() == binding.layoutIncidentAmount.imgIncidentMap.getId()) {
            openMapFragment();
        }
    }

    private void openMapFragment() {
        try {
            Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.main_container);
            if (currentFragment instanceof CompleteLocationMapFragment) {
                return;
            }
            mainActivity.getSupportFragmentManager().executePendingTransactions();
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.main_container, CompleteLocationMapFragment.newInstance(request.getProviderReachedLatitude(), request.getProviderReachedLongitude()))
                    .addToBackStack(CompleteLocationMapFragment.fragmentName)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFeedbackFragment() {
        if (mainActivity != null && request != null) {
            mainActivity.feedbackRequest = null;
            mainActivity.openFeedbackFragment(request.getId(), true);
        }
    }
}
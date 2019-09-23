package tj.sodiq.jsonapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import tj.sodiq.jsonapp.MyApolloClient;
import tj.sodiq.jsonapp.ProfileViewQuery;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.dialogs.EditProfileDialog;
import tj.sodiq.jsonapp.dialogs.UserAboutDialog;
import tj.sodiq.jsonapp.dialogs.UserAchievementsDialog;
import tj.sodiq.jsonapp.dialogs.UserCareersDialog;
import tj.sodiq.jsonapp.dialogs.UserContactsDialog;

import tj.sodiq.jsonapp.dialogs.UserEducationDialog;
import tj.sodiq.jsonapp.dialogs.UserLanguagesDialog;
import tj.sodiq.jsonapp.dialogs.UserPhotoDialog;
import tj.sodiq.jsonapp.dialogs.UserScheduleDialog;
import tj.sodiq.jsonapp.dialogs.VerifyProfileDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ImageView userPhoto;

    public TextView userName, userSpecialization, userCountry, userPracticeYears, userScoreCount, userAboutText,
    userEducationOrganization, userEducationDate, userEducationDescription, userCareerOrganization,
    userWorkPosition, userAchievementText, userContactsType, userContactsValue, userLanguagesCode,
    userScheduleOrganization, mondayHours, tuesdayHours, wednesdayHours, thursdayHours, fridayHours,
    saturdayHours, sundayHours, userPhoneValue;

    FragmentClinics fragmentClinics;
    FragmentSpecializations fragmentSpecializations;

    Button btnAboutEdit, btnEducationEdit, btnCareersEdit, btnAchievementsEdit, btnContactsEdit,
    btnLanguagesEdit, btnScheduleEdit, btnVerifyProfile, btnEditProfile;

    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userPhoto = view.findViewById(R.id.userPhotoProfile);
        userName = view.findViewById(R.id.userNameProfile);

        userSpecialization = view.findViewById(R.id.userSpecializationProfile);
        userSpecialization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fragmentSpecializations = new FragmentSpecializations();
                ft.replace(R.id.container, fragmentSpecializations);
                ft.commit();
            }
        });

        userCountry = view.findViewById(R.id.userCountryProfile);
        userPracticeYears = view.findViewById(R.id.tv_userYearsPractice);
        userScoreCount = view.findViewById(R.id.tv_userScoreCount);
        userAboutText = view.findViewById(R.id.tv_userAboutText);
        userEducationOrganization = view.findViewById(R.id.tv_userEducationOrganization);
        userEducationDate = view.findViewById(R.id.tv_userEducationDate);
        userEducationDescription = view.findViewById(R.id.tv_userEducationDescription);

        userCareerOrganization = view.findViewById(R.id.tv_userCareerOrganization);
        userCareerOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fragmentClinics = new FragmentClinics();
                ft.replace(R.id.container, fragmentClinics);
                ft.commit();
            }
        });

        userWorkPosition = view.findViewById(R.id.tv_userWorkPosition);
        userAchievementText = view.findViewById(R.id.tv_userAchievementText);
        userContactsType = view.findViewById(R.id.tv_userContactsType);
        userContactsValue = view.findViewById(R.id.tv_userContactsValue);
        userLanguagesCode = view.findViewById(R.id.tv_userLanguagesCode);


        userScheduleOrganization = view.findViewById(R.id.tv_userScheduleOrganization);
        userScheduleOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fragmentClinics = new FragmentClinics();
                ft.replace(R.id.container, fragmentClinics);
                ft.commit();
            }
        });

        mondayHours = view.findViewById(R.id.tv_mondayHours);
        tuesdayHours = view.findViewById(R.id.tv_tuesdayHours);
        wednesdayHours = view.findViewById(R.id.tv_wednesdayHours);
        thursdayHours = view.findViewById(R.id.tv_thursdayHours);
        fridayHours = view.findViewById(R.id.tv_fridayHours);
        saturdayHours = view.findViewById(R.id.tv_saturdayHours);
        sundayHours = view.findViewById(R.id.tv_sundayHours);
        userPhoneValue = view.findViewById(R.id.tv_userPhoneValue);

        btnAboutEdit = view.findViewById(R.id.btn_aboutEdit);
        btnEducationEdit = view.findViewById(R.id.btn_educationEdit);
        btnCareersEdit = view.findViewById(R.id.btn_careersEdit);
        btnAchievementsEdit = view.findViewById(R.id.btn_achievementsEdit);
        btnContactsEdit = view.findViewById(R.id.btn_contactsEdit);
        btnLanguagesEdit = view.findViewById(R.id.btn_languagesEdit);
        btnScheduleEdit = view.findViewById(R.id.btn_scheduleEdit);
        btnVerifyProfile = view.findViewById(R.id.btn_verifyProfile);
        btnEditProfile = view.findViewById(R.id.btn_editProfile);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPhotoDialog dialog = new UserPhotoDialog();
                dialog.show(getChildFragmentManager(), "UserPhotoDialog");
            }
        });

        btnAboutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAboutDialog dialog = new UserAboutDialog();
                dialog.show(getChildFragmentManager(), "UserAboutDialog");
            }
        });

        btnEducationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEducationDialog dialog = new UserEducationDialog();
                dialog.show(getChildFragmentManager(), "UserEducationDialog");
            }
        });

        btnCareersEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCareersDialog dialog = new UserCareersDialog();
                dialog.show(getChildFragmentManager(), "UserCareersDialog");
            }
        });

        btnAchievementsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAchievementsDialog dialog = new UserAchievementsDialog();
                dialog.show(getChildFragmentManager(), "UserAchievementsDialog");
            }
        });

        btnContactsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserContactsDialog dialog = new UserContactsDialog();
                dialog.show(getChildFragmentManager(), "UserContactsDialog");
            }
        });

        btnLanguagesEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLanguagesDialog dialog = new UserLanguagesDialog();
                dialog.show(getChildFragmentManager(), "UserLanguagesDialog");
            }
        });

        btnScheduleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserScheduleDialog dialog = new UserScheduleDialog();
                dialog.show(getChildFragmentManager(), "UserScheduleDialog");
            }
        });

        btnVerifyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyProfileDialog dialog = new VerifyProfileDialog();
                dialog.show(getChildFragmentManager(), "VerifyProfileDialog");
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileDialog dialog = new EditProfileDialog();
                dialog.show(getChildFragmentManager(), "EditProfileDialog");
            }
        });

        final SharedPreferences prefProfile = getActivity().getSharedPreferences(getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final int id = prefProfile.getInt("userId", 0);

        ProfileViewQuery query = ProfileViewQuery
                .builder()
                .id(id)
                .build();

        MyApolloClient
                .getMyApolloClient(getActivity())
                .query(query)
                .enqueue(new ApolloCall.Callback<ProfileViewQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<ProfileViewQuery.Data> response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0){
                                    Toast.makeText(getActivity(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                                    Log.e("Ошибка загрузки профиля", response.errors().toString());
                                    return;
                                }

                                ProfileViewQuery.User user = response.data().user();

                                if (user.photo() != null){
                                    String urlUser = user.photo().filename();
                                    Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + urlUser + "&type=attach-photo&w=920&h=280").into(userPhoto);
                                } else {
                                    userPhoto.setImageResource(0);
                                }

                                    userName.setText(user.name());

                                    SharedPreferences.Editor editor =  prefProfile.edit();

                                    userSpecialization.setText(user.profile().specializations().get(0).name());

                                    userCountry.setText(user.livingRegion().name() + "," + user.livingRegion().country().name());

                                    if (user.profile().practiceYears() != null){
                                        userPracticeYears.setText(String.valueOf(user.profile().practiceYears()));
                                    } else {
                                        userPracticeYears.setText("0");
                                    }

                                    if (user.profile().scoreCount() != null){
                                        userScoreCount.setText(String.valueOf(user.profile().scoreCount()));
                                    } else {
                                        userScoreCount.setText("0");
                                    }

                                    userAboutText.setText(user.profile().about());

                                    editor.putString("userAbout", user.profile().about());
                                    editor.commit();

                                    userEducationOrganization.setText(user.profile().educations().get(0).organization());

                                    userEducationDate.setText(user.profile().educations().get(0).dateFrom() + " - " + user.profile().educations().get(0).dateTo());

                                    userEducationDescription.setText(user.profile().educations().get(0).description());

                                    userCareerOrganization.setText(user.profile().careers().get(0).clinic().name());

                                    if (user.profile().careers().get(0).dateTo() != null){

                                        String dateTo = String.valueOf(user.profile().careers().get(0).dateTo());

                                        userWorkPosition.setText(user.profile().careers().get(0).workPosition() + " " + user.profile().careers().get(0).dateFrom()
                                                + " - " + dateTo + " " + user.profile().careers().get(0).department());

                                    } else {

                                        String dateTo = "по настоящее время";

                                        userWorkPosition.setText(user.profile().careers().get(0).workPosition() + " " + user.profile().careers().get(0).dateFrom()
                                                + " - " + dateTo + " " + user.profile().careers().get(0).department());
                                    }

                                    userAchievementText.setText(user.profile().achievements().get(0).name());

                                    userContactsType.setText(user.profile().contacts().get(0).contactType());

                                    userContactsValue.setText(user.profile().contacts().get(0).value());

                                    userLanguagesCode.setText(user.profile().profileLanguages().get(0).code() + " (" + user.profile().profileLanguages().get(0).level()+")");

                                    userScheduleOrganization.setText(user.profile().careers().get(0).clinic().name());

                                    if (user.profile().careers().get(0).schedules().size() > 0){
                                        mondayHours.setText(user.profile().careers().get(0).schedules().get(0).registerDate() + " - " + user.profile().careers().get(0).schedules().get(0).finishDate());
                                    } else {
                                        mondayHours.setText("нет приема");
                                    }
                                    if (user.profile().careers().get(0).schedules().size() > 1){
                                        tuesdayHours.setText(user.profile().careers().get(0).schedules().get(1).registerDate() + " - " + user.profile().careers().get(0).schedules().get(1).finishDate());
                                    } else {
                                        tuesdayHours.setText("нет приема");
                                    }

                                    if (user.profile().careers().get(0).schedules().size() > 2){
                                        wednesdayHours.setText(user.profile().careers().get(0).schedules().get(2).registerDate() + " - " + user.profile().careers().get(0).schedules().get(2).finishDate());
                                    } else {
                                        wednesdayHours.setText("нет приема");
                                    }

                                    if (user.profile().careers().get(0).schedules().size() > 3){
                                        thursdayHours.setText(user.profile().careers().get(0).schedules().get(3).registerDate() + " - " + user.profile().careers().get(0).schedules().get(3).finishDate());
                                    } else {
                                        thursdayHours.setText("нет приема");
                                    }

                                    if (user.profile().careers().get(0).schedules().size() > 4){
                                        fridayHours.setText(user.profile().careers().get(0).schedules().get(4).registerDate() + " - " + user.profile().careers().get(0).schedules().get(4).finishDate());
                                    } else {
                                        fridayHours.setText("нет приема");
                                    }

                                    if (user.profile().careers().get(0).schedules().size() > 5){
                                        saturdayHours.setText(user.profile().careers().get(0).schedules().get(5).registerDate() + " - " + user.profile().careers().get(0).schedules().get(5).finishDate());
                                    } else {
                                        saturdayHours.setText("нет приема");
                                    }

                                    if (user.profile().careers().get(0).schedules().size() > 6){
                                        sundayHours.setText(user.profile().careers().get(0).schedules().get(6).registerDate() + " - " + user.profile().careers().get(0).schedules().get(6).finishDate());
                                    } else {
                                        sundayHours.setText("нет приема");
                                    }

                                    userPhoneValue.setText(user.profile().phoneNumber());

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull final ApolloException e) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Queru Response Failure", Toast.LENGTH_SHORT).show();
                                Log.e("Exception error", e.toString());
                            }
                        });
                    }
                });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

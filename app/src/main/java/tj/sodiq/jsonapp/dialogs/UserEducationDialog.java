package tj.sodiq.jsonapp.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import tj.sodiq.jsonapp.AuthorizationQueryMutation;
import tj.sodiq.jsonapp.MyApolloClient;
import tj.sodiq.jsonapp.ProfileEducationSaveMutation;
import tj.sodiq.jsonapp.ProfileViewQuery;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.type.ProfileEducationSaveArgs;

public class UserEducationDialog extends DialogFragment {

    private static final String TAG = "UserEducationDialog";

    //widgets
    private EditText educationOrganization, specializationOrFaculty, dateFrom, dateTo, description;
    private TextView actionOk, actionCancel, image, add;
    private CheckBox untilToday;
    private Boolean tillNow;
    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_user_education, container, false);
        actionOk = view.findViewById(R.id.action_okEd);
        actionCancel = view.findViewById(R.id.action_cancelEd);
        educationOrganization = view.findViewById(R.id.etEducationOrganization);
        specializationOrFaculty = view.findViewById(R.id.etSpecializationOrFaculty);
        dateFrom = view.findViewById(R.id.etEdDateFrom);
        untilToday = view.findViewById(R.id.cbEdUntilToday);
        dateTo = view.findViewById(R.id.etEdDateTo);
        description = view.findViewById(R.id.etEdDescription);
        image = view.findViewById(R.id.ivEdImage);
        add = view.findViewById(R.id.tvEdAdd);


        SharedPreferences preferences = getActivity().getSharedPreferences(getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final int profileId = preferences.getInt("userId", 0);

        String cookie = preferences.getString("cookie", "No cookie");

        Log.e("Cookie", cookie);

        ProfileViewQuery query = ProfileViewQuery
                .builder()
                .id(profileId)
                .build();

        Log.e("profileId", String.valueOf(profileId));

        MyApolloClient
                .getMyApolloClient(getActivity())
                .query(query)
                .enqueue(new ApolloCall.Callback<ProfileViewQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<ProfileViewQuery.Data> response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0) {
                                    Toast.makeText(getActivity(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                ProfileViewQuery.User user = response.data().user();

                                id = user.profile().educations().get(0).id();

                                educationOrganization.setText(user.profile().educations().get(0).organization());

                                specializationOrFaculty.setText(user.profile().educations().get(0).field());

                                dateFrom.setText(String.valueOf(user.profile().educations().get(0).dateFrom()));

                                dateTo.setText(String.valueOf(user.profile().educations().get(0).dateTo()));

                                description.setText(user.profile().educations().get(0).description());

                                tillNow = user.profile().educations().get(0).tillNow();

                                if (tillNow) {
                                    untilToday.setChecked(true);
                                    dateTo.setVisibility(View.INVISIBLE);
                                }




//                                if (user.profile().educations().get(0).attachFile() != null){
//                                    String urlEducation = user.profile().educations().get(0).attachFile().filename();
//                                    Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + urlEducation + "&type=attach-photo&w=920&h=280").into(new Target() {
//                                        @Override
//                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                            image.setBackground(new BitmapDrawable(getResources(), bitmap));
//                                        }
//
//                                        @Override
//                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                                            Log.d("Failed", "Failed");
//                                        }
//
//                                        @Override
//                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//                                            Log.d("Failed", "Failed");
//                                        }
//                                    });
//                                }

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        Toast.makeText(getActivity(), "Query respinse failure", Toast.LENGTH_SHORT).show();

                    }
                });

        getDialog().setTitle("Образование");

        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        actionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input.");

                ProfileEducationSaveArgs education = ProfileEducationSaveArgs
                        .builder()
                        .id(id)
                        .organization(educationOrganization.getText().toString())
                        .field(specializationOrFaculty.getText().toString())
                        .dateFrom(Integer.valueOf(dateFrom.getText().toString()))
                        .dateTo(Integer.valueOf(dateTo.getText().toString()))
                        .description(description.getText().toString())
                        .tillNow(tillNow)
                        .build();

                List<ProfileEducationSaveArgs> educations = new ArrayList<>();
                educations.add(education);

                ProfileEducationSaveMutation mutation = ProfileEducationSaveMutation
                        .builder()
                        .educations(educations)
                        .removeEducationIds(new ArrayList<Integer>())
                        .build();

                MyApolloClient
                        .getMyApolloClient(getActivity())
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ProfileEducationSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<ProfileEducationSaveMutation.Data> response) {

                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        if (response.errors().size() > 0) {
                                            Toast.makeText(getActivity(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Toast.makeText(getActivity(), "Add successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                getDialog().dismiss();
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {

                                Toast.makeText(getActivity(), "Add Failure", Toast.LENGTH_SHORT).show();

                                getDialog().dismiss();
                            }
                        });
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = 1;
//                RelativeLayout.LayoutParams rl_educationEdit = new RelativeLayout(RelativeLayout)
            }
        });

        return view;
    }

}

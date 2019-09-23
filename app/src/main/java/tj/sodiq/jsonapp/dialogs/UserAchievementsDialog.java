package tj.sodiq.jsonapp.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import tj.sodiq.jsonapp.MyApolloClient;
import tj.sodiq.jsonapp.ProfileAchievementSaveMutation;
import tj.sodiq.jsonapp.ProfileViewQuery;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.type.ProfileAchievementSaveArgs;

public class UserAchievementsDialog extends DialogFragment {

    private static final String TAG = "UserAchievementsDialog";

    //widgets
    private EditText achievements;
    private TextView actionOk, actionCancel, image, add;
    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_user_achievements, container, false);
        actionOk = view.findViewById(R.id.action_okAc);
        actionCancel = view.findViewById(R.id.action_cancelAc);
        achievements = view.findViewById(R.id.etAchievements);
        image = view.findViewById(R.id.ivAcImage);
        add = view.findViewById(R.id.tvAcAdd);

        SharedPreferences preferences = getActivity().getSharedPreferences(getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final int profileId = preferences.getInt("userId", 0);

        ProfileViewQuery query = ProfileViewQuery
                .builder()
                .id(profileId)
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

                                if (response.errors().size() > 0) {
                                    Toast.makeText(getActivity(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                ProfileViewQuery.User user = response.data().user();

                                id = user.profile().achievements().get(0).id();

                                achievements.setText(user.profile().achievements().get(0).name());
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        Toast.makeText(getActivity(), "Query response failure", Toast.LENGTH_SHORT).show();

                    }
                });


        getDialog().setTitle("Достижения");

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

                ProfileAchievementSaveArgs achievement = ProfileAchievementSaveArgs
                        .builder()
                        .id(id)
                        .name(achievements.getText().toString())
                        .build();

                List<ProfileAchievementSaveArgs> achievements = new ArrayList<>();
                achievements.add(achievement);

                ProfileAchievementSaveMutation mutation = ProfileAchievementSaveMutation
                        .builder()
                        .achievements(achievements)
                        .removeAchievementIds(new ArrayList<Integer>())
                        .build();

                MyApolloClient
                        .getMyApolloClient(getActivity())
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ProfileAchievementSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<ProfileAchievementSaveMutation.Data> response) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response.errors().size() > 0){
                                            Toast.makeText(getActivity(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                                        }

                                        Toast.makeText(getActivity(), "Added successfully",  Toast.LENGTH_SHORT).show();
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

        return view;
    }
}

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
import tj.sodiq.jsonapp.ProfileLanguageSaveMutation;
import tj.sodiq.jsonapp.ProfileViewQuery;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.type.ProfileLanguageSaveArgs;

public class UserLanguagesDialog extends DialogFragment {

    private static final String TAG = "UserLanguagesDialog";

    //widgets
    private EditText languagesCode, languagesLevel;
    private TextView actionOk, actionCancel, add;
    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_user_languages, container, false);
        actionOk = view.findViewById(R.id.action_okLn);
        actionCancel = view.findViewById(R.id.action_cancelLn);
        languagesCode = view.findViewById(R.id.etLanguagesCode);
        languagesLevel = view.findViewById(R.id.etLanguagesLevel);
        add = view.findViewById(R.id.tvLnAdd);

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

                                id = user.profile().profileLanguages().get(0).id();

                                languagesCode.setText(user.profile().profileLanguages().get(0).code());

                                languagesLevel.setText(user.profile().profileLanguages().get(0).level());

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        Toast.makeText(getActivity(), "Query response failure", Toast.LENGTH_SHORT).show();

                    }
                });


        getDialog().setTitle("Языки");

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

                ProfileLanguageSaveArgs language = ProfileLanguageSaveArgs
                        .builder()
                        .id(id)
                        .code(languagesCode.getText().toString())
                        .level(languagesLevel.getText().toString())
                        .build();

                List<ProfileLanguageSaveArgs> languages = new ArrayList<>();
                languages.add(language);

                ProfileLanguageSaveMutation mutation = ProfileLanguageSaveMutation
                        .builder()
                        .languages(languages)
                        .removeLanguageIds(new ArrayList<Integer>())
                        .build();

                MyApolloClient
                        .getMyApolloClient(getActivity())
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ProfileLanguageSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<ProfileLanguageSaveMutation.Data> response) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response.errors().size() > 0) {
                                            Toast.makeText(getActivity(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
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

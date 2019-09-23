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

import tj.sodiq.jsonapp.MyApolloClient;
import tj.sodiq.jsonapp.ProfileViewQuery;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.UserSaveMutation;
import tj.sodiq.jsonapp.type.ProfileSaveArgs;
import tj.sodiq.jsonapp.type.UserUpdateArgs;

public class EditProfileDialog extends DialogFragment {

    private static final String TAG = "EditProfileDialog";

    //widgets
    private EditText firstName, secondName, middleName, specialization, yearsOfPractice, degree, rank, category, city;
    private TextView actionOk, actionCancel;
    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_profile, container, false);
        actionOk = view.findViewById(R.id.action_okPr);
        actionCancel = view.findViewById(R.id.action_cancelPr);
        firstName = view.findViewById(R.id.etUserFirstName);
        secondName = view.findViewById(R.id.etUserSecondName);
        middleName = view.findViewById(R.id.etUserMiddleName);
        specialization = view.findViewById(R.id.etUserSpecializations);
        yearsOfPractice = view.findViewById(R.id.etUserYearsOfPractice);
        degree = view.findViewById(R.id.etUserDegree);
        rank = view.findViewById(R.id.etUserRank);
        category = view.findViewById(R.id.etUserCategory);
        city = view.findViewById(R.id.etUserCity);

        SharedPreferences prefUser = getActivity().getSharedPreferences(getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final int profileId = prefUser.getInt("userId", 0);

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

                                id = user.id();

                                firstName.setText(user.firstName());

                                secondName.setText(user.secondName());

                                middleName.setText(user.middleName());

                                specialization.setText(user.profile().specializations().get(0).name());  // не сделана так как необходимо сделат ьмножественный выбор и мутация сохранения происходит по айди

                                if (user.profile().practiceYears() == null || user.profile().practiceYears() == 0){
                                    yearsOfPractice.setText("0");
                                } else {
                                    yearsOfPractice.setText(user.profile().practiceYears());
                                }

                                degree.setText(user.profile().degree());

                                rank.setText(user.profile().rank());

                                category.setText(user.profile().category());

                                city.setText(user.livingRegion().name());  // не сделана так как в соответсвующей мутации нет переменной

                            }
                        });

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        Toast.makeText(getActivity(), "Query response failure", Toast.LENGTH_SHORT).show();

                    }
                });

        getDialog().setTitle("Редактировать профиль");

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

                ProfileSaveArgs profile = ProfileSaveArgs
                        .builder()
                        .id(id)
                        .category(category.getText().toString())
                        .degree(degree.getText().toString())
                        .practiceYears(Integer.valueOf(yearsOfPractice.getText().toString()))
                        .rank(rank.getText().toString())
                        .build();

                UserUpdateArgs userUpdate = UserUpdateArgs
                        .builder()
                        .firstName(firstName.getText().toString())
                        .secondName(secondName.getText().toString())
                        .middleName(middleName.getText().toString())
                        .id(id)
                        .build();

                UserSaveMutation mutation = UserSaveMutation
                        .builder()
                        .profileSaveArgs(profile)
                        .userUpdateArgs(userUpdate)
                        .build();

                MyApolloClient
                        .getMyApolloClient(getActivity())
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<UserSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<UserSaveMutation.Data> response) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response.errors().size() > 0) {
                                            Toast.makeText(getActivity(),"Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
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

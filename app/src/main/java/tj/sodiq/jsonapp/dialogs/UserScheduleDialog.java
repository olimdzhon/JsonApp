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
import tj.sodiq.jsonapp.ProfileScheduleSaveMutation;
import tj.sodiq.jsonapp.ProfileViewQuery;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.type.ProfileSaveArgs;
import tj.sodiq.jsonapp.type.ScheduleSaveArgs;

public class UserScheduleDialog extends DialogFragment {

    private static final String TAG = "UserScheduleDialog";

    //widgets
    private EditText scheduleOrganization, monTimeFromHour, monTimeFromMinute, monTimeToHour, monTimeToMinute, recordPhone;
    private TextView actionOk, actionCancel, addMon, addTue, addWed, addThu, addSat, addSun;
    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_user_schedule, container, false);
        actionOk = view.findViewById(R.id.action_okSc);
        actionCancel = view.findViewById(R.id.action_cancelSc);
        scheduleOrganization = view.findViewById(R.id.etScheduleOrganization);
        monTimeFromHour = view.findViewById(R.id.etMonTimeFromHour);
        monTimeFromMinute = view.findViewById(R.id.etMonTimeFromMinute);
        monTimeToHour = view.findViewById(R.id.etMonTimeToHour);
        monTimeToMinute = view.findViewById(R.id.etMonTimeToMinute);
        recordPhone = view.findViewById(R.id.etRecordPhone);
        addMon = view.findViewById(R.id.tvMonAdd);
        addTue = view.findViewById(R.id.tvTueAdd);
        addWed = view.findViewById(R.id.tvWedAdd);
        addThu = view.findViewById(R.id.tvThuAdd);
        addSat = view.findViewById(R.id.tvSatAdd);
        addSun = view.findViewById(R.id.tvSunAdd);

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

                                id = user.profile().careers().get(0).schedules().get(0).id();

                                scheduleOrganization.setText(user.profile().careers().get(0).clinic().name());

                                String registerDate = user.profile().careers().get(0).schedules().get(0).registerDate();

                                monTimeFromHour.setText(registerDate.substring(0, registerDate.indexOf(":")));

                                monTimeFromMinute.setText(registerDate.substring(registerDate.indexOf(":") + 1, registerDate.length()));

                                String finishDate = user.profile().careers().get(0).schedules().get(0).finishDate();

                                monTimeToHour.setText(finishDate.substring(0, finishDate.indexOf(":")));

                                monTimeToMinute.setText(finishDate.substring(finishDate.indexOf(":") + 1, finishDate.length()));

                                recordPhone.setText(user.profile().phoneNumber());

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        Toast.makeText(getActivity(), "Query response failure", Toast.LENGTH_SHORT).show();

                    }
                });

        getDialog().setTitle("График приема");

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

                ScheduleSaveArgs schedule = ScheduleSaveArgs
                        .builder()
                        .id(id)
                        .registerDate(monTimeFromHour.getText().toString() + ":" + monTimeFromMinute.getText().toString())
                        .finishDate(monTimeToHour.getText().toString() + ":" + monTimeToMinute.getText().toString())
                        .build();

                List<ScheduleSaveArgs> schedules = new ArrayList<>();
                schedules.add(schedule);

                ProfileSaveArgs profile = ProfileSaveArgs
                        .builder()
                        .id(profileId)
                        .build();

                ProfileScheduleSaveMutation mutation = ProfileScheduleSaveMutation
                        .builder()
                        .profile(profile)
                        .schedules(schedules)
                        .scheduleIds(new ArrayList<Integer>())
                        .build();

                MyApolloClient
                        .getMyApolloClient(getActivity())
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ProfileScheduleSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<ProfileScheduleSaveMutation.Data> response) {

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

package tj.sodiq.jsonapp.dialogs;

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import tj.sodiq.jsonapp.MyApolloClient;
import tj.sodiq.jsonapp.ProfileCareerSaveMutation;
import tj.sodiq.jsonapp.ProfileViewQuery;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.type.ProfileCareerSaveArgs;

public class UserCareersDialog extends DialogFragment {

    private static final String TAG = "UserCareersDialog";

    //widgets
    private EditText careersOrganization, workPosition, dateFrom, dateTo, description;
    private TextView actionOk, actionCancel, image, add;
    private CheckBox untilToday;
    private Boolean tillNow;
    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_user_careers, container, false);

        actionOk = view.findViewById(R.id.action_okCr);
        actionCancel = view.findViewById(R.id.action_cancelCr);
        careersOrganization = view.findViewById(R.id.etCareersOrganization);
        workPosition = view.findViewById(R.id.etWorkPosition);
        dateFrom = view.findViewById(R.id.etCrDateFrom);
        untilToday = view.findViewById(R.id.cbCrUntilToday);
        dateTo = view.findViewById(R.id.etCrDateTo);
        description = view.findViewById(R.id.etCrDescription);
        image = view.findViewById(R.id.ivCrImage);
        add = view.findViewById(R.id.tvCrAdd);

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

                                         if (response.errors().size() > 0){
                                             Toast.makeText(getActivity(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                                             return;
                                         }

                                         ProfileViewQuery.User user = response.data().user();

                                         id = user.profile().careers().get(0).id();

                                         careersOrganization.setText(user.profile().careers().get(0).clinic().name());

                                         workPosition.setText(user.profile().careers().get(0).workPosition());

                                         dateFrom.setText(String.valueOf(user.profile().careers().get(0).dateFrom()));

                                         if (user.profile().careers().get(0).dateTo() == null) {

                                             dateTo.setText("0");

                                         } else {

                                             dateTo.setText(String.valueOf(user.profile().careers().get(0).dateTo()));

                                         }

                                         description.setText(user.profile().careers().get(0).description());

                                         tillNow = user.profile().careers().get(0).tillNow();

                                         if (tillNow) {
                                             untilToday.setChecked(true);
                                             dateTo.setVisibility(View.INVISIBLE);
                                             dateTo.setText("0");
                                         }

                                     }
                                 });
                             }

                             @Override
                             public void onFailure(@NotNull ApolloException e) {

                                 Toast.makeText(getActivity(), "Query response failure", Toast.LENGTH_SHORT).show();

                             }
                         });

                        getDialog().setTitle("Карьера");

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

                ProfileCareerSaveArgs career = ProfileCareerSaveArgs
                        .builder()
                        .id(id)
                        .organization(careersOrganization.getText().toString())
                        .workPosition(workPosition.getText().toString())
                        .dateFrom(Integer.valueOf(dateFrom.getText().toString()))
                        .dateTo(Integer.valueOf(dateTo.getText().toString()))
                        .description(description.getText().toString())
                        .tillNow(tillNow)
                        .build();

                List<ProfileCareerSaveArgs> careers = new ArrayList<>();
                careers.add(career);

                ProfileCareerSaveMutation mutation = ProfileCareerSaveMutation
                        .builder()
                        .careers(careers)
                        .removeCareerIds(new ArrayList<Integer>())
                        .build();

                MyApolloClient
                        .getMyApolloClient(getActivity())
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ProfileCareerSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<ProfileCareerSaveMutation.Data> response) {

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

        add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                LinearLayout linearLayout = view.findViewById(R.id.rl_careersParent);

                RelativeLayout relativeLayoutNew = new RelativeLayout(getContext());

                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                relativeParams.addRule(RelativeLayout.BELOW, R.id.rl_careersEdit);

                RelativeLayout.LayoutParams relativeParams1 = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                linearLayout.addView(relativeLayoutNew, relativeParams);

                EditText et1 = new EditText(getContext());
                et1.setId(1);


                EditText et2 = new EditText(getContext());
                et2.setId(2);


                relativeLayoutNew.addView(et1);
                relativeLayoutNew.addView(et2, relativeParams1);

                Log.e("linerLayoutParent", "linerLayoutParent: " + linearLayout);

            }
        });

        return view;
    }
}

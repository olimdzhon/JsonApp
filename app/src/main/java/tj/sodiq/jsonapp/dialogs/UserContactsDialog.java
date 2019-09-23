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
import tj.sodiq.jsonapp.ProfileContactSaveMutation;
import tj.sodiq.jsonapp.ProfileViewQuery;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.type.ProfileContactSaveArgs;

public class UserContactsDialog extends DialogFragment {

    private static final String TAG = "UserContactsEdit";

    //widgets
    private EditText contactsType, contactsValue;
    private TextView actionOk, actionCancel, add;
    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_user_contacts, container, false);
        actionOk = view.findViewById(R.id.action_okCt);
        actionCancel = view.findViewById(R.id.action_cancelCt);
        contactsType = view.findViewById(R.id.etContactsType);
        contactsValue = view.findViewById(R.id.etContactsValue);
        add = view.findViewById(R.id.tvCtAdd);

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

                                id = user.profile().contacts().get(0).id();

                                contactsType.setText(user.profile().contacts().get(0).contactType());

                                contactsValue.setText(user.profile().contacts().get(0).value());

                            }
                        });


                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        Toast.makeText(getActivity(), "Query response failure", Toast.LENGTH_SHORT).show();

                    }
                });


        getDialog().setTitle("Контакты");

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

                ProfileContactSaveArgs contact = ProfileContactSaveArgs
                        .builder()
                        .id(id)
                        .contactType(contactsType.getText().toString())
                        .value(contactsValue.getText().toString())
                        .build();

                List<ProfileContactSaveArgs> contacts = new ArrayList<>();
                contacts.add(contact);

                ProfileContactSaveMutation mutation = ProfileContactSaveMutation
                        .builder()
                        .contacts(contacts)
                        .removeContactIds(new ArrayList<Integer>())
                        .build();

                MyApolloClient
                        .getMyApolloClient(getActivity())
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ProfileContactSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<ProfileContactSaveMutation.Data> response) {

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

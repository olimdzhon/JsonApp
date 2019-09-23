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


import java.util.Date;


import tj.sodiq.jsonapp.R;


public class VerifyProfileDialog extends DialogFragment {

    private static final String TAG = "VerifyProfileDialog";

    //widgets
    private EditText input;
    private TextView actionOk, actionCancel;
    private int id;
    private String firstName, secondName, middleName;
    private Date birthday;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_verify_profile, container, false);
        actionOk = view.findViewById(R.id.action_ok);
        actionCancel = view.findViewById(R.id.action_cancel);
        input = view.findViewById(R.id.input);

        SharedPreferences prefUser = getActivity().getSharedPreferences(getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final int profileId = prefUser.getInt("userId", 0);

        getDialog().setTitle("Подтвердить профиль");

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

//                String input1 = input.getText().toString();
//                if (!input.equals("")){

//                    FragmentProfile fragment = (FragmentProfile) getActivity().getSupportFragmentManager().findFragmentByTag("FragmentProfile");
//                    fragment.userAboutText.setText(input1);
//            }


            }
        });

        return view;
    }

}



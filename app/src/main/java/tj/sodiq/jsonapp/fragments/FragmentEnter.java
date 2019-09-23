package tj.sodiq.jsonapp.fragments;

import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import tj.sodiq.jsonapp.AuthorizationQueryMutation;
import tj.sodiq.jsonapp.Main2Activity;
import tj.sodiq.jsonapp.MyApolloClient;
import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.type.AuthorizationArgs;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentEnter.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentEnter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEnter extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    EditText emailOrPhone, plainPassword;
    Button button;

    LandingPageFragment landingPageFragment;


    public FragmentEnter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentExit.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentEnter newInstance(String param1, String param2) {
        FragmentEnter fragment = new FragmentEnter();
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
        final View view = inflater.inflate(R.layout.fragment_enter, container, false);

        landingPageFragment = new LandingPageFragment();

        emailOrPhone = view.findViewById(R.id.editText3);
        plainPassword = view.findViewById(R.id.editText4);

        button = view.findViewById(R.id.button5);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthorizationArgs authorizationArgs = AuthorizationArgs
                        .builder()
                        .emailOrPhone(emailOrPhone.getText().toString())
                        .plainPassword(plainPassword.getText().toString())
                        .isLongSession(true)
                        .build();

                final AuthorizationQueryMutation authorization = AuthorizationQueryMutation
                        .builder()
                        .authorizationArgs(authorizationArgs)
                        .build();

                MyApolloClient
                        .getMyApolloClient(getActivity())
                        .mutate(authorization)
                        .enqueue(new ApolloCall.Callback<AuthorizationQueryMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<AuthorizationQueryMutation.Data> response) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response.errors().size() > 0) {
                                            Toast.makeText(getActivity(), "Ошибка входа", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        AuthorizationQueryMutation.SessionUser sessionUser = response.data().sessionUser();

                                        SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getString(R.string.preference_file_key), MODE_PRIVATE);

                                        String cookies = prefs.getString("cookie", "No cookie");
//                                            Log.e("cookie1", cookies);

                                        SharedPreferences.Editor editor2 = prefs.edit();
                                        editor2.putString("cookies", cookies);

                                        if (sessionUser.__typename() != null) {
                                            editor2.putString("__typename", sessionUser.__typename());
                                        }

                                        if (sessionUser.userId() != null) {
                                            editor2.putInt("userId", sessionUser.userId());
                                        }

                                        if (sessionUser.accountId() != null) {
                                            editor2.putInt("sessionUserAccountId", sessionUser.accountId());
                                        }

                                        if (sessionUser.profileType() != null) {
                                            editor2.putString("profileType-rawValue", sessionUser.profileType().rawValue());
                                        }

                                        if (sessionUser.lastActiveTime() != null) {
                                            editor2.putInt("lastActiveTime", sessionUser.lastActiveTime().intValue());
                                        }

                                        if (sessionUser.isLongSession() != null) {
                                            editor2.putBoolean("isLongSession", sessionUser.isLongSession());
                                        }

                                        if (sessionUser.name() != null) {
                                            editor2.putString("name", sessionUser.name());
                                        }

                                        if (sessionUser.identicon() != null) {
                                            editor2.putString("identicon", sessionUser.identicon());
                                        }

                                        if (sessionUser.isModerator() != null) {
                                            editor2.putBoolean("isModerator", sessionUser.isModerator());
                                        }

                                        if (sessionUser.isDeveloper() != null) {
                                            editor2.putBoolean("isDeveloper", sessionUser.isDeveloper());
                                        }

                                        if (sessionUser.isVerified() != null) {
                                            editor2.putBoolean("isVerified", sessionUser.isVerified());
                                        }

                                        if (sessionUser.lastVerificationRequest() != null) {
                                            editor2.putLong("lastVerificationRequest", sessionUser.lastVerificationRequest().getTime());
                                        }

                                        if (sessionUser.photo() != null) {
                                            editor2.putString("photo__typename", sessionUser.photo().__typename());
                                        }

                                        if (sessionUser.photo() != null) {
                                            editor2.putInt("photoId", sessionUser.photo().id());
                                        }

                                        if (sessionUser.photo() != null) {
                                            editor2.putString("photoFilename", sessionUser.photo().filename());
                                        }

                                        editor2.apply();

                                        ((Main2Activity) getActivity()).updateNavBar(sessionUser);

                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.replace(R.id.container, landingPageFragment);
                                        ft.commit();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e("Вход", "Ошибка входа");
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
    public void onResume() {
        super.onResume();
        emailOrPhone.getText().clear();
        plainPassword.getText().clear();
    }

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

package tj.sodiq.jsonapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tj.sodiq.jsonapp.type.MessageFilter;
import tj.sodiq.jsonapp.type.UserFilter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMessageUsers.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMessageUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMessageUsers extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EndlessRecycleViewScrollListener scrollListener;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MessageUsersRecyclerCustomAdapter adapter;
    UserFilter.Builder userFilterBuilder;

    public FragmentMessageUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMessageUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMessageUsers newInstance(String param1, String param2) {
        FragmentMessageUsers fragment = new FragmentMessageUsers();
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

    public void load(final boolean clear){

        MessageUsersQuery query = MessageUsersQuery
                .builder()
                .filter(userFilterBuilder.build())
                .build();

        MyApolloClient
                .getMyApolloClient(getContext())
                .query(query)
                .enqueue(new ApolloCall.Callback<MessageUsersQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<MessageUsersQuery.Data> response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0){
                                    Toast.makeText(getActivity(), "Ошибка загрузки MessageList", Toast.LENGTH_SHORT).show();
                                    Log.e("MessageList", "Ошибка загрузки" + response.errors().toString());
                                    return;
                                }

                                if (clear){
                                    adapter.messageUsers.clear();
                                    scrollListener.resetState();
                                }
                                MessageUsersQuery.Data data = response.data();
                                adapter.messageUsersCount = data.messageUsers.size();
                                adapter.messageUsers.addAll(data.messageUsers);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("apollo query", e.toString());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = view.findViewById(R.id.message_list);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessageUsersRecyclerCustomAdapter(getContext(), new ArrayList<MessageUsersQuery.MessageUser>());
        adapter.setOnEntryClickListener(new MessageUsersRecyclerCustomAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                Intent intent = new Intent(getActivity(), MessageViewActivity.class);
                intent.putExtra("idMessageUsers", adapter.messageUsers.get(position).user.id);
                intent.putExtra("userName", adapter.messageUsers.get(position).user.firstName + " " + adapter.messageUsers.get(position).user.secondName);
                intent.putExtra("List", "Message");
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        userFilterBuilder = UserFilter
                .builder()
                .limit(10);

        load(false);

        scrollListener = new EndlessRecycleViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                if (adapter.messageUsers.size() < adapter.messageUsersCount){
                    userFilterBuilder.offset(page * 10);
                    load(false);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.messagePullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh(){
                userFilterBuilder.offset(0);
                load(true);
                pullToRefresh.setRefreshing(false);
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

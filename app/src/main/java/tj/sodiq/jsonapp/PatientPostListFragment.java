package tj.sodiq.jsonapp;

import android.content.Intent;
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
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tj.sodiq.jsonapp.type.PatientPostFilter;

public class PatientPostListFragment extends Fragment {

    private EndlessRecycleViewScrollListener scrollListener;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    PatientPostRecyclerCustomAdapter adapter;
    PatientPostFilter.Builder patientPostFilterBuilder;

    public void load(final boolean clear) {

        PatientPostListQuery query = PatientPostListQuery
                .builder()
                .filter(patientPostFilterBuilder.build())
                .build();

        MyApolloClient
                .getMyApolloClient(getContext())
                .query(query)
                .enqueue(new ApolloCall.Callback<PatientPostListQuery.Data>() {

                    @Override
                    public void onResponse(@NotNull final com.apollographql.apollo.api.Response<PatientPostListQuery.Data> response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0){
                                    Toast.makeText(getActivity(), "Ошибка загрузки PatientPostList", Toast.LENGTH_SHORT).show();
                                    Log.e( "Ошибка загрузки PatPoLi", response.errors().toString());
                                    return;
                                }

                                if (clear) {
                                    adapter.patientPosts.clear();
                                    scrollListener.resetState();
                                }
                                PatientPostListQuery.Data data = response.data();
                                adapter.patientPostsCount = data.count;
                                adapter.patientPosts.addAll(data.patientPosts);
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_post, container, false);

        recyclerView = view.findViewById(R.id.patient_post_list);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        adapter = new PatientPostRecyclerCustomAdapter(getContext(), new  ArrayList<PatientPostListQuery.PatientPost>());
        adapter.setOnEntryClickListener(new PatientPostRecyclerCustomAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                Intent intent = new Intent(getActivity(), UnitViewActivity.class);
                intent.putExtra("idPatientPost", adapter.patientPosts.get(position).id);
                intent.putExtra("List", "PatientPost");
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        patientPostFilterBuilder = PatientPostFilter
                .builder()
                .limit(10);

        scrollListener = new EndlessRecycleViewScrollListener((LinearLayoutManager) layoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemCount, RecyclerView view) {

                if (adapter.patientPosts.size() < adapter.patientPostsCount) {
                    patientPostFilterBuilder.offset(page * 10);
                    load(false);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.patientPostPullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                patientPostFilterBuilder.offset(0);
                load(true);
                pullToRefresh.setRefreshing(false);
            }
        });

        int position = getArguments().getInt("position");

        if (position == 0) {
            patientPostFilterBuilder.sortBy("active");
        } else if (position == 1) {
            patientPostFilterBuilder.sortBy("last");
        } else if (position == 2) {
            patientPostFilterBuilder.sortBy("popular");
        } else if (position == 3) {
            patientPostFilterBuilder.sortBy("my");
        }
        load(false);

        return view;
    }

}

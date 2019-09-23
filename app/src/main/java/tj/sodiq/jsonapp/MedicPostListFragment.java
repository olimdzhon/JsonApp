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
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tj.sodiq.jsonapp.type.MedicPostFilter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MedicPostListFragment extends Fragment {

    private EndlessRecycleViewScrollListener scrollListener;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MedicPostRecyclerCustomAdapter adapter;
    MedicPostFilter.Builder medicPostFilterBuilder;

    public void load(final boolean clear) {

        MedicPostListQuery query = MedicPostListQuery
                .builder()
                .filter(medicPostFilterBuilder.build())
                .build();

        MyApolloClient
                .getMyApolloClient(getActivity())
                .query(query)
                .enqueue(new ApolloCall.Callback<MedicPostListQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<MedicPostListQuery.Data> response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.errors().size() > 0){
                                    Toast.makeText(getActivity(), "Ошибка загрузки MedicPostList", Toast.LENGTH_SHORT).show();
                                    Log.e( "Ошибка загрузки MedPoLi", response.errors().toString());
                                    return;
                                }

                                if (clear) {
                                    adapter.medicPosts.clear();
                                    scrollListener.resetState();
                                }
                                MedicPostListQuery.Data data = response.data();
                                adapter.medicPostsCount = data.count;
                                adapter.medicPosts.addAll(data.medicPosts);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("Apollo Query Failure", e.toString());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_medic_post, container, false);

        recyclerView = view.findViewById(R.id.medic_post_list);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        adapter = new MedicPostRecyclerCustomAdapter(getContext(), new ArrayList<MedicPostListQuery.MedicPost>());
        adapter.setOnEntryClickListener(new MedicPostRecyclerCustomAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                Intent intent = new Intent(getActivity(), UnitViewActivity.class);
                intent.putExtra("idMedicPost", adapter.medicPosts.get(position).id);
                intent.putExtra("List", "MedicPost");
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        medicPostFilterBuilder = MedicPostFilter
                .builder()
                .limit(10);

        scrollListener = new EndlessRecycleViewScrollListener((LinearLayoutManager) layoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                if (adapter.medicPosts.size() < adapter.medicPostsCount) {
                    medicPostFilterBuilder.offset(page * 10);
                    load(false);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.medicPostPullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                medicPostFilterBuilder.offset(0);
                load(true);
                pullToRefresh.setRefreshing(false);
            }
        });

        int position = getArguments().getInt("position");

        if (position == 0) {
            medicPostFilterBuilder.sortBy("active");
        } else if (position == 1) {
            medicPostFilterBuilder.sortBy("last");
        } else if (position == 2) {
            medicPostFilterBuilder.sortBy("popular");
        } else if (position == 3) {
            medicPostFilterBuilder.sortBy("my");
        }
        load(false);

        return view;
    }

}

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

import tj.sodiq.jsonapp.type.PaperFilterArgs;

public class PaperListFragment extends Fragment {

    private EndlessRecycleViewScrollListener scrollListener;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    PaperRecyclerCustomAdapter adapter;
    PaperFilterArgs.Builder paperFilterBuilder;

    public void load(final boolean clear) {

        PaperListQuery query = PaperListQuery
                .builder()
                .filter(paperFilterBuilder.build())
                .build();

        MyApolloClient
                .getMyApolloClient(getContext())
                .query(query)
                .enqueue(new ApolloCall.Callback<PaperListQuery.Data>() {

                    @Override
                    public void onResponse(@NotNull final com.apollographql.apollo.api.Response<PaperListQuery.Data> response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0){
                                    Toast.makeText(getActivity(), "Ошибка загрузки PaperList", Toast.LENGTH_SHORT).show();
                                    Log.e( "Ошибка загрузки PaperLi", response.errors().toString());
                                    return;
                                }

                                if (clear) {
                                    adapter.papers.clear();
                                    scrollListener.resetState();
                                }
                                PaperListQuery.Data data = response.data();
                                adapter.papersCount = data.count;
                                adapter.papers.addAll(data.papers);
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

        View view = inflater.inflate(R.layout.fragment_paper, container, false);

        recyclerView = view.findViewById(R.id.paper_list);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        adapter = new PaperRecyclerCustomAdapter(getContext(), new ArrayList<PaperListQuery.Paper>());
        adapter.setOnEntryClickListener(new PaperRecyclerCustomAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                Intent intent = new Intent(getActivity(), UnitViewActivity.class);
                intent.putExtra("idPaper", adapter.papers.get(position).id);
                intent.putExtra("List", "Paper");
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        paperFilterBuilder = PaperFilterArgs
                .builder()
                .limit(10);

        scrollListener = new EndlessRecycleViewScrollListener((LinearLayoutManager) layoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemCount, RecyclerView view) {

                if (adapter.papers.size() < adapter.papersCount) {
                   paperFilterBuilder.offset(page * 10);
                    load(false);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.paperPullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                paperFilterBuilder.offset(0);
                load(true);
                pullToRefresh.setRefreshing(false);
            }
        });

        int position = getArguments().getInt("position");

        if (position == 0) {
            paperFilterBuilder.sortBy("active");
        } else if (position == 1) {
            paperFilterBuilder.sortBy("last");
        } else if (position == 2) {
            paperFilterBuilder.sortBy("popular");
        } else if (position == 3) {
            paperFilterBuilder.sortBy("my");
        }
        load(false);

        return view;
    }
}

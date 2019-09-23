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

import tj.sodiq.jsonapp.type.ConciliumFilter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConciliumListFragment extends Fragment {

    private EndlessRecycleViewScrollListener scrollListener;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ConciliumRecyclerCustomAdapter adapter;
    ConciliumFilter.Builder conciliumFilterBuilder;

    public void load(final boolean clear){

        ConciliumListQuery query = ConciliumListQuery
                .builder()
                .filter(conciliumFilterBuilder.build())
                .build();

        MyApolloClient
                .getMyApolloClient(getActivity())
                .query(query)
                .enqueue(new ApolloCall.Callback<ConciliumListQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<ConciliumListQuery.Data> response) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.errors().size() > 0) {
                                    Toast.makeText(getActivity(), "Ошибка загрузки Concilium", Toast.LENGTH_SHORT).show();
                                    Log.e("Ошибка загрузки ConciLi", response.errors().toString());
                                    return;
                                }

                                if (clear){
                                    adapter.conciliums.clear();
                                    scrollListener.resetState();
                                }
                                ConciliumListQuery.Data data = response.data();
                                adapter.conciliumsCount = data.count;
                                adapter.conciliums.addAll(data.conciliums);
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

        View view = inflater.inflate(R.layout.fragment_concilium, container, false);

        recyclerView = view.findViewById(R.id.concilium_list);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        adapter = new ConciliumRecyclerCustomAdapter(getContext(), new ArrayList<ConciliumListQuery.Concilium>());
        adapter.setOnEntryClickListener(new ConciliumRecyclerCustomAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                Intent intent = new Intent(getActivity(), UnitViewActivity.class);
                intent.putExtra("idConcilium", adapter.conciliums.get(position).id);
                intent.putExtra("List", "Concilium");
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        conciliumFilterBuilder = ConciliumFilter
                .builder()
                .limit(10);

        scrollListener = new EndlessRecycleViewScrollListener((LinearLayoutManager)layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                if (adapter.conciliums.size() < adapter.conciliumsCount){
                    conciliumFilterBuilder.offset(page * 10);
                    load(false);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.conciliumPullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                conciliumFilterBuilder.offset(0);
                load(true);
                pullToRefresh.setRefreshing(false);
            }
        });

        int position = getArguments().getInt("position");

        if (position == 0){
            conciliumFilterBuilder.sortBy("active");
        }
        if (position == 1){
            conciliumFilterBuilder.sortBy("last");
        }
        if (position == 2){
            conciliumFilterBuilder.sortBy("popular");
        }
        if (position == 3){
            conciliumFilterBuilder.sortBy("my");
        }
        load(false);

        return view;
    }

}

package tj.sodiq.jsonapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FeedRecyclerCustomAdapter extends RecyclerView.Adapter<FeedRecyclerCustomAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image, imagePhoto;
        TextView title, text, heart, watch, date, author;
        Button btnDelete, btnEdit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            image = itemView.findViewById(R.id.iv_image);
            imagePhoto = itemView.findViewById(R.id.iv_photo);
            title = itemView.findViewById(R.id.tv_title);
            text = itemView.findViewById(R.id.tv_text);
            heart = itemView.findViewById(R.id.tv_heart);
            watch = itemView.findViewById(R.id.tv_eye);
            date = itemView.findViewById(R.id.tv_date);
            author = itemView.findViewById(R.id.tv_author);

            btnDelete = itemView.findViewById(R.id.button2);
            btnEdit = itemView.findViewById(R.id.button);
        }

        @Override
        public void onClick(View v) {
            if (onEntryClickListener != null) {
                onEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }

    public int activitiesCount;
    public List<ActivityNewsListQuery.Activity> activities;
    Context context;

    public FeedRecyclerCustomAdapter(Context context, @NonNull List<ActivityNewsListQuery.Activity> arrayList) {
        activities = arrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ActivityNewsListQuery.Activity activity = activities.get(position);

        String title = activity.unit.__typename();

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    private OnEntryClickListener onEntryClickListener;


    public interface OnEntryClickListener {
        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener){
        this.onEntryClickListener = onEntryClickListener;
    }
}

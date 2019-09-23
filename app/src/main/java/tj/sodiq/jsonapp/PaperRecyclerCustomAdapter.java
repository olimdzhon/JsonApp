package tj.sodiq.jsonapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaperRecyclerCustomAdapter extends RecyclerView.Adapter<PaperRecyclerCustomAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image, imagePhoto;
        TextView title, text, heart, watch, date, author;
        Button btnDelete, btnEdit;

        public MyViewHolder(@NonNull View itemView){
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
        public void onClick(View v){
            if (onEntryClickListener != null) {
                onEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }

    public int papersCount;
    public List<PaperListQuery.Paper> papers;
    Context context;

    public PaperRecyclerCustomAdapter(Context context, @NonNull List<PaperListQuery.Paper> arrayList) {
        papers = arrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return papers.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final PaperListQuery.Paper paper = papers.get(position);

        String title = paper.name;
        String text = paper.annotation;
        if (text.length() > 200) {
            text = text.substring(0, 200) + "...";
        }
        String heart = String.valueOf(paper.score);

        String data = paper.createdDate.toString();
        String author = paper.author.firstName + " " + paper.author.secondName;

        if (paper.author.photo != null) {
            String url1 = paper.author.photo.name;
            Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + url1 + "&type=attach-photo&w=920&h=280").into(holder.imagePhoto);
        } else {
            holder.imagePhoto.setImageResource(0);
        }

        holder.title.setText(title);
        holder.text.setText(text);
        holder.heart.setText(heart);
        holder.date.setText(data);
        holder.author.setText(author);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaperEditActivity.class);
                intent.putExtra("idPaper", paper.id);
                context.startActivity(intent);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaperDeleteMutation mutation = PaperDeleteMutation
                        .builder()
                        .id(paper.id)
                        .build();

                MyApolloClient.getMyApolloClient(context).mutate(mutation)
                        .enqueue(new ApolloCall.Callback<PaperDeleteMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<PaperDeleteMutation.Data> response) {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        papers.remove(paper);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        Toast.makeText(context, "Delete canceled", Toast.LENGTH_SHORT).show();
                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                                .setNegativeButton("No", dialogClickListener).show();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e("apollo delete mutation", e.toString());
                            }

                        });
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
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

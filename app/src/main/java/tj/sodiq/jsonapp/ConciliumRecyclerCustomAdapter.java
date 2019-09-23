package tj.sodiq.jsonapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

public class ConciliumRecyclerCustomAdapter extends RecyclerView.Adapter<ConciliumRecyclerCustomAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image, imagePhoto;
        TextView title, text, heart, watch, date, author;
        Button btnDelete, btnEdit;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

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

    public int conciliumsCount;
    public List<ConciliumListQuery.Concilium> conciliums;
    Context context;

    public ConciliumRecyclerCustomAdapter(Context context, @NonNull List<ConciliumListQuery.Concilium> arrayList){
        conciliums = arrayList;
        this.context = context;
    }

    @Override
    public int getItemCount(){
        return conciliums.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position){

        final ConciliumListQuery.Concilium concilium = conciliums.get(position);

        String title = concilium.name;
        String text = concilium.text;
        if (text.length() > 200){
            text = text.substring(0,200) + "...";
        }
        Integer heart = concilium.score;
        String data = concilium.createdDate.toString();
        String author = concilium.author.firstName + " " + concilium.author.secondName;

        if (concilium.author.photo != null) {
            String url1 = concilium.author.photo.name;
            Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + url1 + "&type=attach-photo&w=920&h=280").into(holder.imagePhoto);
        } else {
            holder.imagePhoto.setImageResource(0);
        }

        holder.title.setText(title);
        holder.text.setText(text);
        holder.heart.setText(heart.toString());
        holder.date.setText(data);
        holder.author.setText(author);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConciliumEditActivity.class);
                intent.putExtra("idConcilium", concilium.id);
                context.startActivity(intent);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConciliumDeleteMutation mutation = ConciliumDeleteMutation
                        .builder()
                        .id(concilium.id)
                        .build();

                MyApolloClient
                        .getMyApolloClient(context)
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ConciliumDeleteMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<ConciliumDeleteMutation.Data> response) {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        conciliums.remove(concilium);
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
                                Log.e("Concil Delete Mutation", e.toString());
                            }
                        });

            }

        });
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
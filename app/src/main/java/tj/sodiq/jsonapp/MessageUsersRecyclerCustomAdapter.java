package tj.sodiq.jsonapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageUsersRecyclerCustomAdapter extends RecyclerView.Adapter<MessageUsersRecyclerCustomAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imagePhoto;
        TextView name, text;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            imagePhoto = itemView.findViewById(R.id.imagePhotoMessageUsers);
            name = itemView.findViewById(R.id.nameMessageUsers);
            text = itemView.findViewById(R.id.textMessageUsers);
        }

        @Override
        public void onClick(View v){
            if (onEntryClickListener != null) {
                onEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }

    public int messageUsersCount;
    public List<MessageUsersQuery.MessageUser> messageUsers;
    Context context;

    public MessageUsersRecyclerCustomAdapter(Context context, @NonNull List<MessageUsersQuery.MessageUser> arrayList){
        messageUsers = arrayList;
        this.context = context;
    }

    @Override
    public int getItemCount(){
        return messageUsers.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_users_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position){

        final MessageUsersQuery.MessageUser message = messageUsers.get(position);

        String name = message.user.firstName + " " + message.user.secondName;
        String text = message.lastMessage.text;
        if (text.length() > 35){
            text = text.substring(0, 35) + "...";
        }

        if (message.user.photo != null){
            String url = message.user.photo.filename;
            Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + url + "&type=attach-photo&w=920&h=280").into(holder.imagePhoto);
        } else {
            holder.imagePhoto.setImageResource(0);
        }

        holder.name.setText(name);
        holder.text.setText(text);

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

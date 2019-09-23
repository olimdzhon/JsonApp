package tj.sodiq.jsonapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageRecyclerCustomAdapter extends RecyclerView.Adapter<MessageRecyclerCustomAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messageText, messageTime;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            messageTime = itemView.findViewById(R.id.messageTime);
        }

    }

    public int messagesCount;
    public List<MessageListQuery.Message> messages;
    Context context;

    public MessageRecyclerCustomAdapter(Context context, @NonNull List<MessageListQuery.Message> arrayList){
        messages = arrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MessageListQuery.Message message = messages.get(position);

        String text = message.text;
        String time = message.createdDate.toString();

        holder.messageText.setText(text);
        holder.messageTime.setText(time);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

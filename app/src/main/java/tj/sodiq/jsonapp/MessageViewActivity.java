package tj.sodiq.jsonapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tj.sodiq.jsonapp.type.MessageDraft;
import tj.sodiq.jsonapp.type.MessageFilter;

public class MessageViewActivity extends AppCompatActivity {

    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    private BroadcastReceiver broadcastReceiver;

    private EditText editMessage;
    private int id;
    private int ownerId;

    private RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    MessageRecyclerCustomAdapter adapter;

    public void load(final boolean clear){

        if (id != 0) {

        MessageFilter filter = MessageFilter
                .builder()
                .ownerId(id)
                .build();

        MessageListQuery query = MessageListQuery
                .builder()
                .filter(filter)
                .build();

        MyApolloClient
                .getMyApolloClient(this)
                .query(query)
                .enqueue(new ApolloCall.Callback<MessageListQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<MessageListQuery.Data> response) {

                        MessageViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(response.errors().size() > 0){
                                    Toast.makeText(MessageViewActivity.this, "Ошибка загрузки MessageList", Toast.LENGTH_SHORT).show();
                                    Log.e("MessageList", response.errors().toString());
                                    return;
                                }

                                if (clear){
                                    adapter.messages.clear();
                                }

                                MessageListQuery.Data data = response.data();
                                adapter.messagesCount = data.count;
                                adapter.messages.addAll(data.messages);
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);

        editMessage = findViewById(R.id.editMessage);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra("userName");

        actionBar.setTitle(title);

        SharedPreferences preferences = MessageViewActivity.this.getSharedPreferences(MessageViewActivity.this.getString(R.string.preference_file_key), MODE_PRIVATE);

        ownerId = preferences.getInt("userId", 0);

        id = getIntent().getIntExtra("idMessageUsers",0);

        if (id == 0){
            id = getIntent().getIntExtra("idSub", 0);
            Log.e("id", "onCreate: " + id);
        }

        recyclerView = findViewById(R.id.messageRec);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MessageRecyclerCustomAdapter(MessageViewActivity.this, new ArrayList<MessageListQuery.Message>());

        recyclerView.setAdapter(adapter);

        load(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_message_users_remove, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item){

        switch (item.getItemId()) {

            case R.id.action_remove:

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                MessageRemoveChatMutation mutation = MessageRemoveChatMutation
                                        .builder()
                                        .userId(id)
                                        .build();

                                MyApolloClient
                                        .getMyApolloClient(MessageViewActivity.this)
                                        .mutate(mutation)
                                        .enqueue(new ApolloCall.Callback<MessageRemoveChatMutation.Data>() {
                                            @Override
                                            public void onResponse(@NotNull final Response<MessageRemoveChatMutation.Data> response) {

                                                MessageViewActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (response.errors().size() > 0) {
                                                            Toast.makeText(MessageViewActivity.this, "Ошибка мутации", Toast.LENGTH_SHORT).show();
                                                            Log.e("Ошибка мутации", response.errors().toString());
                                                            return;
                                                        }

                                                        Toast.makeText(MessageViewActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                                        Log.e("Delete Chat", "Deleted successfully");
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onFailure(@NotNull ApolloException e) {
                                                Log.e("Apollo Mutation", "Failure");
                                            }
                                        });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(MessageViewActivity.this, "Delete canceled", Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MessageViewActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;

            case R.id.home:

                Intent intent = new Intent(MessageViewActivity.this, Main2Activity.class);
                intent.putExtra("From", "MessageViewActivity");
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void sendButtonClick(View view) {
        final String messageValue = editMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(messageValue)){

            MessageDraft messageDraft = MessageDraft
                    .builder()
                    .ownerId(ownerId)
                    .receiverId(id)
                    .text(messageValue)
                    .build();

            MessageSaveMutation mutation = MessageSaveMutation
                    .builder()
                    .message(messageDraft)
                    .build();

            MyApolloClient
                    .getMyApolloClient(this)
                    .mutate(mutation)
                    .enqueue(new ApolloCall.Callback<MessageSaveMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull final Response<MessageSaveMutation.Data> response) {

                            MessageViewActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (response.errors().size() > 0 ){
                                        Toast.makeText(MessageViewActivity.this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                                        Log.e("Ошибка сохранения", response.errors().toString());
                                        return;
                                    }

                                    Toast.makeText(MessageViewActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    Log.e("Mutation", "Added successfully");

                                    load(true);

                                    editMessage.setText("");

                                }
                            });

                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.e("Apollo Mutation", "Failure");
                        }
                    });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NOTIFY_ACTIVITY_ACTION)){
                load(true);
                }
            }
        };

        IntentFilter filter = new IntentFilter(NOTIFY_ACTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    public static final class RequestReceiver extends ResultReceiver{

        public RequestReceiver(){
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {



        }
    }
}

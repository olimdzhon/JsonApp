package tj.sodiq.jsonapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tj.sodiq.jsonapp.type.AttachFileDraft;
import tj.sodiq.jsonapp.type.ConciliumSaveArgs;

public class ConciliumEditActivity extends AppCompatActivity {

    private EditText title, text;
    private int id;

    ArrayList<Integer> tagIds = new ArrayList<>();
    ArrayList<AttachFileDraft> attachFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concilium_edit);

        Intent intent = getIntent();
        id = intent.getIntExtra("idConcilium", 0);
        Log.e("idConcilium", String.valueOf(id));
        title = findViewById(R.id.ed_titleConcilium);
        text = findViewById(R.id.ed_textConcilium);

        ConciliumQuery query = ConciliumQuery
                .builder()
                .id(id)
                .build();

        MyApolloClient
                .getMyApolloClient(this)
                .query(query)
                .enqueue(new ApolloCall.Callback<ConciliumQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<ConciliumQuery.Data> response) {

                        ConciliumEditActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0){
                                    Toast.makeText(ConciliumEditActivity.this, "Ошибка загрузки ConciliumQuery", Toast.LENGTH_SHORT).show();
                                    Log.e("Ошибка загрузки", "ConciliumQuery: " + response.errors().toString());
                                    return;
                                }

                                ConciliumQuery.Concilium concilium = response.data().concilium;

                                title.setText(concilium.name);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    text.setText(Html.fromHtml(concilium.text, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    text.setText(Html.fromHtml(concilium.text));
                                }

                                for (int i = 0; i < concilium.tags.size(); i++) {
                                    tagIds.add(concilium.tags.get(i).id);
                                }

                                for (int i = 0; i < concilium.attachFiles.size(); i++) {
                                    AttachFileDraft attachFileDraft = AttachFileDraft
                                            .builder()
                                            .id(concilium.attachFiles.get(i).id)
                                            .filename(concilium.attachFiles.get(i).filename)
                                            .fileType(concilium.attachFiles.get(i).fileType)
                                            .build();
                                    attachFiles.add(attachFileDraft);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("concilium edit", e.toString());
                    }
                });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("ConciliumEditActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds item to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_post_edit, menu);
        return true;
    }

    // Determines if Action bar item was selected. If true then do corresponding action.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_create:

                ConciliumSaveArgs concilium = ConciliumSaveArgs
                        .builder()
                        .id(id)
                        .name(title.getText().toString())
                        .text(text.getText().toString())
                        .tagIds(tagIds)
                        .attachFiles(attachFiles)
                        .build();

                ConciliumSaveMutation mutation = ConciliumSaveMutation
                        .builder()
                        .concilium(concilium)
                        .build();

                MyApolloClient.getMyApolloClient(this).mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ConciliumSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<ConciliumSaveMutation.Data> response) {
                                ConciliumEditActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response.errors().size() > 0){
                                            Toast.makeText(ConciliumEditActivity.this, "Ошибка сохранения MedicPost", Toast.LENGTH_SHORT).show();
                                            Log.e("Medic Post Mutation", response.errors().toString());
                                            return;
                                        }

                                        Toast.makeText(ConciliumEditActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e("apollo save mutation", "Concilium Mutation Failure" + e.toString());
                            }
                        });

                break;
            case R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

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
import tj.sodiq.jsonapp.type.MedicPostSaveArgs;

public class MedicPostEditActivity extends AppCompatActivity {

    private String coverFilename;
    private EditText title, text;
    private int id;

    ArrayList<Integer> tagIds = new ArrayList<>();
    ArrayList<AttachFileDraft> attachFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_post_edit);

        Intent intent = getIntent();
        id = intent.getIntExtra("idMedicPost", 0);
        Log.e("idMedicPost", String.valueOf(id));
        title = findViewById(R.id.ed_titleMedicPost);
        text = findViewById(R.id.ed_textMedicPost);

        MedicPostQuery query = MedicPostQuery
                .builder()
                .id(id)
                .build();

        MyApolloClient
                .getMyApolloClient(this)
                .query(query)
                .enqueue(new ApolloCall.Callback<MedicPostQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<MedicPostQuery.Data> response) {

                        MedicPostEditActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0) {
                                    Toast.makeText(MedicPostEditActivity.this, "Ошибка загрузки MedicPostQuery", Toast.LENGTH_SHORT).show();
                                    Log.e("Ошибка загрузки", "MedicPostQuery: " + response.errors().toString());
                                    return;
                                }

                                MedicPostQuery.MedicPost medicPost = response.data().medicPost;

                                title.setText(medicPost.name);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    text.setText(Html.fromHtml(medicPost.text, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    text.setText(Html.fromHtml(medicPost.text));
                                }

                                for (int i = 0; i < medicPost.tags.size(); i++) {
                                    tagIds.add(medicPost.tags.get(i).id);
                                }

                                if (medicPost.cover != null) {
                                    coverFilename = medicPost.cover.filename;
                                } else {
                                    coverFilename = null;
                                }

                                for (int i = 0; i < medicPost.attachFiles.size(); i++) {
                                    AttachFileDraft attachFileDraft = AttachFileDraft
                                            .builder()
                                            .id(medicPost.attachFiles.get(i).id)
                                            .filename(medicPost.attachFiles.get(i).filename)
                                            .fileType(medicPost.attachFiles.get(i).fileType)
                                            .build();
                                    attachFiles.add(attachFileDraft);
                                }

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("medicPost edit", e.toString());
                    }
                });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("MedicPostEditActivity");
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

                MedicPostSaveArgs medicPost = MedicPostSaveArgs
                        .builder()
                        .id(id)
                        .name(title.getText().toString())
                        .text(text.getText().toString())
                        .tagIds(tagIds)
                        .coverFilename(coverFilename)
                        .attachFiles(attachFiles)
                        .build();

                MedicPostSaveMutation mutation = MedicPostSaveMutation
                        .builder()
                        .medicPost(medicPost)
                        .build();

                MyApolloClient.getMyApolloClient(this).mutate(mutation)
                        .enqueue(new ApolloCall.Callback<MedicPostSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<MedicPostSaveMutation.Data> response) {
                                MedicPostEditActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response.errors().size() > 0){
                                            Toast.makeText(MedicPostEditActivity.this, "Ошибка сохранения MedicPost", Toast.LENGTH_SHORT).show();
                                            Log.e("Medic Post Mutation", response.errors().toString());
                                            return;
                                        }

                                        Toast.makeText(MedicPostEditActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e("apollo save mutation", "Medic Post Mutation Failure" + e.toString());
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

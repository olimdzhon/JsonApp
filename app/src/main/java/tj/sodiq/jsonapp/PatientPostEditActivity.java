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
import tj.sodiq.jsonapp.type.PatientPostSaveArgs;

public class PatientPostEditActivity extends AppCompatActivity {

    private String coverFilename;
    private EditText title, text;
    private int id;

    ArrayList<Integer> tagIds = new ArrayList<>();
    ArrayList<AttachFileDraft> attachFiles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_post_edit);

        Intent intent = getIntent();
        id = intent.getIntExtra("idPatientPost", 0);
        Log.e("idPatientPost", String.valueOf(id));
        title = findViewById(R.id.ed_titlePatientPost);
        text = findViewById(R.id.ed_textPatientPost);

        PatientPostQuery query = PatientPostQuery
                .builder()
                .id(id)
                .build();

        MyApolloClient
                .getMyApolloClient(this)
                .query(query)
                .enqueue(new ApolloCall.Callback<PatientPostQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<PatientPostQuery.Data> response) {

                        PatientPostEditActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0) {
                                    Toast.makeText(PatientPostEditActivity.this, "Ошибка загрузки PatientPost", Toast.LENGTH_SHORT).show();
                                    Log.e("Ошибка загрузки", "PatientPost: " + response.errors().toString());
                                    return;
                                }

                                PatientPostQuery.PatientPost patientPost = response.data().patientPost;

                                title.setText(patientPost.name);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    text.setText(Html.fromHtml(patientPost.text, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    text.setText(Html.fromHtml(patientPost.text));
                                }

                                for (int i = 0; i < patientPost.tags.size(); i++) {
                                    tagIds.add(patientPost.tags.get(i).id);
                                }

                                if (patientPost.cover != null) {
                                    coverFilename = patientPost.cover.filename;
                                } else {
                                    coverFilename = null;
                                }

                                for (int i = 0; i < patientPost.attachFiles.size(); i++) {
                                    AttachFileDraft attachFileDraft = AttachFileDraft
                                            .builder()
                                            .id(patientPost.attachFiles.get(i).id)
                                            .filename(patientPost.attachFiles.get(i).filename)
                                            .fileType(patientPost.attachFiles.get(i).fileType)
                                            .build();
                                    attachFiles.add(attachFileDraft);
                                }

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("patientPost edit", e.toString());
                    }
                });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PatientPostEditActivity");

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

                PatientPostSaveArgs patientPost = PatientPostSaveArgs
                        .builder()
                        .id(id)
                        .name(title.getText().toString())
                        .text(text.getText().toString())
                        .tagIds(tagIds)
                        .coverFilename(coverFilename)
                        .attachFiles(attachFiles)
                        .build();

                PatientPostSaveMutation mutation = PatientPostSaveMutation
                        .builder()
                        .patientPost(patientPost)
                        .build();

                MyApolloClient.getMyApolloClient(this).mutate(mutation)
                        .enqueue(new ApolloCall.Callback<PatientPostSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<PatientPostSaveMutation.Data> response) {
                                PatientPostEditActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PatientPostEditActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e("apollo save mutation", e.toString());
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

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
import tj.sodiq.jsonapp.type.PaperSaveArgs;
import tj.sodiq.jsonapp.type.ProfileSaveArgs;

public class PaperEditActivity extends AppCompatActivity {

    private EditText title, text;
    private int id;

    ArrayList<Integer> tagIds = new ArrayList<>();
    ArrayList<AttachFileDraft> attachFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_edit);

        Intent intent = getIntent();
        id = intent.getIntExtra("idPaper", 0);
        Log.e("idPaper", String.valueOf(id));
        title = findViewById(R.id.ed_titlePaper);
        text = findViewById(R.id.ed_textPaper);

        PaperQuery query = PaperQuery
                .builder()
                .id(id)
                .build();

        MyApolloClient
                .getMyApolloClient(this)
                .query(query)
                .enqueue(new ApolloCall.Callback<PaperQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<PaperQuery.Data> response) {

                        PaperEditActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.errors().size() > 0) {
                                    Toast.makeText(PaperEditActivity.this, "Ошибка загрузки PaperQuery", Toast.LENGTH_SHORT).show();
                                    Log.e("Ошибка загрузки", "PaperQuery: " + response.errors().toString());
                                    return;
                                }

                                PaperQuery.Paper paper = response.data().paper;

                                title.setText(paper.name);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    text.setText(Html.fromHtml(paper.annotation, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    text.setText(Html.fromHtml(paper.annotation));
                                }

                                for (int i = 0; i < paper.tags.size(); i++) {
                                    tagIds.add(paper.tags.get(i).id);
                                }

                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("paper edit", e.toString());
                    }
                });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PaperEditActivity");
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

                PaperSaveArgs paper = PaperSaveArgs
                        .builder()
                        .id(id)
                        .name(title.getText().toString())
                        .annotation(text.getText().toString())
                        .tagIds(tagIds)
                        .build();

                PaperSaveMutation mutation = PaperSaveMutation
                        .builder()
                        .paper(paper)
                        .build();

                MyApolloClient.getMyApolloClient(this).mutate(mutation)
                        .enqueue(new ApolloCall.Callback<PaperSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<PaperSaveMutation.Data> response) {
                                PaperEditActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response.errors().size() > 0){
                                            Toast.makeText(PaperEditActivity.this, "Ошибка сохранения MedicPost", Toast.LENGTH_SHORT).show();
                                            Log.e("Medic Post Mutation", response.errors().toString());
                                            return;
                                        }

                                        Toast.makeText(PaperEditActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e("apollo save mutation", "Paper Mutation Failure" + e.toString());
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

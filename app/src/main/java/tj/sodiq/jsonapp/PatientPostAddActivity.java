package tj.sodiq.jsonapp;


import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import tj.sodiq.jsonapp.type.PatientPostSaveArgs;

public class PatientPostAddActivity extends AppCompatActivity {

    private EditText title1, text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_post_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PatientPostAddActivity");


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
                title1 = findViewById(R.id.ed_title);
                text1 = findViewById(R.id.ed_text);

                PatientPostSaveArgs patientPost = PatientPostSaveArgs
                        .builder()
                        .name(title1.getText().toString())
                        .text(text1.getText().toString())
                        .tagIds(new ArrayList<Integer>())
                        .build();

                PatientPostSaveMutation mutation = PatientPostSaveMutation
                        .builder()
                        .patientPost(patientPost)
                        .build();

                MyApolloClient.getMyApolloClient(this).mutate(mutation)
                        .enqueue(new ApolloCall.Callback<PatientPostSaveMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<PatientPostSaveMutation.Data> response) {
                                PatientPostAddActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PatientPostAddActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e("apollo mutation", e.toString());
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

package tj.sodiq.jsonapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class UnitViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_view);

        final ImageView image = findViewById(R.id.iv_image_v);
        final TextView title = findViewById(R.id.tv_title_v);
        final TextView text = findViewById(R.id.tv_text_v);
        final TextView heart = findViewById(R.id.tv_heart_v);
        final TextView watch = findViewById(R.id.tv_eye_v);
        final TextView date = findViewById(R.id.tv_date_v);
        final TextView author = findViewById(R.id.tv_author_v);
        final ImageView imagePhoto = findViewById(R.id.iv_photo_v);

        Intent intent = getIntent();

        String list = intent.getStringExtra("List");

        if (list.equals("PatientPost")) {

            int id = intent.getIntExtra("idPatientPost", 0);

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

                            UnitViewActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    PatientPostQuery.Data data = response.data();

                                    if (data.patientPost.cover != null) {
                                        String vUrl = data.patientPost.cover.filename;
                                        Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + vUrl + "&type=attach-photo&w=920&h=280").into(image);
                                    } else {
                                        image.setImageResource(0);
                                    }

                                    title.setText(data.patientPost.name);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        text.setText(Html.fromHtml(data.patientPost.text, Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        text.setText(Html.fromHtml(data.patientPost.text));
                                    }

                                    heart.setText(String.valueOf(data.patientPost.score));

                                    watch.setText(String.valueOf(data.patientPost.watch));

                                    date.setText(data.patientPost.createdDate.toString());

                                    author.setText(data.patientPost.author.firstName + " " + data.patientPost.author.secondName);

                                    if (data.patientPost.author.photo != null) {
                                        String vUrl1 = data.patientPost.author.photo.name;
                                        Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + vUrl1 + "&type=attach-photo&w=920&h=280").into(imagePhoto);
                                    } else {
                                        imagePhoto.setImageResource(0);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.e("patientPost query", e.toString());
                        }
                    });

        } else if (list.equals("MedicPost")){

            int id = intent.getIntExtra("idMedicPost", 0);

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

                            UnitViewActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (response.errors().size() > 0){
                                        Toast.makeText(UnitViewActivity.this, "Ошибка загрузки MedicPost", Toast.LENGTH_SHORT).show();
                                        Log.e( "MedicPostQuery", response.errors().toString());
                                        return;
                                    }

                                    MedicPostQuery.MedicPost medicPost = response.data().medicPost;

                                    if (medicPost.cover != null) {
                                        String vUrl = medicPost.cover.filename;
                                        Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + vUrl + "&type=attach-photo&w=920&h=280").into(image);
                                    } else {
                                        image.setImageResource(0);
                                    }

                                    title.setText(medicPost.name);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        text.setText(Html.fromHtml(medicPost.text, Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        text.setText(Html.fromHtml(medicPost.text));
                                    }

                                    heart.setText(String.valueOf(medicPost.score));

                                    watch.setText(String.valueOf(medicPost.watch));

                                    date.setText(medicPost.createdDate.toString());

                                    author.setText(medicPost.author.firstName + " " + medicPost.author.secondName);

                                    if (medicPost.author.photo != null) {
                                        String vUrl1 = medicPost.author.photo.name;
                                        Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + vUrl1 + "&type=attach-photo&w=920&h=280").into(imagePhoto);
                                    } else {
                                        imagePhoto.setImageResource(0);
                                    }

                                }
                            });

                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.e("medicPostQuery", e.toString());
                        }
                    });

        } else if (list.equals("Concilium")){

            int id = intent.getIntExtra("idConcilium", 0);

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

                            UnitViewActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (response.errors().size() > 0){
                                        Toast.makeText(UnitViewActivity.this, "Ошибка загрузки Concilium", Toast.LENGTH_SHORT).show();
                                        Log.e("Concilium Query", response.errors().toString());
                                        return;
                                    }

                                    ConciliumQuery.Concilium concilium = response.data().concilium;

                                    image.setImageResource(0);

                                    title.setText(concilium.name);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        text.setText(Html.fromHtml(concilium.text, Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        text.setText(Html.fromHtml(concilium.text));
                                    }

                                    heart.setText(String.valueOf(concilium.score));

                                    watch.setText(String.valueOf(concilium.watch));

                                    date.setText(concilium.createdDate.toString());

                                    author.setText(concilium.author.firstName + " " + concilium.author.secondName);

                                    if (concilium.author.photo != null){
                                        String vUrl1 = concilium.author.photo.name;
                                        Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + vUrl1 + "&type=attach-photo&w=920&h=280").into(imagePhoto);
                                    } else {
                                        imagePhoto.setImageResource(0);
                                    }
                                }
                            });

                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.e("conciliumQuery", e.toString());
                        }
                    });

        } else if (list.equals("Paper")) {

            int id = intent.getIntExtra("idPaper",0);

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

                            UnitViewActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (response.errors().size() > 0){
                                        Toast.makeText(UnitViewActivity.this, "Ошибка загрузки Paper", Toast.LENGTH_SHORT).show();
                                        Log.e("Paper Query", response.errors().toString());
                                        return;
                                    }

                                    PaperQuery.Paper paper = response.data().paper;

                                    image.setImageResource(0);

                                    title.setText(paper.name);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        text.setText(Html.fromHtml(paper.annotation, Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        text.setText(Html.fromHtml(paper.annotation));
                                    }

                                    heart.setText(String.valueOf(paper.score));

                                    watch.setText(String.valueOf(paper.watch));

                                    date.setText(paper.createdDate.toString());

                                    author.setText(paper.author.firstName + " " + paper.author.secondName);

                                    if (paper.author.photo != null){
                                        String vUrl1 = paper.author.photo.name;
                                        Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + vUrl1 + "&type=attach-photo&w=920&h=280").into(imagePhoto);
                                    } else {
                                        imagePhoto.setImageResource(0);
                                    }

                                }
                            });
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            Log.e("paperQuery", e.toString());
                        }
                    });
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.home:

                Intent intent = new Intent(UnitViewActivity.this, Main2Activity.class);
                startActivity(intent);
                finish();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }



}

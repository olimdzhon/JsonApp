package tj.sodiq.jsonapp.dialogs;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;

import tj.sodiq.jsonapp.R;
import tj.sodiq.jsonapp.UploadService;

import static android.app.Activity.RESULT_OK;

public class UserPhotoDialog extends DialogFragment {

    private static final String TAG = "UserPhotoDialog";

    //widgets
    private static final int RESULT_LOAD_IMAGE = 1;
    private TextView image;
    private static int id;
    private ImageView imageView;
    private RelativeLayout rlayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_user_photo, container, false);
        image = view.findViewById(R.id.ivPhImage);
        imageView = view.findViewById(R.id.ivPhotoView);

        SharedPreferences preferences = getActivity().getSharedPreferences(getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final int profileId = preferences.getInt("userId", 0);

        getDialog().setTitle("Изменить изображение профиля");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: uploading photo");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CropImage.activity()
                                .start(getContext(), UserPhotoDialog.this);
                    }
                });

                ImageView imageView1 = new ImageView(getContext());
                rlayout = view.findViewById(R.id.user_photo);

                imageView1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                rlayout.addView(imageView1);

            }

        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("data", "data: " + data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.e("result", "result: " + result);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                Log.e("resultUri", "resultUri: " + resultUri);

                imageView.setImageURI(resultUri);

                Intent intent = new Intent(getContext(), UploadService.class);
                intent.putExtra("resultUri", resultUri.toString());
                getContext().startService(intent);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("error", "error: " + error);
            }
        }

    }
}



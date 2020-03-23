package auto.app;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import auto.app.model.Advertisment;

import static android.app.Activity.RESULT_OK;

public class AddAdvertisementDialog extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBehavior;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private View view;
    private ImageView carImageView;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private TextInputLayout price;
    private TextInputLayout description;
    private TextInputLayout title;
    private ProgressDialog pd;
    private StorageTask mTask;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        view = View.inflate(getContext(), R.layout.add_advertisement_dialog, null);
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mStorageRef = FirebaseStorage.getInstance().getReference("cars_pictures");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("cars_advertisement");

        carImageView = view.findViewById(R.id.image_view_car);
        carImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        price = view.findViewById(R.id.textInputPrice);
        title = view.findViewById(R.id.textInputName);
        description = view.findViewById(R.id.textInputDescription);
        Button save_button = view.findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePrice() | !validateDescription() | !validateTitle())
                    return;
                if (imageUri == null) {
                    Toast.makeText(view.getContext(), "Необходимо выбрать фотографию", Toast.LENGTH_LONG).show();
                    return;
                }

                if (mTask == null || !mTask.isInProgress()) {
                    Bundle bundle = getArguments();
                    pd = new ProgressDialog(dialog.getContext());
                    pd.setMessage("Пожалуйста, подождите . . .");
//                    pd.setCancelable(false);
//                    pd.show();
                    uploadData();

                }
            }
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).centerCrop().fit().into(carImageView);
        }
    }

    private boolean validateTitle() {
        String titleS = Objects.requireNonNull(title.getEditText()).getText().toString().trim();

        if (titleS.isEmpty()) {
            title.setError("Поле не может быть пустым");
            return false;
        } else {
            title.setError(null);
            return true;
        }
    }

    private boolean validatePrice() {
        String priceS = Objects.requireNonNull(price.getEditText()).getText().toString().trim();

        if (priceS.isEmpty()) {
            price.setError("Поле не может быть пустым");
            return false;
        } else {
            price.setError(null);
            return true;
        }
    }

    private boolean validateDescription() {
        String decriptionS = Objects.requireNonNull(description.getEditText()).getText().toString().trim();

        if (decriptionS.isEmpty()) {
            description.setError("Поле не может быть пустым");
            return false;
        } else {
            description.setError(null);
            return true;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = view.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadData() {
        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        mTask = fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) {
                        }
                        String downloadUrl = Objects.requireNonNull(urlTask.getResult()).toString();
                        Advertisment advertisment = new Advertisment(Objects.requireNonNull(title.getEditText()).getText().toString(), Objects.requireNonNull(price.getEditText()).getText().toString(), Objects.requireNonNull(description.getEditText()).getText().toString(), downloadUrl);
                        String advId = mDatabaseRef.push().getKey();
                        if (advId != null) {
                            mDatabaseRef.child(advId).setValue(advertisment);
                            Toast.makeText(view.getContext(), "Объявление добавлено", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else
                            Toast.makeText(view.getContext(), "Null Adv ID", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

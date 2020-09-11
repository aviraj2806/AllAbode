package com.mp2.allabode.dialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mp2.allabode.R;
import com.mp2.allabode.activity.AuthActivity;
import com.mp2.allabode.activity.LHomeActivity;
import com.mp2.allabode.databse.FlatEntity;
import com.mp2.allabode.util.GoogleMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class LFirstDialog extends DialogFragment {

    public LFirstDialog(FirstFlatEntry firstFlatEntry) {
        this.firstFlatEntry = firstFlatEntry;
    }

    private TextView txtBack,txtLoc,txtSave;
    private FirstFlatEntry firstFlatEntry;
    private EditText etFlat,etRent;
    private ImageView imgFirst;
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private String pictureFilePath;
    private String deviceIdentifier;
    private String uploadedImage = "";
    private SharedPreferences sharedPreferences;
    String flat = "";
    private Bundle addBundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_l_first_dialog, container, false);

        txtBack = view.findViewById(R.id.txtLFirstBack);
        txtLoc = view.findViewById(R.id.txtLFirstLoc);
        txtSave = view.findViewById(R.id.txtLFirstSave);
        etFlat = view.findViewById(R.id.etLFirstFlat);
        etRent = view.findViewById(R.id.etLFirstRent);
        imgFirst = view.findViewById(R.id.imgLFirst);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        imgFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditTextFocus();
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_PICTURE_CAPTURE);
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean("isFirst",false)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setCancelable(false);
                    dialog.setMessage("Are you sure?\nThis will result in Log Out.");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), AuthActivity.class);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            getActivity().finish();
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.create();
                    dialog.show();
                }else{
                    dismiss();
                }
            }
        });

        txtLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flat = etFlat.getText().toString().trim();
                if (flat.isEmpty()) {
                    makeErrorToast("Enter Building Name and Flat No.", etFlat, "Enter Building Name and Flat No.");
                } else {
                    Intent intent = new Intent(getActivity(), GoogleMap.class);
                    startActivityForResult(intent, 101);
                }
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String rent = etRent.getText().toString().trim();
                String add = txtLoc.getText().toString().trim();
                clearEditTextFocus();

                if(uploadedImage.equals("")){
                    makeErrorToast("Upload Image",null,"");
                    imgFirst.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else if(add.equals("Select Location")){
                    makeErrorToast("Select Location",null,"");
                    txtLoc.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else if(rent.isEmpty()){
                    makeErrorToast("Enter Rent",etRent,"Enter Rent");
                }else{
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyYY-MM-dd hh:mm:ss");
                    String timeStamp = simpleDateFormat.format(calendar.getTime());

                    FlatEntity flatEntity = new FlatEntity(timeStamp,uploadedImage,rent,add,addBundle.getString("city"),sharedPreferences.getString("mobile",""));
                    firstFlatEntry.onInsertFirstFlat(flatEntity);
                    sharedPreferences.edit().putBoolean("isFirst",false).apply();
                    dismiss();
                }
            }
        });

        return view;
    }

    private void clearEditTextFocus() {
        etRent.clearFocus();
        etFlat.clearFocus();
    }

    public interface FirstFlatEntry{
        void onInsertFirstFlat(FlatEntity flatEntity);
    }

    public void makeErrorToast(String text, EditText editText, String hint) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast, null);
        Toast toast = new Toast(getActivity());
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        if (editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
            editText.clearFocus();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pictureFilePath = cursor.getString(columnIndex);
            cursor.close();
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                imgFirst.setBackground(null);
                imgFirst.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgFirst.setImageURI(Uri.fromFile(imgFile));
                addToCloudStorage(saveBitmapToFile(imgFile));
            }
        }
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                clearEditTextFocus();
                addBundle = data.getBundleExtra("result");
                txtLoc.setText(flat + "," + data.getBundleExtra("result").getString("area"));
                txtLoc.setTextColor(getResources().getColor(R.color.dark));
            }
        }
    }

    private void addToCloudStorage(File f) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();
        Uri picUri = Uri.fromFile(f);
        final String cloudFilePath = deviceIdentifier + picUri.getLastPathSegment();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageRef = firebaseStorage.getReference();
        final StorageReference uploadeRef = storageRef.child(cloudFilePath);

        uploadeRef.putFile(picUri).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                makeErrorToast("Image Upload Failed.", null, "");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        makeErrorToast("Image uploaded.", null, "");
                        progressDialog.dismiss();
                        uploadedImage = uri.toString();
                    }
                });
            }
        });
    }

    public File saveBitmapToFile(File file) {
        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            final int REQUIRED_SIZE = 75;

            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
}

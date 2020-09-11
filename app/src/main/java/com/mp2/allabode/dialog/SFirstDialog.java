package com.mp2.allabode.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.allabode.R;
import com.mp2.allabode.activity.AuthActivity;

import java.util.Objects;


public class SFirstDialog extends DialogFragment {

    public SFirstDialog(StudentFirstDetails studentFirstDetails) {
        this.studentFirstDetails = studentFirstDetails;
    }

    private StudentFirstDetails studentFirstDetails;
    private TextView txtBack,txtSave;
    private EditText etAge,etUni;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_first_dialog, container, false);

        txtBack = view.findViewById(R.id.txtSFirstBack);
        txtSave = view.findViewById(R.id.txtSFirstSave);
        etAge = view.findViewById(R.id.etSFirstAge);
        etUni = view.findViewById(R.id.etSFirstUni);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uni = etUni.getText().toString().trim();
                String age = etAge.getText().toString().trim();

                if(uni.isEmpty()){
                    makeErrorToast("Enter University Name",etUni,"Enter University Name");
                }else if(age.isEmpty()){
                    makeErrorToast("Enter Age",etAge,"Enter Age");
                }else{
                    studentFirstDetails.onUpdateStudent(age,uni);
                    dismiss();
                }

            }
        });

        return view;
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

    public interface StudentFirstDetails{
        void onUpdateStudent(String age, String uni);
    }
}

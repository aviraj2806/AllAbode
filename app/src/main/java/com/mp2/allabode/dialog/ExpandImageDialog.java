package com.mp2.allabode.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mp2.allabode.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class ExpandImageDialog extends DialogFragment {

    public ExpandImageDialog(String url) {
        this.url = url;
    }

    private ImageView imgExpand;
    private ProgressBar progressBar;
    private String url;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            int w = ViewGroup.LayoutParams.MATCH_PARENT;
            int h = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(w,h);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.notify_back));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expand_image_dialog, container, false);

        imgExpand = view.findViewById(R.id.imgExpand);
        progressBar = view.findViewById(R.id.proExpand);

        imgExpand.setVisibility(View.GONE);

        Picasso.get().load(url).into(imgExpand, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                imgExpand.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                dismiss();
            }
        });

        return view;
    }
}

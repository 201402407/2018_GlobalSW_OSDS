package com.example.administrator.huha.jaehun;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.administrator.huha.R;

public class CustomAnimationDialog extends Dialog {

    private Context c;
    private ImageView imgLogo;
    public CustomAnimationDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        c=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        imgLogo = (ImageView) findViewById(R.id.loading_Test);
        Animation anim = AnimationUtils.loadAnimation(c, R.anim.loading);
        imgLogo.setAnimation(anim);
    }
    @Override
    public void show() {
        super.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
    }
}

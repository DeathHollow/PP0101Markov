package com.example.pp0101markov;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

public class AuthDialog {
    private Dialog dialog;
    private Context context;

    public AuthDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_register);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        Button loginButton = dialog.findViewById(R.id.btn_log_in);
        Button registerButton = dialog.findViewById(R.id.btn_create_account);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, SignUpActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });
    }
    public void show() {
        dialog.show();
    }
}


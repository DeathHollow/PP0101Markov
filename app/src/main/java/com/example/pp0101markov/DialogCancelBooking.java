package com.example.pp0101markov;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogCancelBooking {
    public interface OnConfirmCancelListener {
        void onConfirm();
    }

    public static void show(Context context, OnConfirmCancelListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_cancel_booking);
        dialog.setCancelable(true);

        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView tvText = dialog.findViewById(R.id.tvText);

        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) listener.onConfirm();
        });

        dialog.show();
    }
}
package com.example.firebasenotificationactivity;

import android.content.Context;
import android.widget.Toast;

public class Utilities {
    public static void showToast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }
}

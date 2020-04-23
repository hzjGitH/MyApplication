package com.example.myapplication.Bean;

import android.graphics.Bitmap;
import android.net.Uri;




public class Picture {
    private Bitmap bitmap;
    private Uri uri;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}

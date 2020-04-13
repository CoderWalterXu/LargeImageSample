package com.xlh.study.largeimagesample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.xlh.study.largeimagelibrary.LargeView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    LargeView mLargeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLargeView = findViewById(R.id.largeView);

        InputStream is = null;
        try {
            is = getAssets().open("2.jpeg");
            mLargeView.setImage(is);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

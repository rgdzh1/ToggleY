package com.yey.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.yey.library_tby.OnClick;
import com.yey.library_tby.ToggleColorY;
import com.yey.library_tby.ToggleImageY;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ToggleColorY) findViewById(R.id.ytb_color)).setOnClick(new OnClick() {
            @Override
            public void click(boolean isOpen) {
                Log.e("开关状态", String.valueOf(isOpen));
            }
        });

        ((ToggleImageY) findViewById(R.id.ytb_image)).setOnClick(new OnClick() {
            @Override
            public void click(boolean isOpen) {
                Log.e("开关状态", String.valueOf(isOpen));
            }
        });
    }
}

package com.speedata.camerapreviewtest.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.speedata.camerapreviewtest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuyan  条码边框
 */
public class CircleActivity extends AppCompatActivity {

    private BarcodeDrawView drawView;
    private List<BarcodeBounds> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        initView();
    }

    private void initView() {
        mList = new ArrayList<>();

    }

}

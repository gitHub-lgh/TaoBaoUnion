package com.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taobaounion.R;
import com.ui.custom.TextFlowLayout;
import com.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity implements TextFlowLayout.OnFlowTextItemClickListener {

    @BindView(R.id.text_flow_layout)
    public TextFlowLayout flowLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        ButterKnife.bind(this);

        List<String> textList = new ArrayList<>();
        textList.add("按实际发客户");
        textList.add("按实客户");
        textList.add("按实际发客户");
        textList.add("按实客户");
        textList.add("按实际发客户");
        textList.add("际发客户");
        textList.add("按实际发客户");
        textList.add("按实际发123客户");
        textList.add("按实客户");
        textList.add("按实际发123客户");
        textList.add("按实客户");
        textList.add("按实际户");
        textList.add("按实客");
        textList.add("际户");
        textList.add("按实际发客户");
        textList.add("按实客户");
        textList.add("按实际发客户");
        textList.add("按实客户");
        flowLayout.setTextList(textList);
        flowLayout.setOnFlowTextItemClickListener(this);
    }

    @Override
    public void onFlowItemClick(String text) {
        LogUtils.d(this,"click text ----> " + text);
    }
}

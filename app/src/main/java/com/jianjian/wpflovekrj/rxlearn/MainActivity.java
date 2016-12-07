package com.jianjian.wpflovekrj.rxlearn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_tm)
    Button mBtnTm;
    @BindView(R.id.cb_tm)
    CheckBox mCbTm;
    @BindView(R.id.et_input)
    EditText mEtInput;
    @BindView(R.id.lv_list)
    ListView mLvList;

    private String[] allName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        allName = getResources().getStringArray(R.array.languages);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1);
        mLvList.setAdapter(adapter);

        //防抖动
        RxView.clicks(mBtnTm)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(aVoid -> Toast.makeText(MainActivity.this, "点击了按钮", Toast.LENGTH_SHORT).show());

        RxCompoundButton.checkedChanges(mCbTm)
                .subscribe(aBoolean -> {
                    mBtnTm.setEnabled(aBoolean);
                    mBtnTm.setBackgroundResource(aBoolean ? R.color.colorPrimary : R.color.colorPrimaryDark);
                });

        RxTextView.textChanges(mEtInput)
                .debounce(600,TimeUnit.MILLISECONDS)
                .map(charSequence -> charSequence.toString())
                .observeOn(Schedulers.io())
                .map(key -> {
                    List<String> dataList = new ArrayList<>();
                    if (!TextUtils.isEmpty(key)) {
                        for(String each : allName) {
                            if (each != null) {
                                if (each.contains(key)) {
                                    dataList.add(each);
                                }
                            }
                        }
                    }
                    return dataList;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                        adapter.clear();
                        adapter.addAll(strings);
                        adapter.notifyDataSetChanged();
                });
    }
}

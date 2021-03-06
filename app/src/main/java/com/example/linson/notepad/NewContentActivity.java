package com.example.linson.notepad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.linson.notepad.domain.ContentBean;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewContentActivity extends AppCompatActivity {
    public final static String DATE_STYLE = "yyyy年MM月dd日 HH时mm分ss秒";
    private static final String TAG = "log";
    @ViewInject(R.id.et_title)
    EditText et_title;
    @ViewInject(R.id.et_content)
    EditText et_content;
    @ViewInject(R.id.btn_save)
    Button btn_save;
    @ViewInject(R.id.tv_update)
    TextView tv_update;
    @ViewInject(R.id.ll_content)
    LinearLayout ll_content;
    private boolean isNew = true;
    private ContentBean mBean;
    private KeyListener mKeyListener_content;
    private KeyListener mKeyListener_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_content);
        ViewUtils.inject(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBean = (ContentBean) bundle.get("bean");
            if (mBean != null) {
                isNew = false;
                et_title.setText(mBean.getTitle());
                et_content.setText(mBean.getContent());
                String strUpdate = "上次修改：" + mBean.getUpdate();
                tv_update.setText(strUpdate);
            } else {
                isNew = true;
            }
        }
        initUI();
    }
    private void initUI() {
        mKeyListener_content = et_content.getKeyListener();
        mKeyListener_title = et_title.getKeyListener();
        et_content.setKeyListener(null);
        et_title.setKeyListener(null);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNew) {
                    saveContent();
                    setResult(101);
                } else {
                    updateContent();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", mBean);
                    intent.putExtras(bundle);
                    setResult(102, intent);
                }
                finish();
            }
        });
        et_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                et_content.setKeyListener(mKeyListener_content);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_content, InputMethodManager.SHOW_FORCED);
                et_content.setSelection(et_content.length());
                et_content.requestFocus();
                return true;
            }
        });
        et_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                et_title.setKeyListener(mKeyListener_title);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_title, InputMethodManager.SHOW_FORCED);
                et_title.setSelection(et_title.length());
                et_title.requestFocus();
                return true;
            }
        });
    }

    private void updateContent() {
        Log.i(TAG, "更新 id " + mBean.getId());
        ContentBean bean = DataSupport.find(ContentBean.class, mBean.getId());
        bean.setTitle(et_title.getText().toString());
        bean.setContent(et_content.getText().toString());
        bean.setUpdate(new SimpleDateFormat(DATE_STYLE).format(new Date()));
        boolean saved = bean.isSaved();
        Log.i(TAG, "save: " + saved);
        bean.save();
        mBean = bean;
    }

    private void saveContent() {
        //插入数据库
        Log.i(TAG, "新增内容：" + et_content.getText().toString());
        ContentBean bean = new ContentBean();
        bean.setTitle(et_title.getText().toString());
        bean.setContent(et_content.getText().toString());
        bean.setUpdate(new SimpleDateFormat(DATE_STYLE).format(new Date()));
        bean.save();
    }
}

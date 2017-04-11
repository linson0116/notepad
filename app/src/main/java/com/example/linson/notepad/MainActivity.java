package com.example.linson.notepad;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linson.notepad.domain.ContentBean;
import com.example.linson.notepad.domain.SnackbarUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "log";
    @ViewInject(R.id.btn_add)
    FloatingActionButton btn_add;
    @ViewInject(R.id.recyclerview)
    RecyclerView recyclerView;
    @ViewInject(R.id.coordinator_layout)
    CoordinatorLayout coordinator_layout;
    private List<ContentBean> mListData = new ArrayList<>();
    private MyAdapter myAdapter;
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    //上传成功
                    DataSupport.deleteAll(ContentBean.class);
                    List<ContentBean> list = (List<ContentBean>) msg.obj;
                    for (ContentBean bean : list) {
                        bean.save();
                    }
                    Log.i(TAG, "handleMessage: " + list);
                    mListData = DataSupport.order("update desc").find(ContentBean.class);
                    myAdapter.notifyDataSetChanged();
                    SnackbarUtils.showOK(coordinator_layout, "下载备份数据成功，并刷新数据库");
                    break;
                case 101:
                    //下载失败
                    SnackbarUtils.showFail(coordinator_layout, "下载数据失败");
            }
        }
    };



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.lidroid.xutils.ViewUtils.inject(this);
        mListData = initData();
        initUI();
        String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE};
        ActivityCompat.requestPermissions(MainActivity.this, permissions, 100);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int count = 0;
        switch (requestCode) {
            case 100:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, permissions[i] + " 已授权", Toast.LENGTH_SHORT).show();
                        count++;
                    } else {
                        Toast.makeText(this, permissions[i] + " 未授权", Toast.LENGTH_SHORT).show();
                    }
                }
                if (count == permissions.length) {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String line1Number = tm.getLine1Number();
                    String simSerialNumber = tm.getSimSerialNumber();
                    Log.i(TAG, "onCreate: " + line1Number + " " + simSerialNumber);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myAdapter = new MyAdapter(mListData);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewContentActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void uploadInfo() {
        backupToXml();
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetUtils.upFile(MainActivity.this, ConstantUtils.FILE_NAME, ConstantUtils.DIR_NAME, new RequestCallBack() {
                    @Override
                    public void onSuccess(ResponseInfo responseInfo) {
                        Log.i(TAG, "onSuccess: 上传数据成功");
                        SnackbarUtils.showOK(coordinator_layout, "上传数据成功");
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Log.i(TAG, "onSuccess: 上传数据失败");
                        SnackbarUtils.showFail(coordinator_layout, "上传数据失败");
                    }
                });
            }
        }).start();
    }

    public void downloadInfo() {
        String down_file_path = getFilesDir().getAbsolutePath() + File.separator + ConstantUtils.DOWN_FILE_NAME;
        NetUtils.downFile(mHandler, ConstantUtils.SERVER_DOWN_FILE_URL, down_file_path, ConstantUtils.FILE_NAME, ConstantUtils.DIR_NAME);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_up_file:
                Log.i(TAG, "onOptionsItemSelected: 点击了上传按钮");
                DialogUtils.show(
                        MainActivity.this,
                        "是否要上传数据到服务器？",
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadInfo();
                            }
                        },
                        "Cancel",
                        null);
                break;
            case R.id.menu_down_file:
                Log.i(TAG, "onOptionsItemSelected: 点击了下载按钮");
                DialogUtils.show(
                        MainActivity.this,
                        "是否要下载数据到本机？",
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadInfo();
                            }
                        },
                        "Cancel",
                        null);
                break;
        }
        return true;
    }

    private void backupToXml() {
        List<ContentBean> list = DataSupport.findAll(ContentBean.class);
        File filesDir = getFilesDir();
        try {
            FileOutputStream fos = new FileOutputStream(new File(filesDir, ConstantUtils.FILE_NAME));
            XmlUtils.saveToXml(list, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101) {
            mListData.add(0, DataSupport.findLast(ContentBean.class));
            myAdapter.notifyDataSetChanged();
        } else if (requestCode == 100 && resultCode == 102) {
            //更新后的bean
            Bundle bundle = data.getExtras();
            ContentBean bean = (ContentBean) bundle.get("bean");
            //查找数据源中的信息并更新
            for (int i = 0; i < mListData.size(); i++) {
                ContentBean temp = mListData.get(i);
                if (temp.getId() == bean.getId()) {
                    //更新数据
                    temp.setContent(bean.getContent());
                    temp.setTitle(bean.getTitle());
                    temp.setUpdate(bean.getUpdate());
                    //刷新UI
                    myAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    private List initData() {
        mListData = DataSupport.order("update desc").find(ContentBean.class);
        return mListData;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        List<ContentBean> list = null;

        public MyAdapter(List<ContentBean> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentBean bean = mListData.get(viewHolder.getAdapterPosition());
                    Intent intent = new Intent(getApplicationContext(), NewContentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", bean);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 100);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.i(TAG, "onLongClick: 长按" + viewHolder.getAdapterPosition());
                    DialogUtils.show(MainActivity.this, "删除这条记录吗？", "是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //数据源移除
                            ContentBean bean = mListData.remove(viewHolder.getAdapterPosition());
                            myAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            //数据库删除
                            DataSupport.delete(ContentBean.class, bean.getId());
                        }
                    }, "否", null);
                    return true;
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ContentBean bean = mListData.get(position);
            holder.tv_content.setText(bean.getContent());
            if (!TextUtils.isEmpty(bean.getTitle())) {
                holder.tv_title.setVisibility(View.VISIBLE);
                holder.tv_title.setText(bean.getTitle());
                holder.tv_content.setMaxLines(3);
            } else {
                holder.tv_title.setVisibility(View.GONE);
                holder.tv_content.setMaxLines(4);
            }
        }

        @Override
        public int getItemCount() {
            return mListData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_content;
            TextView tv_title;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_content = (TextView) itemView.findViewById(R.id.tv_content);
                tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            }
        }
    }
}

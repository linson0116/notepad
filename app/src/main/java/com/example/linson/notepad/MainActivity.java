package com.example.linson.notepad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.linson.notepad.domain.ContentBean;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    @ViewInject(R.id.btn_add)
    Button btn_add;
//    @ViewInject(R.id.btn_delete)
//    Button btn_delete;
    @ViewInject(R.id.recyclerview)
    RecyclerView recyclerView;
    private List<ContentBean> mListData = new ArrayList<>();
    private MyAdapter myAdapter;
    private static final String TAG = "log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.lidroid.xutils.ViewUtils.inject(this);
        mListData = initData();
        initUI();
    }

    private void initUI() {
        myAdapter = new MyAdapter(mListData);
        recyclerView.setAdapter(myAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewContentActivity.class);
                startActivityForResult(intent,100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101) {
            mListData.add(0,DataSupport.findLast(ContentBean.class));
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
        SQLiteDatabase database = LitePal.getDatabase();
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
                    bundle.putSerializable("bean",bean);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,100);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.i(TAG, "onLongClick: 长按" + viewHolder.getAdapterPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("删除？");
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //数据源移除
                            ContentBean bean = mListData.remove(viewHolder.getAdapterPosition());
                            myAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            //数据库删除
                            DataSupport.delete(ContentBean.class, bean.getId());
                        }
                    });
                    builder.create().show();
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

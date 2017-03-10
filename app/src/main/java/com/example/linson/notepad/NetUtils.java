package com.example.linson.notepad;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.linson.notepad.domain.ContentBean;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.entity.FileUploadEntity;

import java.io.File;
import java.util.List;

/**
 * Created by linson on 2017/3/9.
 */

public class NetUtils {
    private static final String TAG = "log";

    public static void upFile(final Context context, String fileName, String dirName) {
        HttpUtils httpUtils = new HttpUtils();
        String filePath = context.getFilesDir() + File.separator + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            Log.i(TAG, "upFile: 文件存在 " + file.getAbsolutePath());
        }
        String url = ConstantUtils.SERVER_UP_FILE_URL;
        RequestParams requestParams = new RequestParams();
        requestParams.addQueryStringParameter("fileName", fileName);
        requestParams.addQueryStringParameter("dirName", dirName);
        requestParams.setBodyEntity(new FileUploadEntity(new File(filePath), "binary/octet-stream"));
        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.i(TAG, "onSuccess: 上传成功");
                Toast.makeText(context, "上传数据成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG, "onFailure: 上传失败" + s);
                Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void downFile(final Handler mHandler, String down_file_url, String down_file_path, String fileName, String dirName) {
        HttpUtils httpUtils = new HttpUtils();
        Log.i(TAG, "downFile: " + down_file_url);
        Log.i(TAG, "downFile: " + down_file_path);
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("fileName", fileName);
        params.addQueryStringParameter("dirName", dirName);
        httpUtils.download(HttpRequest.HttpMethod.POST, down_file_url, down_file_path, params, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                File file = responseInfo.result;
                Log.i(TAG, "onSuccess: 下载成功 " + file.getAbsolutePath());
                List<ContentBean> beanList = XmlUtils.readXmlFile(file);
                Log.i(TAG, "onSuccess: " + beanList);
                Message message = mHandler.obtainMessage();
                message.what = 100;
                message.obj = beanList;
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG, "onFailure: 下载失败");
            }
        });
    }

    public static void send() {
        String url = "http://192.168.11.171:8080/ServletUpFile";
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.i(TAG, "onSuccess: " + responseInfo.result.toString());
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i(TAG, "onFailure: ");
            }
        });
    }

}

package com.itheima.alipaydemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.itheima.alipaydemo.model.PrePayInfo;
import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itheima.alipaydemo.model.PayResult;

public class MainActivity extends Activity implements Response.Listener<String>, Response.ErrorListener{

    private RequestQueue queue;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PayResult payResult = new PayResult((String) msg.obj);
            /**
             * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
             * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
             * docType=1) 建议商户依赖异步通知
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息

            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, "9000")) {
                Toast.makeText(MainActivity.this, "支付成功，" + resultInfo , Toast.LENGTH_SHORT).show();
            } else {
                // 判断resultStatus 为非"9000"则代表可能支付失败
                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(resultStatus, "8000")) {
                    Toast.makeText(MainActivity.this, "支付结果确认中，" + resultInfo, Toast.LENGTH_SHORT).show();

                } else if(TextUtils.equals(resultStatus, "6001")){
                    Toast.makeText(MainActivity.this, "取消支付， " + resultInfo, Toast.LENGTH_SHORT).show();
                }else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    Toast.makeText(MainActivity.this, "支付失败， " + resultInfo, Toast.LENGTH_SHORT).show();

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
    }

    /**
     * 支付宝支付
     * @param v
     */
    public void pay(View v){
        //支付宝支付四个流程
        String url = "http://192.168.56.1:8080/HeiMaPay/Pay";
        //1.post参数到服务器
        StringRequest request = new StringRequest(Request.Method.POST, url, MainActivity.this, MainActivity.this);
        queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    @Override
    public void onErrorResponse(VolleyError err) {
        Log.d("result", err.toString());
    }

    @Override
    public void onResponse(final String response) {
        Log.d("result", response);
        //2.解析获取支付串码
        final PrePayInfo payInfo = JSON.parseObject(response, PrePayInfo.class);

       new Thread(new Runnable() {
           @Override
           public void run() {
               // 构造PayTask 对象
               PayTask alipay = new PayTask(MainActivity.this);
               //3.调起支付宝sdk支付方法
               String result = alipay.pay(payInfo.getPayInfo(), true);

               Message msg = mHandler.obtainMessage();
               msg.obj = result;
               //4.处理支付结果
               mHandler.sendMessage(msg);
               Log.d("result", result);
           }
       }).start();
    }
}

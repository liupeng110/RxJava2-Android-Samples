package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.utils.AppConstant;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class SimpleExampleActivity extends AppCompatActivity {

    private static final String TAG = SimpleExampleActivity.class.getSimpleName();
    Button btn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        btn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                doSomeWork();
            }
        });
    }

     //逐个发送值
    private void doSomeWork() {
        Observable.just("Cricket1", "Football2")  //按顺序挨个发射数据
                .subscribeOn(Schedulers.io())                                     //后台线程运行
                .observeOn(AndroidSchedulers.mainThread())         // 通知主线程
                .subscribe( new Observer<String>() {
                    @Override public void onSubscribe(Disposable d) { Log.d(TAG, " onSubscribe : " + d.isDisposed());  }
                    @Override public void onNext(String value) {  textView.append(value); } //多次进入,这里处理数据
                    @Override public void onError(Throwable e) { e.printStackTrace(); }                           //处理各种异常信息
                    @Override  public void onComplete() {   Log.d(TAG, " 完成onComplete");   }  //全部接收完成之后
                });
    }

}
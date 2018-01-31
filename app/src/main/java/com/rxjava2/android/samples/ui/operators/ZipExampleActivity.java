package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.model.User;
import com.rxjava2.android.samples.utils.AppConstant;
import com.rxjava2.android.samples.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class ZipExampleActivity extends AppCompatActivity {

    private static final String TAG = ZipExampleActivity.class.getSimpleName();
    Button btn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        btn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSomeWork();
            }
        });
    }

    /*
      在这里，我们得到两个用户列表一，
      板球迷的列表另一个，足球迷的名单然后，
      我们正在寻找爱好的用户列表
    */
    private void doSomeWork() {
        Observable.zip(Observable.create(new ObservableOnSubscribe<List<User>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<User>> e) throws Exception {
                            e.onNext(Utils.getUserListWhoLovesCricket());//发射第一个数据
                            e.onComplete();
                    }
                }), Observable.create(new ObservableOnSubscribe<List<User>>() {
                    @Override  public void subscribe(ObservableEmitter<List<User>> e) throws Exception {
                            e.onNext(Utils.getUserListWhoLovesFootball());//发射第二个数据
                            e.onComplete();
                    }
                }), new BiFunction<List<User>, List<User>, List<User>>() {  //接收两个参数 返回一个结果 本质还是对原来数据的处理
                    @Override  public List<User> apply(List<User> cricketFans, List<User> footballFans) throws Exception {
                        return Utils.filterUserWhoLovesBoth(cricketFans, footballFans);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                   @Override public void onSubscribe(Disposable d) {
                        Log.d(TAG, " onSubscribe : " + d.isDisposed());
                    }
                    @Override public void onNext(List<User> userList) {
                        textView.append(" onNext");
                        textView.append(AppConstant.LINE_SEPARATOR);
                        for (User user : userList) {
                            textView.append(" firstname : " + user.firstname);
                            textView.append(AppConstant.LINE_SEPARATOR);
                        }
                        Log.d(TAG, " onNext : " + userList.size());
                    }
                    @Override public void onError(Throwable e) {  Log.d(TAG, " onError : " + e.getMessage());  }
                    @Override  public void onComplete() {  Log.d(TAG, " onComplete"); }
                });
    }



}
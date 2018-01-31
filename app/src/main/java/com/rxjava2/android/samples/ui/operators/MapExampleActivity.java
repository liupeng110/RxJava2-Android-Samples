package com.rxjava2.android.samples.ui.operators;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.model.ApiUser;
import com.rxjava2.android.samples.model.User;
import com.rxjava2.android.samples.utils.AppConstant;
import com.rxjava2.android.samples.utils.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class MapExampleActivity extends AppCompatActivity {

    private static final String TAG = MapExampleActivity.class.getSimpleName();
    Button btn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        btn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                doSomeWork();
//            }
//        });


        RxView.clicks(btn)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override  public void call(Void aVoid) {
//                        Toast.makeText(MapExampleActivity.this, "ceshi", Toast.LENGTH_SHORT).show();
                        doSomeWork();
                    }
                });
    }

    /*
   在这里，我们从API服务器获取ApiUser对象，然后我们将其转换为用户对象，
   因为可能是我们的数据库支持用户不是ApiUser对象在这里，
   我们正在使用地图运算符来做到这一点
    */
    private void doSomeWork() {
        Observable.create(new ObservableOnSubscribe<List<ApiUser>>() {    //原始数据格式
            @Override  public void subscribe(ObservableEmitter<List<ApiUser>> e) throws Exception {  //是否弃置
                    e.onNext(Utils.getApiUserList());                   //发送获取到的数据 未转化
                    e.onComplete();                                               //注册oncomplete事件 否则接收不到
                  } }  )
                .subscribeOn(Schedulers.io())                             //后台线程运行
                .observeOn(AndroidSchedulers.mainThread())//通知主线程
                .map(new Function<List<ApiUser>, List<User>>() {

                    @Override
                    public List<User> apply(List<ApiUser> apiUsers) throws Exception {
                        return Utils.convertApiUserListToUserList(apiUsers);//从服务器转化为本地bean
                    }
                })
                .subscribe(new Observer<List<User>>() {
                    @Override public void onSubscribe(Disposable d) {  Log.d(TAG, " onSubscribe : " + d.isDisposed());  } //是否弃置
                    @Override public void onNext(List<User> userList) { //接收到转换后的数据 并处理
                        textView.append(" onNext");
                        textView.append(AppConstant.LINE_SEPARATOR);
                        for (User user : userList) {
                            textView.append(" firstname : " + user.firstname);
                            textView.append(AppConstant.LINE_SEPARATOR);
                        }
                        Log.d(TAG, " onNext : " + userList.size());
                    }
                    @Override public void onError(Throwable e) {  Log.d(TAG, " onError : " + e.getMessage()); } //所有异常
                    @Override public void onComplete() {  Log.d(TAG, " onComplete"); }//事件完成之后
                });
    }


}
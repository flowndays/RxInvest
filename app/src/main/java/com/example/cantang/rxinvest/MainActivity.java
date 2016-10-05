package com.example.cantang.rxinvest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_OBSERVER = "Observer";
    private TextView mResultTextView;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView = (TextView) findViewById(R.id.resultText);

        findViewById(R.id.testMuteObservable).setOnClickListener(v -> testObservable(Observable.fromCallable(() -> {
            Thread.sleep(10000);
            return 0;
        })));

        findViewById(R.id.testNormalObservable).setOnClickListener(v -> {
            testObservable(Observable.just(10));
        });
        findViewById(R.id.testObservableException).setOnClickListener(v -> {
            testObservable(Observable.error(new RuntimeException("other exception")));
        });
    }

    private void testObservable(Observable<Integer> observable) {
        subscriptions.add(
                observable
                        .compose(integerObservable -> RxUtil.emitDefaultOnTimeout(integerObservable, 1, 1, TimeUnit.SECONDS))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG_OBSERVER, "onError");
                                mResultTextView.setText("onError() called, Throwable:" + e);
                            }

                            @Override
                            public void onNext(Integer integer) {
                                Log.d(TAG_OBSERVER, "onError");
                                mResultTextView.setText("onNext() called, emission:" + integer);
                            }
                        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }
}

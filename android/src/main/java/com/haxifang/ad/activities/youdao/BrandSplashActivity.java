package com.haxifang.ad.activities.youdao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.youdao.sdk.common.logging.YouDaoLog;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YoudaoSplash;
import com.youdao.sdk.nativeads.YoudaoSplashAd;
import com.youdao.sdk.nativeads.YoudaoSplashAdEventListener;
import com.youdao.sdk.nativeads.YoudaoSplashAdLoadListener;
import com.youdao.sdk.nativeads.YoudaoSplashAdParameters;
import com.youdao.sdk.nativeads.YoudaoSplashMediaView;

/**
 * Created by lyy on 2020/2/12
 * <p>
 * 品牌广告开屏广告demo类，品牌广告开屏广告拉取提供了两个接口，一个是preload方法表示预取，一个是loadAd取广告接口
 * preload方法建议在启动完成之后调用，下次启动开屏页面可以展示preload方法预取好的广告，demo中在主页YouDaoSampleActivity中调用了预取功能
 * loadAd方法在该开屏页面中调用，并且在取到广告后展示开屏广告
 * demo根据app中广告场景，页面增加了跳过按钮和跳过逻辑，这部分开发者可以按照自己开屏需求进行定制
 */
public class BrandSplashActivity extends Activity {
//    public static final String PLACEMENT_ID = "7a594f9df93df0404da6b0553f9f93b7";

    private boolean mIsJump = true;
    private boolean mIsClicked = false;
    private String code_id;

    private YoudaoSplash mSplashAdLoader;
    private YoudaoSplashAd mSplashAd;

    private TextView mSkipTv;

    private Handler mUiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_splash);

        Bundle extras = getIntent().getExtras();
        code_id = extras.getString("codeid");

        loadSplashAd();
    }

    private void jump() {
        YouDaoLog.v("jump");
        // 如果当前应用在前台则跳转，解决当显示开屏广告时，用户点击home键进入后台，此时仍会跳转的问题
        boolean background = Utils.isBackground(getApplicationContext());
        if (mIsJump && !background) {
            // 如果跳转成功过则不再跳转
            mIsJump = false;
        //    startActivity(new Intent(BrandSplashActivity.this,
        //            YouDaoSampleActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //点击发生之后，建议在用户点击返回原来页面之后再进行跳转，用户点击返回之后会走该方法
        if (mIsClicked) {
            jump();
        }
    }

    /**
     * 加载品牌开屏广告
     */
    private void loadSplashAd() {
        mSplashAdLoader = new YoudaoSplash(YoudaoSplashAdParameters.builder()
                .context(this)
//                .placementId(BrandSplashActivity.PLACEMENT_ID)
                .placementId(code_id)
                .requestParameters(new RequestParameters.Builder().build())
                .build());

        //加载广告，设置一个点击回调和超时时间，这里超时时间设置为了700ms(开发者可根据需求设置)，如果该时间没有广告成功返回则会回调onAdLoadTimeout
        mSplashAdLoader.loadAd(new YoudaoSplashAdLoadListener() {
            @Override
            public void onAdLoaded(final YoudaoSplashAd splashAd) {
                YouDaoLog.w("onAdLoaded");
                //该方法在非UI线程回调，ui操作放到主线程
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showSplashAd(splashAd);
                    }
                });
            }

            @Override
            public void onAdLoadFailed(int errorCode, String errorMessage) {
                YouDaoLog.w("onAdLoadFailed errorCode = " + errorCode + ",errorMessage = " + errorMessage);
                //该方法在非UI线程回调，ui操作放到主线程
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        jump();
                    }
                });
            }

            @Override
            public void onAdLoadTimeout() {
                YouDaoLog.w("onAdLoadTimeout");
                //该方法在非UI线程回调，ui操作放到主线程
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        jump();
                    }
                });
            }
        }, 700);
    }

    /**
     * 展示加载到的品牌开屏广告
     * sdk提供YoudaoSplashMediaView可以渲染开屏大图和视频，方便用户渲染;用户也可以自己获取数据进行渲染
     * xml中可以增加自定义按钮等,如这里的跳过按钮
     *
     * @param splashAd 广告对象
     */
    private void showSplashAd(YoudaoSplashAd splashAd) {
        mSplashAd = splashAd;
        splashAd.setEventListener(new YoudaoSplashAdEventListener() {
            @Override
            public void onAdClicked() {
                YouDaoLog.w("onAdClicked");
                mIsClicked = true;
            }

            @Override
            public void onAdImpression() {
                YouDaoLog.w("onAdImpression");
            }

            @Override
            public void onDownloadDialogConfirmed() {
                YouDaoLog.w("onDownloadDialogConfirmed");
                jump();
            }

            @Override
            public void onDownloadDialogCanceled() {
                YouDaoLog.w("onDownloadDialogCanceled");
            }
        });

        //YoudaoSplashMediaView为渲染品牌开屏视频和图片的mediaView，默认宽高为match_parent
        YoudaoSplashMediaView mediaView = findViewById(R.id.ad_media_view);

        //设置半屏广告的高宽比范围，说明：宽度固定为屏幕宽，高度可以根据比例动态调整，如果设置了范围则半屏广告高度最小值为minAspectRatio * width,最大值为maxAspectRatio * width
        //开发者可以根据需求进行设置，不设置表示高度不受范围限制
        double minAspectRatio = (double) 1480 / 1080;
        double maxAspectRatio = (double) 1860 / 1080;
        mediaView.setAspectRatioRange(minAspectRatio, maxAspectRatio);

        //渲染时无论视频或图片都是scaleType都是centerCrop
        mediaView.renderAd(splashAd);

        initSkipBtn(splashAd.getShowInterval() - 1);
    }

    /**
     * 定制skip按钮，可以根据需求定制skip按钮样式
     *
     * @param showInterval 展示时长
     */
    private void initSkipBtn(final int showInterval) {
        mSkipTv = findViewById(R.id.skip_tv);
        new CountDownTimer(showInterval * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                mSkipTv.setText("跳过 " + (millisUntilFinished / 1000 + 1));
            }

            public void onFinish() {
                if (!mIsClicked) {
                    jump();
                }
            }
        }.start();
        mSkipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //资源释放部分，必须要调用，防止内存泄漏
        if (mSplashAdLoader != null) {
            mSplashAdLoader.destroy();
        }
        if (mSplashAd != null) {
            mSplashAd.destroy();
        }
    }
}

package com.example.climbachiya.dragandmapobjectdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.rebound.SpringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean mAnimating;
    private View mRootContainer;
    private ViewGroup mRootView;
    private ExampleContainerView mCurrentExample;

    private static final List<Sample> SAMPLES = new ArrayList<Sample>();

    static {
        SAMPLES.add(new Sample(BallExample.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();

        mRootContainer = findViewById(R.id.root_container);
        mRootView = (ViewGroup) findViewById(R.id.root);
    }

    private void setUpToolbar() {
        // Enable up icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    public void letCheckBallDemo(View view){

        if (mAnimating) {
            return;
        }

        Class<? extends View> clazz = SAMPLES.get(0).viewClass;
        View sampleView = null;
        try {
            Constructor<? extends View> ctor = clazz.getConstructor(Context.class);
            sampleView = ctor.newInstance(this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (sampleView == null) {
            return;
        }
        mAnimating = true;

        mCurrentExample = new ExampleContainerView(this);
        mCurrentExample.addView(sampleView);
        mRootView.addView(mCurrentExample);

        mCurrentExample.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentExample.reveal(true, new ExampleContainerView.Callback() {
                    @Override
                    public void onProgress(double progress) {
                        float scale = (float) SpringUtil.mapValueFromRangeToRange(progress, 0, 1, 0.8, 1);
                        mRootContainer.setScaleX(scale);
                        mRootContainer.setScaleY(scale);
                        mRootContainer.setAlpha((float) progress);
                    }

                    @Override
                    public void onEnd() {
                        mAnimating = false;
                        //setTitle("BallView");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setTitle(R.string.ball_view);
                        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.color.colorAccent));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccentPrimary));
                        }

                    }
                });
            }
        }, 100);
    }

    @Override
    public void onBackPressed() {
        if (mAnimating || mCurrentExample == null) {
            return;
        }
        mAnimating = true;
        mCurrentExample.hide(true, new ExampleContainerView.Callback() {
            @Override
            public void onProgress(double progress) {
                float scale = (float) SpringUtil.mapValueFromRangeToRange(progress, 0, 1, 0.8, 1);
                mRootContainer.setScaleX(scale);
                mRootContainer.setScaleY(scale);
                mRootContainer.setAlpha((float) progress);
            }

            @Override
            public void onEnd() {
                mAnimating = false;
                mCurrentExample.clearCallback();
                mRootView.removeView(mCurrentExample);
                mCurrentExample = null;

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setTitle(R.string.app_name);
                getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.color.colorPrimary));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                }
                //setTitle(getResources().getString(R.string.app_name));
                //toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
            }
        });
    }

    private static class Sample {
        public Class<? extends View> viewClass;
        public String text;
        public String subtext;

        public Sample(Class<? extends View> viewClass) {
            this.viewClass = viewClass;
            /*this.text = text;
            this.subtext = subtext;*/
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_content, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

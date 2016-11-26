package com.example.yue_liang.smaplematerialavatarview;

import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.yue_liang.materialavatarview.CombineAvatarView;
import com.example.yue_liang.materialavatarview.MaterialAvatarView;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    CombineAvatarView combineAvatarView0;

    CombineAvatarView combineAvatarView1;

    CombineAvatarView combineAvatarView2;

    MaterialAvatarView combineAvatarView1_child_0;

    MaterialAvatarView combineAvatarView2_child_1;

    MaterialAvatarView combineAvatarView2_child_2;

    MaterialAvatarView combineAvatarView2_child_3;

    MaterialAvatarView combineAvatarView1_child_1;

    MaterialAvatarView combineAvatarView1_child_2;
    MaterialAvatarView a1, a2, a3;
    boolean disappear = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(disappear) {
                    disappear = false;

                    combineAvatarView0.animateForDisappear();
                } else {
                    disappear = true;

                    if(a1 == null) {
                        a1 = MaterialAvatarView.obtain(MainActivity.this).
                                setSingleDrawable(
                                        (VectorDrawable) (getResources().getDrawable(R.drawable.stranger).mutate()),
                                        getResources().getColor(R.color.cyan));
                    }
                    if(a2 == null) {
                        a2 = MaterialAvatarView.obtain(MainActivity.this).
                                setSingleDrawable(
                                        (VectorDrawable) getResources().getDrawable(R.drawable.stranger).mutate(),
                                getResources().getColor(R.color.blue_grey));
                    }
                    if(a3 == null) {
                        a3 = MaterialAvatarView.obtain(MainActivity.this).
                                setSingleDrawable(
                                        (VectorDrawable) getResources().getDrawable(R.drawable.stranger).mutate(),
                                getResources().getColor(R.color.blue));
                    }

                    combineAvatarView0.addChild(a1);
                    combineAvatarView0.addChild(a2);
                    combineAvatarView0.addChild(a3);
                    combineAvatarView0.refreshLayout(CombineAvatarView.AVATAR_COUNT_MORE);
                }
            }
        });

        combineAvatarView1_child_0 = MaterialAvatarView.obtain(this);
        combineAvatarView1_child_0.setSingleDrawable((VectorDrawable) (getResources().getDrawable(R.drawable.stranger).mutate()),
                getResources().getColor(R.color.drak));
        combineAvatarView0 = (CombineAvatarView) findViewById(R.id.combine0);
        combineAvatarView0.setAvatarCount(CombineAvatarView.AVATAR_COUNT_ONE);
        combineAvatarView0.addChild(combineAvatarView1_child_0);


        combineAvatarView1_child_1 = MaterialAvatarView.obtain(this);
        combineAvatarView1_child_1.setSingleDrawable((VectorDrawable) (getResources().getDrawable(R.drawable.stranger).mutate()),
                getResources().getColor(R.color.cyan));
        combineAvatarView1_child_2 = MaterialAvatarView.obtain(this);
        combineAvatarView1_child_2.setSingleDrawable((VectorDrawable) getResources().getDrawable(R.drawable.stranger).mutate(),
                getResources().getColor(R.color.blue_grey));
        combineAvatarView1 = (CombineAvatarView) findViewById(R.id.combine1);
        combineAvatarView1.setAvatarCount(CombineAvatarView.AVATAR_COUNT_TWO);
        combineAvatarView1.addChild(combineAvatarView1_child_1);
        combineAvatarView1.addChild(combineAvatarView1_child_2);


        combineAvatarView2_child_1 = MaterialAvatarView.obtain(this);
        combineAvatarView2_child_1.setSingleDrawable((VectorDrawable) getResources().getDrawable(R.drawable.stranger).mutate(),
                getResources().getColor(R.color.yellow));
        combineAvatarView2_child_2 = MaterialAvatarView.obtain(this);
        combineAvatarView2_child_2.setSingleDrawable((VectorDrawable) getResources().getDrawable(R.drawable.stranger).mutate(),
                getResources().getColor(R.color.blue_grey));
        combineAvatarView2_child_3 = MaterialAvatarView.obtain(this);
        combineAvatarView2_child_3.setSingleDrawable((VectorDrawable) getResources().getDrawable(R.drawable.stranger).mutate(),
                getResources().getColor(R.color.blue));
        combineAvatarView2 = (CombineAvatarView) findViewById(R.id.combine2);
        combineAvatarView2.setAvatarCount(CombineAvatarView.AVATAR_COUNT_MORE);
        combineAvatarView2.addChild(combineAvatarView2_child_1);
        combineAvatarView2.addChild(combineAvatarView2_child_2);
        combineAvatarView2.addChild(combineAvatarView2_child_3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

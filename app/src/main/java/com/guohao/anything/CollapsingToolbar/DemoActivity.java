package com.guohao.anything.CollapsingToolbar;

import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.guohao.anything.LogUtil;
import com.guohao.anything.R;
import com.guohao.common.widget.PortraitView;
import com.jaeger.library.StatusBarUtil;

/**
 * 该 demo 的目的是做出折叠效果的 AppBar
 *
 * 由 CoordinatorLayout，AppBarLayout，CollapsingToolbarLayout 和 Toolbar 共同实现。
 *
 *
 */
public class DemoActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected AppBarLayout mAppBarLayout;
    protected MenuItem mMenuItem;
    protected PortraitView mPortrait;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // 设置状态栏透明
        StatusBarUtil.setTransparent(DemoActivity.this);

        // 初始化 toolbar
        initToolbar();

        // 监听 appbar 的滑动状态
        setLinstenerOnAppBar();
    }

    /**
     * 初始化 toolbar，设置 toolbar 左边按钮的图标 和点击响应
     *
     */
    protected void initToolbar() {
        mPortrait = findViewById(R.id.im_portrait);
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {

            // 初始化 toolbar 菜单，// 下述方法二选一
            //setSupportActionBar(mToolbar); //要配合 onCreateOptionsMenu 函数初始化 mToolbar

            mToolbar.inflateMenu(R.menu.personal);
            // 拿到菜单的 item，下述函数会控制其放缩
            mMenuItem = mToolbar.getMenu().findItem(R.id.action_follow);

            // 设置左边按钮的 Icon
            mToolbar.setNavigationIcon(R.drawable.ic_home);
            // 设置左边按钮的 监听
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do something
                }
            });


        }

    }

    /**
     * 加载菜单到 toolbar，需要执行 setSupportActionBar 函数，否则加载无效
     *
     * @param menu 就是 setSupportActionBar 函数的入参
     * @return
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // 初始化菜单
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.personal, menu);
//
//        // 这样拿不到
//        //mMenuItem = menu.findItem(R.id.action_follow);
//        return super.onCreateOptionsMenu(menu);
//    }

    /**
     * 监听 mAppBarLayout 的折叠和展开
     */
    protected void setLinstenerOnAppBar(){
        mAppBarLayout = findViewById(R.id.appBarLayout);
        if(mAppBarLayout != null){
            mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    // 当 appBar 往上折叠时，verticalOffset 是负数
                    LogUtil.e(" verticalOffset = " + verticalOffset);
                    updatePortraitView(verticalOffset);
                }
            });
        }
    }

    /**
     * 随着 mAppBarLayout 的折叠和展开，同步更新头像控件的放缩比例和透明度
     */
    private void updatePortraitView(int verticalOffset){
        LogUtil.e("");

        View view = mPortrait;
        MenuItem menuItem = mMenuItem;
        AppBarLayout appBarLayout = mAppBarLayout;

        if (view == null /*|| menuItem == null*/ || appBarLayout == null)
            return;

        LogUtil.e("111");

        if (verticalOffset == 0) {
            // 完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);

            // 隐藏菜单
            menuItem.setVisible(false);
            menuItem.getIcon().setAlpha(0);
        } else {
            // abs 运算
            verticalOffset = Math.abs(verticalOffset);
            final int totalScrollRange = appBarLayout.getTotalScrollRange();
            if (verticalOffset >= totalScrollRange) {
                // 关闭状态
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);

                // 显示菜单
                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255);

            } else {
                // 中间状态
                // 算出的 progress 是比例值，值为 0～1 之间
                // totalScrollRange 是总高度
                // verticalOffset / (float) totalScrollRange 表示折叠的比例
                // 折叠程度越高，view 缩得越小，故需要用 1 减去折叠的比例
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
                // 和头像恰好相反
                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255 - (int) (255 * progress));
            }
        }
    }

}

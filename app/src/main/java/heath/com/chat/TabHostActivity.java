package heath.com.chat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import heath.com.chat.adapter.ViewPagerFragmentAdapter;
import heath.com.chat.friend.AddFriendsActivity;
import heath.com.chat.friend.FriendsFragment;
import heath.com.chat.message.MessageFragment;
import heath.com.chat.mine.MineFragment;

public class TabHostActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvAdd;

    private BottomNavigationView navigationView;
    private ViewPager viewPager;
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabhost);
        //初始化控件
        initView();
        initListener();
        //将各个fragment加入到fragments中
        addFragment();
        //将viewPager和BottomNavigationView的选择事件连接起来
        link();
        //设置viewPager的适配器
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        //设置初始的页面项
        navigationView.setSelectedItemId(R.id.navigation_message);
        open();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        mIvAdd = this.findViewById(R.id.iv_add);

        navigationView = this.findViewById(R.id.bnv_viewpager);
        viewPager = this.findViewById(R.id.vp_viewpager);
        //将控件默认的图标隐藏，以显示自定义的按钮图标
        navigationView.setItemIconTintList(null);
    }

    private void initListener() {
        mIvAdd.setOnClickListener(this);
    }

    /**
     * 填充fragments
     */
    private void addFragment() {
        fragments = new Fragment[]{
                new MessageFragment(),
                new FriendsFragment(),
                new MineFragment()
        };
    }

    /**
     * 将viewPager和BottomNavigationView的事件连接起来
     * 分为两步
     * 1.给viewPager添加监听事件，即viewPager的页面变化时，navigation的选中项跟随变化
     * 2.给navigation添加监听事件，即当navigation的选中项变化时，viewPager的页面跟随变化
     */
    private void link() {
        //给viewPager设置监听事件,使viewPager页面变化时BottomNavigationView的item跟随其变化
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //以下三个方法是需要重写的父类方法

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ////当页面滑动,即当前页面滑动时调用
            }

            @Override
            public void onPageSelected(int position) {
                //当viewpager选定(滑动)后调用
                switch (position) {
                    case 0:
                        navigationView.setSelectedItemId(R.id.navigation_message);
                        break;
                    case 1:
                        navigationView.setSelectedItemId(R.id.navigation_friends);
                        break;
                    case 2:
                        navigationView.setSelectedItemId(R.id.navigation_mine);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //当滚动状态发生变化时，比如从滚动变到不滚动时调用
            }
        });

        //配置viewPager的页面随navigation的选中项变化
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        //每次点击后都将所有图标重置到默认不选中图片
                        resetToDefaultIcon();
                        switch (item.getItemId()) {
                            case R.id.navigation_message:
                                viewPager.setCurrentItem(0);
                                //设置按钮的图标
                                item.setIcon(R.drawable.select_message);
                                return true;
                            case R.id.navigation_friends:
                                viewPager.setCurrentItem(1);
                                //设置按钮的图标
                                item.setIcon(R.drawable.select_friends);
                                return true;
                            case R.id.navigation_mine:
                                viewPager.setCurrentItem(2);
                                //设置按钮的图标
                                item.setIcon(R.drawable.select_mine);
                                return true;
                        }

                        return false;
                    }
                });
    }

    /**
     * 重置按钮的图片为未选中的图标
     */
    private void resetToDefaultIcon() {
        navigationView.getMenu().findItem(R.id.navigation_message).setIcon(R.drawable.unselect_message);
        navigationView.getMenu().findItem(R.id.navigation_friends).setIcon(R.drawable.unselect_friends);
        navigationView.getMenu().findItem(R.id.navigation_mine).setIcon(R.drawable.unselect_mine);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                startActivity(new Intent(TabHostActivity.this, AddFriendsActivity.class));
                break;
            default:
                break;
        }
    }

    private void open() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
    }
}

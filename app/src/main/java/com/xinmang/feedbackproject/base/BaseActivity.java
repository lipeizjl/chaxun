package com.xinmang.feedbackproject.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xinmang.feedbackproject.R;
import com.xinmang.feedbackproject.databinding.ActivityBaseBinding;
import com.xinmang.feedbackproject.factory.PresenterMvpFactory;
import com.xinmang.feedbackproject.factory.PresenterMvpFactoryIml;
import com.xinmang.feedbackproject.proxy.BaseProxy;
import com.xinmang.feedbackproject.proxy.PresenterProxyInterface;
import com.xinmang.feedbackproject.utils.LogUtils;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


/**
 * Created by 李佩
 * @date 2017/01/28
 * @desrcption mvp模式Activity的基类,既然是基类，就不做任何业务逻辑,
 * 只把所有Activity共有的抽象出来，本基类提供类使用公共的ToolBar和使用自定义的ToolBar的方法
 *
 */

public abstract class BaseActivity<V extends BaseMvpView,P extends BaseMvpPresenter<V>,VB extends ViewDataBinding> extends AppCompatActivity implements PresenterProxyInterface<V,P> {
    public final static String TAG=BaseActivity.class.getName();
    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";
    /**
     * 创建被代理的对象,传入默认Presenter的工厂
     */
    private BaseProxy<V,P> mProxy=new BaseProxy<>(PresenterMvpFactoryIml.<V,P>createFactory(getClass()));

    protected Context mContext;

    private ActivityBaseBinding mBaseBinding;//根布局

    protected VB mBindingView;//集成BaseActivity传递进来的布局

    private boolean isShowToolBar=true;//是否显示通用的ToolBar,默认显示

    private int backPic= R.drawable.back_white;//默认返回图标,可修改

    private int tooBarBackGroundColor= R.color.colorPrimary;//通用toolBar默认的颜色

    private ACProgressFlower dialog;//加载显示框

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRestore(savedInstanceState);
        initLayout();
        init();
        setToolBar();
        initData();
        initEventer();

    }

    private void initLayout(){
        mContext=this;
        mProxy.onAttachView((V) this);
        mBaseBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null, false);
        mBindingView = DataBindingUtil.inflate(getLayoutInflater(), getLayoutContent(), null, false);
        // content
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBindingView.getRoot().setLayoutParams(params);
        mBaseBinding.container.addView(mBindingView.getRoot());
        getWindow().setContentView(mBaseBinding.getRoot());
    }

    /**
     * 设置titlebar
     */
    protected void setToolBar() {
        if (isShowToolBar) {
            setSupportActionBar(mBaseBinding.topToolBar.toolBar);
            mBaseBinding.topToolBar.toolBar.setBackgroundResource(tooBarBackGroundColor);
            mBaseBinding.topToolBar.toolBar.setNavigationIcon(backPic);
            mBaseBinding.topToolBar.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } else {
            mBaseBinding.topToolBar.toolBar.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题
     * @param title 传入的标题
     * @desrciption 次方法要在init()中执行
     */
    public void setmTitle(CharSequence title){
        mBaseBinding.topToolBar.toolBar.setTitle(title);
    }

    /**
     * 设置是否使用公共的ToolBar,要在init()方法中执行
     * @param isShowToolBar 是否显示公共的ToolBar
     */
    public void setShowToolBar(boolean isShowToolBar){
        this.isShowToolBar=isShowToolBar;
    }

    /**
     * 设置ToolBar的背景颜色,要在init()方法中执行
     * @param tooBarBackGroundColor 颜色值
     */
    public void setTooBarBackGroundColor(int tooBarBackGroundColor){
        this.tooBarBackGroundColor=tooBarBackGroundColor;
    }

    private void onRestore(Bundle savedInstanceState){
        LogUtils.e(TAG,"onCreate");
        LogUtils.e(TAG,"mProxy="+mProxy);
        if(savedInstanceState!=null){
            mProxy.onRestoreInstanceState(savedInstanceState);
        }
        mProxy.onAttachView((V) this);
    }

    /**
     * 创建加载框
     *
     * @param title
     * @return
     */
    public ACProgressFlower getLoadingDialog(String title) {
        dialog= new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(title)
                .textSize(30)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    /**
     * 隐藏
     */
    public void hide(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }


    /**
     * 获取布局文件
     */
    protected abstract int getLayoutContent();

    /**
     * 初始化的方法
     */
    protected abstract void init();

    /**
     * 初始化数据
     */

    protected abstract void initData();

    /**
     *初始化监听
     */

    protected abstract void initEventer();

    @Override
    protected void onResume() {
        super.onResume();
        mProxy.onAttachView((V) this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProxy.onDestory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_SAVE_KEY,mProxy.onSaveInstanceState());
    }

    @Override
    public void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory) {
        mProxy.setPresenterFactory(presenterFactory);
    }

    @Override
    public PresenterMvpFactory<V, P> getPresenterFactory() {
        return mProxy.getPresenterFactory();
    }

    @Override
    public P getPresenter() {
        return mProxy.getPresenter();
    }


}

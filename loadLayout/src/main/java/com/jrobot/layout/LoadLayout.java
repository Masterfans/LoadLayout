package com.jrobot.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.Map;

/**
 * 加载布局组件
 */
public class LoadLayout extends FrameLayout implements LoadingModel, View.OnClickListener {

    private OnRetryListener mRetryListener;

    //加载中view
    private int contentLayout;

    private int emptyLayout = R.layout._layout_empty;
    private int loadingLayout = R.layout._layout_loading;
    private int errorLayout = R.layout._layout_error;

    private int errorImage = R.drawable._loading_error;
    private int emptyImage = R.drawable._loading_empty;
    private int imagePadding = 10;

    private CharSequence errorText;
    private CharSequence emptyText;

    private int textColor;
    private int retryTextColor;
    private LoadStatus mLoadStatus;
    private Map<Integer, View> mViewMap;
    private LayoutInflater mInflater;
    private MutableLiveData<LoadStatus> mLiveStatus;

    public LoadLayout(@NonNull Context context) {
        this(context, null);
    }

    public LoadLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.loadingLayout, R.style.LoadLayout_Style);
    }

    public LoadLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.LoadLayout_Style);
    }

    public LoadLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadLayout, defStyleAttr, defStyleRes);

        emptyLayout = a.getResourceId(R.styleable.LoadLayout_emptyLayout, emptyLayout);
        loadingLayout = a.getResourceId(R.styleable.LoadLayout_loadingLayout, loadingLayout);
        errorLayout = a.getResourceId(R.styleable.LoadLayout_errorLayout, errorLayout);

        errorText = a.getString(R.styleable.LoadLayout_errorText);
        emptyText = a.getString(R.styleable.LoadLayout_emptyText);

        emptyImage = a.getResourceId(R.styleable.LoadLayout_emptyImage, NO_ID);
        errorImage = a.getResourceId(R.styleable.LoadLayout_errorImage, NO_ID);

        int status = a.getInt(R.styleable.LoadLayout_loadStatus, -1);
        mLoadStatus = LoadStatus.valueOf(status);
        imagePadding = a.getDimensionPixelSize(R.styleable.LoadLayout_imagePadding, dp2px(imagePadding));

        textColor = a.getColor(R.styleable.LoadLayout_llTextColor, 0xff999999);
        retryTextColor = a.getColor(R.styleable.LoadLayout_llRetryTextColor, 0xff999999);

        init();

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 0) {
            return;
        }
        setContentView(getChildAt(0));
        if (mLoadStatus != null) {
            updateStatus(mLoadStatus);
        }
    }

    private void init() {
        mViewMap = new HashMap<>(4);
        mInflater = LayoutInflater.from(getContext());
        mLiveStatus = new MutableLiveData<>();
    }

    private void setContentView(View contentView) {
        contentLayout = contentView.getId();
        if (contentLayout == NO_ID) {
            contentLayout = View.generateViewId();
            contentView.setId(contentLayout);
            Log.w("LoadLayout", String.format("content view id is: %d, generateViewId: %d", NO_ID, contentLayout));
        }
        mViewMap.put(contentLayout, contentView);
    }

    int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    public void setRetryListener(OnRetryListener retryListener) {
        mRetryListener = retryListener;
    }

    @Override
    public void showLoading() {
        updateStatus(LoadStatus.LOADING);
    }

    public LoadStatus getLoadStatus() {
        return mLoadStatus;
    }

    private void updateStatus(LoadStatus loadStatus) {
        mLoadStatus = loadStatus;
        mLiveStatus.setValue(loadStatus);
        switch (loadStatus) {
            case EMPTY:
                show(emptyLayout);
                break;
            case ERROR:
                show(errorLayout);
                break;
            case CONTENT:
                show(contentLayout);
                break;
            case LOADING:
                show(loadingLayout);
                break;
        }
    }

    /**
     * 可以通过show***来更新状态或者设置LoadStatus来更新状态
     *
     * @param loadStatus
     */
    public void setStatus(LoadStatus loadStatus) {
        updateStatus(loadStatus);
    }

    /**
     * 订阅状态改变事件
     *
     * @param owner
     * @param observer
     */
    public void observerStatus(LifecycleOwner owner, Observer<LoadStatus> observer) {
        mLiveStatus.observe(owner, observer);
    }

    private void show(int layoutId) {
        for (View view : mViewMap.values()) {
            if (view.getId() != layoutId) {
                view.setVisibility(GONE);
            }
        }
        getView(layoutId).setVisibility(VISIBLE);
    }

    private View getView(int layoutId) {
        if (mViewMap.containsKey(layoutId)) {
            return mViewMap.get(layoutId);
        }
        View view = mInflater.inflate(layoutId, this, false);
        view.setVisibility(GONE);
        addView(view);
        mViewMap.put(layoutId, view);
        if (layoutId == R.layout._layout_empty) {
            TextView textView = findViewById(R.id.tv_empty);
            textView.setText(emptyText);
            textView.setTextColor(textColor);
            if (emptyImage != NO_ID) {
                textView.setCompoundDrawablesWithIntrinsicBounds(0, emptyImage, 0, 0);
                textView.setCompoundDrawablePadding(imagePadding);
            }

        } else if (layoutId == R.layout._layout_error) {
            TextView textView = findViewById(R.id.tv_error);
            textView.setText(errorText);
            textView.setTextColor(textColor);
            if (errorImage != NO_ID) {
                textView.setCompoundDrawablesWithIntrinsicBounds(0, errorImage, 0, 0);
                textView.setCompoundDrawablePadding(imagePadding);
            }
            TextView retry = findViewById(R.id.tv_retry);
            retry.setTextColor(retryTextColor);
            retry.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void showContent() {
        updateStatus(LoadStatus.CONTENT);
    }

    @Override
    public void showError() {
        showError(errorText);
    }

    @Override
    public void showError(CharSequence message) {
        updateStatus(LoadStatus.ERROR);
        if (errorLayout == R.layout._layout_error) {
            TextView textView = getView(errorLayout).findViewById(R.id.tv_error);
            textView.setText(message);
        }
    }


    @Override
    public void showEmpty() {
        updateStatus(LoadStatus.EMPTY);
    }

    @Override
    public void onClick(View v) {
        if (mRetryListener != null) {
            showLoading();
            mRetryListener.onRetry(v);
        }
    }
}

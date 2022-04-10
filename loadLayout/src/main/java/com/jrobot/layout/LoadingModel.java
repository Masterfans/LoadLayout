package com.jrobot.layout;

public interface LoadingModel {
    void showLoading();

    void showContent();

    void showError();

    void showError(CharSequence message);

    void showEmpty();

}

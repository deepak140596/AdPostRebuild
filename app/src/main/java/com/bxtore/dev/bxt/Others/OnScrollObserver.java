package com.bxtore.dev.bxt.Others;

import android.widget.AbsListView;

/**
 * Created by Deepak Prasad on 17-10-2018.
 */

public abstract class OnScrollObserver implements AbsListView.OnScrollListener {

    public abstract void onScrollUp();

    public abstract void onScrollDown();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    int last = 0;

    @Override
    public void onScroll(AbsListView view, int current, int visibles, int total) {
        int currentVisiblePos = view.getFirstVisiblePosition();
        if (currentVisiblePos < last ) {
            onScrollUp();
        } else if (currentVisiblePos > last ) {
            onScrollDown();

        }

        last = currentVisiblePos;
    }

}

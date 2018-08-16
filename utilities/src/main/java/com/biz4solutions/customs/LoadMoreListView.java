package com.biz4solutions.customs;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.biz4solutions.utilities.R;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by ketan on 3/3/2016.
 */
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private static final String TAG = "LoadMoreListView";

    private boolean mIsLoadingMore = false;
    private int mCurrentScrollState;
    private OnScrollListener mOnScrollListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private SmoothProgressBar mProgressBarLoadMore;
    private OnItemClickListener mOnItemClickListener;

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // footer
        if (mInflater != null) {
            RelativeLayout mFooterView = (RelativeLayout) mInflater.inflate(R.layout.load_more_footer, this, false);
            mProgressBarLoadMore = mFooterView.findViewById(R.id.load_more_progressBar);
            addFooterView(mFooterView);
        }

        super.setOnScrollListener(this);
        super.setOnItemClickListener(this);
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     *
     * @param l The scroll listener.
     */
    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        /*if ( scrollState == OnScrollListener.SCROLL_STATE_IDLE ){
            view.invalidateViews();
        }*/

        mCurrentScrollState = scrollState;

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (mOnLoadMoreListener != null) {
            if (visibleItemCount == totalItemCount) {
                if (mProgressBarLoadMore.getVisibility() != View.INVISIBLE)
                    mProgressBarLoadMore.setVisibility(View.INVISIBLE);

                //return;
            } else {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

                if (!mIsLoadingMore && loadMore && mCurrentScrollState != SCROLL_STATE_IDLE) {
                    mProgressBarLoadMore.setVisibility(View.VISIBLE);
                    mIsLoadingMore = true;
                    onLoadMore();
                }

            }
        }
    }

    /**
     * Register a callback to be invoked when this list reaches the end (last
     * item be visible)
     *
     * @param onLoadMoreListener The callback to run.
     */

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void onLoadMore() {
        Log.d(TAG, "onLoadMore");
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    /**
     * Notify the loading more operation has finished
     */
    public void onLoadMoreComplete() {
        mIsLoadingMore = false;
        mProgressBarLoadMore.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(adapterView, view, i, l);
        }
    }

    /**
     * Interface definition for a callback to be invoked when list reaches the
     * last item (the user load more items in the list)
     */
    public interface OnLoadMoreListener {
        /**
         * Called when the list reaches the last item (the last item is visible
         * to the user)
         */
        void onLoadMore();
    }
}
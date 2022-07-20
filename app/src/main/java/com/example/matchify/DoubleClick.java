package com.example.matchify;

import android.os.SystemClock;
import android.view.View;

public abstract class DoubleClick implements View.OnClickListener {

    // The time in which the second tap should be done in order to qualify as
    // a double click
    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;

    public DoubleClick() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    public DoubleClick(long doubleClickQualificationSpanInMillis) {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }


    @Override
    public void onClick(View v) {
        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            onDoubleClick();
        }
        timestampLastClick = SystemClock.elapsedRealtime();
    }
    public abstract void onDoubleClick();
}

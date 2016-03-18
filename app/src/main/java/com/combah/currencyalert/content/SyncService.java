package com.combah.currencyalert.content;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by rodrigo on 12/14/15.
 */
public class SyncService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        SyncManager.getInstance(this).sync();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}

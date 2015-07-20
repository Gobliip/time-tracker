package com.gobliip.chronos.server.service;

import com.gobliip.chronos.server.entities.Tracking;
import com.gobliip.chronos.server.entities.WorkPeriod;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lsamayoa on 19/07/15.
 */
public class WorkTrackingService {

    @Autowired
    private TrackingsService trackingsService;

    public void addWork(String principal, Long trackingId, int keyboardActions, int mouseActions){
        final Tracking tracking = trackingsService.findTracking(principal, trackingId);


    }

}

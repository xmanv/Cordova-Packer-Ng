package com.nordnetab.chcp.main.events;

import com.nordnetab.chcp.main.config.ApplicationConfig;
import com.nordnetab.chcp.main.model.ChcpError;

/**
 * Created by zliu on 2017/12/28.
 */

public class UpdateDownloadSuccessEvent extends WorkerEvent {

    public static final String EVENT_NAME = "chcp_updateDownLoadSuccess";

    /**
     * Class constructor.
     *
     * @param error  error information
     * @param config application config that was used for update download
     */
    public UpdateDownloadSuccessEvent(ChcpError error, ApplicationConfig config) {
        super(EVENT_NAME, error, config);
    }
}

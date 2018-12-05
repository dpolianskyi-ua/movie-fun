package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater) {
        this.albumsUpdater = albumsUpdater;
    }

    @Autowired
    private AlbumSchedulerTaskBean albumSchedulerTaskBean;

    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        try {
            if (albumSchedulerTaskBean.addRunEntry()) {
                try {
                    logger.debug("Starting albums update");
                    albumsUpdater.update();

                    logger.debug("Finished albums update");

                } catch (Throwable e) {
                    albumSchedulerTaskBean.cleanUpAppName();
                    logger.error("Error while updating albums; cleaned up run flag", e);
                }
            }
        } catch (Throwable e) {
            // transactional exception
        }
    }
}

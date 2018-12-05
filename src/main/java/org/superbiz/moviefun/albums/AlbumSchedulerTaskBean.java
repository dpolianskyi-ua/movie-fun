package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AlbumSchedulerTaskBean {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Boolean addRunEntry() {
        try {
            return (entityManager.createNativeQuery("INSERT INTO album_scheduler_task (app_name, started_at) VALUES ('moviefun', now())").executeUpdate() > 0) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
    }

    @Transactional
    public void cleanUpAppName() {
        int count = entityManager.createNativeQuery("DELETE FROM album_scheduler_task WHERE app_name = 'moviefun'").executeUpdate();
    }
}

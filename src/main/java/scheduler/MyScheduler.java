package scheduler;

import org.apache.logging.log4j.core.config.Scheduled;
import org.slf4j.LoggerFactory;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class MyScheduler {

    private static final Logger log = LoggerFactory.getLogger(MyScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}

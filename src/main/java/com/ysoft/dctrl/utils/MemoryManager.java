package com.ysoft.dctrl.utils;

import com.ysoft.dctrl.utils.exceptions.RunningOutOfMemoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by kuhn on 10/31/2017.
 */
public class MemoryManager {
    private static final Logger logger = LogManager.getLogger(MemoryManager.class);

    private static final long VM_MAX_MEMORY = Runtime.getRuntime().maxMemory();
    private static final long MEMORY_CHECK_MIN_INTERVAL = 500; // ms
    private static final long RUNNING_OUTTA_MEMORY_THRESHOLD = 1000000; // bytes
    private static final long TRY_GARBAGE_COLLECTION_THRESHOLD = 5000000; // bytes
    private static final long FREE_MEMORY_AFTER_GC_THRESHOLD = 200*1000; // 20x difference < 200kb
    private static final int FREE_MEMORY_AFTER_GC_HISTORY_SIZE = 10;

    private volatile static long lastCheckTimeStamp = 0;
    private volatile static boolean gcInitiated = false;
    private volatile static LimitedSizeQueue<Long> freeMemoryHistory = new LimitedSizeQueue<>(FREE_MEMORY_AFTER_GC_HISTORY_SIZE);

    public static void checkMemory() throws RunningOutOfMemoryException{

        if(System.currentTimeMillis() - lastCheckTimeStamp < MEMORY_CHECK_MIN_INTERVAL){ return; }

        long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        long presumableFreeMemory = VM_MAX_MEMORY - allocatedMemory;

        freeMemoryHistory.addFirst(presumableFreeMemory);

        // Normal constant low free memory threshold
        if(presumableFreeMemory < RUNNING_OUTTA_MEMORY_THRESHOLD){
            logger.warn("Low on free memory (<{} bytes}, throwing exception", RUNNING_OUTTA_MEMORY_THRESHOLD);
            reset();
            throw new RunningOutOfMemoryException();
        }

        // Minimum differential threshold (To prevent freezing due to excessive garbage collection with no improvement.)
        long d;
        long maxD = 0;
        for(int i = 0; i<freeMemoryHistory.size()-1; i++){
            d = Math.abs(freeMemoryHistory.get(i+1) - freeMemoryHistory.get(i));
            if(maxD < d){ maxD = d; }
        }

        if(freeMemoryHistory.size() == FREE_MEMORY_AFTER_GC_HISTORY_SIZE && maxD < FREE_MEMORY_AFTER_GC_THRESHOLD){
            logger.warn("Differental free memory threshold exceeded (<{} bytes)", FREE_MEMORY_AFTER_GC_THRESHOLD);
            reset();
            throw new RunningOutOfMemoryException();
        }

        if(presumableFreeMemory < TRY_GARBAGE_COLLECTION_THRESHOLD){
            if(!gcInitiated){
                logger.warn("Running out of memory (<{}): trying to initiate GC", FREE_MEMORY_AFTER_GC_THRESHOLD);
                System.gc();
                gcInitiated = true;
            }
        } else {
            gcInitiated = false;
        }

        lastCheckTimeStamp = System.currentTimeMillis();
    }

    private static void reset(){
        lastCheckTimeStamp = 0;
        gcInitiated = false;
        freeMemoryHistory.clear();
    }
}

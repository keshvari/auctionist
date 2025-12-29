package com.example.auctionist.support;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import java.util.function.Supplier;

@Component
public class OptimisticRetry {
    public <T> T runWithRetry(Supplier<T> action, int maxRetries) {
        int attempt = 0;
        long backoff = 5L; // ms
        for (;;) {
            try {
                return action.get();
            } catch (ObjectOptimisticLockingFailureException ex) {
                if (attempt++ >= maxRetries) throw ex;
                try { Thread.sleep(backoff); } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); throw ex;
                }
                backoff = Math.min(100, backoff << 1); // 5,10,20,40,80,100
            }
        }
    }
}

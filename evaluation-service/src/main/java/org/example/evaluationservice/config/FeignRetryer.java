package org.example.evaluationservice.config;

import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FeignRetryer implements Retryer {
    private static final Logger logger = LoggerFactory.getLogger(FeignRetryer.class);
    private final int maxAttempts;
    private final long period;
    private final long maxPeriod;
    private int attempt;

    public FeignRetryer() {
        this(3, 1000L, 2000L);
    }

    public FeignRetryer(int maxAttempts, long period, long maxPeriod) {
        this.maxAttempts = maxAttempts;
        this.period = period;
        this.maxPeriod = maxPeriod;
        this.attempt = 1;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) {
            throw e;
        }

        long interval;
        if (e.retryAfter() != null) {
            interval = e.retryAfter() - System.currentTimeMillis();
            if (interval > maxPeriod) {
                interval = maxPeriod;
            }
            if (interval < 0) {
                return;
            }
        } else {
            interval = Math.min(period * attempt, maxPeriod);
        }

        try {
            logger.info("Retrying request, attempt {} of {}", attempt, maxAttempts);
            TimeUnit.MILLISECONDS.sleep(interval);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    @Override
    public Retryer clone() {
        return new FeignRetryer(maxAttempts, period, maxPeriod);
    }
} 
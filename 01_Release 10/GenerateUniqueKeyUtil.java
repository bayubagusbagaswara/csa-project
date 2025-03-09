package com.services.billingservice.utils.placement;

import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class GenerateUniqueKeyUtil {

    private static final String PREFIX = "CSA";
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static synchronized String generateUniqueKey() {
        long timestamp = System.currentTimeMillis();
        int count = counter.incrementAndGet();

        if (count > 999) {
            counter.set(1);
            count = 1;
        }

        return PREFIX + timestamp + String.format("%03d", count);
    }

    public static String generateUniqueKeyUUID() {
        long timestamp = System.currentTimeMillis();
        String uuidPart = UUID.randomUUID().toString().substring(0, 14).replace("-","");
        return PREFIX + timestamp + uuidPart;
    }

}

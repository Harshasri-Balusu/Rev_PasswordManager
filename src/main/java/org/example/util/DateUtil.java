package org.example.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DateUtil {

    private DateUtil() {}

    public static Timestamp minutesFromNow(int minutes) {
        return Timestamp.valueOf(
                LocalDateTime.now().plusMinutes(minutes)
        );
    }
}

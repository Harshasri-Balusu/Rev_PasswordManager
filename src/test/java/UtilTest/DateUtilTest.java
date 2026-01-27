package UtilTest;

import org.example.util.DateUtil;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void minutesFromNow_shouldReturnFutureTime() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp future = DateUtil.minutesFromNow(5);

        assertNotNull(future);
        assertTrue(future.after(now));
    }

    @Test
    void minutesFromNow_shouldAddCorrectMinutes() {
        Timestamp future = DateUtil.minutesFromNow(10);

        long diffMillis =
                future.getTime() - System.currentTimeMillis();

        assertTrue(diffMillis >= 590_000 && diffMillis <= 610_000);

    }
}


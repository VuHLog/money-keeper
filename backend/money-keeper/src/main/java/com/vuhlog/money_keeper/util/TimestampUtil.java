package com.vuhlog.money_keeper.util;

import com.vuhlog.money_keeper.constants.TimeOptionType;
import com.vuhlog.money_keeper.model.PeriodOfTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class TimestampUtil {
    public static Timestamp stringToTimestamp(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new Timestamp(dateFormat.parse(dateString).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: " + dateString, e);
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(timestamp);
    }

    public static PeriodOfTime getPeriodOfTime(String timeOption) {
        PeriodOfTime periodOfTime = new PeriodOfTime();
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        if(timeOption.equals(TimeOptionType.LAST_30_DAYS.getType())){
            LocalDateTime localDateTimeLast30Days = localDateTimeNow.minusDays(30);
            periodOfTime.setStartDate(Timestamp.valueOf(localDateTimeLast30Days));
            periodOfTime.setEndDate(Timestamp.valueOf(localDateTimeNow));
        }else if(timeOption.equals(TimeOptionType.THIS_MONTH.getType())){
            LocalDateTime startDateTimeThisMonth = LocalDateTime.of(localDateTimeNow.getYear(), localDateTimeNow.getMonth(), 1, 0, 0, 0);
            periodOfTime.setStartDate(Timestamp.valueOf(startDateTimeThisMonth));
            LocalDateTime endDateTimeThisMonth = LocalDateTime.of(localDateTimeNow.getYear(), localDateTimeNow.getMonth(), 1, 23, 59, 59).with(TemporalAdjusters.lastDayOfMonth());
            periodOfTime.setEndDate(Timestamp.valueOf(endDateTimeThisMonth));
        } else if(timeOption.equals(TimeOptionType.LAST_MONTH.getType())){
            LocalDateTime startDateTimeThisMonth = LocalDateTime.of(localDateTimeNow.getYear(), localDateTimeNow.minusMonths(1).getMonth(), 1, 0, 0, 0);
            periodOfTime.setStartDate(Timestamp.valueOf(startDateTimeThisMonth));
            LocalDateTime endDateTimeThisMonth = LocalDateTime.of(localDateTimeNow.getYear(), localDateTimeNow.minusMonths(1).getMonth(), 1, 23, 59, 59).with(TemporalAdjusters.lastDayOfMonth());
            periodOfTime.setEndDate(Timestamp.valueOf(endDateTimeThisMonth));
        } else if(timeOption.equals(TimeOptionType.THIS_QUARTER.getType())){
            int currentQuarter = (localDateTimeNow.getMonthValue() - 1) / 3 + 1;
            int year = localDateTimeNow.getYear();
            int startMonth = (currentQuarter - 1) * 3 + 1; //tháng bắt đầu của quý này
            int endMonth = startMonth + 2;
            LocalDateTime startDateTime = LocalDateTime.of(year, startMonth, 1,0,0,0);
            LocalDateTime endDateTime = LocalDateTime.of(year, endMonth, 1,23,59,59).with(TemporalAdjusters.lastDayOfMonth());
            periodOfTime.setStartDate(Timestamp.valueOf(startDateTime));
            periodOfTime.setEndDate(Timestamp.valueOf(endDateTime));
        }else if (timeOption.equals(TimeOptionType.LAST_QUARTER.getType())){
            int currentQuarter = (localDateTimeNow.getMonthValue() - 1) / 3 + 1;
            int previousQuarter = currentQuarter - 1; // quy truoc
            int year = localDateTimeNow.getYear();
            // neu currentQuarter la Quy 1
            if (previousQuarter == 0) {
                previousQuarter = 4;
                year -= 1;
            }
            int startMonth = (previousQuarter - 1) * 3 + 1; //tháng bắt đầu của quý trước
            int endMonth = startMonth + 2;
            LocalDateTime startDateTime = LocalDateTime.of(year, startMonth, 1,0,0,0);
            LocalDateTime endDateTime = LocalDateTime.of(year, endMonth, 1,23,59,59).with(TemporalAdjusters.lastDayOfMonth());
            periodOfTime.setStartDate(Timestamp.valueOf(startDateTime));
            periodOfTime.setEndDate(Timestamp.valueOf(endDateTime));
        } else if (timeOption.equals(TimeOptionType.THIS_YEAR.getType())) {
            int year = localDateTimeNow.getYear();
            LocalDateTime startDateTime = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(year, 1, 1, 23,59,59);
            periodOfTime.setStartDate(Timestamp.valueOf(startDateTime));
            periodOfTime.setEndDate(Timestamp.valueOf(endDateTime));
        }else if (timeOption.equals(TimeOptionType.LAST_YEAR.getType())) {
            int year = localDateTimeNow.getYear() - 1;
            LocalDateTime startDateTime = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(year, 1, 1, 23,59,59);
            periodOfTime.setStartDate(Timestamp.valueOf(startDateTime));
            periodOfTime.setEndDate(Timestamp.valueOf(endDateTime));
        }
        return periodOfTime;
    }
}

package id.co.lua.pbj.penilaian_karyawan.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyDateFormat {
    public static final Locale LOCALE_INA = Locale.forLanguageTag("id-ID");
    public static final DateFormat defaultDateFormat =  DateFormat.getDateInstance(DateFormat.LONG, LOCALE_INA);
    public static final DateFormat defaultTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK);

    private MyDateFormat() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String getDayAndDateAndTimeIndonesian(Date date){
        if(date!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm:ss", LOCALE_INA);
            return sdf.format(date);
        }return null;
    }

    public static String formatDateInd(Date date){
        if(date != null)
            return defaultDateFormat.format(date);
        return "";
    }

    public static String formatDateTimeInd(Date date){
        return formatDateInd(date)+" "+formatTime(date);
    }

    public static String formatTime(Date date) {
        if (date != null) {
            return defaultTimeFormat.format(date);
        }
        return "";
    }

    public static String getDateIndonesian(Date date){
        if(date!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", LOCALE_INA);
            return sdf.format(date);
        }return null;
    }

    public static String getDateIndonesianAndHours(Date date){
        if(date!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm", LOCALE_INA);
            return sdf.format(date);
        }return null;
    }

    public static String formatTimeDay(Date date) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.forLanguageTag("id-ID"));
            return format.format(date);
        }
        return "";
    }

    public static String formatBulan(Date date) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("MMMM", Locale.forLanguageTag("id-ID"));
            return format.format(date);
        }
        return "";
    }

    public static String formatTahun(Date date) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            return format.format(date);
        }
        return "";
    }
}
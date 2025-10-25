package eci.edu.dosw.proyecto.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Centraliza los manjeos del tiempo en la aplicacion
 */
public final class TimeUtils {

    /***
     *  Para formatear la fecha y hora  para verificar el cruce de horarios
     * H:mm = 0:00 - 23:59 o 9:05
     * HH:mm = 00:00 - 23:59
     * h:mm a = 1:00 PM
     * hh:mm a = 01:00 PM
     *
     */
    private static final List<DateTimeFormatter> TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH),  // 1:00 PM
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH), // 01:00 PM
            DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH),   // 1:00PM
            DateTimeFormatter.ofPattern("hh:mma", Locale.ENGLISH)   // 01:00PM
    );

    private TimeUtils() {}

    /**
     * Devuelve la hora actual
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }


    /**
     * Normaliza el LocalDateTime recibido a equivalente en utc
     */
    public static LocalDateTime toUtc(LocalDateTime ldt) {
        if (ldt == null) return null;
        ZonedDateTime z = ldt.atZone(ZoneId.systemDefault());
        return z.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }



    /**
     * Intenta parsear la cadena de tiempo a LocalTime
     */
    public static LocalTime parseLocalTime(String timeStr) {
        if (timeStr == null) {
            throw new DateTimeParseException("Hora nula", "", 0);
        }
        String s = timeStr.trim().toUpperCase(Locale.ROOT);
        s = s.replace('.', ':').replace(',', ':');

        DateTimeParseException lastEx = null;
        for (DateTimeFormatter fmt : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(s, fmt);
            } catch (DateTimeParseException ex) {
                lastEx = ex;
            }
        }
        throw new DateTimeParseException("Formato de hora no soportado: " + timeStr, timeStr, 0);
    }


}


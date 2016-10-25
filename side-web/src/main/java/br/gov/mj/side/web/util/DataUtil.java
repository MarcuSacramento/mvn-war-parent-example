package br.gov.mj.side.web.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DataUtil {

    public static LocalDate converteDeDateToLocalDate(Date data) {
        if (data != null) {
            return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    public static Date converteLocalDateToDate(LocalDate data) {
        if (data != null) {
            return Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return null;

    }

    public static Date converteDeLocalDateTimeToDate(LocalDateTime data) {
        if (data != null) {
            return Date.from(data.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;

    }

    public static LocalDateTime converteDataInicial(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String strInicial = data + " 00:00:00";
        return LocalDateTime.parse(strInicial, formatter);
    }

    public static LocalDateTime converteDataFinal(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String strFinal = data + " 23:59:59";
        return LocalDateTime.parse(strFinal, formatter);
    }

    public static LocalDate converteDataDeStringParaLocalDate(String data, String pattern) {
        if (data == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(data, formatter);
    }

    public static String converteDataDeLocalDateParaString(LocalDate data, String pattern) {
        if (data == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return data.format(formatter);
    }

    // pattern dd/MM/yyyy HH:mm:ss
    public static String converteDataDeLocalDateParaString(LocalDateTime data, String pattern) {
        if (data == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return data.format(formatter);
    }

}

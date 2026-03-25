package com.Usuarios.Memoria.Util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final ZoneId MADAGASCAR_ZONE = ZoneId.of("Indian/Antananarivo");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String nowMadagascar() {

        return ZonedDateTime.now(MADAGASCAR_ZONE).format(FORMATTER);
    }

}

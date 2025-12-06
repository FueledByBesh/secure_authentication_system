package com.lostedin.authenticator.auth_service.util;

import java.util.UUID;

public class Helper {

    public static String getCodeString(int code) {
        if (code >= 200 && code < 300) return "2xx";
        if (code >= 300 && code < 400) return "3xx";
        if (code >= 400 && code < 500) return "4xx";
        if (code >= 500) return "5xx";
        return "1xx";
    }

    public static UUID generateRandomUUID() {
        return UUID.randomUUID();
    }

}

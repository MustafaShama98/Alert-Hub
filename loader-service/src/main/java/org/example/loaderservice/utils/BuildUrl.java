package org.example.loaderservice.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BuildUrl{
    public static String buildUrl( String service) {
        String baseUrl = "https://raw.githubusercontent.com/yones753/project_data_files/main/";

        // Format: 2025_04_12T13_30_00
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd'T'HH_mm_ss");

        String timestamp = now.format(formatter);
        String fileName = service + "_" + timestamp + ".csv";

        // Build full URL
        String fullUrl = baseUrl + service + "/" + fileName;

        return fullUrl;
    }
}

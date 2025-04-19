package org.example.loaderservice.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JiraFetcher {

    public static void getData() {
        String rawTextUrl = "https://raw.githubusercontent.com/yones753/project_data_files/main/jira/jira_2024_08_22T13_30_00.csv";

        try {
            URL url = new URL(rawTextUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            System.out.println("----- Raw Text Content -----");

            while ((line = reader.readLine()) != null) {
                System.out.println(line); // treat each line as plain text
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


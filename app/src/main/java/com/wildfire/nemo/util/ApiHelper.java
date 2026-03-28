package com.wildfire.nemo.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ApiHelper {
    public static String getRandomVocab() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://5f3e6db9-bdba-476a-9af0-bc78b708d9a9.mock.pstmn.io/vocabulary");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                Scanner sc = new Scanner(conn.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (sc.hasNext()) sb.append(sc.nextLine());
                sc.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }
}
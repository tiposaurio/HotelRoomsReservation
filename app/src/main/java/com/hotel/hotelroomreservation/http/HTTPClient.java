package com.hotel.hotelroomreservation.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hotel.hotelroomreservation.constants.Addresses;
import com.hotel.hotelroomreservation.constants.Constants;
import com.hotel.hotelroomreservation.utils.validations.ContextHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPClient {
    public static Bitmap getPhoto(String URL) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL reqUrl = new URL(URL);
            connection = ((HttpURLConnection) reqUrl.openConnection());
            connection.setDoInput(true);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static String getDBInfo(String fileName) {
        String serverUrl = Addresses.SERVER_URL + Addresses.INFO;
        String jsonInfo = "";

        try {
            URL url = new URL(serverUrl + Constants.FILE_NAME_PARAMETER + fileName);
            HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                try {
                    StringBuilder str = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        str.append(line);
                    }

                    jsonInfo = str.toString();

                } finally {
                    inputStream.close();
                    reader.close();
                    connection.disconnect();
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonInfo;
    }

    public static void setDBBookingsInfo(String reservation) {
        String serverUrl = Addresses.SERVER_URL + Addresses.INFO + Constants.FILE_NAME_PARAMETER + Constants.BOOKING;
        File file = new File(ContextHolder.getInstance().getContext().getFilesDir(),
                Constants.BOOKING + Constants.JSON_EXTENSION);

        try {
            FileWriter writer = new FileWriter(file);
            writer.append(reservation);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setFixedLengthStreamingMode(reservation.getBytes().length);

            OutputStream outputStream = connection.getOutputStream();
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();

            PrintWriter out = new PrintWriter(outputStream);
            out.print(inputStream);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
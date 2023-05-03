package com.example.iotv1app;

import java.io.*;
import java.net.*;

public class HttpManager {
    private static final String TAG = "HttpCommunicationManager";
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    public static void main(String[] args) {
        try {
            String serverUrl = "http://localhost:5000/upload";
            File imageFile = new File("C:\\Users\\Alvin\\Desktop\\iot\\proj\\hz_headshot.jpg");
            String response = HttpManager.uploadImage(serverUrl, imageFile, null);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String uploadImage(String url, File imageFile, String modelName) throws IOException {
        String boundary = "*****";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        int maxBufferSize = 1024 * 1024;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String result = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            dos = new DataOutputStream(conn.getOutputStream());

            // add image file data to request body
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + imageFile.getName() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            FileInputStream fileInputStream = new FileInputStream(imageFile);
            int bytesRead, totalBytesRead = 0;
            byte[] buffer = new byte[maxBufferSize];
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            fileInputStream.close();
            dos.writeBytes(lineEnd);


            // add model data to request body
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"model\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(modelName);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // end of multipart/form-data request body
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // read server response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = readStream(in);
        } finally {
            if (dos != null) {
                dos.flush();
                dos.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }


    public static String sendGetRequest(String url) throws IOException {
        HttpURLConnection conn = null;
        String result = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = readStream(in);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public static void downloadImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            conn.disconnect();
        }
    }

    public static String sendPostRequest(String url, String postData) throws IOException {
        HttpURLConnection conn = null;
        String result = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setDoOutput(true);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(postData.getBytes());
            out.flush();
            out.close();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = readStream(in);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    private static String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
}

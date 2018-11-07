package com.app.client;

import org.json.JSONObject;
import org.json.*;

import javax.ws.rs.PathParam;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client {
    public static void main(String[] argv) {
        doDeleteAllOwner();
        doPostOwner("Chandler", "Bing", "625-132-9100","Transponder", "mon777", "bing@gmail.com", "F123926", "9877434352");
        doPostOwner("Monica", "Geller", "415-320-9337","BestChef", "chand123", "m.geller@gmail.com", "F324567", "8749537482");
        doPostOwner("Ross", "Geller", "654-120-3445","DinoBoy", "rach456", "r.geller@gmail.com", "F334093", "1927483927");
        doGetAllOwner();

        doDeleteAllCar();
        doPostCar("5bc6fa76501bb83c44621b9b", "Honda", "CR-V","SUV", "2012", "1034523234", "4", "San Diego");
        doPostCar("5bc6fa76501bb83c44621b9c", "Toyota", "Camry","Sedan", "2008", "1894039458", "5", "Mountain View");
        doPostCar("5bc6fa76501bb83c44621b9d", "Honda", "s2000","Sports", "1999", "5687943054", "7", "San Francisco");
        //doGetAllCar("5bdbb248e498a8483cdcf395");

    }

    public static void doPostOwner(String firstName, String lastName, String phoneNumber, String username, String password, String email, String license, String accountNumber){
        try {
            URL url = new URL("http://localhost:8080/api/owners");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoInput(true);

            con.setDoOutput(true);

            JSONObject owner = new JSONObject();
            owner.put("firstName",firstName);
            owner.put("lastName",lastName);
            owner.put("phoneNumber",phoneNumber);
            owner.put("username",username);
            owner.put("password",password);
            owner.put("email",email);
            owner.put("license",license);
            owner.put("accountNumber",accountNumber);

            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            wr.write(owner.toString());
            wr.flush();

            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        }
        catch(Exception e) {
            e.printStackTrace();

        }

    }


    public static void doGetAllOwner() {
        try {
            URL url = new URL("http://localhost:8080/api/owners");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void doDeleteAllOwner() {
        try {
            URL url = new URL("http://localhost:8080/api/owners");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static void doPostCar(String ownerId, String carManufacturer, String carModel, String carType, String carYear, String carRegistration, String costOfCar, String carLocation){
        try {
            //String ownId = "";
            URL url = new URL("http://localhost:8080/api/owners/{ownerId}/cars");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoInput(true);

            con.setDoOutput(true);

            JSONObject car = new JSONObject();
            car.put("ownerId",ownerId);
            car.put("carManufacturer",carManufacturer);
            car.put("carModel",carModel);
            car.put("carType",carType);
            car.put("carYear",carYear);
            car.put("carRegistration",carRegistration);
            car.put("costOfCar",costOfCar);
            car.put("carLocation",carLocation);

            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            wr.write(car.toString());
            wr.flush();

            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        }
        catch(Exception e) {
            e.printStackTrace();

        }

    }


    public static void doGetAllCar(String ownerId) {
        try {
            URL url = new URL("http://localhost:8080/api/owners/{ownerId}/cars");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void doDeleteAllCar() {
        try {
            URL url = new URL("http://localhost:8080/api/owners/{ownerId}/cars");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println(content);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


}

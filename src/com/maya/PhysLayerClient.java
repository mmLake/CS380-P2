package com.maya;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Client listens on a PORT hardcoded to IP Address 18.221.102.182 PORT 38001. Client inputs an initial String that will represent
 * the user name. From there socket input is sent as chat. Client can also receive and display chat messages from other users.
 */

public class PhysLayerClient {
    private static final String HOST_IP = "18.221.102.182";
    private static final int PORT = 38002;
    private static final int preambleSize = 64;
    private static final int dataSize = 320;

    private Map<String, BitConverterValues> fourBitMap = new HashMap<String, BitConverterValues>();
    private String decodedValue = "";

    public static void main(String[] args) throws Exception {
        PhysLayerClient chatClient = new PhysLayerClient();
        chatClient.main();
    }

    public void instantiateMap(){
        fourBitMap.put("0000", new BitConverterValues("11110", "0"));
        fourBitMap.put("0001", new BitConverterValues("01001", "1"));
        fourBitMap.put("0010", new BitConverterValues("10100", "2"));
        fourBitMap.put("0011", new BitConverterValues("10101", "3"));
        fourBitMap.put("0100", new BitConverterValues("01010", "4"));
        fourBitMap.put("0101", new BitConverterValues("01011", "5"));
        fourBitMap.put("0110", new BitConverterValues("01110", "6"));
        fourBitMap.put("0111", new BitConverterValues("01111", "7"));
        fourBitMap.put("1000", new BitConverterValues("10010", "8"));
        fourBitMap.put("1001", new BitConverterValues("10011", "9"));
        fourBitMap.put("1010", new BitConverterValues("10110", "A"));
        fourBitMap.put("1011", new BitConverterValues("10111", "B"));
        fourBitMap.put("1100", new BitConverterValues("11010", "C"));
        fourBitMap.put("1101", new BitConverterValues("11011", "D"));
        fourBitMap.put("1110", new BitConverterValues("11100", "E"));
        fourBitMap.put("1111", new BitConverterValues("11101", "F"));
    }

    public void main(){
        instantiateMap();

        try (Socket socket = new Socket(HOST_IP, PORT)) {
            int number = 0;
            int baseline = 0;
            String fourBitVal = "";
            String decodedBitVal = "";

            //Read from server
            InputStream is = socket.getInputStream();

            //Write to server
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os, true, "UTF-8");

            //receive preamble
            for(int i = 0; i < preambleSize; i++){
                number = is.read();

                baseline += number;
            }

            //calculate baseline
            baseline /= preambleSize;
            System.out.println(" baseLine " + baseline);

            //receive data values and convert to five bit data String
            for(int i = 0; i < dataSize; i++){
                number = is.read();

                //convert from data to bit value
                if (number > baseline) {
                    fourBitVal += "1";
                } else if (number < baseline){
                    fourBitVal += "0";
                }

                //convert from four bit value to five bit value
                if (fourBitVal.length() == 4) {
                    String fiveBitVal = fourBitMap.get(fourBitVal).getFiveBitVal();
                    decodedBitVal += fiveBitVal;
                    fourBitVal = "";
                }
            }

            System.out.println("Decoded bit val: " + decodedBitVal + " length: " + decodedBitVal.length());

            //Send guess to server
            String line = "";
            out.println(line);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public class BitConverterValues{
        private String fiveBitVal;
        private String hexVal;

        public BitConverterValues(String fiveBitVal, String hexVal){
            this.fiveBitVal = fiveBitVal;
            this.hexVal = hexVal;
        }

        public String getFiveBitVal() {
            return fiveBitVal;
        }

        public String getHexVal() {
            return hexVal;
        }
    }
}
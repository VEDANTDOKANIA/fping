package com.company;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("fping");
        commands.add("-c");
        commands.add("3");
        commands.add("-t");
        commands.add("1000");
        commands.add("-q");
        commands.add("10.20.40.140");
        commands.add("google.com");
        File file = new File("src/iplist.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader br=new BufferedReader(fileReader);

        String line1;
        while((line1=br.readLine())!=null)
        {
            commands.add(line1);
        }
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);
        builder.redirectErrorStream(true);
        Process pb = builder.start();
        InputStream processInputStream = pb.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(processInputStream));
        String line;
        //StringBuilder builder1 = new StringBuilder();
        HashMap<String,HashMap<String,Object>>  CredentialMap = new HashMap<>();
      while ((line = reader.readLine()) != null) {
          HashMap<String,Object> DataMap = new HashMap<>();
          try{

              //System.out.print(((line.split(":"))[0]));
              DataMap.put("Packet_Received",((line.split(":"))[1]).split("=")[1].split(",")[0].split("/")[1]);
              DataMap.put("Packet_Send",((line.split(":"))[1]).split("=")[1].split(",")[0].split("/")[0]);
              DataMap.put("Packet_Percent_Lost",((line.split(":"))[1]).split("=")[1].split(",")[0].split("/")[2]);
              DataMap.put("Min_Latency",((line.split(":"))[1]).split("=")[2].split("/")[0]);
              DataMap.put("Max_Latency",((line.split(":"))[1]).split("=")[2].split("/")[1]);
              DataMap.put("Avg_Latency",((line.split(":"))[1]).split("=")[2].split("/")[2]);

              //System.out.print(((line.split(":"))[1]).split("=")[1].split(",")[0]);//phela ke 3 data
              //System.out.println(((line.split(":"))[1]).split("=")[2]); //last ke 3 data
          }
          catch (Exception e){
              DataMap.put("Min_Latency",0);
              DataMap.put("Max_Latency",0);
              DataMap.put("Avg_Latency",0);
          }
          if(DataMap.get("Packet_Percent_Lost").equals("0%"))
          {
              DataMap.put("IP_Status","Up");
          }else
          {
              DataMap.put("IP_Status","Down");
          }

          CredentialMap.put(((line.split(":"))[0]),DataMap);

        }
        System.out.println(CredentialMap);

    }
}

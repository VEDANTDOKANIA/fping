package com.company;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Task implements Runnable{

    List<String> Commands = new ArrayList<>();
    HashMap<String,HashMap<String,Object>>  CredentialMap = new HashMap<>();
    public HashMap<String,HashMap<String,Object>> getMap() {
        return CredentialMap;
    }
    public Task(List<String> commands) {
        Commands=commands ;
        Commands.add(0,"fping");
        Commands.add(1,"-c");
        Commands.add(2,"3");
        Commands.add(3,"-t");
        Commands.add(4,"1000");
        Commands.add(5,"-q");
    }


    @Override
    public void run() {

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(Commands);
        builder.redirectErrorStream(true);
        Process pb = null;
        try {
            pb = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream processInputStream = pb.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(processInputStream));
        String line = null;
        //StringBuilder builder1 = new StringBuilder();
        int count =0;

        while (true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            // count += Float.parseFloat((String) DataMap.get("Avg_Latency"));
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

public class Main {

    private static final  int MAX_T = 16;

    public static void main(String[] args) throws Exception {
        /*ArrayList<String> commands = new ArrayList<>();
        File file = new File("src/iplist.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader br=new BufferedReader(fileReader);
        String line1;
        while((line1=br.readLine())!=null)
        {
            commands.add(line1);
        }
        *//*HashMap<String,HashMap<String,Object>>  ipmap = new HashMap<>();
        ipmap = task(commands);
        System.out.println(ipmap);*//*
        *//*while (true){
            Thread.sleep(000);
            HashMap<String,HashMap<String,Object>>  ipmap = new HashMap<>();
            ipmap = task(commands);
            System.out.println(ipmap);
        }*//*


        ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
        Integer startindex =0;
        Integer endindexcounter = commands.size()/16;
            for(int i =0 ; i<16 ;i++){
            Integer endindex = startindex+ endindexcounter;
            if(endindex>commands.size()){
                endindex = commands.size()-1;
            }
            Runnable task = new Task(commands.subList(startindex,endindex));
            startindex = endindex+1;
            pool.execute(task);
            Thread.sleep(5);
        }
        pool.shutdown();*/

        while (true){
            Thread.sleep(20000);
            task();
            System.out.println("...........................................................................................................");
        }

    }

    public static  void task() throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<>();
        File file = new File("src/iplist.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader br=new BufferedReader(fileReader);
        String line1;
        while((line1=br.readLine())!=null)
        {
            commands.add(line1);
        }
        ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
        Integer startindex =0;
        Integer endindexcounter = commands.size()/16;
        for(int i =0 ; i<16 ;i++){
            Integer endindex = startindex+ endindexcounter;
            if(endindex>commands.size()){
                endindex = commands.size()-1;
            }

            Runnable task = new Task(commands.subList(startindex,endindex));
            startindex = endindex+1;
            pool.execute(task);
            Thread.sleep(5);
        }
        pool.shutdown();

    }

}

package org.jru;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class OrderBookSolution {

    private TreeMap<Integer, Integer> bidMap= new TreeMap<>();
    private TreeMap<Integer, Integer> askMap= new TreeMap<>();
    private StringBuilder data = new StringBuilder();


    public void run() {
        String order;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
            while (bufferedReader.ready()) {
                order = bufferedReader.readLine(); //считываем каждую строку из файла
                char action = order.charAt(0); //определяем в какой из методов передадим считанную строку
                switch (action) {
                    case 'o' -> remove(order);
                    case 'u' -> update(order);
                    case 'q' -> query(order);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.txt"))) {
            bufferedWriter.write(data.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(String order) {
        String[] splitData = split(order);
        int price = Integer.parseInt(splitData[1]);
        int size = Integer.parseInt(splitData[2]);
        TreeMap<Integer, Integer> orders = splitData[3].equals("ask") ? askMap : bidMap;

        if (size == 0) {
            orders.remove(price);
        } else {
           orders.put(price, size);
        }
    }

    // если получаем market order
    private void remove(String order) {
        String[] marketOrder = split(order);
        int marketOrderSize = Integer.parseInt(marketOrder[2]);
        Map.Entry<Integer, Integer> orderEntry;
        if (marketOrder[1].equals("buy") & askMap.size() !=0) {
            do {
                orderEntry = askMap.firstEntry();
                if (orderEntry.getValue() < marketOrderSize) {
                    askMap.remove(orderEntry.getKey());
                    marketOrderSize -= orderEntry.getValue();
                } else {
                    askMap.put(orderEntry.getKey(), orderEntry.getValue() - marketOrderSize);
                    return;
                }
            } while (true);
        } else if (bidMap.size() != 0){
            do {
                orderEntry = bidMap.lastEntry();
                if (orderEntry.getValue() < marketOrderSize ) {
                    bidMap.remove(orderEntry.getKey());
                    marketOrderSize -= orderEntry.getValue();
                } else {
                    bidMap.put(orderEntry.getKey(), orderEntry.getValue() - marketOrderSize);
                    return;
                }
            } while (true);
        }
    }

    private void query(String order) {
        String[] splitData = split(order);
        String command = splitData[1];
        switch (command) {
            case "best_bid" : {
                Map.Entry<Integer, Integer> bestBid = bidMap.lastEntry();
                if (bestBid != null) {
                    data.append(bestBid.getKey()).append(",").append(bestBid.getValue()).append("\n");
                }
                break;
            }
            case "best_ask" : {
                Map.Entry<Integer, Integer> bestAsk = askMap.firstEntry();
                if (bestAsk != null) {
                    data.append(bestAsk.getKey()).append(",").append(bestAsk.getValue()).append("\n");
                }
                break;
            }
            case "size" : {
                int price = Integer.parseInt(splitData[2]);
                Integer size = askMap.containsKey(price) ? askMap.get(price) : bidMap.get(price);
                if (size == null) {
                    size =0;
                }
                data.append(size).append("\n");
            }
        }
    }

    private String[] split(String order) {
        String[] array = new String[4];
        int begin = 0;
        int index = 0;
        for (int i = 0; i <order.length(); i++) {
            if (order.charAt(i) ==',') {
                array[index++] = order.substring(begin, i);
                begin = i+1;
            }
        }
        array[index] = order.substring(begin);
        return array;
    }
}

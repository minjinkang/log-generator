package com.fastcampus.clickstream;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    // 실제 로그를 생성할 사용자 유저 수 생성, 스레드 지속시간 5분
    static int userNum = 15;
    static int durationSeconds = 300;
    // ip 중복제거 
    static Set<String> ipSet = new HashSet<>();
    // ip 주소 랜덤 생성
    static Random rand = new Random();

    // 스레드 생성과 실행
    public static void main(String[] args) { 
        // 스레드 종료를 기다리는 로직
        CountDownLatch latch = new CountDownLatch(userNum);
        ExecutorService executor = Executors.newFixedThreadPool(userNum);
        // 실제 유저 수 만큼 스레드를 생성해서 실행하는 로직
        IntStream.range(0, userNum).forEach(i -> {
            // ip주소 선언
            String ipAddr = getIpAddr();
            // 스레드 실행
            executor.execute(new LogGenerator(latch, ipAddr, UUID.randomUUID().toString(), durationSeconds));
        });
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    private static String getIpAddr() {
        while (true) { // 중복되는 ip가 생성되었다면 루프
            // 0~255 주소 생성
            String ipAddr = "192.168.0." + rand.nextInt(256);
            // ipSet에 포함되어있지 않다면 주소 추가하고 리턴
            if (!ipSet.contains(ipAddr)) {
                ipSet.add(ipAddr);
                return ipAddr;
            }
        }
    }
}
package org.example;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class OlympicRunner extends Thread {
    static Random random = new Random();
    protected volatile AtomicInteger distance = new AtomicInteger(0);

    String country_name;
    private GameManger gameManger;


    public OlympicRunner(String name, String country_name,GameManger gameManger) {
        super(name);
        this.country_name = country_name;
        this.gameManger = gameManger;
    }

    @Override
    public void run() {
        while (distance.get() < 4500 && !gameManger.isGameOver()) {
            int currentMeter = random.nextInt(300) + 1;
            distance.set(distance.get() + currentMeter);
            if (distance.get() > 100){
                    if (gameManger.checkIfSameDistance(distance.get())){
                        this.distance.set(0);
                    }
                    else {
                        gameManger.integerQueue.add(distance.get());
                    }
            }
            System.out.println(Thread.currentThread().getName() + " random " + distance.get());
                try {
                    Thread.sleep(random.nextInt(100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
        if (distance.get() > 4500) {
            gameManger.setGameOver(new AtomicBoolean(true));
            System.out.println(Thread.currentThread().getName() + " " + this.country_name + " Finish the race first");
        }
    }
}

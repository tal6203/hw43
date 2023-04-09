package org.example;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class GameManger {
    private AtomicBoolean gameOver = new AtomicBoolean(false);
    public volatile Queue<Integer> integerQueue = new ConcurrentLinkedQueue<>();


    public boolean checkIfSameDistance(int num){
        return integerQueue.contains(num);
    }
    public void setGameOver(AtomicBoolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver.get();
    }
}

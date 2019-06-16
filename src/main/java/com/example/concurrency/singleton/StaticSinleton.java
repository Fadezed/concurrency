package com.example.concurrency.singleton;

public class StaticSinleton {

    private StaticSinleton(){
    }
    public static class StaticSingletonHolder{
        private static StaticSinleton singleton= new StaticSinleton();
    }
    public static StaticSinleton getInstance(){
        return StaticSingletonHolder.singleton;
    }
}

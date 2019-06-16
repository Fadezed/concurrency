package com.example.concurrency.singleton;

public class StaticSingleton {

    private StaticSingleton(){
    }
    public static class StaticSingletonHolder{
        private static StaticSingleton singleton= new StaticSingleton();
    }
    public static StaticSingleton getInstance(){
        return StaticSingletonHolder.singleton;
    }
}

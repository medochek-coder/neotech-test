package com.kulygina.neotechdev.service;

public interface TimeStorageService {
    void saveDataEverySecond();
    Runnable createEverySecondCachingDataTask();
    void tryToSaveEverySecond();
    void tryToSaveDataInDatabase();
    void printAll();
}

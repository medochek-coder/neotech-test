package com.kulygina.neotechdev.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import com.kulygina.neotechdev.model.TimeStorage;
import com.kulygina.neotechdev.repository.TimeStorageRepository;
import com.kulygina.neotechdev.service.TimeStorageService;

@Service
public class TimeStorageServiceImpl implements TimeStorageService {

    // Our cache
    private static List<TimeStorage> cache = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    private TimeStorageRepository timeStorageRepository;

    @Override
    public void saveDataEverySecond() {
        // Here we put data in cache every second
        Runnable everySecondCachingDataTask = createEverySecondCachingDataTask();

        // Start task of saving data in cache
        new Thread(everySecondCachingDataTask).start();

        // Here we try to write data in database from our cache
        tryToSaveEverySecond();
    }

    @Override
    public void printAll() {
        List<TimeStorage> all = timeStorageRepository.findAll();
        System.out.println("Storaged data:");
        all.forEach(timeStorage -> System.out.println(timeStorage.getSavedTime()));
    }

    public Runnable createEverySecondCachingDataTask() {
        return () -> {
            while (true) {
                cache.add(TimeStorage.builder()
                        .savedTime(LocalDateTime.now())
                        .build());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Process has been interrupted!");
                }
            }
        };
    }

    public void tryToSaveEverySecond() {
        while (true) {
            tryToSaveDataInDatabase();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Process has been interrupted!");
            }
        }
    }

    public void tryToSaveDataInDatabase() {
        try {
            // We take all elements from our cache at the current moment
            List<TimeStorage> current = new ArrayList<>(cache);

            // Try to write data in database
            timeStorageRepository.saveAll(current);
            System.out.println("Database connection has been successfully established");
            System.out.println("Saved data: " + current);

            // Remove saved data from out cache
            cache.removeAll(current);
        } catch (CannotCreateTransactionException e) {
            System.out.println("Database connection lost!");
            System.out.println("Data stored in cache...");
        }
    }

    public List<TimeStorage> getCache() {
        return cache;
    }
}

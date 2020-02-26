package com.kulygina.neotechdev.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.kulygina.neotechdev.model.TimeStorage;
import com.kulygina.neotechdev.repository.TimeStorageRepository;

@DisplayName("TimeStorageService should ")
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import(TimeStorageServiceImpl.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TimeStorageServiceImplTest {
    @Autowired
    private TimeStorageServiceImpl service;

    @Autowired
    private TimeStorageRepository timeStorageRepository;

    @Test
    @DisplayName("should store data from cache in database")
    public void shouldStoreDataFromCacheToDatabase() throws InterruptedException {
        Runnable everySecondCachingDataTask = service.createEverySecondCachingDataTask();
        new Thread(everySecondCachingDataTask).start();

        new Thread(() -> service.tryToSaveEverySecond()).start();
        Thread.sleep(10000L);

        List<TimeStorage> all = timeStorageRepository.findAll();
        assertThat(all).isNotEmpty();
        assertThat(service.getCache()).doesNotContainSequence(all);
    }

    @Test
    @DisplayName("add data to cache")
    public void shouldAddDataToCache() throws InterruptedException {
        Runnable everySecondCachingDataTask = service.createEverySecondCachingDataTask();
        new Thread(everySecondCachingDataTask).start();

        Thread.sleep(5000L);

        assertThat(service.getCache()).isNotEmpty();
    }

    @Test
    @DisplayName("correctly save data in database if database overloaded")
    public void shouldCorrectlySaveDataIfDatabaseOverloaded() throws InterruptedException {
        Runnable everySecondCachingDataTask = service.createEverySecondCachingDataTask();
        new Thread(everySecondCachingDataTask).start();

        new Thread(() -> {
            try {
                Thread.sleep(5000L); // simulate high database load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            service.tryToSaveEverySecond();
        }).start();
        Thread.sleep(10000L);

        List<TimeStorage> all = timeStorageRepository.findAll();
        assertThat(all.isEmpty()).isFalse();
        for (int i = 1; i < all.size(); i++) {
            assertThat(all.get(i).getSavedTime().isAfter(all.get(i - 1).getSavedTime()));
        }
    }
}

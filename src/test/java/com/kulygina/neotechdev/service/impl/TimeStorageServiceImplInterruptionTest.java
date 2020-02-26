package com.kulygina.neotechdev.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.CannotCreateTransactionException;

import com.kulygina.neotechdev.repository.TimeStorageRepository;

@DisplayName("TimeStorageService should ")
@ExtendWith(SpringExtension.class)
@Import(TimeStorageServiceImpl.class)
public class TimeStorageServiceImplInterruptionTest {
    @Autowired
    private TimeStorageServiceImpl service;

    @MockBean
    private TimeStorageRepository timeStorageRepository;

    @Test
    @DisplayName("remain data in the cache even if the record failed")
    public void shouldRemainDataInCache() throws InterruptedException {
        Runnable everySecondCachingDataTask = service.createEverySecondCachingDataTask();
        new Thread(everySecondCachingDataTask).start();

        doThrow(new CannotCreateTransactionException("")).when(timeStorageRepository)
                .saveAll(anyList());
        new Thread(() -> service.tryToSaveEverySecond()).start();
        Thread.sleep(10000L);

        assertThat(service.getCache()).isNotEmpty();
    }
}

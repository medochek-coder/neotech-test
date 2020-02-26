package com.kulygina.neotechdev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kulygina.neotechdev.model.TimeStorage;

public interface TimeStorageRepository extends JpaRepository<TimeStorage, Long> {
    List<TimeStorage> findAllByOrderByIdAsc();
}

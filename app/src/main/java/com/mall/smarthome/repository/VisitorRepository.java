package com.mall.smarthome.repository;

import com.mall.smarthome.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  VisitorRepository extends JpaRepository<Visitor, Long> {
    Visitor findByIp(String ip);
}
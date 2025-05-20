package com.example.cdtn.controller;

import com.example.cdtn.service.impl.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlashSaleScheduler {
    private final FlashSaleService flashSaleService;

    @Scheduled(fixedRate = 5 * 60 * 1000) // mỗi 5 phút
    public void updateExpiredFlashSales() {
        flashSaleService.handleExpiredFlashSales();
    }

    @Scheduled(fixedRate = 1 * 60 * 1000) // mỗi 1 phút
    public void activateUpcomingFlashSales() {
        flashSaleService.activateUpcomingFlashSales();
    }
}

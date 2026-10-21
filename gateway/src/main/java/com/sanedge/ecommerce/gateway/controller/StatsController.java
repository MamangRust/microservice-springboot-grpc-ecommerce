package com.sanedge.ecommerce.gateway.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.common.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.gateway.dto.responses.stats.StatItemResponse;
import com.sanedge.ecommerce.gateway.mapper.ProtoMapper;
import com.sanedge.ecommerce.pb.stats.StatsReaderServiceGrpc;
import com.sanedge.ecommerce.pb.stats.StatsRequest;

import lombok.RequiredArgsConstructor;

/**
 * REST facade over the ClickHouse-backed {@code stats-reader} gRPC service.
 * All analytics previously served per-domain are now read from here.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsReaderServiceGrpc.StatsReaderServiceBlockingStub statsReaderStub;

    @GetMapping("/monthly-revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> findMonthlyTotalRevenue(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        var resp = statsReaderStub.findMonthlyTotalRevenue(StatsRequest.newBuilder()
                .setYear(nvl(year))
                .setMonth(nvl(month))
                .build());
        return ResponseEntity.ok(ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.statItems(resp.getDataList()))
                .build());
    }

    @GetMapping("/yearly-revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> findYearlyTotalRevenue(
            @RequestParam(required = false) Integer year) {
        var resp = statsReaderStub.findYearlyTotalRevenue(StatsRequest.newBuilder()
                .setYear(nvl(year))
                .build());
        return ResponseEntity.ok(ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.statItems(resp.getDataList()))
                .build());
    }

    @GetMapping("/merchant-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> findMerchantSales(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer year) {
        var resp = statsReaderStub.findMerchantSales(StatsRequest.newBuilder()
                .setMerchantId(nvl(merchantId))
                .setYear(nvl(year))
                .build());
        return ResponseEntity.ok(ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.statItems(resp.getDataList()))
                .build());
    }

    @GetMapping("/product-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> findProductSales(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer year) {
        var resp = statsReaderStub.findProductSales(StatsRequest.newBuilder()
                .setProductId(nvl(productId))
                .setYear(nvl(year))
                .build());
        return ResponseEntity.ok(ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.statItems(resp.getDataList()))
                .build());
    }

    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static long nvl(Long value) {
        return value == null ? 0L : value;
    }
}

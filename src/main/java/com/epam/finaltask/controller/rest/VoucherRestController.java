package com.epam.finaltask.controller.rest;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.dto.VoucherStatusDTO;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherRestController {

    private static final Logger logger = LoggerFactory.getLogger(VoucherRestController.class);

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, String>> order(@Valid @RequestBody VoucherDTO voucherDTO){
        logger.info("Received request to create voucher: {}", voucherDTO);
        voucherService.order(voucherDTO);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "Voucher is successfully created"
        );
        logger.info("Voucher created successfully for userId: {}", voucherDTO.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/cancel/{voucherId}")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable String voucherId){
        logger.info("Received request to cancel voucher with ID: {}", voucherId);
        voucherService.cancelOrder(voucherId);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "Voucher now is CANCELED"
        );
        logger.info("Voucher with ID {} successfully canceled", voucherId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/pay/{voucherId}")
    public ResponseEntity<Map<String, String>> payForVoucher(@PathVariable String voucherId){
        logger.info("Received request to pay for voucher with ID: {}", voucherId);
        voucherService.payOrder(voucherId);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "Voucher successfully payed"
        );
        logger.info("Voucher with ID {} successfully paid", voucherId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status/change/{voucherId}")
    public ResponseEntity<Map<String, String>> changeVoucherStatus(@PathVariable String voucherId,
                                                                   @Valid @RequestBody VoucherStatusDTO voucherStatusDTO){

        logger.info("Received request to change status of voucher with ID: {} to {}", voucherId, voucherStatusDTO.getVoucherStatus());
        VoucherStatus voucherStatus = VoucherStatus.valueOf(voucherStatusDTO.getVoucherStatus());
        voucherService.changeVoucherStatus(voucherId, voucherStatus);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "Voucher now is " + voucherStatusDTO.getVoucherStatus()
        );
        logger.info("Voucher with ID {} successfully changed status to {}", voucherId, voucherStatusDTO.getVoucherStatus());
        return ResponseEntity.ok(response);
    }

}

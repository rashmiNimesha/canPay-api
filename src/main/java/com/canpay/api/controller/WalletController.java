package com.canpay.api.controller;

import com.canpay.api.dto.UserWalletBalanceDto;
import com.canpay.api.entity.RechargeTransaction;
import com.canpay.api.entity.User;
import com.canpay.api.service.implementation.WalletServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletServiceImpl walletService;

    public WalletController(WalletServiceImpl walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/recharge")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> rechargeWallet(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        double amount = Double.parseDouble(request.get("amount"));

        User updatedUser = walletService.rechargeWallet(email, amount);
        UserWalletBalanceDto walletBalancedto = new UserWalletBalanceDto(updatedUser.getWalletBalance());

        System.out.println("wallet recharged ");
        return ResponseEntity.ok(Map.of(
                "message", "Wallet recharged successfully",
                "balance", walletBalancedto
        ));
    }


    @GetMapping("/balance")
    public ResponseEntity<?> getWalletBalance(@RequestParam String email) {
        double balance = walletService.getWalletBalance(email);
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getRechargeHistory(@RequestParam String email) {
        List<RechargeTransaction> history = walletService.getRechargeHistory(email);
        return ResponseEntity.ok(history);
    }
}

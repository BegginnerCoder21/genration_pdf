package com.etat_charge.controller;

import com.etat_charge.dto.CalculBalanceDto;
import com.etat_charge.service.impl.CalculBalanceServiceImpl;
import com.itextpdf.text.DocumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RequestMapping("calcul-balance")
@RestController
public class CalculBalanceController {

    private final CalculBalanceServiceImpl calculBalanceServiceImpl;

    public CalculBalanceController(CalculBalanceServiceImpl calculBalanceServiceImpl) {
        this.calculBalanceServiceImpl = calculBalanceServiceImpl;
    }

    @GetMapping("/calcul-for-n")
    public ResponseEntity<List<CalculBalanceDto>> calculBalanceForN() throws DocumentException, FileNotFoundException {
        List< CalculBalanceDto> calculBalanceDtoList = this.calculBalanceServiceImpl.calculBalanceForN();
        return ResponseEntity.ok(calculBalanceDtoList);
    }
}

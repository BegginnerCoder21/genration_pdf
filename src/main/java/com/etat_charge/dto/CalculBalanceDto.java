package com.etat_charge.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculBalanceDto {

    private String accountNumber;
    private String description;
    private BigDecimal sumDebitBalance;
}

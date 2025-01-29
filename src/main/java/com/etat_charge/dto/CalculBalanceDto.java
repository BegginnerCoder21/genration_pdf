package com.etat_charge.dto;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculBalanceDto {

    private String accountNumber;
    private String description;
    private BigInteger sumDebitBalance;
}

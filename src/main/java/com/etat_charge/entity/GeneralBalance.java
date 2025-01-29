package com.etat_charge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Entity
@Table(name = "general_balance")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class GeneralBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer account;
    private String description;
    private BigInteger openingBalance;
    private BigInteger debit;
    private BigInteger credit;
    private BigInteger periodBalance;
    private BigInteger endCreditBalance;
    private BigInteger endDebitBalance;
}

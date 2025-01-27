package com.etat_charge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bilan_general")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class GeneralBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer compte;
    private String description;
    private BigDecimal openingBalance;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal periodBalance;
    private BigDecimal endCreditBalance;
    private BigDecimal endDebitBalance;
}

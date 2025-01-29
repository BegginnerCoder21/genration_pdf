package com.etat_charge.service;

import com.etat_charge.dto.CalculBalanceDto;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface CalculBalanceService {

    public List<CalculBalanceDto> calculBalanceForN() throws DocumentException, FileNotFoundException;
    public CalculBalanceDto calcul(Map.Entry<String, String> contentCharge);
    public void generateAccountBalancePdf(List<CalculBalanceDto> calculBalanceDtoList, Map<Integer, BigInteger> totalDebit) throws FileNotFoundException, DocumentException;
}

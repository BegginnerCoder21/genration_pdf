package com.etat_charge.service.impl;

import com.etat_charge.dto.CalculBalanceDto;
import com.etat_charge.entity.GeneralBalance;
import com.etat_charge.repository.GeneralBalanceRepository;
import com.etat_charge.service.CalculBalanceService;
import com.etat_charge.utils.ChargesUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class CalculBalanceServiceImpl implements CalculBalanceService {

    private final GeneralBalanceRepository generalBalanceRepository;
    private static final String FILE = "/home/teguera-aboubacar/accountBalance.pdf";

    public CalculBalanceServiceImpl(GeneralBalanceRepository generalBalanceRepository) {
        this.generalBalanceRepository = generalBalanceRepository;
    }

    public List<CalculBalanceDto> calculBalanceForN() throws DocumentException, FileNotFoundException {
        Map<String, String> charges = ChargesUtils.genrateCharge();
        List<CalculBalanceDto> calculBalanceDtoList = new ArrayList<>();
        for (Map.Entry<String, String> contentCharge : charges.entrySet()) {

            CalculBalanceDto calculBalanceDto = this.calcul(contentCharge);
            calculBalanceDtoList.add(calculBalanceDto);
            //System.out.println("Code: " + entry.getKey() + " -> " + entry.getValue());
        }

        this.generateAccountBalancePdf(calculBalanceDtoList);

        return calculBalanceDtoList;
    }

    public CalculBalanceDto calcul(Map.Entry<String, String> contentCharge)
    {
        List<GeneralBalance> generalsStartingWithSixBalances = this.generalBalanceRepository.findByCompteStartingWithSix();

        BigDecimal sumBalance = BigDecimal.ZERO;
        for (GeneralBalance generalBalance : generalsStartingWithSixBalances)
        {
            String stringCompte = String.valueOf(generalBalance.getCompte());

            int lengthCode = contentCharge.getKey().length();
            if(contentCharge.getKey().equals(stringCompte.substring(0,lengthCode)))
            {
                sumBalance = sumBalance.add(generalBalance.getEndDebitBalance());
                System.out.println("Code: " + contentCharge.getKey() + " -> " + stringCompte + " value: " + generalBalance.getEndDebitBalance());
            }


            //log.info("rien ne correspond");
        }
        System.out.println(String.format("resultat sum pour %s est: %s", contentCharge.getKey(), sumBalance));

        return CalculBalanceDto.builder()
                .accountNumber(contentCharge.getKey())
                .description(contentCharge.getValue())
                .sumDebitBalance(sumBalance)
                .build();
    }

    public void generateAccountBalancePdf(List<CalculBalanceDto> calculBalanceDtoList) throws FileNotFoundException, DocumentException {


        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize, 36, 36, 36, 36); // Marges ajoutées
        log.info("Paramètrage de la taille du document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable accountInfoTable = new PdfPTable(1);
        PdfPCell resultAccount = new PdfPCell(new Phrase("COMPTE DE RESULTAT SYSTEME NORMAL", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        resultAccount.setBorder(0);
        resultAccount.setBackgroundColor(BaseColor.LIGHT_GRAY);
        resultAccount.setPadding(10f);

        resultAccount.setHorizontalAlignment(Element.ALIGN_CENTER);
        resultAccount.setVerticalAlignment(Element.ALIGN_MIDDLE);

        accountInfoTable.addCell(resultAccount);

        PdfPCell usualAcronym = new PdfPCell(new Phrase("Sigle usuel: CNPS", new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
        usualAcronym.setBorder(0);
        usualAcronym.setPadding(5f);
        accountInfoTable.addCell(usualAcronym);
        document.add(accountInfoTable);

        PdfPTable durationInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Durée (en mois): 12", new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
        customerInfo.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        durationInfo.addCell(customerInfo);
        durationInfo.addCell(space);
        document.add(durationInfo);
        document.add(Chunk.NEWLINE);

        PdfPTable accountTable = new PdfPTable(4);
        accountTable.setWidthPercentage(100);
        accountTable.setSpacingBefore(10f);

        PdfPCell account = new PdfPCell(new Phrase("Compte", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        account.setBackgroundColor(BaseColor.GRAY);
        account.setPadding(8f);
        account.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell description = new PdfPCell(new Phrase("Description", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        description.setBackgroundColor(BaseColor.GRAY);
        description.setPadding(8f);
        description.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell balanceForN = new PdfPCell(new Phrase("Solde N", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        balanceForN.setBackgroundColor(BaseColor.GRAY);
        balanceForN.setPadding(8f);
        balanceForN.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell balance = new PdfPCell(new Phrase("Solde N+1", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        balance.setBackgroundColor(BaseColor.GRAY);
        balance.setPadding(8f);
        balance.setHorizontalAlignment(Element.ALIGN_CENTER);

        accountTable.addCell(account);
        accountTable.addCell(description);
        accountTable.addCell(balanceForN);
        accountTable.addCell(balance);

        calculBalanceDtoList.forEach(bilan -> {
            accountTable.addCell(new PdfPCell(new Phrase(bilan.getAccountNumber(), new Font(Font.FontFamily.HELVETICA, 10))));
            accountTable.addCell(new PdfPCell(new Phrase(bilan.getDescription(), new Font(Font.FontFamily.HELVETICA, 10))));
            accountTable.addCell(new PdfPCell(new Phrase(bilan.getSumDebitBalance().toString(), new Font(Font.FontFamily.HELVETICA, 10))));
            accountTable.addCell(new PdfPCell(new Phrase("", new Font(Font.FontFamily.HELVETICA, 10))));
        });

        document.add(accountTable);
        document.close();

    }
}

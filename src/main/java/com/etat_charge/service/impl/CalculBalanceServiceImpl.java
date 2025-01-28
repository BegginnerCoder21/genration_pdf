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

        }

        this.generateAccountBalancePdf(calculBalanceDtoList);

        return calculBalanceDtoList;
    }

    public CalculBalanceDto calcul(Map.Entry<String, String> contentCharge)
    {
        List<GeneralBalance> generalsStartingWithSixBalances = this.generalBalanceRepository.findByCompteStartingWithSix();
        log.info("Code: {} libelle: {} ",contentCharge.getKey(), contentCharge.getValue());
        BigDecimal sumBalance = BigDecimal.ZERO;
        for (GeneralBalance generalBalance : generalsStartingWithSixBalances)
        {
            String stringCompte = String.valueOf(generalBalance.getCompte());

            int lengthCode = contentCharge.getKey().length();
            if(contentCharge.getKey().equals(stringCompte.substring(0,lengthCode)))
            {
                sumBalance = sumBalance.add(generalBalance.getEndDebitBalance());
                //log.info("Code: {} -> {} value: {}" , contentCharge.getKey(), stringCompte, generalBalance.getEndDebitBalance());
            }

        }
        log.info("resultat sum pour {} est: {}", contentCharge.getKey(), sumBalance);

        return CalculBalanceDto.builder()
                .accountNumber(contentCharge.getKey())
                .description(contentCharge.getValue())
                .sumDebitBalance(sumBalance)
                .build();
    }

    public void generateAccountBalancePdf(List<CalculBalanceDto> calculBalanceDtoList) throws FileNotFoundException, DocumentException {

        Rectangle pageSize = PageSize.A4; // Taille de la page
        Document document = new Document(pageSize, 36, 36, 36, 36); // Marges
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell(new Phrase("COMPTE DE RESULTAT SYSTEME NORMAL",
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setBorderWidth(1);
        titleCell.setPadding(10f);
        titleTable.addCell(titleCell);

        // Ligne pour le numéro de page
        PdfPCell pageNumberCell = new PdfPCell(new Phrase("2/4",
                new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
        pageNumberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pageNumberCell.setBorderWidth(1);
        pageNumberCell.setPadding(5f);
        titleTable.addCell(pageNumberCell);

        document.add(titleTable);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);
        infoTable.setWidths(new float[]{2, 3});

        addCellToTable(infoTable, "Dénomination sociale de :", Element.ALIGN_LEFT, true);
        addCellToTable(infoTable, "Caisse Nationale de Prévoyance Sociale", Element.ALIGN_LEFT, false);

        addCellToTable(infoTable, "Adresse :", Element.ALIGN_LEFT, true);
        addCellToTable(infoTable, "01 BP 317 ABIDJAN Côte d'Ivoire Tel:(225) 27 20 252 100", Element.ALIGN_LEFT, false);

        addCellToTable(infoTable, "N° d'identification fiscale :", Element.ALIGN_LEFT, true);
        addCellToTable(infoTable, "CC:5000810F-DGE", Element.ALIGN_LEFT, false);

        addCellToTable(infoTable, "Date :", Element.ALIGN_LEFT, true);
        addCellToTable(infoTable, "Décembre 2020", Element.ALIGN_LEFT, false);
        addCellToTable(infoTable, "Durée (en mois) :", Element.ALIGN_LEFT, true);
        addCellToTable(infoTable, "12", Element.ALIGN_LEFT, false);

        document.add(infoTable);

        PdfPTable accountTable = new PdfPTable(4);
        accountTable.setWidthPercentage(100);
        accountTable.setSpacingBefore(10f);
        accountTable.setWidths(new float[]{2, 4, 2, 2});

        // En-tête du tableau
        addTableHeader(accountTable, "Ref");
        addTableHeader(accountTable, "CHARGES");
        addTableHeader(accountTable, "décembre-20");
        addTableHeader(accountTable, "décembre-19");

        // Contenu du tableau
        for (CalculBalanceDto dto : calculBalanceDtoList) {
            addCellToTable(accountTable, dto.getAccountNumber(), Element.ALIGN_CENTER, false);
            addCellToTable(accountTable, dto.getDescription(), Element.ALIGN_LEFT, false);
            addCellToTable(accountTable, dto.getSumDebitBalance().toString(), Element.ALIGN_RIGHT, false);
            addCellToTable(accountTable, "", Element.ALIGN_RIGHT, false);
        }

        document.add(accountTable);
        document.close();
    }

    // permet d'ajouter des entêtes au tableau
    private void addTableHeader(PdfPTable table, String headerTitle) {
        BaseColor orangeYellow = new BaseColor(255, 165, 0);
        BaseColor lightOrangeYellow = new BaseColor(255, 223, 186);
        BaseColor tableHeaderColor = new BaseColor(255, 217, 102);
        PdfPCell header = new PdfPCell(new Phrase(headerTitle,
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK)));
        header.setBackgroundColor(tableHeaderColor);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(8f);
        table.addCell(header);
    }


    private void addCellToTable(PdfPTable table, String content, int alignment, boolean bold) {
        Font font = bold
                ? new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)
                : new Font(Font.FontFamily.HELVETICA, 10);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorderWidth(0.5f);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5f);
        table.addCell(cell);
    }


}

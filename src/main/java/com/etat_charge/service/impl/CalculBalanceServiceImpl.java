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
import java.math.BigInteger;
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
        List<GeneralBalance> generalsStartingWithSixBalances = this.generalBalanceRepository.findByAccountStartingWithSix();
        log.info("Code: {} libelle: {} ",contentCharge.getKey(), contentCharge.getValue());
        BigInteger sumBalance = BigInteger.ZERO;
        for (GeneralBalance generalBalance : generalsStartingWithSixBalances)
        {
            String stringCompte = String.valueOf(generalBalance.getAccount());

            int lengthCode = contentCharge.getKey().length();
            if(contentCharge.getKey().equals(stringCompte.substring(0,lengthCode)))
            {
                sumBalance = sumBalance.add(generalBalance.getEndDebitBalance());

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

        // Section: Rectangle contenant "COMPTE DE RESULTAT"
        PdfPTable titleTable = new PdfPTable(2);
        titleTable.setWidthPercentage(100);
        titleTable.setWidths(new float[]{7, 1}); // Largeur pour les colonnes

        PdfPCell subtitleCell = new PdfPCell(new Phrase("COMPTE DE RESULTAT SYSTEME NORMAL", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        subtitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        subtitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        subtitleCell.setBorder(Rectangle.BOX);
        subtitleCell.setPadding(5f);
        titleTable.addCell(subtitleCell);

        PdfPCell pageBoxCell = new PdfPCell(new Phrase("1/4", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
        pageBoxCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pageBoxCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pageBoxCell.setBorder(Rectangle.BOX);
        pageBoxCell.setPadding(5f);
        titleTable.addCell(pageBoxCell);

        PdfPCell titleCell = new PdfPCell(new Phrase("COMPTE DE RESULTAT", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setColspan(2); // Prendre toute la largeur
        titleCell.setPadding(10f);
        titleTable.addCell(titleCell);

        // Ajouter le tableau des titres au document
        document.add(titleTable);

        // Section Informations Entreprise
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);
        infoTable.setWidths(new float[]{(float) 7 /2, 7, 3, 2});

        addCellToTable(infoTable, "Dénomination sociale de :", Element.ALIGN_LEFT, true, Rectangle.NO_BORDER, BaseColor.WHITE);
        addCellToTableWithUnderline(infoTable, "Caisse Nationale de Prévoyance Sociale", Element.ALIGN_LEFT, false, Rectangle.NO_BORDER);

        addCellToTable(infoTable, "Sigle usuel :", Element.ALIGN_LEFT, true, Rectangle.NO_BORDER,BaseColor.WHITE);
        addCellToTableWithUnderline(infoTable, "CNPS", Element.ALIGN_LEFT, false, Rectangle.NO_BORDER);

        addCellToTable(infoTable, "Adresse :", Element.ALIGN_LEFT, true, Rectangle.NO_BORDER, BaseColor.WHITE);
        addCellToTableWithUnderline(infoTable, "01 BP 317 ABIDJAN Côte d'Ivoire Tel:(225) 27 20 252 100", Element.ALIGN_LEFT, false, Rectangle.NO_BORDER);

        addCellToTable(infoTable, "Durée (en mois) :", Element.ALIGN_LEFT, true, Rectangle.NO_BORDER,BaseColor.WHITE);
        addCellToTableWithUnderline(infoTable, "12", Element.ALIGN_LEFT, false, Rectangle.NO_BORDER);

        addCellToTable(infoTable, "N° d'identification fiscale :", Element.ALIGN_LEFT, true, Rectangle.NO_BORDER,BaseColor.WHITE);
        addCellToTableWithUnderline(infoTable, "CC:5000810F-DGE", Element.ALIGN_LEFT, false, Rectangle.NO_BORDER);

        addCellToTable(infoTable, "Date :", Element.ALIGN_LEFT, true, Rectangle.NO_BORDER,BaseColor.WHITE);
        addCellToTableWithUnderline(infoTable, "Décembre 2020", Element.ALIGN_LEFT, false, Rectangle.NO_BORDER);

        document.add(infoTable);

        // Section du tableau de données
        PdfPTable accountTable = new PdfPTable(5);
        accountTable.setWidthPercentage(100);
        accountTable.setSpacingBefore(10f);
        accountTable.setWidths(new float[]{1, 6, 2, 2, 2});

        // En-tête
        addTableHeader(accountTable, "Réf");
        addTableHeader(accountTable, "CHARGES (1re partie)");
        addTableHeader(accountTable, "décembre-20");
        addTableHeader(accountTable, "décembre-19");
        addTableHeader(accountTable, "Variation %");

        // Contenu
        BaseColor backgroundColor = new BaseColor(255, 228, 206);
        addCellToTable(accountTable, "", Element.ALIGN_CENTER, false, Rectangle.BOX,backgroundColor);
        addCellToTable(accountTable, "ACTIVITES D'EXPLOITATION", Element.ALIGN_LEFT, true, Rectangle.BOX,backgroundColor);
        addCellToTable(accountTable, "", Element.ALIGN_RIGHT, false, Rectangle.BOX,backgroundColor);
        addCellToTable(accountTable, "", Element.ALIGN_RIGHT, false, Rectangle.BOX,backgroundColor);
        addCellToTable(accountTable, "", Element.ALIGN_RIGHT, false, Rectangle.BOX,backgroundColor);
        for (CalculBalanceDto dto : calculBalanceDtoList) {
            addCellToTable(accountTable, dto.getAccountNumber(), Element.ALIGN_CENTER, false, Rectangle.BOX,BaseColor.WHITE);
            addCellToTable(accountTable, dto.getDescription(), Element.ALIGN_LEFT, false, Rectangle.BOX,BaseColor.WHITE);
            addCellToTable(accountTable, ChargesUtils.formatetedNumber(dto.getSumDebitBalance()), Element.ALIGN_RIGHT, false, Rectangle.BOX,BaseColor.WHITE);
            addCellToTable(accountTable, "", Element.ALIGN_RIGHT, false, Rectangle.BOX,BaseColor.WHITE);
            addCellToTable(accountTable, "", Element.ALIGN_RIGHT, false, Rectangle.BOX,BaseColor.WHITE);
        }

        document.add(accountTable);
        document.close();
    }


    // Méthode pour ajouter des entêtes au tableau
    private void addTableHeader(PdfPTable table, String headerTitle) {
        BaseColor tableHeaderColor = new BaseColor(255, 217, 102); // Couleur jaune orangé clair
        PdfPCell header = new PdfPCell(new Phrase(headerTitle, new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK)));
        header.setBackgroundColor(tableHeaderColor);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPadding(8f);
        header.setBorder(Rectangle.BOX);
        table.addCell(header);
    }

    // Méthode pour ajouter des cellules au tableau avec soulignement
    private void addCellToTableWithUnderline(PdfPTable table, String content, int alignment, boolean bold, int borderStyle) {
        Font font = bold ? new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD) : new Font(Font.FontFamily.HELVETICA, 8);
        Chunk chunk = new Chunk(content, font);
        chunk.setUnderline(0.1f, -2f); // Soulignage
        PdfPCell cell = new PdfPCell(new Phrase(chunk));
        cell.setBorder(borderStyle);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    // Méthode pour ajouter des cellules au tableau
    private void addCellToTable(PdfPTable table, String content, int alignment, boolean bold, int borderStyle, BaseColor backgroundColor) {
        Font font = bold ? new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD) : new Font(Font.FontFamily.HELVETICA, 8);
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorder(borderStyle);
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        table.addCell(cell);
    }




}

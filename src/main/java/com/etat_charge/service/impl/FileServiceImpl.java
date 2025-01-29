package com.etat_charge.service.impl;

import com.etat_charge.entity.GeneralBalance;
import com.etat_charge.repository.GeneralBalanceRepository;
import com.etat_charge.service.FileService;
import com.etat_charge.utils.ChargesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private final GeneralBalanceRepository generalBalanceRepository;

    public FileServiceImpl(GeneralBalanceRepository generalBalanceRepository) {
        this.generalBalanceRepository = generalBalanceRepository;
    }

    @Value("${path.file}")
    private String pathFile;

    @Override
    public String readFile() throws IOException {

        log.info("Debut de la fonction");

        File excelFile = this.loadFile(this.pathFile);

        FileInputStream fis = new FileInputStream(excelFile);
        XSSFWorkbook workbook = this.openWorkbook(fis);

        XSSFSheet sheet = workbook.getSheetAt(0);
        this.readSheet(sheet);


        workbook.close();
        fis.close();

        return "Affichage du contenu du fichier excel effectué avec succès";

    }

    private File loadFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new IOException(String.format("Ce fichier: %s n'existe pas ", path));
        }
        log.info("Fichier chargé : {}", path);
        return file;
    }

    private XSSFWorkbook openWorkbook(FileInputStream fis) throws IOException {
        log.info("Ouverture du fichier Excel");
        return new XSSFWorkbook(fis);
    }

    private void readSheet(XSSFSheet sheet) {
        log.info("Début de la lecture de la feuille Excel");
        List<GeneralBalance> generalBalances = new ArrayList<>();

        int currentIndex = 0;
        for (Row row : sheet) {
            if(currentIndex++ == 0) continue;
            //Passage des lignes du fichier excel pour traitement
            GeneralBalance generalBalance = this.readRow(row);

            generalBalances.add(generalBalance);
        }

        this.generalBalanceRepository.saveAll(generalBalances);

        log.info("Fin de la lecture de la feuille Excel");
    }

    private GeneralBalance readRow(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        int cellIndex = 0;
        GeneralBalance generalBalance = new GeneralBalance();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            var cellValue = getCellValueAsString(cell);


            try {
                switch (cellIndex) {
                    case 0 -> {
                        double numericValue = Double.parseDouble(cellValue);
                        generalBalance.setAccount((int) numericValue);
                    }
                    case 1 -> generalBalance.setDescription(cellValue);
                    case 2 -> generalBalance.setOpeningBalance(ChargesUtils.convertStringAsBigInteger(cellValue));
                    case 3 -> generalBalance.setDebit(ChargesUtils.convertStringAsBigInteger(cellValue));
                    case 4 -> generalBalance.setCredit(ChargesUtils.convertStringAsBigInteger(cellValue));
                    case 5 -> generalBalance.setPeriodBalance(ChargesUtils.convertStringAsBigInteger(cellValue));
                    case 6 -> generalBalance.setEndDebitBalance(ChargesUtils.convertStringAsBigInteger(cellValue));
                    case 7 -> generalBalance.setEndCreditBalance(ChargesUtils.convertStringAsBigInteger(cellValue));
                    default -> log.warn("Index de cellule inattendu : {}", cellIndex);
                }
            } catch (NumberFormatException ex) {
                log.error("Erreur de conversion pour la cellule {} avec la valeur : {}", cellIndex, cellValue, ex);
            }
            cellIndex++;
        }

        return generalBalance;
    }



    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                if(cell.getStringCellValue().trim().equals("-"))
                {
                    log.info("retourne zero");
                    return String.valueOf(0);
                }
                log.info("chaine de caractère: {}", cell.getStringCellValue());
                return cell.getStringCellValue();
            case NUMERIC:
                log.info("de type numerique: {}", cell.getNumericCellValue());
                return String.valueOf(cell.getNumericCellValue());

            case BLANK:
                return "";
            default:
                return "La type de la cellule est inconnu";
        }
    }

}

package com.etat_charge.utils;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.*;

public class ChargesUtils {

    private ChargesUtils() {
    }

    public static Map<String, String> genrateCharge(){
        Map<String, String> charges = new LinkedHashMap<>();

        charges.put("601", "Allocations Familiales, prénatales, de maternité et au foyer du travailleur");
        charges.put("602", "Rentes aux assurés et autres charges des AT/MP");
        charges.put("603", "Pension de vieillesse");
        charges.put("604", "Frais médicaux Assurance Matern");
        charges.put("605", "Autres Subventions accordées");
        charges.put("1", "Total des charges Techniques");
        charges.put("610", "Fournitures médicales");
        charges.put("611", "Variation des stocks fournitures de bureau");
        charges.put("2", "Achats de Matières et Fournitures/Variation des stocks");
        charges.put("612", "Électricité, eau, carburant et autres fournitures non stockables");
        charges.put("616", "Achat pour réception");
        charges.put("618", "Transport");
        charges.put("621", "Frais de voyage/déplacement personnel");
        charges.put("626", "Frais de déménagement Agents");
        charges.put("630", "Indemnités de mission");
        charges.put("632", "Loyer et charges locatives, redevance-brevet-licence-logiciel");
        charges.put("633", "Entretien/réparation immeubles d'exploitation");
        charges.put("634", "Honoraires");
        charges.put("635", "Téléphone, Affranchissement, Redevance et Autres dépenses publicitaires");
        charges.put("636", "Lignes spéciales informatiques");
        charges.put("638", "Autres services extérieurs rendus par les tiers");
        charges.put("641", "Primes d'assurance");
        charges.put("642", "Indemnités de Présidence et Jetons de présence");
        charges.put("643", "Subventions Exceptionnelles");
        charges.put("644", "Autres charges et pertes diverses");
        charges.put("651", "Charges du personnel");
        charges.put("655", "Charges sociales");
        charges.put("657", "Autres charges sociales");
        charges.put("3", "Total des Matières, Fournitures & Services consommés");
        charges.put("6611", "Impôts sur salaires");
        charges.put("6652", "Taxes foncières");
        charges.put("6653", "Autres impôts");
        charges.put("6654", "Impôts divers");
        charges.put("4", "Total des charges d'impot");
        charges.put("6911", "Dotations aux amortissements");
        charges.put("6915", "Dotation aux provisions");
        charges.put("5", "Total amortissements");
        return charges;
    }

    public static final List<String> firstCharges = Arrays.asList("601", "602", "603","604", "605");
    public static final List<String> secondCharges = Arrays.asList("610", "611");
    public static final List<String> thirdCharges = Arrays.asList(
            "612", "616", "618", "621", "626", "630", "632", "633", "634", "635",
            "636", "638", "641", "642", "643", "644", "651", "655", "657"
    );
    public static final List<String> fourCharges = Arrays.asList("6611", "6652","6653", "6654");
    public static final List<String> fiveCharges = Arrays.asList("6911", "6915");

    public static Map<Integer, BigInteger> totalDebit()
    {
        Map<Integer, BigInteger> totalDebits = new LinkedHashMap<>();
        totalDebits.put(1, BigInteger.ZERO);
        totalDebits.put(2, BigInteger.ZERO);
        totalDebits.put(3, BigInteger.ZERO);
        totalDebits.put(4, BigInteger.ZERO);
        totalDebits.put(5, BigInteger.ZERO);

        return totalDebits;
    }

    public static BigInteger convertStringAsBigInteger(String cellValue)
    {
        return BigInteger.valueOf((long) Double.parseDouble(cellValue));
    }

    public static String formatetedNumber(BigInteger number)
    {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        // Appliquer le formatage afin de mettre des virgules a chaque millier
        return numberFormat.format(number);

    }

}

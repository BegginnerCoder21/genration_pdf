package com.etat_charge.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChargesUtils {

    public static Map<String, String> genrateCharge(){
        Map<String, String> charges = new LinkedHashMap<>();

        charges.put("601", "Allocations Familiales, prénatales, de maternité et au foyer du travailleur");
        charges.put("602", "Rentes aux assurés et autres charges des AT/MP");
        charges.put("603", "Pension de vieillesse");
        charges.put("604", "Frais médicaux Assurance Matern");
        charges.put("605", "Autres Subventions accordées");
        charges.put("610", "Fournitures médicales");
        charges.put("611", "Variation des stocks fournitures de bureau");
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
        charges.put("6611", "Impôts sur salaires");
        charges.put("6652", "Taxes foncières");
        charges.put("6653", "Autres impôts");
        charges.put("6654", "Impôts divers");
        charges.put("6911", "Dotations aux amortissements");
        charges.put("6915", "Dotation aux provisions");
        return charges;
    }

}

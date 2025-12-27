package tds.importacion.csv;

// campos del csv 
public record CsvGastoRow(
        String date,
        String account,
        String category,
        String subcategory,
        String note,
        String payer,
        String amount,
        String currency
) {
}

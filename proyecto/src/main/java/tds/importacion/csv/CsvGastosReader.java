package tds.importacion.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tds.importacion.ImportacionException;


// Clase encargada de leer y parsear el fichero csv
public class CsvGastosReader {

    public List<CsvGastoRow> leer(File fichero) throws ImportacionException {
        if (fichero == null) {
            throw new ImportacionException("Fichero CSV nulo");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                return List.of();
            }

            List<String> header = splitCsvLine(headerLine);
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < header.size(); i++) {
                idx.put(header.get(i).trim().toLowerCase(), i);
            }

            // Comprobar columnas mínimas
            String[] required = {"date","account","category","subcategory","note","payer","amount","currency"};
            for (String col : required) {
                if (!idx.containsKey(col)) {
                    throw new ImportacionException("CSV inválido: falta la columna '" + col + "'");
                }
            }

            List<CsvGastoRow> rows = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> parts = splitCsvLine(line);

                // Si la línea tiene menos columnas, se ignora
                if (parts.size() < header.size()) {
                    continue;
                }

                rows.add(new CsvGastoRow(
                        get(parts, idx.get("date")),
                        get(parts, idx.get("account")),
                        get(parts, idx.get("category")),
                        get(parts, idx.get("subcategory")),
                        get(parts, idx.get("note")),
                        get(parts, idx.get("payer")),
                        get(parts, idx.get("amount")),
                        get(parts, idx.get("currency"))
                ));
            }
            return rows;
        } catch (IOException e) {
            throw new ImportacionException("No se pudo leer el CSV: " + fichero.getName(), e);
        }
    }

    private static String get(List<String> parts, int i) {
        if (i < 0 || i >= parts.size()) return "";
        String v = parts.get(i);
        return v == null ? "" : v.trim();
    }

    static List<String> splitCsvLine(String line) {
        List<String> out = new ArrayList<>();
        if (line == null) return out;

        String[] parts = line.split(",", -1);
        for (String part : parts) {
            out.add(part);
        }
        
        return out;
    }
}

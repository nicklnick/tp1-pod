package ar.edu.itba.pod.grpc.client.utils.files;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvFileReader {

    private CsvFileReader() {
        throw new AssertionError("This class should not be instantiated");
    }

    public static void readRows(String filePath, CsvLineListener listener) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                listener.onCsvParsedLine(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

}

package ar.edu.itba.pod.grpc.client.utils.files;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;

public class CsvFileReader {

    private CsvFileReader() {
        throw new AssertionError("This class should not be instantiated");
    }

    public static void readRows(String filePath, CsvLineListener listener) {
        try {
            FileReader fileReader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withCSVParser(new CSVParserBuilder()
                            .withSeparator(';')
                            .build()
                    )
                    .withSkipLines(1)
                    .build();

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                listener.onCsvParsedLine(nextLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

}

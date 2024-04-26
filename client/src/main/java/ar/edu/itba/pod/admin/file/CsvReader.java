package ar.edu.itba.pod.admin.file;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class CsvReader {


    public static <T> List<T> readRows(String filePath, Class<T> rowType) throws FileNotFoundException {
        return readRows(filePath, rowType,';');
    }

    public static <T> List<T> readRows(String filePath, Class<T> rowType, char separator) throws FileNotFoundException {
       FileReader fileReader = new FileReader(filePath);

        return new CsvToBeanBuilder<T>(fileReader)
                .withSeparator(separator)
                .withType(rowType)
                .build()
                .parse();
    }
}

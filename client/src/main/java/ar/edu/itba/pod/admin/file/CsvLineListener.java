package ar.edu.itba.pod.admin.file;

@FunctionalInterface
public interface CsvLineListener<T> {

    void onCsvParsedLine(T line);

}

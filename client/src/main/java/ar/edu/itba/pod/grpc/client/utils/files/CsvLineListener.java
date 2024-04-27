package ar.edu.itba.pod.grpc.client.utils.files;

@FunctionalInterface
public interface CsvLineListener {

    void onCsvParsedLine(String[] line);

}


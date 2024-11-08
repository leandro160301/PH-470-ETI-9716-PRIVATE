package com.jws.jwsapi.utils.file;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class FileCsvUtil {
    public static List<String> getColumn(String nameFile, int column) {
        List<String> columnsElements = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (column < columns.length) {
                    columnsElements.add(columns[column]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return columnsElements;
    }

    public static List<String> getRow(String nameFile, int rowNumber) {
        List<String> rowElements = new ArrayList<>();
        int currentRow = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (currentRow == rowNumber) {
                    String[] columns = line.split(",");
                    rowElements.addAll(Arrays.asList(columns));
                    break;
                }
                currentRow++;
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return rowElements;
    }

    //Products.class
    public static <T> List<T> read(String nameFile, Class<T> modelClass) {
        try (FileReader lector = new FileReader(nameFile)) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(lector)
                    .withType(modelClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void write(String nameFile, List<T> data) {
        try (FileWriter escritor = new FileWriter(nameFile, false)) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(escritor)
                    .withApplyQuotesToAll(false)
                    .build();

            beanToCsv.write(data);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

    public static <T> void addElement(String fileName, T element) {
        try (FileWriter escritor = new FileWriter(fileName, true)) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(escritor)
                    .withApplyQuotesToAll(false)
                    .build();

            beanToCsv.write(element);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

}

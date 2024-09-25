package com.jws.jwsapi.utils;

public class ExcelHelper {

   /* public void dialogoExcel(List<FormModelGuardados> lista, Context context) throws IOException {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialogo_transferenciaarchivo, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        excelGuardados(lista);
        new Thread(() -> {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            });
        }).start();
    }


    public void excelGuardados(List<FormModelGuardados> lista) throws IOException {
        if(lista.size()>0){
            int j=1;
            File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Registro.xls");
            while(filePath.exists()){
                filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Registro ("+ j +").xls");
                j++;
            }
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet("Pesadas");
            HSSFRow row = hssfSheet.createRow(0);
            row.createCell(0).setCellValue("Id");
            row.createCell(1).setCellValue("Producto");
            row.createCell(2).setCellValue("Lote");
            row.createCell(3).setCellValue("Cliente");
            row.createCell(4).setCellValue("Dimensiones");
            row.createCell(5).setCellValue("Neto");
            row.createCell(6).setCellValue("Bruto");
            row.createCell(7).setCellValue("Tara");
            row.createCell(8).setCellValue("Fecha");
            row.createCell(9).setCellValue("Hora");

            for(int i=0;i<lista.size();i++){
                row = hssfSheet.createRow(i + 1);
                row.createCell(0).setCellValue(lista.get(i).getId());
                row.createCell(1).setCellValue(lista.get(i).getProducto());
                row.createCell(2).setCellValue(lista.get(i).getLote());
                row.createCell(3).setCellValue(lista.get(i).getEmpresa());
                row.createCell(4).setCellValue(lista.get(i).getDimensiones());
                row.createCell(5).setCellValue(lista.get(i).getNeto());
                row.createCell(6).setCellValue(lista.get(i).getBruto());
                row.createCell(7).setCellValue(lista.get(i).getTara());
                row.createCell(8).setCellValue(lista.get(i).getFecha());
                row.createCell(9).setCellValue(lista.get(i).getHora());
            }
            try {
                if (!filePath.exists()){
                    filePath.createNewFile();
                }
                FileOutputStream fileOutputStream= new FileOutputStream(filePath);
                hssfWorkbook.write(fileOutputStream);
                if (fileOutputStream!=null){
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
}

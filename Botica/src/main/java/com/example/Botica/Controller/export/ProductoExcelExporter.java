package com.example.Botica.Controller.export;

import com.example.Botica.domain.Producto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ProductoExcelExporter {
    private final List<Producto> productos;
    public ProductoExcelExporter(List<Producto> productos) { this.productos = productos; }

    public void export(HttpServletResponse response) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Productos");
            int r = 0;
            Row header = sheet.createRow(r++);
            String[] cols = {"ID","Nombre","Descripción","Precio","Precio Regular","Categoría","Destacado","Promo Activa"};
            for (int i=0;i<cols.length;i++) header.createCell(i).setCellValue(cols[i]);

            for (Producto p : productos) {
                Row row = sheet.createRow(r++);
                int c = 0;
                row.createCell(c++).setCellValue(p.getId()==null?0:p.getId());
                row.createCell(c++).setCellValue(p.getNombre());
                row.createCell(c++).setCellValue(p.getDescripcion()==null?"":p.getDescripcion());
                row.createCell(c++).setCellValue(p.getPrecio()==null?0:p.getPrecio().doubleValue());
                row.createCell(c++).setCellValue(p.getPrecioRegular()==null?0:p.getPrecioRegular().doubleValue());
                row.createCell(c++).setCellValue(p.getCategoria()==null?"":p.getCategoria());
                row.createCell(c++).setCellValue(p.isDestacado());
                row.createCell(c++).setCellValue(p.isPromoActiva());
            }
            for (int i=0;i<8;i++) sheet.autoSizeColumn(i);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition","attachment; filename=productos.xlsx");
            workbook.write(response.getOutputStream());
        }
    }
}

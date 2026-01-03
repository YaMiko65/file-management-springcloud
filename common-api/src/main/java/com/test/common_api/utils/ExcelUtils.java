package com.test.common_api.utils;

import com.test.common_api.entity.ExcelData;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类
 * 提供Excel文件解析和处理功能
 */
public class ExcelUtils {

    /**
     * 解析Excel文件内容
     * 将Excel文件中的数据解析为ExcelData对象列表
     * 读取Excel文件的第1个工作表，从第2行开始读取（跳过表头），将每行数据映射为ExcelData对象
     *
     * @param file 要解析的Excel文件
     * @return ExcelData对象列表
     * @throws IOException 文件读取异常
     */
    public static List<ExcelData> parseExcel(MultipartFile file) throws IOException {
        List<ExcelData> dataList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            int rowStart = Math.max(1, sheet.getFirstRowNum()); // 从第二行开始读(跳过表头)
            int rowEnd = sheet.getLastRowNum();

            for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;

                ExcelData data = new ExcelData();

                // 解析单元格数据（调整后列顺序：文件夹名称、用户名、用户姓名、权限类型）
                Cell folderCell = row.getCell(0);       // 第1列：文件夹名称
                Cell userCell = row.getCell(1);         // 第2列：用户账号
                Cell nameCell = row.getCell(2);         // 第3列：用户姓名
                Cell permCell = row.getCell(3);         // 第4列：权限类型

                if (folderCell != null) {
                    data.setFolderName(getCellValue(folderCell));
                }
                if (userCell != null) {
                    data.setUsername(getCellValue(userCell));
                }
                // 新增：设置用户姓名（从第3列读取）
                if (nameCell != null) {
                    data.setName(getCellValue(nameCell));
                }
                if (permCell != null) {
                    try {
                        data.setPermission(Integer.parseInt(getCellValue(permCell)));
                    } catch (NumberFormatException e) {
                        // 处理权限类型格式错误
                    }
                }

                dataList.add(data);
            }
        }

        return dataList;
    }

    /**
     * 获取单元格的值
     * 根据单元格类型返回相应的字符串值，支持字符串、数字、布尔值和日期类型的单元格
     *
     * @param cell Excel单元格
     * @return 单元格的字符串值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
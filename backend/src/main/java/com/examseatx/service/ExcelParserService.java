package com.examseatx.service;

import com.examseatx.model.Classroom;
import com.examseatx.model.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ExcelParserService {

    // Dynamic header dictionary
    private static final List<String> REG_NO_KEYS = Arrays.asList(
            "register number", "register no", "reg no", "reg_no", "regno", 
            "class roll no", "roll no", "roll_no", "rollno", "candidate code", "student id"
    );

    private static final List<String> NAME_KEYS = Arrays.asList(
            "student name", "candidate name", "name", "full name", "student", "student name (generated)"
    );

    private static final List<String> DEPT_KEYS = Arrays.asList(
            "department", "dept", "dept name", "department name", "branch", "branch name", "stream"
    );

    private static final List<String> BRANCH_CODE_KEYS = Arrays.asList(
            "branch code", "branch", "branch_code", "dept code", "stream code"
    );

    private static final List<String> SEMESTER_KEYS = Arrays.asList(
            "semester", "sem", "year/sem", "term"
    );

    private static final List<String> SUB_CODE_KEYS = Arrays.asList(
            "subject code", "sub code", "subjcet code", "course code", "sub_code", "paper code", "subject_code"
    );

    private static final List<String> SUB_NAME_KEYS = Arrays.asList(
            "subject name", "course name", "sub name", "subjcet name", "paper name", "subject"
    );

    private static final List<String> EXAM_TYPE_KEYS = Arrays.asList(
            "exam type", "type", "r/s", "regular / supplementary", "regular/supple", "exam_type", "category"
    );

    private static final List<String> SLOT_KEYS = Arrays.asList(
            "slot", "exam slot", "session slot", "slot code"
    );

    public List<Student> parseStudentRoster(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (filename.endsWith(".csv")) {
            return parseCsvRoster(file.getInputStream());
        } else {
            return parseExcelRoster(file.getInputStream());
        }
    }

    public List<Student> parseExcelRoster(InputStream inputStream) throws Exception {
        List<Student> students = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            // Find appropriate sheet (first sheet or sheet with rows)
            Sheet sheet = workbook.getSheetAt(0);
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet cur = workbook.getSheetAt(s);
                if (cur.getPhysicalNumberOfRows() > 1) {
                    sheet = cur;
                    break;
                }
            }

            Map<String, Integer> colMap = new HashMap<>();
            int headerRowIndex = -1;

            // Search for header row in top 15 rows
            for (int r = 0; r <= Math.min(sheet.getLastRowNum(), 15); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, Integer> tempMap = mapHeaders(row);
                if (tempMap.containsKey("regNo")) {
                    headerRowIndex = r;
                    colMap = tempMap;
                    break;
                }
            }

            if (headerRowIndex == -1) {
                // Fallback: assume column 0 is Register No, 1 is Name, etc.
                colMap.put("regNo", 0);
                colMap.put("name", 1);
                colMap.put("semester", 2);
                colMap.put("branchCode", 3);
                colMap.put("department", 4);
                colMap.put("examType", 6);
                colMap.put("slot", 7);
                colMap.put("subName", 8);
                colMap.put("subCode", 9);
                headerRowIndex = 0;
            }

            for (int r = headerRowIndex + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String regNo = getCellValue(row, colMap.get("regNo"));
                if (regNo == null || regNo.trim().isEmpty() || regNo.equalsIgnoreCase("Register Number")) {
                    continue; // Skip blank or repeated header rows
                }

                regNo = regNo.trim();
                String name = getCellValue(row, colMap.get("name"));
                String dept = getCellValue(row, colMap.get("department"));
                String branchCode = getCellValue(row, colMap.get("branchCode"));
                String sem = getCellValue(row, colMap.get("semester"));
                String subCode = getCellValue(row, colMap.get("subCode"));
                String subName = getCellValue(row, colMap.get("subName"));
                String examType = getCellValue(row, colMap.get("examType"));
                String slot = getCellValue(row, colMap.get("slot"));

                // Fallback heuristic inferencing
                if (branchCode == null || branchCode.trim().isEmpty()) {
                    branchCode = inferBranchCode(regNo);
                }
                if (dept == null || dept.trim().isEmpty()) {
                    dept = inferDepartment(branchCode);
                }
                if (name == null || name.trim().isEmpty()) {
                    name = "Student " + regNo;
                }
                if (sem == null || sem.trim().isEmpty()) {
                    sem = "S6";
                }
                if (subCode == null || subCode.trim().isEmpty()) {
                    subCode = branchCode + "301";
                }
                if (subName == null || subName.trim().isEmpty()) {
                    subName = "Core " + branchCode + " Theory";
                }
                if (examType == null || examType.trim().isEmpty()) {
                    examType = inferExamType(regNo);
                }

                Student student = new Student(regNo, name, dept, branchCode, sem, subCode, subName, examType, slot);
                students.add(student);
            }
        }
        return students;
    }

    public List<Student> parseCsvRoster(InputStream inputStream) throws Exception {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            Map<String, Integer> colMap = new HashMap<>();
            int lineIndex = 0;

            while ((line = reader.readLine()) != null) {
                lineIndex++;
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCsvLine(line);

                if (colMap.isEmpty()) {
                    // Check if this line is header
                    colMap = mapCsvHeaders(parts);
                    if (colMap.containsKey("regNo")) {
                        continue;
                    } else if (lineIndex > 10) {
                        // Fallback defaults
                        colMap.put("regNo", 0);
                        colMap.put("name", 1);
                        colMap.put("semester", 2);
                        colMap.put("branchCode", 3);
                        colMap.put("department", 4);
                        colMap.put("examType", 6);
                        colMap.put("slot", 7);
                        colMap.put("subName", 8);
                        colMap.put("subCode", 9);
                    } else {
                        continue;
                    }
                }

                String regNo = getCsvVal(parts, colMap.get("regNo"));
                if (regNo == null || regNo.trim().isEmpty() || regNo.equalsIgnoreCase("Register Number")) continue;

                regNo = regNo.trim();
                String name = getCsvVal(parts, colMap.get("name"));
                String dept = getCsvVal(parts, colMap.get("department"));
                String branchCode = getCsvVal(parts, colMap.get("branchCode"));
                String sem = getCsvVal(parts, colMap.get("semester"));
                String subCode = getCsvVal(parts, colMap.get("subCode"));
                String subName = getCsvVal(parts, colMap.get("subName"));
                String examType = getCsvVal(parts, colMap.get("examType"));
                String slot = getCsvVal(parts, colMap.get("slot"));

                if (branchCode == null || branchCode.trim().isEmpty()) branchCode = inferBranchCode(regNo);
                if (dept == null || dept.trim().isEmpty()) dept = inferDepartment(branchCode);
                if (name == null || name.trim().isEmpty()) name = "Student " + regNo;
                if (sem == null || sem.trim().isEmpty()) sem = "S6";
                if (subCode == null || subCode.trim().isEmpty()) subCode = branchCode + "301";
                if (subName == null || subName.trim().isEmpty()) subName = "Core " + branchCode + " Subject";
                if (examType == null || examType.trim().isEmpty()) examType = inferExamType(regNo);

                students.add(new Student(regNo, name, dept, branchCode, sem, subCode, subName, examType, slot));
            }
        }
        return students;
    }

    private Map<String, Integer> mapHeaders(Row row) {
        Map<String, Integer> map = new HashMap<>();
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell == null) continue;
            String header = cell.getStringCellValue().trim().toLowerCase();
            matchHeaderKey(header, c, map);
        }
        return map;
    }

    private Map<String, Integer> mapCsvHeaders(String[] parts) {
        Map<String, Integer> map = new HashMap<>();
        for (int c = 0; c < parts.length; c++) {
            String header = parts[c].trim().toLowerCase();
            matchHeaderKey(header, c, map);
        }
        return map;
    }

    private void matchHeaderKey(String header, int colIdx, Map<String, Integer> map) {
        if (!map.containsKey("regNo") && REG_NO_KEYS.stream().anyMatch(header::contains)) {
            map.put("regNo", colIdx);
        } else if (!map.containsKey("name") && NAME_KEYS.stream().anyMatch(header::contains)) {
            map.put("name", colIdx);
        } else if (!map.containsKey("branchCode") && BRANCH_CODE_KEYS.stream().anyMatch(header::contains)) {
            map.put("branchCode", colIdx);
        } else if (!map.containsKey("department") && DEPT_KEYS.stream().anyMatch(header::contains)) {
            map.put("department", colIdx);
        } else if (!map.containsKey("subCode") && SUB_CODE_KEYS.stream().anyMatch(header::contains)) {
            map.put("subCode", colIdx);
        } else if (!map.containsKey("subName") && SUB_NAME_KEYS.stream().anyMatch(header::contains)) {
            map.put("subName", colIdx);
        } else if (!map.containsKey("examType") && EXAM_TYPE_KEYS.stream().anyMatch(header::contains)) {
            map.put("examType", colIdx);
        } else if (!map.containsKey("slot") && SLOT_KEYS.stream().anyMatch(header::contains)) {
            map.put("slot", colIdx);
        } else if (!map.containsKey("semester") && SEMESTER_KEYS.stream().anyMatch(header::contains)) {
            map.put("semester", colIdx);
        }
    }

    public String inferBranchCode(String regNo) {
        if (regNo == null) return "GEN";
        String clean = regNo.toUpperCase().replaceAll("[^A-Z0-9]", "");
        // Standard KTU Pattern: VJC23CS001, LVJC23AD063, VJC21EC015
        for (String code : Arrays.asList("CSD", "HMCT", "MBA", "AD", "CS", "EC", "EE", "IT", "ME", "CE")) {
            if (clean.contains(code)) {
                return code;
            }
        }
        return "ENG";
    }

    public String inferDepartment(String branchCode) {
        if (branchCode == null) return "Engineering";
        switch (branchCode.toUpperCase()) {
            case "CS": return "Computer Science";
            case "AD": return "Artificial Intelligence and Data Science";
            case "CSD": return "Computer Science and Design";
            case "EC": return "Electronics and Communication";
            case "EE": return "Electrical and Electronics";
            case "IT": return "Information Technology";
            case "ME": return "Mechanical";
            case "CE": return "Civil Engineering";
            case "HMCT": return "Hotel Management & Catering Tech";
            case "MBA": return "Management Studies";
            default: return branchCode + " Department";
        }
    }

    public String inferExamType(String regNo) {
        if (regNo == null) return "Regular";
        String u = regNo.toUpperCase();
        // Regular cohort KTU 2023: VJC23, LVJC23
        if (u.contains("23")) return "Regular";
        if (u.contains("19") || u.contains("20") || u.contains("21") || u.contains("22")) return "Supplementary";
        return "Regular";
    }

    private String getCellValue(Row row, Integer colIdx) {
        if (colIdx == null || colIdx < 0) return null;
        Cell cell = row.getCell(colIdx);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double d = cell.getNumericCellValue();
                    if (d == (long) d) return String.format("%d", (long) d);
                    return String.valueOf(d);
                }
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue(); } 
                catch (Exception e) { return String.valueOf(cell.getNumericCellValue()); }
            default: return null;
        }
    }

    private String getCsvVal(String[] parts, Integer idx) {
        if (idx == null || idx < 0 || idx >= parts.length) return null;
        String val = parts[idx].trim();
        if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }

    private String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens.toArray(new String[0]);
    }
}

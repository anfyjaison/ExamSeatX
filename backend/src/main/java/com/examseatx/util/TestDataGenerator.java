package com.examseatx.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class TestDataGenerator {

    private static final String TARGET_DIR = "C:/Users/Asus/.gemini/antigravity/scratch/ExamSeatX/ExamSeatX_TestData/";

    public static void main(String[] args) {
        try {
            File dir = new File(TARGET_DIR);
            if (!dir.exists()) dir.mkdirs();

            generateClassroomsMaster();
            generateRawStudentRoster();
            generateHistoricalSeatingPlan();
            generateSampleNoticeBoardPdf();

            System.out.println("ALL 4 TEST DATA FILES GENERATED SUCCESSFULLY IN: " + TARGET_DIR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateClassroomsMaster() throws Exception {
        File file = new File(TARGET_DIR + "02_Classrooms_Master.xlsx");
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Classrooms Master");

            org.apache.poi.ss.usermodel.Font hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle hStyle = wb.createCellStyle();
            hStyle.setFont(hFont);
            hStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            hStyle.setAlignment(HorizontalAlignment.CENTER);

            Row r0 = sheet.createRow(0);
            String[] headers = {"Room No", "Row Count", "Col Count", "Bench Capacity", "Total Capacity", "CCTV Available", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell c = r0.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(hStyle);
            }

            Object[][] rooms = {
                {"C-207", 5, 3, 2, 30, "Yes", "Active"},
                {"C-212", 5, 3, 2, 30, "Yes", "Active"},
                {"C-213", 5, 3, 2, 30, "Yes", "Active"},
                {"C-214", 5, 4, 2, 40, "Yes", "Active"},
                {"C-215", 5, 3, 2, 30, "Yes", "Active"},
                {"C-222", 5, 3, 2, 30, "Yes", "Active"},
                {"C-223", 5, 4, 2, 40, "Yes", "Active"},
                {"C-304", 6, 3, 2, 36, "Yes", "Active"},
                {"C-305", 6, 3, 2, 36, "Yes", "Active"},
                {"C-306", 6, 3, 2, 36, "Yes", "Active"},
                {"C-307", 6, 3, 2, 36, "Yes", "Active"},
                {"C-309", 5, 3, 2, 30, "Yes", "Active"},
                {"C-313", 6, 3, 2, 36, "Yes", "Active"},
                {"C-314", 6, 3, 2, 36, "Yes", "Active"},
                {"C-323", 6, 3, 2, 36, "Yes", "Active"},
                {"C-324", 6, 3, 2, 36, "Yes", "Active"}
            };

            int rowIdx = 1;
            for (Object[] r : rooms) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue((String) r[0]);
                row.createCell(1).setCellValue((Integer) r[1]);
                row.createCell(2).setCellValue((Integer) r[2]);
                row.createCell(3).setCellValue((Integer) r[3]);
                row.createCell(4).setCellValue((Integer) r[4]);
                row.createCell(5).setCellValue((String) r[5]);
                row.createCell(6).setCellValue((String) r[6]);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
        }
    }

    public static void generateRawStudentRoster() throws Exception {
        File file = new File(TARGET_DIR + "01_Raw_Student_Roster_Sample.xlsx");
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Raw Student Roster");

            org.apache.poi.ss.usermodel.Font hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle hStyle = wb.createCellStyle();
            hStyle.setFont(hFont);
            hStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
            hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row r0 = sheet.createRow(0);
            String[] headers = {"Register Number", "Student Name", "Semester", "Branch Code", "Department", "Exam Type", "SLOT", "SUBJECT NAME", "SUBJECT CODE"};
            for (int i = 0; i < headers.length; i++) {
                Cell c = r0.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(hStyle);
            }

            String[][] deptTemplates = {
                {"AD", "Artificial Intelligence and Data Science", "AIT304", "Deep Learning Algorithms", "SLOT B"},
                {"CS", "Computer Science", "CST304", "Computer Graphics & Image Processing", "SLOT B"},
                {"CSD", "Computer Science and Design", "CDT304", "User Interface & UX Design", "SLOT B"},
                {"EC", "Electronics and Communication", "ECT304", "VLSI Design", "SLOT B"},
                {"EE", "Electrical and Electronics", "EET304", "Microprocessors & Microcontrollers", "SLOT B"},
                {"IT", "Information Technology", "ITT304", "Web Programming", "SLOT B"},
                {"ME", "Mechanical", "MET304", "Dynamics and Control of Machinery", "SLOT B"},
                {"CE", "Civil Engineering", "CET304", "Design of Hydraullic Structures", "SLOT B"}
            };

            String[] firstNames = {"Aparna", "Varun", "Midhun", "Nayana", "Devika", "Anand", "Ashwin", "Abhinav", "Gokul", "Sandra", "Jithin", "Kevin", "Riya", "Nithin", "Alan", "Sanjay", "Akhil", "Thomas", "Rahul", "Amal", "Sneha", "Joel", "Meera", "Athira", "Hari", "Ishan", "Diya", "Kiran", "Vivek"};
            String[] lastNames = {"Rajan", "Roy", "Antony", "Menon", "Nair", "Kurian", "Thomas", "Reddy", "Pillai", "Shah", "Mathew", "Paul", "Das", "Joseph", "Mohan", "Babu", "Suresh", "Varghese", "Chacko", "Kumar", "Jacob"};

            int rowNum = 1;
            Random rand = new Random(42);

            for (String[] t : deptTemplates) {
                String branch = t[0];
                String dept = t[1];
                String subCode = t[2];
                String subName = t[3];
                String slot = t[4];

                // Supplementary students (2019-2022)
                for (int i = 1; i <= 6; i++) {
                    int yr = 19 + (i % 4);
                    String reg = "VJC" + yr + branch + String.format("%03d", i * 7);
                    String name = firstNames[rand.nextInt(firstNames.length)] + " " + lastNames[rand.nextInt(lastNames.length)];
                    Row r = sheet.createRow(rowNum++);
                    r.createCell(0).setCellValue(reg);
                    r.createCell(1).setCellValue(name);
                    r.createCell(2).setCellValue("S6");
                    r.createCell(3).setCellValue(branch);
                    r.createCell(4).setCellValue(dept);
                    r.createCell(5).setCellValue("Supplementary");
                    r.createCell(6).setCellValue(slot);
                    r.createCell(7).setCellValue(subName);
                    r.createCell(8).setCellValue(subCode);
                }

                // Regular students (2023)
                for (int i = 1; i <= 30; i++) {
                    String reg = (i == 30 ? "LVJC23" : "VJC23") + branch + String.format("%03d", i);
                    String name = firstNames[rand.nextInt(firstNames.length)] + " " + lastNames[rand.nextInt(lastNames.length)];
                    Row r = sheet.createRow(rowNum++);
                    r.createCell(0).setCellValue(reg);
                    r.createCell(1).setCellValue(name);
                    r.createCell(2).setCellValue("S6");
                    r.createCell(3).setCellValue(branch);
                    r.createCell(4).setCellValue(dept);
                    r.createCell(5).setCellValue("Regular");
                    r.createCell(6).setCellValue(slot);
                    r.createCell(7).setCellValue(subName);
                    r.createCell(8).setCellValue(subCode);
                }
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
        }
    }

    public static void generateHistoricalSeatingPlan() throws Exception {
        File file = new File(TARGET_DIR + "03_Historical_Seating_Plan.xlsx");
        try (Workbook wb = new XSSFWorkbook()) {
            String[] sheets = {"29 April 2026", "2 May 2026", "12 May 2026", "14 May 2026"};
            for (String shName : sheets) {
                Sheet sheet = wb.createSheet(shName);
                Row r0 = sheet.createRow(0);
                r0.createCell(0).setCellValue("CLASSROOM-WISE STUDENT ALLOCATION — " + shName.toUpperCase());

                Row r2 = sheet.createRow(2);
                String[] cols = {"Classroom", "AD", "CS", "CSD", "EC", "EE", "IT", "ME", "CE", "Total Students", "Room Capacity", "Seats Remaining"};
                for (int i = 0; i < cols.length; i++) {
                    r2.createCell(i).setCellValue(cols[i]);
                }

                Object[][] allocs = {
                    {"C-207", 10, 20, 0, 0, 0, 0, 0, 0, 30, 30, 0},
                    {"C-212", 12, 18, 0, 0, 0, 0, 0, 0, 30, 30, 0},
                    {"C-213", 12, 18, 0, 0, 0, 0, 0, 0, 30, 30, 0},
                    {"C-214", 16, 16, 0, 0, 0, 0, 0, 0, 32, 40, 8},
                    {"C-215", 10, 12, 0, 0, 0, 0, 0, 8, 30, 30, 0},
                    {"C-222", 0, 18, 0, 0, 0, 0, 0, 12, 30, 30, 0},
                    {"C-223", 0, 16, 0, 0, 16, 0, 0, 0, 32, 40, 8},
                    {"C-305", 0, 16, 0, 8, 8, 0, 0, 0, 32, 36, 4},
                    {"C-306", 0, 16, 0, 16, 0, 0, 0, 0, 32, 36, 4},
                    {"C-307", 0, 16, 0, 16, 0, 0, 0, 0, 32, 36, 4},
                    {"C-309", 0, 15, 0, 15, 0, 0, 0, 0, 30, 30, 0},
                    {"C-313", 0, 14, 4, 0, 0, 14, 0, 0, 32, 36, 4},
                    {"C-314", 0, 0, 16, 0, 0, 16, 0, 0, 32, 36, 4},
                    {"C-323", 0, 0, 15, 0, 0, 10, 7, 0, 32, 36, 4},
                    {"C-324", 0, 0, 10, 0, 0, 6, 14, 0, 30, 36, 6}
                };

                int rowIdx = 3;
                for (Object[] a : allocs) {
                    Row r = sheet.createRow(rowIdx++);
                    for (int c = 0; c < a.length; c++) {
                        if (a[c] instanceof String) {
                            r.createCell(c).setCellValue((String) a[c]);
                        } else {
                            r.createCell(c).setCellValue(((Number) a[c]).doubleValue());
                        }
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
        }
    }

    public static void generateSampleNoticeBoardPdf() throws Exception {
        File file = new File(TARGET_DIR + "04_Notice_Board_Sample_Output.pdf");
        Document doc = new Document(PageSize.A4, 25, 25, 25, 25);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            PdfWriter.getInstance(doc, fos);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font subFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font tdFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            Font tdBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);

            Paragraph p1 = new Paragraph("VISWAJYOTHI COLLEGE OF ENGINEERING & TECHNOLOGY", titleFont);
            p1.setAlignment(Element.ALIGN_CENTER);
            doc.add(p1);

            Paragraph p2 = new Paragraph("KTU - B.Tech./BHMCT/MBA Degree University Examination Apr/May 2025\nMAIN SEATING ARRANGEMENT", subFont);
            p2.setAlignment(Element.ALIGN_CENTER);
            doc.add(p2);
            doc.add(new Paragraph(" "));

            PdfPTable meta = new PdfPTable(2);
            meta.setWidthPercentage(100);
            PdfPCell c1 = new PdfPCell(new Phrase("Date : 14-05-2025", metaFont));
            c1.setBorder(Rectangle.NO_BORDER);
            PdfPCell c2 = new PdfPCell(new Phrase("Session : FN", metaFont));
            c2.setBorder(Rectangle.NO_BORDER);
            c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            meta.addCell(c1);
            meta.addCell(c2);
            doc.add(meta);
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.0f, 1.8f, 7.5f});

            table.addCell(createThCell("Class Room", thFont));
            table.addCell(createThCell("Branch", thFont));
            table.addCell(createThCell("Register Number", thFont));

            // Room C-207
            PdfPCell rC207 = new PdfPCell(new Phrase("C - 207", tdBold));
            rC207.setRowspan(2);
            rC207.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rC207.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(rC207);
            table.addCell(new Phrase("S4 - CE", tdBold));
            table.addCell(new Phrase("VJC21CE020, VJC22CE017, VJC22CE018, VJC22CE019, VJC22CE023, VJC22CE026, VJC22CE030, VJC23CE001, VJC23CE003 - VJC23CE009", tdFont));
            table.addCell(new Phrase("S4 - EC", tdBold));
            table.addCell(new Phrase("VJC19EC024, VJC19EC053, VJC19EC064, VJC21EC015, VJC21EC020, VJC21EC029, VJC21EC030, VJC21EC042, VJC22EC051, VJC22EC058, VJC23EC001 - VJC23EC006", tdFont));

            // Room C-212
            PdfPCell rC212 = new PdfPCell(new Phrase("C - 212", tdBold));
            rC212.setRowspan(2);
            rC212.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rC212.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(rC212);
            table.addCell(new Phrase("S4 - CE", tdBold));
            table.addCell(new Phrase("VJC23CE011 - VJC23CE017, LVJC23CE019 - LVJC23CE023", tdFont));
            table.addCell(new Phrase("S4 - EC", tdBold));
            table.addCell(new Phrase("VJC23EC007 - VJC23EC010, VJC23EC012 - VJC23EC025", tdFont));

            // Room C-213
            PdfPCell rC213 = new PdfPCell(new Phrase("C - 213", tdBold));
            rC213.setRowspan(2);
            rC213.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rC213.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(rC213);
            table.addCell(new Phrase("S4 - EE", tdBold));
            table.addCell(new Phrase("VJC19EE015, VJC23EE001 - VJC23EE011", tdFont));
            table.addCell(new Phrase("S4 - EC", tdBold));
            table.addCell(new Phrase("VJC23EC026 - VJC23EC042, VJC23EC044", tdFont));

            // Room C-214
            PdfPCell rC214 = new PdfPCell(new Phrase("C - 214", tdBold));
            rC214.setRowspan(2);
            rC214.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rC214.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(rC214);
            table.addCell(new Phrase("S4 - EE", tdBold));
            table.addCell(new Phrase("VJC23EE012 - VJC23EE020, LVJC23EE021", tdFont));
            table.addCell(new Phrase("S4 - EC", tdBold));
            table.addCell(new Phrase("VJC23EC045 - VJC23EC049, LVJC23EC050 - LVJC23EC052", tdFont));

            // Room C-215
            PdfPCell rC215 = new PdfPCell(new Phrase("C - 215", tdBold));
            rC215.setRowspan(3);
            rC215.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rC215.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(rC215);
            table.addCell(new Phrase("S4 - IT", tdBold));
            table.addCell(new Phrase("VJC19IT026, VJC21IT025, VJC21IT056, VJC22IT027, VJC22IT039, VJC22IT040, VJC23IT002 - VJC23IT007, VJC23IT010", tdFont));
            table.addCell(new Phrase("S4 - IT", tdBold));
            table.addCell(new Phrase("VJC23IT011 - VJC23IT020, VJC23IT022 - VJC23IT024, VJC23IT026, VJC23IT027, VJC23IT029", tdFont));
            table.addCell(new Phrase("S4 - ME", tdBold));
            table.addCell(new Phrase("VJC19ME004, VJC19ME056, LVJC19ME088, VJC20ME019, VJC21ME005, VJC21ME027, VJC21ME028, VJC21ME031, VJC22ME001, VJC22ME007, VJC22ME018, VJC23ME002 - VJC23ME005", tdFont));

            doc.add(table);

            Paragraph foot = new Paragraph("\n\n\nChief Supdt.", tdBold);
            foot.setAlignment(Element.ALIGN_RIGHT);
            doc.add(foot);

            doc.close();
        }
    }

    private static PdfPCell createThCell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(new Color(240, 240, 240));
        c.setPadding(4);
        return c;
    }
}

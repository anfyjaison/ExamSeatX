# ExamSeatX — Intelligent University Examination Seating & Invigilation Engine

ExamSeatX is a full-stack, enterprise-grade examination seating allocation and faculty invigilation management system built with **Spring Boot 3 (Java 17+)**, **MySQL / H2 (MySQL Mode)**, **Apache POI**, **OpenPDF**, and **React.js with Tailwind CSS**.

It is engineered to automate university examination seat allocation while eliminating malpractice through strict spatial non-adjacency constraints, and to enforce department-neutral faculty duty assignments.

---

## 🚀 Key Modules & Capabilities

### 1. Dual Data Management & Flexible Parsing
- **Classroom Venue Management**: Add/Edit/Disable examination halls with room number, row depth, column count, bench capacity (1 or 2 students per bench), and CCTV surveillance tracking. Max seat capacities are computed dynamically.
- **Apache POI Dynamic Header Mapping**: Ingests candidate rosters (`.xlsx` or `.csv`) regardless of column naming variations (e.g., `Register No` vs `Class Roll No`, `Branch` vs `Department`, `Sub Code` vs `Subject Code`).
- **Heuristic Cohort Inferencing**: Automatically classifies candidates into **Regular** vs. **Supplementary** cohorts based on register number prefixes (e.g., `VJC23` vs `VJC19–VJC22`), and infers branch codes directly if omitted.
- **Student Roster Editor**: Search, filter by branch or exam type, and manually add, edit, or delete student records.

### 2. Faculty Invigilator Module & Duty Assignment Engine
- **Faculty Records**: Manage invigilator records with Name, Faculty ID, Department, Email, Phone, and Maximum Duty limits.
- **Department Neutrality Constraint**: Automatically pairs faculty with exam halls such that an invigilator **NEVER** oversees candidates from their own department (`invigilator.department != room.student_departments`).
- **Export Formats**: Exports the Master Duty Schedule as styled **Excel (`.xlsx`)** and **PDF** circulars.

### 3. Intelligent Seating Allocation (CSP + Backtracking Engine)
- **Constraint Satisfaction Problem (CSP)**: Enforces **8-way spatial non-adjacency** (horizontal, vertical, and diagonal). Desks physically adjacent in the 2D classroom grid MUST NOT share the same department or subject code.
- **Interleaved Candidate Queuing**: Round-robin schedules Regular and Supplementary candidates from diverse branches (CS, AD, EC, EE, IT, ME, CE, CSD) to maximize spatial entropy.
- **Greedy Placement + Intelligent Backtracking**: Places candidates using domain heuristics; when spatial bottlenecks arise, the backtracking engine shifts compatible neighbors automatically without deadlock.

### 4. Official University Report Generation (PDF & Excel)
- **Notice Board Seating Arrangement PDF**: Replicates university examination notice board formats (KTU / Viswajyothi College standard). Contiguous register numbers are condensed into clean ranges (e.g. `VJC23EC001 - VJC23EC010`).
- **Room Attendance & Verification Sheet PDF**: Desk-by-desk candidate lists (e.g. `R1-C1-S1`) equipped with booklet serial number entry fields, student signature lines, and invigilator reconciliation tally blocks.

### 5. Smart Slate/Indigo Aesthetic & User Guide
- Modern, clean user interface with slate and indigo palette.
- Interactive **"? How to Use"** guide modal detailing each step of the exam preparation workflow.

---

## 📁 Repository Structure

```
ExamSeatX/
├── ExamSeatX_TestData/                      # Pre-generated sample datasets
│   ├── 01_Raw_Student_Roster_Sample.xlsx   # 488 candidates across all branches (Regular + Supple)
│   ├── 02_Classrooms_Master.xlsx           # 16 classrooms with rows, cols, capacities, CCTV
│   ├── 03_Historical_Seating_Plan.xlsx     # 4 exam dates allocation history
│   └── 04_Notice_Board_Sample_Output.pdf   # Formatted printed notice board sample
├── backend/                                # Spring Boot 3 + Java 17 Application
│   ├── build.gradle
│   ├── src/main/java/com/examseatx/
│   │   ├── ExamSeatXApplication.java
│   │   ├── config/                         # CorsConfig, DataInitializer
│   │   ├── controller/                     # REST APIs (Classrooms, Students, Invigilators, Allocations, Reports)
│   │   ├── dto/                            # Matrix, GridCell, Duty DTOs
│   │   ├── model/                          # JPA Entities
│   │   ├── repository/                     # Spring Data Repositories
│   │   ├── service/                        # CspSeatingService, ExcelParserService, InvigilatorService, PdfExportService
│   │   └── util/                           # TestDataGenerator
│   └── src/main/resources/
│       └── application.properties          # H2 in-memory (MySQL compatible) & MySQL config
└── frontend/                               # React + Tailwind Dashboard
    ├── package.json
    ├── tailwind.config.js
    ├── index.html                          # Vite entry
    ├── standalone.html                     # Double-clickable direct browser dashboard
    └── src/
        ├── App.jsx
        ├── api/client.js                   # API client
        └── components/                     # Modular UI Components
```

---

## 🛠️ How to Run

### Step 1: Run the Backend (Spring Boot)

You can launch the executable jar directly using Java 17:
```powershell
$env:JAVA_HOME = "C:\Users\Asus\.gemini\antigravity\scratch\openjdk17-extracted\jdk-17.0.20.1+1"
& "$env:JAVA_HOME\bin\java.exe" -jar "C:\Users\Asus\.gemini\antigravity\scratch\ExamSeatX\backend\build\libs\examseatx-backend-1.0.0-SNAPSHOT.jar"
```
Or run with Gradle:
```powershell
cd C:\Users\Asus\.gemini\antigravity\scratch\ExamSeatX\backend
..\gradle-8.2-extracted\gradle-8.2\bin\gradle.bat bootRun
```
The server will start on `http://localhost:8080`.
The database initializes automatically with all 16 classrooms, 16 faculty invigilators, and sample students!

### Step 2: Open the Frontend Dashboard

Option A: **Instant Double-Click (Zero Node setup required)**
Open `C:\Users\Asus\.gemini\antigravity\scratch\ExamSeatX\frontend\standalone.html` in Chrome, Edge, or Firefox. It runs React 18 and Tailwind CSS directly and connects to the Spring Boot backend on `http://localhost:8080/api`.

Option B: **Standard Vite Dev Server**
```bash
cd frontend
npm install
npm run dev
```

---

## 🧪 Testing the Seating Engine & Reports

1. Click **"CSP Seating Engine"** in the top navigation bar.
2. Click **"Execute Seating Solver"**. The engine will run in ~100ms, allocating candidates across classrooms while strictly honoring 8-way spatial non-adjacency.
3. Open **"Visual Seat Map"** to inspect classroom layouts (e.g. C-207, C-212) with branch color-coded tags and verify that no adjacent seats share the same branch.
4. Click **"Invigilators"** to inspect duty assignments and verify that all assigned rooms show **"✓ Dept Neutral"**.
5. Click **"PDF Reports"** and download **Notice Board Seating Arrangement PDF** and **Room Attendance Sheet PDF**.

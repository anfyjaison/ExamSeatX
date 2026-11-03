package com.examseatx.config;

import com.examseatx.model.Classroom;
import com.examseatx.model.ExamSession;
import com.examseatx.model.Invigilator;
import com.examseatx.model.Student;
import com.examseatx.repository.ClassroomRepository;
import com.examseatx.repository.ExamSessionRepository;
import com.examseatx.repository.InvigilatorRepository;
import com.examseatx.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private InvigilatorRepository invigilatorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExamSessionRepository examSessionRepository;

    @Override
    public void run(String... args) {
        initClassrooms();
        initInvigilators();
        initExamSession();
        initStudentsSample();
    }

    private void initClassrooms() {
        if (classroomRepository.count() > 0) return;

        List<Classroom> rooms = Arrays.asList(
                new Classroom("C-207", 5, 3, 2, true),
                new Classroom("C-212", 5, 3, 2, true),
                new Classroom("C-213", 5, 3, 2, true),
                new Classroom("C-214", 5, 4, 2, true),
                new Classroom("C-215", 5, 3, 2, true),
                new Classroom("C-222", 5, 3, 2, true),
                new Classroom("C-223", 5, 4, 2, true),
                new Classroom("C-304", 6, 3, 2, true),
                new Classroom("C-305", 6, 3, 2, true),
                new Classroom("C-306", 6, 3, 2, true),
                new Classroom("C-307", 6, 3, 2, true),
                new Classroom("C-309", 5, 3, 2, true),
                new Classroom("C-313", 6, 3, 2, true),
                new Classroom("C-314", 6, 3, 2, true),
                new Classroom("C-323", 6, 3, 2, true),
                new Classroom("C-324", 6, 3, 2, true)
        );
        classroomRepository.saveAll(rooms);
    }

    private void initInvigilators() {
        if (invigilatorRepository.count() > 0) return;

        List<Invigilator> invigilators = Arrays.asList(
                new Invigilator("FAC-CE-101", "Dr. Manoj K. Varghese", "Civil Engineering", "manoj@vjcet.org", "9847100001"),
                new Invigilator("FAC-CE-102", "Prof. Bindu Thomas", "Civil Engineering", "bindu@vjcet.org", "9847100002"),
                new Invigilator("FAC-ME-101", "Dr. Sreejith R.", "Mechanical", "sreejith@vjcet.org", "9847100003"),
                new Invigilator("FAC-ME-102", "Prof. Eldhose Mathew", "Mechanical", "eldhose@vjcet.org", "9847100004"),
                new Invigilator("FAC-EC-101", "Dr. Jose P. Mathew", "Electronics and Communication", "jose@vjcet.org", "9847100005"),
                new Invigilator("FAC-EC-102", "Prof. Anila Paul", "Electronics and Communication", "anila@vjcet.org", "9847100006"),
                new Invigilator("FAC-EE-101", "Dr. Cyriac M. K.", "Electrical and Electronics", "cyriac@vjcet.org", "9847100007"),
                new Invigilator("FAC-EE-102", "Prof. Reshma Jacob", "Electrical and Electronics", "reshma@vjcet.org", "9847100008"),
                new Invigilator("FAC-CS-101", "Dr. Mini Joseph", "Computer Science", "mini@vjcet.org", "9847100009"),
                new Invigilator("FAC-CS-102", "Prof. Jiby Peter", "Computer Science", "jiby@vjcet.org", "9847100010"),
                new Invigilator("FAC-IT-101", "Dr. Binu K. George", "Information Technology", "binu@vjcet.org", "9847100011"),
                new Invigilator("FAC-IT-102", "Prof. Deepa S.", "Information Technology", "deepa@vjcet.org", "9847100012"),
                new Invigilator("FAC-AD-101", "Prof. Thomas Abraham", "Artificial Intelligence and Data Science", "thomas@vjcet.org", "9847100013"),
                new Invigilator("FAC-BS-101", "Dr. Renju Joseph", "Basic Science & Humanities", "renju@vjcet.org", "9847100014"),
                new Invigilator("FAC-BS-102", "Prof. Sunitha K.", "Basic Science & Humanities", "sunitha@vjcet.org", "9847100015"),
                new Invigilator("FAC-MBA-101", "Dr. Anish Kurian", "Management Studies", "anish@vjcet.org", "9847100016")
        );
        invigilatorRepository.saveAll(invigilators);
    }

    private void initExamSession() {
        if (examSessionRepository.count() > 0) return;

        ExamSession session = new ExamSession(
                LocalDate.of(2025, 5, 14),
                "FN",
                "KTU B.Tech/BHMCT/MBA Degree University Examination Apr/May 2025",
                "S6"
        );
        examSessionRepository.save(session);
    }

    private void initStudentsSample() {
        if (studentRepository.count() > 0) return;

        List<Student> sample = new ArrayList<>();

        // AD Students (AI & Data Science)
        sample.add(new Student("VJC21AD013", "Aparna Rajan", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC22AD021", "Midhun Antony", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC23AD001", "Varun Roy", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD002", "Aparna Jose", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD003", "Nayana Menon", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD004", "Devika Nair", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD005", "Anand Kurian", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD006", "Ashwin Thomas", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD007", "Abhinav Reddy", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD008", "Gokul Pillai", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD009", "Aparna Menon", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));
        sample.add(new Student("VJC23AD010", "Sandra Shah", "Artificial Intelligence and Data Science", "AD", "S6", "AIT304", "Deep Learning Algorithms", "Regular", "SLOT B"));

        // CS Students (Computer Science)
        sample.add(new Student("VJC20CS006", "Akhil Suresh", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC22CS012", "Kevin Antony", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC23CS001", "Anand Reddy", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS002", "Sreehari Reddy", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS003", "Neha Mathew", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS004", "Neha Menon", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS005", "Sanjay Suresh", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS007", "Alen Joseph", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS008", "Midhun Kumar", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS009", "Riya Chacko", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS010", "Sandra Kurian", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CS011", "Midhun Menon", "Computer Science", "CS", "S6", "CST304", "Computer Graphics & Image Processing", "Regular", "SLOT B"));

        // EC Students (Electronics & Communication)
        sample.add(new Student("VJC20EC014", "Gokul Das", "Electronics and Communication", "EC", "S6", "ECT304", "VLSI Design", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC21EC015", "Sreehari Das", "Electronics and Communication", "EC", "S6", "ECT304", "VLSI Design", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC23EC001", "Aarav Shah", "Electronics and Communication", "EC", "S6", "ECT304", "VLSI Design", "Regular", "SLOT B"));
        sample.add(new Student("VJC23EC002", "Diya Nair", "Electronics and Communication", "EC", "S6", "ECT304", "VLSI Design", "Regular", "SLOT B"));
        sample.add(new Student("VJC23EC004", "Sreehari Chacko", "Electronics and Communication", "EC", "S6", "ECT304", "VLSI Design", "Regular", "SLOT B"));
        sample.add(new Student("VJC23EC005", "Ishan Krishnan", "Electronics and Communication", "EC", "S6", "ECT304", "VLSI Design", "Regular", "SLOT B"));
        sample.add(new Student("VJC23EC006", "Athira Rajan", "Electronics and Communication", "EC", "S6", "ECT304", "VLSI Design", "Regular", "SLOT B"));
        sample.add(new Student("VJC23EC007", "Aiswarya Nair", "Electronics and Communication", "EC", "S6", "ECT304", "VLSI Design", "Regular", "SLOT B"));

        // EE Students (Electrical & Electronics)
        sample.add(new Student("VJC19EE015", "Nayana Das", "Electrical and Electronics", "EE", "S6", "EET304", "Microprocessors & Microcontrollers", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC21EE012", "Sanjay Nair", "Electrical and Electronics", "EE", "S6", "EET304", "Microprocessors & Microcontrollers", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC23EE001", "Rahul Antony", "Electrical and Electronics", "EE", "S6", "EET304", "Microprocessors & Microcontrollers", "Regular", "SLOT B"));
        sample.add(new Student("VJC23EE002", "Hari Reddy", "Electrical and Electronics", "EE", "S6", "EET304", "Microprocessors & Microcontrollers", "Regular", "SLOT B"));
        sample.add(new Student("VJC23EE003", "Maya Paul", "Electrical and Electronics", "EE", "S6", "EET304", "Microprocessors & Microcontrollers", "Regular", "SLOT B"));
        sample.add(new Student("VJC23EE004", "Alan Varghese", "Electrical and Electronics", "EE", "S6", "EET304", "Microprocessors & Microcontrollers", "Regular", "SLOT B"));

        // IT Students (Information Technology)
        sample.add(new Student("VJC19IT046", "Ishan Chacko", "Information Technology", "IT", "S6", "ITT304", "Web Programming", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC20IT012", "Ashwin George", "Information Technology", "IT", "S6", "ITT304", "Web Programming", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC23IT002", "Neha Roy", "Information Technology", "IT", "S6", "ITT304", "Web Programming", "Regular", "SLOT B"));
        sample.add(new Student("VJC23IT003", "Nithin Pillai", "Information Technology", "IT", "S6", "ITT304", "Web Programming", "Regular", "SLOT B"));
        sample.add(new Student("VJC23IT004", "Sandra Pillai", "Information Technology", "IT", "S6", "ITT304", "Web Programming", "Regular", "SLOT B"));
        sample.add(new Student("VJC23IT005", "Nikhil Antony", "Information Technology", "IT", "S6", "ITT304", "Web Programming", "Regular", "SLOT B"));
        sample.add(new Student("VJC23IT013", "Anfy Jaison", "Information Technology", "IT", "S6", "ITT304", "Web Programming", "Regular", "SLOT B"));

        // ME Students (Mechanical)
        sample.add(new Student("LVJC19ME087", "Vivek Jacob", "Mechanical", "ME", "S6", "MET304", "Dynamics & Control of Machinery", "Supplementary", "SLOT B"));
        sample.add(new Student("LVJC19ME088", "Ishan Reddy", "Mechanical", "ME", "S6", "MET304", "Dynamics & Control of Machinery", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC23ME002", "Varun Varghese", "Mechanical", "ME", "S6", "MET304", "Dynamics & Control of Machinery", "Regular", "SLOT B"));
        sample.add(new Student("VJC23ME003", "Alen Varghese", "Mechanical", "ME", "S6", "MET304", "Dynamics & Control of Machinery", "Regular", "SLOT B"));
        sample.add(new Student("VJC23ME004", "Nithin Babu", "Mechanical", "ME", "S6", "MET304", "Dynamics & Control of Machinery", "Regular", "SLOT B"));
        sample.add(new Student("VJC23ME005", "Aparna Krishnan", "Mechanical", "ME", "S6", "MET304", "Dynamics & Control of Machinery", "Regular", "SLOT B"));

        // CE Students (Civil Engineering)
        sample.add(new Student("VJC23CE001", "Nithin Chacko", "Civil Engineering", "CE", "S6", "CET304", "Design of Hydraulic Structures", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CE003", "Thomas Chacko", "Civil Engineering", "CE", "S6", "CET304", "Design of Hydraulic Structures", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CE004", "Athira Paul", "Civil Engineering", "CE", "S6", "CET304", "Design of Hydraulic Structures", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CE005", "Hari Menon", "Civil Engineering", "CE", "S6", "CET304", "Design of Hydraulic Structures", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CE006", "Maya Antony", "Civil Engineering", "CE", "S6", "CET304", "Design of Hydraulic Structures", "Regular", "SLOT B"));

        // CSD Students (Computer Science and Design)
        sample.add(new Student("VJC21CSD030", "Jithin Mohan", "Computer Science and Design", "CSD", "S6", "CDT304", "UI/UX Design", "Supplementary", "SLOT B"));
        sample.add(new Student("VJC23CSD001", "Rohan Babu", "Computer Science and Design", "CSD", "S6", "CDT304", "UI/UX Design", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CSD002", "Riya Shah", "Computer Science and Design", "CSD", "S6", "CDT304", "UI/UX Design", "Regular", "SLOT B"));
        sample.add(new Student("VJC23CSD003", "Sanjay Suresh", "Computer Science and Design", "CSD", "S6", "CDT304", "UI/UX Design", "Regular", "SLOT B"));

        studentRepository.saveAll(sample);
    }
}

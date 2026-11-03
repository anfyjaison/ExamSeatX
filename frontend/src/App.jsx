import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import HowToUseModal from './components/HowToUseModal';
import ClassroomManager from './components/ClassroomManager';
import StudentRoster from './components/StudentRoster';
import InvigilatorManager from './components/InvigilatorManager';
import SeatingEngine from './components/SeatingEngine';
import SeatingMatrixView from './components/SeatingMatrixView';
import ReportDownloads from './components/ReportDownloads';
import { api } from './api/client';

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [isHowToUseOpen, setIsHowToUseOpen] = useState(false);

  const [classrooms, setClassrooms] = useState([]);
  const [students, setStudents] = useState([]);
  const [invigilators, setInvigilators] = useState([]);
  const [duties, setDuties] = useState([]);
  const [roomMatrices, setRoomMatrices] = useState([]);
  const [examSessions, setExamSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [backendConnected, setBackendConnected] = useState(false);

  const fetchAllData = async () => {
    try {
      setLoading(true);
      const [roomsData, studentsData, invData, sessionsData] = await Promise.all([
        api.getClassrooms(),
        api.getStudents(),
        api.getInvigilators(),
        api.getExamSessions()
      ]);

      setClassrooms(roomsData || []);
      setStudents(studentsData || []);
      setInvigilators(invData || []);
      setExamSessions(sessionsData || []);
      setBackendConnected(true);

      // Fetch matrix and duties if sessions exist
      if (sessionsData && sessionsData.length > 0) {
        const matrixData = await api.getSeatingMatrix(sessionsData[0].id).catch(() => []);
        setRoomMatrices(matrixData || []);
        const dutyData = await api.getDuties(sessionsData[0].id).catch(() => []);
        setDuties(dutyData || []);
      }
    } catch (err) {
      console.warn('Backend connection note:', err.message);
      setBackendConnected(false);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllData();
  }, []);

  const handleAllocationSuccess = (result) => {
    if (result && result.roomMatrices) {
      setRoomMatrices(result.roomMatrices);
    }
    // Refresh duties after allocation
    if (examSessions.length > 0) {
      api.getDuties(examSessions[0].id).then(setDuties).catch(() => {});
    }
  };

  const totalSeats = classrooms.reduce((sum, r) => sum + (r.active ? r.totalCapacity : 0), 0);
  const activeRooms = classrooms.filter(r => r.active).length;
  const regularCount = students.filter(s => s.examType && s.examType.toLowerCase() === 'regular').length;
  const suppCount = students.filter(s => s.examType && s.examType.toLowerCase() === 'supplementary').length;

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 font-sans antialiased flex flex-col">
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        onOpenHowToUse={() => setIsHowToUseOpen(true)}
        examSessions={examSessions}
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        {/* Dashboard Tab */}
        {activeTab === 'dashboard' && (
          <div className="space-y-6">
            {/* Hero Welcome Banner */}
            <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-indigo-900 text-white rounded-3xl p-8 shadow-xl relative overflow-hidden">
              <div className="absolute right-0 top-0 bottom-0 w-1/3 bg-radial from-indigo-500/10 to-transparent pointer-events-none" />
              <div className="max-w-3xl space-y-3 relative z-10">
                <div className="inline-flex items-center space-x-2 bg-indigo-500/20 text-indigo-300 border border-indigo-400/30 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">
                  <span>Spring Boot + React + OpenPDF</span>
                </div>
                <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
                  ExamSeatX Command Center
                </h1>
                <p className="text-sm text-indigo-200/80 leading-relaxed max-w-2xl">
                  Intelligent Constraint Satisfaction Seating Engine for university examinations. Eliminates exam malpractice by strictly enforcing 8-way spatial non-adjacency between candidates of identical departments and course codes.
                </p>

                <div className="flex flex-wrap items-center gap-3 pt-3">
                  <button
                    onClick={() => setActiveTab('seating')}
                    className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold rounded-xl shadow-md transition-all flex items-center space-x-2"
                  >
                    <span>Run CSP Allocation Engine</span>
                    <span>→</span>
                  </button>
                  <button
                    onClick={() => setIsHowToUseOpen(true)}
                    className="px-4 py-2.5 bg-white/10 hover:bg-white/20 text-white text-xs font-bold rounded-xl border border-white/20 transition-all flex items-center space-x-1.5"
                  >
                    <span>? Interactive Guide</span>
                  </button>
                  <button
                    onClick={() => setActiveTab('reports')}
                    className="px-4 py-2.5 bg-white/10 hover:bg-white/20 text-white text-xs font-bold rounded-xl border border-white/20 transition-all flex items-center space-x-1.5"
                  >
                    <span>PDF Notice Board</span>
                  </button>
                </div>
              </div>
            </div>

            {/* Quick Metrics Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <div
                onClick={() => setActiveTab('students')}
                className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm hover:border-indigo-300 cursor-pointer transition-all"
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Enrolled Roster</span>
                  <span className="w-2.5 h-2.5 rounded-full bg-emerald-500" />
                </div>
                <div className="mt-2 text-3xl font-black text-slate-900">{students.length}</div>
                <div className="mt-1 flex items-center space-x-2 text-xs text-slate-500">
                  <span className="font-semibold text-emerald-600">{regularCount} Reg</span>
                  <span>•</span>
                  <span className="font-semibold text-amber-600">{suppCount} Supple</span>
                </div>
              </div>

              <div
                onClick={() => setActiveTab('classrooms')}
                className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm hover:border-indigo-300 cursor-pointer transition-all"
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Exam Halls</span>
                  <span className="w-2.5 h-2.5 rounded-full bg-indigo-500" />
                </div>
                <div className="mt-2 text-3xl font-black text-indigo-700">{activeRooms}</div>
                <div className="mt-1 text-xs text-slate-500">
                  <span className="font-bold text-slate-900">{totalSeats}</span> total seat capacity
                </div>
              </div>

              <div
                onClick={() => setActiveTab('invigilators')}
                className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm hover:border-indigo-300 cursor-pointer transition-all"
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Faculty Invigilators</span>
                  <span className="w-2.5 h-2.5 rounded-full bg-teal-500" />
                </div>
                <div className="mt-2 text-3xl font-black text-slate-900">{invigilators.length}</div>
                <div className="mt-1 text-xs text-slate-500">
                  <span className="font-semibold text-teal-600">{duties.length} duties</span> assigned in session
                </div>
              </div>

              <div
                onClick={() => setActiveTab('matrix')}
                className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm hover:border-indigo-300 cursor-pointer transition-all"
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Allocated Desks</span>
                  <span className="w-2.5 h-2.5 rounded-full bg-purple-500" />
                </div>
                <div className="mt-2 text-3xl font-black text-purple-700">
                  {roomMatrices.reduce((sum, m) => sum + (m.occupiedSeats || 0), 0)}
                </div>
                <div className="mt-1 text-xs text-slate-500">
                  across {roomMatrices.filter(m => m.occupiedSeats > 0).length} venues
                </div>
              </div>
            </div>

            {/* Workflow Quick Links */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-2">
              <div
                onClick={() => setActiveTab('students')}
                className="p-6 bg-white rounded-2xl border border-slate-200/80 shadow-sm hover:shadow-md hover:border-indigo-300 cursor-pointer transition-all space-y-3"
              >
                <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-700 flex items-center justify-center font-bold">
                  1
                </div>
                <h3 className="text-base font-bold text-slate-900">Import Student Roster</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Upload raw university student Excel or CSV files. The Apache POI parser automatically resolves variable header naming.
                </p>
                <span className="text-xs font-bold text-indigo-600 inline-flex items-center space-x-1">
                  <span>Manage Candidates</span>
                  <span>→</span>
                </span>
              </div>

              <div
                onClick={() => setActiveTab('seating')}
                className="p-6 bg-white rounded-2xl border border-slate-200/80 shadow-sm hover:shadow-md hover:border-indigo-300 cursor-pointer transition-all space-y-3"
              >
                <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-700 flex items-center justify-center font-bold">
                  2
                </div>
                <h3 className="text-base font-bold text-slate-900">Run CSP Seating Engine</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Execute intelligent greedy allocation with backtracking to ensure non-adjacent department & subject code distribution.
                </p>
                <span className="text-xs font-bold text-indigo-600 inline-flex items-center space-x-1">
                  <span>Launch Solver</span>
                  <span>→</span>
                </span>
              </div>

              <div
                onClick={() => setActiveTab('reports')}
                className="p-6 bg-white rounded-2xl border border-slate-200/80 shadow-sm hover:shadow-md hover:border-indigo-300 cursor-pointer transition-all space-y-3"
              >
                <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-700 flex items-center justify-center font-bold">
                  3
                </div>
                <h3 className="text-base font-bold text-slate-900">Download Official Reports</h3>
                <p className="text-xs text-slate-500 leading-relaxed">
                  Export publication-ready Notice Board Seating Matrices and Room Attendance Sheets with student signature blocks.
                </p>
                <span className="text-xs font-bold text-indigo-600 inline-flex items-center space-x-1">
                  <span>View PDF Suite</span>
                  <span>→</span>
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Other Tabs */}
        {activeTab === 'classrooms' && (
          <ClassroomManager classrooms={classrooms} onRefresh={fetchAllData} api={api} />
        )}

        {activeTab === 'students' && (
          <StudentRoster students={students} onRefresh={fetchAllData} api={api} />
        )}

        {activeTab === 'invigilators' && (
          <InvigilatorManager
            invigilators={invigilators}
            duties={duties}
            examSessions={examSessions}
            onRefresh={fetchAllData}
            api={api}
          />
        )}

        {activeTab === 'seating' && (
          <SeatingEngine
            examSessions={examSessions}
            classrooms={classrooms}
            students={students}
            onAllocationComplete={handleAllocationSuccess}
            onNavigateToMatrix={() => setActiveTab('matrix')}
            api={api}
          />
        )}

        {activeTab === 'matrix' && (
          <SeatingMatrixView
            roomMatrices={roomMatrices}
            examSessions={examSessions}
            onRefresh={fetchAllData}
            api={api}
          />
        )}

        {activeTab === 'reports' && (
          <ReportDownloads
            examSessions={examSessions}
            classrooms={classrooms}
            api={api}
          />
        )}
      </main>

      <HowToUseModal isOpen={isHowToUseOpen} onClose={() => setIsHowToUseOpen(false)} />

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-6 text-center text-xs text-slate-400">
        ExamSeatX • Intelligent University Examination Seating Allocation System • Built with Spring Boot (Java 17) & React
      </footer>
    </div>
  );
}

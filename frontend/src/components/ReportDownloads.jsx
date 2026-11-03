import React, { useState } from 'react';

export default function ReportDownloads({ examSessions, classrooms, api }) {
  const [selectedSessionId, setSelectedSessionId] = useState(
    examSessions.length > 0 ? examSessions[0].id : ''
  );
  const [selectedRoomId, setSelectedRoomId] = useState('');

  return (
    <div className="space-y-6">
      {/* Overview Card */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4">
        <div>
          <h3 className="text-base font-bold text-slate-900">Official Examination PDF & Excel Report Suite</h3>
          <p className="text-xs text-slate-500">
            Export ready-to-print university standard documents generated dynamically by OpenPDF & Apache POI engines.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 max-w-xl">
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Select Exam Session</label>
            <select
              value={selectedSessionId}
              onChange={(e) => setSelectedSessionId(e.target.value)}
              className="w-full text-xs px-3 py-2 rounded-xl border border-slate-200 bg-white font-medium text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
            >
              {examSessions.map(s => (
                <option key={s.id} value={s.id}>
                  {s.sessionDate} ({s.sessionType}) - {s.examTitle}
                </option>
              ))}
              {examSessions.length === 0 && <option value="">Active Session</option>}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Target Room Filter (Optional)</label>
            <select
              value={selectedRoomId}
              onChange={(e) => setSelectedRoomId(e.target.value)}
              className="w-full text-xs px-3 py-2 rounded-xl border border-slate-200 bg-white font-medium text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
            >
              <option value="">All Allocated Rooms</option>
              {classrooms.map(r => (
                <option key={r.id} value={r.id}>{r.roomNumber} ({r.totalCapacity} seats)</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Reports Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Report 1: Notice Board Seating Matrix PDF */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4 flex flex-col justify-between hover:border-indigo-300 transition-all">
          <div className="space-y-3">
            <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-700 flex items-center justify-center font-bold">
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider text-indigo-600 bg-indigo-50 px-2 py-0.5 rounded-full border border-indigo-100">
                Official Notice Board
              </span>
              <h4 className="text-base font-bold text-slate-900 mt-1">Main Seating Arrangement PDF</h4>
              <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                Matches the university notice board format: classroom-wise grouping with condensed consecutive register number ranges (e.g. VJC23EC001 - VJC23EC010) and branch splits.
              </p>
            </div>
            <ul className="text-[11px] text-slate-600 space-y-1">
              <li className="flex items-center space-x-1.5">
                <span className="text-emerald-500 font-bold">✓</span>
                <span>KTU Viswajyothi College exact university layout</span>
              </li>
              <li className="flex items-center space-x-1.5">
                <span className="text-emerald-500 font-bold">✓</span>
                <span>Branch-wise candidate code sorting</span>
              </li>
              <li className="flex items-center space-x-1.5">
                <span className="text-emerald-500 font-bold">✓</span>
                <span>Chief Superintendent verification stamp footer</span>
              </li>
            </ul>
          </div>

          <a
            href={api.getNoticeBoardPdfUrl(selectedSessionId)}
            target="_blank"
            rel="noreferrer"
            className="w-full py-2.5 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl shadow-sm transition-all flex items-center justify-center space-x-2"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            <span>Download Notice Board PDF</span>
          </a>
        </div>

        {/* Report 2: Room Attendance & Signature Sheet PDF */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4 flex flex-col justify-between hover:border-indigo-300 transition-all">
          <div className="space-y-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center font-bold">
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
              </svg>
            </div>
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-100">
                Invigilation Verification
              </span>
              <h4 className="text-base font-bold text-slate-900 mt-1">Room Attendance & Signature Sheet PDF</h4>
              <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                Candidate roster per classroom ordered by seat position (Row × Col) with student signature boxes, booklet barcode columns, and invigilator verification tallies.
              </p>
            </div>
            <ul className="text-[11px] text-slate-600 space-y-1">
              <li className="flex items-center space-x-1.5">
                <span className="text-emerald-500 font-bold">✓</span>
                <span>Seat coordinates (R1-C1) and candidate photo-ID codes</span>
              </li>
              <li className="flex items-center space-x-1.5">
                <span className="text-emerald-500 font-bold">✓</span>
                <span>Main answer book barcode serial entry fields</span>
              </li>
              <li className="flex items-center space-x-1.5">
                <span className="text-emerald-500 font-bold">✓</span>
                <span>Present/Absent/Malpractice reconciliation box</span>
              </li>
            </ul>
          </div>

          <a
            href={api.getAttendancePdfUrl(selectedSessionId, selectedRoomId)}
            target="_blank"
            rel="noreferrer"
            className="w-full py-2.5 px-4 bg-slate-900 hover:bg-slate-800 text-white font-semibold text-xs rounded-xl shadow-sm transition-all flex items-center justify-center space-x-2"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            <span>Download Attendance Sheets PDF</span>
          </a>
        </div>

        {/* Report 3: Invigilation Duty Excel Master */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4 flex flex-col justify-between hover:border-indigo-300 transition-all">
          <div className="space-y-3">
            <div className="w-10 h-10 rounded-xl bg-teal-50 text-teal-700 flex items-center justify-center font-bold">
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M3 14h18m-9-4v8m-7 0h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider text-teal-600 bg-teal-50 px-2 py-0.5 rounded-full border border-teal-100">
                Staff Administration
              </span>
              <h4 className="text-base font-bold text-slate-900 mt-1">Invigilation Duty Master Schedule (Excel)</h4>
              <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                Detailed spreadsheet mapping faculty invigilators to exam rooms, confirming department neutrality verification, and auditing duty distribution.
              </p>
            </div>
          </div>

          <a
            href={api.getDutyExcelUrl(selectedSessionId)}
            target="_blank"
            rel="noreferrer"
            className="w-full py-2.5 px-4 bg-teal-600 hover:bg-teal-700 text-white font-semibold text-xs rounded-xl shadow-sm transition-all flex items-center justify-center space-x-2"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            <span>Download Duty Master (.xlsx)</span>
          </a>
        </div>

        {/* Report 4: Invigilation Duty PDF Schedule */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4 flex flex-col justify-between hover:border-indigo-300 transition-all">
          <div className="space-y-3">
            <div className="w-10 h-10 rounded-xl bg-rose-50 text-rose-700 flex items-center justify-center font-bold">
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
              </svg>
            </div>
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider text-rose-600 bg-rose-50 px-2 py-0.5 rounded-full border border-rose-100">
                Staff Circular
              </span>
              <h4 className="text-base font-bold text-slate-900 mt-1">Invigilation Duty Schedule (PDF)</h4>
              <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                Landscape formatted staff circular ready to print or email to departments with signatures of Chief Superintendent & Senior Superintendent.
              </p>
            </div>
          </div>

          <a
            href={api.getDutyPdfUrl(selectedSessionId)}
            target="_blank"
            rel="noreferrer"
            className="w-full py-2.5 px-4 bg-rose-600 hover:bg-rose-700 text-white font-semibold text-xs rounded-xl shadow-sm transition-all flex items-center justify-center space-x-2"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            <span>Download Duty PDF</span>
          </a>
        </div>
      </div>
    </div>
  );
}

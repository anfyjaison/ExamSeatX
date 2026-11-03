import React, { useState } from 'react';

export default function SeatingMatrixView({ roomMatrices, examSessions, onRefresh, api }) {
  const [selectedRoomNumber, setSelectedRoomNumber] = useState(
    roomMatrices.length > 0 ? roomMatrices[0].roomNumber : ''
  );
  const [highlightDept, setHighlightDept] = useState('ALL');
  const [searchReg, setSearchReg] = useState('');

  const activeMatrix = roomMatrices.find(m => m.roomNumber === selectedRoomNumber) || roomMatrices[0];

  const getBranchBadgeStyle = (branch) => {
    switch ((branch || '').toUpperCase()) {
      case 'CS': return 'bg-blue-50 text-blue-700 border-blue-200';
      case 'AD': return 'bg-purple-50 text-purple-700 border-purple-200';
      case 'CSD': return 'bg-teal-50 text-teal-700 border-teal-200';
      case 'EC': return 'bg-orange-50 text-orange-700 border-orange-200';
      case 'EE': return 'bg-amber-50 text-amber-700 border-amber-200';
      case 'IT': return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      case 'ME': return 'bg-rose-50 text-rose-700 border-rose-200';
      case 'CE': return 'bg-sky-50 text-sky-700 border-sky-200';
      case 'HMCT': return 'bg-pink-50 text-pink-700 border-pink-200';
      case 'MBA': return 'bg-indigo-50 text-indigo-700 border-indigo-200';
      default: return 'bg-slate-50 text-slate-700 border-slate-200';
    }
  };

  if (!activeMatrix) {
    return (
      <div className="bg-white p-12 rounded-2xl border border-slate-200 text-center space-y-3 shadow-sm">
        <div className="w-12 h-12 bg-indigo-50 text-indigo-600 rounded-2xl flex items-center justify-center mx-auto">
          <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
          </svg>
        </div>
        <h3 className="text-base font-bold text-slate-900">No Seating Matrix Computed Yet</h3>
        <p className="text-xs text-slate-500 max-w-sm mx-auto">
          Switch to the <strong>CSP Seating Engine</strong> tab to generate 8-way non-adjacent seating plans across all active classrooms.
        </p>
      </div>
    );
  }

  // Group seats by row, then by col
  const rowGroups = {};
  for (let r = 1; r <= activeMatrix.rowCount; r++) {
    rowGroups[r] = {};
    for (let c = 1; c <= activeMatrix.colCount; c++) {
      rowGroups[r][c] = [];
    }
  }

  activeMatrix.seats.forEach(s => {
    if (rowGroups[s.rowNum] && rowGroups[s.rowNum][s.colNum]) {
      rowGroups[s.rowNum][s.colNum].push(s);
    }
  });

  return (
    <div className="space-y-6">
      {/* Room Selector Pills & Toolbar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-sm space-y-4">
        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
          <div>
            <h3 className="text-sm font-bold text-slate-900">Visual Classroom Seating Map</h3>
            <p className="text-xs text-slate-400">8-way non-adjacent desk grid layout with branch tag colors</p>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <div className="relative w-48">
              <input
                type="text"
                placeholder="Find candidate (reg no)..."
                value={searchReg}
                onChange={(e) => setSearchReg(e.target.value)}
                className="w-full pl-8 pr-3 py-1.5 text-xs rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
              <svg className="w-3.5 h-3.5 text-slate-400 absolute left-2.5 top-2.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>

            <select
              value={highlightDept}
              onChange={(e) => setHighlightDept(e.target.value)}
              className="text-xs px-3 py-1.5 rounded-xl border border-slate-200 bg-white font-medium text-slate-700 focus:outline-none"
            >
              <option value="ALL">Highlight All Branches</option>
              {Object.keys(activeMatrix.departmentCounts || {}).map(dept => (
                <option key={dept} value={dept}>{dept} ({activeMatrix.departmentCounts[dept]})</option>
              ))}
            </select>
          </div>
        </div>

        {/* Room Tab Pills */}
        <div className="flex space-x-2 overflow-x-auto pb-1 pt-1 border-t border-slate-100">
          {roomMatrices.map((m) => (
            <button
              key={m.roomNumber}
              onClick={() => setSelectedRoomNumber(m.roomNumber)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 flex items-center space-x-1.5 ${
                selectedRoomNumber === m.roomNumber
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200 hover:text-slate-900'
              }`}
            >
              <span>{m.roomNumber}</span>
              <span className={`text-[10px] px-1.5 py-0.2 rounded-full ${
                selectedRoomNumber === m.roomNumber ? 'bg-indigo-700 text-indigo-100' : 'bg-slate-200 text-slate-500'
              }`}>
                {m.occupiedSeats}/{m.totalCapacity}
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* Selected Room Details Bar */}
      <div className="bg-slate-900 text-white p-5 rounded-2xl shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center space-x-2">
            <span className="text-xl font-black tracking-tight">{activeMatrix.roomNumber}</span>
            <span className="bg-indigo-500/30 text-indigo-200 text-xs px-2.5 py-0.5 rounded-full font-bold border border-indigo-400/30">
              {activeMatrix.occupiedSeats} / {activeMatrix.totalCapacity} Seats Occupied
            </span>
            {activeMatrix.cctvAvailable && (
              <span className="bg-emerald-500/20 text-emerald-300 text-[10px] px-2 py-0.5 rounded-full font-semibold border border-emerald-400/30">
                CCTV Monitored
              </span>
            )}
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Layout: {activeMatrix.rowCount} Rows × {activeMatrix.colCount} Columns | {activeMatrix.benchCapacity} Students per Bench
          </p>
        </div>

        {/* Department breakdown chips */}
        <div className="flex flex-wrap items-center gap-1.5">
          {Object.entries(activeMatrix.departmentCounts || {}).map(([branch, count]) => (
            <span
              key={branch}
              className={`text-[11px] font-bold px-2.5 py-1 rounded-lg border ${getBranchBadgeStyle(branch)}`}
            >
              {branch}: {count}
            </span>
          ))}
        </div>
      </div>

      {/* Podium / Black Board Indicator */}
      <div className="text-center">
        <div className="inline-block bg-slate-200 border border-slate-300 text-slate-600 text-xs font-bold uppercase tracking-widest px-12 py-1.5 rounded-lg shadow-inner">
          [ Front • Podium / Whiteboard Area ]
        </div>
      </div>

      {/* Physical Classroom Grid View */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm overflow-x-auto">
        <div className="min-w-fit space-y-4">
          {Object.keys(rowGroups).map((rowIdx) => (
            <div key={rowIdx} className="flex items-center space-x-3">
              <span className="w-12 text-xs font-bold text-slate-400 shrink-0 text-right">
                Row {rowIdx}
              </span>

              <div className="flex items-center space-x-4">
                {Object.keys(rowGroups[rowIdx]).map((colIdx) => {
                  const benchSeats = rowGroups[rowIdx][colIdx];
                  return (
                    <div
                      key={colIdx}
                      className="p-2 bg-slate-50 border border-slate-200/90 rounded-xl flex items-center space-x-2 shrink-0 shadow-xs hover:border-indigo-300 transition-all"
                    >
                      {benchSeats.map((seat, seatIdx) => {
                        const isStudent = Boolean(seat.studentId);
                        const isHighlight = highlightDept === 'ALL' || seat.branchCode === highlightDept;
                        const isSearched = searchReg.trim() && seat.registerNumber && 
                                           seat.registerNumber.toLowerCase().includes(searchReg.toLowerCase());

                        return (
                          <div
                            key={seatIdx}
                            className={`w-36 p-2 rounded-lg border text-left transition-all ${
                              !isStudent
                                ? 'bg-white border-dashed border-slate-200 text-slate-300'
                                : seat.conflict
                                ? 'bg-rose-50 border-rose-300 text-rose-900 ring-2 ring-rose-400'
                                : isSearched
                                ? 'bg-amber-100 border-amber-400 ring-2 ring-amber-500 shadow-sm'
                                : isHighlight
                                ? 'bg-white border-slate-200 shadow-xs'
                                : 'bg-slate-100/60 border-slate-200 opacity-40'
                            }`}
                          >
                            <div className="flex items-center justify-between text-[10px] mb-1">
                              <span className="font-mono text-slate-400">{seat.seatLabel}</span>
                              {seat.branchCode && (
                                <span className={`px-1.5 py-0.2 rounded font-bold uppercase text-[9px] border ${getBranchBadgeStyle(seat.branchCode)}`}>
                                  {seat.branchCode}
                                </span>
                              )}
                            </div>

                            {isStudent ? (
                              <div>
                                <div className="font-mono font-bold text-xs text-slate-900 truncate">
                                  {seat.registerNumber}
                                </div>
                                <div className="text-[11px] text-slate-600 truncate font-medium">
                                  {seat.studentName}
                                </div>
                                <div className="flex items-center justify-between text-[10px] text-slate-400 mt-1 pt-1 border-t border-slate-100">
                                  <span>{seat.subjectCode}</span>
                                  <span className={`font-semibold ${seat.examType === 'Supplementary' ? 'text-amber-600' : 'text-slate-500'}`}>
                                    {seat.examType === 'Supplementary' ? 'Supple' : 'Regular'}
                                  </span>
                                </div>
                                {seat.conflict && (
                                  <div className="text-[9px] text-rose-600 font-bold mt-1 bg-rose-100/60 p-0.5 rounded">
                                    ⚠ Conflict: {seat.conflictReason}
                                  </div>
                                )}
                              </div>
                            ) : (
                              <div className="py-2 text-center text-[10px] text-slate-300 font-medium">
                                Empty Desk
                              </div>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  );
                })}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

import React, { useState } from 'react';

export default function SeatingEngine({ examSessions, classrooms, students, onAllocationComplete, onNavigateToMatrix, api }) {
  const [selectedSessionId, setSelectedSessionId] = useState(
    examSessions.length > 0 ? examSessions[0].id : ''
  );
  const [enforceDiagonal, setEnforceDiagonal] = useState(true);
  const [autoAssignInvigilators, setAutoAssignInvigilators] = useState(true);
  const [running, setRunning] = useState(false);
  const [lastResult, setLastResult] = useState(null);
  const [errorMsg, setErrorMsg] = useState('');

  const activeClassrooms = classrooms.filter(r => r.active);
  const totalCapacity = activeClassrooms.reduce((sum, r) => sum + r.totalCapacity, 0);

  const handleRunCsp = async () => {
    try {
      setRunning(true);
      setErrorMsg('');
      const payload = {
        examSessionId: selectedSessionId || undefined,
        classroomIds: activeClassrooms.map(r => r.id),
        enforceNonAdjacency: true,
        enforceDiagonal: enforceDiagonal,
        autoAssignInvigilators: autoAssignInvigilators
      };

      const result = await api.runAllocation(payload);
      setLastResult(result);
      if (onAllocationComplete) onAllocationComplete(result);
    } catch (err) {
      setErrorMsg('Allocation execution failed: ' + err.message);
    } finally {
      setRunning(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Control Panel Header */}
      <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-indigo-900 text-white p-6 rounded-2xl shadow-md space-y-4">
        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
          <div className="space-y-1">
            <div className="flex items-center space-x-2">
              <span className="bg-indigo-500/30 text-indigo-200 text-xs px-2.5 py-0.5 rounded-full font-bold border border-indigo-400/30 uppercase">
                CSP Backtracking Engine
              </span>
              <h3 className="text-xl font-bold">Constraint Satisfaction Exam Seating</h3>
            </div>
            <p className="text-xs text-indigo-200/80 max-w-2xl leading-relaxed">
              Enforces strict 8-way spatial non-adjacency: students seated horizontally, vertically, or diagonally adjacent
              MUST NOT share the same department or subject code. Greedy placement is followed by intelligent backtracking to automatically resolve bottlenecks.
            </p>
          </div>

          <button
            onClick={handleRunCsp}
            disabled={running || students.length === 0 || activeClassrooms.length === 0}
            className={`px-6 py-3 rounded-xl font-bold text-sm shadow-lg flex items-center space-x-2 transition-all ${
              running
                ? 'bg-indigo-400 text-indigo-100 cursor-not-allowed'
                : 'bg-indigo-500 hover:bg-indigo-600 text-white shadow-indigo-500/25 active:scale-95'
            }`}
          >
            {running ? (
              <>
                <svg className="animate-spin w-4 h-4 text-white" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                </svg>
                <span>Solving CSP Constraints...</span>
              </>
            ) : (
              <>
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>Execute Seating Solver</span>
              </>
            )}
          </button>
        </div>

        {/* Solver Toggles */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pt-4 border-t border-indigo-900/60 text-xs">
          <label className="flex items-center space-x-2.5 bg-white/5 p-3 rounded-xl border border-white/10 cursor-pointer hover:bg-white/10 transition-colors">
            <input
              type="checkbox"
              checked={true}
              disabled={true}
              className="rounded text-indigo-500 focus:ring-0 w-4 h-4"
            />
            <div>
              <span className="font-bold text-white block">Horizontal & Vertical Non-Adjacency</span>
              <span className="text-[11px] text-slate-400">Strict different branch/subject constraint</span>
            </div>
          </label>

          <label className="flex items-center space-x-2.5 bg-white/5 p-3 rounded-xl border border-white/10 cursor-pointer hover:bg-white/10 transition-colors">
            <input
              type="checkbox"
              checked={enforceDiagonal}
              onChange={(e) => setEnforceDiagonal(e.target.checked)}
              className="rounded text-indigo-500 focus:ring-0 w-4 h-4"
            />
            <div>
              <span className="font-bold text-white block">Diagonal Non-Adjacency (8-Way)</span>
              <span className="text-[11px] text-slate-400">Check diagonal desks in grid</span>
            </div>
          </label>

          <label className="flex items-center space-x-2.5 bg-white/5 p-3 rounded-xl border border-white/10 cursor-pointer hover:bg-white/10 transition-colors">
            <input
              type="checkbox"
              checked={autoAssignInvigilators}
              onChange={(e) => setAutoAssignInvigilators(e.target.checked)}
              className="rounded text-indigo-500 focus:ring-0 w-4 h-4"
            />
            <div>
              <span className="font-bold text-white block">Auto-Assign Invigilators</span>
              <span className="text-[11px] text-slate-400">Enforce department neutrality</span>
            </div>
          </label>
        </div>
      </div>

      {errorMsg && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-center space-x-2">
          <span>⚠ {errorMsg}</span>
        </div>
      )}

      {/* Capacity & Readiness Checker */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-1">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider block">Eligible Candidates</span>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl font-black text-slate-900">{students.length}</span>
            <span className="text-xs text-slate-500">students in roster</span>
          </div>
          <p className="text-[11px] text-slate-400 pt-1">
            Regular & Supplementary candidates will be interleaved.
          </p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-1">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider block">Classroom Capacity</span>
          <div className="flex items-baseline space-x-2">
            <span className="text-2xl font-black text-indigo-600">{totalCapacity}</span>
            <span className="text-xs text-slate-500">seats in {activeClassrooms.length} active halls</span>
          </div>
          <p className="text-[11px] text-slate-400 pt-1">
            {totalCapacity >= students.length ? '✓ Sufficient capacity for all students' : '⚠ Capacity deficit'}
          </p>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm space-y-1">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider block">Constraint Strategy</span>
          <div className="text-sm font-bold text-emerald-700">
            Greedy Placement + Backtracking
          </div>
          <p className="text-[11px] text-slate-400 pt-1">
            MRV forward checking + structural bottleneck resolver
          </p>
        </div>
      </div>

      {/* Results Telemetry Panel */}
      {lastResult && (
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 space-y-6 animate-in fade-in zoom-in-95">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-4 border-b border-slate-100 gap-4">
            <div>
              <div className="flex items-center space-x-2">
                <span className={`w-3 h-3 rounded-full ${lastResult.constraintViolations === 0 ? 'bg-emerald-500' : 'bg-amber-500'}`} />
                <h4 className="text-base font-bold text-slate-900">Allocation Execution Telemetry</h4>
              </div>
              <p className="text-xs text-slate-400 mt-0.5">{lastResult.message}</p>
            </div>

            <button
              onClick={onNavigateToMatrix}
              className="px-4 py-2 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 font-bold text-xs rounded-xl border border-indigo-200 transition-colors flex items-center space-x-1.5"
            >
              <span>Inspect Visual Seating Map →</span>
            </button>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
            <div className="p-4 bg-slate-50 rounded-xl border border-slate-100 text-center">
              <span className="text-[11px] font-semibold text-slate-400 block mb-0.5">Students Placed</span>
              <span className="text-2xl font-black text-slate-900">{lastResult.totalStudentsAllocated}</span>
            </div>
            <div className="p-4 bg-slate-50 rounded-xl border border-slate-100 text-center">
              <span className="text-[11px] font-semibold text-slate-400 block mb-0.5">Unallocated</span>
              <span className="text-2xl font-black text-slate-900">{lastResult.unallocatedStudentsCount}</span>
            </div>
            <div className="p-4 bg-slate-50 rounded-xl border border-slate-100 text-center">
              <span className="text-[11px] font-semibold text-slate-400 block mb-0.5">Rooms Utilized</span>
              <span className="text-2xl font-black text-indigo-600">{lastResult.totalRoomsUsed}</span>
            </div>
            <div className="p-4 bg-slate-50 rounded-xl border border-slate-100 text-center">
              <span className="text-[11px] font-semibold text-slate-400 block mb-0.5">Execution Time</span>
              <span className="text-2xl font-black text-slate-900">{lastResult.executionTimeMs} ms</span>
            </div>
            <div className="p-4 bg-slate-50 rounded-xl border border-slate-100 text-center">
              <span className="text-[11px] font-semibold text-slate-400 block mb-0.5">Constraint Violations</span>
              <span className={`text-2xl font-black ${lastResult.constraintViolations === 0 ? 'text-emerald-600' : 'text-rose-600'}`}>
                {lastResult.constraintViolations}
              </span>
            </div>
          </div>

          {lastResult.warnings && lastResult.warnings.length > 0 && (
            <div className="p-4 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 text-xs space-y-1">
              <span className="font-bold block">Solver Advisories:</span>
              {lastResult.warnings.map((w, idx) => (
                <div key={idx}>• {w}</div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

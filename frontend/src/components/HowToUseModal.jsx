import React, { useState } from 'react';

export default function HowToUseModal({ isOpen, onClose }) {
  const [activeStep, setActiveStep] = useState(1);

  if (!isOpen) return null;

  const steps = [
    {
      step: 1,
      title: "1. Classroom & Hall Setup",
      subtitle: "Define Physical Exam Venues",
      desc: "Configure classrooms with Room Numbers, Row and Column counts, Bench Capacity (1 or 2 students per bench), and CCTV status. The system computes total seat capacity automatically. You can temporarily disable rooms for maintenance without deleting them."
    },
    {
      step: 2,
      title: "2. Flexible Roster Parsing",
      subtitle: "Import Student Rosters (.xlsx / .csv)",
      desc: "ExamSeatX's Apache POI parser dynamically recognizes variable headers across different university formats (e.g. 'Register No', 'Class Roll No', 'Branch', 'Department', 'Slot', 'Course'). Supplementary and Regular students are automatically categorized based on cohorts."
    },
    {
      step: 3,
      title: "3. Invigilator Neutrality Engine",
      subtitle: "Department-Neutral Duty Allocation",
      desc: "Manage faculty records and run the automated duty assignment algorithm. To guarantee integrity, ExamSeatX strictly enforces department neutrality: an invigilator is NEVER assigned to an exam room containing students from their own department."
    },
    {
      step: 4,
      title: "4. Intelligent CSP Seating",
      subtitle: "Greedy + Backtracking 8-Way Non-Adjacency",
      desc: "Run the Constraint Satisfaction Problem (CSP) solver. It enforces that students seated horizontally, vertically, or diagonally adjacent do NOT share the same department or subject code. Intelligent backtracking automatically resolves structural bottlenecks."
    },
    {
      step: 5,
      title: "5. Official PDF Report Generation",
      subtitle: "Notice Board & Attendance Sheets",
      desc: "Export publication-ready documents formatted according to official university examination standards: Notice Board Seating Matrix with condensed register number ranges, and Room Attendance Sheets with signature and verification blocks."
    }
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full border border-slate-200 overflow-hidden animate-in fade-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-indigo-900 p-6 text-white flex justify-between items-center">
          <div>
            <div className="flex items-center space-x-2">
              <span className="bg-indigo-500/30 text-indigo-200 text-xs px-2.5 py-0.5 rounded-full font-semibold border border-indigo-400/30 uppercase tracking-wider">
                User Guide
              </span>
              <h2 className="text-xl font-bold tracking-tight">How to Use ExamSeatX</h2>
            </div>
            <p className="text-xs text-indigo-200/80 mt-1">Step-by-step examination management and intelligent seating workflow</p>
          </div>
          <button
            onClick={onClose}
            className="text-indigo-200 hover:text-white p-2 rounded-xl hover:bg-white/10 transition-colors"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Step Navigation Pill Tabs */}
        <div className="flex border-b border-slate-100 bg-slate-50 px-6 py-2 gap-2 overflow-x-auto">
          {steps.map((s) => (
            <button
              key={s.step}
              onClick={() => setActiveStep(s.step)}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all shrink-0 ${
                activeStep === s.step
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/60'
              }`}
            >
              Step {s.step}
            </button>
          ))}
        </div>

        {/* Body Content */}
        <div className="p-6">
          {steps.map((s) => activeStep === s.step && (
            <div key={s.step} className="space-y-4">
              <div className="flex items-center space-x-3">
                <span className="w-9 h-9 rounded-xl bg-indigo-100 text-indigo-700 flex items-center justify-center font-bold text-sm">
                  {s.step}
                </span>
                <div>
                  <h3 className="text-lg font-bold text-slate-900">{s.title}</h3>
                  <p className="text-xs font-medium text-indigo-600">{s.subtitle}</p>
                </div>
              </div>

              <p className="text-sm text-slate-600 leading-relaxed bg-slate-50 p-4 rounded-xl border border-slate-100">
                {s.desc}
              </p>

              <div className="grid grid-cols-2 gap-3 pt-2">
                <div className="p-3 rounded-lg border border-indigo-100 bg-indigo-50/40 text-xs">
                  <span className="font-semibold text-indigo-900 block mb-0.5">Key Tip</span>
                  <span className="text-slate-600">
                    {s.step === 1 && "Bench capacity 2 allows two students to share a bench provided they belong to distinct branches."}
                    {s.step === 2 && "Missing columns like Branch Code or Exam Type are automatically inferred from KTU register prefixes."}
                    {s.step === 3 && "You can export the duty schedule as a formatted Excel or PDF for notice distribution."}
                    {s.step === 4 && "Check the Visual Seating Map to inspect seat layouts with branch color tags and verify zero conflicts."}
                    {s.step === 5 && "The Notice Board PDF groups contiguous register numbers into clean ranges like VJC23EC001 - VJC23EC010."}
                  </span>
                </div>
                <div className="p-3 rounded-lg border border-slate-200 bg-white text-xs">
                  <span className="font-semibold text-slate-900 block mb-0.5">Requirement</span>
                  <span className="text-slate-600">
                    {s.step === 1 && "Active classrooms are automatically used as target venues during CSP runs."}
                    {s.step === 2 && "Uploaded rosters persist into the system database for full editing & auditing."}
                    {s.step === 3 && "Neutrality check flags any accidental same-department assignments instantly."}
                    {s.step === 4 && "8-way constraint checks diagonal, vertical, and horizontal adjacent seats."}
                    {s.step === 5 && "Attendance sheets include answer booklet number and signature columns."}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Footer */}
        <div className="bg-slate-50 px-6 py-4 border-t border-slate-100 flex justify-between items-center">
          <div className="text-xs text-slate-400">
            Step {activeStep} of {steps.length}
          </div>
          <div className="flex space-x-2">
            {activeStep > 1 && (
              <button
                onClick={() => setActiveStep(activeStep - 1)}
                className="px-3.5 py-1.5 text-xs font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-200 rounded-lg transition-colors"
              >
                Previous
              </button>
            )}
            {activeStep < steps.length ? (
              <button
                onClick={() => setActiveStep(activeStep + 1)}
                className="px-4 py-1.5 text-xs font-semibold bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 shadow-sm transition-colors"
              >
                Next Step →
              </button>
            ) : (
              <button
                onClick={onClose}
                className="px-4 py-1.5 text-xs font-semibold bg-slate-900 text-white rounded-lg hover:bg-slate-800 shadow-sm transition-colors"
              >
                Got it, let's start!
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

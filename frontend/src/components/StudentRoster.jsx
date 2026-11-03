import React, { useState } from 'react';

export default function StudentRoster({ students, onRefresh, api }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [formData, setFormData] = useState({
    registerNumber: '',
    studentName: '',
    department: 'Computer Science',
    branchCode: 'CS',
    semester: 'S6',
    subjectCode: 'CST304',
    subjectName: 'Computer Graphics & Image Processing',
    examType: 'Regular',
    slot: 'SLOT B'
  });

  const [search, setSearch] = useState('');
  const [examTypeFilter, setExamTypeFilter] = useState('ALL');
  const [deptFilter, setDeptFilter] = useState('ALL');
  const [uploading, setUploading] = useState(false);
  const [uploadStats, setUploadStats] = useState(null);
  const [errorMsg, setErrorMsg] = useState('');

  const departmentsList = [
    { code: 'AD', name: 'Artificial Intelligence and Data Science', defSub: 'AIT304', defTitle: 'Deep Learning Algorithms' },
    { code: 'CS', name: 'Computer Science', defSub: 'CST304', defTitle: 'Computer Graphics & Image Processing' },
    { code: 'CSD', name: 'Computer Science and Design', defSub: 'CDT304', defTitle: 'UI/UX Design' },
    { code: 'EC', name: 'Electronics and Communication', defSub: 'ECT304', defTitle: 'VLSI Design' },
    { code: 'EE', name: 'Electrical and Electronics', defSub: 'EET304', defTitle: 'Microprocessors & Microcontrollers' },
    { code: 'IT', name: 'Information Technology', defSub: 'ITT304', defTitle: 'Web Programming' },
    { code: 'ME', name: 'Mechanical', defSub: 'MET304', defTitle: 'Dynamics and Control of Machinery' },
    { code: 'CE', name: 'Civil Engineering', defSub: 'CET304', defTitle: 'Design of Hydraulic Structures' },
    { code: 'HMCT', name: 'Hotel Management & Catering Tech', defSub: 'HMT202', defTitle: 'Hospitality Management' },
    { code: 'MBA', name: 'Management Studies', defSub: 'MBA202', defTitle: 'Financial Management' }
  ];

  const handleDeptSelect = (deptName) => {
    const found = departmentsList.find(d => d.name === deptName);
    if (found) {
      setFormData(prev => ({
        ...prev,
        department: found.name,
        branchCode: found.code,
        subjectCode: found.defSub,
        subjectName: found.defTitle
      }));
    } else {
      setFormData(prev => ({ ...prev, department: deptName }));
    }
  };

  const openCreateModal = () => {
    setEditingStudent(null);
    setFormData({
      registerNumber: '',
      studentName: '',
      department: 'Computer Science',
      branchCode: 'CS',
      semester: 'S6',
      subjectCode: 'CST304',
      subjectName: 'Computer Graphics & Image Processing',
      examType: 'Regular',
      slot: 'SLOT B'
    });
    setErrorMsg('');
    setIsModalOpen(true);
  };

  const openEditModal = (student) => {
    setEditingStudent(student);
    setFormData({
      registerNumber: student.registerNumber,
      studentName: student.studentName,
      department: student.department,
      branchCode: student.branchCode,
      semester: student.semester,
      subjectCode: student.subjectCode,
      subjectName: student.subjectName,
      examType: student.examType,
      slot: student.slot || 'SLOT B'
    });
    setErrorMsg('');
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.registerNumber.trim()) {
      setErrorMsg('Register Number is required');
      return;
    }
    try {
      if (editingStudent) {
        await api.updateStudent(editingStudent.id, formData);
      } else {
        await api.createStudent(formData);
      }
      setIsModalOpen(false);
      onRefresh();
    } catch (err) {
      setErrorMsg(err.message || 'Error saving student');
    }
  };

  const handleDelete = async (id, reg) => {
    if (window.confirm(`Delete student record ${reg}?`)) {
      try {
        await api.deleteStudent(id);
        onRefresh();
      } catch (err) {
        alert('Failed to delete student');
      }
    }
  };

  const handleClearAll = async () => {
    if (window.confirm('Are you sure you want to remove ALL student records from the roster?')) {
      try {
        await api.clearAllStudents();
        setUploadStats(null);
        onRefresh();
      } catch (err) {
        alert('Failed to clear students');
      }
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      setUploading(true);
      setErrorMsg('');
      const res = await api.uploadRoster(file);
      setUploadStats(res);
      onRefresh();
    } catch (err) {
      setErrorMsg('Import failed: ' + err.message);
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  };

  // Filter students
  const filtered = students.filter(s => {
    const matchesSearch = s.registerNumber.toLowerCase().includes(search.toLowerCase()) ||
                          s.studentName.toLowerCase().includes(search.toLowerCase()) ||
                          s.subjectCode.toLowerCase().includes(search.toLowerCase());
    const matchesType = examTypeFilter === 'ALL' || s.examType.equalsIgnoreCase(examTypeFilter);
    const matchesDept = deptFilter === 'ALL' || s.branchCode.toUpperCase() === deptFilter.toUpperCase();
    return matchesSearch && matchesType && matchesDept;
  });

  const regularCount = students.filter(s => s.examType && s.examType.toLowerCase() === 'regular').length;
  const suppCount = students.filter(s => s.examType && s.examType.toLowerCase() === 'supplementary').length;

  return (
    <div className="space-y-6">
      {/* Roster Overview Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block mb-1">Total Enrolled</span>
          <span className="text-2xl font-black text-slate-900">{students.length}</span>
          <span className="text-xs text-slate-400 block mt-1">Students across all branches</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <span className="text-xs font-semibold text-emerald-600 uppercase tracking-wider block mb-1">Regular Candidates</span>
          <span className="text-2xl font-black text-emerald-700">{regularCount}</span>
          <span className="text-xs text-slate-400 block mt-1">Current cohort (VJC23)</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <span className="text-xs font-semibold text-amber-600 uppercase tracking-wider block mb-1">Supplementary Candidates</span>
          <span className="text-2xl font-black text-amber-700">{suppCount}</span>
          <span className="text-xs text-slate-400 block mt-1">Buffer cohorts (VJC19–VJC22)</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <span className="text-xs font-semibold text-indigo-600 uppercase tracking-wider block mb-1">Distinct Departments</span>
          <span className="text-2xl font-black text-indigo-700">
            {new Set(students.map(s => s.branchCode)).size}
          </span>
          <span className="text-xs text-slate-400 block mt-1">Active disciplines represented</span>
        </div>
      </div>

      {/* Flexible Excel Upload & Action Box */}
      <div className="bg-gradient-to-br from-indigo-900 via-slate-900 to-indigo-950 p-6 rounded-2xl text-white shadow-md">
        <div className="flex flex-col lg:flex-row items-start lg:items-center justify-between gap-6">
          <div className="space-y-1">
            <div className="flex items-center space-x-2">
              <span className="bg-indigo-500/30 text-indigo-200 text-xs px-2.5 py-0.5 rounded-full font-bold border border-indigo-400/30 uppercase">
                Apache POI Engine
              </span>
              <h3 className="text-lg font-bold">Dynamic Excel / CSV Roster Ingestion</h3>
            </div>
            <p className="text-xs text-indigo-200/80 max-w-xl">
              Upload university candidate lists in <span className="font-semibold text-white">.xlsx</span> or <span className="font-semibold text-white">.csv</span>.
              The parser automatically detects header variations (Register No, Class Roll No, Branch, Subject Code) and infers cohorts seamlessly.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <label className="cursor-pointer px-4 py-2 bg-indigo-500 hover:bg-indigo-600 text-white text-xs font-bold rounded-xl shadow-sm transition-all flex items-center space-x-2">
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
              <span>{uploading ? 'Parsing File...' : 'Import Excel / CSV Roster'}</span>
              <input
                type="file"
                accept=".xlsx,.xls,.csv"
                onChange={handleFileUpload}
                disabled={uploading}
                className="hidden"
              />
            </label>

            <button
              onClick={openCreateModal}
              className="px-4 py-2 bg-white/10 hover:bg-white/20 text-white text-xs font-semibold rounded-xl border border-white/20 transition-all flex items-center space-x-1.5"
            >
              <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              <span>Manual Add</span>
            </button>

            {students.length > 0 && (
              <button
                onClick={handleClearAll}
                className="px-3.5 py-2 bg-rose-500/20 hover:bg-rose-500/30 text-rose-200 text-xs font-semibold rounded-xl border border-rose-400/30 transition-all"
              >
                Clear Roster
              </button>
            )}
          </div>
        </div>

        {uploadStats && (
          <div className="mt-4 p-3 bg-emerald-500/20 border border-emerald-400/40 rounded-xl text-xs text-emerald-200 flex items-center justify-between">
            <span>
              ✓ <strong>{uploadStats.message}</strong> — Parsed {uploadStats.totalParsed} records ({uploadStats.inserted} inserted, {uploadStats.updated} updated).
            </span>
            <button onClick={() => setUploadStats(null)} className="text-emerald-300 hover:text-white text-xs">Dismiss</button>
          </div>
        )}
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col md:flex-row items-center justify-between gap-4">
        <div className="flex flex-wrap items-center gap-2 w-full md:w-auto">
          <div className="relative w-full sm:w-64">
            <input
              type="text"
              placeholder="Search register no, name..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 text-xs rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600"
            />
            <svg className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>

          <div className="flex items-center space-x-1 bg-slate-100 p-1 rounded-xl text-xs font-semibold">
            {['ALL', 'Regular', 'Supplementary'].map(type => (
              <button
                key={type}
                onClick={() => setExamTypeFilter(type)}
                className={`px-3 py-1.5 rounded-lg transition-all ${
                  examTypeFilter === type
                    ? 'bg-white text-indigo-700 shadow-xs'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                {type}
              </button>
            ))}
          </div>

          <select
            value={deptFilter}
            onChange={(e) => setDeptFilter(e.target.value)}
            className="text-xs px-3 py-2 rounded-xl border border-slate-200 bg-white font-medium text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
          >
            <option value="ALL">All Departments</option>
            {departmentsList.map(d => (
              <option key={d.code} value={d.code}>{d.code} - {d.name}</option>
            ))}
          </select>
        </div>

        <div className="text-xs text-slate-500">
          Showing <span className="font-bold text-slate-900">{filtered.length}</span> of {students.length} candidates
        </div>
      </div>

      {/* Student Roster Table */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-200/80 text-slate-500 font-bold uppercase tracking-wider">
                <th className="py-3 px-4">Register No</th>
                <th className="py-3 px-4">Student Name</th>
                <th className="py-3 px-4">Branch / Dept</th>
                <th className="py-3 px-4">Exam Type</th>
                <th className="py-3 px-4">Subject</th>
                <th className="py-3 px-4">Slot</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {filtered.slice(0, 100).map((st) => (
                <tr key={st.id} className="hover:bg-slate-50/70 transition-colors">
                  <td className="py-2.5 px-4 font-mono font-bold text-indigo-700">{st.registerNumber}</td>
                  <td className="py-2.5 px-4 font-medium text-slate-900">{st.studentName}</td>
                  <td className="py-2.5 px-4">
                    <span className="font-semibold text-slate-800">{st.branchCode}</span>
                    <span className="text-slate-400 text-[11px] block">{st.department}</span>
                  </td>
                  <td className="py-2.5 px-4">
                    <span
                      className={`px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wide border ${
                        st.examType && st.examType.toLowerCase() === 'supplementary'
                          ? 'bg-amber-50 text-amber-700 border-amber-200'
                          : 'bg-emerald-50 text-emerald-700 border-emerald-200'
                      }`}
                    >
                      {st.examType || 'Regular'}
                    </span>
                  </td>
                  <td className="py-2.5 px-4">
                    <span className="font-bold text-slate-900">{st.subjectCode}</span>
                    <span className="text-slate-400 text-[11px] block truncate max-w-xs">{st.subjectName}</span>
                  </td>
                  <td className="py-2.5 px-4 font-semibold text-slate-500">{st.slot || 'SLOT B'}</td>
                  <td className="py-2.5 px-4 text-right">
                    <div className="flex items-center justify-end space-x-1">
                      <button
                        onClick={() => openEditModal(st)}
                        className="p-1 text-slate-400 hover:text-indigo-600 rounded"
                        title="Edit"
                      >
                        <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </button>
                      <button
                        onClick={() => handleDelete(st.id, st.registerNumber)}
                        className="p-1 text-slate-400 hover:text-rose-600 rounded"
                        title="Delete"
                      >
                        <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {filtered.length === 0 && (
                <tr>
                  <td colSpan={7} className="py-8 text-center text-slate-400">
                    No student records found matching the filter criteria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        {filtered.length > 100 && (
          <div className="p-3 text-center bg-slate-50 border-t border-slate-100 text-xs text-slate-500">
            Showing first 100 results. Use the search box above to locate specific candidates.
          </div>
        )}
      </div>

      {/* Manual Add / Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full border border-slate-200 overflow-hidden">
            <div className="bg-slate-900 p-5 text-white flex justify-between items-center">
              <h3 className="font-bold text-base">
                {editingStudent ? `Edit Candidate ${editingStudent.registerNumber}` : 'Add Student to Roster'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="text-slate-400 hover:text-white">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              {errorMsg && (
                <div className="p-3 rounded-lg bg-rose-50 border border-rose-200 text-rose-700 text-xs">
                  {errorMsg}
                </div>
              )}

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Register Number *</label>
                  <input
                    type="text"
                    placeholder="e.g. VJC23CS001"
                    value={formData.registerNumber}
                    onChange={(e) => setFormData({ ...formData, registerNumber: e.target.value.toUpperCase() })}
                    className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 font-mono"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Student Name *</label>
                  <input
                    type="text"
                    placeholder="Candidate full name"
                    value={formData.studentName}
                    onChange={(e) => setFormData({ ...formData, studentName: e.target.value })}
                    className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    required
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Department</label>
                  <select
                    value={formData.department}
                    onChange={(e) => handleDeptSelect(e.target.value)}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  >
                    {departmentsList.map(d => (
                      <option key={d.code} value={d.name}>{d.name} ({d.code})</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Exam Type</label>
                  <select
                    value={formData.examType}
                    onChange={(e) => setFormData({ ...formData, examType: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  >
                    <option value="Regular">Regular</option>
                    <option value="Supplementary">Supplementary</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Subject Code</label>
                  <input
                    type="text"
                    value={formData.subjectCode}
                    onChange={(e) => setFormData({ ...formData, subjectCode: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Slot</label>
                  <input
                    type="text"
                    value={formData.slot}
                    onChange={(e) => setFormData({ ...formData, slot: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Subject Name</label>
                <input
                  type="text"
                  value={formData.subjectName}
                  onChange={(e) => setFormData({ ...formData, subjectName: e.target.value })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div className="flex justify-end space-x-2 pt-4 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 text-xs font-semibold bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl shadow-sm transition-colors"
                >
                  {editingStudent ? 'Update Candidate' : 'Save Candidate'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

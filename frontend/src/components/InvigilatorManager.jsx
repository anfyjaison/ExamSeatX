import React, { useState } from 'react';

export default function InvigilatorManager({ invigilators, duties, examSessions, onRefresh, api }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingInv, setEditingInv] = useState(null);
  const [formData, setFormData] = useState({
    facultyId: '',
    name: '',
    department: 'Civil Engineering',
    email: '',
    phone: '',
    maxDuties: 5,
    available: true
  });
  const [selectedSessionId, setSelectedSessionId] = useState(
    examSessions.length > 0 ? examSessions[0].id : ''
  );
  const [assigning, setAssigning] = useState(false);
  const [search, setSearch] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const departments = [
    'Civil Engineering',
    'Mechanical',
    'Electronics and Communication',
    'Electrical and Electronics',
    'Computer Science',
    'Information Technology',
    'Artificial Intelligence and Data Science',
    'Basic Science & Humanities',
    'Management Studies'
  ];

  const openCreateModal = () => {
    setEditingInv(null);
    setFormData({
      facultyId: `FAC-${Date.now().toString().slice(-4)}`,
      name: '',
      department: 'Civil Engineering',
      email: '',
      phone: '',
      maxDuties: 5,
      available: true
    });
    setErrorMsg('');
    setIsModalOpen(true);
  };

  const openEditModal = (inv) => {
    setEditingInv(inv);
    setFormData({
      facultyId: inv.facultyId,
      name: inv.name,
      department: inv.department,
      email: inv.email || '',
      phone: inv.phone || '',
      maxDuties: inv.maxDuties,
      available: inv.available
    });
    setErrorMsg('');
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim() || !formData.facultyId.trim()) {
      setErrorMsg('Faculty Name and Faculty ID are required.');
      return;
    }

    try {
      if (editingInv) {
        await api.updateInvigilator(editingInv.id, {
          ...editingInv,
          ...formData,
          maxDuties: parseInt(formData.maxDuties, 10)
        });
      } else {
        await api.createInvigilator({
          ...formData,
          maxDuties: parseInt(formData.maxDuties, 10)
        });
      }
      setIsModalOpen(false);
      onRefresh();
    } catch (err) {
      setErrorMsg(err.message || 'Error saving invigilator');
    }
  };

  const handleDelete = async (id, name) => {
    if (window.confirm(`Delete faculty record for ${name}?`)) {
      try {
        await api.deleteInvigilator(id);
        onRefresh();
      } catch (err) {
        alert('Failed to delete invigilator');
      }
    }
  };

  const handleAutoAssign = async () => {
    try {
      setAssigning(true);
      await api.autoAssignDuties(selectedSessionId || undefined);
      onRefresh();
    } catch (err) {
      alert('Auto assignment error: ' + err.message);
    } finally {
      setAssigning(false);
    }
  };

  const filteredInv = invigilators.filter(i => 
    i.name.toLowerCase().includes(search.toLowerCase()) ||
    i.facultyId.toLowerCase().includes(search.toLowerCase()) ||
    i.department.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Top Neutrality Control Banner */}
      <div className="bg-gradient-to-r from-indigo-950 via-slate-900 to-indigo-900 p-6 rounded-2xl text-white shadow-md">
        <div className="flex flex-col lg:flex-row items-start lg:items-center justify-between gap-6">
          <div className="space-y-1">
            <div className="flex items-center space-x-2">
              <span className="bg-emerald-500/30 text-emerald-300 text-xs px-2.5 py-0.5 rounded-full font-bold border border-emerald-400/30 uppercase">
                Department Neutrality Active
              </span>
              <h3 className="text-lg font-bold">Automated Invigilation Engine</h3>
            </div>
            <p className="text-xs text-indigo-200/80 max-w-xl">
              Strictly guarantees that no invigilator is assigned to an exam room where students from their own department or discipline are writing examinations.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={handleAutoAssign}
              disabled={assigning}
              className="px-5 py-2.5 bg-indigo-500 hover:bg-indigo-600 text-white text-xs font-bold rounded-xl shadow-md transition-all flex items-center space-x-2"
            >
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
              <span>{assigning ? 'Solving Duty Matching...' : 'Run Auto-Assignment'}</span>
            </button>

            <a
              href={api.getDutyExcelUrl(selectedSessionId)}
              target="_blank"
              rel="noreferrer"
              className="px-4 py-2 bg-white/10 hover:bg-white/20 text-white text-xs font-semibold rounded-xl border border-white/20 transition-all flex items-center space-x-1.5"
            >
              <svg className="w-3.5 h-3.5 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <span>Export Excel</span>
            </a>

            <a
              href={api.getDutyPdfUrl(selectedSessionId)}
              target="_blank"
              rel="noreferrer"
              className="px-4 py-2 bg-white/10 hover:bg-white/20 text-white text-xs font-semibold rounded-xl border border-white/20 transition-all flex items-center space-x-1.5"
            >
              <svg className="w-3.5 h-3.5 text-rose-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
              </svg>
              <span>Export PDF</span>
            </a>
          </div>
        </div>
      </div>

      {/* Duty Assignment Results Matrix */}
      {duties.length > 0 && (
        <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h4 className="text-sm font-bold text-slate-900">Current Duty Master Schedule</h4>
              <p className="text-xs text-slate-400">Allocated halls and assigned faculty invigilators</p>
            </div>
            <span className="text-xs font-bold text-indigo-700 bg-indigo-50 px-2.5 py-1 rounded-full border border-indigo-100">
              {duties.length} Rooms Covered
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
            {duties.map((d) => (
              <div key={d.assignmentId || d.roomNumber} className="p-4 rounded-xl border border-slate-100 bg-slate-50/60 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-black text-indigo-950 bg-indigo-100 px-2.5 py-0.5 rounded-lg">
                    Room {d.roomNumber}
                  </span>
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border uppercase ${
                    d.departmentNeutral
                      ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                      : 'bg-amber-50 text-amber-700 border-amber-200'
                  }`}>
                    {d.departmentNeutral ? '✓ Dept Neutral' : '⚠ Dept Alert'}
                  </span>
                </div>

                <div className="text-xs">
                  <span className="font-bold text-slate-900 block">{d.facultyName}</span>
                  <span className="text-slate-500 text-[11px] block">{d.facultyId} • {d.facultyDepartment}</span>
                </div>

                <div className="text-[11px] text-slate-600 pt-1 border-t border-slate-200/60 flex items-center justify-between">
                  <span>Students Allotted:</span>
                  <span className="font-bold text-slate-900">{d.totalStudentsInRoom} candidates</span>
                </div>

                <div className="text-[10px] text-slate-400 truncate">
                  Depts: {d.roomStudentDepartments ? d.roomStudentDepartments.join(', ') : 'None'}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Faculty Roster List Header */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="relative w-full sm:w-72">
          <input
            type="text"
            placeholder="Search faculty name, ID..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-xs rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600"
          />
          <svg className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <button
          onClick={openCreateModal}
          className="w-full sm:w-auto px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-xl shadow-sm transition-all flex items-center justify-center space-x-1.5"
        >
          <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          <span>Add Invigilator</span>
        </button>
      </div>

      {/* Faculty Table */}
      <div className="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
        <table className="w-full text-left text-xs border-collapse">
          <thead>
            <tr className="bg-slate-50 border-b border-slate-200/80 text-slate-500 font-bold uppercase tracking-wider">
              <th className="py-3 px-4">Faculty ID</th>
              <th className="py-3 px-4">Faculty Name</th>
              <th className="py-3 px-4">Department</th>
              <th className="py-3 px-4">Contact</th>
              <th className="py-3 px-4">Duty Load</th>
              <th className="py-3 px-4">Status</th>
              <th className="py-3 px-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-slate-700">
            {filteredInv.map((inv) => (
              <tr key={inv.id} className="hover:bg-slate-50/70 transition-colors">
                <td className="py-3 px-4 font-mono font-bold text-slate-900">{inv.facultyId}</td>
                <td className="py-3 px-4 font-semibold text-slate-900">{inv.name}</td>
                <td className="py-3 px-4">
                  <span className="bg-slate-100 text-slate-700 px-2 py-0.5 rounded-md font-medium text-[11px]">
                    {inv.department}
                  </span>
                </td>
                <td className="py-3 px-4 text-slate-500">
                  <div>{inv.email || '—'}</div>
                  <div className="text-[10px] text-slate-400">{inv.phone || '—'}</div>
                </td>
                <td className="py-3 px-4">
                  <div className="flex items-center space-x-2">
                    <span className="font-bold text-slate-900">{inv.assignedDutiesCount} / {inv.maxDuties}</span>
                    <div className="w-16 bg-slate-100 h-1.5 rounded-full overflow-hidden">
                      <div
                        className="bg-indigo-600 h-full rounded-full"
                        style={{ width: `${Math.min(100, (inv.assignedDutiesCount / inv.maxDuties) * 100)}%` }}
                      />
                    </div>
                  </div>
                </td>
                <td className="py-3 px-4">
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border uppercase ${
                    inv.available ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : 'bg-slate-200 text-slate-600 border-slate-300'
                  }`}>
                    {inv.available ? 'Available' : 'On Leave'}
                  </span>
                </td>
                <td className="py-3 px-4 text-right">
                  <div className="flex items-center justify-end space-x-1">
                    <button
                      onClick={() => openEditModal(inv)}
                      className="p-1 text-slate-400 hover:text-indigo-600 rounded"
                      title="Edit"
                    >
                      <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button
                      onClick={() => handleDelete(inv.id, inv.name)}
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
          </tbody>
        </table>
      </div>

      {/* Add / Edit Invigilator Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full border border-slate-200 overflow-hidden">
            <div className="bg-slate-900 p-5 text-white flex justify-between items-center">
              <h3 className="font-bold text-base">
                {editingInv ? `Edit Invigilator ${editingInv.name}` : 'Add Faculty Invigilator'}
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
                  <label className="block text-xs font-bold text-slate-700 mb-1">Faculty ID *</label>
                  <input
                    type="text"
                    value={formData.facultyId}
                    onChange={(e) => setFormData({ ...formData, facultyId: e.target.value.toUpperCase() })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 font-mono"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Full Name *</label>
                  <input
                    type="text"
                    placeholder="e.g. Dr. Manoj K."
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Department *</label>
                <select
                  value={formData.department}
                  onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  {departments.map(d => (
                    <option key={d} value={d}>{d}</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Email</label>
                  <input
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Phone</label>
                  <input
                    type="text"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Max Duties Limit</label>
                <input
                  type="number"
                  min="1"
                  max="20"
                  value={formData.maxDuties}
                  onChange={(e) => setFormData({ ...formData, maxDuties: e.target.value })}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div className="flex items-center space-x-2 pt-1">
                <input
                  type="checkbox"
                  id="available"
                  checked={formData.available}
                  onChange={(e) => setFormData({ ...formData, available: e.target.checked })}
                  className="rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 w-4 h-4"
                />
                <label htmlFor="available" className="text-xs font-medium text-slate-700">
                  Available for Examination Duty
                </label>
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
                  {editingInv ? 'Update Faculty' : 'Save Faculty'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

import React, { useState } from 'react';

export default function ClassroomManager({ classrooms, onRefresh, api }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingRoom, setEditingRoom] = useState(null);
  const [formData, setFormData] = useState({
    roomNumber: '',
    rowCount: 5,
    colCount: 3,
    benchCapacity: 2,
    cctvAvailable: true
  });
  const [filterText, setFilterText] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const openCreateModal = () => {
    setEditingRoom(null);
    setFormData({
      roomNumber: '',
      rowCount: 5,
      colCount: 3,
      benchCapacity: 2,
      cctvAvailable: true
    });
    setErrorMsg('');
    setIsModalOpen(true);
  };

  const openEditModal = (room) => {
    setEditingRoom(room);
    setFormData({
      roomNumber: room.roomNumber,
      rowCount: room.rowCount,
      colCount: room.colCount,
      benchCapacity: room.benchCapacity,
      cctvAvailable: room.cctvAvailable
    });
    setErrorMsg('');
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.roomNumber.trim()) {
      setErrorMsg('Room number is required (e.g. C-207)');
      return;
    }

    try {
      setLoading(true);
      if (editingRoom) {
        await api.updateClassroom(editingRoom.id, {
          ...editingRoom,
          ...formData,
          rowCount: parseInt(formData.rowCount, 10),
          colCount: parseInt(formData.colCount, 10),
          benchCapacity: parseInt(formData.benchCapacity, 10)
        });
      } else {
        await api.createClassroom({
          ...formData,
          rowCount: parseInt(formData.rowCount, 10),
          colCount: parseInt(formData.colCount, 10),
          benchCapacity: parseInt(formData.benchCapacity, 10)
        });
      }
      setIsModalOpen(false);
      onRefresh();
    } catch (err) {
      setErrorMsg(err.message || 'Error saving classroom');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleActive = async (id) => {
    try {
      await api.toggleClassroom(id);
      onRefresh();
    } catch (err) {
      alert('Failed to toggle classroom status');
    }
  };

  const handleDelete = async (id, roomNum) => {
    if (window.confirm(`Are you sure you want to delete classroom ${roomNum}?`)) {
      try {
        await api.deleteClassroom(id);
        onRefresh();
      } catch (err) {
        alert('Failed to delete classroom');
      }
    }
  };

  const filtered = classrooms.filter(r => 
    r.roomNumber.toLowerCase().includes(filterText.toLowerCase())
  );

  const totalSeats = classrooms.reduce((sum, r) => sum + (r.active ? r.totalCapacity : 0), 0);
  const activeRooms = classrooms.filter(r => r.active).length;
  const cctvCount = classrooms.filter(r => r.cctvAvailable && r.active).length;

  return (
    <div className="space-y-6">
      {/* Top Banner / Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block mb-1">Total Classrooms</span>
          <span className="text-2xl font-black text-slate-900">{classrooms.length}</span>
          <span className="text-xs text-slate-400 block mt-1">Configured in Master</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <span className="text-xs font-semibold text-emerald-600 uppercase tracking-wider block mb-1">Active Venues</span>
          <span className="text-2xl font-black text-emerald-700">{activeRooms}</span>
          <span className="text-xs text-slate-400 block mt-1">Available for allocation</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <span className="text-xs font-semibold text-indigo-600 uppercase tracking-wider block mb-1">Effective Seat Capacity</span>
          <span className="text-2xl font-black text-indigo-700">{totalSeats}</span>
          <span className="text-xs text-slate-400 block mt-1">Rows × Cols × Bench Cap</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider block mb-1">CCTV Coverage</span>
          <span className="text-2xl font-black text-slate-900">
            {activeRooms > 0 ? Math.round((cctvCount / activeRooms) * 100) : 0}%
          </span>
          <span className="text-xs text-slate-400 block mt-1">{cctvCount} of {activeRooms} halls surveyed</span>
        </div>
      </div>

      {/* Action Toolbar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="relative w-full sm:w-72">
          <input
            type="text"
            placeholder="Search room (e.g. C-207)..."
            value={filterText}
            onChange={(e) => setFilterText(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-sm rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600"
          />
          <svg className="w-4 h-4 text-slate-400 absolute left-3 top-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <button
          onClick={openCreateModal}
          className="w-full sm:w-auto px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold rounded-xl shadow-sm transition-all flex items-center justify-center space-x-2"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          <span>Add Classroom</span>
        </button>
      </div>

      {/* Classrooms Grid Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {filtered.map((room) => (
          <div
            key={room.id}
            className={`p-5 rounded-2xl border transition-all ${
              room.active
                ? 'bg-white border-slate-200/90 hover:shadow-md hover:border-indigo-300'
                : 'bg-slate-50 border-slate-200 opacity-60'
            }`}
          >
            <div className="flex items-start justify-between">
              <div>
                <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">Hall</span>
                <h3 className="text-xl font-black text-slate-900 tracking-tight">{room.roomNumber}</h3>
              </div>
              <span
                className={`text-[10px] font-bold px-2 py-0.5 rounded-full border uppercase ${
                  room.active
                    ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                    : 'bg-slate-200 text-slate-600 border-slate-300'
                }`}
              >
                {room.active ? 'Active' : 'Disabled'}
              </span>
            </div>

            <div className="my-4 space-y-1.5 text-xs text-slate-600 border-t border-b border-slate-100 py-3">
              <div className="flex justify-between">
                <span>Layout (R × C):</span>
                <span className="font-semibold text-slate-900">{room.rowCount} rows × {room.colCount} cols</span>
              </div>
              <div className="flex justify-between">
                <span>Bench Capacity:</span>
                <span className="font-semibold text-slate-900">{room.benchCapacity} student{room.benchCapacity > 1 ? 's' : ''}/bench</span>
              </div>
              <div className="flex justify-between">
                <span>Max Capacity:</span>
                <span className="font-bold text-indigo-700">{room.totalCapacity} seats</span>
              </div>
              <div className="flex justify-between items-center pt-0.5">
                <span>CCTV Monitored:</span>
                <span className={`inline-flex items-center text-[11px] font-semibold ${room.cctvAvailable ? 'text-emerald-600' : 'text-slate-400'}`}>
                  {room.cctvAvailable ? '✓ Yes' : '✕ No'}
                </span>
              </div>
            </div>

            <div className="flex items-center justify-between pt-1">
              <button
                onClick={() => handleToggleActive(room.id)}
                className={`text-xs font-semibold px-2.5 py-1 rounded-lg transition-colors ${
                  room.active
                    ? 'text-amber-700 hover:bg-amber-50'
                    : 'text-emerald-700 hover:bg-emerald-50'
                }`}
              >
                {room.active ? 'Disable' : 'Enable'}
              </button>
              <div className="flex space-x-1">
                <button
                  onClick={() => openEditModal(room)}
                  className="p-1.5 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors"
                  title="Edit"
                >
                  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
                <button
                  onClick={() => handleDelete(room.id, room.roomNumber)}
                  className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                  title="Delete"
                >
                  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Add / Edit Classroom Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full border border-slate-200 overflow-hidden animate-in fade-in zoom-in-95">
            <div className="bg-slate-900 p-5 text-white flex justify-between items-center">
              <h3 className="font-bold text-base">
                {editingRoom ? `Edit Classroom ${editingRoom.roomNumber}` : 'Add New Classroom'}
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

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Room Number *</label>
                <input
                  type="text"
                  placeholder="e.g. C-207 or C-314"
                  value={formData.roomNumber}
                  onChange={(e) => setFormData({ ...formData, roomNumber: e.target.value })}
                  className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Rows (Depth)</label>
                  <input
                    type="number"
                    min="1"
                    max="20"
                    value={formData.rowCount}
                    onChange={(e) => setFormData({ ...formData, rowCount: e.target.value })}
                    className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    required
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Columns (Benches)</label>
                  <input
                    type="number"
                    min="1"
                    max="10"
                    value={formData.colCount}
                    onChange={(e) => setFormData({ ...formData, colCount: e.target.value })}
                    className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Bench Capacity</label>
                <select
                  value={formData.benchCapacity}
                  onChange={(e) => setFormData({ ...formData, benchCapacity: parseInt(e.target.value, 10) })}
                  className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value={1}>1 Student per Bench (Single)</option>
                  <option value={2}>2 Students per Bench (Standard University Desk)</option>
                </select>
                <p className="text-[11px] text-slate-400 mt-1">
                  Computed Capacity: <span className="font-bold text-indigo-600">{formData.rowCount * formData.colCount * formData.benchCapacity} seats</span>
                </p>
              </div>

              <div className="flex items-center space-x-2 pt-2">
                <input
                  type="checkbox"
                  id="cctvAvailable"
                  checked={formData.cctvAvailable}
                  onChange={(e) => setFormData({ ...formData, cctvAvailable: e.target.checked })}
                  className="rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 w-4 h-4"
                />
                <label htmlFor="cctvAvailable" className="text-xs font-medium text-slate-700">
                  CCTV Surveillance Installed & Verified
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
                  disabled={loading}
                  className="px-5 py-2 text-xs font-semibold bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl shadow-sm transition-colors"
                >
                  {loading ? 'Saving...' : editingRoom ? 'Update Room' : 'Save Room'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

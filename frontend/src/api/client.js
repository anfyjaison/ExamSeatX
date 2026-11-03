const API_BASE_URL = 'http://localhost:8080/api';

export const api = {
  // Classrooms
  getClassrooms: async (activeOnly = false) => {
    const res = await fetch(`${API_BASE_URL}/classrooms?activeOnly=${activeOnly}`);
    if (!res.ok) throw new Error('Failed to fetch classrooms');
    return res.json();
  },

  createClassroom: async (classroom) => {
    const res = await fetch(`${API_BASE_URL}/classrooms`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(classroom),
    });
    if (!res.ok) throw new Error('Failed to create classroom');
    return res.json();
  },

  updateClassroom: async (id, classroom) => {
    const res = await fetch(`${API_BASE_URL}/classrooms/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(classroom),
    });
    if (!res.ok) throw new Error('Failed to update classroom');
    return res.json();
  },

  toggleClassroom: async (id) => {
    const res = await fetch(`${API_BASE_URL}/classrooms/${id}/toggle`, {
      method: 'PATCH',
    });
    if (!res.ok) throw new Error('Failed to toggle classroom');
    return res.json();
  },

  deleteClassroom: async (id) => {
    const res = await fetch(`${API_BASE_URL}/classrooms/${id}`, {
      method: 'DELETE',
    });
    if (!res.ok) throw new Error('Failed to delete classroom');
    return true;
  },

  // Students
  getStudents: async (params = {}) => {
    const query = new URLSearchParams(params).toString();
    const res = await fetch(`${API_BASE_URL}/students?${query}`);
    if (!res.ok) throw new Error('Failed to fetch students');
    return res.json();
  },

  createStudent: async (student) => {
    const res = await fetch(`${API_BASE_URL}/students`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(student),
    });
    if (!res.ok) throw new Error('Failed to create student');
    return res.json();
  },

  updateStudent: async (id, student) => {
    const res = await fetch(`${API_BASE_URL}/students/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(student),
    });
    if (!res.ok) throw new Error('Failed to update student');
    return res.json();
  },

  deleteStudent: async (id) => {
    const res = await fetch(`${API_BASE_URL}/students/${id}`, {
      method: 'DELETE',
    });
    if (!res.ok) throw new Error('Failed to delete student');
    return true;
  },

  clearAllStudents: async () => {
    const res = await fetch(`${API_BASE_URL}/students`, {
      method: 'DELETE',
    });
    if (!res.ok) throw new Error('Failed to clear students');
    return res.json();
  },

  uploadRoster: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await fetch(`${API_BASE_URL}/students/upload`, {
      method: 'POST',
      body: formData,
    });
    if (!res.ok) throw new Error('Failed to upload roster');
    return res.json();
  },

  // Invigilators
  getInvigilators: async () => {
    const res = await fetch(`${API_BASE_URL}/invigilators`);
    if (!res.ok) throw new Error('Failed to fetch invigilators');
    return res.json();
  },

  createInvigilator: async (inv) => {
    const res = await fetch(`${API_BASE_URL}/invigilators`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(inv),
    });
    if (!res.ok) throw new Error('Failed to create invigilator');
    return res.json();
  },

  updateInvigilator: async (id, inv) => {
    const res = await fetch(`${API_BASE_URL}/invigilators/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(inv),
    });
    if (!res.ok) throw new Error('Failed to update invigilator');
    return res.json();
  },

  deleteInvigilator: async (id) => {
    const res = await fetch(`${API_BASE_URL}/invigilators/${id}`, {
      method: 'DELETE',
    });
    if (!res.ok) throw new Error('Failed to delete invigilator');
    return true;
  },

  autoAssignDuties: async (examSessionId) => {
    const url = examSessionId ? `${API_BASE_URL}/invigilators/auto-assign?examSessionId=${examSessionId}` : `${API_BASE_URL}/invigilators/auto-assign`;
    const res = await fetch(url, { method: 'POST' });
    if (!res.ok) throw new Error('Failed to auto-assign invigilators');
    return res.json();
  },

  getDuties: async (examSessionId) => {
    const url = examSessionId ? `${API_BASE_URL}/invigilators/duties?examSessionId=${examSessionId}` : `${API_BASE_URL}/invigilators/duties`;
    const res = await fetch(url);
    if (!res.ok) throw new Error('Failed to fetch duties');
    return res.json();
  },

  getDutyExcelUrl: (sessionId) => `${API_BASE_URL}/invigilators/export/excel${sessionId ? `?examSessionId=${sessionId}` : ''}`,
  getDutyPdfUrl: (sessionId) => `${API_BASE_URL}/invigilators/export/pdf${sessionId ? `?examSessionId=${sessionId}` : ''}`,

  // Seating Engine
  runAllocation: async (payload = {}) => {
    const res = await fetch(`${API_BASE_URL}/allocations/run`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error('Failed to run allocation');
    return res.json();
  },

  getSeatingMatrix: async (sessionId) => {
    const url = sessionId ? `${API_BASE_URL}/allocations/matrix?examSessionId=${sessionId}` : `${API_BASE_URL}/allocations/matrix`;
    const res = await fetch(url);
    if (!res.ok) throw new Error('Failed to fetch seating matrix');
    return res.json();
  },

  getExamSessions: async () => {
    const res = await fetch(`${API_BASE_URL}/allocations/sessions`);
    if (!res.ok) throw new Error('Failed to fetch sessions');
    return res.json();
  },

  // Reports
  getNoticeBoardPdfUrl: (sessionId) => `${API_BASE_URL}/reports/notice-board/pdf${sessionId ? `?examSessionId=${sessionId}` : ''}`,
  getAttendancePdfUrl: (sessionId, roomId) => {
    const params = new URLSearchParams();
    if (sessionId) params.append('examSessionId', sessionId);
    if (roomId) params.append('classroomId', roomId);
    return `${API_BASE_URL}/reports/attendance/pdf?${params.toString()}`;
  }
};

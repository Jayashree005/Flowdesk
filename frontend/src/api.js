const API_BASE = '/api';

export async function fetchDashboard() {
  const res = await fetch(`${API_BASE}/dashboard`);
  if (!res.ok) throw new Error('Failed to load dashboard metrics');
  return res.json();
}

export async function fetchTickets(params = {}) {
  const query = new URLSearchParams();
  if (params.category && params.category !== 'ALL') query.append('category', params.category);
  if (params.severity && params.severity !== 'ALL') query.append('severity', params.severity);
  if (params.status && params.status !== 'ALL') query.append('status', params.status);
  if (params.search) query.append('search', params.search);

  const res = await fetch(`${API_BASE}/tickets?${query.toString()}`);
  if (!res.ok) throw new Error('Failed to fetch tickets');
  return res.json();
}

export async function fetchTicketById(id) {
  const res = await fetch(`${API_BASE}/tickets/${id}`);
  if (!res.ok) throw new Error('Failed to fetch ticket');
  return res.json();
}

export async function createTicket(ticketData) {
  const res = await fetch(`${API_BASE}/tickets`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(ticketData),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Failed to create ticket' }));
    throw new Error(err.message || 'Failed to create ticket');
  }
  return res.json();
}

export async function updateTicketStatus(id, { status, performedBy, comment }) {
  const res = await fetch(`${API_BASE}/tickets/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status, performedBy, comment }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Status transition failed' }));
    throw new Error(err.message || 'Invalid workflow transition');
  }
  return res.json();
}

export async function assignTicket(id, { agentId, departmentId, performedBy }) {
  const res = await fetch(`${API_BASE}/tickets/${id}/assign`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ agentId, departmentId, performedBy }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Assignment failed' }));
    throw new Error(err.message || 'Assignment failed');
  }
  return res.json();
}

export async function addComment(ticketId, { authorId, content }) {
  const res = await fetch(`${API_BASE}/tickets/${ticketId}/comments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ authorId, content }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Failed to add comment' }));
    throw new Error(err.message || 'Failed to add comment');
  }
  return res.json();
}

export async function fetchUsers() {
  const res = await fetch(`${API_BASE}/users`);
  if (!res.ok) throw new Error('Failed to fetch users');
  return res.json();
}

export async function fetchDepartments() {
  const res = await fetch(`${API_BASE}/departments`);
  if (!res.ok) throw new Error('Failed to fetch departments');
  return res.json();
}

export async function fetchWorkflowRules() {
  const res = await fetch(`${API_BASE}/workflows/rules`);
  if (!res.ok) throw new Error('Failed to fetch workflow rules');
  return res.json();
}

export async function fetchWorkflowMatrix() {
  const res = await fetch(`${API_BASE}/workflows/matrix`);
  if (!res.ok) throw new Error('Failed to fetch workflow transition matrix');
  return res.json();
}

export async function triggerEscalationCheck() {
  const res = await fetch(`${API_BASE}/tickets/escalate-check`, {
    method: 'POST',
  });
  if (!res.ok) throw new Error('Escalation check failed');
  return res.json();
}

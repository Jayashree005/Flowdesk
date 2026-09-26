import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json",
    },
});

export const getTickets = () =>
    api.get("/tickets");

export const getTicket = (id) =>
    api.get(`/tickets/${id}`);

export const createTicket = (data) =>
    api.post("/tickets", data);

export const updateTicketStatus = (id, data, changedBy) => {
    const payload = (typeof data === "object" && data !== null)
        ? data
        : { status: data, changedBy: changedBy || 2, userId: changedBy || 2 };
    return api.patch(`/tickets/${id}/status`, payload);
};

export const changeTicketStatus = (id, status, changedBy) =>
    updateTicketStatus(id, status, changedBy);

export const assignTicket = (id, data, changedBy) => {
    const payload = (typeof data === "object" && data !== null)
        ? data
        : { agentId: data, changedBy: changedBy || 1, userId: changedBy || 1 };
    return api.patch(`/tickets/${id}/assign`, payload);
};

export const getSlaStatus = (id) =>
    api.get(`/tickets/${id}/sla-status`);

export const checkSla = (id) =>
    api.post(`/tickets/${id}/check-sla`);

export const getTicketHistory = (id) =>
    api.get(`/tickets/${id}/history`);

export const getTicketComments = (id) =>
    api.get(`/tickets/${id}/comments`);

export const addComment = (id, data, userId) => {
    const payload = (typeof data === "object" && data !== null)
        ? data
        : { content: data, userId: userId || 2 };
    return api.post(`/tickets/${id}/comments`, payload);
};

export const getUsers = (role) =>
    api.get(`/users${role ? `?role=${role}` : ""}`);

export const getDepartments = () =>
    api.get("/departments");

export default api;

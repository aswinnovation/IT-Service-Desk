const API = "/api/tickets";
let tickets = [];

const tableBody = document.getElementById("ticketTable");
const modal = document.getElementById("ticketModal");
const form = document.getElementById("ticketForm");
const message = document.getElementById("message");

function showMessage(text, isError = false) {
    message.textContent = text;
    message.style.color = isError ? "#bd4737" : "#287548";
}

function pretty(value) {
    return String(value || "").replaceAll("_", " ").toLowerCase()
        .replace(/\b\w/g, character => character.toUpperCase());
}

function safe(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    })[character]);
}

async function loadTickets() {
    try {
        const response = await fetch(API);
        if (!response.ok) throw new Error("Could not load tickets");
        tickets = await response.json();
        updateDashboard();
        renderTickets();
    } catch (error) {
        tableBody.innerHTML = '<tr><td colspan="7" class="empty">Unable to load tickets. Check the backend and database connection.</td></tr>';
        showMessage(error.message, true);
    }
}

function updateDashboard() {
    document.getElementById("totalCount").textContent = tickets.length;
    document.getElementById("openCount").textContent = tickets.filter(t => t.status === "OPEN").length;
    document.getElementById("progressCount").textContent = tickets.filter(t => t.status === "IN_PROGRESS").length;
    document.getElementById("resolvedCount").textContent = tickets.filter(t => t.status === "RESOLVED").length;
}

function renderTickets() {
    const query = document.getElementById("searchInput").value.toLowerCase().trim();
    const status = document.getElementById("statusFilter").value;
    const priority = document.getElementById("priorityFilter").value;

    const filtered = tickets.filter(ticket => {
        const searchable = [ticket.id, ticket.title, ticket.requesterName, ticket.requesterEmail]
            .join(" ").toLowerCase();
        return searchable.includes(query)
            && (!status || ticket.status === status)
            && (!priority || ticket.priority === priority);
    });

    if (!filtered.length) {
        tableBody.innerHTML = '<tr><td colspan="7" class="empty">No tickets found. Create a ticket to get started.</td></tr>';
        return;
    }

    tableBody.innerHTML = filtered.map(ticket => `
        <tr>
            <td><span class="ticket-title">#SD-${ticket.id}</span><span class="subtext">${safe(ticket.title)}</span></td>
            <td>${safe(ticket.requesterName)}<span class="subtext">${safe(ticket.requesterEmail)}</span></td>
            <td>${safe(pretty(ticket.category))}</td>
            <td><span class="pill priority-${safe(ticket.priority)}">${safe(pretty(ticket.priority))}</span></td>
            <td><span class="pill status-${safe(ticket.status)}">${safe(pretty(ticket.status))}</span></td>
            <td>${ticket.createdAt ? new Date(ticket.createdAt).toLocaleDateString() : "-"}</td>
            <td><select class="action-select" aria-label="Update ticket status" data-id="${ticket.id}">
                ${["OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"].map(s =>
                    `<option value="${s}" ${ticket.status === s ? "selected" : ""}>${pretty(s)}</option>`
                ).join("")}
            </select></td>
        </tr>`).join("");
}

async function updateStatus(id, status) {
    try {
        const response = await fetch(`${API}/${id}/status`, {
            method: "PATCH",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({status})
        });
        if (!response.ok) throw new Error("Status update failed");
        showMessage(`Ticket #SD-${id} updated.`);
        await loadTickets();
    } catch (error) {
        showMessage(error.message, true);
    }
}

document.getElementById("openFormButton").addEventListener("click", () => modal.classList.remove("hidden"));
document.getElementById("closeFormButton").addEventListener("click", () => modal.classList.add("hidden"));
document.getElementById("cancelButton").addEventListener("click", () => modal.classList.add("hidden"));

form.addEventListener("submit", async event => {
    event.preventDefault();
    const ticket = Object.fromEntries(new FormData(form).entries());

    try {
        const response = await fetch(API, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(ticket)
        });
        if (!response.ok) {
            const errorBody = await response.json().catch(() => ({}));
            throw new Error(errorBody.message || "Could not create ticket");
        }
        form.reset();
        modal.classList.add("hidden");
        showMessage("Ticket created successfully.");
        await loadTickets();
    } catch (error) {
        showMessage(error.message, true);
    }
});

tableBody.addEventListener("change", event => {
    if (event.target.matches(".action-select")) {
        updateStatus(event.target.dataset.id, event.target.value);
    }
});

["searchInput", "statusFilter", "priorityFilter"].forEach(id => {
    document.getElementById(id).addEventListener("input", renderTickets);
    document.getElementById(id).addEventListener("change", renderTickets);
});

document.getElementById("exportButton").addEventListener("click", () => {
    window.location.href = `${API}/export`;
});

loadTickets();

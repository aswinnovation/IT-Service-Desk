package com.example.IT_Service_Desk.service;

import com.example.IT_Service_Desk.entity.Ticket;
import com.example.IT_Service_Desk.repository.TicketRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    private static final Set<String> PRIORITIES =
            Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");

    private static final Set<String> STATUSES =
            Set.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED");

    private static final Set<String> CATEGORIES =
            Set.of("HARDWARE", "SOFTWARE", "NETWORK", "ACCESS", "OTHER");

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found: " + id));
    }

    public Ticket createTicket(Ticket ticket) {
        validateTicket(ticket);

        ticket.setPriority(normalize(ticket.getPriority(), "MEDIUM"));
        ticket.setStatus("OPEN");

        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Long id, Ticket updatedTicket) {
        Ticket existing = getTicketById(id);
        validateTicket(updatedTicket);

        existing.setTitle(updatedTicket.getTitle().trim());
        existing.setDescription(updatedTicket.getDescription().trim());
        existing.setRequesterName(updatedTicket.getRequesterName().trim());
        existing.setRequesterEmail(updatedTicket.getRequesterEmail().trim());
        existing.setCategory(normalize(updatedTicket.getCategory(), "OTHER"));
        existing.setPriority(normalize(updatedTicket.getPriority(), "MEDIUM"));

        return ticketRepository.save(existing);
    }

    public Ticket updateStatus(Long id, String status) {
        String normalizedStatus = normalize(status, "");
        if (!STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid status. Use OPEN, IN_PROGRESS, RESOLVED or CLOSED.");
        }

        Ticket ticket = getTicketById(id);
        ticket.setStatus(normalizedStatus);
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        Ticket ticket = getTicketById(id);
        ticketRepository.delete(ticket);
    }

    private void validateTicket(Ticket ticket) {
        if (ticket.getTitle() == null || ticket.getTitle().isBlank()
                || ticket.getDescription() == null || ticket.getDescription().isBlank()
                || ticket.getRequesterName() == null || ticket.getRequesterName().isBlank()
                || ticket.getRequesterEmail() == null || ticket.getRequesterEmail().isBlank()
                || ticket.getCategory() == null || ticket.getCategory().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Please provide all required ticket details.");
        }

        String category = normalize(ticket.getCategory(), "OTHER");
        if (!CATEGORIES.contains(category)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ticket category.");
        }

        String priority = normalize(ticket.getPriority(), "MEDIUM");
        if (!PRIORITIES.contains(priority)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ticket priority.");
        }
    }

    private String normalize(String value, String defaultValue) {
        return value == null || value.isBlank()
                ? defaultValue
                : value.trim().toUpperCase().replace(' ', '_');
    }
}

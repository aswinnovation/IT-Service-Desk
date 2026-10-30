package com.example.IT_Service_Desk.controller;

import com.example.IT_Service_Desk.entity.Ticket;
import com.example.IT_Service_Desk.service.TicketService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        return ResponseEntity.status(201).body(ticketService.createTicket(ticket));
    }

    @PutMapping("/{id}")
    public Ticket updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        return ticketService.updateTicket(id, ticket);
    }

    @PatchMapping("/{id}/status")
    public Ticket updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ticketService.updateStatus(id, request.get("status"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Requester,Email,Category,Priority,Status,Created At\n");

        for (Ticket ticket : tickets) {
            csv.append(ticket.getId()).append(',')
                    .append(csvValue(ticket.getTitle())).append(',')
                    .append(csvValue(ticket.getRequesterName())).append(',')
                    .append(csvValue(ticket.getRequesterEmail())).append(',')
                    .append(csvValue(ticket.getCategory())).append(',')
                    .append(csvValue(ticket.getPriority())).append(',')
                    .append(csvValue(ticket.getStatus())).append(',')
                    .append(csvValue(ticket.getCreatedAt() == null ? "" : ticket.getCreatedAt().toString()))
                    .append('\n');
        }

        byte[] content = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }

    private String csvValue(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}

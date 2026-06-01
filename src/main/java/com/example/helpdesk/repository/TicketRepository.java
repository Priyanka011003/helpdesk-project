package com.example.helpdesk.repository;

import java.util.List;

import com.example.helpdesk.entity.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

	List<Ticket> findByTitleContainingIgnoreCase(
	        String keyword);
}
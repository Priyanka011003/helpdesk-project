package com.example.helpdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.helpdesk.entity.Comment;

public interface CommentRepository
        extends JpaRepository<Comment, Long> {

    List<Comment> findByTicketId(Long ticketId);

}
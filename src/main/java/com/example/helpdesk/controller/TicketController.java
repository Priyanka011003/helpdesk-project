package com.example.helpdesk.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.repository.TicketRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import com.example.helpdesk.entity.Asset;
import com.example.helpdesk.repository.AssetRepository;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.helpdesk.entity.Comment;
import com.example.helpdesk.repository.CommentRepository;
@Controller
public class TicketController {
	@Autowired
	private CommentRepository commentRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private AssetRepository assetRepository;
    @GetMapping("/tickets")
    public String tickets(Model model) {

        var tickets = ticketRepository.findAll();

        long totalTickets = tickets.size();

        long resolvedTickets = tickets.stream()
                .filter(t -> "Resolved".equals(t.getStatus()))
                .count();

        long pendingTickets = tickets.stream()
                .filter(t -> "Pending".equals(t.getStatus()))
                .count();

        model.addAttribute("tickets", tickets);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("resolvedTickets", resolvedTickets);
        model.addAttribute("pendingTickets", pendingTickets);

        return "tickets";
    }
    @PostMapping("/save")
    public String saveTicket(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String priority,
            RedirectAttributes redirectAttributes) {

        Ticket ticket = new Ticket();

        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);

        ticketRepository.save(ticket);

        redirectAttributes.addFlashAttribute(
                "success",
                "✅ Ticket Created Successfully");
        redirectAttributes.addFlashAttribute(
                "success",
                "🗑 Ticket Deleted Successfully");
        redirectAttributes.addFlashAttribute(
                "success",
                "✔ Ticket Resolved Successfully");
        return "redirect:/tickets";
    }
   

  

    @GetMapping("/settings")
    public String settings() {

        return "settings";
    }
    @GetMapping("/delete-ticket/{id}")
    public String deleteTicket(
            @PathVariable Long id) {

        ticketRepository.deleteById(id);

        return "redirect:/tickets";
    }
    @GetMapping("/resolve/{id}")
    public String resolveTicket(
            @PathVariable Long id) {

        Ticket ticket =
                ticketRepository.findById(id).orElse(null);

        if(ticket != null){

            ticket.setStatus("Resolved");

            ticketRepository.save(ticket);
        }

        return "redirect:/tickets";
    }
   
    @GetMapping("/edit-ticket/{id}")
    public String editTicket(
            @PathVariable Long id,
            Model model) {

        Ticket ticket =
                ticketRepository.findById(id).orElse(null);

        if(ticket == null){
            return "redirect:/tickets";
        }

        model.addAttribute("ticket", ticket);

        return "edit-ticket";
    }
    @PostMapping("/update-ticket")
    public String updateTicket(

            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String priority,

            RedirectAttributes redirectAttributes) {

        Ticket ticket =
                ticketRepository.findById(id).orElse(null);

        if(ticket != null){

            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setPriority(priority);

            ticketRepository.save(ticket);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "✏ Ticket Updated Successfully");
        }

        return "redirect:/tickets";
    }
    @GetMapping("/reports")
    public String reports(Model model) {

        var tickets = ticketRepository.findAll();

        long totalTickets = tickets.size();

        long resolvedTickets = tickets.stream()
                .filter(t -> "Resolved".equals(t.getStatus()))
                .count();

        long pendingTickets = tickets.stream()
                .filter(t -> "Pending".equals(t.getStatus()))
                .count();

        long highPriorityTickets = tickets.stream()
                .filter(t -> "High".equals(t.getPriority()))
                .count();

        model.addAttribute("tickets", tickets);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("resolvedTickets", resolvedTickets);
        model.addAttribute("pendingTickets", pendingTickets);
        model.addAttribute("highPriorityTickets", highPriorityTickets);

        return "reports";
    }
    @GetMapping("/reports/excel")
    public void exportExcel(HttpServletResponse response) throws Exception {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=tickets.xlsx");

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Tickets");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Issue");
        header.createCell(2).setCellValue("Status");
        header.createCell(3).setCellValue("Priority");

        List<Ticket> tickets = ticketRepository.findAll();

        int rowCount = 1;

        for(Ticket t : tickets){

            Row row = sheet.createRow(rowCount++);

            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getTitle());
            row.createCell(2).setCellValue(t.getStatus());
            row.createCell(3).setCellValue(t.getPriority());
        }

        workbook.write(response.getOutputStream());

        workbook.close();
    }
    @GetMapping("/reports/pdf")
    public void exportPdf(
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=tickets.pdf");

        Document document = new Document();

        PdfWriter.getInstance(
                document,
                response.getOutputStream());

        document.open();

        document.add(
                new Paragraph("Helpdesk Ticket Report"));

        document.add(new Paragraph(" "));

        PdfPTable table =
                new PdfPTable(4);

        table.addCell("ID");
        table.addCell("Issue");
        table.addCell("Status");
        table.addCell("Priority");

        List<Ticket> tickets =
                ticketRepository.findAll();

        for(Ticket t : tickets){

            table.addCell(
                    String.valueOf(t.getId()));

            table.addCell(
                    t.getTitle());

            table.addCell(
                    t.getStatus());

            table.addCell(
                    t.getPriority());
        }

        document.add(table);

        document.close();
    }
    @GetMapping("/assets")
    public String assets(Model model) {

        var assets = assetRepository.findAll();

        long totalAssets = assets.size();

        long availableAssets = assets.stream()
                .filter(a -> "Available".equals(a.getStatus()))
                .count();

        long assignedAssets = assets.stream()
                .filter(a -> "Assigned".equals(a.getStatus()))
                .count();

        long maintenanceAssets = assets.stream()
                .filter(a -> "Maintenance".equals(a.getStatus()))
                .count();

        model.addAttribute("assets", assets);
        model.addAttribute("totalAssets", totalAssets);
        model.addAttribute("availableAssets", availableAssets);
        model.addAttribute("assignedAssets", assignedAssets);
        model.addAttribute("maintenanceAssets", maintenanceAssets);

        return "assets";
    }
    @PostMapping("/save-asset")
    public String saveAsset(

            @RequestParam String assetId,
            @RequestParam String assetName,
            @RequestParam String category,
            @RequestParam String status,

            @RequestParam String assignedTo,
            @RequestParam String department,
            @RequestParam String employeeId,

            RedirectAttributes redirectAttributes) {

        if(assetRepository.existsByAssetId(assetId)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "⚠ Asset ID Already Exists");

            return "redirect:/assets";
        }

        Asset asset = new Asset();

        asset.setAssetId(assetId);

        asset.setAssetName(assetName);

        asset.setCategory(category);

        asset.setStatus(status);

        asset.setAssignedTo(assignedTo);

        asset.setDepartment(department);

        asset.setEmployeeId(employeeId);

        assetRepository.save(asset);

        redirectAttributes.addFlashAttribute(
                "success",
                "💻 Asset Added Successfully");

        return "redirect:/assets";
    }
    @GetMapping("/delete-asset/{id}")
    public String deleteAsset(
            @PathVariable Long id,

            RedirectAttributes redirectAttributes) {

        assetRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "🗑 Asset Deleted Successfully");

        return "redirect:/assets";
    }
    @GetMapping("/edit-asset/{id}")
    public String editAsset(
            @PathVariable Long id,
            Model model) {

        Asset asset =
                assetRepository.findById(id).orElse(null);

        model.addAttribute("asset", asset);

        return "edit-asset";
    }
    @PostMapping("/update-asset")
    public String updateAsset(

            @RequestParam Long id,
            @RequestParam String assetName,
            @RequestParam String category,
            @RequestParam String status,

            RedirectAttributes redirectAttributes) {

        Asset asset =
                assetRepository.findById(id).orElse(null);

        if(asset != null){

            asset.setAssetName(assetName);
            asset.setCategory(category);
            asset.setStatus(status);

            assetRepository.save(asset);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "✏ Asset Updated Successfully");
        }

        return "redirect:/assets";
    }
    @GetMapping("/tickets/search")
    public String searchTicket(
            @RequestParam String keyword,
            Model model) {

        var tickets =
                ticketRepository
                .findByTitleContainingIgnoreCase(keyword);

        model.addAttribute("tickets", tickets);

        if(tickets.isEmpty()){

            model.addAttribute(
                    "message",
                    "❌ No Ticket Found");
        }

        return "tickets";
    }
    @GetMapping("/assets/search")
    public String searchAsset(
            @RequestParam String keyword,
            Model model) {

        var assets =
                assetRepository
                .findByAssetNameContainingIgnoreCase(
                        keyword);

        model.addAttribute(
                "assets",
                assets);

        if(assets.isEmpty()){

            model.addAttribute(
                    "message",
                    "❌ No Asset Found");
        }

        return "assets";
    }
    @GetMapping("/ticket/{id}")
    public String ticketDetails(
            @PathVariable Long id,
            Model model) {

        Ticket ticket =
                ticketRepository
                .findById(id)
                .orElse(null);

        model.addAttribute(
                "ticket",
                ticket);

        model.addAttribute(
                "comments",
                commentRepository
                .findByTicketId(id));

        return "ticket-details";
    }
    @PostMapping("/add-comment")
    public String addComment(

            @RequestParam Long ticketId,

            @RequestParam String message) {

        Ticket ticket =
                ticketRepository
                .findById(ticketId)
                .orElse(null);

        Comment comment =
                new Comment();

        comment.setTicket(ticket);

        comment.setMessage(message);

        commentRepository.save(comment);

        return "redirect:/ticket/" + ticketId;
    }
   
}
package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Patient;
import com.gestionhopital.gestionhopital.services.HospitalService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class PatientController {
    private HospitalService hospitalService;

    @GetMapping("/patients")
    public String listPatients(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int p,
                               @RequestParam(name = "size", defaultValue = "5") int s,
                               @RequestParam(name = "keyword", defaultValue = "") String kw) {
        Page<Patient> pagePatients = hospitalService.chercherPatients(kw, PageRequest.of(p, s));
        model.addAttribute("listPatients", pagePatients.getContent());
        model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("keyword", kw);
        return "patients/list";
    }

    @GetMapping("/admin/formPatients")
    public String formPatients(Model model) {
        model.addAttribute("patient", new Patient());
        return "patients/form";
    }

    @PostMapping("/admin/save")
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword) {
        if (bindingResult.hasErrors()) return "formPatients";
        hospitalService.savePatient(patient);
        return "redirect:/patients?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/admin/delete")
    public String delete(Long id, String keyword, int page) {
        hospitalService.deletePatient(id);
        return "redirect:/patients?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/admin/editPatient")
    public String edit(Model model, Long id, String keyword, int page) {
        Patient patient = hospitalService.getPatient(id);
        model.addAttribute("patient", patient);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        return "patients/edit";
    }
}


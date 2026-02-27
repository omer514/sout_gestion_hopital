package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Patient;
import com.gestionhopital.gestionhopital.entities.AppUser; 
import com.gestionhopital.gestionhopital.entities.Consultation;
import com.gestionhopital.gestionhopital.services.HospitalService;
import com.gestionhopital.gestionhopital.repositories.PatientRepository;
import com.gestionhopital.gestionhopital.services.AccountService;
import java.security.Principal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.context.Context;
import java.io.StringReader;
import com.itextpdf.html2pdf.HtmlConverter;


@Controller
@AllArgsConstructor
public class PatientController {
    private HospitalService hospitalService;
    private PatientRepository patientRepository;
    private AccountService accountService;
    private final org.thymeleaf.TemplateEngine templateEngine; 

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
                   @RequestParam(defaultValue = "") String keyword,
                   RedirectAttributes redirectAttributes) { 
    
    // 1. En cas d'erreur de saisie, on retourne au formulaire
    // Note : assure-toi que le chemin est "patients/form" ou "formPatients" selon ton dossier
    if (bindingResult.hasErrors()) return "patients/form";

    // 2. On enregistre le patient. 
    // Grâce à la modification faite dans HospitalServiceImpl, savedPatient contient maintenant le matricule et l'AppUser.
    Patient savedPatient = hospitalService.savePatient(patient);

    // 3. On prépare les données pour la Modale de succès
    redirectAttributes.addFlashAttribute("showSuccessModal", true);
    redirectAttributes.addFlashAttribute("patientNom", savedPatient.getNom() + " " + savedPatient.getPrenom());
    redirectAttributes.addFlashAttribute("patientMatricule", savedPatient.getMatricule());
    
    // On génère le mot de passe temporaire pour l'affichage (doit correspondre à la logique du Service)
    String tempPass = "Pass" + savedPatient.getMatricule().substring(savedPatient.getMatricule().lastIndexOf("-") + 1);
    redirectAttributes.addFlashAttribute("tempPassword", tempPass);

    // 4. Redirection vers la liste avec les paramètres de pagination
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


    @GetMapping("/patient/profil")
    public String afficherProfil(Model model, Principal principal) {
        Patient p = patientRepository.findByAppUser_Username(principal.getName());
        model.addAttribute("patient", p);
        return "patients/profil";
    }

    @PostMapping("/patient/updatePassword")
    public String updatePassword(@RequestParam String oldPassword, 
                                 @RequestParam String newPassword, 
                                 @RequestParam String confirmPassword,
                                 Principal principal, 
                                 RedirectAttributes ra) {
        
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Les nouveaux mots de passe ne correspondent pas.");
            return "redirect:/patient/profil"; // Correction du chemin (patient sans s)
        }

        try {
            accountService.updateUserPassword(principal.getName(), oldPassword, newPassword);
            ra.addFlashAttribute("success", "Mot de passe mis à jour avec succès !");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "L'ancien mot de passe est incorrect.");
        }

        return "redirect:/patient/profil"; // Correction du chemin
    }

    @PostMapping("/patient/updateInfos")
    public String updateInfos(@RequestParam String email, 
                              @RequestParam String telephone, 
                              Principal principal, 
                              RedirectAttributes ra) {
        try {
            Patient p = patientRepository.findByAppUser_Username(principal.getName());
            AppUser user = p.getAppUser();
            
            user.setUsername(email);
            p.setTelephone(telephone);
            
            patientRepository.save(p);
            ra.addFlashAttribute("successInfos", "Vos informations ont été mises à jour.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorInfos", "Erreur lors de la mise à jour.");
        }
        return "redirect:/patient/profil"; // Correction du chemin
    }



@GetMapping("/patient/ordonnance/pdf/{id}")
public void generateOrdonnancePdf(@PathVariable Long id, HttpServletResponse response) throws Exception {
    Consultation consultation = hospitalService.getConsultation(id);
    
    // 1. Préparer les données pour Thymeleaf
    Context context = new Context();
    context.setVariable("consultation", consultation);
    String htmlContent = templateEngine.process("patients/ordonnance_pdf", context);
    
    // 2. Configurer la réponse
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=Ordonnance_" + id + ".pdf");
    
    // 3. Conversion MAGIQUE : Tout le HTML/CSS devient un PDF parfait
    HtmlConverter.convertToPdf(htmlContent, response.getOutputStream());
}
}


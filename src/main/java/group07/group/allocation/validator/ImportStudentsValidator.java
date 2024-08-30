package group07.group.allocation.validator;

import group07.group.allocation.model.account.DTO.ImportUsersDTO;
import group07.group.allocation.model.Module;
import group07.group.allocation.repos.ModuleRepo;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

public class ImportStudentsValidator implements Validator {

    private final ModuleRepo moduleRepo;

    public ImportStudentsValidator(ModuleRepo moduleRepo){
        this.moduleRepo = moduleRepo;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ImportUsersDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ImportUsersDTO importForm = (ImportUsersDTO) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedModuleID", "", "Please select a module.");

        try {
            MultipartFile csvFile = (MultipartFile) importForm.getCsvFile();

            if (csvFile.isEmpty()){ //check if the user didn't upload a file
                errors.rejectValue("csvFile", "", "Please upload a CSV file");
            }

            if (errors.hasErrors()) {
                return;
            }

            String fileType = csvFile.getContentType();
            //Check the file uploaded is of CSV format
            if (!(Objects.equals(fileType, "text/csv") || Objects.equals(fileType, "application/vnd.ms-excel") || Objects.equals(fileType, "text/comma-separated-values"))) { //make sure file is csv
                errors.rejectValue("csvFile", "", "You can only upload CSV files, please download our template file using the button above and try again");
            }
        } catch (Exception e){
            errors.rejectValue("csvFile", "", "Error processing CSV file, please download our template file using the button above and try again");
        }

        try {
            Integer moduleID = Integer.parseInt(importForm.getSelectedModuleID());
            Optional<Module> dbModule = moduleRepo.findById(moduleID);
            if (dbModule.isEmpty()){
                errors.rejectValue("selectedModule", "", "Your module does not exist, please create your module first using the button above");
            }
        } catch (Exception e){
            errors.rejectValue("selectedModule", "", "Error, processing selected module, please logout and try again");
        }

    }
}

package group07.group.allocation.model.account.DTO;

public class ImportUsersDTO {

    private Object csvFile;
    private String selectedModuleID;

    public Object getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(Object csvFile) {
        this.csvFile = csvFile;
    }

    public String getSelectedModuleID() {
        return selectedModuleID;
    }

    public void setSelectedModuleID(String selectedModuleID) {
        this.selectedModuleID = selectedModuleID;
    }
}

package ro.home.model;

abstract class BasicInformationDTO {

    private String type;

    public BasicInformationDTO(String type) {
        this.type = type;
    }

    public BasicInformationDTO() { }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
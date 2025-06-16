package digital_complain_box;

public class Department {
    private Integer deptId;
    private String deptName;
    private String description;

    public Department(Integer deptId, String deptName, String description) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.description = description;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public String getDescription() {
        return description;
    }
}

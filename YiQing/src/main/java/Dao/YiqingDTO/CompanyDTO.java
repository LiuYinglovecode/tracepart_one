package Dao.YiqingDTO;
import com.yunlu.core.data.sql.annotation.SqlColumn;
import com.yunlu.core.data.sql.annotation.SqlTable;

/**
 * Created by Hastings  on 2020/3/9.
 */

@SqlTable(name="kg_covid_company_test")
//@SqlTable(name="kg_covid_company")
public class CompanyDTO {
	@SqlColumn(name = "id")
	private String id;
	@SqlColumn(name = "source_id")
	private String sourceId;
	@SqlColumn(name = "category_id")
	private String categoryId;
	@SqlColumn(name = "company_id")
	private String companyId;
	@SqlColumn(name = "name")
	private String name;
	@SqlColumn(name = "scale")
	private String scale;
	@SqlColumn(name = "employees")
	private String employees;
	@SqlColumn(name = "address")
	private String address;
	@SqlColumn(name = "post_code")
	private String postCode;
	@SqlColumn(name = "info")
	private String info;
	@SqlColumn(name = "contacts")
	private String contacts;
	@SqlColumn(name = "tel")
	private String tel;
	@SqlColumn(name = "fax")
	private String fax;
	@SqlColumn(name = "country")
	private String country;
	@SqlColumn(name = "province")
	private String province;
	@SqlColumn(name = "city")
	private String city;
	@SqlColumn(name = "type")
	private String type;
	@SqlColumn(name = "model")
	private String model;
	@SqlColumn(name = "industry")
	private String industry;
	@SqlColumn(name = "product")
	private String product;
	@SqlColumn(name = "brand")
	private String brand;
	@SqlColumn(name = "status")
	private String status;
	@SqlColumn(name = "create_time")
	private String createTime;
	@SqlColumn(name = "update_time")
	private String updateTime;
	@SqlColumn(name = "deleted")
	private String deleted;


public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

public String getCompanyId() {
        return companyId;
    }
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

public String getScale() {
        return scale;
    }
    public void setScale(String scale) {
        this.scale = scale;
    }

public String getEmployees() {
        return employees;
    }
    public void setEmployees(String employees) {
        this.employees = employees;
    }

public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

public String getPostCode() {
        return postCode;
    }
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

public String getContacts() {
        return contacts;
    }
    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }

public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }

public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

public String getIndustry() {
        return industry;
    }
    public void setIndustry(String industry) {
        this.industry = industry;
    }

public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }

public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

public String getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

public String getDeleted() {
        return deleted;
    }
    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

}

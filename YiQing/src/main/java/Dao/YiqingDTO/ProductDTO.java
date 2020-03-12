package Dao.YiqingDTO;
import com.yunlu.core.data.sql.annotation.SqlColumn;
import com.yunlu.core.data.sql.annotation.SqlTable;

/**
 * Created by Hastings  on 2020/3/12.
 */

@SqlTable(name="kg_covid_product_test")
//@SqlTable(name="kg_covid_product")
public class ProductDTO {
	@SqlColumn(name = "id")
	private String id;
	@SqlColumn(name = "source_id")
	private String sourceId;
	@SqlColumn(name = "category_id")
	private String categoryId;
	@SqlColumn(name = "company_id")
	private String companyId;
	@SqlColumn(name = "contacts")
	private String contacts;
	@SqlColumn(name = "tel")
	private String tel;
	@SqlColumn(name = "name")
	private String name;
	@SqlColumn(name = "brand")
	private String brand;
	@SqlColumn(name = "specs")
	private String specs;
	@SqlColumn(name = "material")
	private String material;
	@SqlColumn(name = "classify_name")
	private String classifyName;
	@SqlColumn(name = "level")
	private String level;
	@SqlColumn(name = "charge_unit")
	private String chargeUnit;
	@SqlColumn(name = "price")
	private String price;
	@SqlColumn(name = "inventory")
	private String inventory;
	@SqlColumn(name = "moq")
	private String moq;
	@SqlColumn(name = "image")
	private String image;
	@SqlColumn(name = "description")
	private String description;
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

public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

public String getSpecs() {
        return specs;
    }
    public void setSpecs(String specs) {
        this.specs = specs;
    }

public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }

public String getClassifyName() {
        return classifyName;
    }
    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }

public String getChargeUnit() {
        return chargeUnit;
    }
    public void setChargeUnit(String chargeUnit) {
        this.chargeUnit = chargeUnit;
    }

public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

public String getInventory() {
        return inventory;
    }
    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

public String getMoq() {
        return moq;
    }
    public void setMoq(String moq) {
        this.moq = moq;
    }

public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

package Dao.YiqingDTO;
import com.yunlu.core.data.sql.annotation.SqlColumn;
import com.yunlu.core.data.sql.annotation.SqlTable;

/**
 * Created by Hastings  on 2020/3/9.
 */

@SqlTable(name="kg_covid_material_category")
public class MaterialCategoryDTO {
	@SqlColumn(name = "id")
	private String id;
	@SqlColumn(name = "product_category_id")
	private String productCategoryId;
	@SqlColumn(name = "name")
	private String name;
	@SqlColumn(name = "key")
	private String key;
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

public String getProductCategoryId() {
        return productCategoryId;
    }
    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
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

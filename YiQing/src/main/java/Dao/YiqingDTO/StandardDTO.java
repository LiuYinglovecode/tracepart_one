package Dao.YiqingDTO;
import com.yunlu.core.data.sql.annotation.SqlColumn;
import com.yunlu.core.data.sql.annotation.SqlTable;

/**
 * Created by Hastings  on 2020/3/13.
 */

@SqlTable(name="kg_covid_standard")
public class StandardDTO {
	@SqlColumn(name = "id")
	private String id;
	@SqlColumn(name = "source_id")
	private String sourceId;
	@SqlColumn(name = "category_id")
	private String categoryId;
	@SqlColumn(name = "standard_id")
	private String standardId;
	@SqlColumn(name = "name")
	private String name;
	@SqlColumn(name = "code")
	private String code;
	@SqlColumn(name = "scope")
	private String scope;
	@SqlColumn(name = "language")
	private String language;
	@SqlColumn(name = "description")
	private String description;
	@SqlColumn(name = "file_url")
	private String fileUrl;
	@SqlColumn(name = "file_size")
	private String fileSize;
	@SqlColumn(name = "file_format")
	private String fileFormat;
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

public String getStandardId() {
        return standardId;
    }
    public void setStandardId(String standardId) {
        this.standardId = standardId;
    }

public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }

public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

public String getFileUrl() {
        return fileUrl;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

public String getFileFormat() {
        return fileFormat;
    }
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
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

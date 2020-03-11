package Dao.YiqingDTO;
import com.yunlu.core.data.sql.annotation.SqlColumn;
import com.yunlu.core.data.sql.annotation.SqlTable;

/**
 * Created by Hastings  on 2020/3/9.
 */

@SqlTable(name="kg_covid_datasource")
public class DatasourceDTO {
	@SqlColumn(name = "id")
	private String id;
	@SqlColumn(name = "name")
	private String name;
	@SqlColumn(name = "url")
	private String url;
	@SqlColumn(name = "source")
	private String source;
	@SqlColumn(name = "status")
	private String status;
	@SqlColumn(name = "publish_time")
	private String publishTime;
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

public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

public String getPublishTime() {
        return publishTime;
    }
    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
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

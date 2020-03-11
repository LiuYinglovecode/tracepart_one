package Dao.YiqingDTO;
import com.yunlu.core.data.sql.annotation.SqlColumn;
import com.yunlu.core.data.sql.annotation.SqlTable;

/**
 * Created by Hastings  on 2020/3/9.
 */

@SqlTable(name="kg_covid_news")
public class NewsDTO {
	@SqlColumn(name = "id")
	private String id;
	@SqlColumn(name = "source_id")
	private String sourceId;
	@SqlColumn(name = "title")
	private String title;
	@SqlColumn(name = "subtitle")
	private String subtitle;
	@SqlColumn(name = "summary")
	private String summary;
	@SqlColumn(name = "content")
	private String content;
	@SqlColumn(name = "type")
	private String type;
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

public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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

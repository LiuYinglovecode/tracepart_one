package Dao.YiqingDTO;
import com.yunlu.core.data.sql.annotation.SqlColumn;
import com.yunlu.core.data.sql.annotation.SqlTable;

/**
 * Created by Hastings  on 2020/3/12.
 */

@SqlTable(name="kg_covid_patent")
public class PatentDTO {
	@SqlColumn(name = "id")
	private String id;
	@SqlColumn(name = "source_id")
	private String sourceId;
	@SqlColumn(name = "category_id")
	private String categoryId;
	@SqlColumn(name = "patent_id")
	private String patentId;
	@SqlColumn(name = "patent_num")
	private String patentNum;
	@SqlColumn(name = "name")
	private String name;
	@SqlColumn(name = "url")
	private String url;
	@SqlColumn(name = "summary")
	private String summary;
	@SqlColumn(name = "type")
	private String type;
	@SqlColumn(name = "case_status")
	private String caseStatus;
	@SqlColumn(name = "technical_field")
	private String technicalField;
	@SqlColumn(name = "trading_status")
	private String tradingStatus;
	@SqlColumn(name = "price")
	private String price;
	@SqlColumn(name = "application_number")
	private String applicationNumber;
	@SqlColumn(name = "application_date")
	private String applicationDate;
	@SqlColumn(name = "publication_number")
	private String publicationNumber;
	@SqlColumn(name = "publication_date")
	private String publicationDate;
	@SqlColumn(name = "applicant")
	private String applicant;
	@SqlColumn(name = "inventor")
	private String inventor;
	@SqlColumn(name = "main_classification_number")
	private String mainClassificationNumber;
	@SqlColumn(name = "classification_number")
	private String classificationNumber;
	@SqlColumn(name = "address")
	private String address;
	@SqlColumn(name = "country_code")
	private String countryCode;
	@SqlColumn(name = "agency")
	private String agency;
	@SqlColumn(name = "agent")
	private String agent;
	@SqlColumn(name = "preemption")
	private String preemption;
	@SqlColumn(name = "assertion")
	private String assertion;
	@SqlColumn(name = "instructions")
	private String instructions;
	@SqlColumn(name = "file_url")
	private String fileUrl;
	@SqlColumn(name = "file_size")
	private String fileSize;
	@SqlColumn(name = "file_format")
	private String fileFormat;
	@SqlColumn(name = "law_info")
	private String lawInfo;
	@SqlColumn(name = "law_status")
	private String lawStatus;
	@SqlColumn(name = "summary_picture_url")
	private String summaryPictureUrl;
	@SqlColumn(name = "instructions_picture_url")
	private String instructionsPictureUrl;
	@SqlColumn(name = "tfc_picture_url")
	private String tfcPictureUrl;
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

public String getPatentId() {
        return patentId;
    }
    public void setPatentId(String patentId) {
        this.patentId = patentId;
    }

public String getPatentNum() {
        return patentNum;
    }
    public void setPatentNum(String patentNum) {
        this.patentNum = patentNum;
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

public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

public String getCaseStatus() {
        return caseStatus;
    }
    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

public String getTechnicalField() {
        return technicalField;
    }
    public void setTechnicalField(String technicalField) {
        this.technicalField = technicalField;
    }

public String getTradingStatus() {
        return tradingStatus;
    }
    public void setTradingStatus(String tradingStatus) {
        this.tradingStatus = tradingStatus;
    }

public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

public String getApplicationNumber() {
        return applicationNumber;
    }
    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

public String getApplicationDate() {
        return applicationDate;
    }
    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

public String getPublicationNumber() {
        return publicationNumber;
    }
    public void setPublicationNumber(String publicationNumber) {
        this.publicationNumber = publicationNumber;
    }

public String getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

public String getApplicant() {
        return applicant;
    }
    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

public String getInventor() {
        return inventor;
    }
    public void setInventor(String inventor) {
        this.inventor = inventor;
    }

public String getMainClassificationNumber() {
        return mainClassificationNumber;
    }
    public void setMainClassificationNumber(String mainClassificationNumber) {
        this.mainClassificationNumber = mainClassificationNumber;
    }

public String getClassificationNumber() {
        return classificationNumber;
    }
    public void setClassificationNumber(String classificationNumber) {
        this.classificationNumber = classificationNumber;
    }

public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

public String getAgency() {
        return agency;
    }
    public void setAgency(String agency) {
        this.agency = agency;
    }

public String getAgent() {
        return agent;
    }
    public void setAgent(String agent) {
        this.agent = agent;
    }

public String getPreemption() {
        return preemption;
    }
    public void setPreemption(String preemption) {
        this.preemption = preemption;
    }

public String getAssertion() {
        return assertion;
    }
    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }

public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
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

public String getLawInfo() {
        return lawInfo;
    }
    public void setLawInfo(String lawInfo) {
        this.lawInfo = lawInfo;
    }

public String getLawStatus() {
        return lawStatus;
    }
    public void setLawStatus(String lawStatus) {
        this.lawStatus = lawStatus;
    }

public String getSummaryPictureUrl() {
        return summaryPictureUrl;
    }
    public void setSummaryPictureUrl(String summaryPictureUrl) {
        this.summaryPictureUrl = summaryPictureUrl;
    }

public String getInstructionsPictureUrl() {
        return instructionsPictureUrl;
    }
    public void setInstructionsPictureUrl(String instructionsPictureUrl) {
        this.instructionsPictureUrl = instructionsPictureUrl;
    }

public String getTfcPictureUrl() {
        return tfcPictureUrl;
    }
    public void setTfcPictureUrl(String tfcPictureUrl) {
        this.tfcPictureUrl = tfcPictureUrl;
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

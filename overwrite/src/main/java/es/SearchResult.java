package es;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResult {
	private long totalCount;
    private long elapsedMilliseconds;
    private List<Map<String, Object>> resultList;
    private Map<String, Object> resultMap;

    /**
     * 符合条件的结果总数
     * @return
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * 符合条件的结果总数
     * @return
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 搜索消耗时间
     * @return
     */
    public long getElapsedMilliseconds() {
        return elapsedMilliseconds;
    }

    /**
     * 搜索消耗时间
     * @return
     */
    public void setElapsedMilliseconds(long elapsedMilliseconds) {
        this.elapsedMilliseconds = elapsedMilliseconds;
    }

    /**
     * 搜索结果列表
     * @return
     */
    public List<Map<String, Object>> getResultList() {
        return resultList;
    }

    /**
     * 搜索结果结果列表
     * @param resultList
     */
    public void setResultList(List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }

    /**
     * 搜索结果map，用于存放搜索结果列表外的其他结果
     * @return
     */
    public Map<String, Object> getResultMap() {
        if(resultMap == null) {
            resultMap = new HashMap<>();
        }
        return resultMap;
    }
}

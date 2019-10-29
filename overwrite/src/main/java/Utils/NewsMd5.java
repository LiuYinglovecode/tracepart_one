package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * P:所有标点字符
 * Z：所有分隔符字符
 * S:所有符号
 * M:所有音调符号标记
 * C:所有控制字符
 */
public class NewsMd5 {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsMd5.class);

    public static String newsMd5(String text) {
        try {
            if (null != text) {
                String str = text.replaceAll("[\\pP\\pZ\\pS\\pM\\pC]", "").replaceAll(" ","");
                if (str.length() > 200) {
                    return MD5Util.getMD5String(str.substring(0, 100) + str.substring(str.length() - 100, str.length()));
                } else {
                    return MD5Util.getMD5String(str);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        String str = "据海关最新统计，今年一季度我国机电产品出口433.9亿美元，增长40.7%，比同期我国总体出口增速高出7.2个百分点，在我国出口中所占比重首次超过50%，达到50.3%。其中机械及设备出口157亿美元，增长48.9%；电器及电子产品出口168.1亿美元，增长33%。 据商务部机电司介绍，机电产品已经连续8年成为我国第一大类出口商品，目前我国有39种机电产品的出口额居全球首位。机电产品在我国外贸出口中的地位日渐重要。1997年，机电产品占全国出口总额的比重只有33%，2002年提高到48%，今年一季度首次达到50.3%,全年平均有望超过50%。 机电产品出口的快速增长，成为我国对外经济贸易和国民经济发展的重要推动力量。过去5年，机电产品出口拉动外贸出口年均增长7.7个百分点,机电产品出口增量占全国外贸出口增量的比重累计达到68%。据估算，过去5年机电产品出口累计增加国内就业岗位超过1300万个。 随着我国外贸体制改革的不断深化，机电产品出口经营多元化进一步推进。过去5年，国有企业、民营企业出口迅速成长，经营主体多元化的格局基本形成。据统计，2002年我国机电产品出口1570.8亿美元,其中国有企业出口414亿美元,5年间年均增长12%,民营企业出口42亿美元,年均增长2.7倍。外商投资企业出口1050亿美元,5年间年均增长25%,占机电产品出口的比重从1997年的58%提高到67%,成为机电产品出口的主体力量。 有关权威人士指出，虽然我国的机电产品出口增长很快，但还存在着一些问题。一是出口企业国际竞争力还有待提高，突出表现在研发及其投入不足。不少企业缺乏自主知识产权和核心技术，创新能力较弱。二是出口结构尚待优化，技术含量与附加值较高的产品比重较低，高端产品出口规模小。三是出口秩序仍需改善，一些企业低价竞销，贸易摩擦有所增加。四是国际贸易保护主义使我国机电产品出口频频遭遇市场准入限制。";
        NewsMd5.newsMd5(str);
    }
}

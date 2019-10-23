package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * @author chenyan
 * data 2019-10-21
 */
public class ForMat {

    public static void main(String[] args) throws ParseException {
        String time = "2019/08/06 12:00";
        String datetimeFormat = ForMat.getDatetimeFormat(time);
        System.out.println(datetimeFormat);
    }

    /**
     * 判断时间格式
     * @param date
     * @return
     */
    public static String getDatetimeFormat(String date) throws ParseException {
        date=date.trim();
        String a1 = "[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}";//yyyyMMddHHmmss
        String a2 = "[0-9]{4}[0-9]{2}[0-9]{2}";//yyyyMMdd
        String a3 = "[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}";//yyyyMMddHHmm
        String a4 = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a5 = "[0-9]{4}-[0-9]{2}-[0-9]{2}";//yyyy-MM-dd
        String a6= "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";//yyyy-MM-dd  HH:mm
        String a7= "\\d{4}/\\d{2}/\\d{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a8= "\\d{4}/\\d{2}/\\d{2}";//yyyy/MM/dd
        String a9= "\\d{4}/\\d{2}/\\d{2} [0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm
        String a10= "\\d{4}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5]";//yyyy/MM/dd
        String a11= "\\d{4}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5] [0-9]{2}:[0-9]{2}";//yyyy/MM/dd HH:mm
        String a12= "\\d{4}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5] [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd HH:mm:ss

        //yyyyMMddHHmmss
        boolean datea1 = Pattern.compile(a1).matcher(date).matches();
        if(datea1){
            Date orderDateStart = new SimpleDateFormat("yyyyMMddHHmmss").parse(date);
            String DateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderDateStart);
            return DateStart;
        }

        //yyyyMMdd
        boolean datea2 = Pattern.compile(a2).matcher(date).matches();
        if(datea2==true){
            Date orderDateStart = new SimpleDateFormat("yyyyMMdd").parse(date);
            String DateStart = new SimpleDateFormat("yyyy-MM-dd").format(orderDateStart);
            return DateStart + " 00:00:00";
        }

        //yyyyMMddHHmm
        boolean datea3 = Pattern.compile(a3).matcher(date).matches();
        if(datea3==true){
            Date orderDateStart = new SimpleDateFormat("yyyyMMddHHmm").parse(date);
            String DateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(orderDateStart);
            return DateStart + ":00";
        }

        //yyyy-MM-dd HH:mm:ss
        boolean datea4 = Pattern.compile(a4).matcher(date).matches();
        if(datea4==true){
            return date;
        }

        //yyyy-MM-dd
        boolean datea5 = Pattern.compile(a5).matcher(date).matches();
        if(datea5==true){
            return date +" 00:00:00";
        }

        //yyyy-MM-dd  HH:mm
        boolean datea6 = Pattern.compile(a6).matcher(date).matches();
        if(datea6==true){
            String time = date+":00";
            return time;
        }

        //yyyy/MM/dd  HH:mm:ss
        boolean datea7 = Pattern.compile(a7).matcher(date).matches();
        if(datea7==true){
            String time = date.replaceAll("/", "-");
            return time;
        }

        //yyyy/MM/dd
        boolean datea8 = Pattern.compile(a8).matcher(date).matches();
        if(datea8==true){
            String time = date.replaceAll("/", "-");
            return time + " 00:00:00";
        }

        //yyyy/MM/dd  HH:mm
        boolean datea9 = Pattern.compile(a9).matcher(date).matches();
        if(datea9==true){
            String time = date.replaceAll("/", "-");
            return time + ":00";
        }


        //yyyy/MM/dd
        boolean datea10 = Pattern.compile(a10).matcher(date).matches();
        if(datea10==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time + " 00:00:00";
        }

        //yyyy/MM/dd HH:mm
        boolean datea11 = Pattern.compile(a11).matcher(date).matches();
        if(datea11==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time + ":00";
        }

        //yyyy/MM/dd HH:mm:ss
        boolean datea12 = Pattern.compile(a12).matcher(date).matches();
        if(datea12==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time;
        }
        return "";
    }

}


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
        String a13= "\\d{4}/\\d{1}/\\d{1} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a14= "\\d{4}/\\d{2}/\\d{1} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a15= "\\d{4}/\\d{1}/\\d{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a16= "\\d{4}/\\d{1}/\\d{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a17= "\\d{4}/\\d{1}/\\d{1} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a18= "\\d{4}/\\d{2}/\\d{1} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a19= "\\d{4}/\\d{1}/\\d{2} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a20= "\\d{4}/\\d{1}/\\d{2} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a21= "\\d{4}/\\d{2}/\\d{2} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy/MM/dd  HH:mm:ss
        String a22 = "[0-9]{4}-[0-9]{1}-[0-9]{1} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a23 = "[0-9]{4}-[0-9]{2}-[0-9]{1} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a24 = "[0-9]{4}-[0-9]{1}-[0-9]{2} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a25 = "[0-9]{4}-[0-9]{1}-[0-9]{1} [0-9]{1}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a26 = "[0-9]{4}-[0-9]{1}-[0-9]{1} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a27 = "[0-9]{4}-[0-9]{2}-[0-9]{1} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a28 = "[0-9]{4}-[0-9]{1}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a29 = "[0-9]{4}-[0-9]{1}-[0-9]{1} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss

        String a30= "\\d{4}/\\d{1}/\\d{1}";//yyyy/M/d
        String a31= "\\d{4}/\\d{1}/\\d{2}";//yyyy/M/dd
        String a32= "\\d{4}/\\d{2}/\\d{1}";//yyyy/MM/dd

        String a33= "\\d{4}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5]\\d{1}[\u4e00-\u9fa5] [0-9]{2}:[0-9]{2}";//yyyy/MM/dd HH:mm
        String a34= "\\d{4}[\u4e00-\u9fa5]\\d{1}[\u4e00-\u9fa5]\\d{1}[\u4e00-\u9fa5] [0-9]{2}:[0-9]{2}";//yyyy/MM/dd HH:mm
        String a35= "\\d{4}[\u4e00-\u9fa5]\\d{1}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5] [0-9]{2}:[0-9]{2}";//yyyy/MM/dd HH:mm

        String a36= "\\d{4}[\u4e00-\u9fa5]\\d{1}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5]";//yyyy/M/dd
        String a37= "\\d{4}[\u4e00-\u9fa5]\\d{2}[\u4e00-\u9fa5]\\d{1}[\u4e00-\u9fa5]";//yyyy/MM/d
        String a38= "\\d{4}[\u4e00-\u9fa5]\\d{1}[\u4e00-\u9fa5]\\d{1}[\u4e00-\u9fa5]";//yyyy/M/d

        String a39 = "[0-9]{4}-[0-9]{2}-[0-9]{1}";//yyyy-MM-d
        String a40 = "[0-9]{4}-[0-9]{1}-[0-9]{2}";//yyyy-M-dd
        String a41 = "[0-9]{4}-[0-9]{1}-[0-9]{1}";//yyyy-M-d

        //yyyy-MM-dd
        boolean datea39 = Pattern.compile(a39).matcher(date).matches();
        if(datea39==true){
            return date +" 00:00:00";
        }
        //yyyy-MM-dd
        boolean datea40 = Pattern.compile(a40).matcher(date).matches();
        if(datea40==true){
            return date +" 00:00:00";
        }
        //yyyy-MM-dd
        boolean datea41 = Pattern.compile(a41).matcher(date).matches();
        if(datea41==true){
            return date +" 00:00:00";
        }




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

        //yyyy/M/d  HH:mm:ss
        boolean datea13 = Pattern.compile(a13).matcher(date).matches();
        if(datea13==true){
            String time = date.replaceAll("/", "-");
            return time;
        }

        //yyyy/MM/d  HH:mm:ss
        boolean datea14 = Pattern.compile(a14).matcher(date).matches();
        if(datea14==true){
            String time = date.replaceAll("/", "-");
            return time;
        }

        //yyyy/MM/dd  HH:mm:ss
        boolean datea15 = Pattern.compile(a15).matcher(date).matches();
        if(datea15==true){
            String time = date.replaceAll("/", "-");
            return time;
        }

        //yyyy/MM/dd  HH:mm:ss
        boolean datea16 = Pattern.compile(a16).matcher(date).matches();
        if(datea16==true){
            String time = date.replaceAll("/", "-");
            return time;
        }
//yyyy/MM/dd  HH:mm:ss
        boolean datea17 = Pattern.compile(a17).matcher(date).matches();
        if(datea17==true){
            String time = date.replaceAll("/", "-");
            return time;
        }
//yyyy/MM/dd  HH:mm:ss
        boolean datea18 = Pattern.compile(a18).matcher(date).matches();
        if(datea18==true){
            String time = date.replaceAll("/", "-");
            return time;
        }

//yyyy/MM/dd  HH:mm:ss
        boolean datea19 = Pattern.compile(a19).matcher(date).matches();
        if(datea19==true){
            String time = date.replaceAll("/", "-");
            return time;
        }

        //yyyy/MM/dd  HH:mm:ss
        boolean datea20 = Pattern.compile(a20).matcher(date).matches();
        if(datea20==true){
            String time = date.replaceAll("/", "-");
            return time;
        }

//yyyy/MM/dd  HH:mm:ss
        boolean datea21 = Pattern.compile(a21).matcher(date).matches();
        if(datea21==true){
            String time = date.replaceAll("/", "-");
            return time;
        }

        //yyyy-MM-dd HH:mm:ss
        boolean datea22 = Pattern.compile(a22).matcher(date).matches();
        if(datea22==true){
            return date;
        }
        //yyyy-MM-dd HH:mm:ss
        boolean datea23 = Pattern.compile(a23).matcher(date).matches();
        if(datea23==true){
            return date;
        }
        //yyyy-MM-dd HH:mm:ss
        boolean datea24 = Pattern.compile(a24).matcher(date).matches();
        if(datea24==true){
            return date;
        }
        //yyyy-MM-dd HH:mm:ss
        boolean datea25 = Pattern.compile(a25).matcher(date).matches();
        if(datea25==true){
            return date;
        }
        //yyyy-MM-dd HH:mm:ss
        boolean datea26 = Pattern.compile(a26).matcher(date).matches();
        if(datea26==true){
            return date;
        }
        //yyyy-MM-dd HH:mm:ss
        boolean datea27 = Pattern.compile(a27).matcher(date).matches();
        if(datea27==true){
            return date;
        }
        //yyyy-MM-dd HH:mm:ss
        boolean datea28 = Pattern.compile(a28).matcher(date).matches();
        if(datea28==true){
            return date;
        }
        //yyyy-MM-dd HH:mm:ss
        boolean datea29 = Pattern.compile(a29).matcher(date).matches();
        if(datea29==true){
            return date;
        }

        //yyyy-MM-dd
        boolean datea30 = Pattern.compile(a30).matcher(date).matches();
        if(datea30==true){
            String time = date.replaceAll("/", "-");
            return time +" 00:00:00";
        }
        boolean datea31 = Pattern.compile(a31).matcher(date).matches();
        if(datea31==true){
            String time = date.replaceAll("/", "-");
            return time +" 00:00:00";
        }
        boolean datea32 = Pattern.compile(a32).matcher(date).matches();
        if(datea32==true){
            String time = date.replaceAll("/", "-");
            return time +" 00:00:00";
        }
        //yyyy/MM/dd HH:mm
        boolean datea33 = Pattern.compile(a33).matcher(date).matches();
        if(datea33==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time + ":00";
        }
        //yyyy/MM/dd HH:mm
        boolean datea34 = Pattern.compile(a34).matcher(date).matches();
        if(datea34==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time + ":00";
        }
        //yyyy/MM/dd HH:mm
        boolean datea35 = Pattern.compile(a35).matcher(date).matches();
        if(datea35==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time + ":00";
        }

        //yyyy/MM/dd
        boolean datea36 = Pattern.compile(a36).matcher(date).matches();
        if(datea36==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time + " 00:00:00";
        }

        //yyyy/MM/dd
        boolean datea37 = Pattern.compile(a37).matcher(date).matches();
        if(datea37==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time + " 00:00:00";
        }

        //yyyy/MM/dd
        boolean datea38 = Pattern.compile(a38).matcher(date).matches();
        if(datea38==true){
            String time = date
                    .replaceAll("年", "-")
                    .replaceAll("月", "-")
                    .replaceAll("日", "");
            return time + " 00:00:00";
        }


        return "";
    }

}


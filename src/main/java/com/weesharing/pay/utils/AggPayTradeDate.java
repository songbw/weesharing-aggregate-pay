package com.weesharing.pay.utils;

import cn.hutool.core.date.DateUtil;
import com.weesharing.pay.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author clark
 * @since 2020-05-14
 * */
@Slf4j
public class AggPayTradeDate {

    private static final String DATE_TIME_PATTERN = "^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
    private static final String LOCAL_DATE_TIME_PATTERN = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}";
    private static final String DATE_TIME_PATTERN_ALL_NUM14 = "^[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}";
    private static final String DATE_TIME_PATTERN_ALL_NUM17 = "^[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{3}";
    public static final String TRADE_DATE_FORMAT = "yyyyMMddHHmmss";

    public static boolean needNormalize(String dateString){
        if(null == dateString || dateString.isEmpty()){
            return false;
        }

        Pattern pattern = Pattern.compile(DATE_TIME_PATTERN);
        Matcher matcher = pattern.matcher(dateString);
        if (matcher.matches()){
            return true;
        }

        Pattern patternLocal = Pattern.compile(LOCAL_DATE_TIME_PATTERN);
        Matcher matcherLocal = patternLocal.matcher(dateString);
        if (matcherLocal.matches()){
            return true;
        }

        Pattern patternNum17 = Pattern.compile(DATE_TIME_PATTERN_ALL_NUM17);
        Matcher matcherNum17 = patternNum17.matcher(dateString);
        if(matcherNum17.matches()){
            return true;
        }
        return false;
    }

    public static String buildTradeDate(){
        return DateUtil.format(new Date(), TRADE_DATE_FORMAT);
    }

    public static String buildTradeDate(String dateString){
        if(null == dateString || dateString.isEmpty()) {
            return DateUtil.format(new Date(), TRADE_DATE_FORMAT);
        }

        dateString = dateString.trim();
        Pattern patternNum14 = Pattern.compile(DATE_TIME_PATTERN_ALL_NUM14);
        Matcher matcherNum14 = patternNum14.matcher(dateString);
        if(matcherNum14.matches()){
            return dateString;
        }else{
            Pattern pattern = Pattern.compile(DATE_TIME_PATTERN);
            Matcher matcher = pattern.matcher(dateString);
            if (!matcher.matches()){
                Pattern patternLocal = Pattern.compile(LOCAL_DATE_TIME_PATTERN);
                Matcher matcherLocal = patternLocal.matcher(dateString);
                if (!matcherLocal.matches()) {
                    Pattern patternNum17 = Pattern.compile(DATE_TIME_PATTERN_ALL_NUM17);
                    Matcher matcherNum17 = patternNum17.matcher(dateString);
                    if (matcherNum17.matches()) {
                       return dateString.substring(0, TRADE_DATE_FORMAT.length());
                    } else {
                        throw new ServiceException("日期时间格式错误");
                    }
                }
            }

            return dateString.substring(0,4)+dateString.substring(5,7)+dateString.substring(8,10)+
                    dateString.substring(11,13)+dateString.substring(14,16)+dateString.substring(17,19);
        }

    }

    public static String
    LocalDate2String(LocalDateTime dateTime){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(TRADE_DATE_FORMAT);
        return df.format(dateTime);
    }
}

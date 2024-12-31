package com.example.task2.dao;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {

    private  static final DateTimeFormatter df = DateTimeFormatter.ISO_DATE;

    @TypeConverter
    public static LocalDate fromString(String vaule){
        return  vaule == null ? null : LocalDate.parse(vaule,df);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate date){
        return date == null ? null : date.format(df);
    }

}

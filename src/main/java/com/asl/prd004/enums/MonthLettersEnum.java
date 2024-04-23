package com.asl.prd004.enums;

public enum MonthLettersEnum {
    Jan(1, "Jan", "January "),
    Feb(2, "Feb", "February"),
    Mar(3, "Mar", "March"),
    Apr(4, "Apr", "April"),
    May(5, "May", "May"),
    Jun(6, "Jun", "June"),
    Jul(7, "Jul", "July"),
    Aug(8, "Aug", "August"),
    Sep(9, "Sep", "september"),
    Oct(10, "Oct", "October"),
    Nov(11, "Nov", "November"),
    Dec(12, "Dec", "December");

    MonthLettersEnum(Integer month , String letter, String fullLetter) {
        this.month = month;
        this.letter = letter;
        this.fullLetter = fullLetter;
    }

    private Integer month;
    private String letter;
    private String fullLetter;

    public static String getLetterByMonth(Integer month) {
        if (null == month) {
            return null;
        }
        for (MonthLettersEnum months : MonthLettersEnum.values()) {
            if (months.getMonth().intValue() == month.intValue()) {
                return months.getLetter();
            }
        }
        return null;
    }

    public static String getFullLetterByMonth(Integer month) {
        if (null == month) {
            return null;
        }
        for (MonthLettersEnum months : MonthLettersEnum.values()) {
            if (months.getMonth().intValue() == month.intValue()) {
                return months.getFullLetter();
            }
        }
        return null;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getFullLetter() {
        return fullLetter;
    }

    public void setFullLetter(String fullLetter) {
        this.fullLetter = fullLetter;
    }
}

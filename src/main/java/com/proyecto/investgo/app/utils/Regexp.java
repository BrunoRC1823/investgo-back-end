package com.proyecto.investgo.app.utils;

public class Regexp {
	public static final String PATTERN_NAME_LASTNAME = "^[A-Z](['][A-Za-z]?[áéíóú]?)?[A-Za-z]*( [A-Z](['][A-Za-z]?[áéíóú]?)?[A-Za-z]*)*$";
	public static final String PATTERN_PHONE = "^(9\\d{8})$";
	public static final String PATTERN_PASSWORD = "^(?=.*[A-Z])(?=.*[\\d])(?=.*[@#$%^&+=!])(?!.*\\s).{8,}$";
	public static final String PATTERN_NUMBER = "^[\\d]+$";
	public static final String PATTERN_NRO_CUENTA_BANCARIA = "^[A-Z]{2}[\\d]{2}-[\\d]{4}-[\\d]{4}-[\\d]{2}-[\\d]{10}$";
	public static final String PATTERN_NRO_CCI = "^[\\d]{3}-[\\d]{3}-[\\d]{12}-[\\d]{2}$";
	public static final String PATTERN_RUC = "^(10|20|17|15)[0-9]+$";
	public static final String PATTERN_RAZON_SOCIAL = "^[A-Z](['][A-Za-z]?[áéíóú]?)?[A-Za-z]*( [A-Z](['][A-Za-z]?[áéíóú]?)?[A-Za-z]*)* (S.A.C.S|S.A.C|S.A|S.A.A|S.R.L|E.I.R.L)$";
	public static final String PATTERN_MES ="^(0?[1-9]|1[0-2])$";
	public static final String PATTERN_YEAR ="^(20)[1|2]\\d{1}$";
}

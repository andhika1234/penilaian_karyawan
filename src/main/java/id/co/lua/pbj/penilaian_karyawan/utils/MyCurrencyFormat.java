package id.co.lua.pbj.penilaian_karyawan.utils;

import java.text.DateFormat;
import java.text.*;
import java.util.Date;
import java.util.Locale;

public class MyCurrencyFormat {
	public static final Locale indonesia = new Locale("ID","id");
	public static final DateFormat defaultDateFormat =  DateFormat.getDateInstance(DateFormat.LONG, indonesia);
	public static final DateFormat defaultTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK);

	public static String formatCurrencyRupiah(Number number) {
		String hasil = "";
		if (number != null) {
			NumberFormat format = NumberFormat.getCurrencyInstance(indonesia);
			format.setMaximumFractionDigits(0);
			hasil = format.format(number).replaceAll("Rp", "");
		}
		return hasil;
	}

	public static String formatCurrencyRupiahNew2(Number number) {
		String hasil = "";
		if (number != null) {
			Locale indonesia = new Locale("id", "ID");
			NumberFormat format = NumberFormat.getCurrencyInstance(indonesia);
			format.setMaximumFractionDigits(0);
			String formattedNumber = format.format(number);
			hasil = formattedNumber.replaceAll("[Rp\\s]", "").replaceAll("[,.]", "");
		}
		return hasil;
	}

	public static String formatDateInd(Date date){
		if(date != null)
			return defaultDateFormat.format(date);
		return "";
	}
	public static String formatDateTimeInd(Date date){
		return formatDateInd(date)+" "+formatTime(date);
	}

	public static String formatTime(Date date) {
		if (date != null) {
			return defaultTimeFormat.format(date);
		}
		return "";
	}

	public static String formatNumber(Number number) {
		String hasil = "";
		if (number != null) {
			return NumberFormat.getInstance().format(number);
		}
		return hasil;
	}

	public static String number2Word(long number) {
		String[] numbers = {"", "satu ", "dua ", "tiga ", "empat ", "lima ", "enam ", "tujuh ", "delapan ", "sembilan "};
		String[] levels = {"", "ribu ", "juta ", "milyar ", "trilyun "};
		StringBuffer result = new StringBuffer();

		String str = String.valueOf(number);
		int len = (str.length() + 2) / 3;
		String[] groups = new String[len];

		// Memisahkan angka menjadi kelompok-kelompok tiga digit
		for (int i = 0; i < len; i++) {
			int end = str.length() - i * 3;
			int start = Math.max(0, end - 3);
			groups[len - 1 - i] = str.substring(start, end);
		}

		for (int i = 0; i < len; i++) {
			int value = Integer.parseInt(groups[i]);
			if (value == 0) continue; // Skip jika kelompok bernilai 0

			int hundreds = value / 100;
			int tens = (value % 100) / 10;
			int ones = value % 10;

			if (hundreds > 0) {
				if (hundreds == 1) {
					result.append("seratus ");
				} else {
					result.append(numbers[hundreds]).append("ratus ");
				}
			}

			if (tens == 1) {
				if (ones == 0) {
					result.append("sepuluh ");
				} else if (ones == 1) {
					result.append("sebelas ");
				} else {
					result.append(numbers[ones]).append("belas ");
				}
			} else if (tens > 1) {
				result.append(numbers[tens]).append("puluh ");
				if (ones > 0) {
					result.append(numbers[ones]);
				}
			} else if (ones > 0) {
				result.append(numbers[ones]);
			}

			if (i < len - 1 && value > 0) {
				result.append(levels[len - 1 - i]);
			}
		}

		return result.toString().trim();
	}

	public static int parseInt(char c) { 
        int result = c - 48; 
        if(result<0 || result>9) throw new NumberFormatException("For input char: '"+c+"'"); 
        return result; 
    }

	public static String getDateIndonesian(Date date){
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID"));
			return sdf.format(date);
		}return null;
	}
	public static String getDateIndonesianSHORT(Date date){
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"));
			return sdf.format(date);
		}return null;
	}

	public static String formatTimeNoDate(Date date) {
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			return format.format(date);
		}
		return "";
	}

	public static String formatTimeDay(Date date) {
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.forLanguageTag("id-ID"));
			return format.format(date);
		}
		return "";
	}

	public static String formatBulan(Date date) {
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("MMMM", Locale.forLanguageTag("id-ID"));
			return format.format(date);
		}
		return "";
	}

	public static String formatTahun(Date date) {
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy");
			return format.format(date);
		}
		return "";
	}

}

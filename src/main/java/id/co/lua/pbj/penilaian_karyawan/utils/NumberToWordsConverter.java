package id.co.lua.pbj.penilaian_karyawan.utils;

public class NumberToWordsConverter {

    private static final String[] UNITS = {"", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan"};
    private static final String[] TEENS = {"Sepuluh", "Sebelas", "Dua Belas", "Tiga Belas", "Empat Belas", "Lima Belas", "Enam Belas", "Tujuh Belas", "Delapan Belas", "Sembilan Belas"};
    private static final String[] TENS = {"", "Sepuluh", "Dua Puluh", "Tiga Puluh", "Empat Puluh", "Lima Puluh", "Enam Puluh", "Tujuh Puluh", "Delapan Puluh", "Sembilan Puluh"};
    private static final String[] THOUSANDS = {"", "Ribu", "Juta", "Miliar", "Triliun"};

    public static String convertToWords(double number) {
        if (number == 0) return "Nol Rupiah";

        long numberLong = (long) number;
        StringBuilder words = new StringBuilder();

        int chunkCount = 0;
        while (numberLong > 0) {
            int chunk = (int) (numberLong % 1000);
            if (chunk != 0) {
                String chunkWords = convertChunk(chunk);
                if (!chunkWords.isEmpty()) {
                    words.insert(0, chunkWords + " " + THOUSANDS[chunkCount] + " ");
                }
            }
            numberLong /= 1000;
            chunkCount++;
        }

        return words.toString().trim() + " Rupiah";
    }

    private static String convertChunk(int number) {
        if (number == 0) return "";

        StringBuilder chunk = new StringBuilder();
        if (number >= 100) {
            chunk.append(UNITS[number / 100]).append(" Ratus ");
            number %= 100;
        }
        if (number >= 20) {
            chunk.append(TENS[number / 10]);
            if (number % 10 != 0) {
                chunk.append(" ").append(UNITS[number % 10]);
            }
        } else if (number >= 10) {
            chunk.append(TEENS[number - 10]);
        } else if (number > 0) {
            chunk.append(UNITS[number]);
        }

        return chunk.toString().trim();
    }
}




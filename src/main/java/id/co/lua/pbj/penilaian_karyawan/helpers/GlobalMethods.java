package id.co.lua.pbj.penilaian_karyawan.helpers;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class GlobalMethods {
    public static void setRedirectAttribute(RedirectAttributes redirectAttributes, String status, String message, Object data, BindingResult result) {
        redirectAttributes.addFlashAttribute("resultCode", status);
        redirectAttributes.addFlashAttribute("resultMessage", message);
        if (data != null) {
            redirectAttributes.addFlashAttribute("data", data);
        }
        if (result != null) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.data", result);
        }
    }
//    public static boolean isMobile(String userAgent){
//        boolean isMobile = false;
//        if(StringFormat.stringContainsItemFromList(userAgent, new String[]{"Android", "iPhone"})){
//            isMobile = true;
//        }
//        return isMobile;
//    }
}
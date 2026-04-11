package cohappy.backend.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringCheckUtil {
    public static boolean isNotEmptyString(String str){
        return str != null && !str.isBlank();
    }

    public static boolean isEmptyString(String str){
        return str == null || str.isBlank();
    }
}

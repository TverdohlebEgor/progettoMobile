package cohappy.backend.util;

import lombok.experimental.UtilityClass;
import java.util.Objects;
import java.util.function.Consumer;

@UtilityClass
public class PatchUtil {
    public static <T> boolean patchField(T newValue, T currentValue, Consumer<T> setter) {
        if (newValue != null && !Objects.equals(newValue, currentValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }
}

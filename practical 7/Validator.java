import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class Validator {

    public static List<String> check(Object obj) {

        List<String> errors = new ArrayList<>();

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {

            field.setAccessible(true);

            try {
                String value = (String) field.get(obj);

                if (field.isAnnotationPresent(NotBlank.class)) {
                    if (value == null || value.trim().isEmpty()) {
                        errors.add(field.getName() + " cannot be blank");
                    }
                }

                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength max = field.getAnnotation(MaxLength.class);

                    if (value != null && value.length() > max.value()) {
                        errors.add(field.getName() + " is too long");
                    }
                }

            } catch (Exception e) {
                System.out.println("Error");
            }
        }

        return errors;
    }
}
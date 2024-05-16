package hso.autonomy.util.properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TabSeparatedPropertiesUtil {
    private static final Map<String, String> properties = new HashMap<>();

    // 私有构造函数以防止实例化
    private TabSeparatedPropertiesUtil() {
        throw new IllegalStateException("Utility class");
    }

    // Now the method is public and must be called explicitly with a file path
    public static void loadProperties(String filePath) {
        synchronized (properties) {
            properties.clear(); // 清除现有属性
            System.out.println("Loading properties from: " + filePath);
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 正确使用 "\\t\\t" 来分割键和值
                    String[] keyValue = line.split("\\t", 2);
                    if (keyValue.length == 2) {
                        properties.put(keyValue[0], keyValue[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getProperty(String key) {
        return properties.get(key);
    }

    public static float getFloatProperty(String key) {
        String value = getProperty(key);
        System.out.println("Tab"+value+" key"+key);
        return value != null ? Float.parseFloat(value) : Float.NaN; // Return NaN if the key doesn't exist
    }

    public static double getDoubleProperty(String key) {
        String value = getProperty(key);
        return value != null ? Double.parseDouble(value) : Double.NaN; // Return NaN if the key doesn't exist
    }

    public static int getIntegerProperty(String key) {
        String value = getProperty(key);
        return value != null ? Integer.parseInt(value) : 0; // Return 0 if the key doesn't exist
    }

    public static short getShortProperty(String key) {
        String value = getProperty(key);
        return value != null ? Short.parseShort(value) : 0; // Return 0 if the key doesn't exist
    }

    public static byte getByteProperty(String key) {
        String value = getProperty(key);
        return value != null ? Byte.parseByte(value) : 0; // Return 0 if the key doesn't exist
    }

    // The rest of your methods..
}

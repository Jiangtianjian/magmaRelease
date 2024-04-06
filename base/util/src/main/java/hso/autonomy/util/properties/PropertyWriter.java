package hso.autonomy.util.properties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PropertyWriter {
    private static String filepath;

    public PropertyWriter(String filepath) {
        PropertyWriter.filepath = filepath;
    }

    public static void saveFitness(double fitness) throws IOException {
        // 使用 BufferedWriter 和 FileWriter 以指定的格式写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            // 创建一个字符串，fitness 值
            String content = String.valueOf(fitness);
            // 写入字符串内容
            writer.write(content);
            writer.newLine();  // 添加换行符，以便每个属性值单独占一行
        }
    }
}

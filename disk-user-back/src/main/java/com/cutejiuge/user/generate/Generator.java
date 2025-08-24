package com.cutejiuge.user.generate;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.apache.ibatis.type.JdbcType;

/**
 * 实体类代码生成器
 *
 * @author cutejiuge
 * @since 2025/8/23 下午10:20
 */
public class Generator {
    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_service_db?useUnicode=true&characterSet=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USERNAME = System.getenv("DB_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    // 包配置
    private static final String PARENT_PACKAGE = "com.cutejiuge.user";
    private static final String MODULE_NAME = "";

    // 作者信息
    private static final String AUTHOR = "cutejiuge";

    // 输出目录
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/disk-user-back/src/main/java";

    // 要生成代码的表名（可以指定多个表）
    private static final String[] TABLE_NAMES = {
            "tb_user", "tb_user_login_log", "tb_user_token", "tb_verification_code"
    };

    public static void main(String[] args) {
        generateCode();
    }

    // 执行代码生成
    private static void generateCode() {
        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            // 兼容旧版本转换成Integer
                            if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                // 全局配置
                .globalConfig(builder -> {
                    builder.author(AUTHOR)
                            .outputDir(OUTPUT_DIR)
                            .dateType(DateType.TIME_PACK)
                            .commentDate("yyyy-MM-dd HH:mm:ss")
                            .disableOpenDir();
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(PARENT_PACKAGE)
                            .moduleName(MODULE_NAME)
                            .entity("entity")
                            .mapper("mapper");
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(TABLE_NAMES)
                            .addTablePrefix("tb_")
                            // entity配置
                            .entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .idType(IdType.ASSIGN_ID)
                            .formatFileName("%sEntity")
                            .addTableFills(
                                    new Column("created_at", FieldFill.INSERT),
                                    new Column("updated_at", FieldFill.INSERT_UPDATE)
                            )
                            .logicDeleteColumnName("deleted_at")
                            // mapper配置
                            .mapperBuilder()
                            .enableMapperAnnotation()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")
                            // service配置
                            .serviceBuilder()
                            .disable()
                            // controller配置
                            .controllerBuilder()
                            .disable();
                })
                // 模板引擎配置
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
        System.out.println("代码生成完成！");
    }
}

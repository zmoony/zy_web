package com.web.demo.service;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.demo.common.R;
import com.web.demo.common.ResultCodeEnum;
import com.web.demo.exception.GlobalException;
import com.web.demo.util.DBTool;
import com.web.demo.util.JavaTool;
import com.web.demo.vo.DBExportVo;
import com.web.demo.vo.DBTransformVo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.jmmo.tuple.Tuple3;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.swing.filechooser.FileSystemView;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yuez
 * @title: DbService
 * @projectName zy_web
 * @description: 数据库工作集
 * @date 2020/8/28 15:45
 */
@Slf4j
@Service
public class DbService {

    public R exportDML(DBExportVo dBExportVo) throws GlobalException{
        try {
            ScrewApplication.contextLoads(DBType.getByType(dBExportVo.getType()),
                    dBExportVo,DBType.getEngineFileTypeByType(dBExportVo.getFileType()));
        } catch (Exception e) {
            log.error("导出表文档失败:{}",e);
            throw new GlobalException(ResultCodeEnum.UNKNOWN_ERROR.getCode(),"导出表文档失败");
        }
        return R.ok();
    }
    public R transformJAVA(DBTransformVo vo) throws GlobalException{
        try {
            switch (vo.getType()){
                case "DDL":return TransApplication.ddlGenerator(vo);
                case "JSON":return TransApplication.jsonGenerator(vo);
                case "TABLE2CODE":return TransApplication.sqlGenerator(vo);
                case "TABLE2JSON":return TransApplication.sql2JsonGenerator(vo);
                case "TABLE2COLUMN":return TransApplication.sql2ColumnGenerator(vo);
            }
        } catch (Exception e) {
            log.error("生成JAVABEAN:{}",e);
            throw new GlobalException(ResultCodeEnum.UNKNOWN_ERROR.getCode(),"生成JAVABEAN失败");
        }
        return R.error().setMessage("转换参数错误");
    }


}

class ScrewApplication {
    public static void contextLoads(DBType dbType, DBExportVo vo, EngineFileType e) {
        Properties properties = new Properties();
        properties.setProperty("driverClassName",dbType.getDriver());
        properties.setProperty("jdbcUrl",String.format(dbType.getUrl(),vo.getIp(),vo.getPort(),vo.getServiceId()));
        properties.setProperty("username",vo.getUsername());
        properties.setProperty("password",vo.getPassword());
        DataSource dataSourceMysql = new HikariDataSource(new HikariConfig(properties));
        // 生成文件配置
        EngineConfig engineConfig = EngineConfig.builder()
                // 生成文件路径，自己mac本地的地址，这里需要自己更换下路径
                .fileOutputDir(FileSystemView.getFileSystemView() .getHomeDirectory().getAbsolutePath())
//                .fileOutputDir(vo.getDest())
                // 打开目录
                .openOutputDir(false)
                // 文件类型
                .fileType(e)
                // 生成模板实现
                .produceType(EngineTemplateType.freemarker).build();
        // 生成文档配置（包含以下自定义版本号、描述等配置连接）
        Configuration config = Configuration.builder()
                .version(vo.getVersion())
                .description(vo.getDesc())
                .dataSource(dataSourceMysql)
                .engineConfig(engineConfig)
                .produceConfig(getProcessConfig(vo))
                .build();
        // 执行生成
        new DocumentationExecute(config).execute();
    }
    /**
     * 配置想要生成的表+ 配置想要忽略的表
     *
     * @return 生成表配置
     */
    public static ProcessConfig getProcessConfig(DBExportVo vo) {
        return ProcessConfig.builder()
                //根据名称指定表生成
                .designatedTableName(Arrays.asList(vo.getDesignatedTableName().split(",")))
                //根据表前缀生成
                .designatedTablePrefix(Arrays.asList(vo.getDesignatedTablePrefix().split(",")))
                //根据表后缀生成
                .designatedTableSuffix(Arrays.asList(vo.getDesignatedTableSuffix().split(",")))
                //忽略表名
                .ignoreTableName(Arrays.asList(vo.getIgnoreTableName().split(",")))
                //忽略表前缀
                .ignoreTablePrefix(Arrays.asList(vo.getIgnoreTablePrefix().split(",")))
                //忽略表后缀
                .ignoreTableSuffix(Arrays.asList(vo.getIgnoreTableSuffix().split(",")))
                .build();
    }
}
@Slf4j
class TransApplication {

    public static R ddlGenerator (DBTransformVo vo) {
        String tableName = "实体类";
        String tableComment = "实体类";
        List<Tuple3> columns = new ArrayList<>();
        String ddl = vo.getDdl();
        String str = "CREATE\\s*TABLE\\s*'?(\\w+)'?\\s*\\((.*)\\).*COMMENT\\s*=\\s*'?(.*)'?";
        Pattern p = Pattern.compile( str,Pattern.CASE_INSENSITIVE);
        Pattern p1 = Pattern.compile( "'?(\\w+)'?\\s+(\\w+).*COMMENT\\s+'?(.*)'?",Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(ddl.replaceAll("\n",""));
        if(m.find()){
            tableName = JavaTool.firstToUpper(m.group(1));
            tableComment = m.groupCount()==3?m.group(3):"";
            String[] context = m.group(2).split(",");
            for (String cx : context) {
                Matcher m1 = p1.matcher(cx);
                if(m1.find()){
                    switch (vo.getNameRex()){
                        case "camel":
                            columns.add(Tuple3.of(JavaTool.lineToHump(m1.group(1)),Optional.ofNullable(DBTool.sqlMapper(m1.group(2))).orElse(m1.group(2)),
                                    m1.groupCount()==2?null:m1.group(3).replaceAll("'","")));
                            break;
                        case "line":
                            columns.add(Tuple3.of(m1.group(1),Optional.ofNullable(DBTool.sqlMapper(m1.group(2))).orElse(m1.group(2)),
                                    m1.groupCount()==2?null:m1.group(3).replaceAll("'","")));
                            break;
                        default:
                            columns.add(Tuple3.of(m1.group(1),Optional.ofNullable(DBTool.sqlMapper(m1.group(2))).orElse(m1.group(2)),
                                    m1.groupCount()==2?null:m1.group(3).replaceAll("'","")));
                    }

                }
            }
        }
        String javabean = javabean(vo, tableName, tableComment.replaceAll("'",""), columns);
        return R.ok().setData(Collections.singletonMap("javabean",javabean));
    }

    public static R jsonGenerator (DBTransformVo vo) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(vo.getDdl(), new TypeReference<Map<String, Object>>() {});
        Map<String, Object> data = new HashMap<>();
        mapToJavaBean(vo,map,"JSONBean",data);
        return R.ok().setData(data);
    }

    public static R sqlGenerator(DBTransformVo vo) throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("driverClassName",DBType.getByType(vo.getType()).getDriver());
        properties.setProperty("jdbcUrl",String.format(DBType.getByType(vo.getType()).getUrl(),
                vo.getIp().split(",")[0],vo.getIp().split(",")[1],vo.getIp().split(",")[2]));
        properties.setProperty("username",vo.getUsername());
        properties.setProperty("password",vo.getPassword());
        DataSource ds = new HikariDataSource(new HikariConfig(properties));
        String javabean="";
        List<Tuple3> columns = new ArrayList<>();
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            String sql = vo.getTableName().equals("")?vo.getSql():String.format("SELECT t1.COLUMN_NAME fieldName,t1.DATA_TYPE fieldType,t2.COMMENTS  fieldDescribe \n" +
                    "FROM USER_TAB_COLUMNS t1 LEFT JOIN USER_COL_COMMENTS t2 on t1.COLUMN_NAME=t2.COLUMN_NAME \n" +
                    "WHERE t1.TABLE_NAME='%s' AND t2.TABLE_NAME='%s'",vo.getTableName().toUpperCase(),vo.getTableName().toUpperCase());
            conn = ds.getConnection();
            psmt = conn.prepareStatement(sql);
            rs = psmt.executeQuery();
            if(vo.getTableName().equals("")){
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                Pattern p = Pattern.compile("from\\s+(\\w+).*");
                Matcher matcher = p.matcher(sql);
                if(matcher.find()){
                    vo.setTableName(matcher.group(1));
                }
                if(rs.next()){
                    for (int i = 1; i <= columnCount; i++) {
                        switch (vo.getNameRex()){
                            case "camel":
                                columns.add(Tuple3.of(JavaTool.lineToHump(md.getColumnName(i)),DBTool.columnMapper(md.getColumnType(i)),JavaTool.lineToHump(md.getColumnName(i))));
                                break;
                            case "line":
                                columns.add(Tuple3.of(md.getColumnName(i).toLowerCase(),DBTool.columnMapper(md.getColumnType(i)),md.getColumnName(i)));
                                break;
                            default:
                                columns.add(Tuple3.of(md.getColumnName(i).toLowerCase(),DBTool.columnMapper(md.getColumnType(i)),md.getColumnName(i)));
                        }

                    }
                }
            }else{
                while (rs.next()){
                    switch (vo.getNameRex()){
                        case "camel":
                            columns.add(Tuple3.of(JavaTool.lineToHump(rs.getString("FIELDNAME")),DBTool.sqlMapper(rs.getString("FIELDTYPE")),rs.getString("FIELDDESCRIBE")));
                            break;
                        case "line":
                            columns.add(Tuple3.of(rs.getString("FIELDNAME").toLowerCase(),DBTool.sqlMapper(rs.getString("FIELDTYPE")),rs.getString("FIELDDESCRIBE")));
                            break;
                        default:
                            columns.add(Tuple3.of(rs.getString("FIELDNAME").toLowerCase(),DBTool.sqlMapper(rs.getString("FIELDTYPE")),rs.getString("FIELDDESCRIBE")));
                    }
                }
            }
            javabean = javabean(vo,JavaTool.firstToUpper(JavaTool.lineToHump(vo.getTableName())),"数据库实体类",columns);
        } catch (SQLException e) {
            log.error("查询数据库异常:{}",e);
            return R.error().setMessage("查询数据库异常");
        } finally {
            if(rs != null)rs.close();
            if(psmt != null)psmt.close();
            if(conn != null)conn.close();
        }
        return R.ok().setData(Collections.singletonMap(JavaTool.firstToUpper(JavaTool.lineToHump(vo.getTableName())),javabean));
    }

    public static R sql2JsonGenerator(DBTransformVo vo) throws SQLException {
        StringBuffer sb = new StringBuffer();
        Properties properties = new Properties();
        properties.setProperty("driverClassName",DBType.getByType(vo.getType()).getDriver());
        properties.setProperty("jdbcUrl",String.format(DBType.getByType(vo.getType()).getUrl(),
                vo.getIp().split(",")[0],vo.getIp().split(",")[1],vo.getIp().split(",")[2]));
        properties.setProperty("username",vo.getUsername());
        properties.setProperty("password",vo.getPassword());
        DataSource ds = new HikariDataSource(new HikariConfig(properties));
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            String sql = vo.getTableName().equals("")?vo.getSql():String.format(DBType.getList(vo.getType()),vo.getTableName(),vo.getTableName());
            conn = ds.getConnection();
            psmt = conn.prepareStatement(sql);
            rs = psmt.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            sb.append("[\n");
            int j=0;
            while (rs.next()){
                sb.append(" {\n");
                for (int i = 1; i <= columnCount; i++) {
                    String s = DBTool.columnMapper(md.getColumnType(i));
                    if("String".equals(s) || "Date".equals(s)){
                        sb.append("     \""+md.getColumnName(i).toLowerCase()+"\":\""+rs.getObject(i)+"\"\n");
                    }else{
                        sb.append("     \""+md.getColumnName(i).toLowerCase()+"\":"+rs.getObject(i)+"\n");
                    }
                }
                sb.append(" }\n");
                j++;
                if(j>2){
                    break;
                }
            }
            sb.append("]\n");
        } catch (SQLException e) {
            log.error("查询数据库异常:{}",e);
            return R.error().setMessage("查询数据库异常");
        } finally {
            if(rs != null)rs.close();
            if(psmt != null)psmt.close();
            if(conn != null)conn.close();
        }
        return R.ok().setData(Collections.singletonMap("JSON",sb.toString()));
    }

    public static R sql2ColumnGenerator(DBTransformVo vo) throws SQLException {
        List<String> list = new ArrayList<String>();
        Properties properties = new Properties();
        properties.setProperty("driverClassName",DBType.getByType(vo.getType()).getDriver());
        properties.setProperty("jdbcUrl",String.format(DBType.getByType(vo.getType()).getUrl(),
                vo.getIp().split(",")[0],vo.getIp().split(",")[1],vo.getIp().split(",")[2]));
        properties.setProperty("username",vo.getUsername());
        properties.setProperty("password",vo.getPassword());
        DataSource ds = new HikariDataSource(new HikariConfig(properties));
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            String sql = vo.getTableName().equals("")?vo.getSql():String.format(DBType.getList(vo.getType()),vo.getTableName(),vo.getTableName());
            conn = ds.getConnection();
            psmt = conn.prepareStatement(sql);
            rs = psmt.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            for (int i = 1; i <= columnCount ; i++) {
                list.add(md.getColumnName(i));
            }

        } catch (SQLException e) {
            log.error("查询数据库异常:{}",e);
            return R.error().setMessage("查询数据库异常");
        } finally {
            if(rs != null)rs.close();
            if(psmt != null)psmt.close();
            if(conn != null)conn.close();
        }
        return R.ok().setData(Collections.singletonMap("COLUMN",list));
    }

    /******转换*******/
    //数据库字段转javabean
    private static String javabean(DBTransformVo vo,String tableName,String tableComment,List<Tuple3> columns){
        StringBuffer sbf = new StringBuffer();
        sbf.append("import java.io.Serializable;\n" +
                "import lombok.Data;\n" +
                "import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n" +
                "import com.fasterxml.jackson.annotation.JsonAutoDetect;\n" +
                "import java.util.Date;\n" +
                "import java.util.List;\n");
        sbf.append("/**\n" +
                "*  "+tableComment+"\n" +
                "* @author "+vo.getAuthor()+" "+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) +"\n"+
                "*/\n");
        sbf.append("@Data\n");
        sbf.append("@JsonIgnoreProperties(ignoreUnknown = true)\n");
        sbf.append("@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)\n");
        sbf.append("public class "+tableName+" implements Serializable {\n");
        sbf.append("   private static final long serialVersionUID = 1L;\n");
        for (Tuple3 column : columns) {
            sbf.append("       /**\n" +
                    "       * "+Optional.ofNullable(column.getValue2()).orElse(column.getValue0())+"\n" +
                    "       */\n" +
                    "       private "+column.getValue1()+" "+column.getValue0()+";\n");
        }
        sbf.append("}");
        return sbf.toString();
    }
    //map(包含list)转JavaBean
    private static void mapToJavaBean(DBTransformVo vo,Map<String, Object> map,String name,Map<String, Object> data){
        StringBuffer sbf = new StringBuffer();
        sbf.append("import java.io.Serializable;\n" +
                "import lombok.Data;\n" +
                "import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n" +
                "import com.fasterxml.jackson.annotation.JsonAutoDetect;\n" +
                "import java.util.Date;\n" +
                "import java.util.Map;\n" +
                "import java.util.List;\n");
        sbf.append("/**\n" +
                "*  json转换 \n" +
                "* @author "+vo.getAuthor()+" "+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) +"\n"+
                "*/\n");
        sbf.append("@Data\n");
        sbf.append("@JsonIgnoreProperties(ignoreUnknown = true)\n");
        sbf.append("@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)\n");
        sbf.append("public class "+name+"  implements Serializable {\n");
        sbf.append("   private static final long serialVersionUID = 1L;\n");
        for (String key : map.keySet()) {
            String defkey = JavaTool.firstToUpper(key);
            sbf.append( "       private "+getTypeByInstance(map.get(key),defkey)+" "+(vo.getNameRex().equals("camel")?JavaTool.lineToHump(key):key)+";\n");
            if(map.get(key) instanceof Map){
                Map<String, Object> map1 = (Map<String, Object>) (map.get(key));
                mapToJavaBean(vo,map1,defkey,data);
            }else if(getTypeByInstance(map.get(key),defkey).equals("List<"+defkey+">")){
                List list = (List)map.get(key);
                if(list !=null && list.size()>0){
                    Map<String, Object> map2 = (Map<String, Object>) list.get(0);
                    mapToJavaBean(vo,map2,defkey,data);
                }
            }

        }
        sbf.append("}");
        data.put(name,sbf.toString());
    }
    //object的字段类型
    private static String getTypeByInstance(Object o,String defaultStr){
        if(o instanceof String){
            return "String";
        }else if (o instanceof Integer){
            return "Integer";
        }else if (o instanceof Date){
            return "Date";
        }else if (o instanceof Double){
            return "Double";
        }else if (o instanceof List){
            List list = (List)o;
            if(list !=null && list.size()>0){
                Object o1 = list.get(0);
                if(o1 instanceof String|o1 instanceof Integer|o1 instanceof Date|o1 instanceof Double){
                    return "List<"+getTypeByInstance(o1,defaultStr)+">";
                }else{
                    return "List<"+defaultStr+">";
                }
            }
        }
        return defaultStr;
    }
}
@Getter
enum DBType {
    ORACLE("ORACLE", "jdbc:oracle:thin:@%s:%s/%s", "oracle.jdbc.driver.OracleDriver"),
    MYSQL("MYSQL", "jdbc:mysql:/%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false", "com.mysql.cj.jdbc.Driver"),
    CLICKHOUSE("CLICKHOUSE", "jdbc:clickhouse://%s:%s/%s", "ru.yandex.clickhouse.ClickHouseDriver");

    private String type;
    private String url;
    private String driver;

    DBType(String type, String url, String driver) {
        this.type = type;
        this.url = url;
        this.driver = driver;
    }

    public static DBType getByType(String type){
        switch (type){
            case "ORACLE":return ORACLE;
            case "MYSQL":return MYSQL;
            case "CLICKHOUSE":return CLICKHOUSE;
        }
        return ORACLE;
    }
    public static String getList(String type){
        switch (type){
            case "ORACLE":return "select * from %s where ROWNUM <= 2";
            case "MYSQL":return "select * from %s where limit <= 2";
            case "CLICKHOUSE":return "select * from %s where limit <= 2";
        }
        return "select * from %s where ROWNUM <= 2";
    }

    public static EngineFileType getEngineFileTypeByType(String type){
        switch (type){
            case "HTML":return EngineFileType.HTML;
            case "WORD":return EngineFileType.WORD;
            case "MD":return EngineFileType.MD;
        }
        return EngineFileType.WORD;
    }

}

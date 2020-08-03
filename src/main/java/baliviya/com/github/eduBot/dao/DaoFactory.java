package baliviya.com.github.eduBot.dao;

import baliviya.com.github.eduBot.dao.impl.*;
import baliviya.com.github.eduBot.util.PropertiesUtil;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class DaoFactory {

    private static  DataSource source;
    private static  DaoFactory daoFactory = new DaoFactory();

    public  static  DaoFactory                  getInstance() { return daoFactory; }

    public  static  DataSource                  getDataSource() {
        if (source == null) source = getDriverManagerDataSource();
        return source;
    }

    private static DriverManagerDataSource      getDriverManagerDataSource() {
        DriverManagerDataSource driver = new DriverManagerDataSource();
        driver.setDriverClassName(PropertiesUtil.getProperty("jdbc.driverClassName"));
        driver.setUrl(            PropertiesUtil.getProperty("jdbc.url"));
        driver.setUsername(       PropertiesUtil.getProperty("jdbc.username"));
        driver.setPassword(       PropertiesUtil.getProperty("jdbc.password"));
        return driver;
    }


    public PropertiesDao                        getPropertiesDao() {        return new PropertiesDao(); }

    public MessageDao                           getMessageDao() {           return new MessageDao(); }

    public ButtonDao                            getButtonDao() {            return new ButtonDao();}

    public KeyboardMarkUpDao                    getKeyboardMarkUpDao() {    return new KeyboardMarkUpDao(); }

    public LanguageUserDao                      getLanguageUserDao() {      return new LanguageUserDao(); }

    public UserDao                              getUserDao() {              return new UserDao(); }

    public CategoryDao                          getCategoryDao() {          return  new CategoryDao(); }

    public EmployeeCategoryDao                  getEmployeeCategoryDao() {  return new EmployeeCategoryDao(); }

    public TaskDao                              getTaskDao() {              return new TaskDao(); }
}
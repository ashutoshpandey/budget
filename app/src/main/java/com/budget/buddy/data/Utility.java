package com.budget.buddy.data;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Ashutosh on 1/14/2016.
 */
public class Utility {
    private static Properties projectProperties;

    static{
/*
        projectProperties = new Properties();

        try {
            InputStream in = Utility.class.getClassLoader().getResourceAsStream("settings.properties");
            projectProperties.load(in);
            in.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
*/
    }

    public static String getData(String key){

        if(projectProperties.isEmpty())
            return null;

        if(projectProperties.containsKey(key))
            return projectProperties.getProperty(key);
        else
            return null;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Administrator
 */
public class TEMSourceData {
    //GPS信息
    public static String[] filesName;//文件名
    public static String[] workPlace;//工作地点
    public static String[] latitude;//纬度
    public static String[] longtitude;//经度
    public static String[] status;//定位状态
    public static String[] time;//采样时间
    public static int[] gain;//增益
    public static int[] channels;//道数
    public static int[] fundfrequency;//基频
    public static int[] superposition;//叠加次数
    public static double[] division;//
    public static String[] mode;//采集模式
    /*
     1、	SER     自发射采集模式:按采集键，直接开始按参数进行发射和同步采集，文件名取屏幕RTC的时间；
     2、	SER+GPS 秒脉冲同步发射采集模式：按采集键，等待一个秒脉冲开始发射和同步采集、文件名取GPS的时间；
     3、	SR+GPS秒脉冲同步采集模式：按采集键，等待一个秒脉冲开始同步采集，内部PWM不输出、文件名取GPS的时间；
     5、	CSER+GPS  秒脉冲同步连续采集发射模式：按采集键，等待一个秒脉冲开始发射和同步采集、文件名取GPS的时间，连续采集存盘，每300个文件合并存储在一个文件中；
     6、	CSR+GPS   秒脉冲同步连续采集模式：按采集键，等待一个秒脉冲开始同步采集、内部PWM不输出，文件名取GPS的时间，连续采集存盘，每300个文件合并存储在一个文件中；
     7、	OutSYS  外同步模式:按采集键，等待外部触发信号，收到后，开始同步采集，文件名再开始等待触发前取屏幕RTC的时间；
     */
    //坐标设置参数
    //1 文件名
    public static Object[] trWidth;//Tx边长X(m)
    public static Object[] trLength;//Tx边长Y(m)
    public static Object[] trCenterX;
    public static Object[] trCenterY;
    public static Object[] trAngle;
    public static Object[] trTurns;
    public static Object[] rCenterX;
    public static Object[] rCenterY;
    public static Object[] rCenterZ;
    public static Object[] rLongtitude;
    public static Object[] rLatitude;
    public static Object[] rArea;
    public static Object[] Array;
    public static Object[] turnOffTime;
    public static Object[] current;
    public static Object[] firstChannel;
    public static Object[] secondChannel;
    public static Object[] thirdChannel;
    //源数据
    public static HashMap<String, Double> z_fileName = new HashMap< String, Double>();//自定义高程String_文件名，list坐标值，用于保存文件
    public static HashMap<ArrayList, String> xy_fileName = new HashMap<ArrayList, String>();//自定义的大地坐标list坐标值_文件名
    public static HashMap<String, ArrayList> integrationValue = new HashMap<String, ArrayList>();//自定义的String_文件名，ArrayList对应的积分数据 包括电压、中间时间等
    public static HashMap<String, ArrayList> inversionValue = new HashMap<String, ArrayList>();//自定义的String_文件名，单点反演数据
    public static HashMap<String, ArrayList> lineName_XYList = new HashMap<String, ArrayList>();//定义测线 对应的坐标 ArrayList 所选的点的坐标
    public static ArrayList<String> lineName = new ArrayList<String>();//定义测线名
    public static double[][][] temData;//TEM纯数据 电压值 除以叠加次数 和增益
}

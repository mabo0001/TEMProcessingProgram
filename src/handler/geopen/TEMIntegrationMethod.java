/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 *
 * @author Administrator 2014.4.11 更改 int m_point = (int) Math.round(m_end /
 * sampletRate - m_start / sampletRate) + 1;使其满足最小积分窗口为1 ； 去掉第一个时间点 因为第一点t=0；if
 * (i != 0) {//去掉第一个点
 */
public class TEMIntegrationMethod {

    private ArrayList<Double> voltage = new ArrayList<Double>();//电压值
    private ArrayList<Double> timeMid = new ArrayList<Double>();//中间时间
    private ArrayList<Double> currents = new ArrayList<Double>();//电流值
    private ArrayList<Double> areas = new ArrayList<Double>();//等效面积
    private ArrayList<Double> trWidth = new ArrayList<Double>();//发射线框宽度
    private ArrayList<Double> trLength = new ArrayList<Double>();//发射线框长度
    private ArrayList<Double> turns = new ArrayList<Double>();//匝数
    private ArrayList<Double> widthTime = new ArrayList<Double>();//时间窗口宽度
    private ArrayList<Integer> pointsList = new ArrayList<Integer>();//积分数据点
    //用于设定y轴方向的最大最小值，都一样的坐标范围
    public static double voltMin = 1000000;
    public static double voltMax = -1000000;

    //迭代寻找非零点
    public double addNoneZero(int j, int max) {
        //存在为电压为零的点，应该根据实际予以初始化
        double value = voltage.get(j);
        if (value == 0) {
            if (j < max) {
                addNoneZero(j++, max);
            } else if (j == max - 1) {
                addNoneZero(j--, max);
            }
        }
        return value;
    }

    /**
     *    * 积分方法
     *
     * @param sTime 单位s
     * @param eTime 单位s
     * @param wins
     * @param area 单位m2
     * @param trLengthSingle 单位m
     * @param trWidthSingle 单位m
     * @param current
     * @param turnsSingle 匝数
     * @param vol 单位v
     * @param sampletRate 单位m
     * @return
     */
    public ArrayList AddressDatas(
            double sTime,
            double eTime,
            int wins,
            double area,
            double trLengthSingle,
            double trWidthSingle,
            double current,
            double turnsSingle,
            double[] vol,
            double rate) {
        ArrayList<ArrayList> Volt_midT_Current_Area_winT_Points = new ArrayList<ArrayList>();//存放上面的单点集合数据
        int[][] pointss = points(
                sTime / 1000,
                eTime / 1000,
                wins,
                area,
                trLengthSingle,
                trWidthSingle,
                current,
                turnsSingle,
                vol,
                rate);
        ArrayList<Double> voltList = new ArrayList<Double>();
        ArrayList<Double> timeList = new ArrayList<Double>();
        int start = (int) Math.round(sTime * rate / 1000);
        int end = (int) Math.round(eTime * rate / 1000);//设定的值大小
        int total = vol.length;
        double unitTime = 1000.0 / rate;

        for (int i = 0; i < wins; i++) {
            double sumVolt = 0;
            double sumTime = 0;
            int unitPoints = pointss[1][i];//每个窗口的点数
            int count = 0;
            for (int m = 0; m < unitPoints; m++) {
                if (start + m - 1 < total && start + m - 1 <= end && start + m - 1 >= 0) {
                    sumVolt += vol[start + m - 1];
                    sumTime += (start + m) * unitTime;
                    count++;
                }
            }
            start += unitPoints;
            if (count > 0) {
                double avgVolt = sumVolt / count / current / area;//mv0.52.5
                double time = sumTime / count;
                timeList.add(time);
                voltList.add(avgVolt);
                currents.add(current);
                areas.add(area);
                trLength.add(trLengthSingle);
                trWidth.add(trWidthSingle);
                turns.add(turnsSingle);
            }
        }
        //有些数据不对需要进行修改，和采集程序的算法一致
        int size = timeList.size();
        for (int i = 0; i < size; i++) {
            if (voltList.get(i) == 0) {
                if (i == 0) {
                    if (voltList.get(i + 1) != 0) {
                        voltList.set(i, voltList.get(i + 1) * 0.9);
                    } else {
                        voltList.set(i, voltList.get(i + 2) * 0.9);
                    }
                } else if (i == size - 1) {
                    voltList.set(i, voltList.get(i - 1) * 0.9);
                } else {
                    voltList.set(i, voltList.get(i + 1) * 0.9);
                }
            }
            double v = Math.abs(voltList.get(i));
            double t = timeList.get(i);
            timeMid.add(t);
            voltage.add(v);
        }
        double vmax = Collections.max(voltage);
        double vmin = Collections.min(voltage);
        if (voltMin > vmin) {
            voltMin = vmin;
        }
        if (voltMax < vmax) {
            voltMax = vmax;
        }
//        System.out.println(voltMax + "+++++++" + voltMin);
        Volt_midT_Current_Area_winT_Points.add(voltage);
        Volt_midT_Current_Area_winT_Points.add(timeMid);//单位为ms
        Volt_midT_Current_Area_winT_Points.add(currents);//增加电流
        Volt_midT_Current_Area_winT_Points.add(areas);
        Volt_midT_Current_Area_winT_Points.add(trLength);
        Volt_midT_Current_Area_winT_Points.add(trWidth);
        Volt_midT_Current_Area_winT_Points.add(turns);
        Volt_midT_Current_Area_winT_Points.add(widthTime);
        Volt_midT_Current_Area_winT_Points.add(pointsList);
        return Volt_midT_Current_Area_winT_Points;
    }

    public int[][] points(
            double sTime,
            double eTime,
            int wins,
            double area,
            double trLengthSingle,
            double trWidthSingle,
            double current,
            double turnsSingle,
            double[] vol,
            double rate) {
        int[][] points = new int[2][wins];
        //计算参数定义的系数
        double m_B = (Math.log(eTime) - Math.log(sTime)) / wins; //以E为底
        double m_sum = 0;
        for (int i = 0; i < wins; i++) {
            m_sum = m_sum + Math.exp(m_B * (i + 1));
        }
        double m_A = (eTime - sTime) / m_sum;
        double m_start = sTime;
        for (int i = 0; i < wins; i++) {
            double m_width = m_A * Math.exp(m_B * (i + 1));
            double m_end = m_start + m_width;
            int m_point = (int) (Math.round(m_end * rate - m_start * rate)) + 1;//单位要换算为秒

            points[0][i] = i + 1;
            points[1][i] = m_point;
            m_start = m_start + m_width;

        }
        return points;
    }
}

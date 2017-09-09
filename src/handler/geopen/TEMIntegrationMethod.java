/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
//    private int[] countZero;
//
//    /**
//     *    * 积分方法
//     *
//     * @param sTime 单位s
//     * @param eTime 单位s
//     * @param wins
//     * @param area 单位m2
//     * @param trLengthSingle 单位m
//     * @param trWidthSingle 单位m
//     * @param current
//     * @param turnsSingle 匝数
//     * @param vol 单位v
//     * @param sampletRate 单位m
//     * @return
//     */
//    public ArrayList AddressDatas(
//            double sTime,
//            double eTime,
//            int wins,
//            double area,
//            double trLengthSingle,
//            double trWidthSingle,
//            double current,
//            double turnsSingle,
//            double[] vol,
//            double sampletRate) {
//        ArrayList<ArrayList> Volt_midT_Current_Area_winT_Points = new ArrayList<ArrayList>();//存放上面的单点集合数据
//        //计算参数定义的系数
//        double m_B = (Math.log(eTime) - Math.log(sTime)) / wins;
//        double m_sum = 0;
//        for (int i = 0; i < wins; i++) {
//            m_sum = m_sum + Math.exp(m_B * (i + 1));
//        }
//        double m_A = (eTime - sTime) / m_sum;
//        double m_start = sTime;
//        for (int i = 0; i < wins; i++) {
//            double m_width = m_A * Math.exp(m_B * (i + 1));
//            double m_end = m_start + m_width;
//            int m_point = (int) (Math.round(m_end * sampletRate - m_start * sampletRate)) + 1;//单位要换算为秒
//            widthTime.add(m_width);
//            pointsList.add(m_point);
//            m_start = m_start + m_width;
//        }
////        int[][] pointss = points(sTime, eTime, wins, area, trLengthSingle, trWidthSingle, current, turnsSingle, vol, sampletRate);
//        int sumPoint = 0;
//        for (int i = 0; i < wins; i++) {
//            double sumVol = 0;
//            double sumTime = 0;
//            int endPoints = sumPoint + pointsList.get(i);
//            if (sumPoint + pointsList.get(i) >= vol.length) {//超过电压数据点 需要退出
//                endPoints = vol.length;
//            }
//            for (int j = sumPoint; j < endPoints; j++) {
//                sumVol += vol[j];
//                sumTime += vol[j];
//            }
//            double avg = sumVol;
//            avg = Math.abs((avg / pointsList.get(i) / current / area));
//            if (avg == 0) {
//            } else {
//                voltage.add(Math.abs((avg / pointsList.get(i) / current / area)));
//                double tmd = (endPoints + sumPoint) * 1000 / sampletRate / 2;//秒
//                timeMid.add(tmd);
//                currents.add(current);
//                areas.add(area);
//                trLength.add(trLengthSingle);
//                trWidth.add(trWidthSingle);
//                turns.add(turnsSingle);
//            }
//            if (sumPoint + pointsList.get(i) >= vol.length) {
//                break;
//            }
//            sumPoint += pointsList.get(i);//获得视窗对应的点数
//        }
//        Volt_midT_Current_Area_winT_Points.add(voltage);
//        Volt_midT_Current_Area_winT_Points.add(timeMid);//单位为ms
//        Volt_midT_Current_Area_winT_Points.add(currents);//增加电流
//        Volt_midT_Current_Area_winT_Points.add(areas);
//        Volt_midT_Current_Area_winT_Points.add(trLength);
//        Volt_midT_Current_Area_winT_Points.add(trWidth);
//        Volt_midT_Current_Area_winT_Points.add(turns);
//        Volt_midT_Current_Area_winT_Points.add(widthTime);
//        Volt_midT_Current_Area_winT_Points.add(pointsList);
//        return Volt_midT_Current_Area_winT_Points;
//    }

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
        int[][] pointss = points(sTime / 1000, eTime / 1000, wins, area, trLengthSingle, trWidthSingle, current, turnsSingle, vol, rate);
        int start = (int) Math.round(sTime * rate / 1000);
        int end = (int) Math.round(eTime * rate / 1000);//设定的值大小
        int total = vol.length;
        int sumPoint = 0;
        double unitTime = 1000.0 / rate;
//        System.out.println("wins:" + sTime + "vol:" + eTime);
        int s1 = 0;
        for (int i = 0; i < wins; i++) {
//            winInforS = frame.winIndex_winInfor.get((i + 1)).split("[,]");
            double sumVolt = 0;
            double sumTime = 0;
            int unitPoints = pointss[1][i];
//            System.out.println("unitPoints:" + unitPoints);
            int count = 0;

            for (int m = 0; m < unitPoints; m++) {
                int index = start + m;
//                System.out.println(index);
                if (index <= total && index <= end && index >= 0) {
                    sumVolt += vol[index - 1];
                    sumTime += index * unitTime;
                    count++;
                } else {
                    break;
                }
            }
            start += unitPoints;
//            if (count > 0) {
//                double avgVolt = sumVolt / count / current / area;//mv0.52.5
//                double time = sumTime / count;
////                System.out.println(time+","+avgVolt);
//                voltage.add(Math.abs(avgVolt));
////                double tmd = time;//秒
//                timeMid.add(time);
//                currents.add(current);
//                areas.add(area);
//                trLength.add(trLengthSingle);
//                trWidth.add(trWidthSingle);
//                turns.add(turnsSingle);
////                widthTime.add(count * unitTime);
//            }
            double avgVolt = sumVolt / count / current / area;//mv0.52.5
            double time = sumTime / count;
            if (count > 0) {
//            if (count > 0 && sumVolt != 0) {
//                System.out.println(time+","+avgVolt);
                voltage.add(Math.abs(avgVolt));
//                double tmd = time;//秒
                timeMid.add(time);
                currents.add(current);
                areas.add(area);
                trLength.add(trLengthSingle);
                trWidth.add(trWidthSingle);
                turns.add(turnsSingle);
//                widthTime.add(count * unitTime);
            } else {
//                timeMid.add(time);
//                currents.add(current);
//                areas.add(area);
//                trLength.add(trLengthSingle);
//                trWidth.add(trWidthSingle);
//                turns.add(turnsSingle);
                System.out.println(time + "," + avgVolt);
            }

        }
        int size = timeMid.size();
        for (int i = 0; i < size; i++) {
            if (voltage.get(i) == 0) {
                if (i == 0) {
                    if (voltage.get(i + 1) != 0) {
                        voltage.set(i, voltage.get(i + 1) * 0.9);
                    } else {
                        voltage.set(i, voltage.get(i + 2) * 0.9);
                    }
                } else {
                    voltage.set(i, voltage.get(i + 1) * 0.9);
                }
            }
        }
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
        int points1 = 0;
        DecimalFormat fromt1 = new DecimalFormat("0.#####");
        for (int i = 0; i < wins; i++) {
            double m_width = m_A * Math.exp(m_B * (i + 1));
            double m_end = m_start + m_width;
            double m_mid = m_start + m_width / 2;
            int m_point = (int) (Math.round(m_end * rate - m_start * rate)) + 1;//单位要换算为秒
            points[0][i] = i + 1;
            points1 += Math.round(m_point);
            points[1][i] = m_point;
//
//            winIndex_winInfor.put(
//                    i + 1,
//                    (i + 1) + ","
//                    + points[1][i] + ","
//                    + points1 + ","
//                    + fromt1.format(m_start * 1000) + ","
//                    + fromt1.format(m_end * 1000) + ","
//                    + fromt1.format(m_width * 1000) + ","
//                    + fromt1.format(m_mid * 1000));

            m_start = m_start + m_width;

        }
        return points;
    }
//    /**
//     *
//     * @param sTime 妙
//     * @param eTime
//     * @param wins
//     * @param area
//     * @param current
//     * @param vol
//     * @param sampletRate 采样间隔
//     */
//    public ArrayList AddressDatas111(double sTime, double eTime, int wins, double area, double trLengthSingle, double trWidthSingle, double current, double[] vol, double sampletRate) {
//        ArrayList<ArrayList> Volt_midT_Current_Area_winT_Points = new ArrayList<ArrayList>();//存放上面的单点集合数据
//        //计算参数定义的系数
//        double m_B = (Math.log(eTime) - Math.log(sTime)) / wins;
//        double m_sum = 0;
//        for (int i = 0; i < wins; i++) {
//            m_sum = m_sum + Math.exp(m_B * (i + 1));
//        }
//        double m_A = (eTime - sTime) / m_sum;
//        double m_start = sTime;
//        for (int i = 0; i < wins; i++) {
//            double m_width = m_A * Math.exp(m_B * (i + 1));//转换成毫秒
//            double m_end = m_start + m_width;
//            double m_mid = m_start + m_width / 2.0;
//            int m_point = (int) (Math.ceil(m_end / sampletRate) - Math.floor(m_start / sampletRate)) + 1;
//            widthTime.add(m_width);
//            timeMid.add(m_mid * 1000);//秒
//            points.add(m_point);
//            m_start = m_start + m_width;
//        }
////        int counts = (int) (sTime / sampletRate);//最接近的数
////        int countsEnd = (int) (eTime / sampletRate);
//        int counts = (int) Math.round(sTime / sampletRate);//最接近的数
//        int countsEnd = (int) Math.round(eTime / sampletRate);
//        double averageVol = 0;
//        countZero = new int[1];
//        for (int i = 0; i < wins; i++) {
//            if (counts + points.get(i) > countsEnd) {//超过总数据点时
//                for (int j = counts; j < countsEnd; j++) {
//                    averageVol += vol[j];
//                }
//                if (averageVol == 0) {
//                    countZero[0]++;
//                } else {
//                    voltage.add(Math.abs(5.0 * (averageVol / (countsEnd - counts) / current / area / Math.pow(2, 23))));
//                    currents.add(current);
//                    areas.add(area);
//                    trLength.add(trLengthSingle);
//                    trWidth.add(trWidthSingle);
////                    System.out.println(counts + points.get(i)+ + "===" + points.get(i) + "====" + current + "====" + area);
//                }
//            } else {
//                for (int j = counts; j < counts + points.get(i); j++) {
//                    averageVol += vol[j];
//                }
//                voltage.add(Math.abs(5.0 * (averageVol / points.get(i) / current / area / Math.pow(2, 23))));
//                currents.add(current);
//                areas.add(area);
//                trLength.add(trLengthSingle);
//                trWidth.add(trWidthSingle);
////                System.out.println((5.0 * (averageVol / points.get(i) / current / area / Math.pow(2, 23))));
//            }
//            counts += points.get(i);
//            averageVol = 0;
//        }
//        Volt_midT_Current_Area_winT_Points.add(voltage);
//        Volt_midT_Current_Area_winT_Points.add(timeMid);
//        Volt_midT_Current_Area_winT_Points.add(currents);//增加电流
//        Volt_midT_Current_Area_winT_Points.add(areas);
//        Volt_midT_Current_Area_winT_Points.add(trLength);
//        Volt_midT_Current_Area_winT_Points.add(trWidth);
//        Volt_midT_Current_Area_winT_Points.add(widthTime);
//        Volt_midT_Current_Area_winT_Points.add(points);
//        return Volt_midT_Current_Area_winT_Points;
//    }
}

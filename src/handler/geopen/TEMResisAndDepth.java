/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.XYSeries;

/**
 * 计算电阻率和深度类
 *
 * @author uuwy
 */
public class TEMResisAndDepth {

    /**
     ** 由电压值计算电阻率
     *
     * @param width
     * @param height
     * @param current
     * @param volt
     * @param subt 时间 减去关断时间
     * @param turns 匝数
     * @param unitV
     * @return
     */
    public static double resistyCal(double width, double height, double current, double volt, double subt, double turns, String unitV) {
//        System.out.println(width + "," + height + "," + current + "," + volt + "," + subt + "," + unitV);
//        double condAir = accuracyTran(4 * Math.PI * Math.pow(10, -7) + "");
        double condAir = 4 * Math.PI * Math.pow(10, -7);
//        double tranM = turns * current * Math.pow((width + height), 2);
//        double tranM = (double) (turns * current * width * height);
        double tranM = (double) (turns * Math.pow((width + height), 2));//与Ix1D比较 用周长一半
//        double tranM = (double) (current * Math.pow((width + height) / 2.0, 2));
        double midPara = 0;
        if (unitV.equals("V/AM2")) {
            midPara = (double) Math.pow(2 * condAir * tranM / (5 * volt * current), (2.0 / 3.0));
        } else if (unitV.equals("V/M2")) {
            midPara = (double) Math.pow(2 * condAir * tranM / (5 * volt), (2.0 / 3.0));
        } else if (unitV.equals("")) {
            midPara = (double) Math.pow(2 * condAir * tranM / (5 * volt), (2.0 / 3.0));
        }
//        double coff = 6.3 / Math.pow(4, 2 / 3);//与Ix1D比较 需要乘以这个系数 Math.pow(4, 2 / 3) 
        double resistivity = (double) ((condAir / (4 * Math.PI)) * midPara * Math.pow(subt, -5.0 / 3.0)) * Math.pow(4, 2.0 / 3) / 6.2;
//        System.out.println(subt + "," + turns + "," + volt + "," + width + "," + height);
        return resistivity;
    }

    /**
     * 有电阻率进行反算电压值
     *
     * @param width
     * @param height
     * @param current
     * @param resis
     * @param subt
     * @return
     */
    public static double resistyVolt(double width, double height, double current, double resis, double subt) {
//        double condAir = accuracyTran(4 * Math.PI * Math.pow(10, -7) + "");
        double condAir = Math.round(4 * Math.PI * Math.pow(10, -7));
        double tranM = (double) (current * Math.pow((width + height) / 2.0, 2));
        double midPara = (double) Math.pow(2 * condAir * tranM / 5, (2.0 / 3.0));
        double volt2_3 = (double) ((condAir / (4 * Math.PI)) * midPara * Math.pow(subt, -5.0 / 3.0) * (1 / resis));
        double volt = Math.pow(volt2_3, (3.0 / 2));
        return volt;
    }

    /**
     * 璁＄畻娣卞害
     *
     * @param resis
     * @param time 毫秒
     * @return
     */
    public static double depthCal(double resis, double time) {
        double d = Math.sqrt(500D * resis * time);
        return d;
    }

    /**
     * 绮惧害杞崲
     *
     * @param string
     * @return
     */
    public static double accuracyTran(String string) {
        BigDecimal accuracy = new BigDecimal(string);
        accuracy.setScale(0, BigDecimal.ROUND_HALF_UP);
        return accuracy.floatValue();
    }

    /**
     * 建立时间电压数据组
     *
     * @param voltage
     * @param timeMid
     * @return
     */
    public XYSeries extractVolt_Time(ArrayList voltage, ArrayList timeMid) {
        int counts = voltage.size();
        XYSeries volt_time = new XYSeries("时间电压");
        for (int i = 0; i < counts; i++) {
            double timeM = (Double) timeMid.get(i);
            double vol = (Double) voltage.get(i);
            volt_time.add(timeM, vol);
        }
        return volt_time;
    }

    /**
     * 建立时间电阻率数据组
     *
     * @param voltage
     * @param timeMid
     * @return
     */
    public XYSeries extractResis_Time(ArrayList resis, ArrayList timeMid) {
        int counts = resis.size();
        XYSeries volt_time = new XYSeries("时间电压");
        for (int i = 0; i < counts; i++) {
            double timeM = (Double) timeMid.get(i);
            double res = (Double) resis.get(i);
            volt_time.add(timeM, res);
        }
        return volt_time;
    }
}
